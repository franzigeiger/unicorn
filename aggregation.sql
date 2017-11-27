DELETE FROM election.secondvote_aggregates;

WITH ball_aggreg AS (
    	SELECT district,firstvote,secondvote,count(*) AS votes
    	FROM election.ballots
    	GROUP BY district,firstvote,secondvote),
    insert_second AS (
    	INSERT INTO election.secondvote_aggregates(district,party,votes)
    	SELECT ba.district,s.party,SUM(ba.votes)
    	FROM ball_aggreg AS ba, election.statelists AS s
    	WHERE ba.secondvote = s.id
    	GROUP BY ba.district,s.party),
    aggregated_first AS (
    	SELECT ba.firstvote, SUM(votes) AS votes
    	FROM ball_aggreg AS ba
    	GROUP BY ba.firstvote
    	HAVING ba.firstvote IS NOT NULL),
    insert_first AS (
    	UPDATE election.direct_candidatures
    	SET votes = (SELECT votes FROM aggregated_first WHERE firstvote = id)
    	WHERE EXISTS (SELECT * FROM ball_aggreg WHERE firstvote = id)),
    invalid_first AS (
    	SELECT district,SUM(votes) AS votes
        FROM ball_aggreg
    	WHERE firstvote IS NULL
        GROUP BY district),
    invalid_second AS (
    	SELECT district,SUM(votes) AS votes
    	FROM ball_aggreg
    	WHERE secondvote IS NULL
    	GROUP BY district)

UPDATE election.districts
SET invalidfirstvotes = (SELECT votes FROM invalid_first WHERE district = id),
	invalidsecondvotes = (SELECT votes FROM invalid_second WHERE district = id)
