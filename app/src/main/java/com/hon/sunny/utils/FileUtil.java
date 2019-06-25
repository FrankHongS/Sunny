package com.hon.sunny.utils;

import java.io.File;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class FileUtil {

    public static boolean delete(File file) {
        if (file.isFile()) {
            return file.delete();
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return file.delete();
            }

            for (File childFile : childFiles) {
                if (!delete(childFile))
                    return false;
            }
            return true;
        }
        return false;
    }
}

