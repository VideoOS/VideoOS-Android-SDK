package cn.com.venvy.common.bean;

/**
 * Created by lgf on 2020/2/27.
 */

public class AcrConfigInfo {
    public String host;
    public String key;
    public String secret;

    public AcrConfigInfo(String key, String secret, String host) {
        this.host = host;
        this.key = key;
        this.secret = secret;
    }
}
