package com.nowy.albumlib.album.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowy.albumlib.R;
import com.nowy.albumlib.album.adapter.ItemViewHolder.BaseItemViewHolder;
import com.nowy.baselib.utils.GlideUtil;
import com.nowy.baselib.views.grid.GridLayoutAdapter;
import com.nowy.library.bean.AlbumFolder;

import java.util.List;
import java.util.Locale;

/**
 * Created by Nowy on 2018/2/28.
 */

public class AlbumFolderAdapter extends GridLayoutAdapter {
    private List<AlbumFolder> mData;
    private int mChoseIndex;//选中的下标
    public AlbumFolderAdapter(List<AlbumFolder> data,int choseIndex) {
        this.mData = data;
        this.mChoseIndex = choseIndex;
    }

    public void notifyDataSetChanged(List<AlbumFolder> data,int choseIndex){
        if(data == null) return;
        this.mChoseIndex = choseIndex;
        if(mData != null){
            this.mData.clear();
            this.mData.addAll(data);
        }else{
            this.mData = data;
        }
        super.notifyDataSetChanged(false);
    }


    public void notifyDataSetChanged(int choseIndex){
        this.mChoseIndex = choseIndex;
        super.notifyDataSetChanged(false);
    }


    @Override
    protected int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    protected View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = ItemViewHolder.createView(parent, ItemViewHolder.RES_LAYOUT);
            new ItemViewHolder(convertView);
        }

        ItemViewHolder holder = (ItemViewHolder) convertView.getTag();
        holder.bingData(mData.get(position),position);
        holder.setChoseItem(mChoseIndex == position);
        return convertView;
    }


    static class ItemViewHolder extends BaseItemViewHolder {
        static final int RES_LAYOUT = R.layout.item_album_folder;
        ImageView ivFolder;
        ImageView ivChose;
        TextView tvFolderName;
        public ItemViewHolder(View itemView) {
            super(itemView);

            ivFolder = itemView.findViewById(R.id.album_folder_IvImg);
            ivChose = itemView.findViewById(R.id.album_folder_IvChose);
            tvFolderName = itemView.findViewById(R.id.album_folder_TvFolderName);
        }

        public void bingData(AlbumFolder folder,int position){
            if(folder == null) return;
            if(folder.getAlbumFiles() != null && folder.getAlbumFiles().size() > 0){
                GlideUtil.display(ivFolder,folder.getAlbumFiles().get(0).getPath());
            }else{
                ivFolder.setImageResource(R.drawable.ic_launcher_foreground);
            }
            if(position == 0){
                tvFolderName.setText("全部图片");
            }else{
                tvFolderName.setText(folder.getName());
            }

            if(folder.getAlbumFiles() != null && folder.getAlbumFiles().size() > 0){
                tvFolderName.append(String.format(Locale.CHINA,"(%d)",folder.getAlbumFiles().size()));
            }
        }


        public void setChoseItem(boolean isChose){
            ivChose.setVisibility(isChose ? View.VISIBLE : View.INVISIBLE);
        }

    }
}
