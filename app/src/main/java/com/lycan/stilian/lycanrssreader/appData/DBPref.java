package com.lycan.stilian.lycanrssreader.appData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lycan.stilian.lycanrssreader.models.FeedDataModel;

import java.util.ArrayList;
import java.util.List;

public class DBPref extends DbHelper {
    private String[] allColumns = {DbHelper.COLUMN_ID,
            DbHelper.COLUMN_VAL,
            DbHelper.COLUMN_LINK};

    public DBPref(Context context) {
        super(context);
    }

    public FeedDataModel addRecord(String val, String link) {
        this.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("val", val);
        contentValues.put("link", link);
        long insertId = this.db.insert(DbHelper.TABLE_FEEDS, null, contentValues);

        Cursor cursor = this.db.query(DbHelper.TABLE_FEEDS,
                allColumns, DbHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        FeedDataModel newFeed = cursorToFeed(cursor);
        cursor.close();
        return newFeed;
    }

    private FeedDataModel cursorToFeed(Cursor cursor) {
        if (cursor.getCount() > 0) {
            String feedVal = cursor.getString(1);
            FeedDataModel feed = FeedDataModel.mapFromXml(feedVal);
//        FeedDataModel feed = new FeedDataModel();
            feed.DbId = Long.toString(cursor.getLong(0));
            feed.link = cursor.getString(2);
            return feed;
        }

        return null;
    }

    public Boolean removeFeedById(String feedId) {
        this.open();

        Integer deletedRows = this.db.delete(DbHelper.TABLE_FEEDS, DbHelper.COLUMN_ID + "=" + feedId, null);

        this.close();

        return deletedRows > 0;
    }

    public FeedDataModel saveUpdatedFeed(FeedDataModel feedToSave) {
        this.open();

        ContentValues args = new ContentValues();
        args.put(DbHelper.COLUMN_VAL, feedToSave.toString());
        this.db.update(DbHelper.TABLE_FEEDS, args, DbHelper.COLUMN_ID + "=" + feedToSave.DbId, null);

        this.close();

        FeedDataModel savedFeed = this.getFeedById(feedToSave.DbId);
        return savedFeed;
    }

    public FeedDataModel getFeedById(String feedId) {
        this.open();
        Cursor cursor = this.db.query(DbHelper.TABLE_FEEDS,
                allColumns, DbHelper.COLUMN_ID + "=?", new String[]{feedId}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        FeedDataModel feed = cursorToFeed(cursor);
        this.close();
        return feed;
    }

    public List<FeedDataModel> getAllFeeds() {
        this.open();
        List<FeedDataModel> feeds = new ArrayList<FeedDataModel>();

        Cursor cursor = this.db.query(DbHelper.TABLE_FEEDS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FeedDataModel feed = cursorToFeed(cursor);
            feeds.add(feed);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        this.close();
        return feeds;
    }

//    public Cursor getVALS(){
//        return this.db.query("preferences", new String[]{"_id", "val"}, null, null, null, null, null);
//    }
}
