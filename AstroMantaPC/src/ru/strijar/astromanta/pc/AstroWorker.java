package ru.strijar.astromanta.pc;

/**
 * Обработчик для запуска фонового процесса
 */

public interface AstroWorker {
	
	/**
	 * Работа в фоновом режиме
	 */
	public void doInBackground();
	
	/**
	 * Запуск после окончания фоновой задачи
	 */
	public void done();
}
