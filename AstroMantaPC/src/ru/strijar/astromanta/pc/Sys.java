package ru.strijar.astromanta.pc;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;

import bsh.Interpreter;
import ru.strijar.astro.ChartEclipse;
import ru.strijar.astro.ChartNatal;
import ru.strijar.astro.ChartSpotAngle;
import ru.strijar.astro.Charts;
import ru.strijar.astromanta.pc.gui.AstroMenuBar;
import ru.strijar.astromanta.pc.listener.AtlasListener;
import ru.strijar.astromanta.pc.listener.EditChartListener;
import swisseph.SwissEph;

/**
 * Главное окно и системные методы
 */

@SuppressWarnings("serial")
public class Sys extends JFrame {
	private Charts						charts;
	private SwissEph					eph;
    private Interpreter					bsh;
	private HashMap<String, Image>		bitmap = new HashMap<String, Image>();
    private HashMap<String, String>		bitmapSrc = new HashMap<String, String>();
	private HashMap<String, String>		fileSrc = new HashMap<String, String>();
	private HashSet<String>				libs = new HashSet<String>();

    private Info 						info;
	private ViewCharts					viewCharts;
	private AstroMenuBar				menuBar; 
	private Properties 					property;
	private File 						jarFile;
	
    private String						path = "data/";

