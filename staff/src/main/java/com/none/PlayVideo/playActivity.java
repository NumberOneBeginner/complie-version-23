package com.none.PlayVideo;



import com.none.staff.R;
import com.none.staff.activity.BaseActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class playActivity extends BaseActivity implements OnErrorListener,
OnCompletionListener {

	public static final String TAG = "VideoPlayer";

	private int position = 0;
	
	private MediaController mediaControls;
	private ProgressDialog progressDialog;
	private VideoView myVideoView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);
		
		Log.i("111", "调用playActivity");
		if (mediaControls == null) {
			mediaControls = new MediaController(playActivity.this);
		}

		if (mediaControls == null) {
			mediaControls = new MediaController(playActivity.this);
		}

		myVideoView = (VideoView) findViewById(R.id.video_view);

		progressDialog = new ProgressDialog(playActivity.this);
		
		progressDialog.setMessage("Loading...");

		progressDialog.setCancelable(false);
		progressDialog.show();

		try {
			myVideoView.setMediaController(mediaControls);
			myVideoView.setVideoPath(getIntent().getStringExtra("url"));


		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		myVideoView.requestFocus();
		myVideoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				progressDialog.dismiss();
				myVideoView.seekTo(position);
				if (position == 0) {
					myVideoView.start();
				} else {
					myVideoView.pause();
				}
			}
		});

		myVideoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {

				 playActivity.this.finish();
			}
		});
		
		
	}



	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		this.finish();		
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
		myVideoView.pause();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		position = savedInstanceState.getInt("Position");
		myVideoView.seekTo(position);
	}
	
}
