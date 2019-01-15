package cn.com.venvy.common.track;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.RequestFactory;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.priority.Priority;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyPreferenceHelper;

/**
 * Created by Arthur on 2017/5/10.
 */

public class TrackHelper {

    private static final String TRACK_HOST_URL = "https://va.videojj.com/";
    protected static final String TRACK_URL = TRACK_HOST_URL + "track/v4/va.gif";
    protected static BaseRequestConnect requestConnect;

    //事件流id
    private static final String TRACK_SID = "sid";
    //Object id
    private static final String TRACK_OBJECT_ID = "oid";
    private static final String TRACK_VIDED_ID = "vid";
    //栏目id
    private static final String TRACK_CHANNEL_ID = "ch";
    //品牌id
    private static final String TRACK_BRAND_ID = "br";
    //infoId:eg tagId、tagId、linkId、resourceI的
    private static final String TRACK_INFO_ID = "iid";
    private static final String TRACK_CAT = "cat";
    private static final String TRACK_TARGET_ID = "tid";

    private static final String TRACK_CACHE_KEY = "VenvyVOSTRACK";

    private static int mCacheSize = 3;
    private static final String EMPTY_CACHE = "{params:[]}";

    public static void init(Platform platform) {
        requestConnect = RequestFactory.initConnect(platform);
    }

