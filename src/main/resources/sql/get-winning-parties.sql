with firstWinner as(
    select d.id, max(dc.votes) as winner
    from election.direct_candidatures dc join election.districts d on d.id = dc.district
    where d.year = ?
    group by d.id
),
secondWinner as(
    select d.id, max(sa.votes) as winner
    from election.secondvote_aggregates sa join election.districts d on d.id = sa.district
    where d.year = ?
    group by d.id
)

select dc.district, dc.party, sa.party
from election.direct_candidatures dc join firstWinner fw on dc.votes = fw.winner and dc.district = fw.id,
election.secondvote_aggregates sa join secondWinner sw on sa.votes = sw.winner and sa.district = sw.id
where dc.district = sa.district