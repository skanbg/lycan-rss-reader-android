package com.lycan.stilian.lycanrssreader.appData;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    public static final String TABLE_FEEDS = "feeds";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_VAL = "val";

    static final String DB_NAME = "mydb";
    static final int DB_CURRENT_VERSION = 1;

    protected SQLiteDatabase db;

    public DbHelper(Context context){
        super(context, DB_NAME, null, DB_CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE feeds(_id integer primary key autoincrement, link text not null, val text not null);");
        Log.d("D1", "Createdb");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void open() throws SQLException{
        this.db = getWritableDatabase();
    }

    public void close(){
        this.db.close();
    }
}
