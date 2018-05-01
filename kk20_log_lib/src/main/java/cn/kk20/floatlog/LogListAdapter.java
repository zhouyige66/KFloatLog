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
        String time = simpleDateFormat.format(bean.getDate());
        String text = String.format(Locale.CHINA, "[%s %s/%s]:", time, bean.getLogLevelStr(),
                bean.getLogTag());
        TextView tvTag = viewHolder.getView(R.id.tv_tag);
        TextView tvLog = viewHolder.getView(R.id.tv_log);
        int type = bean.getLogLevel();
        switch (type) {
            case 0:
                tvLog.setTextColor(Color.WHITE);
                tvLog.setTextColor(Color.WHITE);
                break;
            case 1:
                tvLog.setTextColor(Color.GREEN);
                tvLog.setTextColor(Color.GREEN);
                break;
            case 2:
                tvLog.setTextColor(Color.BLUE);
                tvLog.setTextColor(Color.BLUE);
                break;
            case 3:
                tvLog.setTextColor(Color.YELLOW);
                tvLog.setTextColor(Color.YELLOW);
                break;
            case 4:
                tvLog.setTextColor(Color.RED);
                tvLog.setTextColor(Color.RED);
                break;
            default:
                break;
        }
        tvTag.setText(text);
        tvLog.setText(bean.getLogText());
    }
}
