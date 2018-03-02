package com.nowy.albumlib.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.nowy.albumlib.R;
import com.nowy.albumlib.album.adapter.BasicPreviewAdapter;
import com.nowy.albumlib.album.adapter.PathPreviewAdapter;
import com.nowy.albumlib.aty.base.BaseTitleAty;
import com.nowy.albumlib.view.photoview.FixViewPager;
import com.nowy.albumlib.view.titlebar.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Nowy on 2018/3/1.
 */

public class AlbumGalleryAty extends BaseTitleAty {
    public static final String BUNDLE_CHECKED_LIST = "checkedList";//传入的已选中列表
    public static final String BUNDLE_ITEM_POSITION = "ItemPosition";//下标
    public static final String BUNDLE_SHOW_DEL = "showDel";//删除按钮
    @BindView(R.id.gallery_FPager)
    FixViewPager mFPager;
    private String mTitle;
    private ArrayList<String> mPathList;
    private int mCurrentItemPosition;
    private BasicPreviewAdapter mPreviewAdapter;
    private boolean mIsShowDel = false;
    private boolean mHasDel = false;//是否有删除图片


    /**
     * 画廊
     * @param fragment
     * @param pathList 路径集合
     * @param itemPosition 默认位置
     * @param isShowDel 是否显示删除按钮
     * @param reqCode
     */
    public static void start(Fragment fragment, ArrayList<String> pathList,int itemPosition,boolean isShowDel, int reqCode) {
        Intent starter = new Intent(fragment.getContext(), AlbumGalleryAty.class);
        starter.putStringArrayListExtra(BUNDLE_CHECKED_LIST,pathList);
        starter.putExtra(BUNDLE_SHOW_DEL,isShowDel);
        starter.putExtra(BUNDLE_ITEM_POSITION,itemPosition);
        fragment.startActivityForResult(starter,reqCode);
    }

    /**
     * 画廊
     * @param activity
     * @param pathList 路径集合
     * @param itemPosition 默认位置
     * @param isShowDel 是否显示删除按钮
     * @param reqCode
     */
    public static void start(Activity activity, ArrayList<String> pathList,int itemPosition,boolean isShowDel, int reqCode) {
        Intent starter = new Intent(activity, AlbumGalleryAty.class);
        starter.putStringArrayListExtra(BUNDLE_CHECKED_LIST,pathList);
        starter.putExtra(BUNDLE_SHOW_DEL,isShowDel);
        starter.putExtra(BUNDLE_ITEM_POSITION,itemPosition);
        activity.startActivityForResult(starter,reqCode);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_gallery,createTitleBar());
        getTitleBar().setRight01Visibility(mIsShowDel ? View.VISIBLE : View.GONE);
        initView();
    }



    private void initData(){
        Intent intent = getIntent();
        mPathList = intent.getStringArrayListExtra(BUNDLE_CHECKED_LIST);
        mIsShowDel = intent.getBooleanExtra(BUNDLE_SHOW_DEL,false);
        mCurrentItemPosition = intent.getIntExtra(BUNDLE_ITEM_POSITION,0);
        if(mPathList != null){
            mTitle = (mCurrentItemPosition+1)+"/"+ mPathList.size();
        }
    }

    private TitleBar createTitleBar(){
        return new TitleBar.Builder(this)
                .setTitle(mTitle)
                .showRightImg(R.drawable.ic_clear,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        remove(mCurrentItemPosition);
                    }
                }).create();

    }


    private void initView(){
        initViewPager();
    }


    private void initViewPager(){
        if (mPathList != null) {
            if (mPathList.size() > 3)
                mFPager.setOffscreenPageLimit(3);
            else if (mPathList.size() > 2)
                mFPager.setOffscreenPageLimit(2);
        }
        mFPager.addOnPageChangeListener(mPageChangeListener);

        mPreviewAdapter = new PathPreviewAdapter(this, mPathList);
        mFPager.setAdapter(mPreviewAdapter);
        mFPager.setCurrentItem(mCurrentItemPosition);
        mPageChangeListener.onPageSelected(mCurrentItemPosition);
    }



    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mCurrentItemPosition = position;
            updateTitle(mCurrentItemPosition);
        }
    };


    private void updateTitle(int position){
        mTitle = (position + 1) + " / " + mPathList.size();
        getTitleBar().setTitle(mTitle);
    }

    private void remove(int position){
        if(mPathList == null || mPathList.size() < position) return;
        mHasDel = true;
        mPathList.remove(position);


        if(mPathList.size() == 0){
            finish();
        }else{
            updateTitle(position);
            if(mPreviewAdapter != null){
                mPreviewAdapter.notifyDataSetChanged();
            }
        }


    }



    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        intent.putStringArrayListExtra(BUNDLE_CHECKED_LIST,mPathList);
        setResult(mHasDel ? RESULT_OK : RESULT_CANCELED,intent);

        super.finish();
    }


}
