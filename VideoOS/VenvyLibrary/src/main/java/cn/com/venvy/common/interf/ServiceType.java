package cn.com.venvy.common.interf;

/***
 * 视联网开启类型列表
 */

public enum ServiceType {
    VPIServiceTypeNone(0),
    VPIServiceTypeCloud(1),//云图
    VPIServiceTypeWedge(2),//中插
    VPIServiceTypeFrontVideo(3),//前贴
    VPIServiceTypeLaterVideo(4),//后贴
    VPIServiceTypePictureAd(5);//暂停广告


    int id;

    ServiceType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ServiceType getStatusById(int id) {
        if (id == VPIServiceTypeCloud.getId()) {
            return VPIServiceTypeCloud;
        } else if (id == VPIServiceTypeWedge.getId()) {
            return VPIServiceTypeWedge;
        } else if (id == VPIServiceTypeFrontVideo.getId()) {
            return VPIServiceTypeFrontVideo;
        } else if (id == VPIServiceTypeLaterVideo.getId()) {
            return VPIServiceTypeLaterVideo;
        } else if (id == VPIServiceTypePictureAd.getId()) {
            return VPIServiceTypePictureAd;
        } else {
            return VPIServiceTypeNone;
        }
    }
}
