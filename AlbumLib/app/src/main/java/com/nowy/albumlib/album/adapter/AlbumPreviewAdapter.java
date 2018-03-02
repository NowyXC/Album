package com.nowy.albumlib.album.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.nowy.baselib.utils.GlideUtil;
import com.nowy.library.bean.AlbumFile;

import java.util.List;

public class AlbumPreviewAdapter extends BasicPreviewAdapter<AlbumFile> {

    public AlbumPreviewAdapter(Context context, List<AlbumFile> previewList) {
        super(context, previewList);
    }

    @Override
    protected boolean loadPreview(ImageView imageView, AlbumFile file, int position) {
        GlideUtil.display(imageView,file.getPath());
        return true;
    }
}