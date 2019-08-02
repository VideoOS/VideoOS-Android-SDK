package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Lucas on 2019/7/31.
 *
 */
public class VideoProgramToolBarView extends LinearLayout {


    protected VideoProgramView videoProgramView;

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

    private void init(){
        inflate(getContext(),R.layout.video_program_tool,this);
        setBackgroundColor(Color.parseColor("#0F5BDB"));
        setAlpha(0.5f);
        videoProgramView = (VideoProgramView)findViewById(R.id.programView);
    }

    public void start(MiniAppConfigModel.MiniAppConfigCallback callback){
        videoProgramView.startPlanB(callback);
    }

}
