package cn.com.venvy.common.interf;

/***
 * 视联网开启类型列表
 */

public enum ServiceType {
    ServiceTypeNone(0),
    ServiceTypeVideoMode(1),//视联网模式
    ServiceTypeFrontVideo(3),//前帖广告
    ServiceTypeLaterVideo(4),//后贴广告
    ServiceTypePauseAd(5);//暂停广告


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
            case 3:
                return ServiceTypeFrontVideo;
            case 4:
                return ServiceTypeLaterVideo;
            case 5:
                return ServiceTypePauseAd;
            default:
                return ServiceTypeNone;
        }
    }
}
