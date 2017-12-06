with directCandidates as(
    select c.id , d.party
    from (directwinner w join direct_candidatures d on  w.winner = d.id) join candidates c on d.candidate=c.id
    where w.year =2017),
    directFreeCandidates as(
      select c.id, s.party, s.state, l.placement
      from (statelists s join list_candidatures l on s.id=l.statelist) join candidates c on c.id= l.candidate
      where s.election=2017 and c.id not in(select id from directCandidates)),
    landlist as (
      select x.id, x.state, x.party
      from (
             select ROW_NUMBER() over(partition by party, state order by placement) as r , t.*
             from directFreeCandidates t) x
      where x.r <=  (select seatsfromlandlist from parlamentdistribution2017 p where p.party =x.party and p.state=x.state))
select * from  directCandidates
union
select id, party from landlist;
