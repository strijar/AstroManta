package ru.strijar.astro;

/**
 * Куспида или ось
 */

public class HouseCusp extends Spot {
	private int		id;
	private double	data[];
	
	protected HouseCusp(int Id, double Data[]) {
		id = Id;
		data = Data;
	}
	
	public void calc() {
		ecliptic.setLon(data[id]);
		ecliptic.setLat(0);
	}
}
