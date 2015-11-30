package com.lycan.stilian.lycanrssreader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lycan.stilian.lycanrssreader.MainActivity;
import com.lycan.stilian.lycanrssreader.R;
import com.lycan.stilian.lycanrssreader.ViewFeedActivity;
import com.lycan.stilian.lycanrssreader.appData.DBPref;
import com.lycan.stilian.lycanrssreader.models.FeedDataModel;
import com.lycan.stilian.lycanrssreader.models.base.Item;
import com.lycan.stilian.lycanrssreader.tasks.GetRssFromUrlTask;
import com.lycan.stilian.lycanrssreader.tasks.LoadUserFeedsTask;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IDataTransformer;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//http://developer.android.com/training/run-background-service/create-service.html
public class RssUpdateService extends Service implements IUpdateable {
    private final IBinder mBinder = new LocalBinder();
    private RssUpdateService rssUpdateService = this;
    private List<FeedDataModel> currentFeeds;
    HashMap<String, HashMap<String, Item>> currentFeedsHashMap = new HashMap<>();

    final Handler handler = new Handler();

    public void checkForChanges() {
        try {
            this.getAllFeeds();

            final Runnable r = new Runnable() {
                public void run() {
                    reloadRss();
//                Intent intent = new Intent(context, MainActivity.class);
//                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.Builder b = new NotificationCompat.Builder(context);
//
//                b.setAutoCancel(true)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.drawable.logo)
//                        .setContentTitle("Feed updated")
//                        .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
//                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
//                        .setContentIntent(contentIntent)
//                        .setContentInfo("Info");
//
//
//                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(1, b.build());
                }
            };

            handler.postDelayed(r, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void checkForChanges(FeedDataModel reloadedFeed) {
        try {
            Boolean containsTheFeed = currentFeedsHashMap.containsKey(reloadedFeed.link);

            if (containsTheFeed) {
                HashMap<String, Item> feedItemsHashMap = currentFeedsHashMap.get(reloadedFeed.link);

                for (Iterator<Item> z = reloadedFeed.channel.items.iterator(); z.hasNext(); ) {
                    Item item = z.next();

                    if ((item.link != null && !feedItemsHashMap.containsKey(item.link)) || (item.link == null && item.title != null && !feedItemsHashMap.containsKey(item.title))) {
                        DBPref dbCon = new DBPref(this);
                        dbCon.saveUpdatedFeed(reloadedFeed);
                        this.notifyNewFeedItem(reloadedFeed, item);
                    }
                }

                this.checkForChanges();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyNewFeedItem(FeedDataModel reloadedFeed, Item item) {
        try {
            Intent intent = new Intent(this, ViewFeedActivity.class);
            intent.putExtra("feedDbId", reloadedFeed.DbId);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(this);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("News: " + reloadedFeed.channel.title)
                    .setContentText(item.title)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");


            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void reloadRss() {
        try {
            for (Iterator<FeedDataModel> i = this.currentFeeds.iterator(); i.hasNext(); ) {
                final FeedDataModel feed = i.next();
                GetRssFromUrlTask getRssFromUrlTask = new GetRssFromUrlTask<FeedDataModel>(FeedDataModel.class, this, ACTION_TYPE.RELOAD_USER_FEED, new IDataTransformer() {
                    public void afterTransform(Object data) {
                        if (data == null) {
                            return;
                        }

                        FeedDataModel model = (FeedDataModel) data;
                        model.link = feed.link;
                        model.DbId = feed.DbId;
                    }
                });
                getRssFromUrlTask.execute(feed.link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void getAllFeeds() {
        Class<?> outputClass = (new ArrayList<FeedDataModel>()).getClass();
        LoadUserFeedsTask userFeedsTask = new LoadUserFeedsTask<List<FeedDataModel>>(outputClass, this, ACTION_TYPE.LOAD_USER_FEEDS);
        userFeedsTask.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void updateData(Object data, ACTION_TYPE actionType) {
        if (data == null) {
            return;
        }

        if (actionType == ACTION_TYPE.LOAD_USER_FEEDS) {
            this.currentFeeds = (List<FeedDataModel>) data;
            for (Iterator<FeedDataModel> i = this.currentFeeds.iterator(); i.hasNext(); ) {
                FeedDataModel feed = i.next();
                HashMap<String, Item> feedItemsHashMap = new HashMap<String, Item>();
                currentFeedsHashMap.put(feed.link, feedItemsHashMap);

                for (Iterator<Item> z = feed.channel.items.iterator(); z.hasNext(); ) {
                    Item item = z.next();

                    if (item.link != null) {
                        feedItemsHashMap.put(item.link, item);
                    } else if (item.title != null) {
                        feedItemsHashMap.put(item.title, item);
                    }
                }
            }
        } else if (actionType == ACTION_TYPE.RELOAD_USER_FEED) {
            FeedDataModel reloadedFeed = (FeedDataModel) data;
            this.checkForChanges(reloadedFeed);
        }
    }

    public class LocalBinder extends Binder {
        public RssUpdateService getService() {
            return rssUpdateService;
        }
    }

//    @Override
//    protected void onHandleIntent(Intent workIntent) {
//        final Context context = this;
//
//        // Gets data from the incoming Intent
//        String dataString = workIntent.getDataString();
//
//        final Handler handler = new Handler();
//
//        final Runnable r = new Runnable() {
//            public void run() {
//                String a = "a";
//                Intent intent = new Intent(context, MainActivity.class);
//                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                NotificationCompat.Builder b = new NotificationCompat.Builder(context);
//
//                b.setAutoCancel(true)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setWhen(System.currentTimeMillis())
//                        .setSmallIcon(R.drawable.logo)
//                        .setContentTitle("Default notification")
//                        .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
//                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
//                        .setContentIntent(contentIntent)
//                        .setContentInfo("Info");
//
//
//                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(1, b.build());
//            }
//        };
//
//        handler.postDelayed(r, 1000);
//    }
}
