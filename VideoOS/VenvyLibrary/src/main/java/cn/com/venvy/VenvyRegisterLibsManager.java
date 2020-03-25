package cn.com.venvy;

import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageSize;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.interf.IACRCloud;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.interf.ISvgaImageView;
import cn.com.venvy.common.webview.IVenvyWebView;

/**
 * Created by yanjiangbo on 2018/1/16.
 */

public class VenvyRegisterLibsManager {

    private static Class<? extends IRequestConnect> sConnectClass;
    private static Class<? extends IImageLoader> sImageLoaderClass;
    private static Class<? extends IImageSize> sImageSizeClass;
    private static Class<? extends IVenvyWebView> sWebViewClass;
    private static Class<? extends IImageView> sImageViewClass;
    private static Class<? extends ISvgaImageView> sSvgaImageViewClass;
    private static Class<? extends ISocketConnect> sSocketConnect;
    private static Class<? extends IACRCloud> sACRCloud;

    public static Class<? extends IRequestConnect> getConnectLib() {
        return sConnectClass;
    }

    public static void registerConnectLib(Class<? extends IRequestConnect> connectClass) {
        if (sConnectClass == null) {
            sConnectClass = connectClass;
        }
    }

    public static Class<? extends IImageLoader> getImageLoaderLib() {
        return sImageLoaderClass;
    }

    public static Class<? extends IImageSize> getImageSizeLib() {
        return sImageSizeClass;
    }

    public static void registerImageViewLib(Class<? extends IImageView> imageViewLib) {
        if (sImageViewClass == null) {
            sImageViewClass = imageViewLib;
        }
    }

    public static Class<? extends IImageView> getImageViewLib() {
        return sImageViewClass;
    }


    public static void registerImageLoaderLib(Class<? extends IImageLoader> imageLoaderLib) {
        if (sImageLoaderClass == null) {
            sImageLoaderClass = imageLoaderLib;
        }
    }

    public static void registerImageSizeLib(Class<? extends IImageSize> imageSizeLib) {
        if (sImageSizeClass == null) {
            sImageSizeClass = imageSizeLib;
        }
    }

    public static Class<? extends IVenvyWebView> getWebViewLib() {
        return sWebViewClass;
    }

    public static void registerWebViewLib(Class<? extends IVenvyWebView> webViewLib) {
        if (sWebViewClass == null) {
            sWebViewClass = webViewLib;
        }
    }

    public static void registerSvgaImageView(Class<? extends ISvgaImageView> svgaImageView) {
        if (sSvgaImageViewClass == null) {
            sSvgaImageViewClass = svgaImageView;
        }
    }

    public static Class<? extends ISvgaImageView> getSvgaImageView() {
        return sSvgaImageViewClass;
    }

    public static Class<? extends ISocketConnect> getSocketConnect() {
        return sSocketConnect;
    }

    public static void registerSocketConnect(Class<? extends ISocketConnect> socketConnect) {
        VenvyRegisterLibsManager.sSocketConnect = socketConnect;
    }

    public static Class<? extends IACRCloud> getACRCloud() {
        return sACRCloud;
    }

    public static void registerACRCloud(Class<? extends IACRCloud> ACRCloud) {
        VenvyRegisterLibsManager.sACRCloud = ACRCloud;
    }
}
