package ru.strijar.astromanta.pc.listener;

import ru.strijar.astro.ChartNatal;

/**
 * Обработчик запускаемый после успешной загрузки данных из базы карт
 * @see Sys.edit
 */

public interface EditChartListener {
	void onEdit(ChartNatal chart);
}
