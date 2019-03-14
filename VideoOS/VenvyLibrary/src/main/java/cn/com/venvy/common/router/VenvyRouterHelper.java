package cn.com.venvy.common.router;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.App;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyReflectUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.processor.annotation.VenvyAutoData;
import cn.com.venvy.processor.annotation.VenvyAutoRun;


class VenvyRouterHelper {

    private static List<IRouterFinder> sFinders;

    static void navigation(Context context, final ViewGroup viewGroup, @NonNull final Uri uri, final Bundle bundle, final IRouterCallback callback) throws Exception {
        if (VenvyRouterManager.INIT_STATUS != VenvyRouterManager.INIT_SUCCESS) {
            throw new RouteInitException("route not init success, can't be navigation");
        }
        if (sFinders == null) {
            throw new RouteException("route finder is null");
        }
        final Context targetContext = viewGroup != null ? viewGroup.getContext() : context != null ? context : App.getContext();
        final String role = !TextUtils.isEmpty(uri.getHost()) ? uri.getScheme() + "//" + uri.getHost() : uri.getScheme();
        if (TextUtils.isEmpty(role)) {
            throw new RouteException("route role is invalid");
        }
        if (VenvyUIUtil.isOnUIThread()) {
            navigation(targetContext, viewGroup, role, bundle, callback);
        } else {
            VenvyUIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        navigation(targetContext, viewGroup, role, bundle, callback);
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.lost();
                        }
                    }
                }
            });
        }
    }

    private static void navigation(Context context, final ViewGroup viewGroup, @NonNull final String role, final Bundle bundle, final IRouterCallback callback) throws Exception {
        if (sFinders == null || sFinders.size() <= 0) {
            VenvyLog.d("Router", "begin navigation but sFinders is null");
        }
        for (IRouterFinder finder : sFinders) {
            String roleValue = finder.findRole(role);
            if (TextUtils.isEmpty(roleValue)) {
                continue;
            }
            if (TextUtils.isEmpty(roleValue)) {
                throw new RouteException("route result is not found");
            }
            RouteRoleCase routeRoleCase = parseJsonStringToCase(roleValue);
            if (routeRoleCase == null) {
                throw new RouteException("route result is not found");
            }
            if (TextUtils.isEmpty(routeRoleCase.className)) {
                throw new RouteException("route class not found");
            }
            if (routeRoleCase.type == RouteType.UNKNOWN) {
                throw new RouteException("route type is unknown");
            }
            Class tClass = VenvyReflectUtil.getClass(routeRoleCase.className);
            if (tClass == null) {
                throw new RouteException("route target class init error");
            }
            switch (routeRoleCase.type) {
                case ACTIVITY:
                    Intent intent = new Intent(context, tClass);
                    intent.putExtras(bundle);
                    ActivityCompat.startActivity(context, intent, bundle);
                    if (callback != null) {
                        callback.arrived();
                    }
                    break;
                case VIEW:
                    View view = null;
                    if (bundle != null && viewGroup != null) {
                        String viewID = bundle.getString("id");
                        if (!TextUtils.isEmpty(viewID)) {
                            View oldView = viewGroup.findViewWithTag(viewID);
                            if (oldView != null) {
                                oldView.setVisibility(View.VISIBLE);
                                oldView.bringToFront();
                                injectView(oldView, bundle);
                                invokeRunMethod(oldView);
                                VenvyLog.d("Router", "navigation running, but view has init and viewID is " + viewID);
                                return;
                            }
                        }
                    }
                    Constructor<? extends View> constructor = tClass.getDeclaredConstructor(Context.class);
                    if (constructor != null) {
                        view = constructor.newInstance(context);
                    }
                    if (view == null) {
                        throw new RouteException("route target view init error");
                    }
                    //方法赋值
                    injectView(view, bundle);
                    invokeRunMethod(view);
                    if (viewGroup != null) {
                        addViewByPriority(viewGroup, view);
                        VenvyLog.d("Router", "navigation running, init view and addView to viewGroup");
                    }
                    if (callback != null) {
                        callback.arrived();
                    }
                    break;
                case SERVICE:
                    Intent intentService = new Intent(context, tClass);
                    intentService.putExtras(bundle);
                    context.startService(intentService);
                    if (callback != null) {
                        callback.arrived();
                    }
                    break;

                case OBJECT:
                    Object object = tClass.newInstance();
                    if (object == null) {
                        return;
                    }
                    if (callback != null) {
                        callback.arrived();
                    }
                    //方法赋值
                    injectView(object, bundle);
                    invokeRunMethod(object);
                    break;
            }
        }
        VenvyLog.d("Router", "navigation end and uri is " + role);
    }

    static void addViewByPriority(ViewGroup viewGroup, View view) {
        if (viewGroup == null || view == null) {
            return;
        }
        int childCount = viewGroup.getChildCount();
        if (childCount <= 0) {
            viewGroup.addView(view);
            return;
        }
        int priority = getViewPriority(view);
        Integer targetIndex = null;
        for (int i = 0; i < childCount; i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView != null) {
                int childPriority = getViewPriority(childView);
                if (childPriority > priority) {
                    targetIndex = i;
                }
            }
        }
        if (targetIndex == null) {
            viewGroup.addView(view);
        } else {
            viewGroup.addView(view, targetIndex);
        }
    }

    static int getViewPriority(View view) {
        try {
            if (view != null) {
                Field field = view.getClass().getDeclaredField("priority");
                if (field != null && field.getAnnotation(VenvyAutoData.class) != null) {
                    field.setAccessible(true);
                    Object targetPriority = field.get(view);
                    if (targetPriority != null && targetPriority instanceof String) {
                        return Integer.valueOf((String) targetPriority);
                    }
                }
            }
        } catch (Exception e) {
            //忽略此处异常
        }
        return 0;
    }


    static void injectView(Service service) {
        if (service == null || service.isRestricted()) {
            return;
        }
        //TODO
    }


    static void injectView(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        injectView(activity, bundle);
    }

    static HashMap<String, RouteRoleCase> initCacheRoleFinder(Set<String> finderClasses) throws Exception {
        HashMap<String, RouteRoleCase> appRouteRoleCaseMap;
        if (finderClasses == null) {
            return null;
        }
        if (sFinders == null) {
            VenvyLog.d("Router", "init router finder");
            sFinders = new ArrayList<>();
        }
        appRouteRoleCaseMap = new HashMap<>();
        for (String className : finderClasses) {
            Object object = VenvyReflectUtil.getInstance(className, null, null);
            if (object == null || !(object instanceof IRouterFinder)) {
                VenvyLog.d("Router", "finder is not instanceof IRouterFinder " + className);
                continue;
            }
            IRouterFinder finder = (IRouterFinder) object;
            VenvyLog.d("Router", "add finder to finders and finder is " + className);
            sFinders.add(finder);

            HashMap<String, RouteRoleCase> cases = parseInitDataToRouteRoleCaseMap(finder.getAllRole());
            if (cases != null) {
                appRouteRoleCaseMap.putAll(cases);
            }
        }
        return appRouteRoleCaseMap;
    }


    private static void injectView(Object host, Bundle bundle) {
        try {
            if (bundle == null) {
                return;
            }
            Field[] fields = host.getClass().getDeclaredFields();
            if (fields == null || fields.length <= 0) {
                return;
            }
            for (Field field : fields) {
                if (!field.isAnnotationPresent(VenvyAutoData.class)) {
                    continue;
                }
                VenvyAutoData venvyAutoData = field.getAnnotation(VenvyAutoData.class);
                if (venvyAutoData == null) {
                    continue;
                }
                String parseValue = venvyAutoData.name();
                String targetName = TextUtils.isEmpty(parseValue) ? field.getName() : parseValue;
                Object object = bundle.get(targetName);
                field.setAccessible(true);
                if (object != null) {
                    field.set(host, object);
                }
            }
        } catch (Exception e) {
            VenvyLog.e(VenvyRouterHelper.class.getName(), e);
        }
    }

    private static void invokeRunMethod(Object host) {
        try {
            Method[] methods = host.getClass().getDeclaredMethods();
            if (methods == null) {
                return;
            }
            for (Method method : methods) {
                VenvyAutoRun venvyAutoRun = method.getAnnotation(VenvyAutoRun.class);
                if (venvyAutoRun != null) {
                    method.setAccessible(true);
                    method.invoke(host, new Object[]{});
                }
            }
        } catch (Exception e) {
            VenvyLog.e(VenvyRouterHelper.class.getName(), e);
        }
    }

    private static HashMap<String, RouteRoleCase> parseInitDataToRouteRoleCaseMap(Map<String, String> data) throws Exception {
        if (data == null || data.size() == 0) {
            return null;
        }
        HashMap<String, RouteRoleCase> map = new HashMap<>();
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            map.put(entry.getKey(), parseJsonStringToCase(entry.getValue()));
        }
        return map;
    }

    private static RouteRoleCase parseJsonStringToCase(String value) throws Exception {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        RouteRoleCase routeRoleCase = new RouteRoleCase();
        JSONObject jsonObject = new JSONObject(value);
        routeRoleCase.className = jsonObject.optString("className");
        routeRoleCase.type = RouteType.parse(jsonObject.optInt("type"));
        return routeRoleCase;
    }
}
