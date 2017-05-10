package com.misscellapp.news.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.misscellapp.news.article.Article;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jishichen on 2017/4/26.
 */
public class NetworkRequest {
    private static final NetworkRequest INSTANCE = new NetworkRequest();

    private OkHttpClient mHttpClient;


    private Handler mHandler;

    private NetworkRequest() {
        mHttpClient = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static NetworkRequest getInstance() {
        return INSTANCE;
    }

    public void getBytes(String url,
                         final Listener<byte[]> listener,
                         final ErrorListener errorListener) {
        Request.Builder request = new Request.Builder()
                .url(url);

        mHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(errorListener);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                listener.onResponse(response.body().bytes());
            }
        });
    }

    public <T> void getArticle(String url,
                               final Class<T> clazz,
                               final Listener<T> listener,
                               final ErrorListener errorListener) {
        final Request.Builder request = new Request.Builder()
                .url(url);
        mHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(errorListener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Document doc = Jsoup.parse(html);

                final Article article = new Article();
                Element content = doc.getElementById("news_body");
                if (null != content) {
                    Elements images = content.select("img");
                    if (null != images && images.size() > 0) {
                        article.imageList = new ArrayList<>();
                        for (Element img : images) {
                            String url = "https:" + img.attr("src");
                            img.parent().html("<img src=\"" + url + "\"/>");
                            article.imageList.add(url);
                        }
                    }
                    article.content = content.html();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        T t = clazz.cast(article);
                        listener.onResponse(t);
                    }
                });
            }
        });
    }

    public void get(String url,
                    final Listener<String> listener,
                    final ErrorListener errorListener) {
        Request.Builder request = new Request.Builder()
                .url(url);

        mHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(errorListener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onSuccess(listener, response.body().string());
            }
        });
    }

    private void onError(final ErrorListener listener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onErrorResponse();
            }
        });
    }

    private void onSuccess(final Listener<String> listener, final String json) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onResponse(json);
            }
        });
    }
}
