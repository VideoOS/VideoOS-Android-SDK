

package com.taobao.luaview.scriptbundle.asynctask.delegate;



import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ScriptBundleDownloadDelegate {
     String url;

    public ScriptBundleDownloadDelegate(String url, String sha256) {
        this.url = url;
    }



    /**
     * create HttpURLConnection
     *
     */
    public HttpURLConnection createHttpUrlConnection() {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();

            return connection;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * download as Stream
     *
     */
    public InputStream downloadAsStream(HttpURLConnection connection) {
        try {
            if (connection != null) {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                return new BufferedInputStream(connection.getInputStream());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }



}
