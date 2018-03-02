package com.nowy.albumlib.album.adapter.ItemViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Nowy on 2017/5/24.
 */

public class BaseItemViewHolder extends RecyclerView.ViewHolder{
    public static View createView(ViewGroup parent, int resLayout){
        return LayoutInflater.from(parent.getContext())
                .inflate(resLayout,parent,false);
    }

    public BaseItemViewHolder(View itemView){
        super(itemView);
        itemView.setTag(this);
    }


}
