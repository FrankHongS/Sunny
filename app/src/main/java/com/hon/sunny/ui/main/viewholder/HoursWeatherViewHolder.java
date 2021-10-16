package com.hon.sunny.ui.main.viewholder;

import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseViewHolder;
import com.hon.sunny.ui.main.adapter.HourInfoAdapter;
import com.hon.sunny.vo.bean.main.HourInfoEntity;
import com.hon.sunny.vo.bean.main.Weather;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Frank_Hon on 4/12/2019.
 * E-mail: v-shhong@microsoft.com
 * <p>
 * hour weather info
 */
public class HoursWeatherViewHolder extends BaseViewHolder<Weather> {

    private static final int HOUR_INFO_COUNT = 4;
    @BindView(R.id.cv_hour_info)
    CardView hourInfoCard;
    @BindView(R.id.rv_hour_info)
    RecyclerView hoursInfo;
    private HourInfoAdapter mHourInfoAdapter;
    private boolean isCollapsed = true;

    public HoursWeatherViewHolder(View itemView) {
        super(itemView);

        mHourInfoAdapter = new HourInfoAdapter();
        hoursInfo.setAdapter(mHourInfoAdapter);
        hoursInfo.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }

    @Override
    public void bind(Weather weather) {
        List<HourInfoEntity> hourInfoList = new ArrayList<>();
        if (weather.hourlyForecast != null) {
            for (Weather.HourlyForecastEntity entity : weather.hourlyForecast) {
                HourInfoEntity hourInfo = new HourInfoEntity(
                        weather.city,
                        entity.date.substring(entity.date.length() - 5),
                        String.format("%s℃", entity.tmp),
                        String.format("%s%%", entity.hum),
                        String.format("%sKm/h", entity.spd)
                );

                hourInfoList.add(hourInfo);
            }
        }

        List<HourInfoEntity> trimmedHourInfoList = generateHourInfoList(hourInfoList);

        mHourInfoAdapter.swapDataList(trimmedHourInfoList);

        hourInfoCard.setOnClickListener(
                view -> {
                    if (hourInfoList.size() > HOUR_INFO_COUNT) {
                        if (isCollapsed) {
                            isCollapsed = false;
                            mHourInfoAdapter.swapDataList(hourInfoList);
                        } else {
                            isCollapsed = true;
                            mHourInfoAdapter.swapDataList(trimmedHourInfoList);
                        }
                    }
                }
        );
    }

    private List<HourInfoEntity> generateHourInfoList(List<HourInfoEntity> hourInfoList) {
        int size = hourInfoList.size();
        return size > HOUR_INFO_COUNT ? hourInfoList.subList(0, HOUR_INFO_COUNT) : hourInfoList;
    }
}
