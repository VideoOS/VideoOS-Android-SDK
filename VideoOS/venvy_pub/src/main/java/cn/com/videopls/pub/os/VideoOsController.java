package cn.com.videopls.pub.os;


import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;

import cn.com.venvy.common.interf.IServiceCallback;
import cn.com.venvy.common.interf.ServiceType;
import cn.com.videopls.pub.VideoPlusController;
import cn.com.videopls.pub.VideoPlusView;
import cn.com.videopls.pub.asmp.VideoPlusStandRequestModel;

/*
 * Created by yanjiangbo on 2017/5/17.
 */

public class VideoOsController extends VideoPlusController {

    private VideoPlusStandRequestModel mModel;

    private int mCurServiceType = -1;

    public VideoOsController(final VideoPlusView osView) {
        super(osView);
    }

    @Override
    public void stop() {
        super.stop();
        if (mModel != null) {
            mModel.destroy();
        }
    }

    @Override
    public void startService(@NonNull ServiceType serviceType, HashMap<String, String> params,
                             IServiceCallback callback) {
        super.startService(serviceType, params, callback);
        String template = "";
        String id = "";
        mCurServiceType = serviceType.getId();
        if (mCurServiceType == ServiceType.VPIServiceTypeVideoAd.getId()) {
            // 前后帖
            template = "os_mix_stand_hotspot.lua";
            id = "os_mix_stand_hotspot";
        } else if (mCurServiceType == ServiceType.VPIServiceTypePictureAd.getId()) {
            // 暂停图片
            template = "os_stand_cloud_hotspot.lua";
            id = "os_stand_cloud_hotspot";
        }
//        if (mModel == null) {
//            mModel = new VideoPlusStandRequestModel(mPlatform);
//        }
//        mModel.destroy();
        Uri uri = Uri.parse("LuaView://defaultLuaView?template=" + template + "&id=" + id +
                "&priority=2");
        mContentView.navigation(uri, params, null);
    }

    @Override
    public void restartService(@NonNull ServiceType serviceType) {
        super.restartService(serviceType);
    }

    @Override
    public void pauseService(@NonNull ServiceType serviceType) {
        super.pauseService(serviceType);
    }

    @Override
    public void stopService(@NonNull ServiceType serviceType) {
        super.stopService(serviceType);
        if (mCurServiceType == -1) {
            return;
        }
        // 关闭暂停广告
        if (mCurServiceType == ServiceType.VPIServiceTypePictureAd.getId()) {
            mContentView.closeInfoView();
        }
    }
}
