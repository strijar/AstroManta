package ru.strijar.astromanta.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ru.strijar.astromanta.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;

public class DownloadFileFromURL extends AsyncTask<String, Integer, Integer> implements OnCancelListener, OnClickListener {
	private ProgressDialog 	dialog;
	private String 			file;
    private URL 			url;
	private URLConnection	conection;
	private Context			context;
	
	protected DownloadFileFromURL(Context context, String file) {
		this.context = context;
        dialog = new ProgressDialog(context);
 
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(this);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(android.R.string.cancel), this);

        this.file = file;
	}
	
	protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

	protected Integer doInBackground(String... params) {
		int count;
	    InputStream input = null;

        try {
			url = new URL(file);
	        conection = url.openConnection();

	        conection.connect();
			input = new BufferedInputStream(url.openStream(), 8192);
        } catch (Exception e) {
            return R.string.pack_connection;
        }

        ZipInputStream	zinput = new ZipInputStream(input);
        ZipEntry		entry = null;
        byte 			data[] = new byte[1024];

        while (true) {
            int	total = 0;

            try {
				entry = zinput.getNextEntry();
			} catch (IOException e) {
				return R.string.pack_content;
			}

        	if (entry == null) break;
        	if (entry.isDirectory()) continue;

        	String	name = entry.getName();
            boolean	db = name.startsWith(Sys.DB);
        	String 	path;

        	if (db) {
            	name = name.replaceFirst(Sys.DB, "");
            	path = Sys.pathDB;
            } else {
            	path = Sys.path;
	        	String	dir = name.substring(0, name.lastIndexOf("/"));

            	if (dir != null) {
	        		java.io.File folder = new java.io.File(path + dir);

	        		if (!folder.exists()) folder.mkdirs();
	        	}
            }
        	
	        dialog.setMax((int) (entry.getSize() / 1024));

	        FileOutputStream		fos = null;
            BufferedOutputStream	bos;
            
            try {
				fos = new FileOutputStream(path + name);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

           	bos = new BufferedOutputStream(fos, data.length);
            
            try {
				while ((count = zinput.read(data, 0, data.length)) != -1) {
				    bos.write(data, 0, count);
				    total += count;
				    publishProgress(total/1024);
				}

				bos.flush();
                bos.close();    	
            } catch (IOException e) {
				return R.string.pack_content;
			}
        }

        try {
			input.close();
		} catch (IOException e) {
			return R.string.pack_content;
		}
        return R.string.pack_ok;
	}

	protected void onProgressUpdate(Integer... progress) {
        dialog.setProgress(progress[0]);
    }

    protected void onPostExecute(Integer res) {
        dialog.dismiss();

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	 
    	builder.setMessage(res).setCancelable(true);
    	builder.setPositiveButton(android.R.string.ok, null);
    	builder.show();
    }

	public void onCancel(DialogInterface dialog) {
		cancel(true);
	}

	public void onClick(DialogInterface dialog, int which) {
		cancel(true);
	}
}
