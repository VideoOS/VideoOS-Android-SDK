package cn.com.venvy.common.interf;

/**
 * Created by yanjiangbo on 2018/1/29.
 */

public enum ScreenStatus {
    SMALL_VERTICAL(1),
    FULL_VERTICAL(2),
    LANDSCAPE(3);

    int id;

    ScreenStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ScreenStatus getStatusById(int id) {
        if (id == SMALL_VERTICAL.getId()) {
            return SMALL_VERTICAL;
        } else if (id == FULL_VERTICAL.getId()) {
            return FULL_VERTICAL;
        } else {
            return LANDSCAPE;
        }
    }
}
