select dc.candidate, dc.party
from election.direct_candidatures dc
where dc.district = ?