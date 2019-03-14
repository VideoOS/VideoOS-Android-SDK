package cn.com.venvy.common.image;


import android.os.Build;
import android.text.TextUtils;

/**
 * http://consolecdn.videojj.com/statics/15158524_xl.jpg
 * https://staticcdn.videojj.com/5948f44e3948d3000013d1e7.jpeg
 * Created by Arthur on 2017/6/22.
 */

public class WebpConvert {
    private static final String WEBP_SUFFIX_QINIU = "?imageView2/0/format/webp";
    private static final String WEBP_SUFFIX_ALI = "?x-oss-process=image/format,webp";

    public static String convertWebp(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return url;
        }
        if (url.contains(".svga")) {
            return url;
        }

        if (url.contains("?")) {
            int index = url.indexOf("?");
            url = url.substring(0, index);
        }

        if (url.contains(".gif")) {
            return url;
        }

        //兼容类似http://www.gaoqing.com/ABCKSOKFDS此种url
        if (url.contains(".jpg") ||
                url.contains(".jpeg")
                || url.contains(".png")) {
            String webpUrl = null;
            if (url.contains("staticcdn.videojj.com") &&
                    !url.contains(WEBP_SUFFIX_QINIU)) {
                webpUrl = url + WEBP_SUFFIX_QINIU;
            } else if (url.contains("consolecdn.videojj.com") &&
                    !url.contains(WEBP_SUFFIX_ALI)) {
                webpUrl = url + WEBP_SUFFIX_ALI;
            }
            return webpUrl == null ? url : webpUrl;
        }
        return url;
    }
}
