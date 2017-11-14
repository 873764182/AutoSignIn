package com.pixel.asi;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 后台定时服务
 */
public class TimingService extends Service {
    // 两次检测的时间间隔 单位分钟
    public static double checkTime = 0.1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 网络变化监听器
    public NetStateChangedReceiver changedReceiver = new NetStateChangedReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, TimingService.class));
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(changedReceiver, filter);

        // 开始前台
        startForeground(1000, createNotification());

        Toast.makeText(this, "任务启动完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AsiApplication.appTimer == null) {
            AsiApplication.appTimer = new Timer();
            AsiApplication.appTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    asyncTask();
                }
            }, 1000, (long) (checkTime * 60 * 1000));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(changedReceiver);

        AsiApplication.appTimer.cancel();

        // 停止前台
        stopForeground(true);

        Toast.makeText(this, "任务已经关闭", Toast.LENGTH_SHORT).show();
    }

    // 穿件一个通知
    private Notification createNotification() {
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setShowWhen(false);
        mBuilder.setAutoCancel(false);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setLargeIcon(((BitmapDrawable) getDrawable(R.mipmap.ic_launcher)).getBitmap());
        mBuilder.setContentText("定时服务正在后台运行,请勿关闭.");
        mBuilder.setContentTitle("定时服务");
        return mBuilder.build();
    }

    /**
     * 异步定时任务 每10分钟执行一次 在子线程中运行
     */
    protected void asyncTask() {

        Log.e("TimingService", "签到服务在后台运行 " + System.currentTimeMillis());

        int state = SignInUtil.computationsTime(this);
        if (state == 1) {
            AsiApplication.run(new Runnable() {
                @Override
                public void run() {
                    SignInUtil.doMorning(AsiApplication.getContext());
                }
            });
        } else if (state == 2) {
            AsiApplication.run(new Runnable() {
                @Override
                public void run() {
                    SignInUtil.doEvening(AsiApplication.getContext());
                }
            });
        } else {
            // 不在签到时间内
        }
    }

}
