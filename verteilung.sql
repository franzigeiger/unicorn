set SEARCH_PATH  to election;

DROP MATERIALIZED VIEW IF EXISTS rawdistributionstate CASCADE;

CREATE MATERIALIZED VIEW IF NOT EXISTS election.rawdistributionstate
TABLESPACE pg_default
  AS
    SELECT s.election, s.party,
      s.state,
      count(*) AS count
    FROM election.ballots b
      JOIN election.statelists s ON b.secondvote = s.id
    WHERE   b.secondvote is not null
    GROUP BY s.party, s.state, s.election
WITH DATA;

CREATE MATERIALIZED VIEW IF NOT EXISTS election.allsecondvoters
TABLESPACE pg_default
  AS
    SELECT s.election AS year,
          sum(s.count) AS voters
    FROM rawdistributionstate s
    GROUP BY s.election
WITH DATA;
	

CREATE MATERIALIZED VIEW IF NOT EXISTS election.directwinner
TABLESPACE pg_default
AS
 WITH allcandidates AS (
         SELECT d.year, d.id AS district,
            dc.id AS candidate,
            count(*) AS reached
           FROM election.districts d
             LEFT JOIN election.direct_candidatures dc ON dc.district = d.id
             JOIN election.ballots b ON dc.id = b.firstvote
          GROUP BY d.id, dc.id, d.year
        )
 SELECT a1.district,
    a1.candidate AS winner,
    a1.reached AS votes,
   a1.year as year
   FROM allcandidates a1
  WHERE a1.reached = (( SELECT max(ac.reached) AS max
           FROM allcandidates ac
          WHERE ac.district = a1.district and ac.year = a1.year))
WITH DATA;



CREATE MATERIALIZED VIEW IF NOT EXISTS election.rawdistribution

TABLESPACE pg_default
AS
 SELECT s.party, s.election,
    sum(s.count) AS count,
    sum(s.count)::numeric(12,2) / (( SELECT allsecondvoters.voters
           FROM election.allsecondvoters
          WHERE allsecondvoters.year = s.election))::numeric * 100::numeric AS percent
   FROM rawdistributionstate s
  GROUP BY s.party, s.election
WITH DATA;	






set search_path to election;

drop view if exists parlamentDistribution2017;

