/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;

/**
 * 文件操作类
 *
 * @author song
 * @date 15/11/9
 */
public class FileUtil {

    /**
     * is a file path contains folder path
     *
     * @param filePath
     * @param folderPath
     * @return
     */
    public static boolean isContainsFolderPath(String filePath, String folderPath) {//TODO ../../目录处理
        if (filePath != null && folderPath != null) {//filePath本身是folder，并且包含folderPath
            if (folderPath.charAt(folderPath.length() - 1) == '/') {//本身是路径
                return filePath.startsWith(folderPath);
            } else {//非路径的话需要判断路径
                return filePath.startsWith(folderPath + "/");
            }
        }
        return false;
    }


    /**
     * build a file path
     *
     * @param basePath
     * @param nameOrPath
     * @return
     */
    public static String buildPath(String basePath, String nameOrPath) {
        if (!TextUtils.isEmpty(basePath)) {
            return new StringBuffer().append(basePath).append(basePath.endsWith(File.separator) ? "" : File.separator).append(nameOrPath).toString();
        } else {
            return nameOrPath;
        }
    }

    /**
     * 是否给定的名称是以postfix结尾的名字
     *
     * @param fileName
     * @param posfix
     * @return
     */
    public static boolean isSuffix(String fileName, String posfix) {
        return !TextUtils.isEmpty(fileName) && posfix != null && fileName.endsWith(posfix);
    }

    /**
     * 是否有后缀
     *
     * @param fileName
     * @return
     */
    public static boolean hasPostfix(String fileName) {
        return fileName != null && fileName.lastIndexOf('.') != -1;
    }

    /**
     * 得到Asset的目录路径
     *
     * @param assetFilePath
     * @return
     */
    public static String getAssetFolderPath(String assetFilePath) {
        return assetFilePath != null && assetFilePath.lastIndexOf(File.separatorChar) != -1 ?
                assetFilePath.substring(0, assetFilePath.lastIndexOf(File.separatorChar)) :
                "";
    }

    /**
     * get filepath
     *
     * @param filepath
     * @return
     */
    public static String getCanonicalPath(String filepath) {
        if (filepath != null) {
            if (filepath.contains("../")) {
                try {
                    return new File(filepath).getCanonicalPath();
                } catch (IOException e) {
                }
            } else {
                return filepath;
            }
        }
        return null;
    }

    /**
     * 不包含父路径，有父路径的话则去掉父路径
     *
     * @param nameOrPath
     * @return
     */
    public static String getSecurityFileName(String nameOrPath) {
        if (nameOrPath != null) {
            if (nameOrPath.contains("../")) {
                int index = nameOrPath.lastIndexOf("../");
                if (index != -1) {
                    return nameOrPath.substring(index + 4);
                }
            }
        }
        return nameOrPath;
    }


    /**
     * save data to a file
     *
     * @param path file path with file name
     * @param data data to saved
     */
    public static boolean save(String path, byte[] data) {
        if (!TextUtils.isEmpty(path) && data != null && data.length > 0) {
            FileOutputStream out = null;
            try {
                File destFile = VenvyFileUtil.createFile(path);
                out = new FileOutputStream(destFile);
                out.write(data);
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                VenvyIOUtils.flush(out);
                VenvyIOUtils.close(out);
            }
        }
        return false;
    }

    /**
     * open a file
     *
     * @param filePath
     * @return
     */
    public static InputStream open(String filePath) {
        try {
            if (!TextUtils.isEmpty(filePath)) {
                return new FileInputStream(filePath);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
