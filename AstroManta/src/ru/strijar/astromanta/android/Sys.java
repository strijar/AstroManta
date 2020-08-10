package ru.strijar.astromanta.android;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import bsh.EvalError;
import bsh.Interpreter;

import ru.strijar.astro.ChartEclipse;
import ru.strijar.astro.ChartNatal;
import ru.strijar.astro.Charts;
import ru.strijar.astromanta.R;
import ru.strijar.astromanta.android.listener.AtlasListener;
import ru.strijar.astromanta.android.listener.EditChartListener;
import ru.strijar.astromanta.android.listener.ErrorListener;
import ru.strijar.astromanta.android.listener.MenuListener;
import ru.strijar.astromanta.android.listener.StringListener;
import swisseph.SwissEph;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Главное окно и системные методы
 */

public class Sys extends Activity implements SysInterface {
	private SharedPreferences			pref;
	private ViewCharts					viewCharts;
	private ProgressBar					wait;
	private Toast 						toast;
	private LinearLayout				toolbar;
	private ImageButton					menuButton;
	private Info						info;

	private HashMap<String, Bitmap>		bitmaps = new HashMap<String, Bitmap>();
    private HashMap<String, String>		bitmapsSrc = new HashMap<String, String>();
    private HashSet<String>				libs = new HashSet<String>();

	private Charts						charts;
	private SwissEph					eph;

	final private int 					EDITOR = 1;
	final private int 					ATLAS = 2;

	final static String					DB = "db/";
	
	private EditChart					editor;
	private EditChartListener			editorListener;
	private AtlasListener 				atlasListener;
	private MenuListener 				menuListener;
	
    private Handler 					handler = new Handler();
    private Interpreter					bsh = new Interpreter();
	private AssetManager 				assetsManager;

	private static ErrorListener		errorListener;
    static protected String				path;
    static protected String				pathDB;
			
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        
        boolean internal = false;

        String state = Environment.getExternalStorageState();
        
        if (Environment.MEDIA_MOUNTED.equals(state) && internal == false) {
        	path = Environment.getExternalStorageDirectory().getPath() + "/AstroManta/";
        	pathDB = path + DB;

        	java.io.File folder = new java.io.File(path);
        	if (!folder.exists()) folder.mkdirs();
        	
        	folder = new java.io.File(pathDB);
        	if (!folder.exists()) folder.mkdirs();
        } else {
        	@SuppressWarnings("deprecation")
			int modeWorldWriteable = MODE_WORLD_WRITEABLE;
    
			path = getDir("work", modeWorldWriteable).getAbsolutePath() + "/";
			pathDB = path;
        }

        assetsManager = getResources().getAssets();
        info = new Info(this);

        wait = (ProgressBar) findViewById(R.id.waitBar);
        toolbar = (LinearLayout) findViewById(R.id.top_toolbar);

