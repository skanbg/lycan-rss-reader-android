package com.lycan.stilian.lycanrssreader;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lycan.stilian.lycanrssreader.adapters.FeedItemsAdapter;
import com.lycan.stilian.lycanrssreader.appData.DBPref;
import com.lycan.stilian.lycanrssreader.helpers.DynamicImageViewUpdater;
import com.lycan.stilian.lycanrssreader.models.FeedDataModel;
import com.lycan.stilian.lycanrssreader.models.base.Item;
import com.lycan.stilian.lycanrssreader.tasks.GetRssFromUrlTask;
import com.lycan.stilian.lycanrssreader.tasks.LoadImageFromUrlTask;
import com.lycan.stilian.lycanrssreader.tasks.LoadUserFeedTask;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IDataTransformer;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewFeedActivity extends Activity implements IUpdateable, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;
    private FeedDataModel feed;

    private Context context;
    private ImageView feedImageView;
    private TextView feedTitleView;
    private ListView feedItemsListView;
    //Swipe
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_feed);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.viewFeedActivityRefreshFeed).setOnTouchListener(mDelayHideTouchListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.removeFeedFab);
        fab.setOnClickListener(this);

        this.context = this;

        //Swipe
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(this);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.feedImageView = (ImageView) findViewById(R.id.viewFeedActivityFeedImage);
        this.feedTitleView = (TextView) findViewById(R.id.viewFeedActivityFeedTitle);
        this.feedItemsListView = (ListView) findViewById(R.id.feeditemsListView);
        this.loadFeed();
    }

    private void loadFeed() {
        String feedId = getIntent().getExtras().getString("feedDbId");
        LoadUserFeedTask userFeedsTask = new LoadUserFeedTask<FeedDataModel>(FeedDataModel.class, this, ACTION_TYPE.LOAD_USER_FEED);
        userFeedsTask.execute(feedId);
    }

    private void showFeed() {
        (new LoadImageFromUrlTask(new DynamicImageViewUpdater(this.feedImageView))).execute(this.feed.channel.image.url);
        feedTitleView.setText(this.feed.channel.title);
        if (this.feed != null && this.feed.channel != null && this.feed.channel.items != null && this.feed.channel.items.size() > 0) {
            this.feedItemsListView.setAdapter(new FeedItemsAdapter(this.context, this.feed.channel.items));
            this.feedItemsListView.setOnItemClickListener(this);
            this.feedItemsListView.setOnItemLongClickListener(this);
            //            final Context context = this.mContext;
//            viewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
//                public boolean onLongClick(View arg0) {
//                    Toast.makeText(context, "Long Clicked " + arg0.getTag().,
//                            Toast.LENGTH_SHORT).show();
//
//                    return true;    // <- set to true
//                }
//            });
        }
//        else {
//            TextView emptyText = (TextView) findViewById(R.id.textViewHomeUserFeedsNoFeeds);
//            this.listViewHomeUserFeeds.setEmptyView(emptyText);
//        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void updateData(Object data, ACTION_TYPE actionType) {
        if(data == null){
            return;
        }

        if (actionType == ACTION_TYPE.LOAD_USER_FEED) {
            FeedDataModel feed = (FeedDataModel) data;
            this.feed = feed;
            this.showFeed();
        } else if (actionType == ACTION_TYPE.RELOAD_USER_FEED) {
            FeedDataModel feed = (FeedDataModel) data;
            DBPref dbCon = new DBPref(this);
            dbCon.saveUpdatedFeed(feed);
            this.feed = feed;
            this.showFeed();
        }
    }

    @Override
    public void onClick(View v) {
        Integer viewId = v.getId();

        if (viewId == R.id.removeFeedFab) {
            DBPref dbCon = new DBPref(this);
            dbCon.removeFeedById(feed.DbId);
            Intent intent = new Intent(this.context, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Item targetItem = (Item) ((ListView) parent).getItemAtPosition(position);
        intent.setData(Uri.parse(targetItem.link));
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Item targetItem = (Item) ((ListView) parent).getItemAtPosition(position);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, targetItem.link);

        startActivity(Intent.createChooser(share, "Share to"));
        share.addCategory(Intent.CATEGORY_LAUNCHER);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return true;
    }

    public void reloadRss(final IDataTransformer transformer) {
        final FeedDataModel feed = this.feed;
        GetRssFromUrlTask getRssFromUrlTask = new GetRssFromUrlTask<FeedDataModel>(FeedDataModel.class, this, ACTION_TYPE.RELOAD_USER_FEED, new IDataTransformer() {
            public void afterTransform(Object data) {
                if(data == null){
                    return;
                }

                FeedDataModel model = (FeedDataModel) data;
                model.link = feed.link;
                model.DbId = feed.DbId;

                if (transformer != null) {
                    transformer.afterTransform(model);
                }
            }
        });
        getRssFromUrlTask.execute(this.feed.link);
    }

    @Override
    public void onRefresh() {
        this.reloadRss(new IDataTransformer() {
            @Override
            public void afterTransform(Object data) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }
}
