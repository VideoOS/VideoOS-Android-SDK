package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Lucas on 2019/7/31.
 * 视联网小程序容器
 * <p>
 * A类小程序   LuaView://defaultLuaView?template=xxx.lua&id=xxx
 * 跳转B类小程序     LuaView://applets?appletId=xxxx&type=x(type: 1横屏,2竖屏)
 * <p>
 * B类小程序容器内部跳转   LuaView://applets?appletId=xxxx&template=xxxx.lua&id=xxxx&(priority=x)
 */
public class VideoProgramTypeBView extends FrameLayout {

    /**
     * 会有多个B类小程序侧边栏覆盖的情况，所以需要一个Map统一管理
     */
    private HashMap<String, VideoProgramToolBarView> programMap = new HashMap<>();

    private VideoProgramToolBarView currentProgram;
    private FrameLayout programContent;

    private VideoPlusAdapter mAdapter;

    private String currentProgramId;

    public VideoProgramTypeBView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoProgramTypeBView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoProgramTypeBView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VideoProgramTypeBView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.video_program_type_b, this);
        // for test
//        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
//        setAlpha(0.5f);
        programContent = (FrameLayout) findViewById(R.id.programContent);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAllProgram();
            }
        });
    }


    public VideoProgramToolBarView createProgram() {
        VideoProgramToolBarView programToolBarView = generateVideoProgram();
        programContent.addView(programToolBarView);
        return programToolBarView;
    }


    private VideoProgramToolBarView generateVideoProgram() {
        VideoProgramToolBarView programToolBarView = new VideoProgramToolBarView(getContext());
        programToolBarView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return programToolBarView;
    }


    public void setVideoOSAdapter(VideoPlusAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void start(@NonNull String appletId, String data, int type) {
        this.currentProgramId = appletId;
        currentProgram = createProgram();
        if (mAdapter != null) {
            currentProgram.setVideoOSAdapter(mAdapter);
        }
        programMap.put(appletId, currentProgram);
        currentProgram.start(appletId, data, type);
    }

    /**
     * 关闭一个小程序
     * @param appletId
     */
    public void close(String appletId) {
        if (!TextUtils.isEmpty(appletId)) {
            VideoProgramToolBarView programView = programMap.get(appletId);
            if (programView != null) {
                programContent.removeView(programView);
                programMap.remove(appletId);
            }
        }
        checkVisionProgram();
    }


    /**
     *  关闭所有小程序
     */
    public void closeAllProgram(){
        for(VideoProgramToolBarView item : programMap.values()){
            programContent.removeView(item);
        }
        programMap.clear();
        setClickable(false);
    }

    private void checkVisionProgram(){
        setClickable(programMap.size() > 0 );
    }

}
