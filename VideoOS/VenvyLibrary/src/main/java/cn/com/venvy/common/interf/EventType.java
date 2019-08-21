package cn.com.venvy.common.interf;

/**
 * Created by videojj_pls on 2019/7/26.
 * 事件处理通知类型枚举
 */

public enum EventType {
    EventTypeNone(0),
    EventTypeAction(1);

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
                return EventTypeAction;
            default:
                return EventTypeNone;
        }
    }
}
