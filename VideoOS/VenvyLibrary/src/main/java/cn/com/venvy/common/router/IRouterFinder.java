package cn.com.venvy.common.router;

import java.util.Map;

/**
 * Created by yanjiangbo on 2018/1/24.
 * 注意这个类不能改变路径。因为会用到类名做反射
 */

public interface IRouterFinder {

    String findRole(String role);

    Map<String, String> getAllRole();
}
