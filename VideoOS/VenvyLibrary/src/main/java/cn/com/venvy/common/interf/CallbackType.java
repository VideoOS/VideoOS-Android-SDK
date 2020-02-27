package cn.com.venvy.common.interf;

/**
 * Created by videopls on 2020/2/26.
 */

public enum CallbackType {
    CallbackTypeDesktop(0),
    CallbackTypePreloadZip(1),
    CallbackTypeQueryAds(2);

    int id;

    CallbackType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
