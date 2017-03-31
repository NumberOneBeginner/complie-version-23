package com.none.staff.view;



import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class SwitchView extends ViewGroup {
	public final static int STATE_LEFT_ON_TOP = 101;
	public final static int STATE_RIGHT_ON_TOP = 102;
	private int currentState = STATE_LEFT_ON_TOP;
	private View childLeft;
	private View childRight;

	private int leftChildW;
	private int leftChildH;
	private int rightChildW;
	private int rightChildH;
	private OnStateChangeListener mStateChangeListener;// 回调状态变化

	public interface OnStateChangeListener {
		void onStateChange(int state);
	}

	public SwitchView(Context context) {
		this(context, null);
	}

	public SwitchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);// 开启有序绘制child
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 初始化
			childLeft = getChildAt(0);
			childRight = getChildAt(1);
			childLeft.setOnClickListener(leftClickListener);
			childRight.setOnClickListener(rightClickListener);
			measureChild(childLeft, widthMeasureSpec, heightMeasureSpec);
			measureChild(childRight, widthMeasureSpec, heightMeasureSpec);
			// 记录childLeft和childRight的宽高
			leftChildW = childLeft.getMeasuredWidth();
			leftChildH = childLeft.getMeasuredHeight();
			rightChildW = childRight.getMeasuredWidth();
			rightChildH = childRight.getMeasuredHeight();

		// 宽度为一个child
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(leftChildW, MeasureSpec.EXACTLY);
		// 高度为两个child的和
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(leftChildH+rightChildH,MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}


	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		switch (currentState) {
		case STATE_LEFT_ON_TOP:
			// childLeft在上，需要先draw childRight再draw childLeft
			// 后draw的会覆盖先draw的，这样childLeft才会在上层
			if (i == 0) {
				return 1;
			} else {
				return 0;
			}
		default:
			return i;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		// int praentLeft = getLeft();
		// int praentTop = getTop();
		// int praentRight = getRight();
		// int praentBottom = getBottom();
		// Log.e("l-t-r-b", praentLeft + "-" + praentTop + "-" + praentRight +
		// "-" + praentBottom);
		switch (currentState) {
		// 两个child的移动是动画，并不需要在这里进行特别处理，注意动画的参数就行了
		case STATE_LEFT_ON_TOP:
		case STATE_RIGHT_ON_TOP:
			childLeft.layout(0, 0, leftChildW, leftChildH);
			childRight.layout(0, (int) (leftChildH*0.75), rightChildW , (int) (rightChildH+leftChildH*0.75));
			break;
		}
		// 这里是初始时的状态判断并初始化显示
			if (currentState == STATE_LEFT_ON_TOP) {

				childLeft.setSelected(true) ;
				childRight.setSelected(false) ;
			} else {
				// 把childLeft变小并且两个都向右移
				childLeft.setSelected(false) ;
				childRight.setSelected(true) ;
			}
	}

	// 本来想设置点击底层的child才会切换状态
	// 但看着自己粗大的手指，还是点击两个child都能切换吧
	OnClickListener leftClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			changeState("left");
		}
	};
	OnClickListener rightClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			changeState("right");
		}
	};

	public void changeState(String flag ) {
		
		requestLayout();// 改变了绘制顺序
		
		if("left".equals(flag)){
			currentState = STATE_LEFT_ON_TOP;
			if (mStateChangeListener != null) {
				mStateChangeListener.onStateChange(currentState);
			}
			childLeft.setSelected(false) ;
			childRight.setSelected(true) ;
		}else{
			currentState = STATE_RIGHT_ON_TOP;
			if (mStateChangeListener != null) {
				mStateChangeListener.onStateChange(currentState);
			}
			childLeft.setSelected(true) ;
			childRight.setSelected(false) ;
		}
//			if (currentState == STATE_RIGHT_ON_TOP) {
//				
//			} else {
//				
//			}
	}

	/**
	 * 获取当前状态
	 * 
	 * @return SwitchView.STATE_LEFT_IN_TOP or SwitchView.STATE_RIGHT_IN_TOP
	 */
	public int getState() {
		return currentState;
	}

	/**
	 * 设置当前状态，设置初始状态请用setState
	 * 
	 * @param state
	 *            SwitchView.STATE_LEFT_IN_TOP or SwitchView.STATE_RIGHT_IN_TOP
	 */
	public void setState(int state) {
		if (state != currentState) {
			changeState("left");
		}
	}

	/**
	 * 设置初始状态，不要用setState来设置初始状态
	 * 
	 * @param state
	 *            SwitchView.STATE_LEFT_IN_TOP or SwitchView.STATE_RIGHT_IN_TOP
	 */
	public void setInitState(int state) {
		currentState = state;
	}

	/**
	 * 设置状态变化时的回调
	 * 
	 * @param l
	 */
	public void setOnStateChangeListener(OnStateChangeListener l) {
		mStateChangeListener = l;
	}
}
