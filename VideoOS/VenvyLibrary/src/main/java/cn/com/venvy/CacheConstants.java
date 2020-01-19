package cn.com.venvy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.venvy.common.utils.VenvyPreferenceHelper;

/**
 * Created by Lucas on 2019/12/24.
 */
public class CacheConstants {

    // key
    private static final String RECENT_MINI_APP_ID = "recentMiniAppId";

    private static final int CACHE_IDS_MAX = 21;// 缓存ID的最大个数

    private static String currentDeveloperId = "";

    /**
     * 保存最近使用的小程序的ID
     *
     * @param context
     * @param id
     */
    public static void putVisionProgramId(Context context, String fileName, @NonNull String id) {

        String data = VenvyPreferenceHelper.getString(context, fileName, RECENT_MINI_APP_ID, "[]");
        try {
            JSONArray array = new JSONArray(data);
            // 判断数据是否之前存在
            boolean isContains = false;
            for (int i = 0, len = array.length(); i < len; i++) {
                isContains = ((String) array.opt(i)).equals(id);
                if (isContains) {
                    array.remove(i);
                    array.put(id);
                    break;
                }
            }
            if (!isContains) {
                // id有最大值限制，超过限制将最早缓存的remove
                if (array.length() >= CACHE_IDS_MAX) {
                    array.remove(0);
                }
                array.put(id);
            }

            VenvyPreferenceHelper.putString(context, fileName, RECENT_MINI_APP_ID, array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray reverse(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length() / 2; i++) {
            Object temp = jsonArray.opt(i);
            jsonArray.put(i,jsonArray.opt(jsonArray.length() - 1 - i));
            jsonArray.put(jsonArray.length() - 1 - i, temp);
        }
        return jsonArray;
    }

    /**
     * 获取最近使用的所有的小程序的id 集合
     *
     * @param context
     * @return
     */
    public static String getVisionProgramId(Context context, String fileName) {
        try {
            return reverse(new JSONArray(VenvyPreferenceHelper.getString(context, fileName, RECENT_MINI_APP_ID, "[]"))).toString();
        } catch (JSONException e) {
            return "[]";
        }
    }

    private static JSONArray removeDuplicates(JSONArray array, String id) throws JSONException {
        boolean isContains;
        for (int i = 0, len = array.length(); i < len; i++) {
            isContains = ((String) array.get(i)).equals(id);
            if (isContains) {
                array.remove(i);
                array.put(id);
                break;
            }
        }
        return array;
    }

    public static String getDeveloperId() {
        return currentDeveloperId;
    }

    public static void setDeveloperId(String currentDeveloperId) {
        CacheConstants.currentDeveloperId = currentDeveloperId;
    }

    /**
     * 查询当前developerId对应的数据
     */
    public static String getCacheByDeveloperId(Context context, String key) {
        return VenvyPreferenceHelper.getString(context, currentDeveloperId, key, "");
    }

    public static void putCacheByFileName(Context context, String fileName, String key, String value) {
        if (TextUtils.isEmpty(fileName)) {
            if (!TextUtils.isEmpty(currentDeveloperId)) {
                // 不传文件名默认用developId保存
                VenvyPreferenceHelper.putString(context, currentDeveloperId, key, value);
            }
        } else {
            // 文件名合法则用使用参数中传入的
            VenvyPreferenceHelper.putString(context, fileName, key, value);
        }
    }
}
