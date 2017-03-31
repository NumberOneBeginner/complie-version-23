package com.none.staff.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hsbc.greenpacket.util.AlgorithmUtil;
import com.hsbc.greenpacket.util.StringUtil;
import com.hsbc.greenpacket.util.actions.HookConstants;

import com.none.staff.R;
import com.none.staff.event.HomeOrBackEvent;
import com.none.staff.utils.CommonUtils;

import de.greenrobot.event.EventBus;

public class BossSoundwaveActivity extends FragmentActivity implements DeliverActionInterFace{
	
	private static final String TAG = "BossSoundwaveActivity";

	/**顶部tab按钮集合**/
	private ImageButton[] mTabs;
	
	private ImageButton tab_deliver ;
	
	private ImageButton tab_collect ;
	
	private Button home ;
	
	private DeliverFragment deliverFragment ;
	private CollectFragment collectFragment ;
	
	private FrameLayout tab_diver_framelayout ;  //包含云布局 发送
	private FrameLayout tab_collect_framelayout ; //包含云布局 接收
	
	private TextView tab_collect_tv,tab_deliver_tv ;
	
	private RelativeLayout boss_soundwave_root ;

	private String location ;
	
	static {
		System.loadLibrary("sinvoice");
		Log.d(TAG, "sinvoice jnicall loadlibrary");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //保存屏幕不暗
		super.setContentView(R.layout.activity_boss_soundwave) ;

        StaffApplication application = (StaffApplication) getApplication();
        application.getActivityStack().put(String.valueOf(this.hashCode()), this);
		initView() ;
		initTab() ;
		setOnclickListener() ;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		/**
		 * @author Bille Y BAI [13 Feb 2015]
		 * @desc override this method to fix the defect which occur text overlaps in an occasional scene
		 */
//		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy(){
	    super.onDestroy();
        ((StaffApplication)this.getApplication()).getActivityStack().remove(String.valueOf(this.hashCode()));
	}

