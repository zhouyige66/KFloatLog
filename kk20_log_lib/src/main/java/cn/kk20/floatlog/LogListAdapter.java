package cn.kk20.floatlog;

import android.content.Context;

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
    private  SimpleDateFormat simpleDateFormat;

    public LogListAdapter(Context context,List<LogItemBean> datas) {
        super(context, R.layout.item_log_detail, datas);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
    }

    @Override
    protected void convert(ViewHolder viewHolder, LogItemBean bean, int i) {
        String time = simpleDateFormat.format(bean.getDate());
        String text = String.format(Locale.CHINA,"[%s %s]:",time,bean.getLogTag());

        viewHolder.setText(R.id.tv_tag,text);
        viewHolder.setText(R.id.tv_log,bean.getLogText());
    }
}
