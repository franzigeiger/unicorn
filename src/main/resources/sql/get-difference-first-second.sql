/*This query is used to get the max. difference between first and second votes that a party received in a district.
This can help to decide whether a direct candidate of a party is particularly popular or unpopular*/

/*Gets difference between first and second votes for every party in every district*/
with pDiff as(
    select sa.party, sa.district, abs(dc.votes - sa.votes) as diff, dc.votes as first, sa.votes as second
    from election.direct_candidatures dc, election.secondvote_aggregates sa
    where sa.district = dc.district and sa.party = dc.party
    order by sa.party, diff desc
),
/*Gets max. difference between first and second votes in a district for every party for a specified year*/
maxPDiff as(
    select pd.party, max(pd.diff) as maxDiff
    from pDiff pd, election.districts d
    where pd.district = d.id and d.year = ?
    group by pd.party
    order by pd.party
)
/*Gets party,
max. difference between first and second votes in a district,
amount of first votes, amount of second votes
and district in which the difference between first and second votes was largest
for every party*/
select pd.party, pd.diff, pd.first, pd.second, pd.district
from maxPDiff mpd join pDiff pd on
(pd.party = mpd.party and pd.diff = mpd.maxDiff)
order by diff desc
