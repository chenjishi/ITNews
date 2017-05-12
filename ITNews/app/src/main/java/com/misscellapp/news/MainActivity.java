package com.misscellapp.news;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends BaseActivity implements Callback, OnListScrollListener.OnPageEndListener,
        OnRefreshListener, View.OnClickListener {

    private static final String BASE_URL = "http://news.cnblogs.com";

    private FeedListAdapter mListAdapter;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int mPage = 1;

    private final OkHttpClient mClient = new OkHttpClient();

    private OnListScrollListener mScrollListener;

    private SwipeRefreshLayout mRefreshLayout;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        findViewById(R.id.left_button).setVisibility(View.GONE);
        setTitle(R.string.app_name);

        findViewById(R.id.title_layout).setOnClickListener(this);
        mListAdapter = new FeedListAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        mScrollListener = new OnListScrollListener(mLayoutManager, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feed_list);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.addOnScrollListener(mScrollListener);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);

        request();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_layout) {
            mLayoutManager.scrollToPositionWithOffset(0, 0);
        }
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
        if (isEmpty(html)) return null;

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
                String href = titleLink.attr("href");
                if (!isEmpty(href)) {
                    feed.id = href.replaceAll("\\D+", "");
                }
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
                String str = comments.get(0).select("a").first().text();
                str = str.replaceAll("\\D+", "");
                if (!isEmpty(str)) feed.commentNum = Integer.parseInt(str);
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


    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        boolean flag = true;
        for (String s : permissions) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }

        if (!flag) requestPermissions(permissions, 233);
    }
}
