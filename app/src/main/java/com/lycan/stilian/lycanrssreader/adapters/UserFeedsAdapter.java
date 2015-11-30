package com.lycan.stilian.lycanrssreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lycan.stilian.lycanrssreader.R;
import com.lycan.stilian.lycanrssreader.helpers.DynamicImageViewUpdater;
import com.lycan.stilian.lycanrssreader.models.FeedDataModel;
import com.lycan.stilian.lycanrssreader.tasks.LoadImageFromUrlTask;

import java.util.List;

public class UserFeedsAdapter extends BaseAdapter {
    private Context mContext;
    private List<FeedDataModel> mUserFeeds;

    public UserFeedsAdapter(Context context, List<FeedDataModel> userFeeds) {
        this.mContext = context;
        this.mUserFeeds = userFeeds;
    }

    @Override
    public int getCount() {
        return mUserFeeds.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mUserFeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolderUserFeed {
        TextView feedTitleTextView;
        ImageView feedImageView;
        View rootView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderUserFeed viewHolder;
        FeedDataModel userFeed = (FeedDataModel) this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.user_feeds_row_layout, parent, false);
            viewHolder = new ViewHolderUserFeed();
            viewHolder.rootView = convertView;
            viewHolder.feedTitleTextView = (TextView) convertView.findViewById(R.id.feedItemTitle);
            viewHolder.feedImageView = (ImageView) convertView.findViewById(R.id.userFeedsRowFeedImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderUserFeed) convertView.getTag();
        }

        viewHolder.feedTitleTextView.setText(userFeed.channel.title);

        if (viewHolder.feedImageView != null) {
            (new LoadImageFromUrlTask(new DynamicImageViewUpdater(viewHolder.feedImageView))).execute(userFeed.channel.image.url);

            viewHolder.feedImageView.setTag(userFeed.channel.image.url);
        }

        return viewHolder.rootView;
    }
}
