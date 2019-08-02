package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;

/**
 * Created by Lucas on 2019/7/31.
 * 视联网小程序容器
 */
public class VideoProgramTypeBView extends FrameLayout {

    /**
     * 会有多个B类小程序侧边栏覆盖的情况，所以需要一个Map统一管理
     */
    private HashMap<String, String> programMap = new HashMap<>();

    private VideoProgramToolBarView currentProgram;
    private FrameLayout programContent;

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


    public void start() {
        currentProgram = createProgram();
        currentProgram.start(new MiniAppConfigModel.MiniAppConfigCallback() {
            @Override
            public void downComplete(String originData) {
                // 视联网小程序需要的lua下载完毕，加载对应的入口文件
            }

            @Override
            public void downError(Throwable t) {

            }
        });
    }

}