	private void initView() {
		
		location = getIntent().getStringExtra("location") ;
		tab_diver_framelayout = (FrameLayout) this.findViewById(R.id.tab_diver_framelayout) ;
		tab_collect_framelayout = (FrameLayout) this.findViewById(R.id.tab_collect_framelayout) ;
		
		tab_deliver_tv = (TextView) this.findViewById(R.id.tab_deliver_tv) ;
		tab_collect_tv = (TextView) this.findViewById(R.id.tab_collect_tv) ;
		
		tab_deliver = (ImageButton) this.findViewById(R.id.tab_deliver) ;
		tab_collect = (ImageButton) this.findViewById(R.id.tab_collect) ;
		home = (Button) this.findViewById(R.id.home) ;
		mTabs = new ImageButton[2] ;
		mTabs[0] = tab_deliver ;
		mTabs[1] = tab_collect ;
		
		
		//自已本地测试的时候写死，到时候给客户的时候把下面的注释打开，由于首页用html界面去做了。
		//String getDeliverState = SPUtil.getValue(BossSoundwaveActivity.this, SPUtil.DELIVER_STATE) ;
		
		try {
			String getDeliverState = getDeliverState();
			
			if("true".equals(getDeliverState)){
				//mTabs[0].bringToFront() ;
				tab_diver_framelayout.bringToFront() ;
				mTabs[0].setSelected(true) ;
				tab_deliver_tv.setSelected(true) ;
			}else{
				//mTabs[1].bringToFront() ;
				tab_collect_framelayout.bringToFront() ;
				mTabs[1].setSelected(true) ;
				tab_collect_tv.setSelected(true) ;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boss_soundwave_root = (RelativeLayout) this.findViewById(R.id.boss_soundwave_root) ;
	}
	private void initTab() {
		deliverFragment = new DeliverFragment() ;
		collectFragment = new CollectFragment() ;
		
		try {
			//自已本地测试的时候写死，到时候给客户的时候把下面的注释打开，由于首页用html界面去做了。
			//String getDeliverState = SPUtil.getValue(BossSoundwaveActivity.this, SPUtil.DELIVER_STATE) ;
			String getDeliverState = getDeliverState();
			
			if("true".equals(getDeliverState)){
				// 添加显示第一个fragment
				getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, deliverFragment).show(deliverFragment).commit();
			}else{
				// 添加显示第一个fragment
				getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, collectFragment).show(collectFragment).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

/*getDeliverState**/
	private String getDeliverState() throws Exception {
		String deliverState =  CommonUtils.getStoredValue("deliver",BossSoundwaveActivity.this) ;
		  if(!StringUtil.IsNullOrEmpty(deliverState)){
		      String aesKey=AlgorithmUtil.gen256Key(HookConstants.SECURE_KEY);
		      deliverState=new String(AlgorithmUtil.aesDecrypt(aesKey,Base64.decode(deliverState, Base64.NO_WRAP)));
		  }
		return deliverState;
	}
	

	private void setOnclickListener() {
		home.setOnClickListener(goHomeClickListener) ;
	}
	/**
	 * button点击事件
	 * @param view
	 */
	public void onTabSelect(View view) {
		
		
		switch (view.getId()) {
		
		case R.id.tab_deliver:
			showCurrentFragment(deliverFragment,collectFragment);
			//把当前tab设为选中状态
			//mTabs[0].bringToFront() ;
			tab_diver_framelayout.bringToFront() ;
			mTabs[0].setSelected(true);
			mTabs[1].setSelected(false);
			tab_deliver_tv.setSelected(true) ;
			tab_collect_tv.setSelected(false) ;
			
			break;
		case R.id.tab_collect:
			showCurrentFragment(collectFragment,deliverFragment);
			//把当前tab设为选中状态
			
			//mTabs[1].bringToFront() ;
			tab_collect_framelayout.bringToFront() ;
			mTabs[0].setSelected(false);
			mTabs[1].setSelected(true);
			tab_deliver_tv.setSelected(false) ;
			tab_collect_tv.setSelected(true) ;
			break;
		}

	}



	/****
	 * 显示和隐藏Fragment
	 * @param showFragment
	 * @param hideFragment
	 */
	private void showCurrentFragment(Fragment showFragment,Fragment hideFragment) {
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		trx.hide(hideFragment);
		if (!showFragment.isAdded()) {
			trx.add(R.id.fragment_container, showFragment);
		}
		trx.show(showFragment).commit();
	
	}
	
	private View.OnClickListener goHomeClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish() ;
		}
	};

	
	@Override
	public void startDeliver() {
		tab_diver_framelayout.setVisibility(View.INVISIBLE) ;
		tab_collect_framelayout.setVisibility(View.INVISIBLE) ;
	}




	@Override
	public void endDeliver() {
		tab_diver_framelayout.setVisibility(View.VISIBLE) ;
		tab_collect_framelayout.setVisibility(View.VISIBLE) ;
	}


	@Override
	public void hideHome() {
		home.setVisibility(View.INVISIBLE) ;
	}


	@Override
	public void showHome() {
		home.setVisibility(View.VISIBLE) ;
	}

	@Override
	public void changeSoundWaveBg(boolean flag) {
		if(flag){
			boss_soundwave_root.setBackgroundResource(R.color.white_color) ;
		}else{
			boss_soundwave_root.setBackgroundResource(R.drawable.green_laisee_bg) ;
		}
	}
	
	/**返回建的时候给DeliverFragment发送事件**/
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		EventBus.getDefault().post(new HomeOrBackEvent("back")) ;
	}

//把结果 给 设置回去，结果在 CollectFragment 请求成功中利是的时候 设置的。
	@Override
	public void setResult(String result) {
		Intent intent = new Intent();
		intent.putExtra(HookConstants.SOUND_WAVE_RESULT, result);
		intent.putExtra(HookConstants.FUNCTION, HookConstants.SOUNDWAVE_TO_NATIVE);
		setResult(Activity.RESULT_OK, intent) ;
		finish() ;
	}
}
