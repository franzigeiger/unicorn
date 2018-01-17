/*Gets direct candidates and their party for a specified district*/
select dc.candidate, dc.party , dc.id
from election.direct_candidatures dc
where dc.district = ?
