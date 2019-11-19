package cn.com.venvy.common.interf;

/***
 * 视联网开启类型列表
 */

public enum ServiceType {
    ServiceTypeNone(0),
    ServiceTypeVideoMode_POP(1),//视联网模式 - 气泡模式 - 1
    ServiceTypeFrontVideo(3),//前帖广告
    ServiceTypeLaterVideo(4),//后贴广告
    ServiceTypePauseAd(5),//暂停广告
    ServiceTypeVideoMode_TAG(6);//视联网模式 - 标签模式 - 0


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
                return ServiceTypeVideoMode_POP;
            case 3:
                return ServiceTypeFrontVideo;
            case 4:
                return ServiceTypeLaterVideo;
            case 5:
                return ServiceTypePauseAd;
            case 6:
                return ServiceTypeVideoMode_TAG;
            default:
                return ServiceTypeNone;
        }
    }
}
