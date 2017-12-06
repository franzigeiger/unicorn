select party, diff, first, second, district, year
from election.differencefirstsecondvotes where year = ?
order by diff desc