package com.nowy.albumlib.album.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.nowy.baselib.utils.GlideUtil;

import java.util.List;

public class PathPreviewAdapter extends BasicPreviewAdapter<String> {

    public PathPreviewAdapter(Context context, List<String> previewList) {
        super(context, previewList);
    }

    @Override
    protected boolean loadPreview(ImageView imageView, String s, int position) {
        GlideUtil.displayOriginal(imageView,s);
        return true;
    }
}