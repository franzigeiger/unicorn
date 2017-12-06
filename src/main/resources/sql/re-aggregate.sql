DELETE FROM election.secondvote_aggregates;

INSERT INTO election.secondvote_aggregates(district,party,votes)
  SELECT b.district,s.party,COUNT(*) AS votes
  FROM election.ballots AS b, election.statelists AS s
  WHERE b.secondvote = s.id
  GROUP BY b.district,s.party;

WITH aggregated_first AS (
    SELECT b.firstvote,COUNT(*) AS votes
    FROM election.ballots AS b
    GROUP BY b.firstvote
    HAVING b.firstvote IS NOT NULL)
UPDATE election.direct_candidatures
SET votes = (SELECT votes FROM aggregated_first WHERE firstvote = id)
WHERE EXISTS (SELECT * FROM aggregated_first WHERE firstvote = id);

WITH invalid_first AS (
    SELECT district,COUNT(*) AS votes FROM election.ballots WHERE firstvote  IS NULL GROUP BY district),
    invalid_second AS (
      SELECT district,COUNT(*) AS votes FROM election.ballots WHERE secondvote IS NULL GROUP BY district)
UPDATE election.districts
SET invalidfirstvotes =  (SELECT votes FROM invalid_first  WHERE district = id),
  invalidsecondvotes = (SELECT votes FROM invalid_second WHERE district = id);
