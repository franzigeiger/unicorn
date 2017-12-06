select c.sex, count(*) as total
from election.parliamentmembers m join election.candidates c on m.id = c.id
group by c.sex
