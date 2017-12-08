with directCandidates as(
    select c.id , d.party
    from (election.directwinner w join election.direct_candidatures d on  w.winner = d.id) join election.candidates c on d.candidate=c.id
    where w.year =2013),
    directFreeCandidates as(
      select c.id, s.party, s.state, l.placement
      from (election.statelists s join election.list_candidatures l on s.id=l.statelist) join election.candidates c on c.id= l.candidate
      where s.election=2013 and c.id not in(select id from directCandidates)),
    landlist as (
      select x.id, x.state, x.party
      from (
             select ROW_NUMBER() over(partition by party, state order by placement) as r , t.*
             from directFreeCandidates t) x
      where x.r <=  (select seatsfromlandlist from election.parlamentdistribution2013 p where p.party =x.party and p.state=x.state))
select * from  directCandidates
union
select id, party from landlist;