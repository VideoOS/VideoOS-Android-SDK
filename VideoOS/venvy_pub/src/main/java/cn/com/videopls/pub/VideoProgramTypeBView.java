package cn.com.videopls.pub;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by Lucas on 2019/7/31.
 */
public class VideoProgramTypeBView extends FrameLayout implements VenvyObserver {

    /**
     *  视联网小程序容器
     *  <p>
     *  A类小程序   LuaView://defaultLuaView?template=xxx.lua&id=xxx
     *  跳转B类小程序     LuaView://applets?appletId=xxxx&type=x&appType=x(type: 1横屏,2竖屏,appType : 1 lua,2 H5)
     *  <p>
     *  B类小程序容器内部跳转   LuaView://applets?appletId=xxxx&template=xxxx.lua&id=xxxx&(priority=x)
     */

    /**
     * 会有多个B类小程序侧边栏覆盖的情况，所以需要一个Map统一管理
     */
    private HashMap<String, VideoProgramToolBarView> programMap = new HashMap<>();


    /**
     * 会有多个H5小程序侧边栏覆盖的情况，用一个map统一管理
     */
    private HashMap<String, VideoWebToolBarView> h5ProgramMap = new HashMap<>();

    private VideoProgramToolBarView currentProgram;

    private VideoWebToolBarView currentH5Program;

    private FrameLayout programContent;


    private VideoPlusAdapter mAdapter;

    private String currentProgramId;

    private String currentH5ProgramId;

