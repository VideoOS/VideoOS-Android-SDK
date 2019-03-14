package cn.com.venvy.common.utils;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by yanjiangbo on 2017/5/10.
 */

public class VenvyMapUtil {

    /***
     * 将Map转化为Json
     * @return
     */
    public static <T> String mapToJson(Map<String, T> map) {
        if (map == null) {
            return "{}";
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

}