    public static void setCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
    }

    public static void sendGetAction(Context context, TrackParams trackParams) {
        Request request = HttpRequest.get(TrackHelper.TRACK_URL, buildGetParams(trackParams));
        request.setPriority(Priority.LOW);
        requestConnect.connect(request, null);
    }

    public static void sendPostAction(Context context, TrackParams trackParams) {
        //检查数据的有效性
        checkInvalid(trackParams);
        //追加缓存数据
        cache(context, trackParams);
        //检测是否达到缓存的大小，并发送请求
        doPost(context);
    }

    public static void onDestroy() {
        if (requestConnect != null) {
            requestConnect.abortAll();
        }
        requestConnect = null;
    }

    private static void doPost(final Context context) {
        String cacheJsonStr = VenvyPreferenceHelper.getString(context, TrackConfig.TRACK_PREFERENCE_NAME, TRACK_CACHE_KEY, EMPTY_CACHE);
        try {
            JSONObject json = new JSONObject(cacheJsonStr);
            JSONArray cacheJsonArray = json.optJSONArray("params");
            int cacheSize = cacheJsonArray.length();
            if (cacheSize >= mCacheSize) {
                doPost(cacheJsonArray, new IRequestHandler() {
                    @Override
                    public void requestFinish(Request request, IResponse response) {
                        if (response.isSuccess()) {
                            //清空缓存
                            VenvyPreferenceHelper.remove(context, TRACK_CACHE_KEY);
                        }
                    }

                    @Override
                    public void requestError(Request request, @Nullable Exception e) {

                    }

                    @Override
                    public void startRequest(Request request) {

                    }

                    @Override
                    public void requestProgress(Request request, int progress) {

                    }
                });
            }
        } catch (JSONException e) {
        }
    }

    private static void initCache(Context context) {
        if (!VenvyPreferenceHelper.contains(context, TrackConfig.TRACK_PREFERENCE_NAME, TRACK_CACHE_KEY)) {
            VenvyPreferenceHelper.putString(context, TrackConfig.TRACK_PREFERENCE_NAME, TRACK_CACHE_KEY, EMPTY_CACHE);
        }
    }

    private static void cache(Context context, TrackParams trackParams) {
        //如果还没有建立缓存,则初始化之
        initCache(context);
        String cacheJsonStr = VenvyPreferenceHelper.getString(context, TrackConfig.TRACK_PREFERENCE_NAME, TRACK_CACHE_KEY, EMPTY_CACHE);
        try {
            JSONObject json = new JSONObject(cacheJsonStr);
            JSONArray cacheJsonArray = json.optJSONArray("params");
            int cacheSize = cacheJsonArray.length();
            JSONObject paramJsonObj = new JSONObject(trackParams.toString());
            cacheJsonArray.put(cacheSize, paramJsonObj);
            VenvyLog.e("cacheParas ==" + json.toString());
            VenvyPreferenceHelper.putString(context, TrackConfig.TRACK_PREFERENCE_NAME, TRACK_CACHE_KEY, json.toString());
        } catch (JSONException e) {
        }
    }

    private static void doPost(JSONArray cacheJsonArray, IRequestHandler requestHandler) {
        Request request = HttpRequest.post(TrackHelper.TRACK_URL, buildPostParams(cacheJsonArray));
        request.setPriority(Priority.LOW);
        requestConnect.connect(request, requestHandler);
    }

    private static HashMap<String, String> buildPostParams(JSONArray cacheJsonArray) {
        HashMap<String, String> postParams = new HashMap<>();

        StringBuilder sidBuilder = new StringBuilder("[");
        StringBuilder oidBuilder = new StringBuilder("[");
        StringBuilder vidBuilder = new StringBuilder("[");
        StringBuilder chBuilder = new StringBuilder("[");
        StringBuilder brBuilder = new StringBuilder("[");
        StringBuilder iidBuilder = new StringBuilder("[");
        StringBuilder catBuilder = new StringBuilder("[");
        StringBuilder tidBuilder = new StringBuilder("[");

        for (int i = 0, len = cacheJsonArray.length(); i < len; i++) {
            JSONObject jsonObj = cacheJsonArray.optJSONObject(i);

            sidBuilder.append(jsonObj.opt("mSsId"));
            oidBuilder.append(jsonObj.opt("mObjectId"));
            vidBuilder.append(jsonObj.opt("mVideoId"));
            chBuilder.append(jsonObj.opt("mChannelId"));
            brBuilder.append(jsonObj.opt("mBrandId"));
            iidBuilder.append(jsonObj.opt("mInfoId"));
            catBuilder.append(jsonObj.opt("mCat"));
            tidBuilder.append(jsonObj.opt("mTargetIds"));

            if (i != len - 1) {
                sidBuilder.append(",");
                oidBuilder.append(",");
                vidBuilder.append(",");
                chBuilder.append(",");
                brBuilder.append(",");
                iidBuilder.append(",");
                catBuilder.append(",");
                tidBuilder.append(",");
            }
        }//end for

        sidBuilder.append("]");
        oidBuilder.append("]");
        vidBuilder.append("]");
        chBuilder.append("]");
        brBuilder.append("]");
        iidBuilder.append("]");
        catBuilder.append("]");
        tidBuilder.append("]");

        postParams.put(TRACK_SID, sidBuilder.toString());
        postParams.put(TRACK_OBJECT_ID, oidBuilder.toString());
        postParams.put(TRACK_VIDED_ID, vidBuilder.toString());
        postParams.put(TRACK_CHANNEL_ID, chBuilder.toString());
        postParams.put(TRACK_BRAND_ID, brBuilder.toString());
        postParams.put(TRACK_INFO_ID, iidBuilder.toString());
        postParams.put(TRACK_CAT, catBuilder.toString());
        postParams.put(TRACK_TARGET_ID, tidBuilder.toString());

        return postParams;
    }

    /**
     * 构造请求参数
     *
     * @param trackParams
     */
    private static HashMap<String, String> buildGetParams(TrackParams trackParams) {
        checkInvalid(trackParams);
        HashMap<String, String> getParams = new HashMap<>();
        getParams.put(TRACK_CAT, trackParams.getCat());
        getParams.put(TRACK_SID, trackParams.getSsId());
        getParams.put(TRACK_OBJECT_ID, trackParams.getObjectId());
        getParams.put(TRACK_INFO_ID, trackParams.getInfoId());
        getParams.put(TRACK_TARGET_ID, trackParams.getTargetIds());
        getParams.put(TRACK_BRAND_ID, trackParams.getBrandId());
        getParams.put(TRACK_CHANNEL_ID, trackParams.getChannelId());
        getParams.put(TRACK_VIDED_ID, trackParams.getVideoId());

        return getParams;
    }

    /**
     * 检查参数的有效性
     *
     * @param trackParams
     */
    private static void checkInvalid(TrackParams trackParams) {
        if (!trackParams.isCatEffective()) {
            throw new TrackParamsException("the param of setCat must be in[9,12,20,21,31,4,17,19]");
        }

        if (!trackParams.isVideoIdEffective()) {
            throw new TrackParamsException("videoId is null,please invoke setVideoId() to set it");
        }

        if (!trackParams.isChannelIdEffective()) {

        }
        //此时cat = 12/9/20/21/31
        if (trackParams.isOtherParamsNecessaryExceptCat()) {

            if (!trackParams.isSsIdEffective()) {
                throw new TrackParamsException("ssID is null,please invoke setSsId() to set it");
            }

            if (!trackParams.isInfoEffective()) {
                throw new TrackParamsException("infoId is null,please invoke setInfoId() to set it");
            }

            if (!trackParams.isTargetIdEffective()) {
                throw new TrackParamsException("targetId is null,please invoke setTargetId() to set it");
            }

            if (!trackParams.isBrandIdEffective()) {
                throw new TrackParamsException("brandId is null,please invoke setBrandId() to set it");
            }

            if (!trackParams.isObjectIdEffective()) {
                throw new TrackParamsException("objectId is null,please invoke setObjectId() to set it");
            }
        }


    }
}
