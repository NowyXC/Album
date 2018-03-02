package com.nowy.baselib.views.grid;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;

import com.nowy.baselib.R;


/**
 */
public class GridLayoutList extends GridLayout {
    private static final int COLUMN_COUNT = R.styleable.GridLayout_columnCount;
    private static final int DEFAULT_COUNT = 1;
    private boolean updating=false;
    GridLayoutAdapter adapter;
    private OnItemClickListener listener;
    private int mColumnCount = DEFAULT_COUNT;//列数
    public GridLayoutList(Context context) {
        super(context);
        init(context,null);
    }

    public GridLayoutList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public GridLayoutList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }



    private void init(Context context ,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridLayout);
        try {
            mColumnCount = a.getInt(COLUMN_COUNT, DEFAULT_COUNT);
            setColumnCount(mColumnCount);
        } finally {
            a.recycle();
        }
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setAdapter(GridLayoutAdapter adapter) {
        this.adapter = adapter;
        adapter.bindGridLayoutList(this);
        initView();

    }

    public GridLayoutAdapter getAdapter() {
        return adapter;
    }

    private  void initView() {
        removeAllViews();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View view = adapter.getView(i, null, this);
            addView(view);
            setClickListener(view,i);
        }
        invalidate();
    }



    private void setClickListener(final View view, final int position) {
        if(listener!=null){
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(view, position);
                    }
                }
            });
        }
    }
    private synchronized void setAllClickListener() {
        if(listener!=null){
            int currentCount = this.getChildCount();
            for (int i = 0; i < currentCount; i++) {
                View convertView = this.getChildAt(i);
                setClickListener(convertView,i);
            }
        }
    }
    /**
     * 更新布局
     *
     * @param isViewTypeChange true:子view的布局种类和种类位置变化,需重置整个布局;false:复用更新布局
     */
    public  void updateView(boolean isViewTypeChange) {
        if(updating){
            throw new RuntimeException("更新时再次更新，会导致数据错乱！！！" );
        }
        updating=true;
        if (isViewTypeChange) {
            initView();
            updating=false;
            return;
        }
        int count = adapter.getCount();
        int currentCount = this.getChildCount();
        for (int i = 0; i < count; i++) {
            if (i < currentCount) {//已经存在过,可以用来复用
                View convertView = this.getChildAt(i);
                adapter.getView(i, convertView, this);
            } else {
                View view = adapter.getView(i, null, this);

                addView(view, i);
                setClickListener(view, i);
            }
        }
        //多的需要移除
        for (int i = currentCount - 1; i >= count; i--) {
            this.removeViewAt(i);
        }
        invalidate();//更新布局
        updating=false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        setAllClickListener();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int posi);
    }
}
