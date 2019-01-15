package cn.com.venvy.common.image.scanner.model;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.image.scanner.bean.ImageFolderBean;
import cn.com.venvy.common.image.scanner.interf.IImageMediaCallback;
import cn.com.venvy.common.image.scanner.loader.ImageScanner;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by mac on 18/2/24.
 */

public class ImageModel implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private IImageMediaCallback mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        ImageFolderBean imageFolderBean = args.getParcelable(ARGS_ALBUM);
        if (imageFolderBean == null) {
            VenvyLog.i("--imageFolderBean is null--");
            return null;
        }

        return ImageScanner.newInstance(context, imageFolderBean);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onImageLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onImageReset();
    }

    public void onCreate(@NonNull Context context, @NonNull IImageMediaCallback callbacks) {
        mContext = new WeakReference<Context>(context);
        mLoaderManager = ((Activity) context).getLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mLoaderManager = null;
        mCallbacks = null;
    }


    public void load(@NonNull ImageFolderBean target) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }
}
