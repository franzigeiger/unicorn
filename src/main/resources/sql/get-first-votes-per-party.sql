with firstVotesPercentage as(
    select dc.party,sum(dc.votes) as votes,d.year
    from election.direct_candidatures dc join election.districts d on d.id = dc.district
    group by dc.party, d.year
)
select p.id, votes, year
from firstVotesPercentage f join election.parties p on p.id = f.party
where year = ?
