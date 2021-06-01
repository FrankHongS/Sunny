package com.hon.sunny.data.main.multicity;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.hon.sunny.component.OrmLite;
import com.hon.sunny.vo.bean.main.CityORM;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Frank_Hon on 9/18/2019.
 * E-mail: v-shhong@microsoft.com
 */
@RunWith(AndroidJUnit4.class)
public class MultiCityRemoteDataSourceTest {

    @Test
    public void fetchMultiCityWeather() {
        List<CityORM> cityList = OrmLite.getInstance().query(CityORM.class);
        Log.d("hon", "fetchMultiCityWeather: "+cityList);
        assertEquals(2,cityList.size());
    }
}