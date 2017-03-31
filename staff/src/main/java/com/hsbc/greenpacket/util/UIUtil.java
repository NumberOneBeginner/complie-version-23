package com.hsbc.greenpacket.util;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


public class UIUtil {
	
    /**
     * Base on device resolution to change dip to px.
     * 
     * @author York Y K LI[Jan 3, 2013]
     * @param context
     * @param dpValue
     * @return
     * 
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Base on device resolution to change px to dip.
     * 
     * @author York Y K LI[Jan 3, 2013]
     * @param context
     * @param dpValue
     * @return
     * 
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    /**
     * @description Get ListView Height when it included in a scrollview
     * @author Cherry
     * @date 2013-02-16
     */
    public static int getListHeight(BaseAdapter adapter, ListView listView,boolean isHaveFooterView){
    	if(adapter == null){
    		return 0;
    	}
    	int totalHeight = 0;
    	int totalListHeight = 0;
    	int totalDividerHeight = 0;
    	int len = 0;
    	
    	if(isHaveFooterView){
    		len = adapter.getCount()+1;
    	}else {
    		len = adapter.getCount();
    	}

    	View listItem = adapter.getView(0, null, listView);   	   		
		listItem.measure(0, 0);
		totalListHeight =listItem.getMeasuredHeight()*len;    	    	
    	totalDividerHeight = listView.getDividerHeight()*(len-1);
    	
    	totalHeight = totalListHeight+totalDividerHeight;
    	return totalHeight;
    }
    
    /**
     * @description Reset ListView Layout
     * @author cherry
     * @date 2013-02-17
     */
    public static void setListViewActualLayout(ListView listView,int height){
    	ViewGroup.LayoutParams listparams = listView.getLayoutParams();
		listparams.height = height;
		listView.setLayoutParams(listparams);
    }
    /**
     * Get Screen width
     * @author York Y K LI[Feb 28, 2013]
     * @param context
     * @return width of pix
     *
     */
    public static int getScreenWidth(Activity context){
        Display display = context.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }
    /**
     * Get device screen height
     * @author York Y K LI[Feb 28, 2013]
     * @param context
     * @return height of pix
     *
     */
    public static int getScreenHeight(Activity context){
        Display display = context.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }
    /**
     * Adjust the image size to fix the special area.
     * @author York Y K LI[Feb 28, 2013]
     * @param photo
     * @param photoView
     * @return
     *
     */
    public static ImageView adjustImageSizeFixToArea(Bitmap photo,ImageView photoView,int maxWidth,int maxHeight){
        if (photo != null) {
            float w = photoView.getWidth();
            float bitmapH = photo.getHeight();
            float bitmapW = photo.getWidth();
            int viewWidth=0;
            int viewHeight=0;
            int h = (int) (maxWidth * bitmapH / bitmapW);
            if(h<=maxHeight){//same as max width and resize the image.
                viewWidth=maxWidth;
                viewHeight=h;
            }else{//same as max height and resize the image.
                int tempWidth = (int) (maxHeight * bitmapW/bitmapH);
                viewHeight=maxHeight;
                viewWidth=tempWidth;               
            }
            
            LayoutParams lp = photoView.getLayoutParams();
            lp.width=viewWidth;
            lp.height =viewHeight;
            photoView.setImageBitmap(photo);
        }
        return photoView;
    }
    /**
     * 
     * add by louischen
     * 
     */
    public static Bitmap readBitMap(Context context, int resId){ 
        BitmapFactory.Options opt = new BitmapFactory.Options();
        //opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
    
    /**
     * release bitmap indeed during onDestory phase
     * add by louischen
     * @param imageView
     */
    public static void releaseBitmap(ImageView imageView){
        if(imageView != null && imageView.getDrawable() != null){
            Bitmap oldBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            imageView.setImageDrawable(null);
            if(oldBitmap != null){ 
                oldBitmap.recycle(); 
                oldBitmap = null;
            }
        }
    }
    
    public static ImageView adjustImageViewSizeFixToImageRadio(Activity activity, int resId, ImageView photoView){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        //opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = activity.getResources().openRawResource(resId);
        Bitmap photo = BitmapFactory.decodeStream(is, null, opt);
        
        if (photo != null) {
            Rect rect = new Rect();
            View a = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            if(a!=null){
                a.getWindowVisibleDisplayFrame(rect);
                int screenWidthPX = rect.width();
                
                float bitmapH = photo.getHeight();
                float bitmapW = photo.getWidth();
                int h = (int) (screenWidthPX * bitmapH / bitmapW);
                
                LayoutParams lp = photoView.getLayoutParams();
                lp.width = screenWidthPX;
                lp.height = h;
                photoView.setLayoutParams(lp);
                photoView.setImageBitmap(photo);
            }else{
                Log.e("UI Util", "can't find activity window");
            }
        }
        return photoView;
    }
}
