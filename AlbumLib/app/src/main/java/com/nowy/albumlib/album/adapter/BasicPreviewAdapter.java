package com.nowy.albumlib.album.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowy.albumlib.view.photoview.AttacherImageView;
import com.nowy.albumlib.view.photoview.PhotoViewAttacher;

import java.util.List;

public abstract class BasicPreviewAdapter<T> extends PagerAdapter {

    private Context mContext;
    private List<T> mPreviewList;
    private int mChildCount = 0;

    public BasicPreviewAdapter(Context context, List<T> previewList) {
        this.mContext = context;
        this.mPreviewList = previewList;
    }

    @Override
    public int getCount() {
        return mPreviewList == null ? 0 : mPreviewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    protected abstract boolean loadPreview(ImageView imageView, T t, int position);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AttacherImageView imageView = new AttacherImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
        imageView.setAttacher(attacher);

        T t = mPreviewList.get(position);
        loadPreview(imageView, t, position);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((View) object));
    }


    @Override public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }


}