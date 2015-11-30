package com.lycan.stilian.lycanrssreader.tasks;

import android.content.Context;

import com.lycan.stilian.lycanrssreader.appData.DBPref;
import com.lycan.stilian.lycanrssreader.models.FeedDataModel;
import com.lycan.stilian.lycanrssreader.models.base.Channel;
import com.lycan.stilian.lycanrssreader.models.base.ChannelImage;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.AbstractUpdaterTask;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadUserFeedsTask<T> extends AbstractUpdaterTask<Object, Void, T> {
    public LoadUserFeedsTask(Class<?> classMap, IUpdateable mContext, ACTION_TYPE actionType) {
        super(classMap, mContext, actionType);
    }

    @Override
    protected T doInBackground(Object... params) {
        DBPref dbCon = new DBPref((Context)mContext);
        T allFeeds = (T)dbCon.getAllFeeds();
//        FeedDataModel test = new FeedDataModel();
//        test.channel = new Channel();
//        test.channel.title = "TEST";
//        test.channel.image =new ChannelImage();
//        test.channel.image.url =  "https://external-ams3-1.xx.fbcdn.net/safe_image.php?d=AQBDZ5hdjCAmM4bt&w=470&h=246&url=http%3A%2F%2Fcdn-jarvis-fun.9cache.com%2Fmedia%2Fphoto%2FpKGM9krXG_600w_v1.jpg&cfs=1&upscale=1&sx=2&sy=0&sw=596&sh=312";
//
//        FeedDataModel test1 = new FeedDataModel();
//        test1.channel = new Channel();
//        test1.channel.title = "Hello pretty one";
//        test1.channel.image =new ChannelImage();
//        test1.channel.image.url =  "http://www.keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg";

//        return (T)new ArrayList<FeedDataModel>(Arrays.asList(test, test1, test, test1, test, test1));
//        return new ArrayList<FeedDataModel>();
        return allFeeds;
    }
}