create view parlamentDistribution2017(party,state,baseseats, seatswithdirect, seatsFromLandlist, seatsFromDistrict, FinalSeats) as (
  /*1. step:   Create the table with divided votes for party and states out of second votes */
  with recursive  highAll (party, state, counter, divisor) as (
    select p.party,p.state, (cast (p.count as decimal (16,4))) / 0.5 , 1.5 from rawDistributionstate p where p.party in(select party from rawdistribution where percent > 5.0 and election=2017) and p.election=2017
    union all
    select distinct h.party ,h.state, cast ((select p.count from rawDistributionstate p where p.party = h.party and p.state=h.state and p.election=2017 ) as decimal (14,3))/h.divisor, h.divisor +1
    from highall h where h.divisor < 50
  ),

    /*Reduce dublicates from recusrive table*/
      high(party, state, counter,divisor) as (
        select distinct * from highAll
    ),

    /*Create a table with divided inhabitants for each state to evaluate the states safe seats*/
      highAllStates ( state, counter, divisor) as (
      select p.id, (cast (p.eligiblevoters2017 as decimal (16,4))) / 0.5 , 1.5 from states p
      union all
      select distinct h.state, cast ((select p.eligiblevoters2017 from states p where p.id=h.state) as decimal (14,3))/h.divisor, h.divisor +1
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
      minimals(state, counter) as(
        select E1.state,  E1.counter from high E1 where
          ((select count from aggregated a where E1.state=a.state) - 1) = (select count(*)
                                                                           from high E2
                                                                           where E2.counter > E1.counter and E2.state = E1.state )
    )
    ,
    /*Select all entries from high table until last is reached per state*/
      recursives(party, state, counter) as(
        select h.party, h.state, counter from high h where counter >=(
          select counter from minimals where h.state= minimals.state
        ) ),

    /*Overviewtable the reached seats per state and party*/
      partyDistribution as(
        select state, party, count(*) from recursives group by  state, party
    ),

    /*Now we need the direct winner to get all direct winners from diffrent states and parties*/
      directWinnerPartyAggregate(party, state, directWinners) as (
        select  k.party, d.state, count(*)
        from (directWinner w join direct_candidatures k on w.winner=k.id) join districts d on d.id=k.district
        where w.year = 2017
        group by k.party, d.state
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
      highest(party,  seats) as(
        select party, seats from  partyoverview g
        where additionalMandats = (select max(additionalmandats) from partyoverview gs )
    ),

    /*
    Now I need another recursive table to calculate the divisor values per party.
    */
      highPerPartyAll (party, counter, divisor) as (
      select p.party, (cast (p.count as decimal (14,3))) / 0.5 , 1.5 from rawDistribution p where p.party in(select party from rawdistribution where percent > 5.0 and election=2017)and election=2017
      union all
      select distinct h.party , cast ((select p.count from rawDistribution p where p.party = h.party and p.election=2017) as decimal (14,3))/h.divisor, h.divisor +1
      from highPerPartyAll h where h.divisor < 500
    ),

    /*Delete duplicates*/
      highperparty(party, counter,divisor) as (
        select distinct * from highperpartyall
    ),

    /*Now I search the last included entry from recusrive table of the party with maximal additional seats*/
      endcounter(party, counter) as(
        select E1.party,  E1.counter from highperparty E1 where E1.party=(select party from highest) and
                                                                ((select seats from highest) - 1) = (select count(*)
                                                                                                     from highperparty E2
                                                                                                     where E2.counter > E1.counter and E2.party = E1.party )),

    /*The position in the table is the new size of parlament*/
      positionoflast(pos) as(
        select count(*) from highperparty where counter >=(select counter from endcounter)
    )

    /*Now i select all values befor the last one and aggregate it for the parties
    This table contains the end seat size for the parties*/
    , finalPartySteas as(
      select party, count(*) ,
        (select pos from positionoflast)
      from
        (select party, counter from highperparty
         order by counter desc
         limit (select pos from positionoflast)) as allseats group by party),

    /*Step 4: Map the party seat size to state lists
    Therefore find the minimal value from recursive list of second votes
    which will be occupied by direct mandats*/
      minimalStatePArty(state,party, counter) as(
        select E1.state,e1.party , E1.counter from high E1 where
          ((select a.directWinners from directWinnerPartyAggregate a where E1.state=a.state and e1.party=a.party) - 1) = (select count(*)
                                                                                                                          from high E2
                                                                                                                          where E2.counter > E1.counter and E2.state = E1.state and e2.party=e1.party)
    ),


    /*Out of this find all direct occupied seats from recusrive division table*/
      directOccupied(party, state, counter) as(
        select h.party, h.state, counter from high h where counter >=(
          select counter from minimalStateParty  m where h.state= m.state and h.party=m.party
        ) ),


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

    /*find the minimal values for each party for state list seats from  list of free seat values */
      minimalStateListPlaces( party,  counter) as(
        select e1.party, E1.counter from directfree E1 where
          ((select seats from seatsforlandlist a where e1.party=a.party) - 1) = (select count(*)
                                                                                 from directfree E2
                                                                                 where E2.counter > E1.counter and e2.party=e1.party  )
    ),

    /*
    select all entries which are above the minimal values
    */
      alllandseats as(select d.party,d.state, d.counter from directfree d
    where counter >=(select (case when counter is null then 0 else  m.counter end) from minimalStateListPlaces m where m.party=d.party)),

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
        (select party from rawdistribution where percent > 5.0 and election=2017)
  group by party, state
        )as r order by party, state );

