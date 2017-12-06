with firstWinner as(
    select max(dc.votes) as winner
    from election.direct_candidatures dc join election.districts d on d.id = dc.district
    where d.id = ?),
    secondWinner as(
      select max(sa.votes) as winner
      from election.secondvote_aggregates sa join election.districts d on d.id = sa.district
      where d.id = ?)
select dc.party, sa.party
from election.direct_candidatures dc join firstWinner fw on dc.votes = fw.winner,
  election.secondvote_aggregates sa join secondWinner sw on sa.votes = sw.winner
where dc.district = ? and sa.district = ?