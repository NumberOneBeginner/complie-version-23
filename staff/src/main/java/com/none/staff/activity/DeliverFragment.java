package com.none.staff.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.libra.sinvoice.SinVoicePlayer;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.staff.R;
import com.none.staff.api.ApiManager;
import com.none.staff.domain.MessageResult;
import com.none.staff.domain.StartGame;
import com.none.staff.event.HomeOrBackEvent;
import com.none.staff.utils.AniUtils;
import com.none.staff.utils.CommonUtils;
import com.none.staff.utils.DialogUtils;
import com.none.staff.utils.NetworkUtils;
import com.none.staff.utils.SPUtil;
import com.none.staff.view.CircleProgressButton;
import com.none.staff.view.CircleProgressButton.OnCompletedListener;
import com.none.staff.view.MyProgressBar;
import com.none.staff.view.SoundWaveBackground;

import de.greenrobot.event.EventBus;


@SuppressLint("ResourceAsColor")
public class DeliverFragment extends Fragment implements  SinVoicePlayer.Listener {
	
	/***Delier界面发动作的回调接口**/
	private DeliverActionInterFace mCallBack ;
	
	/**Boss Deliver设置按钮**/
	private ImageButton deliver_setting ;
	
	/**发送声波的背景***/
	private SoundWaveBackground soundWaveBackground ;
	
	/**环形进度条**/
	private CircleProgressButton  foundDevice;
    
    /**重新开始按钮***/
    private ImageView restart ;

   
    /**分开动画展示的根布局**/
    private LinearLayout deliver_linearlayout ;
    
    /**播放声波中的动画按钮***/
    private ImageView start_sound_ani ;
    
    /**开始播放声波的按钮***/
    private ImageView start_sound_ani_button ;
    
    //发送声波时声音动画
    private AnimationDrawable animationDrawable;
    
    //包含声波背影的布局
    private FrameLayout fl_circle = null ;
    
    //已发送显示发送消息的整体布局
    private RelativeLayout havesent_laisee_rl ;
    
    //已发送声声波警告的根布局
    private LinearLayout end_game_waring_ll ;
    
    //已发送声波显示提示
    private TextView end_game_waringtv ;
    
    //发送声波警告的布局
    private LinearLayout sound_wave_waring_ll ;
    
    /**发送声波动画下面的绿色提示文字***/
    private TextView send_laisee_text ;
    
    /**发送声波动画下面的绿色提示文字下面的黑色字***/
    private TextView below_send_laisee_text ;
    
    /**所有包含警告的布局**/
    private LinearLayout all_waring_ll ;
    
    //警告的提示信息
    private TextView waring_tv ;
    
    //警告图片
    private ImageView waring_icon ;
    
    //已发送红包的数量tv
    private TextView hassent_laisee_tv ;
    
    /**云和太阳的logo图标***/
    private ImageView cloudAndSon_image ;
    
    //boss弹出 send按钮布局
    private RelativeLayout boss_show_send_rl ;
    //把奖发给某人的TextView
    private TextView send_to_name ;
    
    //发奖给某人的取消按钮
    private Button boss_send_cancel_button ;
    //发奖给某人的发送按钮
    private Button boss_send_send_button ;
    //老板--包含发送或取消按钮的布局
    private LinearLayout boss_send_buttons_ll ;
    
    //发送中的进度条
    private MyProgressBar myProgressBar ;
    
    //默认播放声波的时间 10秒
    private int defaultPlayTime = 10 ;
    
    //播放声波一圈以后请求服务器过多少秒钟自动跳回，onclick 界面，默认是10秒
    private int bossrequestToServerTimeOut = 10;
    
    //超时ops
    private LinearLayout alert_ops_timeout_result_ll ;
  
    
    //=======包含声波的代码
    public final static int MSG_SET_RECG_TEXT = 1;
    public final static int MSG_RECG_START = 2;
    public final static int MSG_RECG_END = 3;
    public final static int MSG_PLAY_TEXT = 4;
    
    public final static int[] TOKENS = { 32, 32, 32, 32, 32, 32 };
    public final static String TOKENS_str = "Beeba20141";
    public final static int TOKEN_LEN = TOKENS.length;

    private SinVoicePlayer mSinVoicePlayer;
    
    //==============
	
