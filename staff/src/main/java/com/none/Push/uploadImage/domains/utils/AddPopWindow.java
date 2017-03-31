package com.none.Push.uploadImage.domains.utils;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.none.staff.R;

public class AddPopWindow  extends PopupWindow{


	private Context mcontext;
	private LayoutInflater inflater;
	private ListView pop_list;
	private String[] mActivitySubject;
	private TextView spinner_et;
	private OnItemOnClickListener mItemOnClickListener;
	private OnDismissClickListener onDismissClickListener;
	public AddPopWindow(Context context,String []activitySubject){

		this.mcontext=context;
		//
		//		if(activitySubject.length==0||activitySubject==null){
		//			this.mActivitySubject=new String []{};
		//		}else{
		//			this.mActivitySubject=activitySubject;
		//		}
		//	
		this.mActivitySubject=activitySubject;

		inflater=(LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflater.inflate(R.layout.add_popup_dialog, null));
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		setTouchable(true);
		setFocusable(true); 
		setOutsideTouchable(true);
		ColorDrawable dw = new ColorDrawable(0000000000);  
		this.setBackgroundDrawable(dw);  
		update();
		initViews();

		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

				if(onDismissClickListener != null)
					onDismissClickListener.onDismissClick();
			}
		});

	}

	private void initViews() {

		pop_list=(ListView)getContentView().findViewById(R.id.pop_list);
		pop_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				dismiss();
				spinner_et.setText(mActivitySubject[arg2]);

				if(mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(arg2);
			}
		});



		pop_list.setAdapter(new BaseAdapter() {

			private ViewHolder viewHolder;

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if(convertView == null){
					convertView = LayoutInflater.from(mcontext).inflate(R.layout.pop_category_item, parent,false) ;
					viewHolder = new ViewHolder() ;
					viewHolder.show_select_tv = (TextView) convertView.findViewById(R.id.show_select_textview) ;
					convertView.setTag(viewHolder) ;

				}else{
					viewHolder = (ViewHolder) convertView.getTag() ;
				}
				viewHolder.show_select_tv.setText(mActivitySubject[position]);
				return convertView;

			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				return mActivitySubject.length;
			}
		});

	}


	public void showPopwindow(View parent){
		if(!this.isShowing()){

			this.showAsDropDown(parent, parent.getLayoutParams().width/2, 30);	

		}

	}

	public void showPopwindowdhalf(TextView parent,int height ,int width){
		if(!this.isShowing()){

			spinner_et=parent;

			this.showAsDropDown(parent,0
					,0);	

		}

	}

	private class ViewHolder{
		private TextView show_select_tv;
	}


	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}

	public static interface OnItemOnClickListener{
		public void onItemClick(int position);
	}


	public void setDismissCliklistern(OnDismissClickListener    onDismissClickListener){

		this.onDismissClickListener = onDismissClickListener;

	}


	public static interface OnDismissClickListener{
		public void onDismissClick();
	}



}
