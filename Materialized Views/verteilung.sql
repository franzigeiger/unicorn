set SEARCH_PATH  to election;

CREATE or replace VIEW election.rawdistributionstate(election, party, state, count)
  AS
    SELECT s.year, b.party,
      st.id,
      sum(votes) AS count
    FROM (election.secondvote_aggregates b
      JOIN election.districts s ON b.district = s.id)
      join election.states st on s.state=st.id
    GROUP BY b.party , st.id, s.year;

CREATE or replace VIEW election.allsecondvoters
  AS
    SELECT s.election AS year,
          sum(s.count) AS voters
    FROM rawdistributionstate s
    GROUP BY s.election;


CREATE  or replace VIEW election.directwinner
AS
 WITH allcandidates AS (
         SELECT d.year, d.id AS district,
            dc.id AS winner,
            votes AS votes
           FROM election.districts d
             JOIN election.direct_candidatures dc ON dc.district = d.id ),
             winnerAll as(
             select district, year, max(votes) from allcandidates group by year, district
             )

             select district, winner, votes, year from allcandidates ac1 WHERE votes = (( SELECT max
           FROM winnerAll ac
          WHERE ac.district = ac1.district and ac.year = ac1.year)
        );



CREATE or replace VIEW election.rawdistribution
AS
 SELECT s.party, s.election,
    sum(s.count) AS count,
    sum(s.count)::numeric(12,2) / (( SELECT allsecondvoters.voters
           FROM election.allsecondvoters
          WHERE allsecondvoters.year = s.election))::numeric * 100::numeric AS percent
   FROM rawdistributionstate s
  GROUP BY s.party, s.election;



