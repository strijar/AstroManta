package ru.strijar.astro;

class CheckSpotNamesItem implements CheckSpotItem {
	private int hash1;
	private int hash2;
	
	public CheckSpotNamesItem(String name1, String name2) {
		hash1 = name1.hashCode();
		hash2 = name2.hashCode();
	}
	
	public boolean check(Spot spot1, Spot spot2) {
		int hash1 = spot1.hashName();
		int hash2 = spot2.hashName();
		
		return 
			((hash1 == this.hash1) && (hash2 == this.hash2)) ||
			((hash1 == this.hash2) && (hash2 == this.hash1));
	}
}