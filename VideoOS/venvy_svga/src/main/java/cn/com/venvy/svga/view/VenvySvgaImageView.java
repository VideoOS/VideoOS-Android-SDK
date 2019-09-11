package cn.com.venvy.svga.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.SingleDownloadListener;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageLoaderResult;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.interf.ISvgaImageView;
import cn.com.venvy.common.interf.ISvgaParseCompletion;
import cn.com.venvy.svga.library.SVGADrawable;
import cn.com.venvy.svga.library.SVGADynamicEntity;
import cn.com.venvy.svga.library.SVGAImageView;
import cn.com.venvy.svga.library.SVGAParser;
import cn.com.venvy.svga.library.SVGARange;
import cn.com.venvy.svga.library.SVGAVideoEntity;

/**
 * IImageLoader:适配老板比如直播和点播的view
 * Created by mac on 18/3/23.
 */

public class VenvySvgaImageView extends SVGAImageView implements IImageLoader, ISvgaImageView {
    private SVGAParser svgaParser;
    private static boolean isInstallCache = false;

    public VenvySvgaImageView(Context context) {
        this(context, null);
    }

    public VenvySvgaImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VenvySvgaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        svgaParser = initSVGAParse();
        if (!isInstallCache) {
            isInstallCache = true;
            File cacheDir = new File(SVGAParser.getCacheDirectory(context, true), "venvy/svga/");
            try {
                HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
            } catch (IOException e) {
            }
        }
    }

    @TargetApi(21)
    public VenvySvgaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        svgaParser = initSVGAParse();
        if (!isInstallCache) {
            isInstallCache = true;
            File cacheDir = new File(SVGAParser.getCacheDirectory(context, true), "venvy/svga/");
            try {
                HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
            } catch (IOException e) {
            }
        }
    }

    public void setVideoItem(Object object, Object dynamicEntity) {
        if (object instanceof SVGAVideoEntity) {
            if (dynamicEntity != null && dynamicEntity instanceof SVGADynamicEntity) {
                super.setVideoItem((SVGAVideoEntity) object, (SVGADynamicEntity) dynamicEntity);
            }
            super.setVideoItem((SVGAVideoEntity) object);
        }
    }

    public void parse(@NonNull String path, @NonNull SVGAParser.ParseCompletion parseCompletion) {
        if (isNetworkUrl(path)) {
            parseFromNet(path, parseCompletion);
        } else {
            parseFromAsset(path, parseCompletion);
        }
    }

    private void parseFromAsset(String assetName, SVGAParser.ParseCompletion parseCompletion) {
        svgaParser.parse(assetName, parseCompletion);
    }

    private void parseFromNet(String url, SVGAParser.ParseCompletion parseCompletion) {


        try {
            svgaParser.parse(new URL(url), parseCompletion);
        } catch (MalformedURLException e) {
        }
    }

    public static boolean isNetworkUrl(String url) {
        return isHttpUrl(url) || isHttpsUrl(url);
    }

    private static boolean isHttpUrl(String url) {
        return (null != url) &&
                (url.trim().length() > 6) &&
                url.trim().substring(0, 7).equalsIgnoreCase("http://");
    }

    private static boolean isHttpsUrl(String url) {
        return (null != url) &&
                (url.length() > 7) &&
                url.substring(0, 8).equalsIgnoreCase("https://");
    }

    @Override
    public void loadImage(final WeakReference<? extends IImageView> imageView, VenvyImageInfo venvyImageInfo, @android.support.annotation.Nullable final IImageLoaderResult result) {
        if (venvyImageInfo == null || TextUtils.isEmpty(venvyImageInfo.getUrl())) {
            return;
        }
        final String url = venvyImageInfo.getUrl();
        parse(url, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(SVGAVideoEntity videoItem) {
                SVGADrawable drawable = new SVGADrawable(videoItem);
                if (result != null) {
                    result.loadSuccess(imageView, url, new VenvyBitmapInfo(null, drawable));
                }
            }

            @Override
            public void onError() {
                if (result != null) {
                    result.loadFailure(imageView, url, new Exception("svga parse error"));
                }
            }
        });
    }

    @Override
    public void setFillMode(int mode) {
        super.setFillMode(mode == 0 ? FillMode.Forward : FillMode.Backward);
    }

    @Override
    public void preloadImage(Context context, VenvyImageInfo venvyImageInfo, @android.support.annotation.Nullable IImageLoaderResult result) {
        ///// do nothing: 18/3/28
    }

    @Override
    public void parse(String url, final ISvgaParseCompletion parseCompletion) {
        parse(url, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(SVGAVideoEntity videoItem) {
                SVGADrawable drawable = new SVGADrawable(videoItem);
                setImageDrawable(drawable);
                if (parseCompletion != null) {
                    parseCompletion.onComplete(videoItem);
                }
            }

            @Override
            public void onError() {
                if (parseCompletion != null) {
                    parseCompletion.onError();
                }
            }
        });
    }

    @Override
    public void startAnimation(int location, int length, boolean reverse) {
        if (location != -1 && length != -1) {
            startAnimation(new SVGARange(location, length), reverse);
        }
    }

    private SVGAParser initSVGAParse() {
        SVGAParser svgaParser = new SVGAParser(getContext());
        return svgaParser;
    }
}
