package ru.strijar.astro;

/**
 * Долгота аспекта по эклиптике с учетом пересечения границы знака
 */
public class AspectEclipticLonSign extends AspectEclipticLon {
	private boolean	inside;
	private int		sign;

	protected AspectEclipticLonSign(double lon) {
		super(lon);
	}

	protected AspectEclipticLonSign(double lon, boolean inside) {
		super(lon);
		this.inside = inside;
		this.sign = (int) (lon / 30);
	}

	@Override
	protected String check(Spot spot1, Spot spot2, double lon, boolean reverse) {
		String res = super.check(spot1, spot2, lon, reverse);

		if (res == null)
			return null;
		
		int sign1 = (int) (spot1.getEcliptic().getLon() / 30);
		int sign2 = (int) (spot2.getEcliptic().getLon() / 30);
		int sign = sign1 - sign2;
		
		if (sign < 0) sign = -sign;
		if (sign > 6) sign = 12 - sign;

		if ((sign == this.sign && !inside) || (sign != this.sign && inside))
			return null;
		
		return res;
	}

}
