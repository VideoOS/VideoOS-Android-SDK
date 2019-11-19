package cn.com.venvy.common.interf;

/**
 * Created by Lucas on 2019/11/18.
 */
public enum RotateStatus {
    TO_VERTICAL(1),
    TO_LANDSCAPE(2);

    int id;

    RotateStatus(int id) {
        this.id = id;
    }
}
