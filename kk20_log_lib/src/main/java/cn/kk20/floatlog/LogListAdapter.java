package cn.kk20.floatlog;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * @Description 日志适配器
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class LogListAdapter extends CommonAdapter<LogItemBean> {
    private SimpleDateFormat simpleDateFormat;

    public LogListAdapter(Context context, List<LogItemBean> datas) {
        super(context, R.layout.item_log_detail, datas);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    @Override
    protected void convert(ViewHolder viewHolder, LogItemBean bean, int i) {
        TextView tvTag = viewHolder.getView(R.id.tv_tag);
        TextView tvLog = viewHolder.getView(R.id.tv_log);
        int type = bean.getLogLevel();
        switch (type) {
            case LogItemBean.VERBOSE:
                tvLog.setTextColor(Color.WHITE);
                break;
            case LogItemBean.DEBUG:
                tvLog.setTextColor(Color.parseColor("#fc2c31"));
                break;
            case LogItemBean.INFO:
                tvLog.setTextColor(Color.GREEN);
                break;
            case LogItemBean.WARN:
                tvLog.setTextColor(Color.parseColor("#ef5b25"));
                break;
            case LogItemBean.ERROR:
                tvLog.setTextColor(Color.RED);
                break;
            default:
                break;
        }

        String time = simpleDateFormat.format(bean.getDate());
        String text = String.format(Locale.CHINA, "[%s %s/%s]:", time, bean.getLogLevelStr(),
                bean.getLogTag());
        tvTag.setText(text);
        tvLog.setText(bean.getLogText());
    }
}
