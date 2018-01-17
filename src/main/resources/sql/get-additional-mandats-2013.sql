/*
This statements return the additional mandates per party and state of year 2013
*/
select party, state,(case
                     when seatswithdirect > baseseats then seatswithdirect - baseseats
                     else 0
                     end) as additionalMandats
from election.parlamentdistribution2013
order by additionalmandats desc