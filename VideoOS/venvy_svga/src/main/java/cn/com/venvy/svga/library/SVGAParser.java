package cn.com.venvy.svga.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.http.HttpResponseCache;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.opensource.svgaplayer.proto.MovieEntity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.os.Environment.MEDIA_MOUNTED;


/**
 * Created by yanjiangbo on 2018/3/27.
 * Done
 */

public class SVGAParser {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private final static Object sharedLock = new Object();
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public interface ParseCompletion {
        void onComplete(SVGAVideoEntity videoItem);

        void onError();
    }

    public interface DownloadResult {
        void complete(InputStream inputStream);

        void failure(Exception e);
    }

    private final Context context;
    private FileDownloader fileDownloader;

    public SVGAParser(Context paramContext) {
        this.context = paramContext;
    }

    public void setFileDownloader(FileDownloader fileDownloader) {
        this.fileDownloader = fileDownloader;
    }

    public void parse(String assetsName, ParseCompletion callback) {
        try {
            InputStream inputStream = context.getAssets().open(assetsName);
            if (inputStream != null) {
                parse(inputStream, cacheKey("file:///assets/" + assetsName), callback);
            }
        } catch (Exception e) {
        }
    }

    public void parse(final URL url, final ParseCompletion callback) {
        final String cacheString = cacheKey(url);
        if (cacheFile(cacheString).exists()) {
            final SVGAVideoEntity videoEntity = parseWithCacheKey(cacheString);
            if (videoEntity != null) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onComplete(videoEntity);
                        }
                    }
                });
                return;
            }
        }
        if (fileDownloader == null) {
            fileDownloader = new FileDownloader();
        }
        fileDownloader.resume(url, new DownloadResult() {
            @Override
            public void complete(InputStream inputStream) {
                final SVGAVideoEntity entity = parse(inputStream, cacheString);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            if (entity != null) {
                                callback.onComplete(entity);
                            } else {
                                callback.onError();
                            }
                        }
                    }
                });
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }

            @Override
            public void failure(Exception e) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError();
                        }
                    }
                });
            }
        });
    }

    public void parse(final InputStream inputStream, final String cacheKey, final ParseCompletion callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final SVGAVideoEntity videoEntity = parse(inputStream, cacheKey);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            if (videoEntity == null) {
                                callback.onError();
                            } else {
                                callback.onComplete(videoEntity);
                            }
                        }
                    }
                });
            }
        }.start();
    }

    private SVGAVideoEntity parse(InputStream inputStream, String cacheKey) {
        try {
            byte[] bytes = readAsBytes(inputStream);
            if (bytes == null) {
                return null;
            }
            if (bytes.length > 4 && bytes[0] == 80 && bytes[1] == 75 && bytes[2] == 3 && bytes[3] == 4) {
                synchronized (sharedLock) {
                    if (!cacheDir(cacheKey).exists()) {
                        unzip(new ByteArrayInputStream(bytes), cacheKey);
                    }
                    return parseWithCacheKey(cacheKey);
                }
            } else {
                byte[] newBytes = inflate(bytes);
                if (newBytes != null) {
                    return new SVGAVideoEntity(MovieEntity.ADAPTER.decode(newBytes), new File(cacheKey));
                }
            }

        } catch (Exception e) {
        }
        return null;
    }

    private SVGAVideoEntity parseWithCacheKey(String cacheKey) {
        synchronized (sharedLock) {
            FileInputStream fileInputStream = null;
            File file = null;
            File movieFile = null;
            try {
                file = cacheDir(cacheKey);
                movieFile = new File(file, "movie.binary");
                if (movieFile.isFile() && movieFile.exists()) {
                    fileInputStream = new FileInputStream(movieFile);
                    return new SVGAVideoEntity(MovieEntity.ADAPTER.decode(fileInputStream), file);
                }
            } catch (Exception e) {
                if (file != null) {
                    file.delete();
                }
                if (movieFile != null) {
                    movieFile.delete();
                }
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (Exception e) {
                }

            }
        }
        return null;
    }

    private File cacheFile(String paramString) {
        return new File(getCacheDirectory(context, true), "venvy/svga/" + paramString + "/movie.binary");
    }

    private File cacheDir(String paramString) {
        return new File(getCacheDirectory(context, true), "venvy/svga/" + paramString + "/");
    }

    private String cacheKey(URL url) {
        if (url == null) {
            return "";
        }
        return cacheKey(url.toString());
    }

    private String cacheKey(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        return MD5(string);
    }

    public static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] readAsBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[2048];
        while (true) {
            int count = inputStream.read(bytes, 0, 2048);
            if (count <= 0) {
                break;
            } else {
                byteArrayOutputStream.write(bytes, 0, count);
            }

        }
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] inflate(byte[] bytes) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(bytes, 0, bytes.length);
            byte[] inflaterBytes = new byte[2048];
            ByteArrayOutputStream inflatedOutputStream = new ByteArrayOutputStream();
            while (true) {
                int count = inflater.inflate(inflaterBytes, 0, 2048);
                if (count <= 0) {
                    break;
                } else {
                    inflatedOutputStream.write(inflaterBytes, 0, count);
                }
            }
            inflater.end();
            return inflatedOutputStream.toByteArray();
        } catch (Exception e) {
        }
        return null;

    }

    private void unzip(InputStream inputStream, String cacheKey) throws IOException {
        File cacheDir = this.cacheDir(cacheKey);
        cacheDir.mkdirs();
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
        while (true) {
            ZipEntry zipItem = zipInputStream.getNextEntry();
            if (zipItem == null) {
                break;
            }
            if (zipItem.getName().contains("/")) {
                continue;
            }
            File file = new File(cacheDir, zipItem.getName());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buff = new byte[2048];
            while (true) {
                int readBytes = zipInputStream.read(buff);
                if (readBytes <= 0) {
                    break;
                }
                fileOutputStream.write(buff, 0, readBytes);
            }
            fileOutputStream.close();
            zipInputStream.closeEntry();
        }
        zipInputStream.close();
    }


    public static class FileDownloader {
        private static boolean noCache = false;

        public void resume(final URL url, final DownloadResult downloadResult) {
            if (url == null) {
                return;
            }
            new Thread(new Runnable() {
                InputStream inputStream;
                ByteArrayOutputStream outputStream;

                @Override
                public void run() {
                    try {
                        if (HttpResponseCache.getInstalled() == null && !noCache) {
                            Log.e("SVGAParser", "SVGAParser can not handle cache before install HttpResponseCache. see https://github.com/yyued/SVGAPlayer-Android#cache");
                            Log.e("SVGAParser", "在配置 HttpResponseCache 前 SVGAParser 无法缓存. 查看 https://github.com/yyued/SVGAPlayer-Android#cache ");
                        }
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(20000);
                        connection.setRequestMethod("GET");
                        connection.connect();
                        inputStream = connection.getInputStream();
                        outputStream = new ByteArrayOutputStream();
                        byte[] bytes = new byte[4096];
                        while (true) {
                            int count = inputStream.read(bytes, 0, 4096);
                            if (count == -1) {
                                break;
                            }
                            outputStream.write(bytes, 0, count);
                        }
                        if (downloadResult != null) {
                            downloadResult.complete(new ByteArrayInputStream(outputStream.toByteArray()));
                        }
                    } catch (Exception e) {
                        if (downloadResult != null) {
                            downloadResult.failure(e);
                        }
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (Exception e) {

                        }
                    }

                }
            }).start();
        }
    }

    private static boolean existSDcard() {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        return MEDIA_MOUNTED.equals(externalStorageState);
    }


    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return appCacheDir;
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && existSDcard() && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            @SuppressLint("SdCardPath") String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    public static Handler getHandler() {

        return sHandler;
    }

    public static Thread getUIThread() {

        return Looper.getMainLooper().getThread();
    }

    public static boolean isOnUIThread() {

        return Thread.currentThread() == getUIThread();
    }

    public static void runOnUIThread(Runnable action) {

        if (!isOnUIThread()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

}
