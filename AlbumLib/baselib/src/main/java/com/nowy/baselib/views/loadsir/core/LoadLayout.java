package com.nowy.baselib.views.loadsir.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.nowy.baselib.views.loadsir.LoadSirUtil;
import com.nowy.baselib.views.loadsir.callback.Callback;
import com.nowy.baselib.views.loadsir.callback.SuccessCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:TODO
 * Create Time:2017/9/2 17:02
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

public class LoadLayout extends FrameLayout {
    private Map<Class<? extends Callback>, Callback> callbacks = new HashMap<>();
    private Context context;
    private Callback.OnReloadListener onReloadListener;
    private Class<? extends Callback> preCallback;
    private static final int CALLBACK_CUSTOM_INDEX = 1;

    public LoadLayout(@NonNull Context context) {
        super(context);
    }

    public LoadLayout(@NonNull Context context, Callback.OnReloadListener onReloadListener) {
        this(context);
        this.context = context;
        this.onReloadListener = onReloadListener;
    }

    public void setupSuccessLayout(Callback callback) {
        addCallback(callback);
        View successView = callback.getRootView();
        successView.setVisibility(View.GONE);
        addView(successView);
    }

    public void setupCallback(Callback callback) {
        Callback cloneCallback = callback.copy();
        cloneCallback.setCallback(null, context, onReloadListener);
        addCallback(cloneCallback);
    }

    public void addCallback(Callback callback) {
        if (!callbacks.containsKey(callback.getClass())) {
            callbacks.put(callback.getClass(), callback);
        }
    }

    public void showCallback(final Class<? extends Callback> callback) {
        checkCallbackExist(callback);
        if (LoadSirUtil.isMainThread()) {
            showCallbackView(callback,null);
        } else {
            postToMainThread(callback,null);
        }
    }

    /**
     * 切换展示视图时携带数据，对将要显示的视图进行更新
     * 因为切换操作在UI线程进行，所以可以对callback视图的UI进行调整
     * data数据会在{@link Callback#update}中被调用
     * @see Callback#update(Object)
     * Create Time:2017/12/27
     * Author:Nowy
     * @param callback
     * @param data
     */
    public void showCallback(final Class<? extends Callback> callback,Object data) {
        checkCallbackExist(callback);
        if (LoadSirUtil.isMainThread()) {
            showCallbackView(callback,data);
        } else {
            postToMainThread(callback,data);
        }
    }


    /**
     * 延时显示数据页
     * @param callback
     * @param delay
     * @param data
     */
    public void showCallbackDelayed(final Class<? extends Callback> callback,long delay,Object data){
        checkCallbackExist(callback);
        postToMainThread(callback,delay,data);
    }

    private void postToMainThread(final Class<? extends Callback> status,long delay, final Object data) {
        postDelayed(new Runnable() {
            @Override
            public void run() {

                showCallbackView(status,data);
            }
        },delay);
    }


    private void postToMainThread(final Class<? extends Callback> status, final Object data) {
        post(new Runnable() {
            @Override
            public void run() {
                showCallbackView(status,data);
            }
        });
    }

    private void showCallbackView(Class<? extends Callback> status,Object data) {
        if (preCallback != null) {
            if (preCallback == status) {
                return;
            }
            callbacks.get(preCallback).onDetach();
        }
        if (getChildCount() > 1) {
            removeViewAt(CALLBACK_CUSTOM_INDEX);
        }

        for (Class key : callbacks.keySet()) {
            if (key == status) {
                SuccessCallback successCallback = (SuccessCallback) callbacks.get(SuccessCallback.class);
                if (key == SuccessCallback.class) {
                    successCallback.show();
                } else {
                    successCallback.showWithCallback(callbacks.get(key).getSuccessVisible());
                    Callback currentCallback = callbacks.get(key);//当前需要展示的callback
                    View rootView = currentCallback.getRootView();
                    addView(rootView);
                    if(data != null)
                        currentCallback.update(data);
                    callbacks.get(key).onAttach(context, rootView);
                }
                preCallback = status;
            }
        }
    }

    public void setCallBack(Class<? extends Callback> callback, Transport transport) {
        if (transport == null) {
            return;
        }
        checkCallbackExist(callback);
        transport.order(context, callbacks.get(callback).obtainRootView());
    }

    private void checkCallbackExist(Class<? extends Callback> callback) {
        if (!callbacks.containsKey(callback)) {
            throw new IllegalArgumentException(String.format("The Callback (%s) is nonexistent.", callback
                    .getSimpleName()));
        }
    }
}
