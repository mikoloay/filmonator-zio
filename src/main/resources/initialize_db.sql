create table movie (
    id varchar
    ,name varchar
    ,year_of_production int
    ,director varchar
    ,description varchar
    ,length_in_minutes int
);

insert into movie (id, name, year_of_production, director, description, length_in_minutes)
values
    ('64a9e21edc6a472fa4d4b6e06cfc636e','Dwunastu gniewnych ludzi',1957,'Sidney Lumet','A jury holdout attempts to prevent a miscarriage of justice by forcing his colleagues to reconsider the evidence.',96),
    ('a22e40f16e2a426da1f087d9c1e61b0d','Hot Fuzz - Ostre psy',2007,'Edgar Wright','A skilled London police officer is transferred to a small town with a dark secret.',121),
    ('d13a7f6649ac41e2ac9406d5c27f2636','Ivans Childhood',1962,'Andrei Tarkovsky','During World War II orphaned Ivan bonds with three Soviet officers while seeking revenge against the Nazis.',95),
    ('5103c7339cfb4b058022b12b61e4a70a','Aftersun',2022,'Charlotte Wells','A father and daughter wrestle with lifes complexities during a summer vacation.',102),
    ('b930dfc7c5e14b5a9f5c525e4d852f90','Manhunter',1986,'Michael Mann','An FBI profiler is on the trail of a serial killer who targets families.',120),
    ('0dc250003bff41a1aeb7742cc2a0c3e0','Sound of metal',2019,'Darius Marder','A drummer begins to lose his hearing and has to come to terms with a future that will be filled with silence.',130),
    ('e9a1fe0562a64d1bb25d3b148e019f35','Boogie Nights',1997,'Paul Thomas Anderson','The story of a young mans adventures in the Californian pornography industry of the late 1970s and early 1980s.',155);


create table ticket (name varchar, is_discount bool, description varchar, price_in_zloty int)

    id varchar
    ,name varchar
    ,year_of_production int
    ,director varchar
    ,description varchar
    ,length_in_minutes int
);