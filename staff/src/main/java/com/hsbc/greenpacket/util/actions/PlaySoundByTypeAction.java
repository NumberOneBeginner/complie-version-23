package com.hsbc.greenpacket.util.actions;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;
import com.none.staff.R;

/**
 * @Description this Hook("hsbc://function=PlaySoundByType&type=YYY(value:match/unmatch/result)") used for
 * 		play different sound by different type
 * @author Cherry
 * @date 2013-12-13
 */
public class PlaySoundByTypeAction extends HSBCURLAction {
	private boolean initialized;
	private SoundPool sp;
	HashMap<String, Integer> spMap;
	private static final String MATCH_AUDIO = "match";
    private static final String UNMATCH_AUDIO = "unmatch";
    private static final String RESULT_AUDIO = "result";
    private Context mContext;
	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		try {
			mContext = context;
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			String type = map.get(HookConstants.TYPE);
	        if(StringUtil.IsNullOrEmpty(type)){
	            throw new HookException("request parameter missing");
	        }
	        
	        if (!initialized) {
	            initialized = true;
	            spMap = new HashMap<String, Integer>();
	            spMap.put(MATCH_AUDIO, R.raw.shake_match);
	            spMap.put(UNMATCH_AUDIO, R.raw.shake_nomatch);
	            spMap.put(RESULT_AUDIO, R.raw.shake_match);
	        }

	        playSound(type, 0);
		} catch (Exception e) {
			executeHookAPIFailCallJs(webview);
			Log.e(TAG,"Fail to execute the hook:{}",e);
		}        
	}

/*    public void InitSound() {
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        spMap = new HashMap<String, Integer>();
        // spMap.put(MATCH_AUDIO, sp.load(mContext, R.raw.shake_match, 1));
        // spMap.put(UNMATCH_AUDIO, sp.load(mContext, R.raw.shake_nomatch, 1));
        // spMap.put(RESULT_AUDIO, sp.load(mContext, R.raw.shake_match, 1));
    }
*/
    public void playSound(String soundType, final int number) {
        AudioManager am = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        
        Activity activity =(Activity) mContext;
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC); 

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        final float volumnRatio = volumnCurrent / audioMaxVolumn;
        int resId = spMap.get(soundType);
        Log.d(TAG,"source Id:"+resId);
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        sp.load(mContext,resId, 1);
        sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // TODO Auto-generated method stub
                // soundPool.play(wav,100, 100, 0, 0, 1f);
                Log.d(TAG,"==Sound loaded"+ sampleId);
                sp.setVolume(sp.play(sampleId, 0.0f, 1.0f, 1, number,  1.0f), volumnRatio, volumnRatio);
            }
        });
    }
}
