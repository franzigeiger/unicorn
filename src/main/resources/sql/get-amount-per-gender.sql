/*For each gender of the parliament members of 2017, this computes the amount of candidates of said gender*/
select c.sex, count(*) as total
from election.parliamentmembers2017 m join election.candidates c on m.id = c.id
group by c.sex
