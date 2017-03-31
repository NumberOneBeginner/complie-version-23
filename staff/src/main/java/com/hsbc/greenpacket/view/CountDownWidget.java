package com.hsbc.greenpacket.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.none.staff.R;

/**
 * A count down timer widget with an clockwise animation and number change
 * 
 * @author Tracy Wang
 * @since [17 Dec 2013]
 * 
 */
public class CountDownWidget extends RelativeLayout {
    private static final int MAX_TIME = 2;
    private CountDownFinishListener countDownFinishListener;
    private int coundDownTime = MAX_TIME;
    private ImageView imageView;
    private int[] numberResources = {R.drawable.countdown_1, R.drawable.countdown_2, R.drawable.countdown_3};
    private CountDownView countDownView;
    private static SoundPool sp;
    private SparseIntArray spMap;
    private static boolean isLoadFinish = false;
    private int playingsound=0;

    public CountDownWidget(Context context) {
        super(context);
        InitSound();
    }

    public CountDownWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitSound();
    }

    public CountDownWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        InitSound();
    }

    public void setCountDownFinishListener(CountDownFinishListener countDownFinishListener) {
        this.countDownFinishListener = countDownFinishListener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("count down widget", "onWindowFocusChanged" + hasFocus);
        imageView = (ImageView) this.findViewById(R.id.number_view);
        imageView.setImageResource(numberResources[coundDownTime]);
        countDownView = (CountDownView) this.findViewById(R.id.count_down_view);
        coundDownTime--;
        sp.stop(1);
        playSound(1, 2);
        countDownView.setmDrawDoneOneRoundListener(new CountDownView.DrawDoneOneRoundListener() {

            @Override
            public void onDrawOneRoundDownFinish() {
                if (coundDownTime >= 0) {
                    imageView.setImageResource(numberResources[coundDownTime]);
                    coundDownTime--;
                    countDownView.resetTimerAnimation();
                    countDownView.invalidate();
                } else {
                    countDownFinishListener.onCountDownFinish();
                }
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        Log.d("CountDownWidget", "onDetachedFromWindow");
        sp.stop(playingsound);
        super.onDetachedFromWindow();
    }

    public void resetCountDownTimer() {
        coundDownTime = MAX_TIME;
        if (countDownView != null) {
            countDownView.resetTimerAnimation();
        }
    }

    private void InitSound() {
        if (sp == null) {
            sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            spMap = new SparseIntArray();
            spMap.put(1, sp.load(this.getContext(), R.raw.unlock, 1));
            sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    isLoadFinish = true;
                    AudioManager am = (AudioManager) CountDownWidget.this.getContext().getSystemService(Context.AUDIO_SERVICE);
                    float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    float volumnRatio = volumnCurrent / audioMaxVolumn;
                    playingsound=sp.play(sampleId, 0.0f, 1.0f, 1, 1, 1.0f);
                    sp.setVolume(playingsound, volumnRatio, volumnRatio);
                }
            });
        }
    }

    private synchronized void playSound(int sound, final int number) {
        if (sp != null && isLoadFinish) {
            AudioManager am = (AudioManager) CountDownWidget.this.getContext().getSystemService(Context.AUDIO_SERVICE);
            float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float volumnRatio = volumnCurrent / audioMaxVolumn;
            playingsound=sp.play(sound, 0.0f, 1.0f, 1, number, 1.0f);
            sp.setVolume(playingsound, volumnRatio, volumnRatio);
        }
    }
}
