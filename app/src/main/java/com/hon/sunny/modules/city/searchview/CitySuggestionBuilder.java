package com.hon.sunny.modules.city.searchview;

import android.content.Context;

import com.hon.persistentsearchview.SearchHistoryTable;
import com.hon.persistentsearchview.SearchItem;
import com.hon.persistentsearchview.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public class CitySuggestionBuilder implements SearchSuggestionsBuilder{
    private SearchHistoryTable mSearchHistoryTable;

    public CitySuggestionBuilder(Context context){
        mSearchHistoryTable=new SearchHistoryTable(context);
    }


    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {
        List<SearchItem> items=mSearchHistoryTable.getAllItems(null);
        if(items.size()>maxCount){
            items=items.subList(0,maxCount);
        }
        return items;
    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {
        List<SearchItem> items=mSearchHistoryTable.getSuggestionItems(query);
        if(items.size()>maxCount){
            items=items.subList(0,maxCount);
        }
        return items;
    }
}
