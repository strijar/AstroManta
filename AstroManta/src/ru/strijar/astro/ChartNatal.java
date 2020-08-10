package ru.strijar.astro;

import org.json.JSONException;
import org.json.JSONObject;

import swisseph.SweConst;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Одиночная карта (натал)
 */

public class ChartNatal extends Chart {
	protected Date			moment = new Date();
	protected Place			place = new Place();
	private int				house;
	
	protected SwissEph 		eph;
	protected SwissLib		lib;
	
	private boolean			useHouse = false;
	private double			cusp[];
	private double			axis[];
	
	private double			eclNut;
	private int				ageEclNut = 0;

	private double			arMC;
	private int				ageArMC = 0;

	private double			sidTime;
	private int				ageSidTime = 0;
	
	private double			calcIn[];
	private double			calcOut[];
	
	private static int		houseId[] = {
		SweConst.SE_HSYS_EQUAL,			// 0
		SweConst.SE_HSYS_ALCABITIUS,	// 1
		SweConst.SE_HSYS_CAMPANUS,		// 2
		SweConst.SE_HSYS_HORIZONTAL,	// 3
		SweConst.SE_HSYS_KOCH,			// 4
		SweConst.SE_HSYS_MORINUS,		// 5
		SweConst.SE_HSYS_PORPHYRIUS ,	// 6
		SweConst.SE_HSYS_PLACIDUS,		// 7
		SweConst.SE_HSYS_REGIOMONTANUS,	// 8
		SweConst.SE_HSYS_POLICH_PAGE,	// 9
		SweConst.SE_HSYS_KRUSINSKI,		// 10
		SweConst.SE_HSYS_VEHLOW,		// 11
		SweConst.SE_HSYS_WHOLE_SIGN,	// 12
		SweConst.SE_HSYS_MERIDIAN 		// 13
	};

	public ChartNatal() {
	}

	protected ChartNatal(SwissEph eph, SwissLib lib) {
		this.eph = eph;
		this.lib = lib;
	}

	private void initHouse() {
		cusp = new double[13];
		axis = new double[10];
		useHouse = true;
		house = 7;
	}
	
	/**
	 * Получить фактический момент расчета
	 *
	 * @return объект момента
	 */
	public Date getCalcMoment() {
		return moment;
	}
	
	/**
	 * Получить фактическое место расчета
	 *
	 * @return объект места
	 */
	public Place getCalcPlace() {
		return place;
	}
	
	public void calc() {
		double moment = getCalcMoment().getJD();
		Place place = getCalcPlace();

		if (useHouse) {
			eph.swe_houses(moment, 0, place.getLat(), place.getLon(), houseId[house], cusp, axis);
		}
		super.calc();
	}
		
	/**
	 * Установить момент для расчета карты
	 *
	 * @param moment объект момента
	 */
	public void setMoment(Date moment) {
		this.moment = moment;
	}
	
	/**
	 * Получить момент расчета карты
	 *
	 * @return объект момента
	 */
	public Date getMoment() {
		return moment;
	}
	
	/**
	 * Задать место для расчета карты
	 *
	 * @param place объект места
	 */
	public void setPlace(Place place) {
		this.place = place;
	}
	
	/**
	 * Получить место расчета карты
	 *
	 * @return объект места
	 */
	public Place getPlace() {
		return place;
	}
	
	/**
	 * Установить систему домов
	 *
	 * @param id индекс системы домов (0 .. 13)
	 */
	public void setHouse(int id) {
		house = id;
	}
	
	/**
	 * Получить систему домов
	 *
	 * @return индекс системы домов
	 */
	public int getHouse() {
		return house;
	}
	
	/**
	 * Проверить присутствуют ли в карте дома или оси
	 *
	 */
	public boolean hasHouse() {
		return useHouse;
	}

	/**
	 * Получить значение наклона эклиптики
	 * @since 5.1
	 * @return наклон эклиптики в градусах
	 */
	public double getEclNut() {
		if (ageEclNut != getCalcMoment().age()) {
			eph.swe_calc(getCalcMoment().getJD(), SweConst.SE_ECL_NUT, 0, calcOut, null);
			eclNut = calcOut[0];
			ageEclNut = getCalcMoment().age();
		}
		
		return eclNut;
	}

	private double getArMC() {
		if (ageArMC != getCalcMoment().age()) {
			arMC = lib.swe_degnorm(lib.swe_sidtime(getCalcMoment().getJD()) * 15.0 + place.getLon() + 90.0);
			ageArMC = getCalcMoment().age();
		}
		
		return arMC;
	}

	/**
	 * Получить звездное время
	 * @since 5.1
	 * @return звездное время
	 */
	
	public double getSidTime() {
		if (ageSidTime != getCalcMoment().age()) {
			sidTime = lib.swe_degnorm(lib.swe_sidtime(getCalcMoment().getJD()) * 15.0 + place.getLon());
			ageSidTime = getCalcMoment().age();
		}
		
		return sidTime;
	}
	
