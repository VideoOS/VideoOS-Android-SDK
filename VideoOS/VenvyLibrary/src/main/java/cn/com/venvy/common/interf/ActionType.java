package cn.com.venvy.common.interf;

/**
 * Created by videojj_pls on 2019/7/26.
 * 事件处理通知类型枚举
 */

public enum ActionType {
    EventTypeNone(0),
    EventTypeResume(1),//重新开启
    EventTypePause(2);//暂停


    int id;

    ActionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ActionType getStatusById(int id) {
        switch (id) {
            case 0:
                return EventTypeNone;
            case 1:
                return EventTypeResume;
            case 2:
                return EventTypePause;
            default:
                return EventTypeNone;
        }
    }
}
