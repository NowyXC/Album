package com.nowy.albumlib.album;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowy.albumlib.R;
import com.nowy.albumlib.album.listener.OnItemCheckedListener;
import com.nowy.albumlib.album.listener.OnItemClickListener;
import com.nowy.baselib.utils.GlideUtil;
import com.nowy.library.bean.AlbumFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nowy on 2018/2/28.
 */

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BUTTON = 1;
    private static final int TYPE_IMAGE = 2;

    private static final int MODE_SINGLE = 0;//单选
    private static final int MODE_MULTIPLE = 1;//多选

    private final int itemSize;
    private final boolean hasCamera;


    private List<AlbumFile> mAlbumFiles;
    private int mChoiceMode;//单选/多选
    private OnItemClickListener mAddPhotoClickListener;
    private OnItemClickListener mItemClickListener;
    private OnItemCheckedListener mItemCheckedListener;

    public AlbumAdapter(int choiceMode,int itemSize, boolean hasCamera) {
        this.mAlbumFiles = new ArrayList<>();
        this.hasCamera = hasCamera;
        this.itemSize = itemSize;
        this.mChoiceMode = choiceMode;
    }

    public void notifyDataSetChanged(List<AlbumFile> albumFiles) {
        if(albumFiles != null){
            this.mAlbumFiles.clear();
            this.mAlbumFiles.addAll(albumFiles);
            super.notifyDataSetChanged();
        }
    }

    public void setAddClickListener(OnItemClickListener addPhotoClickListener) {
        this.mAddPhotoClickListener = addPhotoClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemCheckedListener(OnItemCheckedListener checkListener) {
        this.mItemCheckedListener = checkListener;
    }

    @Override
    public int getItemCount() {
        int camera = hasCamera ? 1 : 0;
        return mAlbumFiles == null ? camera : mAlbumFiles.size() + camera;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return hasCamera ? TYPE_BUTTON : TYPE_IMAGE;
        else
            return TYPE_IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUTTON: {
                return new ButtonViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(ButtonViewHolder.RES_LAYOUT, parent, false),
                        itemSize,
                        hasCamera,
                        mAddPhotoClickListener);
            }
            case TYPE_IMAGE:
            default:{
                return new ImageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(ImageHolder.RES_LAYOUT, parent, false),
                        itemSize,
                        hasCamera,
                        mChoiceMode,
                        mItemClickListener,
                        mItemCheckedListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_BUTTON: {
                // Nothing.
                break;
            }
            case TYPE_IMAGE:
            default:{
                ImageHolder imageHolder = (ImageHolder) holder;
                int camera = hasCamera ? 1 : 0;
                position = holder.getAdapterPosition() - camera;
                AlbumFile albumFile = mAlbumFiles.get(position);
                imageHolder.setData(albumFile);
                break;
            }
        }
    }

    private static class ButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        static final int RES_LAYOUT = R.layout.item_album_camera;
        private final boolean hasCamera;
        private final OnItemClickListener mItemClickListener;

        ButtonViewHolder(View itemView, int itemSize, boolean hasCamera, OnItemClickListener itemClickListener) {
            super(itemView);
            itemView.getLayoutParams().height = itemSize;
            this.hasCamera = hasCamera;
            this.mItemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null && v == itemView) {
                int camera = hasCamera ? 1 : 0;
                mItemClickListener.onItemClick(v, getAdapterPosition() - camera);
            }
        }
    }

    private static class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        static final int RES_LAYOUT = R.layout.item_album_img;
        private final int itemSize;
        private final boolean hasCamera;
        private final int mChoiceMode;

        private final OnItemClickListener mItemClickListener;
        private final OnItemCheckedListener mItemCheckedListener;

        private ImageView mIvImage;
        private AppCompatCheckBox mCheckBox;


        ImageHolder(View itemView, int itemSize, boolean hasCamera, int choiceMode,
                    OnItemClickListener itemClickListener, OnItemCheckedListener itemCheckedListener) {
            super(itemView);
            itemView.getLayoutParams().height = itemSize;

            this.itemSize = itemSize;
            this.hasCamera = hasCamera;
            this.mChoiceMode = choiceMode;
            this.mItemClickListener = itemClickListener;
            this.mItemCheckedListener = itemCheckedListener;

            mIvImage = itemView.findViewById(R.id.album_img_IvImg);
            mCheckBox = itemView.findViewById(R.id.album_img_CbChose);

            itemView.setOnClickListener(this);
//            mLayoutLayer.setOnClickListener(this);
            if (mChoiceMode == MODE_MULTIPLE) {//多选
                mCheckBox.setVisibility(View.VISIBLE);
            } else {
                mCheckBox.setVisibility(View.GONE);
            }
        }

        void setData(AlbumFile albumFile) {
            mCheckBox.setChecked(albumFile.isChecked());
            if(mIvImage.getTag() == null || !albumFile.getPath().equals(mIvImage.getTag())){
                GlideUtil.display(mIvImage,albumFile.getPath());
                mIvImage.setTag(albumFile.getPath());
            }

//            mLayoutLayer.setVisibility(albumFile.isEnable() ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                switch (mChoiceMode) {
                    case MODE_MULTIPLE: {
                        if (mItemCheckedListener != null) {
                            mCheckBox.toggle();
                            int camera = hasCamera ? 1 : 0;
                            mItemCheckedListener.onCheckedChanged(mCheckBox, getAdapterPosition() - camera, mCheckBox.isChecked());
                        }
                        break;
                    }
                    case MODE_SINGLE: {
                        if (mItemClickListener != null) {
                            int camera = hasCamera ? 1 : 0;
                            mItemClickListener.onItemClick(v, getAdapterPosition() - camera);
                        }
                        break;
                    }
                }
            }
        }
    }


}