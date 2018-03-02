package com.nowy.library.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import com.nowy.library.Album;
import com.nowy.library.Filter;
import com.nowy.library.bean.AlbumFile;
import com.nowy.library.bean.AlbumFolder;
import com.nowy.library.listener.UpdateListener;
import com.nowy.library.mediascanner.MediaScannerUtil;
import com.nowy.library.provider.CameraFileProvider;
import com.nowy.library.task.MediaReadTask;
import com.nowy.library.task.PathConvertTask;
import com.yanzhenjie.durban.Durban;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nowy on 2018/2/26.
 * 相册管理工具
 * 主要获取相册数据，缓存选中数据列表
 * 相册数据包括，纯图片、纯视频，图片+视频混合
 * 额外功能：
 * 拍照路径转化->executeCameraAction
 * 裁剪功能->cropFile
 *
 */

public class AlbumManager implements UpdateListener {
    public static final int REQ_CODE_CROP = 200;//图片裁剪
    private Context mContext;
    @Album.ChoiceFunction
    private int mFunction;//类型
    private ArrayList<AlbumFile> mCheckedFiles = new ArrayList<>(); //新的已选中的集合
    private int mLimitSize = 6;//最大选中数量
    private Filter<Long> mSizeFilter;    //大小过滤
    private Filter<String> mMimeFilter;  //类型过滤
    private Filter<Long> mDurationFilter;//时长过滤

    private boolean mFilterVisibility;//过滤文件是否可见


    private ArrayList<AlbumFolder> mAlbumFolders;
    private AlbumFolder mCurrentAlbumFolder;
    private int mCurrentAlbumIndex = 0;
    private MultiCheckedFileListener mMultiCheckedFileListener;
    public AlbumManager(Context context,int function,int limitSize){
        this.mContext = context;
        this.mFunction = function;
        this.mLimitSize = limitSize;
    }

    /**
     * 加载数据
     * @param checkedFiles 原来已有的已选中集合
     * @param listener 扫描完成监听，可做空数据等处理
     */
    public void loadData(ArrayList<AlbumFile> checkedFiles,ScanListener listener){
        MediaReadTask scanTask = new MediaReadTask(mContext, mFunction, new ScanCallback(this,listener), mCheckedFiles,
                mSizeFilter, mMimeFilter, mDurationFilter, mFilterVisibility);
        scanTask.execute(checkedFiles);
    }

    public void loadData(ArrayList<AlbumFile> checkedFiles,
                          Filter<Long> mSizeFilter,
                          Filter<String> mMimeFilter,
                          Filter<Long> mDurationFilter,
                          boolean filterVisibility,ScanListener listener){
        MediaReadTask scanTask = new MediaReadTask(mContext, mFunction, new ScanCallback(this,listener), mCheckedFiles,
                mSizeFilter, mMimeFilter, mDurationFilter, filterVisibility);
        scanTask.execute(checkedFiles);
    }


    /**
     * Update data source.
     */
    public void showAlbumFileFromFolder(int position) {
        mCurrentAlbumIndex = position;
        mCurrentAlbumFolder = mAlbumFolders.get(position);
//        mBtnSwitchFolder.setText(albumFolder.getName());
//        mAlbumContentAdapter.notifyDataSetChanged(albumFolder.getAlbumFiles());
    }


    /**
     * 获取当前的目录名称
     * @return
     */
    public String getCurrentFolderName(){
        return mCurrentAlbumFolder == null ? "" : mCurrentAlbumFolder.getName();
    }


    public AlbumFolder getCurrentAlbumFolder() {
        return mCurrentAlbumFolder;
    }

    /**
     * 获取文件目录集合
     * @return
     */
    public ArrayList<AlbumFolder> getAlbumFiles(){
        return mAlbumFolders;
    }



    /**
     * 获取已选中的文件集合
     * @return
     */
    public ArrayList<AlbumFile> getCheckedFiles() {
        return mCheckedFiles;
    }


    /**
     * 获取已选中文件路径集合
     * @return
     */
    public ArrayList<String> getCheckedPath(){
        ArrayList<String> checkPaths = new ArrayList<>();
        for(AlbumFile albumFile : mCheckedFiles){
            if(albumFile.isChecked()){
                checkPaths.add(albumFile.getPath());
            }
        }

        return checkPaths;
    }


