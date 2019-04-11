package com.hon.sunny.ui.main.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.hon.sunny.base.BaseViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Frank_Hon on 4/11/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SuggestionAdapter extends RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    static class SuggestionViewHolder extends BaseViewHolder<String> {

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(String o) {

        }
    }
}