	public void toEquator(Coord ecliptic, Coord equator) {
		if (calcOut == null) { 
			calcOut = new double[6];
		}

		if (calcIn == null) { 
			calcIn = new double[6];
		}

		calcIn[0] = ecliptic.getLon();
		calcIn[1] = ecliptic.getLat();
		calcIn[2] = 1.0;
		
		lib.swe_cotrans(calcIn, calcOut, -getEclNut());
		
		equator.setLon(calcOut[0]);
		equator.setLat(calcOut[1]);
		equator.age(ecliptic.age());
	}
	
	public void toHorizont(Coord ecliptic, Coord horizont) {
		if (calcOut == null) { 
			calcOut = new double[6];
		}

		if (calcIn == null) { 
			calcIn = new double[6];
		}

		Place place = getCalcPlace();

		
		double lat = 90.0 - place.getLat();

		calcIn[0] = ecliptic.getLon();
		calcIn[1] = ecliptic.getLat();
		calcIn[2] = 1.0;
		
		lib.swe_cotrans(calcIn, calcOut, -getEclNut());
		
		calcIn[0] = calcOut[0] - getArMC();
		calcIn[1] = calcOut[1];
		calcIn[2] = 1;

		lib.swe_cotrans(calcIn, calcOut, lat);
		
		horizont.setLon(360.0 - (calcOut[0] - 90.0));
		horizont.setLat(calcOut[1]);
	}

	public void toInMundo(Coord ecliptic, Coord inMundo) {
		if (calcIn == null) { 
			calcIn = new double[6];
		}

		Place	place = getCalcPlace();

		calcIn[0] = ecliptic.getLon();
		calcIn[1] = ecliptic.getLat();

		Double pos = eph.swe_house_pos(getSidTime(), place.getLat(), getEclNut(), houseId[house], calcIn, null);
		
		inMundo.setLon((pos - 1.0) * 30.0);
		inMundo.setLat(0);
		inMundo.age(ecliptic.age());
	}

	/**
	 * Изменить эклиптические координаты вдоль экватора
	 *
	 * @param ecliptic_from исходные эклиптические координаты
	 * @param ecliptic_to объект для получения результата
	 * @param delta смещение по экватору (в градусах)
	 */
	public void shiftEquator(Coord ecliptic_from, Coord ecliptic_to, double delta) {
		if (calcOut == null) { 
			calcOut = new double[6];
		}

		if (calcIn == null) { 
			calcIn = new double[6];
		}

		calcIn[0] = ecliptic_from.getLon();
		calcIn[1] = ecliptic_from.getLat();
		calcIn[2] = 1.0;
		
		lib.swe_cotrans(calcIn, calcOut, -getEclNut());
		calcOut[0] += delta;
		calcOut[2] = 1.0;
		lib.swe_cotrans(calcOut, calcIn, getEclNut());
	
		ecliptic_to.setLon(calcIn[0]);
		ecliptic_to.setLat(calcIn[1]);
	}

	/**
	 * Добавить точку расчитываемую по Швейцарским эффемеридам
	 *
	 * @param Id идентификатор точки (из swisseph.SweConst)
	 * @return объект точки
	 */
	public SwissPlanet addSwissPlanet(int Id) {
		SwissPlanet spot = new SwissPlanet(eph, Id, getCalcMoment());
		
		addSpot(spot);
		
		return spot;
	}

	/**
	 * Добавить куспид дома
	 *
	 * @param Id номер куспиды (1..12)
	 * @return объект точки
	 */
	public HouseCusp addHouseCusp(int Id) {
		if (!useHouse) {
			initHouse();
		}
		
		HouseCusp spot = new HouseCusp(Id, cusp);

		addSpot(spot);

		return spot;
	}

	/**
	 * Добавить ось
	 *
	 * @param Id идентификатор оси (из swisseph.SweConst)
	 * @return объект точки
	 */
	public HouseCusp addAxis(int Id) {
		if (!useHouse) {
			initHouse();
		}

		HouseCusp spot = new HouseCusp(Id, axis);

		addSpot(spot);

		return spot;
	}

	protected void store(JSONObject obj) {
		super.store(obj);
		
		try {
			obj.put("house", getHouse());
		} catch (JSONException e) {
		}
		
		JSONObject json_moment = new JSONObject();
		moment.store(json_moment);
		
		try {
			obj.put("moment", json_moment);
		} catch (JSONException e) {
		}

		JSONObject json_place = new JSONObject();
		place.store(json_place);

		try {
			obj.put("place", json_place);
		} catch (JSONException e) {
		}
	}

	protected void load(JSONObject obj) {
		super.load(obj);

		try {
			setHouse(obj.getInt("house"));
		} catch (JSONException e) {
		}
		
		try {
			JSONObject  json_moment = obj.getJSONObject("moment");
	
			if (moment != null)
				moment.load(json_moment);
		} catch (JSONException e) {
		}

		try {
			JSONObject json_place = obj.getJSONObject("place");

			if (place != null)
				place.load(json_place);
		} catch (JSONException e) {
		}
	}
}
