package cn.com.venvy.svga.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by yanjiangbo on 2018/3/26.
 * Done
 */

public class SVGADynamicEntity {

    private HashMap<String, Boolean> dynamicHidden = new HashMap<>();
    private HashMap<String, Bitmap> dynamicImage = new HashMap<>();
    private HashMap<String, StaticLayout> dynamicLayoutText = new HashMap<>();
    private HashMap<String, String> dynamicText = new HashMap<>();
    private HashMap<String, TextPaint> dynamicTextPaint = new HashMap<>();
    private HashMap<String, SVGAMethod> dynamicDrawer = new HashMap<>();
    private boolean isTextDirty;


    public final void clearDynamicObjects() {
        this.isTextDirty = true;
        this.dynamicHidden.clear();
        this.dynamicImage.clear();
        this.dynamicText.clear();
        this.dynamicTextPaint.clear();
        this.dynamicLayoutText.clear();
        this.dynamicDrawer.clear();
    }

    public void setDynamicDrawer(SVGAMethod method, String imageKey) {
        this.dynamicDrawer.put(imageKey, method);
    }

    public HashMap<String, SVGAMethod> getDynamicDrawer() {
        return dynamicDrawer;
    }

    public final HashMap<String, Boolean> getDynamicHidden() {
        return this.dynamicHidden;
    }

    public final HashMap<String, Bitmap> getDynamicImage() {
        return this.dynamicImage;
    }

    public final HashMap<String, StaticLayout> getDynamicLayoutText() {
        return this.dynamicLayoutText;
    }

    public final HashMap<String, String> getDynamicText() {
        return this.dynamicText;
    }

    public final HashMap<String, TextPaint> getDynamicTextPaint() {
        return this.dynamicTextPaint;
    }

    public final boolean isTextDirty() {
        return this.isTextDirty;
    }

    public final void setHidden(HashMap<String, Boolean> paramHashMap) {
        this.dynamicHidden = paramHashMap;
    }

    public final void setDynamicImage(Bitmap bitmap, String forKey) {
        this.dynamicImage.put(forKey, bitmap);
    }

    public final void setDynamicImage(final String url, final String forKey) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(forKey)) {
            return;
        }
        new Thread(new Runnable() {
            public final void run() {
                try {
                    URLConnection connection = new URL(url).openConnection();
                    if (!(connection instanceof HttpURLConnection)) {
                        connection = null;
                    }
                    if (connection != null) {
                        connection.setConnectTimeout(20000);
                        ((HttpURLConnection) connection).setRequestMethod("GET");
                        connection.connect();
                        InputStream inputStream = connection.getInputStream();
                        if (inputStream != null) {
                            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (bitmap != null) {
                                SVGAParser.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setDynamicImage(bitmap, forKey);
                                    }
                                });
                            }
                            inputStream.close();
                        }
                    }
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        }).start();
    }

    public final void setDynamicImage(HashMap<String, Bitmap> paramHashMap) {
        this.dynamicImage = paramHashMap;
    }

    public final void setDynamicLayoutText(HashMap<String, StaticLayout> paramHashMap) {
        this.dynamicLayoutText = paramHashMap;
    }

    public final void setDynamicText(StaticLayout layoutText, String forKey) {
        this.isTextDirty = true;
        this.dynamicLayoutText.put(forKey, layoutText);
    }

    public final void setDynamicText(String text, TextPaint textPaint, String forKey) {
        this.isTextDirty = true;
        this.dynamicText.put(forKey, text);
        this.dynamicTextPaint.put(forKey, textPaint);
    }

    public final void setDynamicText(HashMap<String, String> paramHashMap) {
        this.dynamicText = paramHashMap;
    }

    public final void setDynamicTextPaint(HashMap<String, TextPaint> paramHashMap) {
        this.dynamicTextPaint = paramHashMap;
    }

    public final void setHidden(boolean paramBoolean, String forKey) {
        this.dynamicHidden.put(forKey, paramBoolean);
    }

    public final void setTextDirty(boolean paramBoolean) {
        this.isTextDirty = paramBoolean;
    }


}
