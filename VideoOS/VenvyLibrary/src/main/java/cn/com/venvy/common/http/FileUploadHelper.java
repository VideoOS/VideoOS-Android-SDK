package cn.com.venvy.common.http;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;
import cn.com.venvy.common.utils.VenvyLog;

public class FileUploadHelper {

    /**
     * 字符编码格式
     */
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String BOUNDARY = "----------" + System.currentTimeMillis();
    /**
     * 请求的内容类型
     */
    private static final String PROTOCOL_CONTENT_TYPE = "multipart/form-data; boundary=" + BOUNDARY;

    /**
     * 多个文件间的间隔
     */
    private static final String FILEINTERVAL = "\r\n";

    @WorkerThread
    public String upload(@NonNull PostFormRequest postFormRequest) {
        if (TextUtils.isEmpty(postFormRequest.url) || postFormRequest.mFileInfos.isEmpty()) {
            throw new IllegalArgumentException("you must make sure mPostFormRequest.url is not null or mFileInfos is not empty");
        }
        postFormRequest.url = BaseRequestConnect.parseUrl(postFormRequest.url);
        HttpURLConnection connection = createConnection(postFormRequest.url);
        if (connection == null) {
            return null;
        }
        addHeaders(connection, postFormRequest.mHeaders);
        addFiles(connection, postFormRequest.mFileInfos);
        return getResponseFromService(connection);
    }

    public void uploadAsync(@NonNull final PostFormRequest postFormRequest,
                            @Nullable final IUploadListener uploadListener) {

        VenvyAsyncTaskUtil.doAsyncTask("upload",
                new VenvyAsyncTaskUtil.IDoAsyncTask<Void, String>() {

                    @Override
                    public String doAsyncTask(Void... voids) throws Exception {
                        return upload(postFormRequest);
                    }
                },
                new VenvyAsyncTaskUtil.IAsyncCallback<String>() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(String data) {
                        if (uploadListener != null) {
                            uploadListener.uploadComplete(data);
                        }
                    }

                    @Override
                    public void onCancelled() {

                    }

                    @Override
                    public void onException(Exception ie) {

                    }
                });
    }

    public void onDestroy() {
        VenvyAsyncTaskUtil.cancel("upload");
    }

    /**
     * 若是文件列表不为空，则将文件列表上传。
     *
     * @param connection
     */
    private void addFiles(HttpURLConnection connection, List<PostFormRequest.FileInfo> fileInfos) {
        if (fileInfos == null || fileInfos.isEmpty()) {
            return;
        }

        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(connection.getOutputStream());

            int i = 1;
            for (PostFormRequest.FileInfo fileInfo : fileInfos) {
                byte[] contentHeader = getFileHead(fileInfo);
                dataOutputStream.write(contentHeader, 0, contentHeader.length);
                readFileData(fileInfo.file, dataOutputStream);
                //添加文件间的间隔，若是一个文件则不用添加间隔。若是多个文件时，最后一个文件不用添加间隔。
                if (fileInfos.size() > 1 && i < fileInfos.size()) {
                    i++;
                    dataOutputStream.write(FILEINTERVAL.getBytes(PROTOCOL_CHARSET));
                }
            }
            //写入文件的尾部格式
            byte[] contentFoot = getFileFoot();
            dataOutputStream.write(contentFoot, 0, contentFoot.length);
            //刷新数据到流中
            dataOutputStream.flush();
        } catch (Exception e) {
            VenvyLog.i("FileUpload", e.getMessage());
        }
    }


    private void addHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (String headerName : headers.keySet()) {
            connection.addRequestProperty(headerName, headers.get(headerName));
        }
    }

    /**
     * 获取从服务器相应的数据
     *
     * @param connection
     * @return
     */
    private String getResponseFromService(HttpURLConnection connection) {
        String responeContent = "";
        BufferedReader bufferedReader = null;
        try {
            if (connection != null) {
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                    responeContent = builder.toString();
                }
            }

        } catch (Exception e) {
            responeContent = "";
            VenvyLog.i("FileUpload", "upload file res");
        } finally {
            VenvyIOUtils.close(bufferedReader);
            VenvyIOUtils.close(connection);
        }
        return responeContent;
    }

    /**
     * 将file数据写入流中
     *
     * @param file
     * @param outputStream
     */

    public void readFileData(File file, OutputStream outputStream) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
            }
        } catch (Exception e) {
            VenvyLog.i("FileUploadHelper", "read File error");
        } finally {
            VenvyIOUtils.close(fileInputStream);
        }
    }

    /**
     * 创建和设置HttpUrlConnection的内容格式为文件上传格式
     *
     * @param url
     * @return
     */
    private HttpURLConnection createConnection(String url) {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            //设置请求方式为post
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            //设置不使用缓存
            httpURLConnection.setUseCaches(false);
            //设置数据字符编码格式
            httpURLConnection.setRequestProperty("Charsert", PROTOCOL_CHARSET);
            //设置内容上传类型（multipart/form-data），这步是关键
            httpURLConnection.setRequestProperty("Content-Type", PROTOCOL_CONTENT_TYPE);
        } catch (Exception e) {
            VenvyLog.i("FileUploadHelper", url + " open HttpUrlConnection error");
        }
        return httpURLConnection;
    }


    /**
     * 获取到文件的head
     *
     * @return
     */
    private byte[] getFileHead(PostFormRequest.FileInfo fileInfo) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("--");
            builder.append(BOUNDARY);
            builder.append("\r\n");
            builder.append("Content-Disposition: form-data;");
            builder.append("name=\"");
            builder.append(fileInfo.name);
            builder.append("\";filename=\"");
            builder.append(fileInfo.fileName);
            builder.append("\"\r\n");
            builder.append("Content-Type:application/octet-stream\r\n\r\n");
            String s = builder.toString();
            return s.getBytes(PROTOCOL_CHARSET);
        } catch (Exception e) {
            VenvyLog.i("FileUploadHelper", " getFileHead error " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取文件的foot
     *
     * @return
     */
    private byte[] getFileFoot() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("\r\n--");
            builder.append(BOUNDARY);
            builder.append("--\r\n");
            String s = builder.toString();
            return s.getBytes(PROTOCOL_CHARSET);
        } catch (Exception e) {
            VenvyLog.i("FileUploadHelper", " getFileFoot error " + e.getMessage());
        }

        return null;
    }

    public interface IUploadListener {
        void uploadComplete(String reponseData);
    }
}
