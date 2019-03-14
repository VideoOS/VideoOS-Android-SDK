package cn.com.venvy.common.router;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.Config;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyPreferenceHelper;
import cn.com.venvy.common.utils.VenvyReflectUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by yanjiangbo on 2018/1/23.
 */

public class VenvyRouterManager {

    private static final String ROUTER_PREFERENCE_FILE_NAME = "Venvy_router_cache";
    private static final String ROUTER_SDK_VERSION = "SDK_VERSION";
    private static final String ROUTER_SDK_INIT_SUCCESS = "SDK_INIT_SUCCESS";

    public static final int INIT_DEFAULT = 0;
    public static final int INIT_START_INIT = 1;
    public static final int INIT_SUCCESS = 2;
    public static final int INIT_ERROR = 3;
    public static final int INIT_CANCEL = 4;

    private static final int RETRY_NUM = 1; // 失败重试次数
    private int initErrorCount = 0;
    public static int INIT_STATUS = INIT_DEFAULT;
    public static VenvyRouterManager sVenvyRouteManager = null;


    public static synchronized VenvyRouterManager getInstance() {
        if (sVenvyRouteManager == null) {
            sVenvyRouteManager = new VenvyRouterManager();
        }
        return sVenvyRouteManager;
    }

    private VenvyRouterManager() {

    }

    public interface RouterInitResult {
        void initSuccess();

        void initFailed();
    }

