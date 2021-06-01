package com.hon.sunny.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseViewHolder;
import com.hon.sunny.vo.bean.main.SuggestionEntity;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Frank_Hon on 4/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
@SuppressWarnings("all")
public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionSubItemViewHolder> {

    private List<SuggestionEntity> mSuggestionList;

    public SuggestionAdapter(@NonNull List<SuggestionEntity> suggestionList) {
        this.mSuggestionList = suggestionList;
    }

    @NonNull
    @Override
    public SuggestionAdapter.SuggestionSubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion_subitem, parent, false);

        return new SuggestionSubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionAdapter.SuggestionSubItemViewHolder holder, int position) {
        holder.bind(mSuggestionList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSuggestionList.size();
    }

    static class SuggestionSubItemViewHolder extends BaseViewHolder<SuggestionEntity> {

        @BindView(R.id.iv_suggestion_icon)
        ImageView suggestionIcon;
        @BindView(R.id.tv_suggestion_title)
        TextView suggestionTitle;
        @BindView(R.id.tv_suggestion_desc)
        TextView suggestionDesc;

        SuggestionSubItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bind(SuggestionEntity entity) {
            suggestionIcon.setImageResource(entity.getImageId());
            suggestionTitle.setText(entity.getTitle());
            suggestionDesc.setText(entity.getDesc());
        }
    }
}