        menuButton = (ImageButton) findViewById(R.id.menu_image);
        menuButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				invalidateOptionsMenu();
				openOptionsMenu();
			}
		});
        
        // Эффемериды и карты //
        
        eph = new SwissEph(path + "ephe/");        
        charts = new Charts(eph);
        
        // Панель отрисовки карт //
        
        viewCharts = (ViewCharts) findViewById(R.id.chart);
        viewCharts.setSys(this);
        viewCharts.setCharts(charts);
        
        bshStart();
	}
	
	private void bshStart() {
		libs.clear();
		bsh = new Interpreter();

		try {
			bsh.set("Sys", this);
			bsh.set("Charts", charts);
			bsh.set("ViewCharts", viewCharts);
			bsh.set("Info", info);
			bsh.set("Eph", eph);
			bsh.set("i18n", new I18n());
		} catch (EvalError e1) {
			e1.printStackTrace();
			return;
		}

		run("start.bsh");
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
    		invalidateOptionsMenu();
			openOptionsMenu();
    		return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	 
	public void onError(Exception e) {
		Throwable throwable = e.getCause();
		
		if (throwable == null)
			throwable = e;
		
		if (errorListener != null) {
			errorListener.onError(throwable);
		} else {
			alert("Error", throwable.getMessage());
		}
	}
	
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (menuListener != null) {
    		try {
    			return menuListener.onPrepareOptionsMenu(menu);
    		} catch (Exception e) {
    			onError(e);
    		}
    	}

    	return super.onPrepareOptionsMenu(menu);
      }
        
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		switch (requestCode) {
    	    	case EDITOR:
    	    		editor.from(data);
    	    		
    	    		try {
    	    			editorListener.onEdit(editor.getChart());
    	    		} catch (Exception  e) {
    	    			onError(e);
					}
    	    		break;
    	    	
    	    	case ATLAS:
    	    		try {
    	    			atlasListener.onAtlas(
    	    				data.getStringExtra("name"),
    	    				data.getDoubleExtra("lat", 0.0),
    	    				data.getDoubleExtra("lon", 0.0),
    	    				data.getStringExtra("zone")
    	    			);
    	    		} catch (Exception e) {
						onError(e);
					}
    	    		break;
    	    }
    	}
    }

    /**
     * Получение потока чтения из внешнего файла или ресурсов внутри программы
     *
     * @param name имя файла
     * @return объект потока
     */
    public InputStream file(String name) {
    	try {
   			InputStream res = new FileInputStream(path + name);
   			return res;
    	} catch (IOException e_content) {
			try {
				InputStream res = assetsManager.open(name);
				return res;
			} catch (IOException e_assets) {
				Log.e("Not found", name);
				return null;
			}
		}
    }

    /**
     * Получение массива байт из внешнего файла или ресурсов внутри программы
     *
     * @param name имя файла
     * @return массив байт или null если неуспешно
     */
    public byte[] fileBytes(String name) {
    	InputStream				in = file(name);
    	byte[] 					res = null;
    	
		try {
			res = new byte[in.available()];
	    	in.read(res);
	    	in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return res;
    }

    /**
	 * Загрузить изображение или найти его в уже загруженных
	 *
	 * @param name имя изображения
	 * @param file имя файла до изображения
	 * @return объект изображения или null если неудачно
	 */
    public Bitmap loadBitmap(String name, String file) {
		Bitmap res = bitmaps.get(name);
		
		if (res == null) {
		    InputStream	bitmap = file(file);
		    
		    if (bitmap != null) {
		    	res = BitmapFactory.decodeStream(bitmap);
		    	bitmaps.put(name, res);
		    	bitmapsSrc.put(name, file);
		    }
		}
		
		return res;
	}

	/**
	 * Получить путь откуда было загруженно изображение
	 *
	 * @param name имя изображения или имя файла
	 * @return путь до изображения или null если не найдено
	 */
    public String getBitmapSrc(String name) {
		return bitmapsSrc.get(name);
	}

	/**
	 * Запустить скрипт
	 *
	 * @param name имя файла
	 * @return true, если успешно
	 */
   public boolean run(String name) {
    	InputStream in = file(name);

    	if (in == null) 
    		return false;
    	
		try {
			long start = System.currentTimeMillis();
			
			bsh.eval(new InputStreamReader(in));

			Log.d("Run", String.format("%s (%d ms)", name, System.currentTimeMillis() - start));
			return true;
		} catch (Exception e) {
			onError(e);
			return false;
		}
    }
    
   /**
    * Загрузка библиотеки (bsh скриптов)
    *
    * @param name имя файла
    */
    public void lib(String name) {
    	if (!libs.contains(name)) {
    		if (run(name)) {
    			libs.add(name);
    		}
    	}
    }

    /**
     * Открыть самозакрывающееся окно с информацией
     *
     * @param msg сообщение
     */
    public void toast(final String msg) {
    	final Context context = this;
    	
    	runOnUiThread(new Runnable() {
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
					toast.show();
				} else {
					toast.setText(msg);
					toast.show();
				}
			}
		});
    }
    
    /**
     * Установить обработчик настройки меню 
     * @param listener объект обработчика
     */
    public void setMenuListener(MenuListener listener) {
    	menuListener = listener;
    }
    
    /**
     * Установить обработчик ошибок
     * @param listener объект обработчика
     */
    public void setErrorListener(ErrorListener listener) {
    	errorListener = listener;
    }
    
    /**
     * Открыть окно редактирования данных одиночной карты
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого при успешном редактировании
     */
    public void edit(ChartNatal chart, EditChartListener listener) {
    	editorListener = listener;

    	editor = new EditNatal(chart);
    	editor.edit(this, EDITOR);
    }

    /**
     * Открыть окно загрузки данных карты из базы данных
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого после успешной загрузки
     */
    public void load(ChartNatal chart, EditChartListener listener) {
    	editorListener = listener;

    	editor = new EditNatal(chart);
    	editor.load(this, EDITOR);
    }

    /**
     * Открыть окно сохранения данных карты в базе данных
     *
     * @param chart объект карты
     */
    public void save(ChartNatal chart) {
    	editor = new EditNatal(chart);
    	editor.save(this, EDITOR);
    }

    /**
     * Открыть окно редактирования данных карты затмений
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого при успешном редактировании
     */
    public void edit(ChartEclipse chart, EditChartListener listener) {
    	editorListener = listener;

    	editor = new EditEclipse(chart);
    	editor.edit(this, EDITOR);
    }

    /**
     * Открыть окно атласа
     *
     * @param listener объект обработчика запускаемый при выборе населенного пункта 
     */
    public void atlas(AtlasListener listener) {
    	atlasListener = listener;
    	
    	Intent intent = new Intent(this, ActivityAtlas.class);
        startActivityForResult(intent, ATLAS);
    }

    /**
     * Получить список файлов директории
     *
     * @param path путь директории
     * @return список файлов, ключ - имя файла, 
     * значение true если файл внутри ресурсов программы
     * или false если внешний файл
     */
    public HashMap<String, Boolean> dir(String path) {
    	HashMap<String, Boolean> res = new HashMap<String, Boolean>();

    	try {
			for (String item : getResources().getAssets().list(path))
				res.put(item, false);
		} catch (IOException e) {
		}
    	
    	java.io.File folder = new java.io.File(this.path + path);
    	
    	if (folder.exists() && folder.isDirectory()) {
    		for (String item : folder.list()) {
				res.put(item, true);
    		}
    	}

    	return res;
    }

    /**
     * Открыть окно получения строки текста
     *
     * @param msg сообщение
     * @param text значение текста по умолчанию
     * @param listener обработчик запускаемый при окончании редактирования
     * @return строка текста или null если отказ от редактирования
     */
    public void getString(String msg, String text, final StringListener listener) {
		AlertDialog.Builder dialog= new AlertDialog.Builder(this);
		
		dialog.setTitle(msg);

		final EditText input = new EditText(this);
		input.setText(text);

		dialog.setView(input);
		
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	try {
            		listener.onString(input.getText().toString());
            	} catch (Exception e) {
            		onError(e);
            	}
            }
        });

        dialog.show();
	}

    /**
     * Сохранить параметр настроек логического типа
     *
     * @param key ключ параметра
     * @param value значение параметра
     */
    public void prefSet(String key, boolean value) {
        pref = getPreferences(MODE_PRIVATE);
        Editor ed = pref.edit();

        ed.putBoolean(key, value);
        ed.commit();
    }
    
    /**
     * Получить параметр настроек логического типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public boolean prefGet(String key, boolean defValue) {
        pref = getPreferences(MODE_PRIVATE);
    	return pref.getBoolean(key, defValue); 
    }

    /**
     * Сохранить параметр настроек
     *
     * @param key ключ параметра
     * @param value значение параметра
     */
    public void prefSet(String key, float value) {
        pref = getPreferences(MODE_PRIVATE);
        Editor ed = pref.edit();

        ed.putFloat(key, value);
        ed.commit();
    }

   /**
    * Получить параметр настроек вещественного типа
    *
    * @param key ключ параметра
    * @param defValue значение если параметр не найден
    * @return значение параметра
    */
    public Float prefGet(String key, float defValue) {
        pref = getPreferences(MODE_PRIVATE);
    	return pref.getFloat(key, defValue); 
    }

    /**
     * Сохранить параметр настроек целого типа
     *
     * @param key ключ параметра
     * @param value значение параметра
     */
    public void prefSet(String key, int value) {
        pref = getPreferences(MODE_PRIVATE);
        Editor ed = pref.edit();

        ed.putInt(key, value);
        ed.commit();
    }

    /**
     * Получить параметр настроек целого типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public int prefGet(String key, int defValue) {
        pref = getPreferences(MODE_PRIVATE);
    	return pref.getInt(key, defValue); 
    }

    /**
     * Сохранить параметр настроек текстового типа
     *
     * @param key ключ параметра
     * @param value значение параметра
     */
    public void prefSet(String key, String value) {
        pref = getPreferences(MODE_PRIVATE);
        Editor ed = pref.edit();

        ed.putString(key, value);
        ed.commit();
    }

    /**
     * Получить параметр настроек текстового типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public String prefGet(String key, String defValue) {
        pref = getPreferences(MODE_PRIVATE);
    	return pref.getString(key, defValue); 
    }

    /**
     * Поставить в очередь отложенных задач
     * @param task объект задачи
     * @param timeout таймаут перед запуском (в мс)
     */
    public void taskDelayed(Runnable task, long timeout) {
		handler.postDelayed(task, timeout);
    }

    /**
     * Удалить задачу из очереди запуска
     * @param task объект задачи
     */
    public void taskRemove(Runnable task) {
		handler.removeCallbacks(task);
    }

    /**
     * Запустить задачу в фоновом режиме с отображением индикатора занятости
     * @param back объект задачи
     */
    public void asyncTaskWait(final Runnable back) {
    	AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
    	    protected void onPreExecute() {
    		      super.onPreExecute();
    		      wait.setVisibility(View.VISIBLE);
    		    }

    	    protected Void doInBackground(Void... params) {
				try { 
					back.run();
				} catch (Exception e) {
					onError(e);
				}
				return null;
			}

    	    protected void onPostExecute(Void result) {
    	    	super.onPostExecute(result);
    	    	wait.setVisibility(View.GONE);
    	    }
    	};
    	
    	task.execute();
    }

    /**
     * Запустить задачу в фоновом режиме
     * @param back объект задачи
     */
    public void asyncTask(final Runnable back) {
    	AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try { 
					back.run();
				} catch (Exception e) {
					onError(e);
				}
				return null;
			}
    	};
    	
    	task.execute();
    }
    
    /**
     * Получить объект области инструментов 
     */
    public LinearLayout getToolbar() {
    	return toolbar;
    }

    /**
     * Выйти из программы
     */
    public void exit() {
    	System.exit(0);
    }

    /**
     * Перезапустить программу
     */
    public void reset() {
    	viewCharts.clear();
    	charts.clear();
    	charts.clearDivision();

    	bitmaps.clear();
		bitmapsSrc.clear();

    	info.clear();
    	viewCharts.repaint();

    	bshStart();
    }

    /**
     * Открыть окно предупреждения
     *
     * @param title заголовок окна
     * @param msg сообщение
     */
    public void alert(final String title, final String msg) {
    	final Context context = this;
    	
    	runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
 
				builder.setTitle(title).setMessage(msg).setCancelable(true);
    	
				AlertDialog alert = builder.create();
				alert.show();
			}
    	});
    }
    
    /**
     * Загрузка пакета дополнений по сети
     * @param url сетевой адррес пакета
     */
    public void download(String url) {
    	new DownloadFileFromURL(this, url).execute();
    }

    /**
     * Загрузка сообщений локализации из json файла
     * @param name имя файла
     */
    public void i18n(String name) {
    	byte[] bytes = fileBytes(name);
    	
    	if (bytes != null) {
    		String file = new String(bytes); 
    		
    		I18n.load(file);
    	}
    }
}
