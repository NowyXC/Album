package com.nowy.baselib.utils.html;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

/**
 * Created by Nowy on 2017/12/29.
 */

public class HtmlUtil {

    public static void showHtml(final TextView txtView, String html, Drawable imgDef){
        final Spanned text = Html.fromHtml(html,new URLImageGetter(txtView.getContext(),txtView,imgDef),null);
        txtView.post(new Runnable() {
            @Override
            public void run() {
                txtView.setText(text);
            }
        });
    }
}
