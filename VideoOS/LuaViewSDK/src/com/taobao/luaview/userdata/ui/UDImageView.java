/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.util.DrawableUtil;
import com.taobao.luaview.util.ImageUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.image.IImageLoaderResult;
import cn.com.venvy.common.image.IImageSizeResult;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.image.VenvyImageLoaderFactory;
import cn.com.venvy.common.utils.VenvyBlurUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Image 数据封装
 *
 * @param <T>
 * @author song
 */
public class UDImageView<T extends BaseImageView> extends UDView<T> {
    AnimationDrawable mFrameAnimation;

    public UDImageView(T view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setCornerRadius(float radius) {
        final T view = getView();
        if (view != null) {
            view.setCornerRadius(radius);
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public float getCornerRadius() {
        final T view = getView();
        if (view != null) {
            return view.getCornerRadius();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setBorderWidth(final int borderWidth) {
        final T view = getView();
        if (view != null) {
            view.setStrokeWidth(borderWidth);
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public int getBorderWidth() {
        T view = getView();
        if (view != null) {
            return view.getStrokeWidth();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setBorderColor(Integer borderColor) {
        if (borderColor != null) {
            T view = getView();
            if (view != null) {
                view.setStrokeColor(borderColor);
            }
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public int getBorderColor() {
        T view = getView();
        if (view != null) {
            return view.getStrokeColor();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public UDView setBorderDashSize(float dashWidth, float dashGap) {
        T view = getView();
        if (view != null) {
            view.setBorderDash(dashWidth, dashGap);
        }
        return this;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public float getBorderDashWidth() {
        T view = getView();
        if (view != null) {
            return view.getBorderDashWidth();
        }
        return 0;
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = {"之前写在View无效果，5.4.0开始，Image支持该方法"})
    @Override
    public float getBorderDashGap() {
        T view = getView();
        if (view != null) {
            return view.getBorderDashGap();
        }
        return 0;
    }

    /**
     * set data
     *
     * @param data
     * @return
     */
    public UDImageView setPlaceHolderBytes(final byte[] data) {
        if (data != null) {
            final T imageView = getView();
            if (imageView != null) {
                new SimpleTask1<Drawable>() {
                    @Override
                    protected Drawable doInBackground(Object... params) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        return new BitmapDrawable(bitmap);
                    }

                    @Override
                    protected void onPostExecute(Drawable drawable) {//TODO 这里的bitmap是不经过缓存的，需要考虑
                        if (drawable != null) {
                            imageView.setPlaceHolderDrawable(drawable);
                        }
                    }
                }.executeInPool();
            }
        }
        return this;
    }

    public UDImageView setImageBytes(final byte[] data) {
        if (data != null) {
            final T imageView = getView();
            if (imageView != null) {
                new SimpleTask1<Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Object... params) {
                        return BitmapFactory.decodeByteArray(data, 0, data.length);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {//TODO 这里的bitmap是不经过缓存的，需要考虑
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }.executeInPool();
            }
        }
        return this;
    }

    public UDImageView setImageBlurBytes(final byte[] data, final int blur) {
        if (data != null) {
            final T imageView = getView();
            if (imageView != null) {
                new SimpleTask1<Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Object... params) {
                        return BitmapFactory.decodeByteArray(data, 0, data.length);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {//TODO 这里的bitmap是不经过缓存的，需要考虑
                        if (bitmap != null) {
                            imageView.setImageBitmap(VenvyBlurUtil.doBlur(bitmap, 10, blur));
                        }
                    }
                }.executeInPool();
            }
        }
        return this;
    }

    /**
     * 设置图片
     *
     * @param urlOrName
     * @param callback
     * @return
     */
    public UDImageView setImageUrl(final String urlOrName, final LuaFunction callback) {
        final T imageView = getView();
        if (imageView != null) {
            imageView.setLuaFunction(callback);
            if (!TextUtils.isEmpty(urlOrName)) {
                if (URLUtil.isNetworkUrl(urlOrName)) {//network
                    imageView.setTag(Constants.RES_LV_TAG_URL, urlOrName);//需要设置tag，防止callback在回调的时候调用错误
                    imageView.setIsNetworkMode(true);
                    imageView.loadUrl(urlOrName, callback == null ? null : new DrawableLoadCallback() {
                        @Override
                        public void onLoadResult(final Drawable drawable) {
                            if (callback == null) {
                                return;
                            }
                            if (imageView.getIImageSize() == null) {
                                if (urlOrName.equals(imageView.getTag(Constants.RES_LV_TAG_URL))) {//异步回调，需要checktag
                                    LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE, drawable != null ? drawable.getIntrinsicWidth() : 0, drawable != null ? drawable.getIntrinsicHeight() : 0);
                                }
                                return;
                            }
                            imageView.getIImageSize().sizeImage(imageView.getContext(), urlOrName, new IImageSizeResult() {
                                @Override
                                public void loadSuccess(String url, @Nullable VenvyBitmapInfo bitmap) {
                                    int width = 0, height = 0;
                                    if (bitmap != null && bitmap.getBitmap() != null) {
                                        width = bitmap.getBitmap().getWidth();
                                        height = bitmap.getBitmap().getHeight();
                                    }
                                    if (urlOrName.equals(imageView.getTag(Constants.RES_LV_TAG_URL))) {//异步回调，需要checktag
                                        LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE, width, height);
                                    }
                                }

                                @Override
                                public void loadFailure(String url, @Nullable Exception e) {

                                }
                            });
                        }
                    });
                } else {
                    imageView.setIsNetworkMode(false);
                    imageView.setTag(Constants.RES_LV_TAG_URL, null);
                    imageView.setUrl(urlOrName);
                    Drawable drawable = null;
                    if (getLuaResourceFinder() != null) {
                        drawable = getLuaResourceFinder().findDrawable(urlOrName);
                        imageView.setImageDrawable(drawable);
                    }
                    if (callback != null) {//本地图片直接调用callback
                        LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                    }
                }
            } else {//设置null
                imageView.setIsNetworkMode(false);
                imageView.loadUrl(null, null);//如果不设置null是否可以被调用 TODO
                if (callback != null) {//本地图片直接调用callback
                    LuaUtil.callFunction(callback, LuaBoolean.TRUE);
                }
            }
        }
        return this;
    }

    public UDImageView setImageBlurUrl(final String urlOrName, final int blur, final LuaFunction callback) {
        final T imageView = getView();
        if (imageView != null) {
            if (!TextUtils.isEmpty(urlOrName)) {
                if (URLUtil.isNetworkUrl(urlOrName)) {//network
                    imageView.setTag(Constants.RES_LV_TAG_URL, urlOrName);//需要设置tag，防止callback在回调的时候调用错误
                    imageView.setIsNetworkMode(true);
                    VenvyImageLoaderFactory.getImageLoader().loadImage(new WeakReference<>(imageView), new VenvyImageInfo.Builder().setUrl(urlOrName).build(), new IImageLoaderResult() {
                        @Override
                        public void loadSuccess(@Nullable WeakReference<? extends IImageView> view, String url, @Nullable VenvyBitmapInfo bitmap) {
                            Drawable drawable = null;
                            if (bitmap != null && bitmap.getDrawable() != null) {
                                drawable = bitmap.getDrawable();
                            } else if (bitmap != null && bitmap.getBitmap() != null) {
                                drawable = new BitmapDrawable(bitmap.getBitmap());
                            }
                            if (drawable != null) {
                                final Drawable needDrawable = drawable;
                                VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(VenvyBlurUtil.doBlur(DrawableUtil.drawableToBitmap(needDrawable), 10, blur));
                                    }
                                }, 80);
                            }
                        }

                        @Override
                        public void loadFailure(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable Exception e) {
                            if (e != null && !TextUtils.isEmpty(url)) {
                                VenvyLog.e("errorImage", "---glide图片加载失败--,url==" + url + (e != null ? " ," +
                                        "exception==" + e.toString() : ""));
                            }
                        }
                    });
//                    imageView.loadUrl(urlOrName, new DrawableLoadCallback() {
//                        @Override
//                        public void onLoadResult(final Drawable drawable) {
//                            Log.i("video++","===data==222="+String.valueOf(drawable==null));
//                            if (drawable != null) {
//                                imageView.setImageBitmap(VenvyBlurUtil.doBlur(DrawableUtil.drawableToBitmap(drawable), 10, blur));
//                            }
//                            if (callback != null && urlOrName.equals(imageView.getTag(Constants.RES_LV_TAG_URL))) {//异步回调，需要checktag
//                                LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
//                            }
//                        }
//                    });
                } else {
                    imageView.setIsNetworkMode(false);
                    imageView.setTag(Constants.RES_LV_TAG_URL, null);
                    imageView.setUrl(urlOrName);
                    Drawable drawable = null;
                    if (getLuaResourceFinder() != null) {
                        drawable = getLuaResourceFinder().findDrawable(urlOrName);
                        imageView.setImageBitmap(VenvyBlurUtil.doBlur(DrawableUtil.drawableToBitmap(drawable), 10, blur));
                    }
                    if (callback != null) {//本地图片直接调用callback
                        LuaUtil.callFunction(callback, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                    }
                }
            } else {//设置null
                imageView.setIsNetworkMode(false);
                imageView.loadUrl(null, null);//如果不设置null是否可以被调用 TODO
                if (callback != null) {//本地图片直接调用callback
                    LuaUtil.callFunction(callback, LuaBoolean.TRUE);
                }
            }
        }
        return this;
    }

    /**
     * 获取图片url
     *
     * @return
     */
    public String getImageUrl() {
        return getView() != null ? getView().getUrl() : "";
    }

    public String getPlaceHolderImage() {
        return getView() != null ? getView().getPlaceHolderImg() : "";
    }

    public UDImageView setPlaceHolderImage(final String placeHolderImage, final LuaFunction callback) {
        final T imageView = getView();
        if (imageView != null) {
            if (!TextUtils.isEmpty(placeHolderImage)) {
                imageView.setPlaceHolderImg(placeHolderImage);

                Drawable drawable = null;
                if (getLuaResourceFinder() != null) {
                    drawable = getLuaResourceFinder().findDrawable(placeHolderImage);
                    imageView.setPlaceHolderDrawable(drawable);
                }
            }
        }
        return this;
    }

    /**
     * 设置图片缩放模式
     *
     * @param scaleType
     * @return
     */
    public UDImageView setScaleType(ImageView.ScaleType scaleType) {
        T view = getView();
        if (view != null) {
            view.setScaleType(scaleType);
        }
        return this;
    }

    /**
     * 获取图片的scale type，
     *
     * @return
     */
    public String getScaleType() {
        return getView() != null ? getView().getScaleType().name() : ImageView.ScaleType.FIT_XY.name();
    }

    /**
     * 开始帧动画(目前只支持本地动画)
     *
     * @param images
     * @param duration
     * @return
     */
    public UDImageView startAnimationImages(String[] images, int duration, boolean repeat) {
        T view = getView();
        if (view != null) {
            Drawable[] frames = null;
            if (images != null && images.length > 0) {
                if (getLuaResourceFinder() != null) {
                    frames = new Drawable[images.length];
                    for (int i = 0; i < images.length; i++) {
                        frames[i] = getLuaResourceFinder().findDrawable(images[i]);
                    }
                }
                if (frames != null && frames.length > 0) {
                    mFrameAnimation = new AnimationDrawable();
                    try {
                        for (Drawable frame : frames) {
                            mFrameAnimation.addFrame(frame, duration);
                        }
                    } catch (Throwable e) {
                    }
                    mFrameAnimation.setOneShot(!repeat);
                    LuaViewUtil.setBackground(view, mFrameAnimation);
                    mFrameAnimation.setVisible(true, true);
                    mFrameAnimation.start();
                }
            }
        }
        return this;
    }

    /**
     * 停止帧动画
     *
     * @return
     */
    public UDImageView stopAnimationImages() {
        if (mFrameAnimation != null) {
            mFrameAnimation.stop();
            mFrameAnimation = null;
        }
        return this;
    }

    /**
     * 是否在播放帧动画
     *
     * @return
     */
    public boolean isAnimationImages() {
        return mFrameAnimation != null && mFrameAnimation.isRunning();
    }

    /**
     * 调整图片大小
     *
     * @return
     */
    @Override
    public UDImageView adjustSize() {
        T view = getView();
        if (view != null) {
            ImageUtil.adjustSize(view.getImageView());
        }
        return this;
    }
}