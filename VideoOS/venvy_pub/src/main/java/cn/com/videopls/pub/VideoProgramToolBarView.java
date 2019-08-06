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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyDeviceUtil;

/**
 * Created by Lucas on 2019/7/31.
 */
public class VideoProgramToolBarView extends LinearLayout implements VenvyObserver, IRouterCallback {


    protected VideoProgramView videoProgramView;

    private View mainContent;
    private View disConnectWifiContent;
    private View loadingContent;
    private TextView tvRetry;
    private View rlTitleBar;
    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivClose;

    private ImageView circle1, circle2;

    private String currentAppletId;

    private String cacheData;
    private int cacheType;


    private AnimatorSet circle1Set,circle2Set;

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
        inflate(getContext(), R.layout.video_program_tool, this);
        rlTitleBar = findViewById(R.id.rlTitleBar);
        rlTitleBar.setAlpha(0.9f);

        mainContent = findViewById(R.id.mainContent);
        loadingContent = findViewById(R.id.loadingContent);
        disConnectWifiContent = findViewById(R.id.disConnectWifiContent);


        tvRetry = (TextView) findViewById(R.id.tvRetry);
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
        videoProgramView = (VideoProgramView) findViewById(R.id.programView);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        circle1 = (ImageView) findViewById(R.id.circle1);
        circle2 = (ImageView) findViewById(R.id.circle2);
        ivBack.setVisibility(INVISIBLE);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                videoProgramView.removeTopView();
                ivBack.setVisibility(videoProgramView.getAllOfLuaView() > 1 ? VISIBLE : INVISIBLE);
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


    public void start(String appletId, String data, int type) {
        if (VenvyDeviceUtil.isNetworkAvailable(getContext())) {
            mainContent.setVisibility(VISIBLE);
            disConnectWifiContent.setVisibility(GONE);
            this.currentAppletId = appletId;

            videoProgramView.setVisibility(GONE);
            loadingContent.setVisibility(VISIBLE);
            startLoadingAnimation();
            videoProgramView.startVision(appletId, data, type, this);
        } else {
            this.currentAppletId = appletId;
            cacheData = data;
            cacheType = type;
            mainContent.setVisibility(GONE);
            disConnectWifiContent.setVisibility(VISIBLE);
        }

    }

    public void navigation(Uri uri, HashMap<String, String> params, IRouterCallback callback) {
        videoProgramView.navigation(uri, params, callback);
    }

    public void setVideoOSAdapter(@NonNull VideoPlusAdapter adapter) {
        videoProgramView.setVideoOSAdapter(adapter);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM.equals(tag)) {
            ivBack.setVisibility(videoProgramView.getAllOfLuaView() > 1 ? VISIBLE : INVISIBLE);
        }
    }


    @Override
    public void arrived() {
        // lua 加载成功回调
        videoProgramView.setVisibility(VISIBLE);
        loadingContent.setVisibility(GONE);
        cancelLoadingAnimation();
    }

    @Override
    public void lost() {

    }


    private void startLoadingAnimation() {

        if(circle1Set == null){
            circle1Set = new AnimatorSet();
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(circle1, "ScaleX", 1f, 0.5f,1f);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(circle1, "ScaleY", 1f, 0.5f,1f);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator[] items = new ObjectAnimator[]{scaleX, scaleY};
        circle1Set.playTogether(items);
        circle1Set.setDuration(1000).start();




        if(circle2Set == null){
            circle2Set = new AnimatorSet();
        }
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(circle2, "ScaleX", 0.5f, 1f,0.5f);
        scaleX2.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(circle2, "ScaleY", 0.5f, 1f,0.5f);
        scaleY2.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator[] items2 = new ObjectAnimator[]{scaleX2, scaleY2};
        circle2Set.playTogether(items2);
        circle2Set.setDuration(1000).start();

    }

    private void cancelLoadingAnimation() {
        if (circle1Set != null) {
            circle1Set.cancel();
            circle1Set = null;
        }
        if (circle2Set != null) {
            circle2Set.cancel();
            circle2Set = null;
        }
    }
}
