package com.nowy.baselib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nowy on 2017/7/21.
 */

public class TestDataUtil {
    public static List<String> getStrData(int n){
        List<String> data = new ArrayList<>();
        for(int i = 0 ; i < n ; i++){
            data.add("模拟数据"+(i+1));
        }
        return data;
    }



    public static List<String> getStrDataRandom(int max){
        Random random = new Random();
        int len = random.nextInt(max);
        return getStrData(len);
    }


    public static <T> List<T> getData(Class<T> clazz,int n){
        List<T> data = new ArrayList<>();
        for(int i = 0 ; i < n ; i++){
            try {
                T t = clazz.newInstance();
                data.add(t);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return data;
    }
}
