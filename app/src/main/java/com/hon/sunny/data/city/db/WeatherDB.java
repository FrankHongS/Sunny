package com.hon.sunny.data.city.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.city.City;
import com.hon.sunny.vo.bean.city.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherDB {

    public WeatherDB() {

    }

    public static List<Province> loadProvinces(SQLiteDatabase db) {

        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.ProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.ProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        Util.closeQuietly(cursor);
        return list;
    }

    public static List<City> loadCities(SQLiteDatabase db, int ProID) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("T_City", null, "ProID = ?", new String[]{String.valueOf(ProID)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.CityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.ProID = ProID;
                city.CitySort = cursor.getInt(cursor.getColumnIndex("CitySort"));
                list.add(city);
            } while (cursor.moveToNext());
        }
        Util.closeQuietly(cursor);
        return list;
    }
}