    protected Sys() {
    	try {
			jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
    	
    	property = new Properties();
  
    	try {
			property.loadFromXML(new FileInputStream("astromanta.xml"));
		} catch (InvalidPropertiesFormatException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
   
        eph = new SwissEph(path + "ephe/");        
        charts = new Charts(eph);
      
        // GUI
        
        info = new Info();
        viewCharts = new ViewCharts(this, charts);

        menuBar = new AstroMenuBar();
        setJMenuBar(menuBar);

        setSize(1024, 740);
        setMinimumSize(new Dimension(640, 640));

        setTitle("AstroManta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, viewCharts, info.scrollPane);
        split.setDividerLocation(1024-400);
        split.setResizeWeight(1);

        getContentPane().add(split, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
            
    /**
     * Получить меню
     *
     * @return объект меню
     */
    
    public AstroMenuBar menuBar() {
    	return menuBar;
    }
    
    protected void bshStart() {
		libs.clear();
		bsh = new Interpreter();

		try {
			bsh.set("Charts", charts);
			bsh.set("Eph", eph);
			bsh.set("Sys", this);
			bsh.set("ViewCharts", viewCharts);
			bsh.set("Info", info);
			bsh.set("i18n", new I18n());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		run("start.bsh");
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
			bsh.eval(new InputStreamReader(in));

			return true;
		} catch (Exception e) {
			onError(name, e);
			return false;
		}
    }
    
    /**
     * Загрузка библиотеки (bsh скриптов или jar файла)
     *
     * @param name имя файла
     */
    @SuppressWarnings("deprecation")
	public void lib(String name) {
    	if (!libs.contains(name)) {
    		if (name.endsWith(".bsh")) {
    			if (run(name)) {
    				libs.add(name);
    			}
    		} else if (name.endsWith(".jar")) {
				try {
					URL url = bsh.pathToFile(name).toURL();

					bsh.getClassManager().addClassPath(url);
	    			libs.add(name);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }

    protected void onError(String src, Exception e) {
		Throwable throwable = e.getCause();
		
		if (throwable == null)
			throwable = e;

		System.err.print(src + ": " + throwable.getMessage());
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
	    	fileSrc.put(name, "file:" + path + name);
	    	
	    	return res;
    	} catch (IOException e_content) {
    		InputStream res = getClass().getResourceAsStream("/" + name);
    
    		if (res == null) {
    			System.err.print("File not found " + name);
    		} else {
    	    	fileSrc.put(name, getClass().getResource("/" + name).toString());
    		}
    		
    		return res;
    	}
    }

    /**
     * Открыть окно предупреждения
     *
     * @param title заголовок окна
     * @param msg сообщение
     */
    public void alert(final String title, final String msg) {
    	JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Открыть самозакрывающееся окно с информацией
     *
     * @param msg сообщение
     */
    public void toast(final String msg) {
    	final WebNotificationPopup popup = new WebNotificationPopup();
    	
        popup.setDisplayTime(3000);
        popup.add(new JLabel(msg));
        popup.setIcon(NotificationIcon.information.getIcon());
        
    	NotificationManager.showNotification(popup);
    }

	/**
	 * Загрузить изображение или найти его в уже загруженных
	 *
	 * @param name имя изображения
	 * @param file имя файла до изображения
	 * @return объект изображения или null если неудачно
	 */
	public Image loadBitmap(String name, String file) {
		Image res = bitmap.get(name);
		
		if (res == null) {
			try {
				res = ImageIO.read(file(file));
				
				if (res != null) {
					bitmapSrc.put(name, file);
					bitmap.put(name, res);
				}
			} catch (IOException e) {
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
		String file = bitmapSrc.get(name);
		
		return fileSrc.get(file != null ? file : name);
	}
	
    /**
     * Сохранить параметр настроек
     *
     * @param key ключ параметра
     * @param value значение параметра
     */
    public void prefSet(String key, Object value) {
    	property.put(key, value.toString());

    	try {
			property.storeToXML(new FileOutputStream("astromanta.xml"), "");
		} catch (FileNotFoundException e) {
			onError("PrefSave", e);
		} catch (IOException e) {
			onError("PrefSave", e);
		}
    }
    
    /**
     * Получить параметр настроек логического типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public boolean prefGet(String key, boolean defValue) {
    	String res = property.getProperty(key);
    
    	if (res == null)
    		return defValue;
 
    	return res.equals("true");
    }

    /**
     * Получить параметр настроек вещественного типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public Float prefGet(String key, float defValue) {
    	String res = property.getProperty(key);
        
    	if (res == null)
    		return defValue;

    	return Float.parseFloat(res);
    }

    /**
     * Получить параметр настроек целого типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public int prefGet(String key, int defValue) {
    	String res = property.getProperty(key);
        
    	if (res == null)
    		return defValue;
   
    	return Integer.parseInt(res);
    }

    /**
     * Получить параметр настроек текстового типа
     *
     * @param key ключ параметра
     * @param defValue значение если параметр не найден
     * @return значение параметра
     */
    public String prefGet(String key, String defValue) {
    	return property.getProperty(key, defValue);
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
    	HashMap<String, Boolean>	res = new HashMap<String, Boolean>();
    	java.io.File 				folder;

    	// Resource

    	if (jarFile != null && jarFile.isFile()) {
    	    try {
    	    	JarFile						jar = new JarFile(jarFile);
	    	    final Enumeration<JarEntry>	entries = jar.entries();
	    	    
	    	    while (entries.hasMoreElements()) {
	    	        final String item = entries.nextElement().getName();
	    	     
	    	        if (item.startsWith(path + "/")) {
	    	        	String name = item.replace(path + "/", "");
	    	        	
	    	        	if (name.length() > 0) {
	    					res.put(name, false);
	    	        	}
	    	        }
	    	    }
	    	    jar.close();
    	    } catch (IOException e) {
			}
    	}
    	
    	// Local
    	
    	folder = new File(this.path + path);
    	
    	if (folder.exists() && folder.isDirectory())
    		for (String item : folder.list())
				res.put(item, true);

    	return res;
    }
    
    /**
     * Открыть окно получения строки текста
     *
     * @param msg сообщение
     * @param text значение текста по умолчанию
     * @return строка текста или null если отказ от редактирования
     */
    public String getString(String msg, String text) {
    	return JOptionPane.showInputDialog(this, msg, text);
    }
    
    /**
     * Выполнить фоновый процесс
     *
     * @param obj объект обработчика фонового процесса
     */
    public void execute(final AstroWorker obj) {
    	SwingWorker<Void, Void> work = new SwingWorker<Void, Void>() {
   
			protected Void doInBackground() {
				try {
					obj.doInBackground();
				} catch (Exception e) {
					onError("AsyncWorker", e);
				}
				return null;
			}
			
			protected void done() {
				try {
					obj.done();
				} catch (Exception e) {
					onError("AsyncWorker", e);
				}
			}
		};
		
		work.execute();
    }
    
    /**
     * Открыть окно редактирования данных одиночной карты
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого при успешном редактировании
     */
    public void edit(final ChartNatal chart, final EditChartListener listener) {
    	final Sys parent = this;
    	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DialogEditNatal editor = new DialogEditNatal(parent, chart, listener);
				
				editor.setVisible(true);
			}
		});
    }

    /**
     * Открыть окно редактирования данных карты затмений
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого при успешном редактировании
     */
    public void edit(final ChartEclipse chart, final EditChartListener listener) {
    	final Sys parent = this;
    	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DialogEditEclipse editor = new DialogEditEclipse(parent, chart, listener);
				
				editor.setVisible(true);
			}
		});
    }

    /**
     * Открыть окно редактировния данных карты на заданное угловое расстояние
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого при успешном редактировании
     */
    public void edit(final ChartSpotAngle chart, final EditChartListener listener) {
    	final Sys parent = this;
    	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DialogEditSpotAngle editor = new DialogEditSpotAngle(parent, chart, listener);
				
				editor.setVisible(true);
			}
		});
    }

    /**
     * Открыть окно загрузки данных карты из базы данных
     *
     * @param chart объект карты
     * @param listener объект обработчика запускаемого после успешной загрузки
     */
    public void load(final ChartNatal chart, final EditChartListener listener) {
    	final Sys parent = this;

    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		    	DialogCharts dialog = new DialogCharts(chart, listener);
		    	
		    	dialog.setLocationRelativeTo(parent);
		    	dialog.setVisible(true);
			}
		});
    }

    /**
     * Открыть окно сохранения данных карты в базе данных
     *
     * @param chart объект карты
     */
    public void save(final ChartNatal chart) {
    	final Sys parent = this;

    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		    	DialogCharts dialog = new DialogCharts(chart);
		    	
		    	dialog.setLocationRelativeTo(parent);
		    	dialog.setVisible(true);
			}
		});
    }
    
    /**
     * Открыть окно атласа
     *
     * @param listener объект обработчика запускаемый при выборе населенного пункта 
     */
    public void atlas(AtlasListener listener) {
		DialogAtlas	atlas = new DialogAtlas(listener);

		atlas.setLocationRelativeTo(this);
		atlas.setVisible(true);
    }
    
    /**
     * Загрузка сообщений локализации из json файла
     * @param name имя файла
     */
    public void i18n(String name) {
    	I18n.load(file(name));
    }
    
    /**
     * Раскрыть главное окно на весь экран 
     */
    public void maximize() {
    	setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
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

    	bitmap.clear();
		bitmapSrc.clear();

    	viewCharts.repaint();

    	bshStart();
    }

}
