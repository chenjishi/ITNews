package com.misscellapp.news;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by chenjishi on 16/7/27.
 */
public class FeedListAdapter extends Adapter<ViewHolder> {

    private static final int VIEW_NORMAL = 0;
    private static final int VIEW_LOADING = 1;

    private Context mContext;

    private LayoutInflater mInflater;

    private boolean mIsLoading;

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

    public void showLoadingView() {
        mIsLoading = true;
        mFeedList.add(null);
        notifyItemInserted(mFeedList.size() - 1);
    }

    public void hideLoadingView() {
        mFeedList.remove(mFeedList.size() - 1);
        notifyItemRemoved(mFeedList.size());
        mIsLoading = false;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public int getItemViewType(int position) {
        return mFeedList.get(position) != null ? VIEW_NORMAL : VIEW_LOADING;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_NORMAL) {
            view = mInflater.inflate(R.layout.item_feed, parent, false);
            return new FeedViewHolder(view);
        } else {
            view = mInflater.inflate(R.layout.loading_more, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) return;

        FeedViewHolder viewHolder = (FeedViewHolder) holder;
        Feed feed = mFeedList.get(position);

        viewHolder.titleLabel.setText(feed.title);
        viewHolder.summaryLabel.setText(feed.summary);
        if (!TextUtils.isEmpty(feed.imageUrl)) {
            Glide.with(mContext).load(feed.imageUrl).into(viewHolder.imageView);
        }
        viewHolder.commentLabel.setText(String.format("%s  %s", feed.comments, feed.views));
        viewHolder.timeLabel.setText(feed.time);
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }
}
