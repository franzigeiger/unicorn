select lc.*
from election.list_candidatures lc
join election.statelists sl
on sl.id = lc.statelist
join election.districts d
on d.state = sl.state
where d.id = ? and sl.election = ? and sl.party = ? and lc.placement < 6