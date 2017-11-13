package com.pixel.asi;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
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

    @Override
    public void onCreate() {
        super.onCreate();

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
     * 检查是否已经打了上下班的卡
     */
    protected boolean checkState() {    // 今天是否已经打了上下班卡
        return ConfigUtil.getBoolean(this, SignInUtil.mKey()) && ConfigUtil.getBoolean(this, SignInUtil.eKey());
    }

    /**
     * 异步定时任务 每10分钟执行一次 在子线程中运行
     */
    protected void asyncTask() {

        Log.e("TimingService", "签到服务在后台运行 " + System.currentTimeMillis());

        if (checkState()) {
            return;
        }

        // 星期日与星期六不执行
        int week = AppUtil.getWeek();
        if (week == 0 || week == 6) {
            return;
        }

        // 上班时间 9:30 之前的30分钟内执行 与 下班后 18:00 之后30分钟内执行 执行范围: 9:00-9:30, 18:00-18:30
        int hour = AppUtil.getHour();
        int minute = AppUtil.getMinute();

        // 当前时间大小
        int t1 = SignInUtil.computationsTimeDifference(hour + ":" + minute);
        // 上班时间大小
        int t2 = SignInUtil.computationsTimeDifference(ConfigUtil.getString(this, SignInUtil.GOTO_TIME));
        // 下班时间大小
        int t3 = SignInUtil.computationsTimeDifference(ConfigUtil.getString(this, SignInUtil.AFTER_TIME));

        if (t1 < t2 && (t2 - t1) < 30) {    // 上班时间前30分钟内

            AsiApplication.run(new Runnable() {
                @Override
                public void run() {
                    SignInUtil.doMorning(AsiApplication.getContext());
                }
            });
            ConfigUtil.saveBoolean(this, SignInUtil.mKey(), true);

        } else if (t1 > t3 && (t1 - t3) < 30) { // 下班时间后30分钟内

            AsiApplication.run(new Runnable() {
                @Override
                public void run() {
                    SignInUtil.doEvening(AsiApplication.getContext());
                }
            });
            ConfigUtil.saveBoolean(this, SignInUtil.eKey(), true);

        } else {
            // 不在签到时间内
        }

    }

}
