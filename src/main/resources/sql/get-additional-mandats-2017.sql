select party, state,(case
                     when seatswithdirect > baseseats then seatswithdirect - baseseats
                     else 0
                     end) as additionalMandats
from election.parlamentdistribution2017
order by additionalmandats desc