package com.nowy.albumlib.view.titlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nowy.albumlib.R;


/**
 * Created by Nowy on 2017/4/1.
 *
 *  支持XML。。。
 *
 *
 */

public class TitleBar extends Toolbar {
    private static final int MAX_UNREAD_COUNT = 99;//未读消息最大数量
    private SparseArray<View> mViews = new SparseArray<>();
    private ImageView mIvLeft;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ImageView mIvRight;
    private ImageView mIvRight2;
    private View mBarView;
    private RelativeLayout mRlRight01;
    private RelativeLayout mRlRight02;
    private TextView mTvUnRead02;
    private TextView mTvUnReadPoint02;
    private TextView mTvUnRead01;
    private View mLineBottom;
    private TitleBarParams mTitleBarParams;
    private FrameLayout mContentView;


    public enum Gravity {
        LEFT, CENTER, RIGHT
    }

    public TitleBar(Context context) {
        super(context);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setupData(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        init(context);

        setupData(context, attrs);
    }


    private void setupData(final Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

        Drawable bgTitleBar = a.getDrawable(R.styleable.TitleBar_tbar_Background);
        if (bgTitleBar != null) {
            ViewCompat.setBackground(this,bgTitleBar);
        }



        String title = a.getString(R.styleable.TitleBar_tbar_title);
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }
        Drawable leftIcon = a.getDrawable(R.styleable.TitleBar_tbar_leftIcon);
        if (leftIcon != null) {
            mIvLeft.setImageDrawable(leftIcon);
        }
        mIvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof Activity){
                    ((Activity) context).finish();
                }
            }
        });

        Drawable rightIcon = a.getDrawable(R.styleable.TitleBar_tbar_rightIcon);
        if (rightIcon != null) {
            mIvRight.setImageDrawable(rightIcon);
            mIvRight.setVisibility(View.VISIBLE);
            mRlRight01.setVisibility(View.VISIBLE);
        }

        Drawable secRightIcon = a.getDrawable(R.styleable.TitleBar_tbar_secRightIcon);
        if (secRightIcon != null) {
            mIvRight2.setImageDrawable(secRightIcon);
            mIvRight2.setVisibility(View.VISIBLE);
            mRlRight02.setVisibility(View.VISIBLE);
        }


        boolean showLineBottom = a.getBoolean(R.styleable.TitleBar_tbar_showLineBottom, true);

        mLineBottom.setVisibility(showLineBottom ? View.VISIBLE : View.GONE);

        a.recycle();
    }


    private void init(Context context) {
//        setContentInsetStartWithNavigation(0);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.title_bar_height)));
        setBackgroundResource(R.color.bg_titleBar);
        mBarView = inflate(context, R.layout.title_bar, this);
        mContentView = (FrameLayout) mBarView.findViewById(R.id.title_bar_contentView);
        mIvLeft = (ImageView) viewById(R.id.title_bar_IvLeft);
        mTvTitle = (TextView) viewById(R.id.title_bar_TvTitle);
        mTvRight = (TextView) viewById(R.id.title_bar_TvRight);

        //右边区域

        //右边第一个区域
        mRlRight01 = (RelativeLayout) viewById(R.id.title_bar_RlRight_01);
        mIvRight = (ImageView) viewById(R.id.title_bar_IvRight01);
        mTvUnRead01 = (TextView) viewById(R.id.title_bar_TvRightUnRead01);

        //右边第二个区域
        mRlRight02 = (RelativeLayout) viewById(R.id.title_bar_RlRight_02);
        mIvRight2 = (ImageView) viewById(R.id.title_bar_IvRight02);
        mTvUnRead02 = (TextView) viewById(R.id.title_bar_TvRightUnRead02);
        mTvUnReadPoint02 = (TextView) viewById(R.id.title_bar_TvRightUnReadPoint02);

        //底部分割线
        mLineBottom = viewById(R.id.title_bar_LineBottom);
    }

    public View getBarView(){
        return mBarView;
    }


    public FrameLayout getContentView() {
        return mContentView;
    }

    public ImageView getIvLeft() {
        return mIvLeft;
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public TextView getTvRight() {
        return mTvRight;
    }

    public ImageView getIvRight2() {
        return mIvRight2;
    }

    public ImageView getIvRight() {
        return mIvRight;
    }

    public RelativeLayout getRlRight01() {
        return mRlRight01;
    }

    public RelativeLayout getRlRight02() {
        return mRlRight02;
    }

    public void setTitle(CharSequence title) {
        this.mTvTitle.setText(title);
    }

    public void setTitleColor(@ColorInt int color) {
        this.mTvTitle.setTextColor(color);
    }




    public void showBottomLine() {
        mLineBottom.setVisibility(VISIBLE);
    }

    public void hideBottomLine() {
        mLineBottom.setVisibility(GONE);
    }

    public void showLeftImg(){
        mIvLeft.setVisibility(VISIBLE);
    }

    public void hideLeftImg(){
        mIvLeft.setVisibility(GONE);
    }


    public TitleBarParams getTitleBarParams() {
        return mTitleBarParams;
    }

    public void setTitleBarParams(TitleBarParams titleBarParams) {
        this.mTitleBarParams = titleBarParams;

    }

    /**
     * 设置标题的位置
     *
     * @param gravity {@link Gravity}
     */
    public void setTitleGravity(Gravity gravity) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTvTitle.getLayoutParams();
        if (Gravity.LEFT == gravity) {//左对齐
            layoutParams.gravity = android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL ;
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (Gravity.CENTER == gravity) {//居中
//            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParams.gravity = android.view.Gravity.CENTER;
        } else if (Gravity.RIGHT == gravity) {//右边
            layoutParams.gravity = android.view.Gravity.END | android.view.Gravity.CENTER_VERTICAL ;
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        this.mTvTitle.setLayoutParams(layoutParams);
    }

    public View viewById(@IdRes int viewId) {
        if (mViews.get(viewId) == null) {
            View view = findViewById(viewId);
            mViews.put(viewId, view);
        }
        return mViews.get(viewId);
    }


    public void setLeftImg(@DrawableRes int imgRes) {
        this.mIvLeft.setImageResource(imgRes);
    }

    public void setLeftClickListener(OnClickListener onClickListener) {
        this.mIvLeft.setOnClickListener(onClickListener);
    }

    public void setLeftVisibility(int visibility) {
        this.mIvLeft.setVisibility(visibility);
    }


    //右边第一区域
    public void setRightImg(@DrawableRes int imgRes) {
        this.mIvRight.setImageResource(imgRes);
    }

    public void setRight01Visibility(int visibility) {
        this.mRlRight01.setVisibility(visibility);
    }

    public void setRightClickListener(OnClickListener onClickListener) {
        this.mRlRight01.setOnClickListener(onClickListener);
    }

    public void showRight01UnRead(int count) {
        String countStr = null;

        if (count > MAX_UNREAD_COUNT) {
            countStr = MAX_UNREAD_COUNT + "+";
        } else if (count > 0) {
            countStr = String.valueOf(count);
        }
        this.mTvUnRead01.setText(countStr);
        this.mTvUnRead01.setVisibility(countStr == null ? GONE : VISIBLE);
    }

    public void clearRight01UnRead() {
        this.mTvUnRead01.setText(String.valueOf(0));
        this.mTvUnRead01.setVisibility(View.GONE);
    }

    //右边第二区域
    public void setIvRight2Img(@DrawableRes int imgRes) {
        this.mIvRight2.setImageResource(imgRes);
    }

    public void setRight02Visibility(int visibility) {
        this.mRlRight02.setVisibility(visibility);
    }

    public void setIvRight2ClickListener(OnClickListener onClickListener) {
        this.mRlRight02.setOnClickListener(onClickListener);
    }

    public void showRight02UnReadPoint() {
        this.mTvUnReadPoint02.setVisibility(View.VISIBLE);
    }

    public void clearRight02UnReadPoint() {
        this.mTvUnReadPoint02.setVisibility(View.GONE);
    }


    public void showRight02Unread(int count) {
        String countStr = null;

        if (count > MAX_UNREAD_COUNT) {
            countStr = MAX_UNREAD_COUNT + "+";
        } else if (count > 0) {
            countStr = String.valueOf(count);
        }
        this.mTvUnRead02.setText(countStr);
        this.mTvUnRead02.setVisibility(countStr == null ? View.GONE : View.VISIBLE);
    }

    public void clearRight02Unread() {
        this.mTvUnRead02.setText("");
        this.mTvUnRead02.setVisibility(View.GONE);
    }


    //右边文本设置，文本控件显示，文本控件点击监听
    public void setTvRightVisibility(int visibility) {
        this.mTvRight.setVisibility(visibility);
    }

    public void setRightTxt(@StringRes int txtRes) {
        this.mTvRight.setText(txtRes);
    }

    public void setRightTxt(CharSequence txtRes) {
        this.mTvRight.setText(txtRes);
        this.mTvRight.setVisibility(View.VISIBLE);
    }


    public void setRightTxtClickListener(OnClickListener onClickListener) {
        this.mTvRight.setOnClickListener(onClickListener);
    }




    /**
     * 把titleBar植入UI
     *
     * @param context
     */
    public void attach(Context context) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("context is not a activity");
        }

        ViewGroup rootView = ((ViewGroup) ((ViewGroup) ((Activity) context)
                .getWindow().getDecorView()
                .findViewById(android.R.id.content))
                .getChildAt(0));

        if (rootView instanceof LinearLayout) {
            rootView.addView(this, 0);
        } else {
            rootView.addView(this);
        }
    }


    public static class Builder {
        TitleBarParams P;

        public Builder(Context context) {
            this.P = new TitleBarParams(context);
        }


        public Builder setTitle(String title) {
            return setTitle(title, Gravity.CENTER);
        }

        public Builder setTitle(@StringRes int title) {
            return setTitle(title, Gravity.CENTER);
        }



        public Builder setBackground(@DrawableRes int bgRes){
            this.P.mBackgroundRes = bgRes;
            return this;
        }

        /**
         * 设置title
         *
         * @param title
         * @param gravity 文本对齐模式
         * @return
         */
        public Builder setTitle(String title, Gravity gravity) {
            this.P.mTitle = title;
            this.P.mTitleRes = 0;
            this.P.mTitleGravity = gravity;
            return this;
        }

        /**
         * 设置title
         *
         * @param title
         * @param gravity 文本对齐模式{@link Gravity}
         * @return
         */
        public Builder setTitle(@StringRes int title, Gravity gravity) {
            this.P.mTitleRes = title;
            this.P.mTitle = null;
            this.P.mTitleGravity = gravity;
            return this;
        }


        /**
         * 显示左边图标
         *
         * @return
         */
        public Builder showLeftImg() {
            return showLeftImg(0, null);
        }

        public Builder hideLeftImg() {
            this.P.mLeftVisible = false;
            return this;
        }

        /**
         * 显示左边图标
         *
         * @param imgRes 图片资源
         * @return
         */
        public Builder showLeftImg(@DrawableRes int imgRes) {
            return showLeftImg(imgRes, null);
        }

        /**
         * 显示左边图标
         *
         * @param imgRes          图片资源
         * @param onClickListener 设置点击事件
         * @return
         */
        public Builder showLeftImg(@DrawableRes int imgRes, OnClickListener onClickListener) {
            this.P.mLeftVisible = true;
            this.P.mLeftImgRes = imgRes;
            if (null != onClickListener) {
                this.P.mLeftClickListener = onClickListener;
            }
            return this;
        }


        /**
         * 设置右边图标及点击事件
         *
         * @param imgRes          资源
         * @param onClickListener 点击事件
         * @return
         */
        public Builder showRightImg(@DrawableRes int imgRes, OnClickListener onClickListener) {
            this.P.mRightVisible = true;
            this.P.mRightImgRes = imgRes;
            if (null != onClickListener) {
                this.P.mRightClickListener = onClickListener;
            }
            return this;
        }


        /**
         * 设置右边图标及点击事件(第二个，可以两个图标同时存在)
         *
         * @param imgRes
         * @param onClickListener
         * @return
         */
        public Builder showRightImg_2(@DrawableRes int imgRes, OnClickListener onClickListener) {
            this.P.mRightVisible_2 = true;
            this.P.mRightImgRes_2 = imgRes;
            if (null != onClickListener) {
                this.P.mRightClickListener_2 = onClickListener;
            }
            return this;
        }


        /**
         * 显示右边文本按钮，并设置监听器，不设置可以设null
         *
         * @param txt             文本
         * @param onClickListener 点击事件
         * @return
         */
        public Builder showRightTxt(String txt, OnClickListener onClickListener) {
            this.P.mRightTxtVisible = true;
            this.P.mRightTxt = txt;
            if (null != onClickListener) {
                this.P.mRightTxtClickListener = onClickListener;
            }
            return this;
        }


        public Builder showRightTxt(CharSequence txt, OnClickListener onClickListener) {
            this.P.mRightTxtVisible = true;
            this.P.mRightTxt = txt;
            if (null != onClickListener) {
                this.P.mRightTxtClickListener = onClickListener;
            }
            return this;
        }

        /**
         * 显示右边文本按钮，并设置监听器，不设置可以设null
         *
         * @param txtRes          文本资源
         * @param onClickListener 点击事件
         * @return
         */
        public Builder showRightTxt(@StringRes int txtRes, OnClickListener onClickListener) {
            this.P.mRightTxtVisible = true;
            this.P.mRightTxtRes = txtRes;
            if (null != onClickListener) {
                this.P.mRightTxtClickListener = onClickListener;
            }
            return this;
        }


        /**
         * 创建TitleBar
         *
         * @return
         */
        public TitleBar create() {
            TitleBar titleBar = new TitleBar(P.mContext);
            P.apply(titleBar);
            return titleBar;
        }

        /**
         * 创建TitleBar，并绑定到当前activity,如果当期上下文非activity，则抛出异常
         *
         * @return
         */
        public TitleBar attach() {
            TitleBar titleBar = create();
            if (!(P.mContext instanceof Activity)) {
                throw new IllegalArgumentException("context is not a activity");
            }

            ViewGroup rootView = ((ViewGroup) ((ViewGroup) ((Activity) P.mContext)
                    .getWindow().getDecorView()
                    .findViewById(android.R.id.content))
                    .getChildAt(0));

            if (rootView instanceof LinearLayout) {
                rootView.addView(titleBar, 0);
            } else {
                rootView.addView(titleBar);
            }
            return titleBar;
        }


        /**
         * 传入当前layout的跟布局ID，将titleBar绑定到layout中
         * 适合Fragment的根布局使用
         * @param layoutIds
         * @return
         */
        public TitleBar attach(@IdRes int layoutIds) {
            TitleBar titleBar = create();
            if (!(P.mContext instanceof Activity)) {
                throw new IllegalArgumentException("context is not a activity");
            }
            ViewGroup rootView = (ViewGroup) ((Activity) P.mContext).findViewById(layoutIds);
            if (rootView instanceof LinearLayout) {
                rootView.addView(titleBar, 0);
            } else {
                rootView.addView(titleBar);
            }
            return titleBar;
        }
    }


    public static class TitleBarParams {
        Context mContext;
        public View mContentView;//内容页
        CharSequence mTitle;
        Gravity mTitleGravity = Gravity.CENTER;

        int mBackgroundRes;

        int mTitleRes;
        boolean mLeftVisible = true;
        int mLeftImgRes;
        OnClickListener mLeftClickListener;

        boolean mRightVisible;
        int mRightImgRes;
        OnClickListener mRightClickListener;

        boolean mRightVisible_2;
        int mRightImgRes_2;
        OnClickListener mRightClickListener_2;

        boolean mRightTxtVisible;
        int mRightTxtRes;
        CharSequence mRightTxt;
        OnClickListener mRightTxtClickListener;

        TitleBarParams(Context context) {
            this.mContext = context;
        }

        OnClickListener mDefClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        };


        public void apply(TitleBar titleBar) {
            if(mBackgroundRes != 0){
                titleBar.setBackgroundResource(mBackgroundRes);
            }

            if (mTitleRes != 0) {
                mTitle = mContext.getText(mTitleRes);
            }

            titleBar.setTitle(mTitle);
            titleBar.setTitleGravity(mTitleGravity);

            if (mLeftVisible) { //显示左边图标，并设置图标和点击事件
                if (mLeftImgRes != 0) {
                    titleBar.setLeftImg(mLeftImgRes);
                }
                if (mLeftClickListener != null) {
                    titleBar.setLeftClickListener(mLeftClickListener);
                } else {
                    titleBar.setLeftClickListener(mDefClickListener);
                }
            } else {
                titleBar.setLeftVisibility(GONE);
            }

            if (mRightVisible) { //显示右边第一个图标，并设置图标和点击事件
                titleBar.setRight01Visibility(VISIBLE);
                if (mRightImgRes != 0) {
                    titleBar.setRightImg(mRightImgRes);
                }
                if (mRightClickListener != null) {
                    titleBar.setRightClickListener(mRightClickListener);
                }
            }

            if (mRightVisible_2) {//显示右边第而个图标，并设置图标和点击事件
                titleBar.setRight02Visibility(VISIBLE);
                if (mRightImgRes_2 != 0) {
                    titleBar.setIvRight2Img(mRightImgRes_2);
                }
                if (mRightClickListener_2 != null) {
                    titleBar.setIvRight2ClickListener(mRightClickListener_2);
                }
            }

            if (mRightTxtVisible) {
                titleBar.setTvRightVisibility(VISIBLE);
                if (mRightTxtRes != 0) {
                    mRightTxt = mContext.getText(mTitleRes);
                }
                titleBar.setRightTxt(mRightTxt);
                if (mRightTxtClickListener != null) {
                    titleBar.setRightTxtClickListener(mRightTxtClickListener);
                }
            }

            titleBar.setTitleBarParams(this);
        }
    }


}
