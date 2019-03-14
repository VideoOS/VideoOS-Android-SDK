package cn.com.venvy.common.interf;

/**
 * Created by yanjiangbo on 2018/1/29.
 */

public enum VideoType {
    DEFAULT(0),
    VIDEOOS(1),
    LIVEOS(2);

    int id;

    VideoType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static VideoType getStatusById(int id) {
        if (id == VIDEOOS.getId()) {
            return VIDEOOS;
        } else if (id == LIVEOS.getId()) {
            return LIVEOS;
        } else {
            return DEFAULT;
        }
    }
}
