package cn.kk20.floatlog.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.kk20.floatlog.FloatLogUtil;
import cn.kk20.floatlog.bean.LogItemBean;
import cn.kk20.floatlog.bean.TagBean;

/**
 * @Description: 数据容器
 * @Author: Roy Z
 * @Date: 2020/4/6 14:59
 * @Version: v1.0
 */
public class DataContainer {
    private static DataContainer instance;
    private List<LogItemBean> originData;
    private Map<String, TagBean> tagBeanMap;
    private Map<Integer, Set<String>> levelTagMap;

    private DataContainer() {
        originData = new ArrayList<>();
        tagBeanMap = new HashMap<>();
        levelTagMap = new HashMap<>();
    }

    public static DataContainer getInstance() {
        if (instance == null) {
            synchronized (DataContainer.class) {
                if (instance == null) {
                    instance = new DataContainer();
                }
            }
        }
        return instance;
    }

    public List<String> getLogTagList(Set<Integer> levels) {
        List<String> tags = new ArrayList<>();
        for (Integer level : levels) {
            Set<String> tagSet = levelTagMap.get(level);
            if (tagSet != null && !tagSet.isEmpty()) {
                tags.addAll(tagSet);
            }
        }
        return tags;
    }

    public List<LogItemBean> getLogList(Set<Integer> levels, Set<String> tags) {
        List<LogItemBean> logItemBeans = new ArrayList<>();
        for (String tag : tags) {
            TagBean tagBean = tagBeanMap.get(tag);
            if (tagBean != null) {
                List<LogItemBean> logItemBeanList = tagBean.logItemBeanList;
                if (!logItemBeanList.isEmpty()) {
                    for (LogItemBean bean : logItemBeanList) {
                        if (levels.contains(bean.getLogLevel())) {
                            logItemBeans.add(bean);
                        }
                    }
                }
            }
        }
        return logItemBeans;
    }

    public List<LogItemBean> getAllLogList() {
        return new ArrayList<>(originData);
    }

    /**
     * 添加日志到容器中
     *
     * @param logItemBean
     * @return true-容器已满，移除最早的数据，false-容器未满，无需移除数据
     */
    public boolean addLog(LogItemBean logItemBean) {
        int size = originData.size();
        boolean isFull = false;
        if (size > FloatLogUtil.getMaxLogItemCount()) {
            int removeSize = size - FloatLogUtil.getMaxLogItemCount() + 1;
            List<LogItemBean> removeLogItemBeans = originData.subList(0, removeSize);
            for (LogItemBean item : removeLogItemBeans) {
                int logLevel = item.getLogLevel();
                String logTag = item.getLogTag();
                // 从相应的标签列表中移除
                TagBean tagBean = tagBeanMap.get(logTag);
                if (tagBean != null) {
                    List<LogItemBean> logItemBeanList = tagBean.logItemBeanList;
                    if (!logItemBeanList.isEmpty()) {
                        logItemBeanList.remove(item);
                    }
                    if (logItemBeanList.isEmpty()) {
                        tagBeanMap.remove(logTag);
                    }
                }
                Set<String> tagSet = levelTagMap.get(logLevel);
                if (tagSet != null) {
                    tagSet.remove(logTag);
                    if (tagSet.isEmpty()) {
                        levelTagMap.remove(logLevel);
                    }
                }
            }
            originData.removeAll(removeLogItemBeans);
            isFull = true;
        }
        originData.add(logItemBean);
        String logTag = logItemBean.getLogTag();
        int logLevel = logItemBean.getLogLevel();
        // 维护tagBeanMap
        TagBean tagBean = tagBeanMap.get(logTag);
        if (tagBean != null) {
            List<LogItemBean> logItemBeanList = tagBean.logItemBeanList;
            logItemBeanList.add(logItemBean);
        } else {
            tagBean = new TagBean();
            tagBean.tagName = logTag;
            tagBean.logItemBeanList = new ArrayList<>();
            tagBean.logItemBeanList.add(logItemBean);
            tagBeanMap.put(logTag, tagBean);
        }
        // 维护levelTagMap
        Set<String> tagSet = levelTagMap.get(logLevel);
        if (tagSet == null) {
            tagSet = new HashSet<>();
            levelTagMap.put(logLevel, tagSet);
        }
        tagSet.add(logTag);

        return isFull;
    }

    public void clear() {
        originData.clear();
        tagBeanMap.clear();
        levelTagMap.clear();
    }

}
