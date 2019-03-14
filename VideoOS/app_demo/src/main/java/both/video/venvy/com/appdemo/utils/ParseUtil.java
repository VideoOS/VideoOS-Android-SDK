package both.video.venvy.com.appdemo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.bean.OsConfigureBean;

/**
 * Create by bolo on 06/06/2018
 */
public class ParseUtil {

    public static OsConfigureBean parseConfig(String json) {
        OsConfigureBean bean = new OsConfigureBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            bean.userIdList = parseList(jsonObject.optJSONArray("user_id"));
            bean.platformIdList = parseList(jsonObject.optJSONArray("platform_id"));
            bean.roomIdList = parseList(jsonObject.optJSONArray("room_id"));
            bean.cateList = parseList(jsonObject.optJSONArray("cate"));
            bean.creativeNameList = parseList(jsonObject.optJSONArray("creative_name"));
        } catch (JSONException e) {
            return bean;
        }
        return bean;
    }

    private static List<String> parseList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.optString(i));
        }
        return list;
    }
}
