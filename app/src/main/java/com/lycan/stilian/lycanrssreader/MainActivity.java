package com.lycan.stilian.lycanrssreader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lycan.stilian.lycanrssreader.adapters.UserFeedsAdapter;
import com.lycan.stilian.lycanrssreader.models.FeedDataModel;
import com.lycan.stilian.lycanrssreader.services.RssUpdateService;
import com.lycan.stilian.lycanrssreader.tasks.LoadUserFeedsTask;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IUpdateable, View.OnClickListener, AdapterView.OnItemClickListener {
    private Context context;

    private ListView listViewHomeUserFeeds;
    private List<FeedDataModel> userFeedsList;

    boolean mBound = false;
    RssUpdateService mService;

    private ServiceConnection mPlayerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            try {
                // We've bound to LocalService, cast the IBinder and get
                // LocalService instance
                RssUpdateService.LocalBinder binder = (RssUpdateService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                mService.checkForChanges();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.activity_main);

        this.context = this;

        this.listViewHomeUserFeeds = (ListView) this.findViewById(R.id.listViewHomeUserFeeds);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        this.populateUserFeeds();

        try {
            Intent playerIntent = new Intent(this.context, RssUpdateService.class);
            this.context.bindService(playerIntent, mPlayerConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_left);
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.populateUserFeeds();
        super.onResume();
    }

    private void populateUserFeeds() {
        Class<?> outputClass = (new ArrayList<FeedDataModel>()).getClass();
        LoadUserFeedsTask userFeedsTask = new LoadUserFeedsTask<List<FeedDataModel>>(outputClass, this, ACTION_TYPE.LOAD_USER_FEEDS);
        userFeedsTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //On fap button click
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.context, AddFeedActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            overridePendingTransition(R.anim.hold, R.anim.pull_out_to_left);
    }

    private void onUserFeedsLoad(List<FeedDataModel> data) {
        this.userFeedsList = data;

        if (this.userFeedsList != null && this.userFeedsList.size() > 0) {
            this.listViewHomeUserFeeds.setAdapter(new UserFeedsAdapter(this.context, this.userFeedsList));
            this.listViewHomeUserFeeds.setOnItemClickListener(this);
        } else {
            TextView emptyText = (TextView) findViewById(R.id.textViewHomeUserFeedsNoFeeds);
            this.listViewHomeUserFeeds.setEmptyView(emptyText);
        }
    }

    @Override
    public void updateData(Object data, ACTION_TYPE actionType) {
        if (data == null) {
            return;
        }

        if (actionType == ACTION_TYPE.LOAD_USER_FEEDS) {
            this.onUserFeedsLoad((ArrayList<FeedDataModel>) data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this.context, ViewFeedActivity.class);
        FeedDataModel targetFeed = (FeedDataModel) ((ListView) parent).getItemAtPosition(position);
        intent.putExtra("feedDbId", targetFeed.DbId);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