create or replace view parlamentDistribution2017(party,state,baseseats, seatswithdirect, seatsFromLandlist, seatsFromDistrict, FinalSeats) as (
  /*1. step:   Create the table with divided votes for party and states out of second votes */

  with recursive
    stateDistribution as (
    select * from rawDistributionstate
    ),
    baseDistribution as(
    select * from rawDistribution
    ),
    high (party, state, counter, divisor) as (
    select p.party,p.state, (cast (p.count as decimal (16,4))) / 0.5 , 1.5 from stateDistribution p where p.party in(select party from baseDistribution where percent > 5.0 and election=2017) and p.election=2017
    union
    select distinct h.party ,h.state, cast ((select p.count from stateDistribution p where p.party = h.party and p.state=h.state and p.election=2017 ) as decimal (14,3))/h.divisor, h.divisor +1
    from high h where h.divisor < 50
  ),


    /*Create a table with divided inhabitants for each state to evaluate the states safe seats*/
      highAllStates ( state, counter, divisor) as (
      select p.id, (cast (p.inhabitants as decimal (16,4))) / 0.5 , 1.5 from states p
      union
      select distinct h.state, cast ((select p.inhabitants from states p where p.id=h.state) as decimal (14,3))/h.divisor, h.divisor +1
      from highallstates h where h.divisor < 250
    ),

    /*Select the parlament contingent from above division table to get the seats distribution between the states*/
      seatsStates(state, counter) as(
        SELECT *
        FROM highallstates
        where counter  is not null
        ORDER BY counter DESC
        limit 598
    ),

    /*Aggregate the selected state seats for every state*/
      aggregated as(
        select state, count(*) from seatsstates group by state
    ),

    /*Step 2:  This is my way to get the highest n entries from divided table(generated above ->high)
      I select the value of the last place which is in for every state
        Then its possible to select the items til the last is reached to get all reached seats.
        1.: Get last division value per state from high table
    */
    directs as(
    select * from directWinner
    ),

      directWinnerPartyAggregate(party, state, directWinners) as (
        select  k.party, d.state, count(*)
        from (directs w join direct_candidatures k on w.winner=k.id) join districts d on d.id=k.district
        where w.year = 2017
        group by k.party, d.state
    ),

    /*Select all entries from high table until last is reached per state*/
      recursives(state, party, counter) as(
          select  x.state, x.party , x.counter
    from (select ROW_NUMBER() over(partition by state order by counter desc) as r , t.* from high t) x
    where x.r <=   (select count from aggregated p where p.state=x.state)),

    /*Overviewtable the reached seats per state and party*/
      partyDistribution as(
        select state, party, count(*) from recursives group by  state, party
    ),

    /*This distribution shows per state and party how much seats it gaines from second vote
    and how much direct seats it gained*/
      distribution (party ,state,  seats, direct_mandats) as (
        select s.party, s.state, s.count , (case when d.directWinners is null then 0 else d.directwinners end)
        from partydistribution s left join  directWinnerPartyAggregate d on s.party = d.party and s.state = d.state
    ),


    /*This table shows the maximum gained seats perparty and state and its additional mandats */
      gainedSeats(party ,state, seats , additionalMandats) as (
        select d.party, d.state, (case when d.seats > d.direct_mandats then d.seats else d.direct_mandats end) ,
          ( case when d.seats < d.direct_mandats then d.direct_mandats - d.seats else 0 end)
        from distribution d
    ),

    /*The actual minimum seats number*/
      newSeatsNumber(counter)as (
        select sum(seats) from gainedSeats
    ),

    /*Little overview with number of additional mandats, new seats number per state*/
      overview as(
        select g.state, sum(seats),
          (select sum(additionalMandats) from gainedSeats gs where gs.state = g.state),
          (select counter from newSeatsNumber), s.name from gainedSeats g join states s on s.id = g.state group by g.state, s.name),

    /*overview per party of seats and included additionalmandats*/
      partyOverview(party, seats, additionalMandats) as (
        select party , sum(seats),        (select sum(additionalMandats) from gainedSeats gs where gs.party = g.party),
          (select counter from newSeatsNumber) from gainedSeats  g group by party
    ),

    /*Step 3: Calculate leveling seats
    Therefore I search for the party with highest additional mandats
    */
    /*
    Now I need another recursive table to calculate the divisor values per party.
    */
      secondVotesPerParty as(
    select party, sum(count) from stateDistribution where party in(select party from baseDistribution where percent > 5.0 and election=2017) and stateDistribution.election=2017 group by party
    ),

    divisors(party, div) as(
    select s.party, s.sum / ((select p.seats from partyOverview p where p.party = s.party) -0.5) from secondvotesPerParty s
    ),

    divisor as(select min(div) from divisors),

    allSeats(party, seats) as (select s.party, round( s.sum / (select min from divisor)
    ) from secondVotesPerParty s),

    finalPartySteas(party, count, summary) as(
      select party, sum(seats) ,
        (select sum(seats) from allSeats)
      from
        allSeats group by party),

      /*Step 4: Map the party seat size to state lists
    Therefore find the minimal value from recursive list of second votes
    which will be occupied by direct mandats*/
        directOccupied(state, party, counter) as(
          select  x.state, x.party , x.counter
    from (select ROW_NUMBER() over(partition by party, state order by counter desc) as r , t.* from high t) x
    where x.r <=  (select a.directWinners from directWinnerPartyAggregate a where x.state=a.state and x.party=a.party)),

    /*Now divide the occupied list from main list to get the already free values for the rest of land lsit seats*/
      directfree(party, state, counter) as (
        select  h.party, h.state, h.counter from high h where not exists(select counter from directOccupied d where h.state=d.state and h.party=d.party and d.counter=h.counter)
    ),

    /*aggregate direct winners for party*/
      directWinners(party, seats) as(
        select party, sum(directWinners) from directWinnerPartyAggregate group by party
    ),

    /*get number of assignable seats to land list from parties common contingent */
      seatsforlandlist(party, seats) as (
        select f.party, f.count - (case when d.seats is null then 0 else d.seats end)from finalPartySteas f left join directwinners d on d.party=f.party
    ),

       alllandseats(state, party, counter) as(
          select  x.state, x.party , x.counter
    from (select ROW_NUMBER() over(partition by party order by counter desc) as r , t.* from directfree t) x
    where x.r <=   (select a.seats from seatsforlandlist a where x.party=a.party)),

    /*Aggregate the values to get the land list seats*/
      landseatsPerState(party, state, landseats) as (
        select party, state, count(*) from alllandseats group by party, state)

  /*This table conains per party and seats the gained direct seats and the gained state list seats*/
  select party, state,
    (select count from partyDistribution p where p.party=r.party and p.state=r.state),
    (select seats from gainedSeats g where g.party=r.party and g.state=r.state) as withadditional,
    (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state ) as land ,
    (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state ) as direct,
    (case when (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state )  is null then 0 else (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state )  end)
    + (case when (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state )  is null then 0 else (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state )  end)
  from  (select party, state, count(*) from statelists
  where party in
        (select party from baseDistribution where percent > 5.0 and election=2017)
  group by party, state
        )as r order by party, state );

        select * from parlamentDistribution2017;




