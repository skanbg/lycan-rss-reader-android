package com.lycan.stilian.lycanrssreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lycan.stilian.lycanrssreader.R;
import com.lycan.stilian.lycanrssreader.models.SuggestedFeed;

import java.util.List;

public class SuggestedFeedsAdapter extends BaseAdapter {
    private Context mContext;
    private List<SuggestedFeed> mSuggestedFeeds;

    public SuggestedFeedsAdapter(Context context, List<SuggestedFeed> mSuggestedFeeds) {
        this.mContext = context;
        this.mSuggestedFeeds = mSuggestedFeeds;
    }

    @Override
    public int getCount() {
        return mSuggestedFeeds.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mSuggestedFeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolderUserFeed {
        TextView feedTitleTextView;
        View rootView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderUserFeed viewHolder;
        SuggestedFeed suggestedFeed = (SuggestedFeed) this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.suggested_feeds_row_layout, parent, false);
            viewHolder = new ViewHolderUserFeed();
            viewHolder.rootView = convertView;
            viewHolder.feedTitleTextView = (TextView) convertView.findViewById(R.id.suggestedFeedsTextViewFeedTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderUserFeed) convertView.getTag();
        }

        viewHolder.feedTitleTextView.setText(suggestedFeed.title);

        return viewHolder.rootView;
    }
}

