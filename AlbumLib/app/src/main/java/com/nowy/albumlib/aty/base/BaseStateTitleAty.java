package com.nowy.albumlib.aty.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nowy.albumlib.view.callback.EmptyCallback;
import com.nowy.albumlib.view.callback.ErrorCallback;
import com.nowy.albumlib.view.callback.LoadingCallback;
import com.nowy.albumlib.view.titlebar.TitleBar;
import com.nowy.baselib.utils.T;
import com.nowy.baselib.utils.TextUtil;
import com.nowy.baselib.views.loadsir.callback.Callback;
import com.nowy.baselib.views.loadsir.core.LoadService;
import com.nowy.baselib.views.loadsir.core.LoadSir;

import java.util.List;

/**
 * Created by Nowy on 2017/12/26.
 * 定义各类回调界面
 * a.加载界面
 * b.错误界面
 * c.展示列表时的空界面
 */

public abstract class BaseStateTitleAty extends BaseTitleAty implements Callback.OnReloadListener{

    protected LoadService mLoadService;

    private View mOldContentView;//真实的当前界面（如果有titleBar，不包含）
    private View mNewContentView;

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initLoadSir();
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
//        initLoadSir();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initLoadSir();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID, @StringRes int title, TitleBar.Gravity gravity) {
        mOldContentView = LayoutInflater.from(this).inflate(layoutResID,null);
        mNewContentView = createContentView(mOldContentView,getResources().getString(title),gravity);
        setContentView(mNewContentView);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID, String title, TitleBar.Gravity gravity) {
        mOldContentView = LayoutInflater.from(this).inflate(layoutResID,null);
        mNewContentView = createContentView(mOldContentView,title,gravity);
        setContentView(mNewContentView);
    }


    /**
     * 设置contentView和导航
     *
     * @param layoutResID
     * @param titleBar
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID, TitleBar titleBar) {
        mOldContentView = LayoutInflater.from(this).inflate(layoutResID,null);
        mNewContentView = createContentView(mOldContentView,titleBar);
        setContentView(mNewContentView);
    }



    private ViewGroup createContentView(View contentView,TitleBar titleBar){
        View oldContentView = contentView;

        ViewGroup newContentView;

         mTitleBar = titleBar;

        if(oldContentView instanceof LinearLayout){
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            newContentView = new LinearLayout(this);
            ((LinearLayout)newContentView).setOrientation( LinearLayout.VERTICAL);
//
            newContentView.addView(mTitleBar,0);
            newContentView.addView(oldContentView,lp);
        }else{
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            newContentView = new FrameLayout(this);

            newContentView.addView(oldContentView,lp);
            newContentView.addView(mTitleBar);
        }
        return newContentView;
    }




    private ViewGroup createContentView(View contentView,String title,TitleBar.Gravity gravity){
        View oldContentView = contentView;

        ViewGroup newContentView;
        mTitleBar = new TitleBar.Builder(this)
                .setTitle(title, gravity)
                .create();
        if(oldContentView instanceof LinearLayout){
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            newContentView = new LinearLayout(this);
            ((LinearLayout)newContentView).setOrientation( LinearLayout.VERTICAL);
            newContentView.addView(mTitleBar,0);
            newContentView.addView(oldContentView,lp);
        }else{
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            newContentView = new FrameLayout(this);

            newContentView.addView(oldContentView,lp);
            newContentView.addView(mTitleBar);
        }
        return newContentView;
    }



    private void initLoadSir(){
        if(mNewContentView == null){//表示没有新构建布局
            mLoadService = LoadSir.getDefault().register(this,this);
        }else{
            mLoadService = LoadSir.getDefault().register(mOldContentView,this);
        }
        //loadService.showWithConvertor(httpResult);
    }


    @Override
    public void onReload(View v) {
        mLoadService.showCallback(LoadingCallback.class);
        onNetReload(v);
    }


    /**
     * 显示错误页
     */
    public void showErrorView(String msg){
        String tmp = msg == null?"加载失败":msg;
        mLoadService.showCallback(ErrorCallback.class,tmp);
    }

    /**
     * 显示空页面
     */
    public void showEmptyView(String msg){
        String tmp = msg == null?"暂无数据":msg;
        mLoadService.showCallback(EmptyCallback.class,tmp);
    }

    /**
     * 显示加载页
     */
    public void showLoadingView(){
        mLoadService.showCallback(LoadingCallback.class);
    }



    /**
     * 显示数据页
     */
    public void showDataView(){
        mLoadService.showSuccess();
    }


    /**
     * 延时显示对话框
     * @param delay
     */
    public void showDataView(long delay){
        mLoadService.showSuccessDelayed(delay);
    }


    /**
     * 此方法就默认的数据加载后的视图切换，在响应成功处调用
     * 1.老数据存在时，显示数据页,错误提示errorMsg
     * 2.老数据不存在时，新数据存在，显示数据页
     * 3.老数据不存在时，新数据不存在，根据errorMsg显示
     *      a.errorMsg没空，显示空数据页面
     *      b.errorMsg不为空，显示错误页面
     * @param oldData 已显示在UI的列表数据
     * @param newData 网络加载返回的新数据
     * @param errorMsg 错误提示信息
     */
    public void showListViewDef(List oldData, List newData, String errorMsg){
        if(oldData != null && oldData.size() > 0 ){//存在老数据,无论什么失败都显示数据页面，只是错误会以提示的方式显示
            showDataView();
            if(!TextUtil.isEmpty(errorMsg))
                T.s(errorMsg);
        }else{
            if(newData != null){//存在新数据
                //显示数据页面
                showDataView();
            }else{
                if(TextUtil.isEmpty(errorMsg)){
                    showErrorView("请求失败");
                }else{
                    showEmptyView(errorMsg);
                }
            }

        }
    }


    public View getContentViewNoTitle() {
        return mOldContentView;
    }

    public View getContentView() {
        return mNewContentView;
    }

    protected abstract void onNetReload(View v);
}
