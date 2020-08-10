#!/usr/local/bin/lua

local country = {}
local id = 1

local f_in = io.open("countryInfo.txt", "r")

for line in f_in:lines() do
    local res = {}

    for col in line:gmatch('(.-)\t') do
	table.insert(res, col)
    end

    country[res[1]] = { id = id, info = res[5]}
    id = id + 1
end

f_in:close()

-----

local admin = {}
local id = 1

local f_in = io.open("admin1CodesASCII.txt", "r")

for line in f_in:lines() do
    local res = {}

    for col in line:gmatch('(.-)\t') do
	table.insert(res, col)
    end

    admin[res[1]] = { id = id, info = res[3] }
    id = id + 1
end

f_in:close()

-----

local reg = {}
local reg_max = 1

local time_zone = {}
local time_zone_max = 1

local f_in = io.open("cities1000.txt", "r")

local f_out = io.open("out/city.csv", "w")
local f_out_a = io.open("out/city_a.csv", "w")

for line in f_in:lines() do
    local res = {}

    for col in line:gmatch('(.-)\t') do
	table.insert(res, col)
    end

    local admin_key = res[9] .. "." .. res[11]

    local city = res[3]
    local alter = res[4] .. ","
    local lat = res[5]
    local lon = res[6]
    local popul = tonumber(res[15])
    local zone = res[18]

    local a = admin[admin_key]

    if a ~= nil then
	reg_key = a.info .. ", " .. country[res[9]].info

	local reg_id = reg[reg_key]

	if reg_id == nil then
	    reg[reg_key] = reg_max
	    reg_id = reg_max

	    reg_max = reg_max + 1
	end

	local zone_id = time_zone[zone]

	if zone_id == nil then
	    time_zone[zone] = time_zone_max
	    zone_id = time_zone_max

	    time_zone_max = time_zone_max + 1
	end

	if (popul > 30000) then
	    local data = ";" .. lat .. ";" .. lon .. ";" .. reg_id .. ";" .. zone_id .. "\n"

	    f_out:write(city, data)

	    for city in alter:gmatch('(.-),') do
		if city:find("\xD0[\x81\x90-\xBF]") or city:find("\xD1[\x80-\x8F\x91]") then
		    f_out_a:write(city, data)
		end
	    end
	end
    end
end

f_out:close()
f_out_a:close()

--- OUT ---

local f_out = io.open("out/reg.csv", "w")

for info,id in pairs(reg) do
    f_out:write(id .. ";" .. info .. "\n")
end

local f_out = io.open("out/time_zone.csv", "w")

for info,id in pairs(time_zone) do
    f_out:write(id .. ";" .. info .. "\n")
end
