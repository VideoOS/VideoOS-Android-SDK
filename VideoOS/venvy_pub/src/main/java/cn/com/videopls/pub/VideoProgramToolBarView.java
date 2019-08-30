package cn.com.videopls.pub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by Lucas on 2019/7/31.
 */
public class VideoProgramToolBarView extends BaseVideoVisionView implements VenvyObserver, IRouterCallback {


    protected VideoProgramView videoProgramView;

    private String currentAppletId;
    private String cacheData;
    private int cacheType;

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
                    start(currentAppletId, cacheData, cacheType);
                    cacheData = "";
                    cacheType = 0;
                }
            }
        });

        initVideoProgramView();

        ivBack.setVisibility(INVISIBLE);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                videoProgramView.removeTopView();
                checkBackDisplayLogic();
            }
        });
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(currentAppletId)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, currentAppletId);
                    ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM, bundle);
                }
                cancelLoadingAnimation();
            }
        });
    }

    private void initVideoProgramView(){
        videoProgramView = new VideoProgramView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = VenvyUIUtil.dip2px(getContext(),44f);
        videoProgramView.setLayoutParams(layoutParams);
        addView(videoProgramView);
    }


    public void start(String appletId, String data, int type) {
        if (VenvyDeviceUtil.isNetworkAvailable(getContext())) {
            this.currentAppletId = appletId;
            retryContent.setVisibility(GONE);
            loadingContent.setVisibility(VISIBLE);
            errorContent.setVisibility(GONE);
            startLoadingAnimation();
            videoProgramView.startVision(appletId, data, type, false,this);
        } else {
            this.currentAppletId = appletId;
            cacheData = data;
            cacheType = type;
            retryContent.setVisibility(VISIBLE);
            loadingContent.setVisibility(GONE);
            errorContent.setVisibility(GONE);
        }

    }

    public void navigation(Uri uri, HashMap<String, String> params, IRouterCallback callback) {
        videoProgramView.navigation(uri, params, callback);
    }

    public void setVideoOSAdapter(@NonNull VideoPlusAdapter adapter) {
        videoProgramView.setVideoOSAdapter(adapter);
    }


    public void showExceptionLogic(String msg, boolean needRetry) {
        VenvyLog.d("showExceptionLogic : " + msg + "  " + needRetry);
        if (needRetry) {
            retryContent.setVisibility(VISIBLE);
            loadingContent.setVisibility(GONE);
            errorContent.setVisibility(GONE);
            tvRetryMsg.setText(msg);
        } else {
            retryContent.setVisibility(GONE);
            loadingContent.setVisibility(GONE);
            errorContent.setVisibility(VISIBLE);
            tvErrorMsg.setText(msg);
        }
        checkBackDisplayLogic();
    }

    public void setTitle(final String title) {
        tvTitle.setText(title);
    }


    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM.equals(tag)) {
            checkBackDisplayLogic();
        }
    }


    private void checkBackDisplayLogic() {
        int luaViewCount = videoProgramView.getAllOfLuaView();
        VenvyLog.d("checkBackDisplayLogic : " + luaViewCount);
        ivBack.setVisibility(luaViewCount > 1 ? VISIBLE : GONE);
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



}
