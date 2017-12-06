DROP MATERIALIZED VIEW IF EXISTS differenceFirstSecondVotes CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS election.differenceFirstSecondVotes
TABLESPACE pg_default
  AS

with pDiff as(
    select sa.party, sa.district, abs(dc.votes - sa.votes) as diff, dc.votes as first, sa.votes as second
    from election.direct_candidatures dc, election.secondvote_aggregates sa
    where sa.district = dc.district and sa.party = dc.party
    order by sa.party, diff desc
),

maxPDiff as(
    select pd.party, max(pd.diff) as maxDiff, d.year
    from pDiff pd, election.districts d
    where pd.district = d.id
    group by pd.party, d.year
    order by pd.party
)

    select pd.*, mpd.year
    from maxPDiff mpd join pDiff pd on (pd.party = mpd.party and pd.diff = mpd.maxDiff)
WITH DATA;