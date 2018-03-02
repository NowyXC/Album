package com.nowy.albumlib.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nowy.albumlib.R;
import com.nowy.albumlib.album.adapter.AlbumChoseAdapter;
import com.nowy.albumlib.album.listener.OnItemCheckedListener;
import com.nowy.albumlib.album.listener.OnItemClickListener;
import com.nowy.albumlib.album.pop.ChoseFolderPop;
import com.nowy.albumlib.aty.base.BaseStateTitleAty;
import com.nowy.albumlib.view.recyclerView.divider.Divider;
import com.nowy.albumlib.view.recyclerView.divider.DividerUtil;
import com.nowy.albumlib.view.recyclerView.divider.SpacesItemDecoration;
import com.nowy.albumlib.view.titlebar.TitleBar;
import com.nowy.baselib.manager.FileManager;
import com.nowy.baselib.manager.ForegroundMaskManager;
import com.nowy.baselib.utils.AppUtil;
import com.nowy.baselib.utils.DeviceUtil;
import com.nowy.baselib.utils.T;
import com.nowy.library.Album;
import com.nowy.library.bean.AlbumFile;
import com.nowy.library.bean.AlbumFolder;
import com.nowy.library.manager.AlbumManager;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Nowy on 2018/2/25.
 */

public class AlbumAty extends BaseStateTitleAty implements AlbumManager.ScanListener,ChoseFolderPop.ChoseListener,
                                    AlbumManager.MultiCheckedFileListener, View.OnClickListener{

    public static final int REQ_CODE_GALLERY = 120;
    public static final String BUNDLE_MODE = "mode";//选择模式
    public static final String BUNDLE_HAS_CAMERA = "hasCamera";//是否有拍照功能
    public static final String BUNDLE_LIMIT_COUNT = "limitCount";//选择数量
    public static final String BUNDLE_CHECKED_LIST = "checkedList";//传入的已选中列表
    public static final String BUNDLE_CHECKED_PATH = "checkedPath";//已选中路径
    public static final String BUNDLE_NEED_CROP = "needCrop";
    private static final int MODE_SINGLE = 0;//单选
    private static final int MODE_MULTIPLE = 1;//多选

    @BindView(R.id.album_RvAlbum)
    RecyclerView mRvAlbum;
    @BindView(R.id.album_StubBottom)
    ViewStub mStubBottom;
    private AlbumManager mAlbumManager;
    private AlbumAdapter mAlbumAdapter;
    private ChoseFolderPop mChoseFolderPop;
    private int mChoiceMode;
    private ArrayList<AlbumFile> mCheckedList;
    private int mLimitCount;
    private boolean mHasCamera;
    private View mBottomView;
    private RecyclerView mRvChoseList;
    private TextView mTvConfirm;
    private AlbumChoseAdapter mChoseAdapter;
    private boolean mIsNeedCrop;


    public static void start(Context context) {
        Intent starter = new Intent(context, AlbumAty.class);
        context.startActivity(starter);
    }

    /**
     * 单选
     * @param context
     * @param hasCamera
     * @param checkedList
     * @param reqCode
     */
    public static void startSingle(Activity context,boolean hasCamera, ArrayList<AlbumFile> checkedList,boolean isNeedCrop,int reqCode) {
        start(context,hasCamera,MODE_SINGLE,1,checkedList,isNeedCrop,reqCode);
    }


    public static void startSingle(Fragment fragment,boolean hasCamera, ArrayList<AlbumFile> checkedList,boolean isNeedCrop,int reqCode) {
        start(fragment,hasCamera,MODE_SINGLE,1,checkedList,isNeedCrop,reqCode);
    }

    /**
     * 多选
     * @param context
     * @param hasCamera
     * @param count
     * @param checkedList
     * @param reqCode
     */
    public static void startMultiple(Activity context,boolean hasCamera,int count, ArrayList<AlbumFile> checkedList,boolean isNeedCrop,int reqCode) {
        start(context,hasCamera,MODE_MULTIPLE,count,checkedList,isNeedCrop,reqCode);
    }

    public static void startMultiple(Fragment fragment,boolean hasCamera,int count, ArrayList<AlbumFile> checkedList,boolean isNeedCrop,int reqCode) {
        start(fragment,hasCamera,MODE_MULTIPLE,count,checkedList,isNeedCrop,reqCode);
    }

    public static void start(Activity context, boolean hasCamera, int mode, int count, ArrayList<AlbumFile> checkedList,boolean isNeedCrop,int reqCode) {
        Intent starter = new Intent(context, AlbumAty.class);
        starter.putExtra(BUNDLE_HAS_CAMERA,hasCamera);
        starter.putExtra(BUNDLE_MODE,mode);
        starter.putExtra(BUNDLE_LIMIT_COUNT,count);
        starter.putExtra(BUNDLE_CHECKED_LIST,checkedList);
        starter.putExtra(BUNDLE_NEED_CROP,isNeedCrop);
        context.startActivityForResult(starter,reqCode);
    }

    public static void start(Fragment fragment, boolean hasCamera, int mode, int count, ArrayList<AlbumFile> checkedList,boolean isNeedCrop,int reqCode) {
        Intent starter = new Intent(fragment.getContext(), AlbumAty.class);
        starter.putExtra(BUNDLE_HAS_CAMERA,hasCamera);
        starter.putExtra(BUNDLE_MODE,mode);
        starter.putExtra(BUNDLE_LIMIT_COUNT,count);
        starter.putExtra(BUNDLE_CHECKED_LIST,checkedList);
        starter.putExtra(BUNDLE_NEED_CROP,isNeedCrop);
        fragment.startActivityForResult(starter,reqCode);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album, createTitleBar());
        initData();
        initView();
        loadData();
    }


    private void initData(){
        mHasCamera = getIntent().getBooleanExtra(BUNDLE_HAS_CAMERA,false);
        mChoiceMode = getIntent().getIntExtra(BUNDLE_MODE,MODE_SINGLE);
        mLimitCount = getIntent().getIntExtra(BUNDLE_LIMIT_COUNT,1);
        mCheckedList = getIntent().getParcelableArrayListExtra(BUNDLE_CHECKED_LIST);

        mIsNeedCrop = getIntent().getBooleanExtra(BUNDLE_NEED_CROP,false);


        mAlbumManager = new AlbumManager(this, Album.FUNCTION_CHOICE_IMAGE,mLimitCount);
        mAlbumManager.setOnCheckedFileListener(this);
    }


    private TitleBar createTitleBar() {
        return new TitleBar.Builder(this)
                .setTitle("图片")
                .showRightTxt("全部图片", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mChoseFolderPop.showPop(getTitleBar(),mAlbumManager.getAlbumFiles(),AlbumAty.this);
                    }
                }).create();
    }


    private void initView() {
        initPop();
        initRecycler();
        if(mChoiceMode == MODE_MULTIPLE){
            initViewStub();

        }

    }

    private void initPop(){
        mChoseFolderPop = new ChoseFolderPop(this,
                ForegroundMaskManager.initMaskManager((FrameLayout) getContentViewNoTitle()));
    }



    private void initRecycler() {
        int mColumnCount = 3;
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, mColumnCount);
        mRvAlbum.setLayoutManager(mLayoutManager);
        Divider divider = DividerUtil.getDivider(ContextCompat.getColor(this,R.color.durban_White));
        mRvAlbum.addItemDecoration(divider);
        int itemSize = (DeviceUtil.getDeviceWidth(this) - divider.getWidth() * (mColumnCount + 1)) / mColumnCount;
        mAlbumAdapter = new AlbumAdapter(mChoiceMode,itemSize,mHasCamera);
        mAlbumAdapter.setAddClickListener(getAddChickListener());
        mAlbumAdapter.setItemClickListener(getOnSingleClickListener());
        mAlbumAdapter.setItemCheckedListener(getMultipleCheckedListener());
        RecyclerView.ItemAnimator animator = mRvAlbum.getItemAnimator();

        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRvAlbum.setAdapter(mAlbumAdapter);
    }


    private void initViewStub(){
        if(mBottomView == null){
            mBottomView = mStubBottom.inflate();
            mRvChoseList = mBottomView.findViewById(R.id.album_chose_bottom_bar_RvChose);
            mTvConfirm = mBottomView.findViewById(R.id.album_chose_bottom_bar_TvConfirm);
            mTvConfirm.setOnClickListener(this);
            initChoseList();
        }
    }

    private void initChoseList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRvChoseList.setLayoutManager(layoutManager);
        mRvChoseList.addItemDecoration(new SpacesItemDecoration(AppUtil.dip2px(12),SpacesItemDecoration.HORIZONTAL));
        mChoseAdapter = new AlbumChoseAdapter(mAlbumManager.getCheckedFiles(),getChoseListClickListener());
        mRvChoseList.setAdapter(mChoseAdapter);

    }


    private void loadData() {
//        List<AlbumFile> data = new ArrayList<>();
        showLoadingView();
        mAlbumManager.loadData(mCheckedList,this);
    }


    @Override
    public void onScanFinish(ArrayList<AlbumFolder> folders) {
        if(mAlbumManager == null) return;

        mAlbumManager.showAlbumFileFromFolder(0);
        if(getTitleBar() != null){
            getTitleBar().setRightTxt("全部图片");
        }
        if(mAlbumAdapter != null && mAlbumManager.getCurrentAlbumFolder() != null){
            mAlbumAdapter.notifyDataSetChanged(mAlbumManager.getCurrentAlbumFolder().getAlbumFiles());
            showDataView(200);
        }

        notifyChoseList();

    }

    @Override
    public void chose(int position) {
        if(mAlbumManager == null) return;

        mAlbumManager.showAlbumFileFromFolder(position);
        if(getTitleBar() != null){
            getTitleBar().setRightTxt(position == 0 ? "全部图片" : mAlbumManager.getCurrentFolderName());
        }
        notifyList();
    }





    @Override
    protected void onNetReload(View v) {

    }


    //拍照
    private OnItemClickListener getAddChickListener(){
        return new OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                T.s("拍照");
            }
        };
    }

    //单选
    private OnItemClickListener getOnSingleClickListener(){
        return new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mAlbumManager.getCurrentAlbumFolder() != null
                        && mAlbumManager.getCurrentAlbumFolder().getAlbumFiles().size() > position){
//                    AlbumFile file =  mAlbumManager.getCurrentAlbumFolder().getAlbumFiles().get(position);

                    mAlbumManager.checkedFile(mAlbumManager.getCurrentAlbumIndex(),position,true,"");

                    if(mIsNeedCrop){
                        cropImg();
                    }else{
                        setResult(mAlbumManager.getCheckedPath());
                    }


                }

            }
        };
    }

    //裁剪
    private void cropImg(){
        ArrayList<String> filePaths = new ArrayList<>();
        for(AlbumFile albumFile : mAlbumManager.getCheckedFiles()){
            if(albumFile.isChecked()){
                filePaths.add(albumFile.getPath());
            }
        }
        mAlbumManager.cropFile(AlbumAty.this,filePaths, FileManager.getCacheImageCutPath());
    }


    //多选
    private OnItemCheckedListener getMultipleCheckedListener(){
        return new OnItemCheckedListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, int position, boolean isChecked) {
                mAlbumManager.checkedFile(mAlbumManager.getCurrentAlbumIndex(),position,isChecked,
                        String.format(Locale.CHINA,"最多只能选%d张图片",mLimitCount));
                if(mAlbumAdapter != null){
                    int rawPosition = mHasCamera ? position + 1 : position;
                    mAlbumAdapter.notifyItemChanged(rawPosition);
                }

            }
        };
    }


    //选中列表点击事件
    private OnItemClickListener getChoseListClickListener(){
        return new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<AlbumFile> albumFiles = mAlbumManager.getCheckedFiles();
                if(albumFiles != null){
                    ArrayList<String> checkedPath = new ArrayList<>();
                    for(AlbumFile file : albumFiles){
                        if(file.isChecked()){
                            checkedPath.add(file.getPath());
                        }
                    }
                    if(checkedPath.size() > 0)
                        AlbumGalleryAty.start(AlbumAty.this,checkedPath,position,true,REQ_CODE_GALLERY);
                }

            }
        };
    }


    @Override
    public void check(int position) {
        notifyChoseList();
    }


    //刷新相册列表
    private void notifyList(){
        if(mAlbumAdapter != null && mAlbumManager.getCurrentAlbumFolder() != null){
            mAlbumAdapter.notifyDataSetChanged(mAlbumManager.getCurrentAlbumFolder().getAlbumFiles());
        }
    }


    //刷新选中列表
    private void notifyChoseList(){
        if(mChoiceMode == MODE_MULTIPLE && mChoseAdapter != null){
            mTvConfirm.setText(String.format(Locale.CHINESE,"完成(%d)",mAlbumManager.getCheckedFiles().size()));
            mChoseAdapter.notifyDataSetChanged();
            if(mRvChoseList != null)
                mRvChoseList.scrollToPosition(mChoseAdapter.getItemCount()-1);
        }
    }


    @Override
    public void onError(String msg) {
        if(!TextUtils.isEmpty(msg))
            showTips(msg);
    }


    private void setResult(ArrayList<String> checkPaths){
        Intent intent = getIntent();
        intent.putParcelableArrayListExtra(BUNDLE_CHECKED_LIST,mAlbumManager.getCheckedFiles());
        intent.putStringArrayListExtra(BUNDLE_CHECKED_PATH,checkPaths);
        setResult(RESULT_OK,intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_GALLERY && resultCode == RESULT_OK){
            if(data != null){
                ArrayList<String> checkedPaths = data.getStringArrayListExtra(AlbumGalleryAty.BUNDLE_CHECKED_LIST);
                mAlbumManager.updateByPaths(checkedPaths);
                notifyList();
                notifyChoseList();
            }
        }else{//裁剪结果
            ArrayList<String> cropList = (ArrayList<String>) mAlbumManager.onActivityResult(requestCode,resultCode,data);
            if(cropList != null){
                setResult(cropList);
            }
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.album_chose_bottom_bar_TvConfirm:
                setResult(mAlbumManager.getCheckedPath());
                break;
        }
    }
}
