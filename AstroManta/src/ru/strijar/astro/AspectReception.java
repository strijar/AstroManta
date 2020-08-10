package ru.strijar.astro;

/**
 * Аспект по приему
 */

public class AspectReception extends Aspect {
	private class ReceptionItem extends Hiding {
		protected String	info;
		
		protected ReceptionItem(String info) {
			this.info = info;
		}
	}
	
	private ReceptionItem	forward;
	private ReceptionItem	backward;
	private ReceptionItem	both;
	private Division		div;

	/**
	 * Информация по приему в прямом направлении (первая точка принимает вторую)
	 *
	 * @param info информация
	 * @return скрываемый объект
	 */
	public Hiding forward(String info) {
		forward = new ReceptionItem(info);
		
		return forward;
	}

	/**
	 * Информация по приему в обратном направлении (вторая точка принимает первую)
	 *
	 * @param info информация
	 * @return скрываемый объект
	 */
	public Hiding backward(String info) {
		backward = new ReceptionItem(info);
		
		return backward;
	}

	/**
	 * Информация при взаимном приеме (первая точка принимает вторую, а вторая первую)
	 *
	 * @param info информация
	 * @return the скрываемый объект
	 */
	public Hiding both(String info) {
		both = new ReceptionItem(info);
		
		return both;
	}

	/**
	 * Установить деление зодиака
	 *
	 * @param div объект деления
	 */
	public void setDivision(Division div) {
		this.div = div;
	}

	protected String calc(Spot spot1, Spot spot2) {
		if (div == null) return null;
		
		double	lon1 = spot1.getEcliptic().getLon();
		double	lon2 = spot2.getEcliptic().getLon();
		int		div1 = div.getHash(lon1);
		int		div2 = div.getHash(lon2);
		
		if (both != null && both.getVisible())
			if (spot1.hashName() == div2 && spot2.hashName() == div1)
				return both.info;
		
		if (forward != null && forward.getVisible())
			if (spot2.hashName() == div1)
				return forward.info;
		
		if (backward != null && backward.getVisible())
			if (spot1.hashName() == div2)
				return backward.info;

		return null;
	}

	public void visibleTag(boolean data, int tag) {
		super.visibleTag(data, tag);
		
		if (both != null) both.visibleTag(data, tag);
		if (forward != null) forward.visibleTag(data, tag);
		if (backward != null) backward.visibleTag(data, tag);
	}
}
