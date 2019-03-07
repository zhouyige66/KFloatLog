package cn.kk20.floatlog;

import android.content.Context;
import android.content.Intent;

import cn.kk20.floatlog.bean.LogItemBean;
import cn.kk20.floatlog.component.AlertWindowPermissionGrantActivity;
import cn.kk20.floatlog.component.LogService;
import cn.kk20.floatlog.util.AppOpsManagerUtil;

/**
 * @Description 悬浮日志工具
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class FloatLogUtil {
    private static Context APP_CONTEXT = null;

    private FloatLogUtil() {

    }

    private static void addLog(int level, String tag, String msg) {
        Context context = getAppContext();
        if (context == null) {
            throw new RuntimeException("FloatLogUtil中APP_CONTEXT未初始化，请先调用bind(Context context)！");
        }

        if (!AppOpsManagerUtil.checkDrawOverlays(context)) {
            Intent i = new Intent(context, AlertWindowPermissionGrantActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getAppContext().startActivity(i);
            return;
        }

        LogItemBean bean = new LogItemBean(level, tag, msg);
        Intent intent = new Intent(context, LogService.class);
        intent.putExtra("data", bean);
        context.startService(intent);
    }

    public static void bind(Context context) {
        APP_CONTEXT = context.getApplicationContext();
    }

    public static Context getAppContext() {
        return APP_CONTEXT;
    }

    public static void clean() {
        Context context = getAppContext();
        Intent intent = new Intent(context, LogService.class);
        context.stopService(intent);
    }

    public static void v(String tag, String msg) {
        addLog(LogItemBean.VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        addLog(LogItemBean.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        addLog(LogItemBean.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        addLog(LogItemBean.WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        addLog(LogItemBean.ERROR, tag, msg);
    }

}
