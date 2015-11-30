package com.lycan.stilian.lycanrssreader.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lycan.stilian.lycanrssreader.R;
import com.lycan.stilian.lycanrssreader.models.base.Item;

import java.util.List;

public class FeedItemsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Item> itemList;

    public FeedItemsAdapter(Context context, List<Item> userFeeds) {
        this.mContext = context;
        this.itemList = userFeeds;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolderFeedItem {
        TextView feedItemTitleTextView;
        TextView feedItemDescription;
        View rootView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderFeedItem viewHolder;
        Item feedItem = (Item) this.getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.feed_items_row_layout, parent, false);
            viewHolder = new ViewHolderFeedItem();
            viewHolder.rootView = convertView;
            viewHolder.feedItemTitleTextView = (TextView) convertView.findViewById(R.id.feedItemTitle);
            viewHolder.feedItemDescription = (TextView) convertView.findViewById(R.id.feedItemDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderFeedItem) convertView.getTag();
        }

        viewHolder.feedItemTitleTextView.setText(feedItem.title);
        viewHolder.feedItemDescription.setText(Html.fromHtml(feedItem.description));

        return viewHolder.rootView;
    }
}
