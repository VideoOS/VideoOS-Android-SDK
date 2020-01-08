package cn.com.venvy.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.common.permission.PermissionCheckHelper;

public class VenvyFileUtil {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    public static final char EXTENSION_SEPARATOR = '.';

    /***
     * 判断文件是否存在
     *
     * @param filePath
     */
    public static boolean isExistFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File mFrameworkFile = new File(filePath);
        return mFrameworkFile.exists();
    }

    public static String getFileNameByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return url.substring(url.lastIndexOf("/") + 1, url.length());

    }

    public static boolean createDir(String destDirName) {// 创建目录
        File dir = new File(destDirName);
        if (dir.exists()) { // 判断目录是否存在
            return false;
        }
        if (!destDirName.endsWith(File.separator)) { // 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) { // 创建目标目录
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getFileName(String path) {
        List<String> nameArray = new ArrayList<>();
        File file = new File(path);
        // 如果这个路径是文件夹
        if (file.isDirectory()) {
            // 获取路径下的所有文件
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 如果还是文件夹 递归获取里面的文件 文件夹
                if (files[i].isDirectory()) {
                    continue;
                } else {
                    nameArray.add(files[i].getAbsolutePath());
                }

            }
        }
        return nameArray;
    }

    /**
     * @param context context
     * @return cache 目录
     */
    @Deprecated
    public static String getCacheDir(Context context) {
        return context.getApplicationContext().getCacheDir().getAbsolutePath() + "/venvy";
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && VenvyDeviceUtil.existSDcard() && hasExternalStoragePermission(context)) {
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

    /**
     * @param context context
     * @return file 目录
     */
    public static String getCachePath(Context context) {
        return getCacheDirectory(context, true).getAbsolutePath();
    }

    /***
     * 读取文件数据
     *
     * @param filePath 文件名
     * @return 文件数据
     * @throws IOException
     *             mContext.getCacheDir() .getAbsolutePath()
     */
    public static String readFile(final Context context, @NonNull final String filePath, PermissionCheckHelper.PermissionCallbackListener callbackListener) {
        if (VenvyAPIUtil.isSupport(23) && !PermissionCheckHelper.isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (PermissionCheckHelper.instance().isRequesting()) {
                callbackListener.onPermissionCheckCallback(-1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new int[]{PackageManager.PERMISSION_DENIED});
                return null;
            }
            PermissionCheckHelper.instance().requestPermissions(context, 201, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, new String[]{
                    "外部文件读取权限"
            }, callbackListener == null ? new PermissionCheckHelper.PermissionCallbackListener() {
                @Override
                public void onPermissionCheckCallback(int requestCode, String[] permissions, int[] grantResults) {

                }
            } : callbackListener);
            return null;
        }
        return readFormFile(context, filePath);
    }

    public static String readFormFile(Context context, @NonNull String filePath) {
        StringBuilder res = new StringBuilder();
        FileInputStream fin = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                fin = new FileInputStream(file);
                InputStreamReader inputReader = new InputStreamReader(fin);
                BufferedReader buffReader = new BufferedReader(inputReader);
                String line;
                // 分行读取
                while ((line = buffReader.readLine()) != null) {
                    res.append(line);
                }
                fin.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VenvyIOUtils.close(fin);
        }
        return res.toString();
    }

    public static void writeToFile(Context context, @NonNull String filePath, String content, boolean append) {
        File file = new File(filePath);
        OutputStreamWriter osw = null;
        try {
            if (!file.exists()) {
                VenvyLog.i("---createNewFile---");
                file.createNewFile();
                return;
            }
            if (!file.exists()) {
                VenvyLog.d("Error: file is not exists!");
                return;
            }
            osw = new OutputStreamWriter(new FileOutputStream(filePath, append), "utf-8");
            try {
                osw.write(content);
                osw.flush();
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            VenvyIOUtils.close(osw);
        }
    }

    public static void writeToFile(Context context, @NonNull String filePath, String content) {
        writeToFile(context, filePath, content, false);

    }

    public static void deleteFile(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        delFolder(filePath);
    }

    /**
     * 获取中插缓存的 path
     *
     * @param context
     * @return
     */
    public static String getMediaCachePath(Context context) {
        if (context == null) {
            return null;
        }
        return getCachePath(context) + "/media/";
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            File myFilePath = new File(folderPath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径 ,"Z:/xuyun/save"
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * 重命名文件
     *
     * @param oldName 原文件名路径
     * @param newName 新文件名路径
     */
    public static void renameFile(String oldName, String newName) {
        if (!TextUtils.equals(oldName, newName)) {
            File oldFile = new File(oldName);
            if (!oldFile.exists()) {
                return;
            }

            File newFile = new File(newName);
            if (!newFile.exists()) {
                oldFile.renameTo(newFile);
            }
        }
    }


    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    /**
     * copy a input stream to given filepath
     *
     * @param input
     * @param filePath
     * @return
     */
    public static boolean copy(final InputStream input, final String filePath) {
        final int bufSize = 8 * 1024;// or other buffer size
        boolean result = false;
        File file = createFile(filePath);
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file), bufSize);
            byte[] buffer = new byte[bufSize];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            VenvyIOUtils.flush(output);
            VenvyIOUtils.close(output);
        }

        return result;
    }

    public static void copyDir(String sourcePath, String newPath) throws IOException {

        File file = new File(sourcePath);

        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {

            (new File(newPath)).mkdir();

        }

        for (int i = 0; i < filePath.length; i++) {

            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {

                copyDir(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);

            }

            if (new File(sourcePath + file.separator + filePath[i]).isFile()) {

                copyFile(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);

            }

        }
    }

    public static void copyFile(String oldPath, String newPath) throws IOException {

        File oldFile = new File(oldPath);

        File file = new File(newPath);

        FileInputStream in = new FileInputStream(oldFile);

        FileOutputStream out = new FileOutputStream(file);

        byte[] buffer = new byte[2097152];
        int readByte = 0;
        while ((readByte = in.read(buffer)) != -1) {
            out.write(buffer, 0, readByte);
        }
    }

    /***
     *
     * @param context
     * @param assetsPath 文件目录
     * @param savePath 目标路径
     * @return
     */
    public static boolean copyFilesFromAssets(Context context, String assetsPath, String savePath) {
        boolean result = false;
        try {
            String fileNames[] = context.getAssets().list(assetsPath);// 获取assets目录下的所有文件及目录名
            if (fileNames == null || fileNames.length <= 0) {
                return result;
            }
            for (String fileName : fileNames) {
                result = copy(context.getAssets().open(assetsPath + "/" + fileName), savePath + "/" + fileName);
                if (!result) {
                    break;
                }
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * crate file with given path and file name
     *
     * @param fullpath
     * @param fullpath
     * @return
     */
    public static File createFile(final String fullpath) {
        File file = new File(fullpath);
        if (file.exists()) {
            return file;
        } else {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            String fileName = file.getName();
            return new File(parent, fileName);
        }
    }

    public static byte[] readBytes(File file) {
        if (file != null && file.exists() && file.isFile()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                return VenvyIOUtils.toBytes(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                VenvyIOUtils.close(inputStream);
            }
        }
        return null;
    }

    /**
     * 获取apk的包名
     *
     * @return
     */
    public static String getPackageNameByApkFile(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";

        PackageManager packageManager = context.getPackageManager();
        PackageInfo pi = packageManager.getPackageArchiveInfo(filePath, 0);
        return pi != null ? pi.applicationInfo != null ? pi.applicationInfo.packageName : "" : "";
    }


    /**
     * 获取APK图标
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static Drawable getApkIcon(Context context, String apkPath) {

        PackageManager packageManager = context.getPackageManager();
        PackageInfo pi = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);

        if (pi != null) {
            ApplicationInfo appInfo = pi.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(packageManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取APK名称
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getApkLabel(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo pi = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);

        if (pi != null) {
            ApplicationInfo appInfo = pi.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadLabel(packageManager).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
