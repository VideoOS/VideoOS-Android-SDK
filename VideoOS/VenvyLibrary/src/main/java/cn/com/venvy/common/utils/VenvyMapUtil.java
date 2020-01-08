package cn.com.venvy.common.utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
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

    public static Map<String, String> jsonToMap(String json) {
        HashMap<String, String> data = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = jsonObject.optString(key);
                data.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
