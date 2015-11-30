package com.lycan.stilian.lycanrssreader.tasks;

import android.content.Context;

import com.lycan.stilian.lycanrssreader.appData.DBPref;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.AbstractUpdaterTask;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

public class LoadUserFeedTask<T> extends AbstractUpdaterTask<Object, Void, T> {
    public LoadUserFeedTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType) {
        super(classMap, mContext, actionType);
    }

    @Override
    protected T doInBackground(Object... params) {
        String feedId = (String)params[0];
        DBPref dbCon = new DBPref((Context)mContext);
        T allFeeds = (T)dbCon.getFeedById(feedId);
        return allFeeds;
    }
}