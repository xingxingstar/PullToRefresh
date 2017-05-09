package com.example.zhuwojia.pulltorefresh.application;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

/**
 * author：shixinxin on 2017/5/8
 * version：v1.0
 */

public class App extends Application {
    public static int width;
    public static int height;

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }
}
