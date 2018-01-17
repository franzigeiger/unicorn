/*Gets direct candidates and their party for a specified district*/
select dc.candidate, dc.party
from election.direct_candidatures dc
where dc.district = ?
