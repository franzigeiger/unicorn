/*This query gets all list candidates that should be shown on a ballot for a specified year, district and party*/
select lc.*
from election.list_candidatures lc
join election.statelists sl
on sl.id = lc.statelist
join election.districts d
on d.state = sl.state
/*only the first 5 candidates on a state list should be displayed on a ballot (list placement < 6)*/
where d.id = ? and sl.election = ? and sl.party = ? and lc.placement < 6
