package cn.com.venvy.common.router;

/**
 * Created by yanjiangbo on 2018/1/23.
 */

public class RouteRoleCase {

    public String className;

    public RouteType type;

    public String toJson() {
        if (className == null || "".equals(className)) {
            return null;
        }
        if (type == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"className\"");
        builder.append(":");
        builder.append("\"" + className + "\"");
        builder.append(",");
        builder.append("\"type\"");
        builder.append(":");
        builder.append("\"" + type.getClassName() + "\"");

        builder.append("}");
        return builder.toString();
    }
}
