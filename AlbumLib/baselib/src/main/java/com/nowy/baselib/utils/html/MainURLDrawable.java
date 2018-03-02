/**
 * @dec 
 */
package com.nowy.baselib.utils.html;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

import com.nowy.baselib.utils.DeviceUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;


/**
 *@Title MainURLDrawable
 *@dec TODO
 *@Author YANGQIYUN
 *@date 2015年11月20日
 */

/**
 * @Title MainURLDrawable.java
 * @dec
 * @author Administrator
 * @date 2015年11月20日
 */
public class MainURLDrawable implements ImageGetter {
	private Context context;
	public MainURLDrawable(Context context){
		this.context = context;
	}
	@Override
	public Drawable getDrawable(String source) {
		// TODO Auto-generated method stub
		Drawable drawable = null;  
        URL url;  
        try {  
            url = new URL(source);  
            drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        } 
        int width = DeviceUtil.getDeviceWidth(context);
        float scale = (float)width/(float)drawable.getIntrinsicWidth();
//        drawable.setBounds(0, 0, width,  
//                (int) (drawable.getIntrinsicHeight() * scale));  
//        Bitmap drawableToBitmap = drawableToBitmap(drawable);
        Drawable zoomDrawable = zoomDrawable(drawable, width, (int) (drawable.getIntrinsicHeight() * scale));
        zoomDrawable.setBounds(0, 0, width,  
                (int) (drawable.getIntrinsicHeight() * scale));
        return zoomDrawable;
//        return drawable;
	}
	private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这�?100表示不压缩，把压缩后的数据存放到baos�?
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream�?
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
}
	// drawable 转换成 bitmap   
	public static Bitmap drawableToBitmap(Drawable drawable){  
      int width = drawable.getIntrinsicWidth();   // 取 drawable 的长宽   
      int height = drawable.getIntrinsicHeight();  
      Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;         // 取 drawable 的颜色格式   
      Bitmap bitmap = Bitmap.createBitmap(width, height, config);     // 建立对应 bitmap   
      Canvas canvas = new Canvas(bitmap);         // 建立对应 bitmap 的画布   
      drawable.setBounds(0, 0, width, height);  
      drawable.draw(canvas);      // 把 drawable 内容画到画布中   
      return bitmap;  
    }  

	@SuppressWarnings("deprecation")
	public static Drawable zoomDrawable(Drawable drawable, int w, int h)  {  
//      int width = drawable.getIntrinsicWidth();  
//      int height= drawable.getIntrinsicHeight();  
      Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap   
//      Matrix matrix = new Matrix();   // 创建操作图片用的 Matrix 对象   
//      float scaleWidth = ((float)w / width);   // 计算缩放比例   
//      float scaleHeight = ((float)h / height);  
//      matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例  
      Bitmap compressImage = compressImage(oldbmp);
//      Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图   
      return new BitmapDrawable(compressImage);       // 把 bitmap 转换成 drawable 并返回   
    }  
}
