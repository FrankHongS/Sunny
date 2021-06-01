package com.hon.sunny.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseViewHolder;
import com.hon.sunny.vo.bean.main.HourInfoEntity;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Frank_Hon on 4/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class HourInfoAdapter extends RecyclerView.Adapter<HourInfoAdapter.HourInfoViewHolder> {

    private List<HourInfoEntity> mHourInfoList;

    @NonNull
    @Override
    public HourInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hour_info_subitem, parent, false);

        return new HourInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourInfoViewHolder holder, int position) {
        holder.bind(mHourInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mHourInfoList == null ? 0 : mHourInfoList.size();
    }

    public void swapDataList(List<HourInfoEntity> hourInfoList) {
        if (mHourInfoList == null) {
            mHourInfoList = hourInfoList;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mHourInfoList.size();
                }

                @Override
                public int getNewListSize() {
                    return hourInfoList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mHourInfoList.get(oldItemPosition).getTime()
                            .equals(hourInfoList.get(newItemPosition).getTime());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    HourInfoEntity newHourInfo = hourInfoList.get(newItemPosition);
                    HourInfoEntity oldHourInfo = mHourInfoList.get(oldItemPosition);
                    return oldHourInfo.getTmp().equals(newHourInfo.getTmp())
                            && oldHourInfo.getCity().equals(newHourInfo.getCity());
                }
            });

            mHourInfoList = hourInfoList;
            result.dispatchUpdatesTo(this);
        }
    }

    static class HourInfoViewHolder extends BaseViewHolder<HourInfoEntity> {

        @BindView(R.id.tv_time)
        TextView time;
        @BindView(R.id.tv_tmp)
        TextView tmp;
        @BindView(R.id.tv_hum)
        TextView hum;
        @BindView(R.id.tv_spd)
        TextView spd;

        HourInfoViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(HourInfoEntity hourInfo) {
            time.setText(hourInfo.getTime());
            tmp.setText(hourInfo.getTmp());
            hum.setText(hourInfo.getHum());
            spd.setText(hourInfo.getSpd());
        }
    }

}
