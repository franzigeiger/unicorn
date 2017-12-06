alter table election.states add column inhabitants integer not null default 0;

alter table election.states add column inhabitants2013 integer not null default 0;

update election.states 
set inhabitants = 2673803, inhabitants2013 = 2686085
where name = 'Schleswig-Holstein';

update election.states 
set inhabitants = 1548400, inhabitants2013 = 1585032
where name = 'Mecklenburg-Vorpommern';

update election.states 
set inhabitants = 1525090, inhabitants2013 = 1559655 
where name = 'Hamburg';

update election.states 
set inhabitants = 7278789, inhabitants2013 = 7354892
where name = 'Niedersachsen';

update election.states 
set inhabitants = 568510, inhabitants2013 = 575805
where name = 'Bremen';

update election.states 
set inhabitants = 2391746, inhabitants2013 = 2418267
where name = 'Brandenburg';

update election.states 
set inhabitants =2145671, inhabitants2013 = 2247673
where name = 'Sachsen-Anhalt';

update election.states 
set inhabitants = 2975745, inhabitants2013 = 3025288
where name = 'Berlin';

update election.states 
set inhabitants = 15707569, inhabitants2013 = 15895182
where name = 'Nordrhein-Westfalen';

update election.states 
set inhabitants = 3914671, inhabitants2013 = 4005278
where name = 'Sachsen';

update election.states 
set inhabitants = 5281198, inhabitants2013 = 5388350
where name = 'Hessen';

update election.states 
set inhabitants = 2077901, inhabitants2013 = 2154202
where name = 'Thüringen';

update election.states 
set inhabitants = 3661245, inhabitants2013 = 3672888
where name = 'Rheinland-Pfalz';

update election.states 
set inhabitants = 11362245, inhabitants2013 = 11353264
where name = 'Bayern';

update election.states 
set inhabitants = 9365001, inhabitants2013 = 9482902
where name = 'Baden-Württemberg';

update election.states 
set inhabitants = 899748, inhabitants2013 = 919402
where name = 'Saarland';