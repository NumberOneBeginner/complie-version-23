package com.hsbc.greenpacket.view;

import java.util.EventListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.none.staff.R;

/**
 * Draw an clockwise animation, and call an listener when one round finish.
 * @author Tracy Wang
 * @since [17 Dec 2013]
 *
 */
public class CountDownView extends View {
    private Paint paintfore, paintback;
    private static final float START = -90;
    private float mSweep = 18;
    private static final float SWEEP_INC = 18;
    private RectF rectf;
    private DrawDoneOneRoundListener mDrawDoneOneRoundListener;
    private Runnable drawRunaale = new Runnable() {
        public void run() {
            synchronized (this) {
                try {
                    // refresh period 
                    this.wait(30);
                    mSweep += SWEEP_INC;
                    invalidate();
                } catch (InterruptedException e) {
                    Log.e("CountDownView","Refresh Count Down View Error", e);
                }
            }

        }

    };

    public CountDownView(Context context) {
        super(context);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paintfore = new Paint();
        paintfore.setAntiAlias(true);
        paintfore.setStyle(Paint.Style.FILL);
        paintfore.setColor(0xFFD9E021);
        paintback = new Paint();
        paintback.setAntiAlias(true);
        paintback.setStyle(Paint.Style.FILL);
        paintback.setColor(0x55000000);
        float height = context.getResources().getDimension(R.dimen.count_down_counter_height);
        float width = context.getResources().getDimension(R.dimen.count_down_counter_height);
        rectf = new RectF(0, 0, height, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(rectf, 0, 360, true, paintback);
        canvas.drawArc(rectf, START, mSweep, true, paintfore);
        if (mSweep > 360) {
            mDrawDoneOneRoundListener.onDrawOneRoundDownFinish();
        }else{
            drawRunaale.run();
        }
    }

    public void resetTimerAnimation() {
        mSweep = 18;
    }

    /**
     * @description event listener
     * @author 43734332 Cherry
     * @date 2013-12-16
     */
    public interface DrawDoneOneRoundListener extends EventListener {
        public void onDrawOneRoundDownFinish();
    }

    public DrawDoneOneRoundListener getmDrawDoneOneRoundListener() {
        return mDrawDoneOneRoundListener;
    }

    public void setmDrawDoneOneRoundListener(DrawDoneOneRoundListener mDrawDoneOneRoundListener) {
        this.mDrawDoneOneRoundListener = mDrawDoneOneRoundListener;
    }
}
