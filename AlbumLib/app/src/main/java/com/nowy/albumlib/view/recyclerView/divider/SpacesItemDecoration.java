package com.nowy.albumlib.view.recyclerView.divider;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    private int mSpace;//PX
    private int mOrientation;
    private boolean mShowFirst;

    public SpacesItemDecoration(int space) {
        this(space,VERTICAL);
    }

    public SpacesItemDecoration(int mSpace, int mOrientation) {
        this(mSpace,mOrientation,false);
    }

    public SpacesItemDecoration(int mSpace, boolean showFirst) {
        this(mSpace,VERTICAL,showFirst);
    }

    public SpacesItemDecoration(int space, int orientation, boolean showFirst) {
        this.mSpace = space;
        this.mOrientation = orientation;
        this.mShowFirst = showFirst;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            if (!mShowFirst && parent.getChildLayoutPosition(view) == 0) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0,mSpace, 0, 0);
            }
        } else {
            if (!mShowFirst && parent.getChildLayoutPosition(view) == 0){
                outRect.set(0, 0, 0, 0);
            }else{
                outRect.set(mSpace, 0,0 , 0);
            }

        }

    }
}