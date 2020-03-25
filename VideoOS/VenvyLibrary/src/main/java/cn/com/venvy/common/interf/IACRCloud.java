package cn.com.venvy.common.interf;

import cn.com.venvy.common.bean.AcrConfigInfo;

/**
 * Created by lgf on 2020/2/24.
 */

public interface IACRCloud {
    /***
     * 识别存储在buffer参数中的音频内容。
     音频格式：RIFF，PCM，16位，单声道8000 Hz。
     它不再开始录制过程。
     这是一个同步功能，它将等待直到结果返回。
     由于“ Android UI主线程”无法发送网络请求，因此必须在子线程中调用此函数。
     * @param buffer
     */
    void startRecognize(AcrConfigInfo info, byte[] buffer);

    /***
     * 此功能将立即取消识别。
     */
    void stopRecognize();

    /***
     * 当不再使用ACRCloudClient实例时，请调用此函数以释放系统资源。
     注意：对于本地（脱机）识别，忘记调用此函数将导致内存泄漏。
     */
    void destroyRecognize();
}
