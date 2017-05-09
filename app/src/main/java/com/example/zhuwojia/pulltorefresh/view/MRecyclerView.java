package com.example.zhuwojia.pulltorefresh.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author：shixinxin on 2017/5/5
 * version：v1.0
 */

public class MRecyclerView extends RecyclerView {
    public MRecyclerView(Context context) {
        super(context);

    }

    public MRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }


}
