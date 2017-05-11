package com.misscellapp.news.article;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.misscellapp.news.BaseActivity;
import com.misscellapp.news.R;
import com.misscellapp.news.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by jishichen on 2017/5/11.
 */
public class ImageActivity extends BaseActivity {

    private final List<String> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_images, true);
        mRootView.setBackgroundColor(0xFF3B3B3B);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> list = extras.getStringArrayList("image_list");
        if (null != list && list.size() > 0) {
            mImageList.addAll(list);
        }

        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter();
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        for (String s : mImageList) {
            Log.i("test", "#s " + s);
        }
    }

    private boolean isGif(String url) {
        return url.endsWith("gif") || url.endsWith("GIF") || url.endsWith("Gif");
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(ImageActivity.this).inflate(R.layout.item_photo,
                    null);

            PhotoView imageView = (PhotoView) view.findViewById(R.id.image_view);
            GifMoveView gifView = (GifMoveView) view.findViewById(R.id.gif_view);

            String url = mImageList.get(position);
            if (isGif(url)) {
                imageView.setVisibility(View.GONE);

                gifView.setImageUrl(url);
                gifView.setVisibility(View.VISIBLE);
            } else {
                Glide.with(ImageActivity.this).load(mImageList.get(position)).into(imageView);

                gifView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }

            container.addView(view, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
