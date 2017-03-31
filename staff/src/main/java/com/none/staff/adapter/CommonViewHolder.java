package com.none.staff.adapter;

import android.util.SparseArray;
import android.view.View;

/**
 * 通用的简化版viewhodler  一次编写 以后方便使用
 * @author willis
 */
@SuppressWarnings("unchecked")
public class CommonViewHolder {
	public static <T extends View> T get(View view, int id) {
		//先得到viewholder判断有无viewholder，再得到childview，判断有无childview
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (null == viewHolder) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (null == childView) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

}
