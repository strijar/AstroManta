package ru.strijar.astro;

class CheckSpotCategoryItem implements CheckSpotItem {
	private int hash;
	
	public CheckSpotCategoryItem(String category) {
		hash = category.hashCode();
	}
	
	public boolean check(Spot spot1, Spot spot2) {
		if (spot1.getCategory().hashCode() == hash) {
			return true;
		}
		
		if (spot2.getCategory().hashCode() == hash) {
			return true;
		}
		
		return false;
	}
}