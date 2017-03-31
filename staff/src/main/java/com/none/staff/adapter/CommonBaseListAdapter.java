package com.none.staff.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

/**
 * 共用的基础list适配器
 * @author willis
 * @param <E> 要适配的bean对象
 */
public abstract class CommonBaseListAdapter<E> extends BaseAdapter {

	/**范型集合**/
	public List<String> list;

	/**上下文*/
	public Context mContext;

	/**布局加载器*/
	public LayoutInflater mInflater;

	/**取得集合对象***/
	public List<String> getList() {
		return list;
	}

	/***设置集合对象**/
	public void setList(List<String> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	/**给集合中添加对象**/
	public void add(String e) {
		this.list.add(e);
		notifyDataSetChanged();
	}

	/**给集合中添加集合**/
	public void addAll(List<String> list) {
		this.list.addAll(list);
		notifyDataSetChanged();

	}

	/**删除指定位置的对象**/
	public void remove(int position) {
		this.list.remove(position);
		notifyDataSetChanged();
	}

	public CommonBaseListAdapter(Context context, List<String> list) {
		super();
		this.mContext = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	/***取得条目对象*/
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = bindView(position, convertView, parent);
		// 绑定内部点击监听
		addInternalClickListener(convertView, position, list.get(position));
		return convertView;
	}

	public abstract View bindView(int position, View convertView,
			ViewGroup parent);

	// adapter中的内部点击事件
	//	public Map<Integer, onInternalClickListener> canClickItem;

	/**** 可以使用 sparseArray来替换 HashMap来提高 效率
	 * public SparseArray<onInternalClickListener> canClickItem ;   ***/
	//SparseArray就是类似于一个Map集合。
	public SparseArray<onInternalClickListener> canClickItem ; 

	private void addInternalClickListener(final View itemV, final Integer position,final Object valuesMap) {
		if (canClickItem != null) {
			//			for (Integer key : canClickItem.keySet()) {
			//				View inView = itemV.findViewById(key);
			//				final onInternalClickListener inviewListener = canClickItem.get(key);
			//				if (inView != null && inviewListener != null) {
			//					inView.setOnClickListener(new OnClickListener() {
			//
			//						public void onClick(View v) {
			//							inviewListener.OnClickListener(itemV, v, position,
			//									valuesMap);
			//						}
			//					});
			//				}
			//			}

			for(int i = 0; i < canClickItem.size(); i++) {
				int key = canClickItem.keyAt(i);
				// get the object by the key.
				View inView = itemV.findViewById(key);
				final onInternalClickListener inviewListener = canClickItem.get(key);
				if (inView != null && inviewListener != null) {
					inView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							inviewListener.OnClickListener(itemV, v, position,valuesMap);
						}
					});
				}
			}
		}
	}



	/**给SparseArray赋值*/
	public void setOnInViewClickListener(Integer key,
			onInternalClickListener onClickListener) {
		if (canClickItem == null)
			//canClickItem = new HashMap<Integer, onInternalClickListener>();
			canClickItem = new SparseArray<onInternalClickListener>() ;
			canClickItem.put(key, onClickListener);
	}

	public interface onInternalClickListener {
		public void OnClickListener(View parentV, View v, Integer position,
				Object values);
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(mContext, text,
								Toast.LENGTH_SHORT);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});

		}
	}




	/**
	 * 打Log ShowLog
	 * 
	 * @return void
	 * @throws
	 */
	public void ShowLog(String msg) {
		Log.d(CommonBaseListAdapter.class.getName(), msg) ;
	}
}
