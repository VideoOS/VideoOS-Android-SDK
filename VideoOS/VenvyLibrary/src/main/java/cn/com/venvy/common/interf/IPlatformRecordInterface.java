package cn.com.venvy.common.interf;

/**
 * Created by lgf on 2020/2/26.
 */

public interface IPlatformRecordInterface {
    void startRecord();
    void endRecord(RecordCallback loginCallback);
    interface RecordCallback {
        void onRecordResult(byte[] data);
        void onRecordResult(String filePath);
    }
}
