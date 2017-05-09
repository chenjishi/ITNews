package com.misscellapp.news;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements Callback, OnListScrollListener.OnPageEndListener, OnRefreshListener {

    private static final String BASE_URL = "http://news.cnblogs.com";

    private FeedListAdapter mListAdapter;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int mPage = 1;

    private final OkHttpClient mClient = new OkHttpClient();

    private OnListScrollListener mScrollListener;

    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.left_button).setVisibility(View.GONE);
        setTitle(R.string.app_name);

        mListAdapter = new FeedListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mScrollListener = new OnListScrollListener(layoutManager, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feed_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.addOnScrollListener(mScrollListener);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        request();
    }

    @Override
    public void onPageEnd() {
        mListAdapter.showLoadingView();

        mPage += 1;
        request();
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);

        mPage = 1;
        request();
    }

    private void request() {
        mScrollListener.setIsLoading(true);

        Request request = new Request.Builder()
                .url(String.format("%s/n/page/%d/", BASE_URL, mPage))
                .build();

        mClient.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mScrollListener.setIsLoading(false);
        stopRefresh();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            final List<Feed> feedList = parseList(response.body().string());
            response.close();

            if (null != feedList && feedList.size() > 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListAdapter.isLoading()) {
                            mListAdapter.hideLoadingView();
                        }

                        if (mPage == 1) mListAdapter.clearData();

                        mListAdapter.addData(feedList);
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                stopRefresh();
            }
        } else {
            stopRefresh();
        }
        mScrollListener.setIsLoading(false);
    }

    private void stopRefresh() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
                if (mListAdapter.isLoading()) {
                    mListAdapter.hideLoadingView();
                }
            }
        });
    }

    private List<Feed> parseList(String html) {
        if (TextUtils.isEmpty(html)) return null;

        Document doc = Jsoup.parse(html);
        if (null == doc) return null;

        Element content = doc.getElementById("news_list");
        if (null == content) return null;

        List<Feed> feedList = new ArrayList<>();
        Elements newsList = content.select("div.news_block");
        for (Element el : newsList) {
            Feed feed = new Feed();

            Elements title = el.getElementsByClass("news_entry");
            if (null != title && title.size() > 0) {
                Element titleLink = title.get(0).select("a").first();
                feed.title = titleLink.text();
                feed.url = String.format("%s%s", BASE_URL, titleLink.attr("href"));
            }

            Elements summary = el.getElementsByClass("entry_summary");
            if (null != summary && summary.size() > 0) {
                Element summaryEl = summary.get(0);
                feed.summary = summaryEl.text();

                Elements images = summaryEl.select("img");
                if (null != images && images.size() > 0) {
                    feed.imageUrl = images.first().attr("src");
                }
            }

            Elements comments = el.select("span.comment");
            if (null != comments && comments.size() > 0) {
                feed.comments = comments.get(0).select("a").first().text();
            }

            Elements views = el.select("span.view");
            if (null != views && views.size() > 0) {
                feed.views = views.get(0).text();
            }

            Elements times = el.select("span.gray");
            if (null != times && times.size() > 0) {
                feed.time = times.get(0).text();
            }

            feedList.add(feed);
        }

        return feedList;
    }
}