    public void updateaByFiles(ArrayList<AlbumFile> checkedFiles) {
        if(checkedFiles == null ) return;
        this.mCheckedFiles.clear();
        List<AlbumFile> albumFiles = mAlbumFolders.get(0).getAlbumFiles();

        for (int i = 0; i < albumFiles.size(); i++) {
            AlbumFile albumFile = albumFiles.get(i);
            albumFile.setChecked(false);
            for (AlbumFile checkAlbumFile : checkedFiles) {
                if (checkAlbumFile.equals(albumFile)) {
                    albumFile.setChecked(true);
                    mCheckedFiles.add(albumFile);
                }

            }
        }
    }


    public void updateByPaths(ArrayList<String> checkedPaths) {
        if(checkedPaths == null) return;
        this.mCheckedFiles.clear();

        List<AlbumFile> albumFiles = mAlbumFolders.get(0).getAlbumFiles();

        for (int i = 0; i < albumFiles.size(); i++) {
            AlbumFile albumFile = albumFiles.get(i);
            albumFile.setChecked(false);
            for (String path : checkedPaths) {
                if (path.equals(albumFile.getPath())) {
                    albumFile.setChecked(true);
                    mCheckedFiles.add(albumFile);
                }

            }
        }
    }

    @Override
    public void update(ArrayList<AlbumFolder> folders) {
        this.mAlbumFolders = folders;
        showAlbumFileFromFolder(0);
    }


    public void setOnCheckedFileListener(MultiCheckedFileListener multiCheckedFileListener) {
        this.mMultiCheckedFileListener = multiCheckedFileListener;
    }

    public void setFunction(@Album.ChoiceFunction int mFunction) {
        this.mFunction = mFunction;
    }

    public void setSizeFilter(Filter<Long> mSizeFilter) {
        this.mSizeFilter = mSizeFilter;
    }

    public void setMimeFilter(Filter<String> mMimeFilter) {
        this.mMimeFilter = mMimeFilter;
    }

    public void setDurationFilter(Filter<Long> mDurationFilter) {
        this.mDurationFilter = mDurationFilter;
    }

    public void setFilterVisibility(boolean mFilterVisibility) {
        this.mFilterVisibility = mFilterVisibility;
    }


    public int getCurrentAlbumIndex() {
        return mCurrentAlbumIndex;
    }

    public boolean isFilterVisibility() {
        return mFilterVisibility;
    }


    /**
     * 拍照后的回调的filePath进行处理
     * 处理包括：
     * 1.刷新系统多媒体数据库
     * 2.addFileToList：将新图片添加到0和currentIndex的目录中
     * @param filePath 新图片路径
     * @param addFileListener 添加完成监听，可以刷新选中数量和刷新图片列表
     *
     * 使用方式，
     * 拍照的完成回调中，调用此方法，传入图片路径。如果不需要操作UI可以不传监听器
     * eg:
     *   onActivityResult(int reqCode,int respCode,Intent data){
     *             //...
     *             String filePath = ...;
     *             manager.executeCameraAction(filePath,null);
     *   }
     */
    public void executeCameraAction(String filePath,AddFileListener addFileListener){
        MediaScannerUtil.scan(mContext,filePath);
        new PathConvertTask(mContext, new ConvertCallback(this,addFileListener), mSizeFilter, mMimeFilter, mDurationFilter).execute(filePath);
    }


    /**
     * 裁剪图片
     * @param activity
     * @param filePath
     * @param outputPath
     */
    public void cropFile(Activity activity,String filePath,String outputPath){
        Durban.with(activity)
                // Image path list/array.
                .inputImagePaths(filePath)
                // Image output directory.
                .outputDirectory(outputPath)
                // Image size limit.
                .maxWidthHeight(500, 500)
                // Aspect ratio.
                .aspectRatio(1, 1)
                // Output format: JPEG, PNG.
                .compressFormat(Durban.COMPRESS_JPEG)
                // Compress quality, see Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
                .compressQuality(90)
                // Gesture: ROTATE, SCALE, ALL, NONE.
                .gesture(Durban.GESTURE_ALL)
                .requestCode(REQ_CODE_CROP)
                .start();
    }

