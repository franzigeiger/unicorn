WITH maxvotes_per_district AS (
    SELECT district,year,MAX(votes) AS maxvotes
    FROM election.direct_candidatures AS dc JOIN election.districts AS d ON d.id = dc.district
    GROUP BY year,district),
    winners_per_district AS (
      SELECT dc.*,maxvotes,year
      FROM election.direct_candidatures AS dc JOIN maxvotes_per_district AS mv ON (mv.district = dc.district AND mv.maxvotes = dc.votes)),
    secondvotes_per_district AS (
      SELECT district,MAX(votes) AS secondvotes
      FROM election.direct_candidatures AS dc
      WHERE NOT EXISTS (
          SELECT *
          FROM winners_per_district
          WHERE id = dc.id)
      GROUP BY district),
    second_per_district AS (
      SELECT dc.*,secondvotes
      FROM election.direct_candidatures AS dc JOIN secondvotes_per_district AS sv ON sv.district = dc.district AND sv.secondvotes = dc.votes),
    first_second_diff_per_district AS (
      SELECT winners.district,
        winners.year,
        winners.votes - second.votes AS votediff,
        winners.candidate AS winner,
        winners.party AS winnerparty,
        winners.votes AS winnervotes,
        second.candidate AS second,
        second.party AS secondparty,
        second.votes AS secondvotes
      FROM winners_per_district as winners JOIN second_per_district AS second ON winners.district = second.district)

SELECT *
FROM first_second_diff_per_district
WHERE (winnerparty=? OR secondparty=?) AND year=?
ORDER BY votediff ASC
LIMIT 10
