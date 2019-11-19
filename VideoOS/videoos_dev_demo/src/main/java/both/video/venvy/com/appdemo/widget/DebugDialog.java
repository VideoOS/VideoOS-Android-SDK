package both.video.venvy.com.appdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.adapter.VideoListConfigAdapter;
import both.video.venvy.com.appdemo.bean.VideoInfo;

/**
 * Created by videopls on 2019/10/11.
 */

public abstract class DebugDialog extends Dialog implements View.OnClickListener{

    protected RecyclerView recyclerView;
    protected VideoListConfigAdapter videoListConfigAdapter;
    protected View vNativeLineView, vOnLineLineView;
    protected EditText etFirstValueView, etSecondValueView, etThirdValueView;
    protected TextView tvNativeTitleView, tvOnLineTitleView, tvFirstNameView, tvSecondNameView, tvThirdNameView;

    //配置模式类型
    public final static int LOCAL_MODE = 0;
    public final static int ONLINE_MODE = 1;

    public Context context;
    protected int currentMode = LOCAL_MODE;

    public DebugDialog(Context context) {
        super(context, R.style.BottomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_content_normal, null);
        setContentView(contentView);

        initView(contentView);
        updateDebugView(LOCAL_MODE);

        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(false);
        getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
    }

    private void initView(View contentView) {
        contentView.findViewById(R.id.rl_debug_native_tab1).setOnClickListener(this);
        contentView.findViewById(R.id.rl_debug_onLine_tab2).setOnClickListener(this);
        contentView.findViewById(R.id.tv_debug_ok).setOnClickListener(this);
        contentView.findViewById(R.id.tv_debug_cancel).setOnClickListener(this);
        contentView.findViewById(R.id.iv_debug_choice).setOnClickListener(this);

        tvNativeTitleView = contentView.findViewById(R.id.tv_debug_native_title);
        vNativeLineView = contentView.findViewById(R.id.v_debug_native_line);
        tvOnLineTitleView = contentView.findViewById(R.id.tv_debug_onLine_title);
        vOnLineLineView = contentView.findViewById(R.id.v_debug_onLine_line);

        tvFirstNameView = contentView.findViewById(R.id.tv_debug_first_name);
        tvSecondNameView = contentView.findViewById(R.id.tv_debug_second_name);
        tvThirdNameView = contentView.findViewById(R.id.tv_debug_third_name);

        etFirstValueView = contentView.findViewById(R.id.et_debug_first_value);
        etSecondValueView = contentView.findViewById(R.id.et_debug_second_value);
        etThirdValueView = contentView.findViewById(R.id.et_debug_third_value);

        if(isHideSecondItemView()){
            contentView.findViewById(R.id.rl_second).setVisibility(View.GONE);
        }

        recyclerView = contentView.findViewById(R.id.rv_video_path_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        videoListConfigAdapter = new VideoListConfigAdapter(null);
        recyclerView.setAdapter(videoListConfigAdapter);
        videoListConfigAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VideoInfo videoInfo = (VideoInfo)adapter.getData().get(position);
                if(videoInfo != null && !TextUtils.isEmpty(videoInfo.videoData)){
                    etThirdValueView.setText(videoInfo.videoData);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_debug_native_tab1:
                currentMode = LOCAL_MODE;
                updateDebugView(LOCAL_MODE);
                break;
            case R.id.rl_debug_onLine_tab2:
                currentMode = ONLINE_MODE;
                updateDebugView(ONLINE_MODE);
                break;
            case R.id.tv_debug_ok:
                if(interactDialogCallback != null){
                    Bundle bundle = handleData();
                    if(bundle == null){
                        return;
                    }
                    interactDialogCallback.onOK(this,bundle);
                }
                break;
            case R.id.tv_debug_cancel:
                if(interactDialogCallback != null){
                    interactDialogCallback.onCancel(this);
                }
                break;
            case R.id.iv_debug_choice:
                if(recyclerView.getVisibility() == View.VISIBLE){
                    recyclerView.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    videoListConfigAdapter.getData().clear();
                    videoListConfigAdapter.addData(generateVideoData());
                }
                break;
        }
    }

    public void updateDebugView(int mode){
        switch (mode) {
            case LOCAL_MODE:
                tvNativeTitleView.setTextColor(0xFFDFE1E6);
                vNativeLineView.setVisibility(View.VISIBLE);

                tvOnLineTitleView.setTextColor(0x88DFE1E6);
                vOnLineLineView.setVisibility(View.GONE);
                break;
            case ONLINE_MODE:
                tvOnLineTitleView.setTextColor(0xFFDFE1E6);
                vOnLineLineView.setVisibility(View.VISIBLE);

                tvNativeTitleView.setTextColor(0x88DFE1E6);
                vNativeLineView.setVisibility(View.GONE);
                break;
        }
        etFirstValueView.setText("");
        etSecondValueView.setText("");
        etThirdValueView.setText("");

        if(recyclerView.getVisibility() == View.VISIBLE){
            videoListConfigAdapter.getData().clear();
            videoListConfigAdapter.addData(generateVideoData());
        }

        updateView(mode);
    }

    public boolean isHideSecondItemView() {
        return false;
    }

    public abstract List<VideoInfo> generateVideoData();

    public abstract void updateView(int mode);

    protected abstract Bundle handleData();

    private InteractDialogCallback interactDialogCallback;

    public interface InteractDialogCallback{
        void onOK(Dialog dialog, Bundle bundleData);

        void onCancel(Dialog dialog);
    }
    public void setInteractDialogCallback(InteractDialogCallback interactDialogCallback) {
        this.interactDialogCallback = interactDialogCallback;
    }
}
