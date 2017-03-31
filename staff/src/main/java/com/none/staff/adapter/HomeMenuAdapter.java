package com.none.staff.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.none.staff.R;

/**
 * 菜单适配器
 * @author willis
 *
 */
public class HomeMenuAdapter extends CommonBaseListAdapter<String> {
	
	private int[] mMenuIconArray = {
			R.drawable.viewall,
			R.drawable.calendar,
			R.drawable.green_laisee_icon,
			R.drawable.settings};


	public HomeMenuAdapter(Context context, List<String> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_menu_list_item, parent,false);
		}
		final String str = getList().get(position) ;
		ImageView mPic = CommonViewHolder.get(convertView,R.id.home_menu_userpic);
		TextView mName = CommonViewHolder.get(convertView,R.id.home_menu_name);
		mPic.setImageResource(mMenuIconArray[position]);
		mName.setText(str);
		return convertView;
	}
}
