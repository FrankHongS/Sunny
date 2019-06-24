package com.hon.sunny.ui.main.viewholder;

import android.view.View;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseViewHolder;
import com.hon.sunny.data.main.bean.SuggestionEntity;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.ui.main.adapter.SuggestionAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * Created by Frank Hon on 2019/4/13 12:36 AM.
 * E-mail: frank_hon@foxmail.com
 *
 *  suggestions
 */
public class SuggestionViewHolder extends BaseViewHolder<Weather> {

    @BindView(R.id.rv_suggestions)
    RecyclerView suggestions;

    public SuggestionViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(Weather weather) {

        List<SuggestionEntity> suggestionList=new ArrayList<>();

        if(weather.lifestyle!=null){
            for(int i=0;i<weather.lifestyle.size();i++){
                Weather.LifestyleEntity lifestyleEntity=weather.lifestyle.get(i);
                SuggestionEntity suggestionEntity=null;
                switch (i){
                    case 0:
                        suggestionEntity=new SuggestionEntity(R.drawable.icon_cloth,
                                String.format(itemView.getResources().getString(R.string.weather_suggesetion_clothes),
                                        lifestyleEntity.brf),
                                lifestyleEntity.txt);
                        break;
                    case 1:
                        suggestionEntity=new SuggestionEntity(R.drawable.icon_sport,
                                String.format(itemView.getResources().getString(R.string.weather_suggesetion_sports),
                                        lifestyleEntity.brf),
                                lifestyleEntity.txt);
                        break;
                    case 2:
                        suggestionEntity=new SuggestionEntity(R.drawable.icon_flu,
                                String.format(itemView.getResources().getString(R.string.weather_suggesetion_illness),
                                        lifestyleEntity.brf),
                                lifestyleEntity.txt);
                        break;
                    case 3:
                        suggestionEntity=new SuggestionEntity(R.drawable.icon_travel,
                                String.format(itemView.getResources().getString(R.string.weather_suggesetion_travel),
                                        lifestyleEntity.brf),
                                lifestyleEntity.txt);
                        break;
                    default:
                        break;
                }

                if(suggestionEntity!=null)
                    suggestionList.add(suggestionEntity);
            }

            suggestions.setAdapter(new SuggestionAdapter(suggestionList));
            suggestions.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

    }
}