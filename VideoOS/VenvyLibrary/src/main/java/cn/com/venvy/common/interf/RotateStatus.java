package cn.com.venvy.common.interf;

/**
 * Created by Lucas on 2019/11/18.
 */
public enum RotateStatus {
    TO_LANDSCAPE(1),
    TO_VERTICAL(2);

    int id;

    RotateStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
