package ru.strijar.astromanta.android;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import ru.strijar.astromanta.R;

import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SlidingDrawer;

/**
 * Панель с информацией (для вывода HTML)
 */

@SuppressWarnings("deprecation")
public class Info {
	private SlidingDrawer				slider;
	private WebView						info;
	private String						infoBuf = new String();
	private Sys				sys;
	private HashMap<String, Drawable>	objList = new HashMap<String, Drawable>();

	private class WebLoad extends WebViewClient {
		/*
		public void onPageFinished(WebView view, String url) {
		    super.onPageFinished(view, url);
		    view.clearCache(true);
		}
		*/

		public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
			WebResourceResponse response = null;
			InputStream 		input;
	
    		if (url.startsWith("astromanta://")) {
    			url = url.replaceFirst("astromanta://", "");
 
    			Drawable obj = objList.get(url);
    			
    			if (obj != null) {
    				obj.update();
	    			input = new ByteArrayInputStream(obj.out());
    			} else {
    				input = new ByteArrayInputStream(sys.fileBytes(url));
 	    		}

    			if (input != null) {
    				response = new WebResourceResponse("text/html", "utf-8", input);
    			}
    			
	    		return response;
    		}
    		return super.shouldInterceptRequest(view, url);
		} 

	}
	
	protected Info(Sys sys) {
		this.sys = sys;

		info = (WebView) sys.findViewById(R.id.TextView);
        info.getSettings().setBuiltInZoomControls(false);
        info.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        info.getSettings().setAppCacheEnabled(false);
        info.setWebViewClient(new WebLoad());
        info.setBackgroundColor(0);
        
        slider = (SlidingDrawer) sys.findViewById(R.id.slidingDrawer);
	}

	/**
     * Добавить текст в буффер
     *
     */

	public void add(String text) {
		infoBuf += text;
	}

	/**
     * Вывысти накопленный буфер
     *
     */
	
	public void out() {
		out(infoBuf);
	}

	/**
     * Вывести текст
     *
     */
	
	public void out(final String text) {
    	sys.runOnUiThread(new Runnable() {
			public void run() {
				info.loadDataWithBaseURL("", text, "text/html", "utf-8", null);
			}
		});
    }

	/**
     * Очистить текст и буффер
     * 
     */

	public void clear() {
		infoBuf = new String();
		objList.clear();
		out(infoBuf);
	}

	/**
	 * Добавить отображаемый объект
	 * @param name имя объекта
	 * @param obj объект для отображения
	 */
	public void addObj(String name, Drawable obj) {
		objList.put(name, obj);
	}

	/**
     * Установвить видимость
     * @param on видимость
     * 
     */
	
	public void setVisible(final boolean on) {
    	sys.runOnUiThread(new Runnable() {
			public void run() {
				if (on) {
					slider.animateOpen();
				} else {
					slider.animateClose();
				}
			}
		});
    }

	/**
     * Установвить видимость и ширину
     * @param on видимость
     * @param width ширина (в точках)
     * 
     */

    public void setVisible(final boolean on, final int width) {
    	sys.runOnUiThread(new Runnable() {
			public void run() {
				slider.getLayoutParams().width = width;
				
				if (on) {
					slider.animateOpen();
				} else {
					slider.animateClose();
				}
			}
		});
    }

}
