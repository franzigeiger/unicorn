-- get the parliament members for 2017
-- directCandidates are the direct winners for the districts
with directCandidates as(
    select c.id , d.party
    from (election.directwinner w join election.direct_candidatures d on  w.winner = d.id) join election.candidates c on d.candidate=c.id
    where w.year =2017),
    -- candidates on the statelists that did not win their district
    directFreeCandidates as(
      select c.id, s.party, s.state, l.placement
      from (election.statelists s join election.list_candidatures l on s.id=l.statelist) join election.candidates c on c.id= l.candidate
      where s.election=2017 and c.id not in(select id from directCandidates)),
    -- the distribution for the parliament
    parlament as(select * from election.parlamentdistribution2017),
    -- the free candidates that made it into the parliament
    landlist as (
      select x.id, x.state, x.party
      from (
             select ROW_NUMBER() over(partition by party, state order by placement) as r , t.*
             from directFreeCandidates t) x
      where x.r <=  (select seatsfromlandlist from parlament p where p.party =x.party and p.state=x.state))
-- the union of the direct candidates and the candidates that made it through the state list
select * from  directCandidates
union
select id, party from landlist;
