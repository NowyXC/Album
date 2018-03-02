package com.nowy.baselib.utils.html;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import java.net.URL;

public class URLImageGetter implements ImageGetter {
    TextView textView;
    Context context;
    Drawable imgDef;
    public URLImageGetter(Context context, TextView textView,Drawable imgDef) {
     this.context = context;
     this.textView = textView;
     this.imgDef = imgDef;
    }
    
    @Override
    public Drawable getDrawable(String paramString) {
        URLDrawable urlDrawable = new URLDrawable(context,imgDef);
        
        ImageGetterAsyncTask getterTask = new ImageGetterAsyncTask(urlDrawable);
        getterTask.execute(paramString);
        return urlDrawable;
    }
    
    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;
        
        public ImageGetterAsyncTask(URLDrawable drawable) {
            this.urlDrawable = drawable;
        }
        
      @Override
      protected void onPostExecute(Drawable result) {
            if (result != null) {
                urlDrawable.drawable = result;
                URLImageGetter.this.textView.requestLayout();
            }
        }
        
       @Override
       protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }
        
        public Drawable fetchDrawable(String url) {
         Drawable drawable = null;
         URL Url;   
         try {    
         Url = new URL(url);    
          drawable = Drawable.createFromStream(Url.openStream(), "");   
         } catch (Exception e) {    
          return null;   
         }
         //����������ͼƬ
         Rect bounds = null;
         if(drawable != null){
        	 int screenWidth = getWidth(context);
			 int draWidth = screenWidth - dip2px(context, 24);
			 
			 int draHeight   = screenWidth *  drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
        	 bounds = new Rect(0, 0, draWidth, draHeight);
         }else{
        	 bounds = getDefaultImageBounds(context);
         }
         int newwidth = bounds.width();
         int newheight = bounds.height();
         double factor = 1;
         double fx = (double)drawable.getIntrinsicWidth() / (double)newwidth;
         double fy = (double)drawable.getIntrinsicHeight() / (double)newheight;
         factor = fx > fy ? fx : fy;
         if (factor < 1) factor = 1;
         newwidth = (int)(drawable.getIntrinsicWidth() / factor);
         newheight = (int)(drawable.getIntrinsicHeight() / factor);
         drawable.setBounds(0, 0, newwidth, newheight);
         return drawable;
        }
    }
    
    //Ԥ��ͼƬ��߱���Ϊ 4:3
    @SuppressWarnings("deprecation")
    public Rect getDefaultImageBounds(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = (int) (width * 3 / 4);
        Rect bounds = new Rect(0, 0, width, height);
        return bounds;
    }
    /**
     * @dec ��ȡ��Ļ��
     * @param context ������
     * @return
     */
    @SuppressWarnings("deprecation")
	public static int getWidth(Context context){
		WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}
    /**
     * @dec dpתpx
     * @param context ������
     * @param dipValue dp��С
     * @return
     */
    public static int dip2px(Context context, float dipValue){
    	final float scale = context.getResources().getDisplayMetrics().density; 
    	return (int)(dipValue * scale +0.5f); 
    }
}