    /**
     * 裁剪图片
     * @param activity
     * @param filePaths
     * @param outputPath
     */
    public void cropFile(Activity activity,ArrayList<String> filePaths,String outputPath){
        Durban.with(activity)
                // Image path list/array.
                .inputImagePaths(filePaths)
                // Image output directory.
                .outputDirectory(outputPath)
                // Image size limit.
                .maxWidthHeight(500, 500)
                // Aspect ratio.
                .aspectRatio(1, 1)
                // Output format: JPEG, PNG.
                .compressFormat(Durban.COMPRESS_JPEG)
                // Compress quality, see Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
                .compressQuality(90)
                // Gesture: ROTATE, SCALE, ALL, NONE.
                .gesture(Durban.GESTURE_ALL)
                .requestCode(REQ_CODE_CROP)
                .start();
    }





    /**
     * 回调处理，绑定Activity.onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public Object onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return null;
        if(REQ_CODE_CROP == requestCode){
            // Analyze the list of paths after cropping.
            return  Durban.parseResult(data);
        }
        return null;
    }


    /**
     * 选中项目，如果选中项目超过最大数量，则不再添加
     * @param file
     * @param isChecked
     */
    public void checkedFile(AlbumFile file,int position,boolean isChecked,String errorMsg){
        int hasCheckSize = mCheckedFiles.size();
        if(isChecked){//添加
            if(hasCheckSize >= mLimitSize){
                //超出最大可选数量
                if(mMultiCheckedFileListener != null)
                    mMultiCheckedFileListener.onError(errorMsg);
            }else{
                file.setChecked(true);
                mCheckedFiles.add(file);
                if(mMultiCheckedFileListener != null)
                    mMultiCheckedFileListener.check(position);
            }
        }else{//移除
            file.setChecked(false);
            mCheckedFiles.remove(file);
            if(mMultiCheckedFileListener != null)
                mMultiCheckedFileListener.check(position);
        }
    }


    /**
     * 选中项目，如果选中项目超过最大数量，则不再添加
     * @param currentIndex 目录下标
     * @param position 选中的图片的位置下标
     * @param isChecked
     */
    public void checkedFile(int currentIndex,int position,boolean isChecked,String errorMsg){
        if(currentIndex < 0 || currentIndex >= mAlbumFolders.size()) return;

        ArrayList<AlbumFile> albumFiles = mAlbumFolders.get(currentIndex).getAlbumFiles();

        if(albumFiles == null || position < 0 || position >= albumFiles.size()) return;
        AlbumFile albumFile = albumFiles.get(position);
        int hasCheckSize = mCheckedFiles.size();
        if(isChecked){//添加
            if(hasCheckSize >= mLimitSize){
                //超出最大可选数量
                if(mMultiCheckedFileListener != null)
                    mMultiCheckedFileListener.onError(errorMsg);
            }else{
                albumFile.setChecked(true);
                mCheckedFiles.add(albumFile);
                if(mMultiCheckedFileListener != null)
                    mMultiCheckedFileListener.check(position);
            }
        }else{//移除
            albumFile.setChecked(false);
            mCheckedFiles.remove(albumFile);
            if(mMultiCheckedFileListener != null)
                mMultiCheckedFileListener.check(position);
        }
    }


    /**
     * 图片单选
     * @param file
     * @param isChecked
     */
    public void checkedSingle(AlbumFile file,boolean isChecked){
        int hasCheckSize = mCheckedFiles.size();

        if(isChecked){//添加
            if(hasCheckSize > 0 ){//取消选中
                mCheckedFiles.get(0).setChecked(false);
                mCheckedFiles.clear();
            }

            file.setChecked(true);
            mCheckedFiles.add(file);
        }else{//移除
            file.setChecked(false);
            mCheckedFiles.remove(file);
        }
    }


