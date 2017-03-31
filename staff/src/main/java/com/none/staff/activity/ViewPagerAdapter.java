package com.none.staff.activity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.hsbc.greenpacket.customersegment.util.CustomerSegmentUtil;

public class ViewPagerAdapter extends PagerAdapter {
	

	
	private static final String TAG = ViewPagerAdapter.class.getSimpleName();
	private Context context;
	private View[] views;
	private String[] srcNames;
	private OnClickListener lastPageClickListener=null;
	public ViewPagerAdapter(Context context, String[] srcNames){
		this.context = context;
		this.srcNames = srcNames;
		this.views = new View[srcNames.length];
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return (arg0 == arg1);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
	    Log.d(TAG, "init item "+position);
		if (views[position] == null) {
			views[position] = getView(srcNames[position]);
			if(position==srcNames.length-1){
			    views[position].setOnClickListener(lastPageClickListener);
			}
		}
		((ViewPager)container).addView(views[position], 0);
		return views[position];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView((View)object);
	}
	
	private View getView(String imageName) {
		ImageView iv = new ImageView(context);
		iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		CustomerSegmentUtil.setImageBgByName(iv,context,imageName);
		iv.setScaleType(ScaleType.CENTER_CROP);
		return iv;
	}
	public void setLastPageClickListener(OnClickListener lastPageClickListener){
	    this.lastPageClickListener=lastPageClickListener;
	}
}
