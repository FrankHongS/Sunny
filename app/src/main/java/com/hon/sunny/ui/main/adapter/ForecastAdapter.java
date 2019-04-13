package com.hon.sunny.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.data.main.bean.ForecastEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Bind;

/**
 * Created by Frank Hon on 2019/4/12 11:34 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastItemViewHolder> {

    private List<ForecastEntity> mForecastList;

    @NonNull
    @Override
    public ForecastItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast_subitem,parent,false);

        return new ForecastItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastItemViewHolder holder, int position) {
        holder.bind(mForecastList.get(position));
    }

    @Override
    public int getItemCount() {
        return mForecastList==null?0:mForecastList.size();
    }

    public void swapDataList(List<ForecastEntity> forecastList){
        if (mForecastList == null) {
            mForecastList = forecastList;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mForecastList.size();
                }

                @Override
                public int getNewListSize() {
                    return forecastList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mForecastList.get(oldItemPosition).getDate()
                            .equals(forecastList.get(newItemPosition).getDate());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ForecastEntity newForecast = forecastList.get(newItemPosition);
                    ForecastEntity oldForecast = mForecastList.get(oldItemPosition);
                    return oldForecast.getDate()
                            .equals(newForecast.getDate());
                }
            });

            mForecastList=forecastList;
            result.dispatchUpdatesTo(this);
        }
    }

    static class ForecastItemViewHolder extends BaseViewHolder<ForecastEntity>{

        @Bind(R.id.iv_forecast_icon)
        ImageView forecastIcon;
        @Bind(R.id.tv_forecast_date)
        TextView forecastDate;
        @Bind(R.id.tv_forecast_desc)
        TextView forecastDesc;
        @Bind(R.id.tv_forecast_temp)
        TextView forecastTemp;

        ForecastItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(ForecastEntity forecast) {
            try {
                forecastDate.setText(
                        Util.dayForWeek(forecast.getDate()));
            } catch (Exception e) {
                forecastDate.setText(
                        forecast.getDate());
            }

            Glide.with(itemView.getContext())
                    .load(SharedPreferenceUtil.getInstance().getInt(forecast.getTxtD(), R.mipmap.none))
                    .into(forecastIcon);

            forecastTemp.setText(
                    String.format(
                            itemView.getResources().getString(R.string.weather_forecast_temp),
                            forecast.getMin(),
                            forecast.getMax()
                    )
            );

            forecastDesc.setText(
                    String.format(
                            itemView.getResources().getString(R.string.weather_forecast_desc),
                            forecast.getTxtD(),
                            forecast.getSc(),
                            forecast.getDir(),
                            forecast.getSpd(),
                            forecast.getPop()
                    )
            );
        }
    }

}