    public void init(final Context context, final RouterInitResult routerInitResult) {
        if (INIT_STATUS == INIT_START_INIT || INIT_STATUS == INIT_SUCCESS) {
            return;
        }
        if (initErrorCount >= 3) {
            //三次实例化失败，可以认为扫描出现问题了，为了防止多次扫描类出现性能影响，故在大于3次错误后停止扫描。
            return;
        }
        INIT_STATUS = INIT_START_INIT;
        VenvyAsyncTaskUtil.doAsyncTask("RouteInit", new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Void>() {
            @Override
            public Void doAsyncTask(Void... voids) throws Exception {

                Set<String> classes = getFinderClassNames(context);
                HashMap<String, RouteRoleCase> allTargetCase = VenvyRouterHelper.initCacheRoleFinder(classes);
                if (allTargetCase == null) {
                    return null;
                }
                Iterator<Map.Entry<String, RouteRoleCase>> it = allTargetCase.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, RouteRoleCase> entry = it.next();
                    RouteRoleCase routeRoleCase = entry.getValue();
                    if (routeRoleCase != null && !TextUtils.isEmpty(routeRoleCase.className)) {
                        VenvyReflectUtil.getClass(routeRoleCase.className);
                    }
                }
                return null;
            }
        }, new VenvyAsyncTaskUtil.CommonAsyncCallback<Void>() {
            @Override
            public void onPostExecute(Void aVoid) {
                INIT_STATUS = INIT_SUCCESS;
                initErrorCount = 0;
                VenvyPreferenceHelper.put(context, ROUTER_PREFERENCE_FILE_NAME, ROUTER_SDK_INIT_SUCCESS, true);
                VenvyPreferenceHelper.put(context, ROUTER_PREFERENCE_FILE_NAME, ROUTER_SDK_VERSION, Config.SDK_VERSION);
                if (routerInitResult != null) {
                    routerInitResult.initSuccess();
                }
            }

            @Override
            public void onCancelled() {
                INIT_STATUS = INIT_CANCEL;
            }

            @Override
            public void onException(Exception ie) {
                INIT_STATUS = INIT_ERROR;
                ++initErrorCount;
                //失败重试2次
                if (initErrorCount <= 2) {
                    init(context, routerInitResult);
                    return;
                }
                if (routerInitResult != null) {
                    routerInitResult.initFailed();
                }
            }
        });
    }


    private Set<String> getFinderClassNames(Context context) throws Exception {
        Set<String> classes = null;
        if (DebugStatus.isRelease() && !isFirstInstall(context)) {
            VenvyLog.d("Router", "begin get Cache router classes");
            String result = VenvyPreferenceHelper.getString(context, ROUTER_PREFERENCE_FILE_NAME, "classes", "");
            if (!TextUtils.isEmpty(result)) {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject == null) {
                        continue;
                    }
                    String className = jsonObject.optString("className");
                    if (TextUtils.isEmpty(className)) {
                        continue;
                    }
                    if (classes == null) {
                        classes = new HashSet<>();
                    }
                    classes.add(className);
                }
                if (classes != null && classes.size() > 0) {
                    VenvyLog.d("Router", "get Cache router classes successful,and class size is " + classes.size());
                    return classes;
                }
            }
        }
        String packageName = "cn.com.venvy.processor.build";
        classes = VenvyReflectUtil.getFileNameByPackageName(context, packageName);
        String result;
        if (classes != null && classes.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            for (String className : classes) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("className", className);
                jsonArray.put(jsonObject);
                VenvyLog.d("Router", "get router classes by packageName successful,and class name is " + className);
            }
            result = jsonArray.toString();
            VenvyPreferenceHelper.put(context, ROUTER_PREFERENCE_FILE_NAME, "classes", result == null ? "" : result);
        } else {
            VenvyLog.d("Router", "get router classes by packageName, but classes is null");
        }
        return classes;
    }

    public PostInfo setUri(Uri uri) {
        PostInfo info = new PostInfo();
        if (uri == null) {
            return info;
        }
        info.setScheme(uri.getScheme());
        info.setPath(uri.getPath());
        Set<String> set = uri.getQueryParameterNames();
        if (set != null && set.size() > 0) {
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                String name = (String) iterator.next();
                String value = uri.getQueryParameter(name);
                info.withString(name, value);
            }
        }
        return info;
    }


    public void injectRouter(Activity activity) {
        VenvyRouterHelper.injectView(activity);
    }

    public void injectRouter(Service service) {
        VenvyRouterHelper.injectView(service);
    }

    void navigation(final Context context, final ViewGroup viewGroup, final Uri uri, final Bundle bundle, final IRouterCallback callback, final int retryNum) {
        try {
            VenvyRouterHelper.navigation(context, viewGroup, uri, bundle, callback);
        } catch (Exception e) {
            handleException(context, viewGroup, uri, bundle, callback, retryNum, e);
        }
    }

    private void handleException(final Context context, final ViewGroup viewGroup, final Uri uri, final Bundle bundle, final IRouterCallback callback, final int retryNum, Exception e) {
        if (retryNum < RETRY_NUM) {
            if (e instanceof RouteInitException) {
                if (INIT_STATUS == INIT_ERROR || INIT_STATUS == INIT_CANCEL) {
                    init(context, null);
                }
                if (VenvyUIUtil.isOnUIThread()) {
                    VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                        @Override
                        public void run() {
                            navigation(context, viewGroup, uri, bundle, callback, retryNum + 1);
                        }
                    }, 800);
                } else {
                    try {
                        Thread.sleep(800);
                        navigation(context, viewGroup, uri, bundle, callback, retryNum + 1);
                    } catch (Exception ex) {
                        VenvyLog.e(VenvyRouterManager.class.getName(), ex);
                    }
                }
                return;
            }
        }
        if (callback != null) {
            callback.lost();
        }
    }


    private boolean isFirstInstall(Context context) {
        String oldVersion = VenvyPreferenceHelper.getString(context, ROUTER_PREFERENCE_FILE_NAME, ROUTER_SDK_VERSION, "");
        return !TextUtils.equals(oldVersion, Config.SDK_VERSION) || !VenvyPreferenceHelper.getBoolean(context, ROUTER_PREFERENCE_FILE_NAME, ROUTER_SDK_INIT_SUCCESS);
    }
}