    /**
     * 图片单选
     * @param currentIndex
     * @param position
     * @param isChecked
     */
    public void checkedSingle(int currentIndex,int position,boolean isChecked){
        if(currentIndex < 0 || currentIndex >= mAlbumFolders.size()) return;
        ArrayList<AlbumFile> albumFiles = mAlbumFolders.get(currentIndex).getAlbumFiles();
        if(albumFiles == null || position < 0 || position >= albumFiles.size()) return;
        AlbumFile albumFile = albumFiles.get(position);
        int hasCheckSize = mCheckedFiles.size();

        if(isChecked){//添加
            if(hasCheckSize > 0 ){//取消选中
                mCheckedFiles.get(0).setChecked(false);
                mCheckedFiles.clear();
            }
            albumFile.setChecked(true);
            mCheckedFiles.add(albumFile);
        }else{//移除
            albumFile.setChecked(false);
            mCheckedFiles.remove(albumFile);
        }
    }



    public static Uri getUri(@NonNull Context context, @NonNull File outPath) {
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(outPath);
        } else {
            uri = CameraFileProvider.getUriForFile(context, CameraFileProvider.getFileProviderName(context), outPath);
        }
        return uri;
    }


    private static class ScanCallback implements MediaReadTask.Callback{
        ScanListener mScanListener;
        UpdateListener mUpdateListener;
        public ScanCallback(UpdateListener updateListener,ScanListener scanListener){
            this.mUpdateListener = updateListener;
            this.mScanListener = scanListener;
        }
        @Override
        public void onScanCallback(ArrayList<AlbumFolder> folders) {
            if(mUpdateListener != null){
                mUpdateListener.update(folders);
            }
            if(mScanListener != null)
                mScanListener.onScanFinish(folders);
        }
    }


    private static class ConvertCallback implements PathConvertTask.Callback{
        private AddFileListener mAddFileListener;
        private Reference<AlbumManager> mAlbumManagerRef;
        public ConvertCallback(AlbumManager manager, AddFileListener addFileListener) {
            this.mAlbumManagerRef = new WeakReference<>(manager);
            this.mAddFileListener = addFileListener;
        }


        @Override
        public void onConvertCallback(AlbumFile albumFile) {
            albumFile.setChecked(albumFile.isEnable());
            if (albumFile.isEnable()) {
                if(mAlbumManagerRef.get() != null)
                    mAlbumManagerRef.get().getCheckedFiles().add(albumFile);
            }

            boolean refreshList = false;
            if (albumFile.isEnable()) {
                if(mAlbumManagerRef.get() != null){
                    mAlbumManagerRef.get().addFileToList(albumFile);
                    refreshList = true;
                }
            } else {
                if (mAlbumManagerRef.get() != null && mAlbumManagerRef.get().isFilterVisibility()){
                    mAlbumManagerRef.get().addFileToList(albumFile);
                    refreshList = true;
                }else{
                    //文件不可见
                }
            }


            if( mAlbumManagerRef.get() != null && mAddFileListener != null)//刷新外部UI，eg:选中数量
                mAddFileListener.updateUI(mAlbumManagerRef.get().getCheckedFiles().size(),refreshList);

        }
    }


    /**
     * 添加新文件到文件集合中
     * @param albumFile
     */
    private void addFileToList(AlbumFile albumFile) {
        if (mCurrentAlbumIndex != 0) {
            List<AlbumFile> albumFiles = mAlbumFolders.get(0).getAlbumFiles();
            if (albumFiles.size() > 0) albumFiles.add(0, albumFile);
            else albumFiles.add(albumFile);
        }

        List<AlbumFile> albumFiles = mAlbumFolders.get(mCurrentAlbumIndex).getAlbumFiles();
        if (albumFiles.size() > 0) {
            albumFiles.add(0, albumFile);
        } else {
            albumFiles.add(albumFile);
        }

    }



    //扫描完成监听
    public interface ScanListener{
        void onScanFinish(ArrayList<AlbumFolder> folders);
    }

    //添加文件监听
    public interface AddFileListener {
        void updateUI(int count, boolean refreshList);// mAlbumContentAdapter.notifyItemInserted(mHasCamera ? 1 : 0);
    }


    //多选的选中监听
    public interface MultiCheckedFileListener {
        void check(int position);
        void onError(String msg);
    }

}
