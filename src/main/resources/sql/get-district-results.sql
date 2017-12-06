with pfirst2013(party, first2013) as(
    select dc.party, dc.votes as first2013
    from election.direct_candidatures dc
    where dc.district = ?
),
    psecond2013(party, second2013) as(
      select dc.party, dc.votes as second2013
      from election.secondvote_aggregates dc
      where dc.district = ?
  ),
    pfirst2017(party, first2017) as(
      select dc.party, dc.votes as first2017
      from election.direct_candidatures dc
      where dc.district = ?
  ),
    psecond2017(party, second2017) as(
      select dc.party, dc.votes as second2017
      from election.secondvote_aggregates dc
      where dc.district = ?)

select p.name, pf13.first2013, ps13.second2013, pf17.first2017, ps17.second2017
from election.parties p
  left outer join pFirst2013 pf13 on p.id = pf13.party
  left outer join pSecond2013 ps13 on p.id = ps13.party
  left outer join pFirst2017 pf17 on p.id = pf17.party
  left outer join pSecond2017 ps17 on p.id = ps17.party
