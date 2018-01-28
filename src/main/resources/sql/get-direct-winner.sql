-- get the direct winner candidate for a district.
-- election.directwinner is a view supplied by the parliament calculation (verteilung.sql)
select c.id
from election.directwinner dw join election.direct_candidatures dc
    on dw.winner = dc.id
  join election.candidates c on dc.candidate = c.id
where dw.district = ?
