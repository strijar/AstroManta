.timer on
.header on
.mode column

select
    city.rowid,
    city.name,
    reg.name,
    city.lat,
    city.lon,
    time_zone.name
from
    city,
    reg,
    time_zone
where
    city.name LIKE "Ура%"
    and city.reg = reg.id
    and city.time_zone = time_zone.id
limit 10;

analyze;

.print "------------"

select * from sqlite_stat1;
