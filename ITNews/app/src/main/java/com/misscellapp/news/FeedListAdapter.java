package com.misscellapp.news;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.misscellapp.news.article.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by chenjishi on 16/7/27.
 */
public class FeedListAdapter extends Adapter<ViewHolder> implements View.OnClickListener {
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

    @Override
    public void onClick(View v) {
        if (null == v.getTag()) return;

        Feed feed = (Feed) v.getTag();

        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra("feed", feed);
        mContext.startActivity(intent);
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
            String url = "https:" + feed.imageUrl;
            Glide.with(mContext).load(url).into(viewHolder.imageView);
        }
        String comment = mContext.getString(R.string.comment_num, feed.commentNum);
        viewHolder.commentLabel.setText(String.format("%s  %s", comment, feed.views));
        viewHolder.timeLabel.setText(feed.time);

        viewHolder.itemView.setTag(feed);
        viewHolder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }
}
