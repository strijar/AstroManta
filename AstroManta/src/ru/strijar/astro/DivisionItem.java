package ru.strijar.astro;

public class DivisionItem {
	protected double	from = 0;
	protected double	to = 0;
	private String		info;
	private int			infoHash = 0;
	
	protected DivisionItem() {
	}
	
	protected DivisionItem(double from, double to, String info) {
		this.from = from;
		this.to = to;
		this.info = info;
		infoHash = info.hashCode();
	}

	protected void point(double data) {
		from = data;
		to = data;
	}
	
	protected String getInfo() {
		return info;
	}
	
	protected int hash() {
		return infoHash;
	}

	public int compare(DivisionItem another) {
		if (another.from >= to) return 1;
		if (another.to <= from) return -1;
		
		return 0;
	}

}
