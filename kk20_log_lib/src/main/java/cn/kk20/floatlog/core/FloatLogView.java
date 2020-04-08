package cn.kk20.floatlog.core;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.kk20.floatlog.adapter.CommonAdapter;
import cn.kk20.floatlog.adapter.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.kk20.floatlog.FloatLogUtil;
import cn.kk20.floatlog.R;
import cn.kk20.floatlog.bean.CheckedItem;
import cn.kk20.floatlog.bean.LogItemBean;

/**
 * @Description 悬浮日志视图
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class FloatLogView extends AbsFloatView implements View.OnClickListener {
    private static final int FLAG_EXPAND = 0;// 展开
    private static final int FLAG_SHRINK = 1;// 收缩

    private View v_control;
    private TextView tv_level, tv_tag;
    private ImageView iv_clean;
    private ImageView iv_expand;
    private ImageView iv_close;
    private RecyclerView recyclerView;

    private View view_choose;
    private CheckBox cb_select_all;
    private RecyclerView recyclerView_choose;
    private Button btn_confirm, btn_cancel;

    private CommonAdapter<CheckedItem> filterAdapter;
    private LogListAdapter logListAdapter;
    private List<CheckedItem> checkItemList;// 筛选适配器数据
    private List<LogItemBean> logItemBeanList;// log适配器数据

    private List<CheckedItem> levels;// 等级
    private List<CheckedItem> tags;// 标签
    private Set<Integer> selectLevels;// 当前选中的显示等级
    private Set<String> selectTags;// 当前选中的显示标签
    private boolean isSelectLevel;
    private boolean isSelectAllTag = true;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        // 过滤选择区
        view_choose = view.findViewById(R.id.v_choose);
        cb_select_all = view.findViewById(R.id.cb_select_all);
        recyclerView_choose = view.findViewById(R.id.recyclerView_choose);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        tv_level.setText("All");
        tv_tag.setText("All");
        iv_expand.setTag(FLAG_EXPAND);
        tv_level.setOnClickListener(this);
        tv_tag.setOnClickListener(this);
        iv_clean.setOnClickListener(this);
        iv_expand.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        cb_select_all.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        // 等级与表签
        recyclerView_choose.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView_choose.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        filterAdapter = new CommonAdapter<CheckedItem>(view.getContext(),
                R.layout.item_log_level_tag, checkItemList) {
            @Override
            protected void convert(ViewHolder viewHolder, CheckedItem checkedItem, int i) {
                CheckBox checkBox = viewHolder.getView(R.id.cb_level);
                checkBox.setText(checkedItem.getItemText());
                checkBox.setChecked(checkedItem.isChecked());
                checkBox.setTag(i);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int position = (int) buttonView.getTag();
                        checkItemList.get(position).setChecked(isChecked);

                        // 检查所有选项是否已全选
                        if (isChecked) {
                            boolean allSelected = true;
                            int size = checkItemList.size();
                            for (int i = 0; i < size; i++) {
                                CheckedItem item = checkItemList.get(i);
                                if (!item.isChecked()) {
                                    allSelected = false;
                                    break;
                                }
                            }
                            cb_select_all.setChecked(allSelected);
                        } else {
                            cb_select_all.setChecked(false);
                        }
                        if (!isSelectLevel) {
                            isSelectAllTag = cb_select_all.isChecked();
                        }
                    }
                });
            }
        };
        recyclerView_choose.setAdapter(filterAdapter);

        // 日志
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        logListAdapter = new LogListAdapter(view.getContext(), logItemBeanList);
        recyclerView.setAdapter(logListAdapter);

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
            expand();
            showLevelChoose();
        } else if (v == tv_tag) {
            expand();
            showTagChoose(false);
        } else if (v == iv_clean) {
            DataContainer.getInstance().clear();
            logItemBeanList.clear();
            logListAdapter.notifyDataSetChanged();
        } else if (v == iv_expand) {
            int flag = (int) v.getTag();
            if (flag == FLAG_SHRINK) {// 当前是收缩，变为展开
                expand();
            } else {
                v.setTag(FLAG_SHRINK);
                iv_expand.setImageResource(R.drawable.ic_log_expand);

                recyclerView.setVisibility(View.GONE);
                getLayoutParams().height = v_control.getHeight();
                FloatWindowManager.updateFloatView(view.getContext().getApplicationContext(),
                        FloatLogView.this);
            }
        } else if (v == iv_close) {
            logItemBeanList.clear();
            logListAdapter.notifyDataSetChanged();
            FloatWindowManager.hideFloatView(view.getContext().getApplicationContext(),
                    this);
        } else if (v == btn_confirm) {
            updateData();
        } else if (v == btn_cancel) {
            view_choose.setVisibility(View.GONE);
            resetSelectData();
        } else if (v == cb_select_all) {
            for (CheckedItem item : checkItemList) {
                item.setChecked(cb_select_all.isChecked());
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    filterAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void initData() {
        levels = new ArrayList<>(5);
        selectLevels = new HashSet<>();
        String[] levelArray = {"Verbose", "Debug", "Info", "Warn", "Error"};
        int index = 0;
        for (String level : levelArray) {
            CheckedItem item = new CheckedItem(level);
            item.setChecked(true);
            levels.add(item);
            selectLevels.add(index);
            index++;
        }
        tags = new ArrayList<>();
        checkItemList = new ArrayList<>();
        logItemBeanList = new ArrayList<>();
        selectTags = new HashSet<>();
    }

    private void expand() {
        int flag = (int) iv_expand.getTag();
        if (flag == FLAG_EXPAND) {
            return;
        }

        iv_expand.setTag(FLAG_EXPAND);
        iv_expand.setImageResource(R.drawable.ic_log_shrink);
        // 设置新的param
        recyclerView.setVisibility(View.VISIBLE);
        getLayoutParams().height = getDisplayPoint().y * 2 / 3;
        FloatWindowManager.updateFloatView(view.getContext().getApplicationContext(),
                FloatLogView.this);
    }

    private void showLevelChoose() {
        isSelectLevel = true;
        view_choose.setVisibility(View.VISIBLE);
        checkItemList.clear();
        checkItemList.addAll(levels);
        filterAdapter.notifyDataSetChanged();
        cb_select_all.setChecked(selectLevels.size() == levels.size());
        cb_select_all.setClickable(true);
    }

    private void showTagChoose(boolean reloadData) {
        isSelectLevel = false;
        view_choose.setVisibility(View.VISIBLE);
        if (reloadData || isSelectAllTag) {// 默认全选
            tags.clear();
            for (String tag : DataContainer.getInstance().getLogTagList(selectLevels)) {
                CheckedItem item = new CheckedItem(tag);
                item.setChecked(true);
                tags.add(item);
            }
        }

        checkItemList.clear();
        checkItemList.addAll(tags);
        filterAdapter.notifyDataSetChanged();
        if (tags.isEmpty()) {
            cb_select_all.setChecked(true);
            cb_select_all.setClickable(false);
        } else {
            cb_select_all.setClickable(true);
        }
        cb_select_all.setChecked(isSelectAllTag);
    }

    private void updateData() {
        // 检查当前选项中是否至少选择一项
        boolean check = false;
        for (CheckedItem item : checkItemList) {
            if (item.isChecked()) {
                check = true;
                break;
            }
        }
        if (!check) {
            if (isSelectLevel || !isSelectAllTag) {
                // 至少选择一项
                Toast.makeText(FloatLogUtil.getAppContext(), "请至少选择一项", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        }

        view_choose.setVisibility(View.GONE);
        if (isSelectLevel) {
            List<Integer> list = new ArrayList<>();
            int index = 0;
            for (CheckedItem item : levels) {
                if (item.isChecked()) {
                    list.add(index);
                }
                index++;
            }
            if (selectLevels.containsAll(list) && selectLevels.size() == list.size()) {
                // 未变化，不需要处理
                Log.d("roy", "level选项未改变");
            } else {
                selectLevels.clear();
                selectLevels.addAll(list);
                // 显示选中等级下的tag
                showTagChoose(true);
            }
        } else {
            List<String> list = new ArrayList<>();
            for (CheckedItem item : tags) {
                if (item.isChecked()) {
                    list.add(item.getItemText());
                }
            }
            if (selectTags.containsAll(list) && selectTags.size() == list.size()) {
                // 未变化，不需要处理
                Log.d("roy", "tag选项未改变");
            } else {
                selectTags.clear();
                selectTags.addAll(list);
                logItemBeanList.clear();
                logItemBeanList.addAll(
                        DataContainer.getInstance().getLogList(selectLevels, selectTags));
                logListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void resetSelectData() {
        if (isSelectLevel) {
            int index = 0;
            for (CheckedItem item : levels) {
                item.setChecked(selectLevels.contains(index));
                index++;
            }
        } else {
            for (CheckedItem item : tags) {
                item.setChecked(tags.contains(item.getItemText()));
            }
        }
    }

    public void addLog(LogItemBean bean) {
        // 数据预处理
        DataContainer.getInstance().addLog(bean);
        int logLevel = bean.getLogLevel();
        String logTag = bean.getLogTag();
        if (selectLevels.contains(logLevel)) {
            if (isSelectAllTag) {
                logItemBeanList.add(bean);
                logListAdapter.notifyDataSetChanged();
                selectTags.add(logTag);
                return;
            }

            if (selectTags.contains(logTag)) {
                logItemBeanList.add(bean);
                logListAdapter.notifyDataSetChanged();
            }
        }
    }

}
