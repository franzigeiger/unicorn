set SEARCH_PATH  to election;
/*create a view, which contains the distribution of votes grouped by party, state and election */
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

--create view which contains the direct winners for every district and every vote
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


/*create a view, which contains the number of votes grouped by party and election*/
CREATE or replace VIEW election.rawdistribution
AS
 SELECT s.party, s.election,
    sum(s.count) AS count,
    sum(s.count)::numeric(12,2) / (( SELECT allsecondvoters.voters
           FROM election.allsecondvoters
          WHERE allsecondvoters.year = s.election))::numeric * 100::numeric AS percent
   FROM rawdistributionstate s
  GROUP BY s.party, s.election;


/*This is the real calculation of parliament distribution. We did the calculation in 5 steps and used mainly highest averages method.*/
create or replace view parlamentDistribution2017(party,state,baseseats, seatswithdirect, seatsFromLandlist, seatsFromDistrict, FinalSeats) as (
  /*1. step:Calculate how many seats must be assigned to every state.    */

  with recursive
       /*We select * from rawDistribution to avoid multiple recalculation of rawdistribution view*/
    baseDistribution as(
      select * from rawDistribution where percent > 5.0 and election=2017
    ),
  /*We select * from rawDistribution to avoid multiple recalculation of rawdistributionstate view*/
    stateDistribution as (
      select * from rawDistributionstate
      where party
        in(select party from baseDistribution) and election=2017
    ),

    /*Then we calculate the highest numbers of votes based on the method grouped by party and state*/
    high (party, state, counter, divisor) as (
      select p.party,p.state, (cast (p.count as decimal (16,4))) / 0.5 , 1.5
      from stateDistribution p
      where p.party
        in(select party from baseDistribution)
    union
      select distinct h.party ,h.state,
        cast ((select p.count from stateDistribution p where p.party = h.party and p.state=h.state) as decimal (14,3))/h.divisor, h.divisor +1
      from high h
      where h.divisor < 50
  ),


    /*Calculate the highest numbers of state inhabitants based on the method grouped by state
    This is necessary to do step 1 and divide the 598 seats between the states*/
      highAllStates ( state, counter, divisor) as (
       select p.id, (cast (p.inhabitants as decimal (16,4))) / 0.5 , 1.5
       from states p
      union
        select distinct h.state,
          cast ((select p.inhabitants from states p where p.id=h.state) as decimal (14,3))/h.divisor, h.divisor +1
        from highallstates h
        where h.divisor < 250
    ),

    /*Select the parliament contingent from above division table to get the seats distribution between the states*/
      seatsStates(state, counter) as(
        SELECT *
        FROM highallstates
        where counter  is not null
        ORDER BY counter DESC
        limit 598
    ),

    /*Aggregate the evaluated state seats for every state*/
      aggregated as(
        select state, count(*) from seatsstates group by state
    ),

    /*Step 2:  Now we divide the states seat contingent based on vote distribution between
    the elected parties.

    Therefore we need the direct winner and create a new view no not recalculate them permanently.
    */
    directs as(
      select * from directWinner where year=2017
    ),

    /*
    We aggregate the direct winners per party and state
     */
    directWinnerPartyAggregate(party, state, directWinners) as (
        select  k.party, d.state, count(*)
        from (directs w join direct_candidatures k on w.winner=k.id) join districts d
          on d.id=k.district
        group by k.party, d.state
    ),

    /*Now we select all the k highest values per state from highest number table high(base: vote, grouped by state and party)
    where k = number of seats assigned to the state(from step one)
    */
    recursives(state, party, counter) as(
      select  x.state, x.party , x.counter
      from
        (select ROW_NUMBER() over(partition by state order by counter desc) as r , t.* from high t) x
      where x.r <=   (select count
                      from aggregated p
                      where p.state=x.state)),

    /*Aggregate the result of statement before to have the seat distribution per party and state without considering direct winners*/
    partyDistribution as(
      select state, party, count(*)
      from recursives
      group by  state, party
    ),

    /*This distribution shows per state and party how much seats were gaines from second votes
    and how many direct seats were gained grouped by state and party*/
    distribution (party ,state,  seats, direct_mandats) as (
      select s.party, s.state, s.count ,
        (case when d.directWinners is null then 0 else d.directwinners end)
      from partydistribution s left join  directWinnerPartyAggregate d
        on s.party = d.party and s.state = d.state
    ),


    /*This table shows the maximum gained seats per party (either second vote gained seats or direct gained seats, depending on which is higher)
    and state and its additional mandates */
      gainedSeats(party ,state, seats , additionalMandats) as (
        select d.party, d.state,
          (case when d.seats > d.direct_mandats then d.seats else d.direct_mandats end) ,
          (case when d.seats < d.direct_mandats then d.direct_mandats - d.seats else 0 end)
        from distribution d
    ),

    /*The actual minimum seats number*/
      newSeatsNumber(counter)as (
        select sum(seats) from gainedSeats
    ),

    /*Little overview with number of additional mandates and new seats number per state*/
      overview as(
        select g.state, sum(seats),
          (select sum(additionalMandats)
            from gainedSeats gs
            where gs.state = g.state),
          (select counter from newSeatsNumber), s.name
        from gainedSeats g join states s on s.id = g.state
        group by g.state, s.name),

    /*overview per party of seats and included additionalmandats*/
      partyOverview(party, seats, additionalMandats) as (
        select party , sum(seats),        (select sum(additionalMandats)
                                            from gainedSeats gs
                                            where gs.party = g.party),
          (select counter from newSeatsNumber)
          from gainedSeats  g
          group by party
    ),

    /*Step 3: Calculate leveling seats
    Now we must calculate how much leveling seats are necessary to have all direct elected candidates in parliament and
    consider the correct second vote distribution.
    For this step we use the divisor method, because is is easier to calculate.

    First we calculate the divisor per party based on votes of party in common.
    */
    divisors(party, div) as(
      select s.party, s.count / ((select p.seats
                                  from partyOverview p
                                  where p.party = s.party) -0.5)
      from baseDistribution s
    ),

    /*
      As divisor for the method we use the smallest divisor of table above.
    */
    divisor as(
      select min(div) from divisors
    ),

    /*
      Now we divide the number of votes per party by our evaluated divisor and round this to the next upper number.
    */
    allSeats(party, seats) as (
      select s.party, round( s.count / (select min from divisor))
      from baseDistribution s
    ),

    /*
      This table contains the seats per party with leveling seats and the final number of parliament seats.
    */
    finalPartySteas(party, count, summary) as(
      select party, sum(seats) ,
        (select sum(seats) from allSeats)
      from
        allSeats group by party),

    /*Step 4: Map the common party seats to seats from state lists and seats from direct winners
    Therefore we use the high table of highest average method from beginning(base: votes, grouped by state and party)
    and select the tuple, which are occupied of direct mandates out of high table.*/
    directOccupied(state, party, counter) as(
      select  x.state, x.party , x.counter
      from
        (select ROW_NUMBER() over(partition by party, state order by counter desc) as r , t.* from high t) x
      where x.r <=  (select a.directWinners
                    from directWinnerPartyAggregate a
                    where x.state=a.state and x.party=a.party)),

    /*Now remove the direct occupied tables items from high table to get the still free free values to calculate the land list seats per state and party*/
      directfree(party, state, counter) as (
        select  h.party, h.state, h.counter
        from high h
        where
          not exists(select counter
                      from directOccupied d
                      where h.state=d.state and h.party=d.party and d.counter=h.counter)
    ),

    /*aggregate direct winners for party*/
      directWinners(party, seats) as(
        select party, sum(directWinners)
        from directWinnerPartyAggregate
        group by party
    ),

    /*get number of assignable seats to land list from parties common seat contingent minus direct seats*/
      seatsforlandlist(party, seats) as (
        select f.party, f.count - (case when d.seats is null then 0 else d.seats end)
        from finalPartySteas f left join directwinners d on d.party=f.party
    ),

    /**
      Now we select the evaluated seats, which must be filled from land list out of the highest numper table minus the direct occuped seats(directFree)
    */
    alllandseats(state, party, counter) as(
      select  x.state, x.party , x.counter
      from (select ROW_NUMBER() over(partition by party order by counter desc) as r , t.* from directfree t) x
      where x.r <=
            (select a.seats
            from seatsforlandlist a
            where x.party=a.party)),

    /*Aggregate the values to get the land list seats*/
      landseatsPerState(party, state, landseats) as (
        select party, state, count(*)
        from alllandseats
        group by party, state)

  /*This table contains the end result:
    party, state,
    the number of seats per party without consideration of direct votes,
    the number of seats with additional mandates,
    the number of seats from land list,
    the number of seats for direct winner,
    the number of common number of gained seats (with leveling mandates)
  */
   select party, state,
    (select count from partyDistribution p where p.party=r.party and p.state=r.state),
    (select seats from gainedSeats g where g.party=r.party and g.state=r.state) as withadditional,
    (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state ) as land ,
    (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state ) as direct,
    (case when (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state )  is null then 0 else (select landseats from landseatsperstate l where l.party=r.party and l.state=r.state )  end)
    + (case when (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state )  is null then 0 else (select directwinners from directWinnerPartyAggregate w where w.party=r.party and w.state=r.state )  end)
  from  (select party, state, count(*) from statelists
  where party in
        (select party from baseDistribution)
  group by party, state
        )as r order by party, state );

  select * from parlamentDistribution2017;



