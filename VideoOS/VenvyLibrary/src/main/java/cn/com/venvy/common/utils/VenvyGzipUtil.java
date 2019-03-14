package cn.com.venvy.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by yanjiangbo on 2017/5/18.
 */

public class VenvyGzipUtil {

    // 压缩
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return VenvyBase64.encode(out.toByteArray());
    }

    public static long unzipFile(String inPath, String outPath, boolean isCopy) throws Exception {
        File input = new File(inPath);
        File output = new File(outPath);
        if (!output.exists()) {
            if (!output.mkdirs()) {
                VenvyLog.e("Failed to make directories:" + output.getAbsolutePath());
                return 0;
            }
        }
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(input);
            entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                File destination = new File(output, entry.getName());
                if (!destination.getParentFile().exists()) {
                    destination.getParentFile().mkdirs();
                }
                FileOutputStream outStream = new FileOutputStream(destination);
                extractedSize += copy(zip.getInputStream(entry), outStream);
                outStream.close();
                if (entry.getName().endsWith("zip")) {
                    unzipFile(destination.getAbsolutePath(), outPath, false);
                    continue;
                }
                if (isCopy) {
                    VenvyFileUtil.copyDir(destination.getParentFile().getAbsolutePath(), outPath);
                }
//
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                zip.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return extractedSize;
    }

    private static int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 2];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 2);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 2);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 2)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            VenvyIOUtils.close(in);
            VenvyIOUtils.close(out);
        }
        return count;
    }

    /**
     * 解压缩
     *
     * @param compressedStr
     * @return
     */
    public static String unCompress(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = VenvyBase64.decode(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            VenvyIOUtils.close(ginzip);
            VenvyIOUtils.close(in);
            VenvyIOUtils.close(out);
        }

        return decompressed;
    }

    /**
     * lua 用将zip过的数据解压缩出来
     *
     * @param compressed
     * @return
     */
    public static byte[] unzip(byte[] compressed) {
        ByteArrayInputStream is = null;
        GZIPInputStream gis = null;
        ByteArrayOutputStream out = null;
        try {
            is = new ByteArrayInputStream(compressed);
            gis = new GZIPInputStream(is, 1024 * 32);
            out = new ByteArrayOutputStream();
            byte[] data = new byte[1024 * 32];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                out.write(data, 0, bytesRead);
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            VenvyIOUtils.close(gis);
            VenvyIOUtils.close(is);
            VenvyIOUtils.close(out);
        }

    }
}
