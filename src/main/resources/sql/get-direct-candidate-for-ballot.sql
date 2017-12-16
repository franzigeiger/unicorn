select dc.candidate, dc.party , dc.id
from election.direct_candidatures dc
where dc.district = ?