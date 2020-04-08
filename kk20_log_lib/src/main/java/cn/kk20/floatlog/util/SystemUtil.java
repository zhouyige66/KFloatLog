package cn.kk20.floatlog.util;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description 应用使用情况查看工具
 * @Author kk20
 * @Date 2018/3/15
 * @Version V1.0.0
 */
public class SystemUtil {

    /**
     * 检测用户是否对本app开启了“Apps with usage access”权限
     */
    public static boolean hasUsageAccessPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), context.getPackageName());
            }
            return mode == AppOpsManager.MODE_ALLOWED;
        }

        return true;
    }

    /**
     * 获取前台运行应用包名（API 21以下适用）
     *
     * @return
     */
    private static String getForegroundApp(Context context) {
        final int PROCESS_STATE_TOP = 2;
        try {
            //通过反射获取私有成员变量processState，稍后需要判断该变量的值
            Field processStateField = ActivityManager.RunningAppProcessInfo.class
                    .getDeclaredField("processState");
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                for (String pkg : process.pkgList) {
                    Log.i("kk20", "进程名：" + pkg);
                }
                //判断进程是否为前台进程
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    int state = processStateField.getInt(process);
                    //processState值为2
                    if (state == PROCESS_STATE_TOP) {
                        String[] pkgList = process.pkgList;
                        return pkgList[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取前台运行应用包名
     *
     * @return
     */
    public static String getForegroundApp2(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String topAppPackage = "";
            UsageStatsManager usm = (UsageStatsManager)
                    context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (usm != null) {
                long now = System.currentTimeMillis();
                //获取30秒之内的应用数据
                List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                        now - 30 * 1000, now);
                Log.i("kk20", "Running app number in last 30 seconds : " + stats.size());
                for (UsageStats us : stats) {
                    Log.i("kk20", "Running app : " + us.getPackageName());
                }
                //取得最近运行的一个app，即当前运行的app
                if (stats != null && !stats.isEmpty()) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topAppPackage = stats.get(j).getPackageName();
                }
                Log.i("kk20", "top running app is : " + topAppPackage);
            }
            return topAppPackage;
        } else {
            return getForegroundApp2(context);
        }
    }

    /**
     * 获取当前正在运行所有进程
     *
     * @param context
     * @return
     */
    public static List<String> getRunningAppProcesses(Context context) {
        List<String> pkgList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (usm != null) {
                long now = System.currentTimeMillis();
                // 获取5秒之内的应用数据
                List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                        now - 5 * 1000, now);
                for (UsageStats us : stats) {
                    pkgList.add(us.getPackageName());
                }
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                pkgList.addAll(Arrays.asList(process.pkgList));
            }
        }
        return pkgList;
    }

    /**
     * 检查指定包名应用是否在运行
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isAppRunning(Context context, String pkgName) {
        boolean running = false;
        List<String> pkgList = getRunningAppProcesses(context);
        if (pkgList != null && pkgList.size() > 0) {
            for (String pkg : pkgList) {
                if (pkg.equals(pkgName)) {
                    running = true;
                    break;
                }
            }
        }
        return running;
    }

    /**
     * 检查指定包名APP是否安装
     *
     * @param context
     * @param pkgName
     * @return
     */
    public boolean isAppInstalled(Context context, @NonNull String pkgName) {
        if (TextUtils.isEmpty(pkgName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 启动指定包名应用
     *
     * @param context
     * @param packageName
     */
    public static void launchApp(Context context, @NonNull String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent == null) {
                intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
