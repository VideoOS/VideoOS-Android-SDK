package both.video.venvy.com.appdemo.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.bean.VideoInfo;
import both.video.venvy.com.appdemo.utils.ConfigUtil;

/**
 * Created by videopls on 2019/10/12.
 */

public class ServiceDebugDialog extends DebugDialog {

    public ServiceDebugDialog(Context context) {
        super(context);
    }

    @Override
    public List<VideoInfo> generateVideoData() {
        List<VideoInfo> videoInfos = new ArrayList<>();
        if (currentMode == LOCAL_MODE) {
            videoInfos.add(new VideoInfo(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + File.separator + "1.mp4"));
            videoInfos.add(new VideoInfo(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + File.separator + "2.mp4"));
            videoInfos.add(new VideoInfo(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + File.separator + "3.mp4"));
        } else if (currentMode == ONLINE_MODE) {
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/car.mp4"));
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/ec.mp4"));
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/food.mp4"));
        }
        return videoInfos;
    }

    @Override
    public boolean isHideSecondItemView() {
        return true;
    }

    @Override
    public void updateView(int mode) {
        switch (mode) {
            case LOCAL_MODE:
                tvFirstNameView.setText("Json路径");
                tvThirdNameView.setText("视频路径");

                etFirstValueView.setFocusable(false);
                etFirstValueView.setFocusableInTouchMode(false);
                etFirstValueView.setText("file:///android_asset/blocal/dev_config.json");
                etThirdValueView.setText(ConfigUtil.getServiceLocalVideoPath());
                break;
            case ONLINE_MODE:
                tvFirstNameView.setText("提交ID");
                tvThirdNameView.setText("视频URL");

                etFirstValueView.setFocusable(true);
                etFirstValueView.setFocusableInTouchMode(true);
                etFirstValueView.setText(ConfigUtil.getServiceOnLineCommitId());
                etThirdValueView.setText(ConfigUtil.getServiceOnLineVideoUrl());
                break;
        }
    }

    @Override
    protected Bundle handleData() {
        String firstData = etFirstValueView.getText().toString().trim();
        String secondData = etThirdValueView.getText().toString().trim();
        if(TextUtils.isEmpty(firstData) || TextUtils.isEmpty(secondData)){
            Toast.makeText(context, "有数据为空。", Toast.LENGTH_SHORT).show();
            return null;
        }
        Bundle bundle = new Bundle();
        if(currentMode == LOCAL_MODE){
            if(!firstData.endsWith(".json")){
                Toast.makeText(context,"json文件格式不正确！",Toast.LENGTH_SHORT).show();
                return null;
            }

            ConfigUtil.putServiceLocalVideoPath(secondData);

            bundle.putInt("mode",LOCAL_MODE);
            bundle.putString("jsonPath", firstData);
            bundle.putString("videoPath", secondData);
        }else{
            ConfigUtil.putServiceOnLineCommitId(firstData);
            ConfigUtil.putServiceOnLineVideoUrl(secondData);

            bundle.putInt("mode",ONLINE_MODE);
            bundle.putString("commitID", firstData);
            bundle.putString("videoUrl", secondData);
        }
        return bundle;
    }
}
