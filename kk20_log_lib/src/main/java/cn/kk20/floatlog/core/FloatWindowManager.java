package cn.kk20.floatlog.core;

import android.content.Context;
import android.view.WindowManager;

/**
 * @Description 悬浮window管理者
 * @Author kk20
 * @Date 2018/1/18
 * @Version V1.0.0
 */
public class FloatWindowManager {
    // 用于控制在屏幕上添加或移除悬浮窗
    private static WindowManager mWindowManager;

    // 私有化构造方法
    private FloatWindowManager() {

    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回，
     * 否则返回当前已创建的WindowManager
     *
     * @param context 必须为application的Context
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void showFloatView(Context context, AbsFloatView floatView) {
        if (context == null || floatView == null) {
            return;
        }

        boolean isShow = floatView.getView().isShown();
        if (!isShow) {
            getWindowManager(context).addView(floatView.getView(), floatView.getLayoutParams());
        }
    }

    public static void hideFloatView(Context context, AbsFloatView floatView) {
        if (context == null || floatView == null) {
            return;
        }

        boolean isShow = floatView.getView().isShown();
        if (isShow) {
            getWindowManager(context).removeView(floatView.getView());
        }
    }

    public static void updateFloatView(Context context, AbsFloatView floatView) {
        getWindowManager(context).updateViewLayout(floatView.getView(), floatView.getLayoutParams());
    }

}
