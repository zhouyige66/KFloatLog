package cn.kk20.floatlog.core;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.kk20.floatlog.R;
import cn.kk20.floatlog.bean.LogItemBean;
import cn.kk20.floatlog.bean.TagBean;

/**
 * @Description 悬浮日志视图
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class FloatLogView extends AbsFloatView implements View.OnClickListener {
    private static final int FLAG_EXPAND = 0;
    private static final int FLAG_SHRINK = 1;

    private View v_control;
    private TextView tv_level, tv_tag;
    private ImageView iv_clean;
    private ImageView iv_expand;
    private ImageView iv_close;

    private View v_data;
    private RecyclerView recyclerView;
    private View view_choose;
    private RecyclerView recyclerView_choose;

    private LogListAdapter adapter;
    private CommonAdapter<String> levelAdapter;
    private CommonAdapter<String> tagAdapter;

    private List<String> levels;
    private List<String> tags;
    private ArrayMap<String, TagBean> tagMap;
    private List<LogItemBean> logItemBeanList;
    private int selectLevel = 0;
    private String selectTag = "All";

    public FloatLogView(Context context) {
        super(context, R.layout.layout_log_view);
    }

    @Override
    public void init(final View view) {
        initData();

        // 工具栏
        v_control = view.findViewById(R.id.v_control);
        tv_level = view.findViewById(R.id.tv_level);
        tv_tag = view.findViewById(R.id.tv_tag);
        iv_clean = view.findViewById(R.id.iv_clean);
        iv_expand = view.findViewById(R.id.iv_expand);
        iv_close = view.findViewById(R.id.iv_close);
        // 数据区
        v_data = view.findViewById(R.id.v_data);
        recyclerView = view.findViewById(R.id.recyclerView);
        view_choose = view.findViewById(R.id.v_choose);
        recyclerView_choose = view.findViewById(R.id.recyclerView_choose);

        tv_level.setText("Verbose");
        tv_tag.setText("All");
        iv_expand.setTag(FLAG_EXPAND);
        tv_level.setOnClickListener(this);
        tv_tag.setOnClickListener(this);
        iv_clean.setOnClickListener(this);
        iv_expand.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        view_choose.setOnClickListener(this);

        // 等级与表签
        recyclerView_choose.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView_choose.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        // 日志
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        adapter = new LogListAdapter(view.getContext(), logItemBeanList);
        recyclerView.setAdapter(adapter);

        setOnFloatViewEventListener(new OnFloatViewEventListener() {
            @Override
            public void onBackEvent() {

            }

            @Override
            public void onClickEvent() {

            }

            @Override
            public void onMoveEvent(AbsFloatView floatView, float x, float y) {
                int offsetX = (int) (floatView.getLayoutParams().x + x);
                int offsetY = (int) (floatView.getLayoutParams().y + y);
                if (offsetX >= 0 && offsetX < floatView.getDisplayPoint().x) {
                    floatView.getLayoutParams().x = offsetX;
                }
                if (offsetY >= 0 && offsetY < floatView.getDisplayPoint().y) {
                    floatView.getLayoutParams().y = offsetY;
                }
                FloatWindowManager.updateFloatView(
                        floatView.getView().getContext().getApplicationContext(), floatView);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tv_level) {
            showLevelChoose();
        } else if (v == tv_tag) {
            showTagChoose();
        } else if (v == iv_clean) {
            for (TagBean bean : tagMap.values()) {
                if (bean.logItemBeanList != null) {
                    bean.logItemBeanList.clear();
                }
            }
            logItemBeanList.clear();
            adapter.notifyDataSetChanged();
        } else if (v == iv_expand) {
            int flag = (int) v.getTag();
            if (flag == FLAG_SHRINK) {// 当前是收缩，变为展开
                v.setTag(FLAG_EXPAND);
                iv_expand.setImageResource(R.drawable.ic_log_shrink);

                // 设置新的param
                v_data.setVisibility(View.VISIBLE);
                getLayoutParams().height = getDisplayPoint().y * 2 / 3;
                FloatWindowManager.updateFloatView(view.getContext().getApplicationContext(),
                        FloatLogView.this);
            } else {
                v.setTag(FLAG_SHRINK);
                iv_expand.setImageResource(R.drawable.ic_log_expand);

                v_data.setVisibility(View.GONE);
                getLayoutParams().height = v_control.getHeight();
                FloatWindowManager.updateFloatView(view.getContext().getApplicationContext(),
                        FloatLogView.this);
            }
        } else if (v == iv_close) {
            logItemBeanList.clear();
            adapter.notifyDataSetChanged();
            FloatWindowManager.hideFloatView(view.getContext().getApplicationContext(),
                    this);
        } else if (v == view_choose) {
            view_choose.setVisibility(View.GONE);
        }
    }

    private void initData() {
        levels = new ArrayList<>();
        levels.add("Verbose");
        levels.add("Debug");
        levels.add("Info");
        levels.add("Warn");
        levels.add("Error");

        tags = new ArrayList<>();
        tags.add("All");
        tagMap = new ArrayMap<>();
        TagBean tagBean = new TagBean();
        tagBean.index = 1;
        tagBean.tagName = "All";
        tagMap.put("All", tagBean);

        logItemBeanList = new ArrayList<>();
    }

    private void showLevelChoose() {
        if (levelAdapter == null) {
            levelAdapter = new CommonAdapter<String>(view.getContext(), R.layout.item_log_level_tag,
                    levels) {
                @Override
                protected void convert(ViewHolder viewHolder, String s, int i) {
                    viewHolder.setText(R.id.tv_level, s);
                }
            };
            levelAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
                    view_choose.setVisibility(View.GONE);
                    if (selectLevel == i) {
                        return;
                    }

                    selectLevel = i;
                    tv_level.setText(levels.get(i));
                    updateData();
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
                    return false;
                }
            });
        }

        recyclerView_choose.setAdapter(levelAdapter);
        view_choose.setVisibility(View.VISIBLE);
    }

    private void showTagChoose() {
        if (tagAdapter == null) {
            tagAdapter = new CommonAdapter<String>(view.getContext(), R.layout.item_log_level_tag,
                    tags) {
                @Override
                protected void convert(ViewHolder viewHolder, String s, int i) {
                    viewHolder.setText(R.id.tv_level, s);
                }
            };
            tagAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
                    view_choose.setVisibility(View.GONE);

                    if (selectTag.equals(tags.get(i))) {
                        return;
                    }

                    selectTag = tags.get(i);
                    tv_tag.setText(selectTag);
                    updateData();
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
                    return false;
                }
            });
        }

        // 处理标签列表，更新数据
        Collection<TagBean> tagBeans = tagMap.values();
        List<TagBean> list = new ArrayList<>(tagBeans);
        sort(list);
        tags.clear();
        for (TagBean bean : list) {
            tags.add(bean.tagName);
        }
        tagAdapter.notifyDataSetChanged();
        recyclerView_choose.setAdapter(tagAdapter);
        view_choose.setVisibility(View.VISIBLE);
    }

    private void updateData() {
        logItemBeanList.clear();
        if (selectLevel == 0) {// 所有等级
            if (selectTag.equals("All")) {// 所有日志
                for (TagBean tagBean : tagMap.values()) {
                    if (tagBean.logItemBeanList != null) {
                        logItemBeanList.addAll(tagBean.logItemBeanList);
                    }
                }
            } else {// 显示选中标签的日志
                logItemBeanList.addAll(tagMap.get(selectTag).logItemBeanList);
            }
        } else {
            if (selectTag.equals("All")) {
                // 按日志等级过滤
                for (TagBean tagBean : tagMap.values()) {
                    if (tagBean.logItemBeanList != null) {
                        for (LogItemBean bean : tagBean.logItemBeanList) {
                            if (bean.getLogLevel() == selectLevel) {
                                logItemBeanList.add(bean);
                            }
                        }
                    }
                }
            } else {
                // 双重过滤
                TagBean tagBean = tagMap.get(selectTag);
                if (tagBean.logItemBeanList != null) {
                    for (LogItemBean bean : tagBean.logItemBeanList) {
                        if (bean.getLogLevel() == selectLevel) {
                            logItemBeanList.add(bean);
                        }
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void sort(List<TagBean> collection) {
        Comparator<TagBean> comparator = new Comparator<TagBean>() {
            @Override
            public int compare(TagBean o1, TagBean o2) {
                return o1.index > o2.index ? 1 : -1;
            }
        };
        Collections.sort(collection, comparator);
    }

    public void addLog(LogItemBean bean) {
        // 数据预处理
        String tag = bean.getLogTag();
        // 处理标签
        if (tagMap.containsKey(tag)) {
            TagBean tagBean = tagMap.get(tag);
            if (tagBean.logItemBeanList == null) {
                tagBean.logItemBeanList = new ArrayList<>();
            }
            tagBean.logItemBeanList.add(bean);
        } else {
            TagBean tagBean = new TagBean();
            tagBean.tagName = tag;
            tagBean.index = tagMap.size() + 1;
            tagBean.logItemBeanList = new ArrayList<>();
            tagBean.logItemBeanList.add(bean);
            tagMap.put(tag, tagBean);
        }

        // 判断日志是否需要显示
        if (selectLevel == 0 || selectLevel == bean.getLogLevel()) {
            if (selectTag.equals("All") || selectTag.equals(tag)) {
                logItemBeanList.add(bean);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
