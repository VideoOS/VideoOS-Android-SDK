package both.video.venvy.com.appdemo.bean;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class ConfigBean {
    private String creativeName;
    private String videoId;
    private String appKey;
    private String appSecret;

    public ConfigBean() {
    }

    public ConfigBean(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getCreativeName() {
        return creativeName;
    }

    public void setCreativeName(String creativeName) {
        this.creativeName = creativeName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
