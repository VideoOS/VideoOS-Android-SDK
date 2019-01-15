package cn.com.venvy.common.router;

public enum RouteType {

    ACTIVITY(1, "android.app.Activity"),
    SERVICE(2, "android.app.Service"),
    VIEW(3, "android.view.View"),
    OBJECT(4, "java.lang.Object"),
    UNKNOWN(-1, "null");

    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_SERVICE = 2;
    public static final int TYPE_VIEW = 3;
    public static final int TYPE_OBJECT = 4;
    public static final int TYPE_UNKNOWN = -1;

    public int id = -1;
    public String className;

    public int getId() {
        return id;
    }

    public RouteType setId(int id) {
        this.id = id;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public RouteType setClassName(String className) {
        this.className = className;
        return this;
    }

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public static RouteType parse(String name) {
        for (RouteType routeType : RouteType.values()) {
            if (routeType.getClassName().equals(name)) {
                return routeType;
            }
        }
        return UNKNOWN;
    }

    public static RouteType parse(int id) {
        if (id == ACTIVITY.getId()) {
            return ACTIVITY;
        }
        if (id == SERVICE.getId()) {
            return SERVICE;
        }
        if (id == VIEW.getId()) {
            return VIEW;
        }
        if (id == OBJECT.getId()) {
            return OBJECT;
        }
        return UNKNOWN;
    }
}
