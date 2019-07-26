package cn.com.venvy.common.interf;

/***
 * 视联网开启类型列表
 */

public enum ServiceType {
    ServiceTypeNone(0),
    ServiceTypeCloud(1),//云图
    ServiceTypeWedge(2),//中插
    ServiceTypeFrontVideo(3),//前贴
    ServiceTypeLaterVideo(4),//后贴
    ServiceTypePictureAd(5);//暂停广告


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
                return ServiceTypeCloud;
            case 2:
                return ServiceTypeWedge;
            case 3:
                return ServiceTypeFrontVideo;
            case 4:
                return ServiceTypeLaterVideo;
            case 5:
                return ServiceTypePictureAd;
            default:
                return ServiceTypeNone;
        }
    }
}
