package com.hon.sunny.modules.city.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseApplication;
import com.hon.sunny.common.PLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class DBManager {

    private static String TAG = DBManager.class.getSimpleName();
    public static final String DB_NAME = "china_city.db"; //数据库名字
    public static final String PACKAGE_NAME = "com.hon.sunny";
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" +
            PACKAGE_NAME;  //在手机里存放数据库的位置(/data/data/com.hon.sunny/china_city.db)
    private SQLiteDatabase database;
    private Context context;

    private DBManager() {

    }

    public static DBManager getInstance() {
        return DBManagerHolder.sInstance;
    }

    private static final class DBManagerHolder {
        public static final DBManager sInstance = new DBManager();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }


    public void openDatabase() {
        PLog.e(TAG, DB_PATH + "/" + DB_NAME);
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    @Nullable
    private SQLiteDatabase openDatabase(String dbfile) {

        try {
            if (!(new File(dbfile).exists())) {
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = BaseApplication.getAppContext().getResources().openRawResource(R.raw.china_city); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                int BUFFER_SIZE = 400000;
                byte[] buffer = new byte[BUFFER_SIZE];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        } catch (FileNotFoundException e) {
            PLog.e("File not found");
            e.printStackTrace();
        } catch (IOException e) {
            PLog.e("IO exception");
            e.printStackTrace();
        }

        return null;
    }

    public void closeDatabase() {
        if (this.database != null) {
            this.database.close();
        }
    }
}
