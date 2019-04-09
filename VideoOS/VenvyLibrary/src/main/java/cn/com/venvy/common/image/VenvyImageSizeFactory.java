package cn.com.venvy.common.image;

import java.lang.ref.WeakReference;

import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.utils.VenvyLog;


/**
 * Created by Arthur on 2017/6/20.
 */

public class VenvyImageSizeFactory {

    private static WeakReference<IImageSize> sImageSizeReference;

    public static IImageSize getImageSize() {
        try {
            IImageSize imageSize = null;
            if (sImageSizeReference == null) {
                imageSize = VenvyRegisterLibsManager.getImageSizeLib().newInstance();
                sImageSizeReference = new WeakReference<>(imageSize);
            } else {
                imageSize = sImageSizeReference.get();
                if (imageSize == null) {
                    if (VenvyRegisterLibsManager.getImageLoaderLib() != null) {
                        imageSize = VenvyRegisterLibsManager.getImageSizeLib().newInstance();
                        sImageSizeReference = new WeakReference<>(imageSize);
                    }
                }
            }
            return imageSize;
        } catch (Exception e) {
            VenvyLog.e(VenvyImageSizeFactory.class.getName(), e);
        }
        return null;
    }
}