    public VideoProgramTypeBView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoProgramTypeBView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoProgramTypeBView(@NonNull Context context, @Nullable AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VideoProgramTypeBView(@NonNull Context context, @Nullable AttributeSet attrs,
                                 int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        programContent = new FrameLayout(getContext());
        int screenHeight = VenvyUIUtil.getScreenHeight(getContext());
        int screenWidth = VenvyUIUtil.getScreenWidth(getContext());


        LayoutParams layoutParams = new LayoutParams((int) (Math.min(screenWidth, screenHeight) / 375.0f * 230),
                LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.END;
        programContent.setLayoutParams(layoutParams);
        addView(programContent);


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAllProgram();
            }
        });
    }


    public VideoProgramToolBarView createProgram(int orientationType) {
        VideoProgramToolBarView programToolBarView = generateVideoProgram();
        programToolBarView.setTag(orientationType);
        programContent.addView(programToolBarView);
        return programToolBarView;
    }


    public VideoWebToolBarView createH5Program() {
        VideoWebToolBarView webToolBarView = new VideoWebToolBarView(getContext());
        webToolBarView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        programContent.addView(webToolBarView);
        return webToolBarView;
    }


    private VideoProgramToolBarView generateVideoProgram() {
        VideoProgramToolBarView programToolBarView = new VideoProgramToolBarView(getContext());
        programToolBarView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return programToolBarView;
    }


    public void setVideoOSAdapter(VideoPlusAdapter adapter) {
        this.mAdapter = adapter;
    }

    /**
     * launch 一个视联网小程序 or H5 容器
     *
     * @param appletId
     * @param data
     * @param orientationType
     */
    public void start(@NonNull String appletId, String data, int orientationType, boolean isH5Type) {
        if (isH5Type) {
            this.currentH5ProgramId = appletId;
            // 有重复appletId则需要reload 当前页面
            if (h5ProgramMap.containsKey(appletId)) {
                VenvyLog.d("h5 appletId is exists");
                VideoWebToolBarView h5View = h5ProgramMap.get(appletId);
                h5View.reload();
                currentH5Program = h5View;
                return;
            }
            currentH5Program = createH5Program();
            if (mAdapter != null) {
                currentH5Program.setAdapter(mAdapter);
            }
            doEntranceAnimation(currentH5Program);
            h5ProgramMap.put(appletId, currentH5Program);

            currentH5Program.fetchTargetUrl(appletId, data);
        } else {
            this.currentProgramId = appletId;
            // 有重复appletId则需要显示到ViewGroup最顶层
            if (programMap.containsKey(appletId)) {
                VenvyLog.d("vision appletId is exists");
                VideoProgramToolBarView existView = programMap.get(appletId);
                //notify lua script refresh
                existView.bringToFront();
                existView.notifyLua(data);
                currentProgram = existView;
                return;
            }

            currentProgram = createProgram(orientationType);
            if (mAdapter != null) {
                currentProgram.setVideoOSAdapter(mAdapter);
            }
            doEntranceAnimation(currentProgram);

            programMap.put(appletId, currentProgram);
            currentProgram.start(appletId, data, orientationType);
        }
    }

    /**
     * launch 一个H5小程序
     *
     * @param url
     */
    public void startH5(String url) {
        currentH5Program.openLink(url);
    }

    /**
     * 关闭一个小程序
     *
     * @param appletId
     */
    public void close(String appletId) {
        close(appletId, true);
    }

    public void close(String appletId, boolean needAnimation) {
        if (!TextUtils.isEmpty(appletId)) {
            VideoProgramToolBarView programView = programMap.get(appletId);
            if (programView != null) {
                if (needAnimation) {
                    doExitAnimation(programView);
                } else {
                    programContent.removeView(programView);
                }
                programMap.remove(appletId);
            }
        }
        checkVisionProgram();
    }

    /**
     * 关闭一个H5小程序
     *
     * @param appletId
     */
    public void closeH5(String appletId) {
        if (!TextUtils.isEmpty(appletId)) {
            VideoWebToolBarView programView = h5ProgramMap.get(appletId);
            if (programView != null) {
                doExitAnimation(programView);
                h5ProgramMap.remove(appletId);
            }
        }
        checkVisionProgram();
    }

    /**
     * 关闭所有小程序
     */
    public void closeAllProgram() {
        // 视联网小程序
        for (VideoProgramToolBarView item : programMap.values()) {
            doExitAnimation(item);
        }
        programMap.clear();

        // H5小程序
        for (VideoWebToolBarView item : h5ProgramMap.values()) {
            doExitAnimation(item);
        }
        h5ProgramMap.clear();

        setClickable(false);
    }

    /**
     * 删除所有指定方向的视联网小程序
     *
     * @param orientationType
     */
    public void closeAllProgramByOrientation(int orientationType) {
        for (Map.Entry<String, VideoProgramToolBarView> item : programMap.entrySet()) {
            if ((int) item.getValue().getTag() == orientationType) {
                programContent.removeView(item.getValue());
                programMap.remove(item.getKey());
            }
        }
    }


    public void showExceptionLogic(String msg, boolean needRetry,String data) {
        if (currentProgram != null) {
            currentProgram.showExceptionLogic(msg,needRetry,data);
        }
    }


    public void setCurrentProgramTitle(String title) {
        if (currentProgram != null) {
            currentProgram.setTitle(title);
        }
    }


    private void checkVisionProgram() {
        setClickable(programMap.size() > 0 || h5ProgramMap.size() > 0);
    }


    private void doEntranceAnimation(View view) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(view, "translationX",
                VenvyUIUtil.dip2px(getContext(), 230), 0);
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    private void doExitAnimation(final View view) {
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(view, "translationX", 0,
                VenvyUIUtil.dip2px(getContext(), 230));
        valueAnimator.setDuration(300);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                programContent.removeView(view);
            }
        });
        valueAnimator.start();
    }


    /**
     * 暂时没有注册
     * @param observable
     * @param tag
     * @param bundle
     */
    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM.equals(tag)) {
            String appletId = bundle.getString(VenvyObservableTarget.KEY_APPLETS_ID);
            String template = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_TEMPLATE);
            String templateId = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_ID);
            String data = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DATA);
            if (programMap.containsKey(appletId)) {
                VideoProgramToolBarView existView = programMap.get(appletId);


                if (existView.isIncludeId(templateId)) {
                    // 当前已加载过对应lua脚本，把对应的lua脚本更新到顶层
                    existView.bringToFront();
                    existView.notifyLua(data);
                    currentProgram = existView;
                    // TODO :  notify lua script refresh
                } else {
                    // 如果没有加载过对应的lua,去加载
                    existView.launchLuaScript(template, templateId, data);
                }


            }
        }
    }
}
