package com.misscellapp.news;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjishi on 16/7/27.
 */
public class FeedListAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private Context mContext;

    private LayoutInflater mInflater;

    private final List<Feed> mFeedList = new ArrayList<>();

    public FeedListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void addData(List<Feed> feedList) {
        mFeedList.addAll(feedList);
        notifyDataSetChanged();
    }

    public void clearData() {
        mFeedList.clear();
        notifyDataSetChanged();
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        Feed feed = mFeedList.get(position);

        holder.titleLabel.setText(feed.title);
        holder.summaryLabel.setText(feed.summary);
        holder.imageView.setImageURI(Uri.parse(feed.imageUrl));
        holder.commentLabel.setText(String.format("%s  %s", feed.comments, feed.views));
        holder.timeLabel.setText(feed.time);
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }
}
