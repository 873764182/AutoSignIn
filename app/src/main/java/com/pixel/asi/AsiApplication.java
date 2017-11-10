package com.pixel.asi;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import java.util.Timer;

/**
 * Created by Administrator on 2017/11/10 0010.
 * <p>
 * 程序代理
 */

public class AsiApplication extends Application {

    private static AsiApplication asiApp;

    public static Handler appHandler = new Handler(Looper.getMainLooper());

    public static Timer appTimer = null;

    public static void run(Runnable runnable) {
        appHandler.post(runnable);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AsiApplication.asiApp = this;
    }

    public static AsiApplication getContext() {
        return asiApp;
    }

}
