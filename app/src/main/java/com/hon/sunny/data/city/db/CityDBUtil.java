package com.hon.sunny.data.city.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hon.sunny.ui.city.view.expandrecycleview.ChildBean;
import com.hon.sunny.ui.city.view.expandrecycleview.ParentBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public class CityDBUtil {
    private CityDBUtil(){}

    public synchronized static List<ParentBean> queryCities(SQLiteDatabase database,String queryText){
        List<ParentBean> list=new ArrayList<>();

        Cursor cursor = database.rawQuery("select CityName,CitySort from T_City " +
                "where CityName like '%"+queryText+"%' order by rowid asc",null);

        if(cursor.moveToFirst()){
            do {
                ParentBean bean=new ParentBean();
                bean.text=cursor.getString(cursor.getColumnIndex("CityName"));
                bean.mChild=queryZonesFromCity(database,cursor.getString(cursor.getColumnIndex("CitySort")));
                list.add(bean);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public synchronized static List<ParentBean> queryZones(SQLiteDatabase database,String queryText){
        List<ParentBean> list=new ArrayList<>();

        Cursor cursor = database.rawQuery("select ZoneName,CityID from T_Zone " +
                "where ZoneName like '%"+queryText+"%' order by rowid asc",null);

        if(cursor.moveToFirst()){
            do {
                ParentBean bean=new ParentBean();
                String cityName=queryCityFromZone(database,cursor.getString(cursor.getColumnIndex("CityID")));
                bean.zone=cursor.getString(cursor.getColumnIndex("ZoneName"));
                bean.text=cityName+cursor.getString(cursor.getColumnIndex("ZoneName"));
                list.add(bean);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    private static List<ChildBean> queryZonesFromCity(SQLiteDatabase database,String citySort){
        List<ChildBean> list=new ArrayList<>();

        Cursor cursor = database.rawQuery("select ZoneName from T_Zone " +
                "where CityID=? order by rowid asc",new String[]{citySort});

        if(cursor.moveToFirst()){
            do {
                ChildBean bean=new ChildBean();
                bean.text=cursor.getString(cursor.getColumnIndex("ZoneName"));
                list.add(bean);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    private static String queryCityFromZone(SQLiteDatabase database,String cityID){
        String cityName="";

        Cursor cursor = database.rawQuery("select CityName from T_City " +
                "where CitySort=? order by rowid asc",new String[]{cityID});
        if(cursor.moveToFirst()){
            cityName=cursor.getString(cursor.getColumnIndex("CityName"));
        }

        cursor.close();
        return cityName;
    }
}