create or replace view parlamentDistribution2013(party,state,baseseats, seatswithdirect, seatsFromLandlist, seatsFromDistrict, FinalSeats) as (
  /*1. step:Calculate how many seats must be assigned to every state.    */

  with recursive
       /*We select * from rawDistribution to avoid multiple recalculation of rawdistribution view*/
    baseDistribution as(
      select * from rawDistribution where percent > 5.0 and election=2013
    ),
  /*We select * from rawDistribution to avoid multiple recalculation of rawdistributionstate view*/
    stateDistribution as (
      select * from rawDistributionstate
      where party
        in(select party from baseDistribution) and election=2013
    ),

    /*Then we calculate the highest numbers of votes based on the method grouped by party and state*/
    high (party, state, counter, divisor) as (
      select p.party,p.state, (cast (p.count as decimal (16,4))) / 0.5 , 1.5
      from stateDistribution p
      where p.party
        in(select party from baseDistribution)
    union
      select distinct h.party ,h.state,
        cast ((select p.count from stateDistribution p where p.party = h.party and p.state=h.state) as decimal (14,3))/h.divisor, h.divisor +1
      from high h
      where h.divisor < 50
  ),


    /*Calculate the highest numbers of state inhabitants based on the method grouped by state
    This is necessary to do step 1 and divide the 598 seats between the states*/
      highAllStates ( state, counter, divisor) as (
       select p.id, (cast (p.inhabitants as decimal (16,4))) / 0.5 , 1.5
       from states p
      union
        select distinct h.state,
          cast ((select p.inhabitants from states p where p.id=h.state) as decimal (14,3))/h.divisor, h.divisor +1
        from highallstates h
        where h.divisor < 250
    ),

    /*Select the parliament contingent from above division table to get the seats distribution between the states*/
      seatsStates(state, counter) as(
        SELECT *
        FROM highallstates
        where counter  is not null
        ORDER BY counter DESC
        limit 598
    ),

    /*Aggregate the evaluated state seats for every state*/
      aggregated as(
        select state, count(*) from seatsstates group by state
    ),

    /*Step 2:  Now we divide the states seat contingent based on vote distribution between
    the elected parties.

    Therefore we need the direct winner and create a new view no not recalculate them permanently.
    */
    directs as(
      select * from directWinner where year=2013
    ),
    /*
    We aggregate the direct winners per party and state
     */
    directWinnerPartyAggregate(party, state, directWinners) as (
        select  k.party, d.state, count(*)
        from (directs w join direct_candidatures k on w.winner=k.id) join districts d
          on d.id=k.district
        group by k.party, d.state
    ),

    /*Now we select all the k highest values per state from highest number table high(base: vote, grouped by state and party)
    where k = number of seats assigned to the state(from step one)
    */
    recursives(state, party, counter) as(
      select  x.state, x.party , x.counter
      from
        (select ROW_NUMBER() over(partition by state order by counter desc) as r , t.* from high t) x
      where x.r <=   (select count
                      from aggregated p
                      where p.state=x.state)),

    /*Aggregate the result of statement before to have the seat distribution per party and state without considering direct winners*/
    partyDistribution as(
      select state, party, count(*)
      from recursives
      group by  state, party
    ),

    /*This distribution shows per state and party how much seats were gaines from second votes
    and how many direct seats were gained grouped by state and party*/
    distribution (party ,state,  seats, direct_mandats) as (
      select s.party, s.state, s.count ,
        (case when d.directWinners is null then 0 else d.directwinners end)
      from partydistribution s left join  directWinnerPartyAggregate d
        on s.party = d.party and s.state = d.state
    ),


    /*This table shows the maximum gained seats per party (either second vote gained seats or direct gained seats, depending on which is higher)
    and state and its additional mandates */
      gainedSeats(party ,state, seats , additionalMandats) as (
        select d.party, d.state,
          (case when d.seats > d.direct_mandats then d.seats else d.direct_mandats end) ,
          (case when d.seats < d.direct_mandats then d.direct_mandats - d.seats else 0 end)
        from distribution d
    ),

    /*The actual minimum seats number*/
      newSeatsNumber(counter)as (
        select sum(seats) from gainedSeats
    ),

    /*Little overview with number of additional mandates and new seats number per state*/
      overview as(
        select g.state, sum(seats),
          (select sum(additionalMandats)
            from gainedSeats gs
            where gs.state = g.state),
          (select counter from newSeatsNumber), s.name
        from gainedSeats g join states s on s.id = g.state
        group by g.state, s.name),

    /*overview per party of seats and included additionalmandats*/
      partyOverview(party, seats, additionalMandats) as (
        select party , sum(seats),        (select sum(additionalMandats)
                                            from gainedSeats gs
                                            where gs.party = g.party),
          (select counter from newSeatsNumber)
          from gainedSeats  g
          group by party
    ),

    /*Step 3: Calculate leveling seats
    Now we must calculate how much leveling seats are necessary to have all direct elected candidates in parliament and
    consider the correct second vote distribution.
    For this step we use the divisor method, because is is easier to calculate.

    First we calculate the divisor per party based on votes of party in common.
    */
    divisors(party, div) as(
      select s.party, s.count / ((select p.seats
                                  from partyOverview p
                                  where p.party = s.party) -0.5)
      from baseDistribution s
    ),

    /*
      As divisor for the method we use the smallest divisor of table above.
    */
    divisor as(select min(div) from divisors),

    /*
      Now we divide the number of votes per party by our evaluated divisor and round this to the next upper number.
    */
    allSeats(party, seats) as (
      select s.party, round( s.count / (select min from divisor))
      from baseDistribution s
    ),

    /*
      This table contains the seats per party with leveling seats and the final number of parliament seats.
    */
    finalPartySteas(party, count, summary) as(
      select party, sum(seats) ,
        (select sum(seats) from allSeats)
      from
        allSeats group by party
    ),

    /*Step 4: Map the common party seats to seats from state lists and seats from direct winners
    Therefore we use the high table of highest average method from beginning(base: votes, grouped by state and party)
    and select the tuple, which are occupied of direct mandates out of high table.*/
    directOccupied(state, party, counter) as(
    select  x.state, x.party , x.counter
    from
      (select ROW_NUMBER() over(partition by party, state order by counter desc) as r , t.* from high t) x
    where x.r <=  (select a.directWinners
                    from directWinnerPartyAggregate a
                    where x.state=a.state and x.party=a.party)
    ),

    /*Now remove the direct occupied tables items from high table to get the still free free values to calculate the land list seats per state and party*/
      directfree(party, state, counter) as (
        select  h.party, h.state, h.counter
        from high h
        where
          not exists(select counter
                      from directOccupied d
                      where h.state=d.state and h.party=d.party and d.counter=h.counter)
    ),

    /*aggregate direct winners for party*/
      directWinners(party, seats) as(
        select party, sum(directWinners)
        from directWinnerPartyAggregate
        group by party
    ),

    /*get number of assignable seats to land list from parties common seat contingent minus direct seats*/
      seatsforlandlist(party, seats) as (
        select f.party, f.count - (case when d.seats is null then 0 else d.seats end)
        from finalPartySteas f left join directwinners d on d.party=f.party
    ),

    /**
      Now we select the evaluated seats, which must be filled from land list out of the highest numper table minus the direct occuped seats(directFree)
    */
    alllandseats(state, party, counter) as(
      select  x.state, x.party , x.counter
      from (select ROW_NUMBER() over(partition by party order by counter desc) as r , t.* from directfree t) x
      where x.r <=
            (select a.seats
            from seatsforlandlist a
            where x.party=a.party)
    ),

    /*Aggregate the values to get the land list seats*/
      landseatsPerState(party, state, landseats) as (
        select party, state, count(*)
        from alllandseats
        group by party, state
      )

  /*This table contains the end result:
    party, state,
    the number of seats per party without consideration of direct votes,
    the number of seats with additional  mandates ,
    the number of seats from land list,
    the number of seats for direct winner,
    the number of common number of gained seats (with leveling mandates)
  */
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


/*
This statement calculates the members of parliament in year 2017 based on parliament distribution.
 */
create or replace view parliamentMembers2017 as (
/*
This view gets the direct winners id from candidate table
 */
  with directCandidates as(
      select c.id , d.party
      from (election.directwinner w join election.direct_candidatures d on  w.winner = d.id) join election.candidates c on d.candidate=c.id
      where w.year =2017
  ),

  /**
  This view contains the candidates, which does not have a direct mandate for parliament.
   */
  directFreeCandidates as(
      select c.id, s.party, s.state, l.placement
      from (election.statelists s join election.list_candidatures l on s.id=l.statelist) join election.candidates c on c.id= l.candidate
      where s.election=2017 and
      c.id not in(select id from directCandidates)
  ),
  /*
  This view calculates the candidates which have a parliament seat from land list based on the direct free candidate view
   */
  landlist as (
     select x.id, x.state, x.party
    from (
        select ROW_NUMBER() over(partition by party, state order by placement) as r , t.*
        from directFreeCandidates t) x
    where x.r <=
        (select seatsfromlandlist
         from parlamentdistribution2017 p
         where p.party =x.party and p.state=x.state)
)
/*
To get all parliament members we add direct candidates and land list members
 */
select * from  directCandidates
union
select id, party from landlist);

/*
This statement calculates the members of parliament in year 2013 based on parliament distribution.
 */
create or replace view parliamentMembers2013 as (
/*
This view gets the direct winners id from candidate table
 */
  with directCandidates as(
      select c.id , d.party
      from (election.directwinner w join election.direct_candidatures d on  w.winner = d.id) join election.candidates c on d.candidate=c.id
      where w.year =2013
  ),

  /**
  This view contains the candidates, which does not have a direct mandate for parliament.
   */
  directFreeCandidates as(
      select c.id, s.party, s.state, l.placement
      from (election.statelists s join election.list_candidatures l on s.id=l.statelist) join election.candidates c on c.id= l.candidate
      where s.election=2013 and
      c.id not in(select id from directCandidates)
  ),
  /*
  This view calculates the candidates which have a parliament seat from land list based on the direct free candidate view
   */
  landlist as (
     select x.id, x.state, x.party
    from (
        select ROW_NUMBER() over(partition by party, state order by placement) as r , t.*
        from directFreeCandidates t) x
    where x.r <=
        (select seatsfromlandlist
         from parlamentdistribution2013 p
         where p.party =x.party and p.state=x.state)
)
/*
To get all parliament members we add direct candidates and land list members
 */
select * from  directCandidates
union
select id, party from landlist);