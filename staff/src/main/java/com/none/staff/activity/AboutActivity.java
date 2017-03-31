package com.none.staff.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.none.staff.R;

public class AboutActivity extends BaseActivity {
	
	private Button home ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_about) ;
		
		StaffApplication application = (StaffApplication) getApplication();
		application.getActivityStack().put(String.valueOf(this.hashCode()), this);

		
		home = (Button) this.findViewById(R.id.home_btn) ;
		
		home.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish() ;
			}
		}) ;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		((StaffApplication)this.getApplication()).getActivityStack().remove(String.valueOf(this.hashCode()));
	}
}
