package com.none.Push.uploadImage.domains.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.none.staff.R;

public class SpinerAdapter  extends BaseAdapter{

	private List<String> mObjects;

	private LayoutInflater mInflater;



	public static interface IOnItemSelectListener{
		public void onItemClick(int pos);
	};

	public SpinerAdapter(Context context,List<String> mObjects){
		this.mObjects = mObjects;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	public void refreshData(List<String> objects, int selIndex){
		mObjects = objects;
		if (selIndex < 0){
			selIndex = 0;
		}
		if (selIndex >= mObjects.size()){
			selIndex = mObjects.size() - 1;
		}
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mObjects.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mObjects.size();
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.spinner_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		//Object item =  getItem(pos);
		viewHolder.mTextView.setText(mObjects.get(pos));

		return convertView;
	}


	public static class ViewHolder
	{
		public TextView mTextView;
	}


}
