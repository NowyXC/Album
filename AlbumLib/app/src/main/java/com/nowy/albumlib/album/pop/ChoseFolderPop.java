package com.nowy.albumlib.album.pop;

import android.app.Activity;
import android.view.View;
import android.widget.PopupWindow;

import com.nowy.albumlib.R;
import com.nowy.albumlib.album.adapter.AlbumFolderAdapter;
import com.nowy.baselib.manager.ForegroundMaskManager;
import com.nowy.baselib.views.grid.GridLayoutList;
import com.nowy.library.bean.AlbumFolder;

import java.util.List;

/**
 * Created by Nowy on 2018/2/28.
 */

public class ChoseFolderPop {
    private Activity mAty;
    private PopupWindow mPopupWindow;
    private AlbumFolderAdapter mAdapter;
    private ForegroundMaskManager mMaskManager;
    private int mChoseIndex ;
    private GridLayoutList mGlFolder;

    public ChoseFolderPop(Activity aty,ForegroundMaskManager maskManager) {
        this.mAty = aty;
        this.mMaskManager = maskManager;
    }

    public void showPop(View view, List<AlbumFolder> data, final ChoseListener listener){
        if(data == null || data.size() == 0) return;
        mPopupWindow = PopUtil.ShowPopAsDropDown(mAty, R.layout.pop_album_folder,view,true,false);
        View contentView = mPopupWindow.getContentView();
        mGlFolder = contentView.findViewById(R.id.album_folder_GlFolder);
        mAdapter = new AlbumFolderAdapter(data,mChoseIndex);
        mGlFolder.setAdapter(mAdapter);
        mGlFolder.setOnItemClickListener(new GridLayoutList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int posi) {
                mChoseIndex = posi;
                if(listener != null)
                    listener.chose(posi);
                dismiss();
            }
        });

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(mMaskManager != null)
                    mMaskManager.hideMask();
            }
        });

        if(mMaskManager != null && !mMaskManager.isAnimRunning())
            mMaskManager.showMask();
    }


    public void dismiss(){
        if(mPopupWindow != null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
        if(mMaskManager != null)
            mMaskManager.hideMask();
    }

    public interface ChoseListener{
        void chose(int position);
    }

}
