.separator ";"

CREATE TABLE city (
    name	TEXT,
    lat		REAL,
    lon		REAL,
    reg		INT,
    time_zone	INT
);

CREATE INDEX city1 ON city(name);

.import city.csv city
.import city_a.csv city

-----

CREATE TABLE reg (
    id		INT,
    name	TEXT
);

CREATE INDEX reg_id ON reg(id);

.import reg.csv reg

-----

CREATE TABLE time_zone (
    id		INT,
    name	TEXT
);

CREATE INDEX time_zone_id ON time_zone(id);

.import time_zone.csv time_zone
