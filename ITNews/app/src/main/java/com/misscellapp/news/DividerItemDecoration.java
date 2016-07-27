package com.misscellapp.news;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by chenjishi on 16/1/19.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DividerItemDecoration(Context context) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(context.getResources().getColor(R.color.divider_color));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + 1;

            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
