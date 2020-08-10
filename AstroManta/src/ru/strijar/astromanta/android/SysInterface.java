package ru.strijar.astromanta.android;

import java.io.InputStream;

import android.graphics.Bitmap;

public interface SysInterface {
	Bitmap loadBitmap(String name, String file);
	InputStream file(String name);
	
	boolean run(String name);

}
