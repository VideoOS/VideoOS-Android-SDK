package cn.com.venvy.common.image.scanner.bean;


import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import cn.com.venvy.common.image.scanner.loader.ImageFolderScanner;

/**
 * 某路径下图片的数量，第一个图片的路径、和 图片所在的路径
 * Created by mac on 17/12/12.
 */

public class ImageFolderBean implements Parcelable {
    public static final Creator<ImageFolderBean> CREATOR = new Creator<ImageFolderBean>() {
        @Nullable
        @Override
        public ImageFolderBean createFromParcel(Parcel source) {
            return new ImageFolderBean(source);
        }

        @Override
        public ImageFolderBean[] newArray(int size) {
            return new ImageFolderBean[size];
        }
    };


    ImageFolderBean(Parcel source) {
        floderId = source.readString();
        firstImagePath = source.readString();
        floderName = source.readString();
        count = source.readInt();
    }

    public static ImageFolderBean valueOf(Cursor cursor) {
        return new ImageFolderBean(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getInt(cursor.getColumnIndex(ImageFolderScanner.COLUMN_COUNT)));
    }


    ImageFolderBean(String id, String coverPath, String albumName, int count) {
        floderId = id;
        firstImagePath = coverPath;
        floderName = albumName;
        this.count = count;
    }

    /**
     * 某文件夹下第一张图片的路径
     */
    public String firstImagePath;

    /**
     * 文件夹的名称
     */
    public String floderName;

    public String floderId;

    /**
     * 图片的数量
     */
    public int count;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(floderId);
        dest.writeString(firstImagePath);
        dest.writeString(floderName);
        dest.writeInt(count);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ImageFolderBean)) {
            return false;
        }
        ImageFolderBean imageFolderBean = (ImageFolderBean) o;
        return TextUtils.equals(imageFolderBean.floderId, floderId);
    }
}
