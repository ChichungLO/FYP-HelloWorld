package com.example.editpart.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.editpart.R;

/**
 * Created by cretin on 16/3/7.
 */
@SuppressLint("AppCompatCustomView")
public class StyleTagImageView extends ImageView {
    private float mEachItemHeight;

    private int[] mStyles = new int[]{
            R.font.calibri,
            R.font.times_new_roman,
            R.font.arial,
            R.font.courier,
            R.font.helvetica,};

    public OnStyleTagChanges getListener() {
        return listener;
    }

    public void setListener(OnStyleTagChanges listener) {
        this.listener = listener;
    }

    private OnStyleTagChanges listener;

    public StyleTagImageView(Context context) {
        super(context);
    }

    public StyleTagImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StyleTagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StyleTagImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEachItemHeight = getWidth() / 5;
        if (event.getX() >= 0 && event.getX() <= getWidth()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (listener != null) {
                        Log.e("",(int)(event.getX() / mEachItemHeight)+"");
                        listener.onStyleChange(mStyles[(int) (event.getX() / mEachItemHeight)]);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    public interface OnStyleTagChanges {
        void onStyleChange(int style);
    }
}
