package com.misscellapp.news.article;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.misscellapp.news.BaseActivity;
import com.misscellapp.news.comment.CommentActivity;
import com.misscellapp.news.Feed;
import com.misscellapp.news.R;
import com.misscellapp.news.utils.ErrorListener;
import com.misscellapp.news.utils.Listener;
import com.misscellapp.news.utils.NetworkRequest;
import com.misscellapp.news.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.text.TextUtils.isEmpty;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by jishichen on 2017/5/2.
 */
public class DetailsActivity extends BaseActivity implements Listener<Article>, ErrorListener,
        JSCallback, View.OnClickListener {
    private static final int TAG_SHARE = 233;
    private static final int TAG_FAVORITE = 234;
    private static final int TAG_COMMENT = 235;


    private WebView mWebView;

    private Feed mFeed;

    private Article mArticle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle args = getIntent().getExtras();
        mFeed = args.getParcelable("feed");
        String url = mFeed.url;

        findViewById(R.id.title_layout).setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScriptBridge(this), "U148");

        showLoading();
        NetworkRequest.getInstance().getArticle(url, Article.class, this, this);
        generateButtons();
    }

    @Override
    public void onResponse(Article response) {
        hideLoading();
        if (null == response) return;

        mArticle = response;
        renderPage(response);
    }

    @Override
    public void onErrorResponse() {
        setError();
    }

    @Override
    public void onImageClicked(String url) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("imgsrc", url);
        intent.putStringArrayListExtra("image_list", mArticle.imageList);
        startActivity(intent);
    }

    @Override
    public void onVideoClicked(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onThemeChange() {

    }

    private void renderPage(Article article) {
        Document doc = Jsoup.parse(article.content);
        if (null == doc) return;

        handleVideos(doc);
        article.content = doc.html();

        String template = Utils.readFromAssets(this, "usite.html");
        template = template.replace("{TITLE}", mFeed.title);
        template = template.replace("{U_AUTHOR}", mFeed.time);
        template = template.replace("{U_COMMENT}", mFeed.views);
        template = template.replace("{CONTENT}", article.content);

        mWebView.loadDataWithBaseURL(null, template, "text/html", "UTF-8", null);
    }

    private void handleVideos(Document doc) {
        Elements videos = doc.select("p");
        String videoText = getString(R.string.video_url);

        for (Element el : videos) {
            String text = el.text();
            if (!isEmpty(text) && text.contains(videoText)) {
                Elements links = el.select("a");
                if (null != links && links.size() > 0) {
                    String url = links.get(0).attr("href");
                    String html = videoText + ":<a href=\"" + url + "\">" + url +
                            "</a><img src=\"file:///android_asset/video.png\" title=\"" + url + "\" />";
                    el.html(html);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_layout) {
            ObjectAnimator animator = ObjectAnimator.ofInt(mWebView, "scrollY",
                    mWebView.getScrollY(), 0);
            animator.setDuration(360);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.start();
            return;
        }

        if (null == v.getTag()) return;

        int tag = (Integer) v.getTag();
        switch (tag) {
            case TAG_COMMENT:
                if (mFeed.commentNum > 0) {
                    Intent intent = new Intent(this, CommentActivity.class);
                    intent.putExtra("post_id", mFeed.id);
                    startActivity(intent);
                } else {
                    Utils.toast(this, R.string.no_comments);
                }
                break;
        }
    }

    private void generateButtons() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.right_view);
        layout.removeAllViews();

        int[] icons = {R.drawable.ic_social_share, R.drawable.ic_favorite,
                R.drawable.ic_comment};
//        if (mDatabase.isFavorite(mFeed.url)) {
//            icons[1] = R.drawable.ic_favorite_full;
//        }

        for (int i = 0; i < icons.length; i++) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dp2px(48), MATCH_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.rightMargin = i * dp2px(48);
            ImageButton button = getImageButton(icons[i]);
            button.setTag(i + TAG_SHARE);
            button.setOnClickListener(this);
            layout.addView(button, lp);
        }

        if (mFeed.commentNum > 0) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dp2px(12),
                    dp2px(12));
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.rightMargin = dp2px(48) * 2 + dp2px(4);
            lp.topMargin = dp2px(8);
            CircleView numView = new CircleView(this);
            numView.setNumber(mFeed.commentNum);
            layout.addView(numView, lp);
        }
    }

    protected ImageButton getImageButton(int resId) {
        ImageButton button = new ImageButton(this);
        button.setBackgroundResource(R.drawable.highlight_bkg);
        button.setImageResource(resId);

        return button;
    }
}
