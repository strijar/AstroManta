package ru.strijar.astro;

class CheckSpotCategoriesItem implements CheckSpotItem {
	private int hash1;
	private int hash2;
	
	public CheckSpotCategoriesItem(String category1, String category2) {
		hash1 = category1.hashCode();
		hash2 = category2.hashCode();
	}
	
	public boolean check(Spot spot1, Spot spot2) {
		int hash1 = spot1.getCategory().hashCode();
		int hash2 = spot2.getCategory().hashCode();
		
		return 
			((hash1 == this.hash1) && (hash2 == this.hash2)) ||
			((hash1 == this.hash2) && (hash2 == this.hash1));
	}
}