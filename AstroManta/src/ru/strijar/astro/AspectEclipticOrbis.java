package ru.strijar.astro;

import java.util.ArrayList;

/**
 * Орбис аспекта по эклиптической долготе
 */

public class AspectEclipticOrbis {
	
	// Базовый класс орбиса //
	
	private class OrbisItem {
		protected double	orbis;
		
		protected OrbisItem(double orbis) {
			this.orbis = orbis;
		}
		
		protected boolean check(Spot spot1, Spot spot2) {
			return true;
		}
	}
	
	// Орбисы по именам точек //
	
	private class OrbisNameItem extends OrbisItem {
		private int hash;
		
		protected OrbisNameItem(double orbis, String name) {
			super(orbis);
			hash = name.hashCode();
		}
		
		protected boolean check(Spot spot1, Spot spot2) {
			if (spot1.getName().hashCode() == hash) {
				return true;
			}

			if (spot2.getName().hashCode() == hash) {
				return true;
			}
			
			return false;
		}
	}

	private class OrbisNamesItem extends OrbisItem {
		private int hash1;
		private int hash2;
		
		public OrbisNamesItem(double orbis, String name1, String name2) {
			super(orbis);

			hash1 = name1.hashCode();
			hash2 = name2.hashCode();
		}
		
		public boolean check(Spot spot1, Spot spot2) {
			int hash1 = spot1.getName().hashCode();
			int hash2 = spot2.getName().hashCode();
			
			return 
				((hash1 == this.hash1) && (hash2 == this.hash2)) ||
				((hash1 == this.hash2) && (hash2 == this.hash1));
		}
	}
	
	// Основной класс //
	
	private ArrayList<OrbisItem> 	items = new ArrayList<OrbisItem>(); 
	private Double					orbis;

	protected AspectEclipticOrbis() {
	}
	
	protected AspectEclipticOrbis(double orbis) {
		this.orbis = orbis;
	}
	
	protected Double get(Spot spot1, Spot spot2) {
		for (OrbisItem item: items) {
			if (item.check(spot1, spot2))
				return item.orbis;
		}
	
		return orbis;
	}

	/**
	 * Добавить значение орбиса для любой точки
	 *
	 * @param orbis орбис (в градусах)
	 */
	public void add(double orbis) {
		items.add(new OrbisItem(orbis));
	}
	
	/**
	 * Добавить значение орбиса для точки с именем
	 *
	 * @param orbis орбис (в градусах)
	 * @param name имя точки
	 */
	public void addName(double orbis, String name) {
		items.add(new OrbisNameItem(orbis, name));
	}

	/**
	 * Добавить значение орбиса для пары точке по именам
	 *
	 * @param orbis орбис (в градусах)
	 * @param name1 имя первой точки
	 * @param name2 имя второй точки
	 */
	public void addName(double orbis, String name1, String name2) {
		items.add(new OrbisNamesItem(orbis, name1, name2));
	}

	/**
	 * Установить значение орбиса по умолчанию
	 * @since 5.1
	 * @param orbis орбис (в градусах)
	 */
	public void setDefault(double orbis) {
		this.orbis = orbis;
	}

	/**
	 * Добавить значение орбиса для точки из категории (не реализовано)
	 *
	 * @param orbis орбис (в градусах)
	 * @param category категория точки
	 */
	public void addCategory(double orbis, String category) {
	}

	/**
	 * Добавить значение орбиса для пары точек из категории (не реализовано)
	 *
	 * @param orbis орбис (в градусах)
	 * @param category1 категория первой точки
	 * @param category2 категория второй точки
	 */
	public void addCategory(double orbis, String category1, String category2) {
	}

}
