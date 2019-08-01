package cn.com.venvy.common.interf;

/***
 * 视联网开启类型列表
 */

public enum ServiceType {
    ServiceTypeNone(0),
    ServiceTypeVideoMode(1),//视联网模式
    ServiceTypeFrontVideo(2),//前帖广告
    ServiceTypeLaterVideo(3),//后贴广告
    ServiceTypePictureAd(4);//暂停广告


    int id;

    ServiceType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ServiceType getStatusById(int id) {
        switch (id) {
            case 0:
                return ServiceTypeNone;
            case 1:
                return ServiceTypeVideoMode;
            case 2:
                return ServiceTypeFrontVideo;
            case 3:
                return ServiceTypeLaterVideo;
            case 4:
                return ServiceTypePictureAd;
            default:
                return ServiceTypeNone;
        }
    }
}
