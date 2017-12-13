with stateAggregates (state, party, votes) as (
    select d.state, sa.party, sum(votes)
    from election.secondvote_aggregates sa 
    join election.districts d 
    on sa.district = d.id
    where d.year = ?
    group by d.state, sa.party
),

orderedPrev(number, state, party, votes) as(
    select ROW_number() over (order by votes desc) as row, *
	from stateAggregates
	where state = ?
	order by votes desc)

select sl.party, ROW_number() over (order by op.number asc, p.name asc) as fullRow
from election.statelists sl
left outer join orderedPrev op on sl.state = op.state and sl.party = op.party
left outer join election.parties p on p.id = sl.party
where sl.state = ? and sl.election = ?
order by fullRow