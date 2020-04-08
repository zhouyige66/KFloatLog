package cn.kk20.floatlog.bean;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/4/6 19:45
 * @Version: v1.0
 */
public class CheckedItem {
    private boolean checked = false;
    private String itemText;

    public CheckedItem(String itemText) {
        this.itemText = itemText;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }
}
