package com.nowy.albumlib.view.recyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.nowy.albumlib.aty.controller.LoadingState;

/**
 * Created by Nowy on 2018/1/24.
 * 辅助adapter完成上拉加载的操作
 */

public class LoadingStateUtil {




    /**
     * 上拉加载更多，完成
     * @param adapter
     * @param pageNo 页码
     * @param pageSize 每页条目数
     * @param total 数据总数,如果传入为0，则一直只会显示加载完成
     * @param isError 加载是否失败，true为失败
     */
    public static void loadMoreCompleted(BaseQuickAdapter adapter,int pageNo,int pageSize,int total,boolean isError){
        if(adapter == null) return;
        if(isError){
            adapter.loadMoreFail();
        }else{
            if (pageNo == 0 && adapter.getData().size() < pageSize) {//第一页显示的数据小于pageSize，说明不需要加载更多
                adapter.loadMoreEnd(true);
            } else {
                if(total <= 0){
                    total = Integer.MAX_VALUE;
                }
                if (adapter.getData().size() >= total) {//适配器存在的数据集合大于网络返回的总数
                    adapter.loadMoreEnd(true);//true is gone,false is visible(滑到最后，不能加载更多一直显示)
                } else {
                    adapter.loadMoreComplete();
//                    adapter.loadMoreEnd(false);
                }
            }
        }
    }


    public static void loadMoreCompleted(BaseQuickAdapter adapter,LoadingState loadMoreState){
        if(adapter == null) return;
        if(loadMoreState == LoadingState.ERROR){
            adapter.loadMoreFail();
        }else{
            if (loadMoreState == LoadingState.NO_MORE) {//第一页显示的数据小于pageSize，说明不需要加载更多
                adapter.loadMoreEnd(true);
            } else {
                adapter.loadMoreComplete();
            }
        }
    }





    /**
     * 上拉加载更多底部footerView显示(使用httpUtil里面的默认条目数)
     * @param adapter
     * @param pageNo 页码
     * @param total 数据总数
     */
    public static void loadMoreCompleted(BaseQuickAdapter adapter,int pageNo,int total,boolean isError){
//        loadMoreCompleted(adapter,pageNo, HttpUtil.ITEM_COUNT_DEF,total,isError);
    }




    /**
     * 上拉加载更多失败
     * @param adapter
     */
    public static void loadMoreError(BaseQuickAdapter adapter){
        adapter.loadMoreFail();
    }

}
