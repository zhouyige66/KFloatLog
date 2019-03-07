package cn.kk20.floatlog.core;

import android.content.Context;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.kk20.floatlog.R;
import cn.kk20.floatlog.bean.LogItemBean;

/**
 * @Description 日志适配器
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class LogListAdapter extends CommonAdapter<LogItemBean> {
    private SimpleDateFormat simpleDateFormat;

    private static final int[] colors = {R.color.colorLogVerbose,R.color.colorLogDebug,
            R.color.colorLogInfo,R.color.colorLogWarn,R.color.colorLogError};

    public LogListAdapter(Context context, List<LogItemBean> datas) {
        super(context, R.layout.item_log_detail, datas);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    @Override
    protected void convert(ViewHolder viewHolder, LogItemBean bean, int i) {
        TextView tvTag = viewHolder.getView(R.id.tv_tag);
        TextView tvLog = viewHolder.getView(R.id.tv_log);
        int type = bean.getLogLevel();
        String time = simpleDateFormat.format(bean.getDate());
        String text = String.format(Locale.CHINA, "[%s %s/%s]:", time, bean.getLogLevelStr(),
                bean.getLogTag());
        tvTag.setTextColor(mContext.getResources().getColor(colors[type]));
        tvLog.setTextColor(mContext.getResources().getColor(colors[type]));
        tvTag.setText(text);
        tvLog.setText(bean.getLogText());
    }
}