    //派利的金额 
    private String amountLaisee ;
    //派利的数量
  //  private String noOfLaisee ;
    
    private StartGame startGame ;
    
    private static final int BOSS_SEND_INFO_TO_SERVER = 1 ;
    private static final int BOSS_SEND_TO_SERVER_TIMEOUT = 2 ;
    
    //是否重复请求的标志
    private boolean doContinue = false ;
    
    //这里把activity注入到Fragment中
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        try {
        	mCallBack = (DeliverActionInterFace) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    private Handler handler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case BOSS_SEND_INFO_TO_SERVER:
				bossSendInfoToServer() ;
				break;
			case BOSS_SEND_TO_SERVER_TIMEOUT:
				doContinue = false ;
				handler.removeMessages(BOSS_SEND_INFO_TO_SERVER) ;
				stopAndShowReplaystate() ;
				break ;
			}
    		
    	};
    } ;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	//====声波有关的        
        mSinVoicePlayer = new SinVoicePlayer();
        mSinVoicePlayer.init();
        mSinVoicePlayer.setListener(this);
        //=============
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventBus.getDefault().register(this,"getHomeOrBack",HomeOrBackEvent.class);
		setOnClickListener() ;
		
	}
	//这里需要注意**********
	private void setOnClickListener() {
		
		
		deliver_setting.setOnClickListener(settingOnClickListener) ;
		//开始播放按钮
		start_sound_ani_button.setOnClickListener(playSoundWaveOnClickListener) ;
		//暂停按钮
		start_sound_ani.setOnClickListener(stopPlaySoundOnclickListener) ;
		//重新开始
		restart.setOnClickListener(playSoundWaveOnClickListener) ;
		//进度条转完一圈
		foundDevice.setOnCompletedListener(completedListener) ;
		
		//发送利是
		boss_send_send_button.setOnClickListener(sendOnClickListener) ;
		//取消利是
		boss_send_cancel_button.setOnClickListener(cancleOnCLickListener) ;
	}
	//播放声波
	private void startPlaySound() {
		amountLaisee = SPUtil.getValue(getActivity(), SPUtil.SET_AMOUNT_LAISEE) ;
		
    		if("".equals(amountLaisee)){
    		sound_wave_waring_ll.setVisibility(View.VISIBLE) ;
    		waring_icon.setImageResource(R.drawable.waring_icon) ;
		waring_tv.setTextColor(R.color.waring_red_color) ;
		waring_tv.setText("Please input Amount of each green Laisee") ;
    		
    		DialogUtils.createSettingDialog(getActivity()) ;
    		return ;
    	}
    	
    	if(CommonUtils.isFastClick()){ //防止连续点击
    		return ;
    	}
    	
    	if(NetworkUtils.isNetworkAvailable(getActivity())){ //有网络才可以操作
    		
    		start_sound_ani_button.setClickable(false) ;
    		start_sound_ani.setClickable(false) ;
    		restart.setClickable(false) ;
    	
    		doContinue = false ;
    		//开始派利动作
    		ApiManager.getInstance().start(getActivity(), 1+"", amountLaisee, new AsyncHttpResponseHandler() {
    			
    			@Override
    			public void onStart() {
    				super.onStart();
    			}
    			
				@SuppressLint("ResourceAsColor")
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					start_sound_ani_button.setClickable(true) ;
					start_sound_ani.setClickable(true) ;
					restart.setClickable(true) ;
					//restart.setClickable(true) ;
					String result = new String(arg2) ;
					MessageResult messageResult = MessageResult.prase(result);
					if(0==messageResult.getCode()){
						try {
							
							deliver_setting.setVisibility(View.INVISIBLE) ;
		            		sound_wave_waring_ll.setVisibility(View.GONE) ;
							
							JSONObject jsonObject = new JSONObject(messageResult.getData()).optJSONObject("game") ;
							//String gameId = jsonObject.optString("gameId") ;
							startGame = StartGame.parse(jsonObject.toString()) ;
							
							if(startGame.getNotEnough().equals("true")){  //余额不足
								
								sound_wave_waring_ll.setVisibility(View.VISIBLE) ;
								waring_icon.setImageResource(R.drawable.waring_icon) ;
								waring_tv.setTextColor(R.color.waring_red_color) ;
								waring_tv.setText("You do not have enough balance") ;
								Log.e("余额不足", "余额不足") ;
								return ;
							}else{
								sound_wave_waring_ll.setVisibility(View.GONE) ;
							}
							
							if("true".equals(startGame.getFlag_hkd())){  //港币剩余100
								sound_wave_waring_ll.setVisibility(View.VISIBLE) ;
								waring_icon.setImageResource(R.drawable.yellow_waring_icon) ;
								waring_tv.setTextColor(R.color.cricle_color) ;
								waring_tv.setText("Your balance is less than HKD100") ;
							}else{
								sound_wave_waring_ll.setVisibility(View.GONE) ;
							}
							if("true".equals(startGame.getFlag_rmb())){ //人发币剩余100
								sound_wave_waring_ll.setVisibility(View.VISIBLE) ;
								waring_icon.setImageResource(R.drawable.yellow_waring_icon) ;
								waring_tv.setTextColor(R.color.cricle_color) ;
								waring_tv.setText("Your balance is less than RMB100") ;
							}else{
								sound_wave_waring_ll.setVisibility(View.GONE) ;
							}
							
							
							defaultPlayTime = Integer.parseInt(startGame.getSoundPlay()) ;
							//暂时注释掉，为了调试
							foundDevice.setmPlayTime(defaultPlayTime) ;
							bossrequestToServerTimeOut = Integer.parseInt(startGame.getBossRequest()) ;
							
							
							Log.e("播放", messageResult.getData()+"===gameId==="+startGame.getGameId()) ;
							mCallBack.startDeliver() ;
		            			soundWaveBackground.startRippleAnimation();
		                      foundDevice.startCartoom() ;
		                      restart.setVisibility(View.INVISIBLE) ;
		                      start_sound_ani_button.setVisibility(View.GONE) ; //播放按钮
		                      
		                      start_sound_ani.setVisibility(View.VISIBLE) ; //播放动画按钮
		                      animationDrawable.start();
		                      
		                      send_laisee_text.setText("Waiting for connection") ;
		                      below_send_laisee_text.setText("Tap to stop") ;
		                      
		                     
		                    
		                      //如果播放声波或 进度条在转，或声波动画在走 才请求服务器
		                      if(soundWaveBackground.isRippleAnimationRunning() || start_sound_ani.getVisibility() == View.VISIBLE || foundDevice.isBCartoom()){
		                    	  doContinue = true ;
		                    	  bossSendInfoToServer();
		                      }
		                      
		                    //=\==========  声波有关的
		                      try {
		                          byte[] strs =startGame.getGameId().getBytes("UTF8");
		                          if ( null != strs ) {
		                              int len = strs.length;
		                              int []tokens = new int[len];
		                              for ( int i = 0; i < len; ++i ) {
		                                  tokens[i] = strs[i];
//		                                  tokens[i] = Common.DEFAULT_CODE_BOOK.indexOf(TOKENS_str.charAt(i));
		                              }
		                              mSinVoicePlayer.play(tokens, len, true, 2000);
		                          } else {
		                              mSinVoicePlayer.play(TOKENS, TOKEN_LEN, true, 2000);
		                          }
		                          
		                      } catch (UnsupportedEncodingException e) {
		                          e.printStackTrace();
		                      }
		                      //===========
						} catch (JSONException e) {
							e.printStackTrace() ;
						}
					}else{
						Toast.makeText(getActivity(), messageResult.getMsg(), 0).show() ;
						//foundDevice.setClickable(true) ;
						doContinue = true ;
					
					}
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					 doContinue = true ;
//					 foundDevice.setClickable(true) ;
					 start_sound_ani_button.setClickable(true) ;
					 start_sound_ani.setClickable(true) ;
					 restart.setClickable(true) ;
				}
				
				@Override
				public void onFinish() {
					super.onFinish();
				}
			}) ;
              
    }else{  //没有网络
    	
    }
	}
	//开始播放声波事件
	private View.OnClickListener playSoundWaveOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startPlaySound() ;
		}
	};
	//进度条转完一圈的事件
	private OnCompletedListener completedListener  = new OnCompletedListener() {
		
		@Override
		public void OnCompleted(CircleProgressButton progressButton,
				boolean finishflag) {
			if (soundWaveBackground.isRippleAnimationRunning()&& soundWaveBackground != null) {
				
				soundWaveBackground.stopRippleAnimation();
				mSinVoicePlayer.stop() ;
				
				//设置超时时间
				handler.sendMessageDelayed(handler.obtainMessage(BOSS_SEND_TO_SERVER_TIMEOUT), bossrequestToServerTimeOut*1000) ;
			}
		}
	};
	//停止播放
	private View.OnClickListener stopPlaySoundOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(NetworkUtils.isNetworkAvailable(getActivity())){ //有网络才可以操作
				doContinue = false ; 
				start_sound_ani.setClickable(false) ;
				if(soundWaveBackground.isRippleAnimationRunning() || start_sound_ani.getVisibility() == View.VISIBLE || foundDevice.isBCartoom()){
					 //=\==========  声波有关的
                     mSinVoicePlayer.stop();
                     //========
             		soundWaveBackground.stopRippleAnimation() ;
                     foundDevice.stopCartoom() ;
                     // restart.setVisibility(View.VISIBLE) ;
                     start_sound_ani_button.setVisibility(View.VISIBLE) ;
                
                     start_sound_ani.setVisibility(View.GONE) ; //播放动画按钮
                     stopAniButton(); //停止播放声波按钮动画
                     stopPlaySound(true) ;
				 }
			}
		}
	};
	
	//发送利是
	private View.OnClickListener sendOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
	
			if (!doContinue)
				return ;
	
			ApiManager.getInstance().sendOrCancel(getActivity(), "send",startGame.getGameId(), new AsyncHttpResponseHandler() {
				
				@Override
    			public void onStart() {
    				super.onStart();
    				boss_send_buttons_ll.setVisibility(View.INVISIBLE) ;
    				myProgressBar.show() ;
    			}
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					
					if (!doContinue)
						return ;
					
					String jsonString = new String(arg2) ;
					MessageResult messageResult = MessageResult.prase(jsonString) ;
					
Log.d("sendorcancel", jsonString) ;
					if(0==messageResult.getCode()){
						//多少利派出去的布局
						havesent_laisee_rl.setVisibility(View.VISIBLE) ;
						try {
							JSONObject jsonObject = new JSONObject(messageResult.getData()) ;  
							if("true".equals(jsonObject.optString("sendOrCancel"))){
								//有send布局
								boss_show_send_rl.setVisibility(View.GONE) ;
								hassent_laisee_tv.setText("Laisees sent!")  ;

								Log.e("老子发送是true", "true") ;
								if("true".equals(startGame.getFlag_hkd())){
									end_game_waring_ll.setVisibility(View.VISIBLE) ;
									end_game_waringtv.setText("You have onle less than 100 HKD left.") ;
								}else if("true".equals(startGame.getFlag_rmb())){
									end_game_waring_ll.setVisibility(View.VISIBLE) ;
									end_game_waringtv.setText("You have onle less than 100 RMB left.") ;
								}else{
									end_game_waring_ll.setVisibility(View.GONE) ;
								}
								disappearDeviverAni() ;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{
						

						
					}
					myProgressBar.dismiss();
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					Log.e("arg3", arg3.toString()) ;
					myProgressBar.dismiss() ;
					alert_ops_timeout_result_ll.setVisibility(View.VISIBLE) ;
					disappearDeviverAni() ;
					if (!doContinue)
						return ;
				}

				@Override
				public void onFinish() {
					super.onFinish();
					myProgressBar.dismiss() ;
					Log.e("bossSendClickFinish", "bossSendClickFinish") ;
				}
				
			}) ;
		}
	};
	//取消发送利是
	private View.OnClickListener cancleOnCLickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			if (!doContinue)
				return ;
			ApiManager.getInstance().sendOrCancel(getActivity(), "cancel",startGame.getGameId(), new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if (!doContinue)
						return ;
					
					String jsonString = new String(arg2) ;
					MessageResult messageResult = MessageResult.prase(jsonString) ;
					if(0== messageResult.getCode()){
						
						try {
							JSONObject jsonObject = new JSONObject(messageResult.getData()) ;
							if("false".equals(jsonObject.optString("sendOrCancel"))){
								stopPlaySound(false) ;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{
						
					}
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					if (!doContinue)
						return ;
				}
			}) ;
		}
	};
	
	//boss请求server是否中奖
	private void bossSendInfoToServer() {
		
		start_sound_ani.setSelected(true) ;
		
		
		if (!doContinue)
			return ;
		
		if(null !=getActivity()){
		ApiManager.getInstance().sendInfoToBoss(getActivity(), new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(int arg0, Header[] arg1,byte[] arg2, Throwable arg3) {
				
				if (!doContinue)
					return ;
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1,byte[] arg2) {
				
				if (!doContinue)
					return ;
				
				String result = new String(arg2) ;
				MessageResult messageResult = MessageResult.prase(result);
				Log.e("结果：", messageResult.getData()) ;
				if(0==messageResult.getCode()){
					try {
						JSONObject jsonObject = new JSONObject(messageResult.getData());
						if(!TextUtils.isEmpty(jsonObject.optString("userId"))){
							//展开动画
							showDeliverAin() ;
							handler.removeMessages(BOSS_SEND_TO_SERVER_TIMEOUT) ; //中奖了就把转完一圈的超时去掉
							boss_show_send_rl.setVisibility(View.VISIBLE) ;
							if (getActivity() != null) {
								String userName = jsonObject.optString("userName") ;
								send_to_name.setText(userName) ;
							}
						}else{

							handler.removeMessages(BOSS_SEND_INFO_TO_SERVER) ;
							handler.sendMessageDelayed(handler.obtainMessage(BOSS_SEND_INFO_TO_SERVER), 1000) ;
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					
				}
			}
		}) ;
		}
	}
	

	private void stopPlaySound(boolean flag){  //为true就是暂停的时候调用的，为false就是boss点击cancle调用的。
		
		handler.removeMessages(BOSS_SEND_TO_SERVER_TIMEOUT) ;
		
		if(flag){
			deliver_linearlayout.removeAllViews() ;
			fl_circle.setVisibility(View.VISIBLE) ;
			mCallBack.endDeliver() ;
			deliver_setting.setVisibility(View.VISIBLE) ;
			mCallBack.showHome() ;
			mCallBack.changeSoundWaveBg(false) ;
			havesent_laisee_rl.setVisibility(View.GONE) ;
			all_waring_ll.setVisibility(View.VISIBLE) ; //包含所有警告的布局
			send_laisee_text.setText("Click now") ;
			below_send_laisee_text.setText("to give laisee") ;
			cloudAndSon_image.setVisibility(View.VISIBLE) ; //云和太阳标志的图片
			
		}else{
			deliver_linearlayout.removeAllViews() ;
			fl_circle.setVisibility(View.VISIBLE) ;
			mCallBack.endDeliver() ;
			deliver_setting.setVisibility(View.VISIBLE) ;
			mCallBack.showHome() ;
			mCallBack.changeSoundWaveBg(false) ;
			havesent_laisee_rl.setVisibility(View.GONE) ;
			all_waring_ll.setVisibility(View.VISIBLE) ; //包含所有警告的布局
			send_laisee_text.setText("Click now") ;
			below_send_laisee_text.setText("to give laisee") ;
			cloudAndSon_image.setVisibility(View.VISIBLE) ; //云和太阳标志的图片
			

			
			
			boss_send_buttons_ll.setVisibility(View.VISIBLE) ;
			myProgressBar.setVisibility(View.INVISIBLE) ;
			boss_show_send_rl.setVisibility(View.GONE) ;
			start_sound_ani_button.setVisibility(View.VISIBLE) ;
            start_sound_ani.setVisibility(View.GONE) ; //播放动画按钮
		}
		
	}
	
	
	private void stopAndShowReplaystate(){
		deliver_linearlayout.removeAllViews() ;
		fl_circle.setVisibility(View.VISIBLE) ;
		mCallBack.endDeliver() ;
		deliver_setting.setVisibility(View.VISIBLE) ;
		mCallBack.showHome() ;
		mCallBack.changeSoundWaveBg(false) ;
		havesent_laisee_rl.setVisibility(View.GONE) ;
		all_waring_ll.setVisibility(View.VISIBLE) ; //包含所有警告的布局
		
		start_sound_ani.setVisibility(View.GONE) ; //播放动画按钮
		  //===========和 disappearDeviverAni 中变化的部分
		start_sound_ani_button.setVisibility(View.GONE) ;
		restart.setVisibility(View.VISIBLE) ;
		  //============
		  
		send_laisee_text.setText("Retry again") ;
		below_send_laisee_text.setText("to give laisee") ;
		cloudAndSon_image.setVisibility(View.VISIBLE) ; //云和太阳标志的图片
		
		boss_send_buttons_ll.setVisibility(View.VISIBLE) ;
		myProgressBar.setVisibility(View.INVISIBLE) ;
		boss_show_send_rl.setVisibility(View.GONE) ;
		
		
		

	}
	
	//消失展开动画并还原各个组件状态
	private void disappearDeviverAni() {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				deliver_linearlayout.removeAllViews() ;
				fl_circle.setVisibility(View.VISIBLE) ;
				mCallBack.endDeliver() ;
				deliver_setting.setVisibility(View.VISIBLE) ;
				mCallBack.showHome() ;
				mCallBack.changeSoundWaveBg(false) ;
				havesent_laisee_rl.setVisibility(View.GONE) ;
				all_waring_ll.setVisibility(View.VISIBLE) ; //包含所有警告的布局
				
				start_sound_ani_button.setVisibility(View.VISIBLE) ;//开始播放动画按钮
				start_sound_ani.setVisibility(View.GONE) ; //播放动画按钮
				  
				send_laisee_text.setText("Click now") ;
				below_send_laisee_text.setText("to give laisee") ;
				cloudAndSon_image.setVisibility(View.VISIBLE) ; //云和太阳标志的图片
				
				boss_send_buttons_ll.setVisibility(View.VISIBLE) ;//发送和cancel按钮布局
				myProgressBar.setVisibility(View.INVISIBLE) ;
				boss_show_send_rl.setVisibility(View.GONE) ;
				alert_ops_timeout_result_ll.setVisibility(View.GONE) ; //发送时超时布局
			}
		}, 3000) ;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.deliver_fragment, container,false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		havesent_laisee_rl = (RelativeLayout) view.findViewById(R.id.havesent_laisee_rl) ;
		end_game_waringtv = (TextView) view.findViewById(R.id.end_game_waringtv) ;
		deliver_setting = (ImageButton) view.findViewById(R.id.deliver_setting) ;
		restart = (ImageView) view.findViewById(R.id.restart) ;
		start_sound_ani = (ImageView) view.findViewById(R.id.start_sound_ani) ;
		start_sound_ani.setBackgroundResource(R.anim.animation_list);
		animationDrawable = (AnimationDrawable) start_sound_ani.getBackground() ;
		
		
		start_sound_ani_button = (ImageView) view.findViewById(R.id.start_sound_ani_button) ;
		soundWaveBackground =(SoundWaveBackground) view.findViewById(R.id.content);
		foundDevice=(CircleProgressButton)view.findViewById(R.id.foundDevice);
		deliver_linearlayout = (LinearLayout) view.findViewById(R.id.deliver_linearlayout) ;
		fl_circle = (FrameLayout) view.findViewById(R.id.fl_circle) ;
				
		foundDevice.setmPlayTime(defaultPlayTime) ;
		
		sound_wave_waring_ll = (LinearLayout) view.findViewById(R.id.sound_wave_waring_ll) ;
		waring_icon = (ImageView) view.findViewById(R.id.waring_icon) ;
		waring_tv = (TextView) view.findViewById(R.id.waring_tv) ;
		send_laisee_text = (TextView) view.findViewById(R.id.send_laisee_text) ;
		below_send_laisee_text = (TextView) view.findViewById(R.id.below_send_laisee_text) ;
		
		all_waring_ll = (LinearLayout) view.findViewById(R.id.all_waring_ll) ;
		
		cloudAndSon_image = (ImageView) view.findViewById(R.id.cloudAndSon_image) ;
		
		hassent_laisee_tv = (TextView) view.findViewById(R.id.hassent_laisee_tv) ;
		end_game_waring_ll = (LinearLayout) view.findViewById(R.id.end_game_waring_ll) ;
		
		boss_show_send_rl = (RelativeLayout) view.findViewById(R.id.boss_show_send_rl) ;
		send_to_name = (TextView) view.findViewById(R.id.send_to_name) ;
		
		boss_send_buttons_ll = (LinearLayout) view.findViewById(R.id.boss_send_buttons_ll) ;
		
		boss_send_cancel_button = (Button) view.findViewById(R.id.boss_send_cancel_button) ;
		boss_send_send_button = (Button) view.findViewById(R.id.boss_send_send_button) ;
		
		myProgressBar = (MyProgressBar) view.findViewById(R.id.boss_send_laisee_progressbar) ;
		alert_ops_timeout_result_ll = (LinearLayout) view.findViewById(R.id.alert_ops_timeout_result_ll) ;
		
	}
	
	private View.OnClickListener settingOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DialogUtils.createSettingDialog(getActivity()) ;
		}
	};
	
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onSinVoicePlayStart() {}

	@Override
	public void onSinVoicePlayEnd() {}

	@Override
	public void onSinToken(int[] tokens) {}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){  //true代隐藏了，不可见
		//	Log.e("DeliverFragment:", hidden+"") ;
		}else{ //false代表显示了，可见
		//	Log.e("DeliverFragment:", hidden+"") ;
		}
		
	}
	
	/**停止播放声波按钮动画**/
	private void stopAniButton(){
		if(animationDrawable!=null && animationDrawable.isRunning()){
			animationDrawable.stop() ;
		}
	}
	
	
	public void getHomeOrBack(HomeOrBackEvent homeOrBack){
		Log.e("9999", "执行了"+homeOrBack) ;
		if("back".equals(homeOrBack.getFlag())){
			   if(getActivity()!=null){
				   if(soundWaveBackground.isRippleAnimationRunning()){
					mSinVoicePlayer.stop() ;
					
				//	stopAndDeliverAni() ;//停止并展开动画
					doContinue = false ;
					soundWaveBackground.stopRippleAnimation() ;
                    foundDevice.stopCartoom() ;
                  //  restart.setVisibility(View.VISIBLE) ;
                    
                    start_sound_ani_button.setVisibility(View.VISIBLE) ;
                    
                    start_sound_ani.setVisibility(View.GONE) ; //播放动画按钮
                    stopAniButton(); //停止播放声波按钮动画
					stopPlaySound(true) ;
				}else{
					getActivity().finish() ;
				}
			   }
		}else{
			
		}
	}

	//展开动画
	public void showDeliverAin(){
		    all_waring_ll.setVisibility(View.INVISIBLE) ; //所有包含警告的布局
		    cloudAndSon_image.setVisibility(View.INVISIBLE) ;//云和太阳标志的图片
			Bitmap bitmap = CommonUtils.takeScreenShot(getActivity()) ;
			
			//上下平分图片的方法
			Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (bitmap.getHeight() / 2));
			Bitmap bm2 = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2), bitmap.getWidth(), (bitmap.getHeight() / 2));
			Bitmap[] bitmaps= new Bitmap[2] ;
			bitmaps[0] = bm1 ;
			bitmaps[1] = bm2 ;
			 
			deliver_linearlayout.removeAllViews() ;
			ArrayList<ImageView> images = new ArrayList<ImageView>() ;
			images.clear() ;
			for (int i = 0; i < 2; i++) {
				View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.divide_image, deliver_linearlayout,false) ;
				ImageView iv= (ImageView) view1.findViewById(R.id.image2) ;
				iv.setImageBitmap(bitmaps[i]) ;
				deliver_linearlayout.addView(iv) ;
				images.add(iv) ;
			}
			
			mCallBack.hideHome() ;
			mCallBack.changeSoundWaveBg(true) ;
			AniUtils.startAnim(images);
			bitmaps = null ;
			bm1= null ;
			bm2=null ;
			bitmap = null ;
			
			soundWaveBackground.stopRippleAnimation() ;
            foundDevice.stopCartoom() ;
         
           //=\==========  声波有关的
            mSinVoicePlayer.stop();
			fl_circle.setVisibility(View.GONE) ;
			stopAniButton() ;//停止播放声波按钮动画
			mSinVoicePlayer.stop() ;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAniButton() ;//停止播放声波按钮动画
		mSinVoicePlayer.stop() ;
		if(soundWaveBackground.isRippleAnimationRunning() && soundWaveBackground !=null){
			soundWaveBackground.stopRippleAnimation() ;
	        foundDevice.stopCartoom() ;
		}

		doContinue = false ;
		Log.e("onclick home", "onclick home") ;
	}
	
}
