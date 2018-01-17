/* This query gets all parties and their row numbers for a specified state and election;
 this data is used to create a new ballot for said state and election;
 to compute this, the year of the previous election is needed as well*/

/* contains the amounts of second votes a party received in a state during the election of a specified year */
with stateAggregates (state, party, votes) as (
    select d.state, sa.party, sum(votes)
    from election.secondvote_aggregates sa
    join election.districts d
    on sa.district = d.id
    where d.year = ?
    group by d.state, sa.party
),
/* adds row number to stateAggregates */
/* this row number indicates the place a certain party will have on a ballot during the following election;
 e.g. the party with the largest amount of second votes during the previous election will be the first party displayed on a ballot during the following election */
orderedPrev(number, state, party, votes) as(
    select ROW_number() over (order by votes desc) as row, *
	from stateAggregates
	where state = ?
	order by votes desc)

/* Gets party and row number (that inidcate the palce that party should have on a ballot)
 for a specified election and state */
select sl.party, ROW_number() over (order by op.number asc, p.name asc) as fullRow
from election.statelists sl
left outer join orderedPrev op on sl.state = op.state and sl.party = op.party
left outer join election.parties p on p.id = sl.party
where sl.state = ? and sl.election = ?
order by fullRow
