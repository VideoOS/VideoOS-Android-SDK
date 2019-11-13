package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Set;

import cn.com.venvy.common.interf.IAppletListener;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by Lucas on 2019/7/31.
 */
@TargetApi(Build.VERSION_CODES.M)
public class VideoProgramToolBarView extends BaseVideoVisionView implements VenvyObserver, IRouterCallback, IAppletListener {


    protected VideoProgramView videoProgramView;

    private Set<String> templateIds = new ArraySet<>();

    private String currentAppletId;

    private String retryData;

    public VideoProgramToolBarView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoProgramToolBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoProgramToolBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VideoProgramToolBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM, this);
        cancelLoadingAnimation();
    }


    private void init() {
        retryContent.setClickable(true);
        errorContent.setClickable(true);
        tvRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VenvyDeviceUtil.isNetworkAvailable(getContext())) {
                    VideoOSLuaView osLuaView = getCurrentLuaView();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("eventType", "2");
                    map.put("appletActionType", "1");
                    map.put("data", retryData);
                    osLuaView.callLuaFunction("event", map);
                }
            }
        });

        initVideoProgramView();

        ivBack.setVisibility(INVISIBLE);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView();
            }
        });
    }

    private void initVideoProgramView() {
        videoProgramView = new VideoProgramView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = VenvyUIUtil.dip2px(getContext(), 44f);
        videoProgramView.setLayoutParams(layoutParams);

        videoProgramView.setAppletListener(this);
        addView(videoProgramView);
    }


    public void start(String appletId, String data, int type) {
        if (VenvyDeviceUtil.isNetworkAvailable(getContext())) {
            this.currentAppletId = appletId;
            retryContent.setVisibility(GONE);
            loadingContent.setVisibility(VISIBLE);
            errorContent.setVisibility(GONE);
            startLoadingAnimation();
            videoProgramView.startVision(appletId, data, type, false, this);
        } else {
            this.currentAppletId = appletId;
            retryContent.setVisibility(VISIBLE);
            loadingContent.setVisibility(GONE);
            errorContent.setVisibility(GONE);
        }

    }

    public void refreshHistory(String appletId) {
        videoProgramView.refreshRecentHistory(appletId);
    }

    public void setVideoOSAdapter(@NonNull VideoPlusAdapter adapter) {
        videoProgramView.setVideoOSAdapter(adapter);
    }


    public boolean isIncludeId(String id) {
        return templateIds.contains(id);
    }

    public void showExceptionLogic(String msg, boolean needRetry, String data) {
        VenvyLog.d("showExceptionLogic : " + msg + "  " + needRetry);
        videoProgramView.setVisibility(INVISIBLE);
        if (needRetry) {
            retryContent.setVisibility(VISIBLE);
            loadingContent.setVisibility(GONE);
            errorContent.setVisibility(GONE);
            tvRetryMsg.setText(msg);

            retryData = data;
        } else {
            retryContent.setVisibility(GONE);
            loadingContent.setVisibility(GONE);
            errorContent.setVisibility(VISIBLE);
            tvErrorMsg.setText(msg);
        }
        checkBackDisplayLogic();
    }


    public void showExceptionLogic(String msg, boolean needRetry) {
        showExceptionLogic(msg, needRetry, "");
    }

    public void setTitle(final String title, boolean nvgShow) {
        tvTitle.setText(title);
        if (!nvgShow) {

        }
    }


    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM.equals(tag)) {
            checkBackDisplayLogic();
        }
    }


    private boolean checkBackDisplayLogic() {
        int luaViewCount = videoProgramView.getAllOfLuaView();
        VenvyLog.d("checkBackDisplayLogic : " + luaViewCount);
        ivBack.setVisibility(luaViewCount > 1 ? VISIBLE : GONE);
        return luaViewCount > 1;
    }


    @Override
    public void arrived() {
        VenvyLog.d("arrived called");
        // lua 加载成功回调
        loadingContent.setVisibility(GONE);
        errorContent.setVisibility(GONE);
        retryContent.setVisibility(GONE);
        cancelLoadingAnimation();
        checkBackDisplayLogic();
    }

    @Override
    public void lost() {
        VenvyLog.d("lost called");
        // 需要判断是否是入口小程序失败，还是小程序之间内部跳转失败
        if (videoProgramView.getAllOfLuaView() > 0) {
            // 首次视联网小程序加载失败
            showExceptionLogic(getContext().getString(VenvyResourceUtil.getStringId(getContext(),
                    "miniAppCrashedTryOtherTag")), false);
        } else {
            // 小程序内部跳转加载失败
            showExceptionLogic(getContext().getString(VenvyResourceUtil.getStringId(getContext(),
                    "miniAppCrashed")), false);
        }
    }

    public void notifyLua(String data) {
        errorContent.setVisibility(GONE);
        retryContent.setVisibility(GONE);
        loadingContent.setVisibility(GONE);
        videoProgramView.setVisibility(VISIBLE);

        VenvyLog.d("call event function");

        // 通知正在展示的lua refresh
        VideoOSLuaView osLuaView = getCurrentLuaView();
        HashMap<String, String> map = new HashMap<>();
        map.put("eventType", "2");
        map.put("appletActionType", "2");
        map.put("data", data);
        osLuaView.callLuaFunction("event", map);

    }

    public VideoOSLuaView getCurrentLuaView() {
        return (VideoOSLuaView) videoProgramView.getChildAt(videoProgramView.getChildCount() - 1);
    }

    public void launchLuaScript(String template, final String id, String data) {
        Uri uri = Uri.parse("LuaView://defaultLuaView?template=" + template + "&id=" + id);
//            Uri uri = Uri.parse("LuaView://applets?appletId=123&template=test.lua&id=test");
        HashMap<String, String> params = new HashMap<>();
        params.put("data", data);
        videoProgramView.navigation(uri, params, new IRouterCallback() {
            @Override
            public void arrived() {
                templateIds.add(id);
            }

            @Override
            public void lost() {

            }
        });
    }


    @Override
    public boolean canGoBack() {
        return checkBackDisplayLogic();
    }

    @Override
    public void goBack() {
        errorContent.setVisibility(GONE);
        retryContent.setVisibility(GONE);
        videoProgramView.setVisibility(VISIBLE);
        videoProgramView.removeTopView();
        checkBackDisplayLogic();
    }

    @Override
    public void closeView() {
        if (!TextUtils.isEmpty(currentAppletId)) {
            Bundle bundle = new Bundle();
            bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, currentAppletId);
            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM, bundle);
        }
        cancelLoadingAnimation();
    }
}
