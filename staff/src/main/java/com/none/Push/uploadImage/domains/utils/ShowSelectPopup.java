package com.none.Push.uploadImage.domains.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.none.Push.uploadImage.domains.Folder;
import com.none.staff.R;

/**
 * 	功能描述：选择类型的（继承自PopupWindow）
 */
public class ShowSelectPopup extends PopupWindow {
	private Context mContext;
	ViewHolder viewHolder = null ;
	//判断是否需要添加或更新列表子类项
	private boolean mIsDirty;
	private int cur_pos;

	//弹窗子类项选中时的监听
	private OnItemOnClickListener mItemOnClickListener;

	//定义列表对象
	private ListView mListView;

	//定义弹窗子类项列表
	private ArrayList<Folder> mActionItems = new ArrayList<Folder>();
	private Button bt1;

	public ShowSelectPopup(Context context){
		//设置布局的参数
		this(context, LayoutParams.MATCH_PARENT, 80);
	}


	public ShowSelectPopup(Context context, int width, int height){
		super(context) ;
		this.mContext = context;

		//设置可以获得焦点
		setFocusable(true);
		//设置弹窗内可点击
		setTouchable(true);
		//设置弹窗外可点击
		setOutsideTouchable(true);
		//设置弹窗的宽度和高度
		setWidth(width);
		setHeight(LayoutParams.WRAP_CONTENT);

		//实例化一个ColorDrawable颜色为半透明  
		ColorDrawable dw = new ColorDrawable(0xa0000000);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

		//设置弹窗的布局界面
		setContentView(LayoutInflater.from(mContext).inflate(R.layout.fragment_news_pop, null));

		initUI();
	}

	/**
	 * 初始化弹窗列表
	 */
	private void initUI(){
		mListView = (ListView) getContentView().findViewById(R.id.title_list);
		mListView.setBackgroundColor(mContext.getResources().getColor(R.color.red));

		mListView.setDividerHeight(0);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				//点击子类项后，弹窗消失
				dismiss();
				if (mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index), index);
				cur_pos = index;
			}
		});
	}

	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view){

		//判断是否需要添加或删除列表子类项
		if(mIsDirty){
			populateActions();
		}

		//显示弹窗的位置
		//showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);
		//设置头部view的下拉
		showAsDropDown(view, 0, 0);
		//		setAnimationStyle(R.style.popwin_anim_style);
		setFocusable(true);
		setOutsideTouchable(false);
		update();
	}





	/**
	 * 设置弹窗列表子项
	 */
	private void populateActions(){
		mIsDirty = false;

		//设置列表的适配器
		mListView.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if(convertView == null){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.select_category_item, parent,false) ;
					viewHolder = new ViewHolder() ;
					viewHolder.show_select_tv = (TextView) convertView.findViewById(R.id.show_select_textview) ;
					convertView.setTag(viewHolder) ;

				}else{
					viewHolder = (ViewHolder) convertView.getTag() ;
				}
				Folder item = mActionItems.get(position);
				//设置文本文字
				viewHolder.show_select_tv.setText(item.name+"("+item.images.size()+"Photos )");
				viewHolder.show_select_tv.setTextColor(mContext.getResources().getColor(R.color.white));

//				if (position == cur_pos) {// 如果当前的行就是ListView中选中的一行，就更改显示样式
//					//					convertView.setBackgroundColor(Color.LTGRAY);// 更改整行的背景色
////					viewHolder.show_select_tv.setTextColor(mContext.getResources().getColor(R.color.selectpop_black));
////					//得到配置文件里的颜色
////					TextPaint tp = 	viewHolder.show_select_tv.getPaint();
////					tp.setFakeBoldText(true);
//
//
//				} else{
//					TextPaint tp = 	viewHolder.show_select_tv.getPaint();
//					tp.setFakeBoldText(false);
//				}

				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}

			@Override
			public int getCount() {
				return mActionItems.size();
			}
		}) ;
	}

	/**
	 * 添加子类项
	 */
	public void addAction(Folder action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}

	/**
	 * 清除子类项
	 */
	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}

	/**
	 * 根据位置得到子类项
	 */
	public Folder getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}

	/**
	 * 设置监听事件
	 */
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}

	/**
	 *	功能描述：弹窗子类项按钮监听事件
	 */
	public static interface OnItemOnClickListener{
		public void onItemClick(Folder item, int position);
	}

	private class ViewHolder{
		private TextView show_select_tv;
	}

}
