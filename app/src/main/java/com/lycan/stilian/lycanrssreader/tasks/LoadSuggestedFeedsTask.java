package com.lycan.stilian.lycanrssreader.tasks;

import com.lycan.stilian.lycanrssreader.models.SuggestedFeed;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.AbstractUpdaterTask;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadSuggestedFeedsTask<T> extends AbstractUpdaterTask<Object, Void, T> {
    public LoadSuggestedFeedsTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType) {
        super(classMap, mContext, actionType);
    }

    @Override
    protected T doInBackground(Object... params) {
        SuggestedFeed nyTimesFeed = new SuggestedFeed();
        nyTimesFeed.url = "http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml";
        nyTimesFeed.title = "The New York Times Feed";
        SuggestedFeed financialTimesFeed = new SuggestedFeed();
        financialTimesFeed.url = "http://www.ft.com/rss/home/europe";
        financialTimesFeed.title = "Financial Times Europe";
        return (T)new ArrayList<SuggestedFeed>(Arrays.asList(nyTimesFeed, financialTimesFeed));
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        this.mContext.updateData(result, this.actionType);
    }
}
