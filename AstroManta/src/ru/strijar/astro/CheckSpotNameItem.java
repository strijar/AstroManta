package ru.strijar.astro;

class CheckSpotNameItem implements CheckSpotItem {
	private int hash;
	
	public CheckSpotNameItem(String name) {
		hash = name.hashCode();
	}
	
	public boolean check(Spot spot1, Spot spot2) {
		if (hash == spot1.hashName())
			return true;

		if (hash == spot2.hashName())
			return true;
	
		return false;
	}
}