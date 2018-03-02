package com.nowy.albumlib.view.recyclerView;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.nowy.albumlib.R;

/**
 * Created by Nowy on 2018/1/24.
 */

public class CusLoadMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.loading_more_view;
    }


    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
