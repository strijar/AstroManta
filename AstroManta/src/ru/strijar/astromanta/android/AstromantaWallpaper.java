package ru.strijar.astromanta.android;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import bsh.EvalError;
import bsh.Interpreter;

import ru.strijar.astro.Charts;
import ru.strijar.astromanta.android.listener.WallpaperListener;
import swisseph.SwissEph;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class AstromantaWallpaper extends WallpaperService implements SysInterface {
	private Charts						charts;
	private SwissEph					eph;
	private ViewChartsEngine			viewCharts;
    private Interpreter					bsh;
    
	private HashMap<String, Bitmap>		bitmaps = new HashMap<String, Bitmap>();
    private HashMap<String, String>		bitmapsSrc = new HashMap<String, String>();

    static protected String				path;
	private AssetManager 				assetsManager;

	public class ViewChartsEngine extends Engine {
		private ArrayList<ViewEcliptic>	viewChart = new ArrayList<ViewEcliptic>();

		private final int				frameDuration = 40;
	    private SurfaceHolder			holder;
	    private boolean					visible;
	    private Handler					handler = new Handler();
	    private SysInterface			sys;
	    private WallpaperListener		listener;

	    private Bitmap					backBitmap;
	    private BitmapDrawable			backDrawable;
	    private int						backColor = Color.WHITE;
		
		private int						width, height;
	   
	    protected void setSys(SysInterface sys) {
	    	this.sys = sys;
	    }
	    
	    private Runnable drawRunnable = new Runnable() {
	        public void run() {
		        if (visible) {
		            Canvas canvas = holder.lockCanvas();
		            canvas.save();

		            if (listener != null)
		            	listener.onUpdate();

		            canvas.drawColor(backColor);

		            if (backDrawable != null) {
		    			backDrawable.setBounds(0, 0, width, height);
		            	backDrawable.draw(canvas);
		            }
	                
		            for (ViewEcliptic item : viewChart)
		            	if (item.getVisible()) 
		            		item.draw(canvas);
		                
		            canvas.restore();
		            holder.unlockCanvasAndPost(canvas);
		            handler.removeCallbacks(drawRunnable);
		            handler.postDelayed(drawRunnable, frameDuration);
		        }
	        }
	    };

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			
		    holder = surfaceHolder;
		}
		
		@Override
	    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			this.width = width;
			this.height = height;
			
			if (listener != null) {
				listener.onChanged(width, height);
			}
	    }
	    
		@Override
		public void onVisibilityChanged(boolean visible) {
		    this.visible = visible;

		    if (visible) {
		        handler.post(drawRunnable);
		    } else {
		        handler.removeCallbacks(drawRunnable);
		    }
		}
		
		@Override
		public void onDestroy() {
		    super.onDestroy();
		    handler.removeCallbacks(drawRunnable);
		}
		
		public ViewEcliptic addViewEcliptic() {
			ViewEcliptic item = new ViewEcliptic(charts, sys);

			viewChart.add(item);
		
			return item;
		}
		
		public void setListener(WallpaperListener listener) {
			this.listener = listener;
		}
		
		public void setBackground(String file) {
			backBitmap = sys.loadBitmap(file, file);
			
			if (backBitmap != null) {
				backDrawable = new BitmapDrawable(getResources(), backBitmap);
				backDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			}
		}
		
		public void setBackground(int color) {
			backColor = color;
		}
		
	}
	
	@Override
	public Engine onCreateEngine() {
        assetsManager = getResources().getAssets();

		bsh = new Interpreter();
		eph = new SwissEph();        
        charts = new Charts(eph);

        viewCharts = new ViewChartsEngine();
        viewCharts.setSys(this);

        try {
			bsh.set("Sys", this);
			bsh.set("Charts", charts);
			bsh.set("ViewCharts", viewCharts);
			bsh.set("Eph", eph);
		} catch (EvalError e1) {
			e1.printStackTrace();
		}

		run("wallpaper.bsh");

		return viewCharts;
	}

	@Override
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
			Log.e("Run", e.getCause().getMessage());
			return false;
		}
	}
}
