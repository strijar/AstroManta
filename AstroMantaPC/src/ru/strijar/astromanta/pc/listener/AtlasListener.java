package ru.strijar.astromanta.pc.listener;

/**
 * Обработчик запускаемый при выборе населенного пункта в Атласе 
 * @see Sys.atlas
 */
public interface AtlasListener {
	void onAtlas(String name, double lat, double lon, String zone);
}
