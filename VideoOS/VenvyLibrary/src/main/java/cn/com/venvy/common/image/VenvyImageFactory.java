package cn.com.venvy.common.image;

import android.content.Context;

import java.lang.reflect.Constructor;

import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.interf.ISvgaImageView;

/**
 * Created by mac on 18/2/28.
 */

public class VenvyImageFactory {

    public static IImageView createImage(Context context) {
        Class<? extends IImageView> clas = VenvyRegisterLibsManager.getImageViewLib();
        if (clas != null) {
            Constructor constructor = null;
            try {
                constructor = clas.getDeclaredConstructor(Context.class);
                return (IImageView) constructor.newInstance(context);
            } catch (Exception e) {
                return new DefaultImageView(context);
            }
        }

        return new DefaultImageView(context);
    }

    public static ISvgaImageView createSvgaImage(Context context) {
        Class<? extends ISvgaImageView> clas = VenvyRegisterLibsManager.getSvgaImageView();
        if (clas != null) {
            Constructor constructor = null;
            try {
                constructor = clas.getDeclaredConstructor(Context.class);
                return (ISvgaImageView) constructor.newInstance(context);
            } catch (Exception e) {
            }
        }

        return null;
    }
}
