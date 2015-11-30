package com.lycan.stilian.lycanrssreader;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lycan.stilian.lycanrssreader.adapters.SuggestedFeedsAdapter;
import com.lycan.stilian.lycanrssreader.appData.DBPref;
import com.lycan.stilian.lycanrssreader.models.FeedDataModel;
import com.lycan.stilian.lycanrssreader.models.SuggestedFeed;
import com.lycan.stilian.lycanrssreader.tasks.GetRssFromUrlTask;
import com.lycan.stilian.lycanrssreader.tasks.LoadSuggestedFeedsTask;
import com.lycan.stilian.lycanrssreader.tasks.constants.ACTION_TYPE;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IDataTransformer;
import com.lycan.stilian.lycanrssreader.tasks.interfaces.IUpdateable;

import java.util.ArrayList;
import java.util.List;

public class AddFeedActivity extends AppCompatActivity implements IUpdateable, View.OnClickListener, AdapterView.OnItemClickListener {
    private Button addFeedActivityAddFeedButton;
    private EditText addFeedActivityFeedUrlEditText;
    private ListView addFeedListViewSuggestedFeeds;
    private List<SuggestedFeed> suggestedFeeds;
    private SuggestedFeedsAdapter suggestedFeedsAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_add_feed);
        this.context = this;

        this.addFeedActivityAddFeedButton = (Button) this.findViewById(R.id.addFeedActivityAddFeedButton);
        this.addFeedActivityFeedUrlEditText = (EditText) this.findViewById(R.id.addFeedActivityFeedUrlEditText);
        this.addFeedListViewSuggestedFeeds = (ListView) this.findViewById(R.id.addFeedListViewSuggestedFeeds);

        this.addFeedActivityAddFeedButton.setOnClickListener(this);
        this.addFeedListViewSuggestedFeeds.setOnItemClickListener(this);
        this.populateSuggestedFeeds();
    }

    private void populateSuggestedFeeds() {
        Class<?> outputClass = (new ArrayList<SuggestedFeed>()).getClass();
        LoadSuggestedFeedsTask loadSuggestedFeedsTask = new LoadSuggestedFeedsTask<List<SuggestedFeed>>(outputClass, this, ACTION_TYPE.LOAD_SUGGESTED_FEEDS);
        loadSuggestedFeedsTask.execute();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addFeedActivityAddFeedButton) {
            Snackbar.make(view, this.addFeedActivityFeedUrlEditText.getText(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            String rssUrl = this.addFeedActivityFeedUrlEditText.getText().toString();
            GetRssFromUrlTask getRssFromUrlTask = new GetRssFromUrlTask<FeedDataModel>(FeedDataModel.class, this, ACTION_TYPE.ADD_NEW_FEED);
            getRssFromUrlTask.execute(rssUrl);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SuggestedFeed suggestedFeed = (SuggestedFeed) this.suggestedFeedsAdapter.getItem(position);

        final String rssUrl = suggestedFeed.url;
        GetRssFromUrlTask getRssFromUrlTask = new GetRssFromUrlTask<FeedDataModel>(FeedDataModel.class, this, ACTION_TYPE.ADD_NEW_FEED, new IDataTransformer(){
            public void afterTransform(Object data){
                if(data == null){
                    return;
                }

                FeedDataModel model = (FeedDataModel)data;
                model.link = rssUrl;
            }
        });
        getRssFromUrlTask.execute(rssUrl);
//        Intent intent = new Intent(Activity.this,destinationActivity.class);
//        startActivity(intent);
    }

    //    @Override
    public void onSuggestedFeedsLoad(ArrayList<SuggestedFeed> suggestedFeeds) {
        this.suggestedFeeds = suggestedFeeds;

        if (this.suggestedFeeds != null && this.suggestedFeeds.size() > 0) {
            this.suggestedFeedsAdapter = new SuggestedFeedsAdapter(this.context, this.suggestedFeeds);
            this.addFeedListViewSuggestedFeeds.setAdapter(this.suggestedFeedsAdapter);
        } else {
            TextView emptyText = (TextView) findViewById(R.id.addFeedTextViewNoFeeds);
            this.addFeedListViewSuggestedFeeds.setEmptyView(emptyText);
        }
    }

    public void onNewFeedAdded(FeedDataModel feed) {
        Snackbar.make(this.addFeedListViewSuggestedFeeds, feed.version.toString(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        DBPref dbCon = new DBPref(this);
        dbCon.addRecord(feed.toString(), feed.link);

        Intent intent = new Intent(this.context, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void updateData(Object data, ACTION_TYPE actionType) {
        if(data == null){
            return;
        }

        if (actionType == ACTION_TYPE.ADD_NEW_FEED) {
            this.onNewFeedAdded((FeedDataModel) data);
        } else if (actionType == ACTION_TYPE.LOAD_SUGGESTED_FEEDS) {
            this.onSuggestedFeedsLoad((ArrayList<SuggestedFeed>) data);
        }
    }
}
