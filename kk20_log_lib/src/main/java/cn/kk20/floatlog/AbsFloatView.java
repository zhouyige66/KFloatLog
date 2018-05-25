package cn.kk20.floatlog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @Description 悬浮窗视图基类
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public abstract class AbsFloatView {
    public View view;

    private GestureDetector gestureDetector;
    private Point point;
    private WindowManager.LayoutParams layoutParams;
    private OnFloatViewEventListener mListener;

    public AbsFloatView(Context context, @LayoutRes int layoutId) {
        view = LayoutInflater.from(context).inflate(layoutId, null);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mListener != null) {
                        mListener.onBackEvent();
                    }
                    return true;
                }
                return false;
            }
        });
        init(view);

        Context applicationContext = context.getApplicationContext();
        WindowManager windowManager = (WindowManager)
                applicationContext.getSystemService(Context.WINDOW_SERVICE);
        point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            windowManager.getDefaultDisplay().getRealSize(point);
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            point.x = metrics.widthPixels;
            point.y = metrics.heightPixels;
        }

        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//6.0+
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        if (Build.VERSION.SDK_INT >= 19) {
            layoutParams.flags = layoutParams.flags
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;

        // 默认开启监控返回键
        setViewFocusable(true);
        // 初始化解码器
        initGestureDetector(context);
        // 监听滑动事件
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initGestureDetector(Context context) {
        gestureDetector = new GestureDetector(context,
                new GestureDetector.OnGestureListener() {
                    private float mTouchStartRawX = 0;
                    private float mTouchStartRawY = 0;
                    private float x = 0;
                    private float y = 0;

                    @Override
                    public boolean onDown(MotionEvent e) {
                        mTouchStartRawX = e.getRawX();
                        mTouchStartRawY = e.getRawY();
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        if (mListener != null) {
                            mListener.onClickEvent();
                        }

                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                            float distanceY) {
                        x = e2.getRawX();
                        y = e2.getRawY();
                        if (mListener != null) {
                            mListener.onMoveEvent(AbsFloatView.this,
                                    x - mTouchStartRawX, y - mTouchStartRawY);
                        }
                        mTouchStartRawX = e2.getRawX();
                        mTouchStartRawY = e2.getRawY();
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        return false;
                    }
                });
    }

    public View getView() {
        return view;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(WindowManager.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    /**
     * 悬浮view响应返回按键事件
     *
     * @param focusable
     */
    public void setViewFocusable(boolean focusable) {
        if (!focusable) {
            layoutParams.flags = layoutParams.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        view.setFocusableInTouchMode(focusable);
    }

    /**
     * 返回屏幕宽高参数
     *
     * @return
     */
    public Point getDisplayPoint() {
        return point;
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnFloatViewEventListener(OnFloatViewEventListener listener) {
        this.mListener = listener;
    }

    public abstract void init(View view);

    public interface OnFloatViewEventListener {
        void onBackEvent();

        void onClickEvent();

        void onMoveEvent(AbsFloatView floatView, float x, float y);
    }

}
