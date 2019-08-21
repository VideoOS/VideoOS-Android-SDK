package both.video.venvy.com.appdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Create by bolo on 06/06/2018
 */
public class OsConfigureBean implements Parcelable {
    public List<String> userIdList;
    public List<String> platformIdList;
    public List<String> creativeNameList;//素材名称集合
    public List<String> roomIdList;
    public List<String> cateList;

    public int debugStatus;

    public OsConfigureBean() {
    }

    protected OsConfigureBean(Parcel in) {
        userIdList = in.createStringArrayList();
        platformIdList = in.createStringArrayList();
        roomIdList = in.createStringArrayList();
        cateList = in.createStringArrayList();
        creativeNameList = in.createStringArrayList();
        debugStatus = in.readInt();
    }

    public static final Creator<OsConfigureBean> CREATOR = new Creator<OsConfigureBean>() {
        @Override
        public OsConfigureBean createFromParcel(Parcel in) {
            return new OsConfigureBean(in);
        }

        @Override
        public OsConfigureBean[] newArray(int size) {
            return new OsConfigureBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(userIdList);
        dest.writeStringList(platformIdList);
        dest.writeStringList(roomIdList);
        dest.writeStringList(cateList);
        dest.writeStringList(creativeNameList);
        dest.writeInt(debugStatus);
    }
}
