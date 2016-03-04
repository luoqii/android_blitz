/**
 * $
 * PROJECT NAME: k2webview
 * PACKAGE NAME: com.taobao.K2WebView.common
 * FILE NAME: IOUtils.java
 * CREATED TIME: 2015年4月17日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tv.blitz.video;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    public static final int IO_BUFFER_SIZE = 16 * 1024;

    public static String readString(InputStream is) throws IOException {
        return new String(readBytes(is));
    }

    public static String readString(BufferedInputStream is) throws IOException {
        return new String(readBytes(is));
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] result = new byte[0];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[IO_BUFFER_SIZE];
        int len = -1;
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception ignore) {
            }
        }
        return result;
    }

    public static byte[] readBytes(BufferedInputStream is) throws IOException {
        byte[] result = new byte[0];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[IO_BUFFER_SIZE];
        int len = -1;
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception ignore) {
            }
        }
        return result;
    }

    /**
     * 将流文件写到本地
     * @param is 可以是context.getContentResolver().openInputStream(uri)
     */
    public static void writeToLocal(InputStream is, String localPath) throws IOException {
        File file = new File(localPath);
        FileOutputStream fos = null;

        if (!file.exists()) {
            File p = file.getParentFile();
            if (!p.exists()) {
                p.mkdirs();
            }
            file.createNewFile();
        }
        fos = new FileOutputStream(file);
        byte[] buff = new byte[IO_BUFFER_SIZE];
        int len = -1;

        try {
            while ((len = is.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception ignore) {
            }
        }
    }

}