create or replace view parlamentDistribution2013(party,state,baseseats, seatswithdirect, seatsFromLandlist, seatsFromDistrict, FinalSeats) as (
  /*1. step:   Create the table with divided votes for party and states out of second votes */

  with recursive
    stateDistribution as (
    select * from rawDistributionstate
    ),
    baseDistribution as(
    select * from rawDistribution
    ),
    high (party, state, counter, divisor) as (
    select p.party,p.state, (cast (p.count as decimal (16,4))) / 0.5 , 1.5 from stateDistribution p where p.party in(select party from baseDistribution where percent > 5.0 and election=2013) and p.election=2013
    union
    select distinct h.party ,h.state, cast ((select p.count from stateDistribution p where p.party = h.party and p.state=h.state and p.election=2013 ) as decimal (14,3))/h.divisor, h.divisor +1
    from high h where h.divisor < 50
  ),


    /*Create a table with divided inhabitants for each state to evaluate the states safe seats*/
      highAllStates ( state, counter, divisor) as (
      select p.id, (cast (p.inhabitants2013 as decimal (16,4))) / 0.5 , 1.5 from states p
      union
      select distinct h.state, cast ((select p.inhabitants2013 from states p where p.id=h.state) as decimal (14,3))/h.divisor, h.divisor +1
      from highallstates h where h.divisor < 250
    ),

    /*Select the parlament contingent from above division table to get the seats distribution between the states*/
      seatsStates(state, counter) as(
        SELECT *
        FROM highallstates
        where counter  is not null
        ORDER BY counter DESC
        limit 598
    ),

    /*Aggregate the selected state seats for every state*/
      aggregated as(
        select state, count(*) from seatsstates group by state
    ),

    /*Step 2:  This is my way to get the highest n entries from divided table(generated above ->high)
      I select the value of the last place which is in for every state
        Then its possible to select the items til the last is reached to get all reached seats.
        1.: Get last division value per state from high table
    */
    directs as(
    select * from directWinner
    ),

      directWinnerPartyAggregate(party, state, directWinners) as (
        select  k.party, d.state, count(*)
        from (directs w join direct_candidatures k on w.winner=k.id) join districts d on d.id=k.district
        where w.year = 2013
        group by k.party, d.state
    ),

    /*Select all entries from high table until last is reached per state*/
      recursives(state, party, counter) as(
          select  x.state, x.party , x.counter
    from (select ROW_NUMBER() over(partition by state order by counter desc) as r , t.* from high t) x
    where x.r <=   (select count from aggregated p where p.state=x.state)),

    /*Overviewtable the reached seats per state and party*/
      partyDistribution as(
        select state, party, count(*) from recursives group by  state, party
    ),

    /*This distribution shows per state and party how much seats it gaines from second vote
    and how much direct seats it gained*/
      distribution (party ,state,  seats, direct_mandats) as (
        select s.party, s.state, s.count , (case when d.directWinners is null then 0 else d.directwinners end)
        from partydistribution s left join  directWinnerPartyAggregate d on s.party = d.party and s.state = d.state
    ),


    /*This table shows the maximum gained seats perparty and state and its additional mandats */
      gainedSeats(party ,state, seats , additionalMandats) as (
        select d.party, d.state, (case when d.seats > d.direct_mandats then d.seats else d.direct_mandats end) ,
          ( case when d.seats < d.direct_mandats then d.direct_mandats - d.seats else 0 end)
        from distribution d
    ),

    /*The actual minimum seats number*/
      newSeatsNumber(counter)as (
        select sum(seats) from gainedSeats
    ),

    /*Little overview with number of additional mandats, new seats number per state*/
      overview as(
        select g.state, sum(seats),
          (select sum(additionalMandats) from gainedSeats gs where gs.state = g.state),
          (select counter from newSeatsNumber), s.name from gainedSeats g join states s on s.id = g.state group by g.state, s.name),

    /*overview per party of seats and included additionalmandats*/
      partyOverview(party, seats, additionalMandats) as (
        select party , sum(seats),        (select sum(additionalMandats) from gainedSeats gs where gs.party = g.party),
          (select counter from newSeatsNumber) from gainedSeats  g group by party
    ),

    /*Step 3: Calculate leveling seats
    Therefore I search for the party with highest additional mandats
    */
    /*
    Now I need another recursive table to calculate the divisor values per party.
    */
    secondVotesPerParty as(
    select party, sum(count) from stateDistribution where party in(select party from baseDistribution where percent > 5.0 and election=2013)
    and stateDistribution.election=2013 group by party
    ),

    divisors(party, div) as(
    select s.party, s.sum / ((select p.seats from partyOverview p where p.party = s.party) -0.5) from secondvotesPerParty s
    ),

    divisor as(select min(div) from divisors),

    allSeats(party, seats) as (select s.party, floor( s.sum / (select min from divisor)
    ) from secondVotesPerParty s),

    finalPartySteas(party, count, summary) as(
      select party, sum(seats) ,
        (select sum(seats) from allSeats)
      from
        allSeats group by party),

      /*Step 4: Map the party seat size to state lists
    Therefore find the minimal value from recursive list of second votes
    which will be occupied by direct mandats*/
        directOccupied(state, party, counter) as(
          select  x.state, x.party , x.counter
    from (select ROW_NUMBER() over(partition by party, state order by counter desc) as r , t.* from high t) x
    where x.r <=  (select a.directWinners from directWinnerPartyAggregate a where x.state=a.state and x.party=a.party)),

    /*Now divide the occupied list from main list to get the already free values for the rest of land lsit seats*/
      directfree(party, state, counter) as (
        select  h.party, h.state, h.counter from high h where not exists(select counter from directOccupied d where h.state=d.state and h.party=d.party and d.counter=h.counter)
    ),

    /*aggregate direct winners for party*/
      directWinners(party, seats) as(
        select party, sum(directWinners) from directWinnerPartyAggregate group by party
    ),

    /*get number of assignable seats to land list from parties common contingent */
      seatsforlandlist(party, seats) as (
        select f.party, f.count - (case when d.seats is null then 0 else d.seats end)from finalPartySteas f left join directwinners d on d.party=f.party
    ),

       alllandseats(state, party, counter) as(
          select  x.state, x.party , x.counter
    from (select ROW_NUMBER() over(partition by party order by counter desc) as r , t.* from directfree t) x
    where x.r <=   (select a.seats from seatsforlandlist a where x.party=a.party)),

    /*Aggregate the values to get the land list seats*/
      landseatsPerState(party, state, landseats) as (
        select party, state, count(*) from alllandseats group by party, state)

  /*This table conains per party and seats the gained direct seats and the gained state list seats*/
  select party, state,
    (select count from partyDistribution p where p.party=r.party and p.state=r.state),
    (select seats from gainedSeats g where g.party=r.party and g.state=r.state) as withadditional,
    (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state ) as land ,
    (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state ) as direct,
    (case when (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state )  is null then 0 else (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state )  end)
    + (case when (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state )  is null then 0 else (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state )  end)
  from  (select party, state, count(*) from statelists
  where party in
        (select party from baseDistribution where percent > 5.0 and election=2013)
  group by party, state
        )as r order by party, state );
        select * from parlamentDistribution2013;

