package com.misscellapp.news.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.misscellapp.news.BaseActivity;
import com.misscellapp.news.DividerItemDecoration;
import com.misscellapp.news.R;
import com.misscellapp.news.utils.Constants;
import com.misscellapp.news.utils.ErrorListener;
import com.misscellapp.news.utils.Listener;
import com.misscellapp.news.utils.NetworkRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

/**
 * Created by jishichen on 2017/5/10.
 */
public class CommentActivity extends BaseActivity implements Listener<String>, ErrorListener {

    private CommentListAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setTitle(R.string.comment);

        mListAdapter = new CommentListAdapter();

        String postId = getIntent().getExtras().getString("post_id");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(mListAdapter);

        showLoading();
        String url = String.format(Constants.COMMENTS, postId, System.currentTimeMillis());
        NetworkRequest.getInstance().get(url, this, this);
    }

    @Override
    public void onResponse(String response) {
        if (!isEmpty(response)) {
            Document doc = Jsoup.parse(response);
            if (null != doc) parseComment(doc);
        }

        hideLoading();
    }

    private void parseComment(Document doc) {
        List<Comment> commentList = new ArrayList<>();

        Elements infos = doc.getElementsByClass("commenter_info");
        if (null != infos && infos.size() > 0) {
            for (Element el : infos) {
                Comment comment = new Comment();

                Elements links = el.select("a.comment-author");
                if (null != links && links.size() > 0) {
                    comment.name = links.get(0).text();
                }

                Elements times = el.select("span.time");
                if (null != times && times.size() > 0) {
                    comment.time = times.get(0).text();
                }
                commentList.add(comment);
            }
        }

        Elements contents = doc.getElementsByClass("comment_main");
        if (null != contents && contents.size() > 0) {
            for (int i = 0; i < contents.size(); i++) {
                Comment comment = commentList.get(i);
                if (null != comment) {
                    comment.content = contents.get(i).text();
                }
            }
        }

        if (commentList.size() > 0) {
            mListAdapter.addData(commentList);
        }
    }

    @Override
    public void onErrorResponse() {
        hideLoading();
    }

    private class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private final List<Comment> dataList = new ArrayList<>();

        public CommentListAdapter() {
            super();
        }

        public void addData(List<Comment> list) {
            dataList.clear();
            dataList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(CommentActivity.this).inflate(R.layout.item_comment,
                    parent, false);
            return new CommentViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = dataList.get(position);

            holder.floorView.setText(getString(R.string.floor_index, position + 1));
            String userInfo = "<font color='#0D47A1'>" + comment.name + "</font> " + comment.time;
            holder.nameView.setText(Html.fromHtml(userInfo));
            holder.contentView.setText(Html.fromHtml(comment.content));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }
}
