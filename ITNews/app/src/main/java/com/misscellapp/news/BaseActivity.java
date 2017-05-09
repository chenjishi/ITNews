package com.misscellapp.news;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by chenjishi on 16/7/28.
 */
public class BaseActivity extends FragmentActivity {
    protected LayoutInflater mInflater;
    protected FrameLayout mRootView;

    protected int mTitleResId = -1;

    protected boolean mHideTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(this);
        mRootView = (FrameLayout) findViewById(android.R.id.content);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        addViewToRoot(mInflater.inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        addViewToRoot(view);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        ((TextView) findViewById(R.id.label_title)).setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    public void onLeftButtonClicked(View view) {
        finish();
    }

    public void onRightViewClicked(View view) {

    }

    protected void setContentView(int layoutResID, int titleResId) {
        mTitleResId = titleResId;
        setContentView(layoutResID);
    }

    protected void setContentView(int layoutResID, boolean hideTitle) {
        mHideTitle = hideTitle;
        setContentView(layoutResID);
    }

    private void addViewToRoot(View view) {
        mRootView.setBackgroundColor(0xF0F0F0);
        if (!mHideTitle) {
            int resId = -1 == mTitleResId ? R.layout.base_title_layout
                    : mTitleResId;
            mInflater.inflate(resId, mRootView);
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.BOTTOM);
        lp.topMargin = mHideTitle ? 0 : dp2px(48);
        mRootView.addView(view, lp);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    protected int dp2px(int n) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n,
                getResources().getDisplayMetrics()));
    }
}
