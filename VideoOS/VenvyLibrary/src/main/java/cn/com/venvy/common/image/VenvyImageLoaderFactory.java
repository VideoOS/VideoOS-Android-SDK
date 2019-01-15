package cn.com.venvy.common.image;

import java.lang.ref.WeakReference;

import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.utils.VenvyLog;


/**
 * Created by Arthur on 2017/6/20.
 */

public class VenvyImageLoaderFactory {

    private static WeakReference<IImageLoader> sImageLoaderReference;

    public static IImageLoader getImageLoader() {
        try {
            IImageLoader imageLoader = null;
            if (sImageLoaderReference == null) {
                imageLoader = VenvyRegisterLibsManager.getImageLoaderLib().newInstance();
                sImageLoaderReference = new WeakReference<>(imageLoader);
            } else {
                imageLoader = sImageLoaderReference.get();
                if (imageLoader == null) {
                    if (VenvyRegisterLibsManager.getImageLoaderLib() != null) {
                        imageLoader = VenvyRegisterLibsManager.getImageLoaderLib().newInstance();
                        sImageLoaderReference = new WeakReference<>(imageLoader);
                    }
                }
            }
            return imageLoader;
        } catch (Exception e) {
            VenvyLog.e(VenvyImageLoaderFactory.class.getName(), e);
        }
        return null;
    }
}
