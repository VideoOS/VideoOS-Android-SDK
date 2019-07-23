package cn.com.venvy.common.interf;

/***
 * 视联网开启类型列表
 */

public enum ServiceType {
    VPIServiceTypeNone(0),
    VPIServiceTypeVideoMode(1),//视联网模式
    VPIServiceTypeVideoAd(2),//视频广告，包括前后帖广告
    VPIServiceTypePictureAd(3);//暂停广告


    int id;

    ServiceType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ServiceType getStatusById(int id) {
        if (id == VPIServiceTypeVideoMode.getId()) {
            return VPIServiceTypeVideoMode;
        } else if (id == VPIServiceTypeVideoAd.getId()) {
            return VPIServiceTypeVideoAd;
        } else if (id == VPIServiceTypePictureAd.getId()) {
            return VPIServiceTypePictureAd;
        } else {
            return VPIServiceTypeNone;
        }
    }
}
