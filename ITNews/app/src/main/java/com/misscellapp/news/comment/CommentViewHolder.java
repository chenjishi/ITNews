package com.misscellapp.news.comment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.misscellapp.news.R;

/**
 * Created by jishichen on 2017/5/10.
 */
public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView floorView, nameView, contentView;

    public CommentViewHolder(View itemView) {
        super(itemView);
        floorView = (TextView) itemView.findViewById(R.id.floor);
        nameView = (TextView) itemView.findViewById(R.id.user);
        contentView = (TextView) itemView.findViewById(R.id.content);
    }
}
