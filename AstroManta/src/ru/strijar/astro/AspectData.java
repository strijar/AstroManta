package ru.strijar.astro;

/**
 * Данные аспекта.
 */

public class AspectData {
	private Spot		spot1;
	private Spot		spot2;

	private String		info;
	private Aspect		aspect;
	
	protected AspectData(Spot spot1, Spot spot2) {
		this.spot1 = spot1;
		this.spot2 = spot2;
	}
	
	protected void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}
	
	/**
	 * Получить аспект.
	 *
	 * @return аспект
	 */
	public Aspect getAspect() {
		return aspect;
	}
	
	/**
	 * Получить первую точку аспекта.
	 *
	 * @return точка
	 */
	public Spot getSpot1() {
		return spot1;
	}

	/**
	 * Получить вторую точку аспекта
	 *
	 * @return точка
	 */
	public Spot getSpot2() {
		return spot2;
	}
	
	/**
	 * Получить информацию аспекта.
	 *
	 * @return информация из аспекта или null если аспекта нет
	 */
	public String getInfo() {
		return info;
	}
	
	protected void calc() {
		if (aspect.getVisible() && ((spot1.getAspected() && spot2.getAspecting()) || (spot1.getAspecting() && spot2.getAspected()))) {
			info = aspect.calc(spot1, spot2);
		} else {
			info = null;
		}
	}
}
