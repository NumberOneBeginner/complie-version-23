package com.none.staff.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.activities.HSBCActivity;
import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.process.ProcessUtil;
import com.hsbc.greenpacket.util.NameValueStore;

import com.none.staff.R;
import com.none.staff.util.DownloadUtil;

public class OnboardingActivity extends HSBCActivity implements OnClickListener,OnPageChangeListener{

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private ImageView[] dots;
	private int currentIndex;
	
	private static final String[] pics = {"nativeapp_onborading1","nativeapp_onborading2","nativeapp_onborading3","nativeapp_onborading4"} ;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onboarding_page);
		vp = (ViewPager)findViewById(R.id.onboarding_viewpager);
		vpAdapter = new ViewPagerAdapter(OnboardingActivity.this, pics);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
		OnClickListener lastPageClickListener = new OnClickListener(){
            @Override
            public void onClick(View v) {
                //if the last image
                gotoStandAloneGreenLaisee();
            }
		};
		vpAdapter.setLastPageClickListener(lastPageClickListener);
		initDots();
	}
	
	private void initDots(){
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
		dots = new ImageView[pics.length];
		for(int i = 0; i <pics.length;i++){
			dots[i] = (ImageView)ll.getChildAt(i);
			dots[i].setEnabled(true);
			dots[i].setOnClickListener(this);
			dots[i].setId(i);
		}
		currentIndex = 0;
		dots[currentIndex].setEnabled(false);
	}
	
	private  void setCurView(int position){
		if(position < 0||position >= pics.length){
			return;
		}
		vp.setCurrentItem(position);
	}
	
	private void setCurDot(int position){
		if(position < 0 || position>pics.length-1 || currentIndex == position){
			return ;
		}
		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = position;
	}

	private void gotoStandAloneGreenLaisee(){
	    boolean isStandAloneGreenLaiseeApp = this.getResources().getBoolean(R.bool.standalone_mode);
	    NameValueStore store = new NameValueStore(this);
	    store.setAttribute(Constants.ONBOARDING_PAGE_SHOWED, "TRUE");

        Intent intent = new Intent(this,MainBrowserActivity.class);
        Bundle bundle = new Bundle();
        
        if(isStandAloneGreenLaiseeApp){
            String url = "file:///web/prelogon/logon/logon.html";
            url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(this));
            bundle.putString(Constants.INTENT_URL_KEY, url);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else{
            String url = "file:///web/postlogon/menu/menu.html";
            url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(this));
            bundle.putString(Constants.INTENT_URL_KEY, url);
            Bundle bundle2 = getIntent().getExtras();
            if(bundle2 !=null ){
                String scaInfo=bundle2.getString(Constants.SCA_INFO);
                bundle.putString(Constants.SCA_INFO, scaInfo);
            }
            intent.putExtras(bundle);
            startActivityForResult(intent, staff.GO_TO_SCA_FUNCTION);
        }
        this.slideRightToLeft();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == staff.GO_TO_SCA_FUNCTION && data != null) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    } 

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurDot(arg0);
		
	}

	@Override
	public void onClick(View arg0) {
		int position = (Integer)arg0.getId();
		setCurView(position);
		setCurDot(position);
		
	}

}
