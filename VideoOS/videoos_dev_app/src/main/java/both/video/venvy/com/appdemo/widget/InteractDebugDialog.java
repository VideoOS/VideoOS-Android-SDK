package both.video.venvy.com.appdemo.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.bean.VideoInfo;
import both.video.venvy.com.appdemo.utils.AssetsUtil;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;

/**
 * Created by videopls on 2019/10/12.
 */

public class InteractDebugDialog extends DebugDialog {

    public InteractDebugDialog(Context context) {
        super(context);
    }

    @Override
    public List<VideoInfo> generateVideoData() {
        List<VideoInfo> videoInfos = new ArrayList<>();
        if (currentMode == LOCAL_MODE) {
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/car.mp4"));
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/ec.mp4"));
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/food.mp4"));
        } else if (currentMode == ONLINE_MODE) {
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/car.mp4"));
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/ec.mp4"));
            videoInfos.add(new VideoInfo("https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/demo/food.mp4"));
        }
        return videoInfos;
    }

    @Override
    public void updateView(int mode) {
        switch (mode) {
            case LOCAL_MODE:
                tvFirstNameView.setText("lua路径");
                tvSecondNameView.setText("json路径");
                tvThirdNameView.setText("视频路径");

                etFirstValueView.setFocusable(false);
                etFirstValueView.setFocusableInTouchMode(false);
                etSecondValueView.setFocusable(false);
                etSecondValueView.setFocusableInTouchMode(false);

                etFirstValueView.setText("file:///android_asset/alocal/lua/os_test_hotspot.lua");
                etSecondValueView.setText("file:///android_asset/alocal/local_test.json");
                etThirdValueView.setText(ConfigUtil.getInteractLocalVideoPath());

                break;
            case ONLINE_MODE:
                tvFirstNameView.setText("提交ID");
                tvSecondNameView.setText("json网址");
                tvThirdNameView.setText("视频网址");

                etFirstValueView.setFocusable(true);
                etFirstValueView.setFocusableInTouchMode(true);
                etSecondValueView.setFocusable(true);
                etSecondValueView.setFocusableInTouchMode(true);

                etFirstValueView.setText(ConfigUtil.getInteractOnLineCommitId());
                etSecondValueView.setText(ConfigUtil.getInteractOnLineJsonUrl());
                etThirdValueView.setText(ConfigUtil.getInteractOnLineVideoUrl());

//                etFirstValueView.setText("383");
//                etSecondValueView.setText("http://os-saas-share.videojj.com/test/applet/manage/5d9d53f4f1de5800113fae39/1575611774526.json");
                break;
        }
    }

    @Override
    protected Bundle handleData() {
        String firstData = etFirstValueView.getText().toString().trim();
        String secondData = etSecondValueView.getText().toString().trim();
        String thirdData = etThirdValueView.getText().toString().trim();
        if(TextUtils.isEmpty(firstData) || TextUtils.isEmpty(secondData) || TextUtils.isEmpty(thirdData)){
            Toast.makeText(context, "有数据为空。", Toast.LENGTH_SHORT).show();
            return null;
        }
        Bundle bundle = new Bundle();
        if(currentMode == LOCAL_MODE){

            if(!thirdData.endsWith(".mp4")){
                Toast.makeText(context,"视频格式不正确！",Toast.LENGTH_SHORT).show();
                return null;
            }

//            if (!new File(thirdData).exists()) {
//                Toast.makeText(context,"视频文件不存在！",Toast.LENGTH_SHORT).show();
//                return null;
//            }

            //数据缓存
            ConfigUtil.putInteractLocalVideoPath(thirdData);

            bundle.putInt("mode",LOCAL_MODE);
            bundle.putString("luaPath", firstData);
            bundle.putString("jsonPath", secondData);
            bundle.putString("videoPath", thirdData);
        }else{

            //数据缓存
            ConfigUtil.putInteractOnLineCommitId(firstData);
            ConfigUtil.putInteractOnLineJsonUrl(secondData);
            ConfigUtil.putInteractOnLineVideoUrl(thirdData);

            bundle.putInt("mode",ONLINE_MODE);
            bundle.putString("commitID", firstData);
            bundle.putString("jsonUrl", secondData);
            bundle.putString("videoUrl", thirdData);
        }
        return bundle;
    }
}
