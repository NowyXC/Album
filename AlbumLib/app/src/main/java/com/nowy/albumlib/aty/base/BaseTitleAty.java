package com.nowy.albumlib.aty.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

import com.nowy.albumlib.view.titlebar.TitleBar;
import com.nowy.baselib.activity.BasePermissionAty;

import butterknife.ButterKnife;

/**
 * Created by Nowy on 2017/12/27.
 * 自动导入标题栏
 * 调用方式
 * @see BaseTitleAty#setContentView(int, int)
 * @see BaseTitleAty#setContentView(int, String)
 * @see BaseTitleAty#setContentView(int, TitleBar)
 * @see BaseTitleAty#setContentView(int, int,TitleBar.Gravity)
 * @see BaseTitleAty#setContentView(int, String,TitleBar.Gravity)
 */

public class BaseTitleAty extends BasePermissionAty {

    protected TitleBar mTitleBar;
    protected View mContentView;
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(LayoutInflater.from(this).inflate(layoutResID,null));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        this.mContentView = view;
        ButterKnife.bind(this);
    }

    /**
     * 设置contentView和导航的标题
     *
     * @param layoutResID
     * @param title
     */
    public void setContentView(@LayoutRes int layoutResID, String title) {
        setContentView(layoutResID, title, null);
    }


    /**
     * 设置contentView和导航的标题
     *
     * @param layoutResID
     * @param title
     */
    public void setContentView(@LayoutRes int layoutResID, @StringRes int title) {
        setContentView(layoutResID, title, null);
        ButterKnife.bind(this);
    }


    public void setContentView(@LayoutRes int layoutResID, @StringRes int title, TitleBar.Gravity gravity) {
        setContentView(layoutResID);
        mTitleBar = new TitleBar.Builder(this)
                .setTitle(title, gravity)
                .attach();
    }


    public void setContentView(@LayoutRes int layoutResID, String title, TitleBar.Gravity gravity) {
        setContentView(layoutResID);
        mTitleBar = new TitleBar.Builder(this)
                .setTitle(title, gravity)
                .attach();
    }


    /**
     * 设置contentView和导航
     *
     * @param layoutResID
     * @param titleBar
     */
    public void setContentView(@LayoutRes int layoutResID, TitleBar titleBar) {
        setContentView(layoutResID);
        if (titleBar != null) {
            mTitleBar = titleBar;
            mTitleBar.attach(this);
        }

    }


    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
        }
    }


    public View getContentView() {
        return mContentView;
    }

    public TitleBar getTitleBar() {
        return mTitleBar;
    }
}
