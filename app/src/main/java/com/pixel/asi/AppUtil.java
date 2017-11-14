package com.pixel.asi;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 * <p>
 * 工具集
 */

public class AppUtil {

    /**
     * 获取当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 今天是星期几
     * <p>
     * Java中: weekday=1，当天是周日；weekday=2，当天是周一；...;weekday=7，当天是周六。
     *
     * @return 0.星期日, 1.星期一, 2.星期二, 3.星期三, 4.星期四, 5.星期五, 6.星期六.
     */
    public static int getWeek() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        return weekday - 1;
    }

    /**
     * 获取现在几点
     *
     * @return 当前时间 24小时制
     */
    public static int getHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY); // Calendar.HOUR 12小时制
    }

    /**
     * 获取当前多少分
     *
     * @return
     */
    public static int getMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE); // Calendar.HOUR 12小时制
    }

    /**
     * 查看应用包名.启动要查看的程序,命令行输入：adb shell dumpsys window w |findstr \/ |findstr name=
     */
    public static void doStartApplicationWithPackageName(Context context, String packageName) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String pn = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(pn, className);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    /**
     * 杀死程序进程
     */
    public static void killAppProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 杀死对应报名的程序进程
     */
    public static void killAppProcess(Context context, String packageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(packageName);
    }

    /**
     * 获取当前正在连接的WIFI名称(SSID)
     * SSID: LX, BSSID: dc:fe:18:f3:d7:1f, MAC: 02:00:00:00:00:00, Supplicant state: COMPLETED, RSSI: -35, Link speed: 72Mbps, Frequency: 2462MHz, Net ID: 0, Metered hint: false, score: 60
     * @return SSID
     */
    public static String getWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    /**
     * 执行一个shell命令，并返回字符串值
     *
     * @param cmd           命令名称&参数组成的数组（例如：{"/system/bin/cat", "/proc/version"}）
     * @param workdirectory 命令执行路径（例如："system/bin/"）
     * @return 执行结果组成的字符串
     */
    public static synchronized String run(String[] cmd, String workdirectory) {
        StringBuffer result = new StringBuffer();
        try {
            // 创建操作系统进程（也可以由Runtime.exec()启动）
            // Runtime runtime = Runtime.getRuntime();
            // Process proc = runtime.exec(cmd);
            // InputStream inputstream = proc.getInputStream();

            ProcessBuilder builder = new ProcessBuilder(cmd);
            InputStream in = null;
            // 设置一个路径（绝对路径了就不一定需要）
            if (workdirectory != null) {
                // 设置工作目录（同上）
                builder.directory(new File(workdirectory));
                // 合并标准错误和标准输出
                builder.redirectErrorStream(true);
                // 启动一个新进程
                Process process = builder.start();
                // 读取进程标准输出流
                in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    result = result.append(new String(re));
                }
            }
            // 关闭输入流
            if (in != null) {
                in.close();
            }
        } catch (Exception ex) {
            Log.e("AppUtil", "run", ex);
        }
        return result.toString();
    }

    /**
     * 执行屏幕点击 (没有ROOT权限无效)
     */
    public static void screenClick(final int x, final int y) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] order = {  // 利用ProcessBuilder执行shell命令
                            "input",
                            "tap",
                            String.valueOf(x),
                            String.valueOf(y)
                    };
                    Process process = new ProcessBuilder(order).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 执行 shell 脚本
     *
     * @param cmd 脚本
     */
    public static void execShellCmd(String cmd) throws IOException {
        // 申请获取root权限，这一步很重要，不然会没有作用
        Process process = Runtime.getRuntime().exec("su");
        // 获取输出流
        OutputStream outputStream = process.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeBytes(cmd);   // 写入指令
        dataOutputStream.flush();
        dataOutputStream.close();
        outputStream.close();
    }

}
