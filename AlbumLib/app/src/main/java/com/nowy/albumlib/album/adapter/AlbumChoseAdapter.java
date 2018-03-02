package com.nowy.albumlib.album.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowy.albumlib.R;
import com.nowy.albumlib.album.listener.OnItemClickListener;
import com.nowy.baselib.utils.GlideUtil;
import com.nowy.library.bean.AlbumFile;

import java.util.List;

/**
 * Created by Nowy on 2018/3/1.
 */

public class AlbumChoseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<AlbumFile> mAlbumFiles;
    private OnItemClickListener mOnItemClickListener;
    public AlbumChoseAdapter(List<AlbumFile> albumFiles,OnItemClickListener onItemClickListener) {
        this.mAlbumFiles = albumFiles;
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(ItemViewHolder.RES_LAYOUT,parent,false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).bingData(mAlbumFiles.get(position));
        ((ItemViewHolder)holder).setListener(mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mAlbumFiles == null ? 0 : mAlbumFiles.size();
    }

    static final class ItemViewHolder extends RecyclerView.ViewHolder{
        static final int RES_LAYOUT = R.layout.item_album_chose;
        ImageView ivImg;
        public ItemViewHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.album_chose_IvImg);
        }

        private void bingData(AlbumFile albumFile){
            if(albumFile == null) return;
            GlideUtil.display(ivImg,albumFile.getPath());
        }

        private void setListener(final OnItemClickListener onItemClickListener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(v,getAdapterPosition());
                    }
                }
            });
        }
    }
}
