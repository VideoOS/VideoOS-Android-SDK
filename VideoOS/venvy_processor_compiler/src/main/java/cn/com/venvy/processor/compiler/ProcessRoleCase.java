package cn.com.venvy.processor.compiler;

/**
 * Created by yanjiangbo on 2018/1/23.
 */

public class ProcessRoleCase {

    public String className;

    public int type;

    public String toJson() {
        if (className == null || "".equals(className)) {
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
        builder.append(type);

        builder.append("}");
        return builder.toString();
    }
}
