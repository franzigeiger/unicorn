with pDiff as(
    select sa.party, sa.district, abs(dc.votes - sa.votes) as diff, dc.votes as first, sa.votes as second
    from election.direct_candidatures dc, election.secondvote_aggregates sa
    where sa.district = dc.district and sa.party = dc.party
    order by sa.party, diff desc
),

maxPDiff as(
    select pd.party, max(pd.diff) as maxDiff
    from pDiff pd, election.districts d
    where pd.district = d.id and d.year = ?
    group by pd.party
    order by pd.party
)

select pd.party, pd.diff, pd.first, pd.second, pd.district
from maxPDiff mpd join pDiff pd on
(pd.party = mpd.party and pd.diff = mpd.maxDiff)
order by diff desc