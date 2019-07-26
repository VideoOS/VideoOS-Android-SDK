package cn.com.venvy.common.interf;

/**
 * Created by videojj_pls on 2019/7/26.
 * 事件分发事件类型
 */

public enum EventType {
    EventTypeNone(0),
    EventTypeRestart(1),//重新开启
    EventTypePause(2);//暂停


    int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EventType getStatusById(int id) {
        switch (id) {
            case 0:
                return EventTypeNone;
            case 1:
                return EventTypeRestart;
            case 2:
                return EventTypePause;
            default:
                return EventTypeNone;
        }
    }
}
