package com.misscellapp.news;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by chenjishi on 16/7/27.
 */
public class FeedViewHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView imageView;

    public TextView titleLabel;

    public TextView summaryLabel;

    public TextView commentLabel;

    public TextView timeLabel;

    public FeedViewHolder(View itemView) {
        super(itemView);
        imageView = (SimpleDraweeView) itemView.findViewById(R.id.image_view);
        titleLabel = (TextView) itemView.findViewById(R.id.feed_title);
        summaryLabel = (TextView) itemView.findViewById(R.id.feed_summary);
        commentLabel = (TextView) itemView.findViewById(R.id.comments);
        timeLabel = (TextView) itemView.findViewById(R.id.time);
    }
}
