package cn.com.venvy.common.router;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;

import cn.com.venvy.Platform;

/**
 * Created by yanjiangbo on 2018/1/24.
 */

public class PostInfo {

    private String scheme;
    private String path;
    private Bundle bundle;
    private ViewGroup targetViewParent;

    PostInfo() {
        bundle = new Bundle();
    }

    PostInfo setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    PostInfo setPath(String path) {
        this.path = path;
        return this;
    }

    public PostInfo withTargetViewParent(ViewGroup viewParent) {
        this.targetViewParent = viewParent;
        return this;
    }

    public PostInfo withTargetPlatform(String key, Platform platform) {
        bundle.putSerializable(key, platform);
        return this;
    }

    public PostInfo withString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public PostInfo withInt(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public PostInfo withBoolean(String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public PostInfo withChar(String key, char value) {
        bundle.putChar(key, value);
        return this;
    }

    public PostInfo withByte(String key, byte value) {
        bundle.putByte(key, value);
        return this;
    }

    public PostInfo withFloat(String key, float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public PostInfo withParcelable(String key, Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    public PostInfo withParcelableArray(String key, Parcelable[] value) {
        bundle.putParcelableArray(key, value);
        return this;
    }

    public PostInfo withParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        bundle.putParcelableArrayList(key, value);
        return this;
    }

    public PostInfo withIntegerArrayList(String key, ArrayList<Integer> value) {
        bundle.putIntegerArrayList(key, value);
        return this;
    }

    public PostInfo withStringArrayList(String key, ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    public PostInfo withCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        bundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public PostInfo withSerializable(String key, Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    public PostInfo withByteArray(String key, byte[] value) {
        bundle.putByteArray(key, value);
        return this;
    }

    public PostInfo withShortArray(String key, short[] value) {
        bundle.putShortArray(key, value);
        return this;
    }

    public PostInfo withCharArray(String key, char[] value) {
        bundle.putCharArray(key, value);
        return this;
    }

    public PostInfo withFloatArray(String key, float[] value) {
        bundle.putFloatArray(key, value);
        return this;
    }

    public PostInfo withCharSequenceArray(String key, CharSequence[] value) {
        bundle.putCharSequenceArray(key, value);
        return this;
    }

    public PostInfo withBundle(String key, Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

    /**
     * 不适用于View的跳转，慎用
     */
    public void navigation() {
        this.navigation(null, null);
    }

    /**
     * 不适用于View的跳转，慎用
     */
    public void navigation(IRouterCallback callback) {
        this.navigation(null, callback);
    }

    public void navigation(Context context) {
        this.navigation(context, null);
    }

    public void navigation(Context context, IRouterCallback callback) {
        Uri uri = new Uri.Builder().scheme(scheme).path(path).build();
        VenvyRouterManager.getInstance().navigation(context, targetViewParent, uri, bundle, callback, 0);
    }
}
