package com.nowy.baselib.utils.html;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;


public class URLDrawable extends BitmapDrawable {
 protected Drawable drawable;
     @SuppressWarnings("deprecation")
     public URLDrawable(Context context,Drawable defDrawable) {
          this.setBounds(getDefaultImageBounds(context));
          drawable = defDrawable;
          drawable.setBounds(getDefaultImageBounds(context));
     }
    @Override
     public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }
    
    @SuppressWarnings("deprecation")
    public Rect getDefaultImageBounds(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = (int) (width * 3 / 4);
        Rect bounds = new Rect(0, 0, width, height);
        return bounds;
    }
}
