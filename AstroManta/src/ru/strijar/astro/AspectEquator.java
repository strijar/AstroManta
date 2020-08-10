package ru.strijar.astro;

/**
 * Аспект по склонению
 */
public class AspectEquator extends Aspect {
	private double	orbis;
	private String	one;
	private String	diff;
	
	protected AspectEquator(double orbis) {
		this.orbis = orbis;
		one = "Parallel";
		diff = "ContrParallel";
	}
	
	/**
	 * Информация для параллели. Аспект в пределах орбиса по одну сторону экватора.
	 *
	 * @param info the info
	 */
	public void oneSide(String info) {
		one = info;
	}
	
	/**
	 * Информация для контрпараллели. Аспект в пределах орбиса по разные стороны экватора.
	 *
	 * @param info the info
	 */
	public void diffSide(String info) {
		diff = info;
	}
	
	protected String calc(Spot spot1, Spot spot2) {
		double decl1 = spot1.getEquator().getLat();
		double decl2 = spot2.getEquator().getLat();
		
		if (Math.abs(Math.abs(decl1) - Math.abs(decl2)) < orbis) {
			return decl1 * decl2 > 0 ? one : diff;
		}
		
		return null;
	}
}
