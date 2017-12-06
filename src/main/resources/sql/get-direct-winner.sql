select c.id
from election.directwinner dw join election.direct_candidatures dc
    on dw.winner = dc.id
  join election.candidates c on dc.candidate = c.id
where dw.district = ?
