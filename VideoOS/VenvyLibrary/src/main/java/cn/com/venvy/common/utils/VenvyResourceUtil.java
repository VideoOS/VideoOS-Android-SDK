package cn.com.venvy.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class VenvyResourceUtil {
    public static int getLayoutId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "layout",
                paramContext.getPackageName());
    }

    public static int getStringId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "string",
                paramContext.getPackageName());
    }

    public static int getAnimId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "anim",
                paramContext.getPackageName());
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }

    public static int getMipmapId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "mipmap",
                paramContext.getPackageName());
    }

    public static int getDrawableOrmipmapId(Context paramContext,
                                            String paramString) {
        int status = getDrawableId(paramContext, paramString);
        if (status != 0)
            return status;
        else

            return getMipmapId(paramContext, paramString);
    }

    public static int getStyleId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "style",
                paramContext.getPackageName());
    }

    public static int getId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "id",
                paramContext.getPackageName());
    }

    public static int getAttrsId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "attrs",
                paramContext.getPackageName());
    }

    public static int getStyleableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "styleable", paramContext.getPackageName());
    }

    public static int getColorId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString, "color",
                paramContext.getPackageName());
    }

    public static Drawable getDrawable(Context mContext, String imgName) {
        Drawable drawable = mContext
                .getResources().getDrawable(
                        VenvyResourceUtil.getDrawableOrmipmapId(mContext, imgName));
        return drawable;

    }

    public static int[] getStyleabName(Context context, String className,
                                       String name) {
        String packageName = context.getPackageName();
        Class<?> r = null;
        int[] ids = null;
        try {
            r = Class.forName(packageName + ".R");

            @SuppressWarnings("rawtypes")
            Class[] classes = r.getClasses();
            Class<?> desireClass = null;

            for (int i = 0; i < classes.length; ++i) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];
                    break;
                }
            }

            if ((desireClass != null)
                    && (desireClass.getField(name).get(desireClass) != null)
                    && (desireClass.getField(name).get(desireClass).getClass()
                    .isArray()))
                ids = (int[]) desireClass.getField(name).get(desireClass);
        } catch (ClassNotFoundException e) {

        } catch (IllegalArgumentException e) {

        } catch (SecurityException e) {

        } catch (IllegalAccessException e) {

        } catch (NoSuchFieldException e) {

        }
        return ids;
    }

    public static int getIdByName(Context context, String className, String name) {
        String packageName = context.getPackageName();
        Class<?> r = null;
        int id = 0;
        try {
            r = Class.forName(packageName + ".R");
            @SuppressWarnings("rawtypes")
            Class[] classes = r.getClasses();
            Class<?> desireClass = null;
            for (int i = 0; i < classes.length; ++i) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];
                    break;
                }
            }
            if (desireClass != null)
                id = desireClass.getField(name).getInt(desireClass);
        } catch (ClassNotFoundException e) {

        } catch (IllegalArgumentException e) {

        } catch (SecurityException e) {

        } catch (IllegalAccessException e) {

        } catch (NoSuchFieldException e) {

        }
        return id;
    }
}
