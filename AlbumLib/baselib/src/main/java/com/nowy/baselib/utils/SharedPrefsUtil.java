package com.nowy.baselib.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * sharedPreferences的工具类，使用一个新的prefsname前先执行init在prefsHashMap加入一个sharedPreferences的工具类。
 * getPrefsUtil方法获取map中保存的sharedPreferences进行操作，能同时保留多个不同名字的sharedPreferences的。
 * getPrefsUtil(key)可以根据不同的key获取不同的util实例，进行key-value存储
 * SharedPrefsUtil默认保存最后init的util对象作为快速操作的当前实例
 */
public class SharedPrefsUtil {
    public Context context;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    static HashMap<String, SharedPrefsUtil> prefsHashMap = new HashMap<>();

    public synchronized static SharedPrefsUtil getPrefsUtil(String prefsName) {
        return prefsHashMap.get(prefsName);
    }


    public static void init(Context context, String prefsName, int mode) {
        if (prefsHashMap.containsKey(prefsName)) {
            return;
        }
        SharedPrefsUtil prefsUtil = new SharedPrefsUtil();
        prefsUtil.context = context;
        prefsUtil.prefs = prefsUtil.context.getSharedPreferences(prefsName, mode);
        prefsUtil.editor = prefsUtil.prefs.edit();
        prefsHashMap.put(prefsName, prefsUtil);
    }

    private SharedPrefsUtil() {
    }


    public boolean getBoolean(String key, boolean defaultVal) {
        return this.prefs.getBoolean(key, defaultVal);
    }

    public boolean getBoolean(String key) {
        return this.prefs.getBoolean(key, false);
    }


    public String getString(String key, String defaultVal) {
        return this.prefs.getString(key, defaultVal);
    }

    public String getString(String key) {
        return this.prefs.getString(key, null);
    }

    public int getInt(String key, int defaultVal) {
        return this.prefs.getInt(key, defaultVal);
    }

    public int getInt(String key) {
        return this.prefs.getInt(key, 0);
    }


    public float getFloat(String key, float defaultVal) {
        return this.prefs.getFloat(key, defaultVal);
    }

    public float getFloat(String key) {
        return this.prefs.getFloat(key, 0f);
    }

    public long getLong(String key, long defaultVal) {
        return this.prefs.getLong(key, defaultVal);
    }

    public long getLong(String key) {
        return this.prefs.getLong(key, 0l);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return this.prefs.getStringSet(key, defaultVal);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key) {
        return this.prefs.getStringSet(key, null);
    }

    public Map<String, ?> getAll() {
        return this.prefs.getAll();
    }


    public SharedPrefsUtil putString(String key, String value) {
        editor.putString(key, value);
//        editor.commit();
        return this;
    }

    public SharedPrefsUtil putInt(String key, int value) {
        editor.putInt(key, value);
//        editor.commit();
        return this;
    }

    public SharedPrefsUtil putFloat(String key, float value) {
        editor.putFloat(key, value);
//        editor.commit();
        return this;
    }

    public SharedPrefsUtil putLong(String key, long value) {
        editor.putLong(key, value);
//        editor.commit();
        return this;
    }

    public SharedPrefsUtil putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
//        editor.commit();
        return this;
    }

    public void commit() {
        editor.commit();
    }


    public void apply() {
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPrefsUtil putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
        return this;
    }

    /**
     * 保存搜索历史记录在本地
     *
     * @param keyword
     */
//    public void saveHistory(String keyword, String accessToken) {
//        //保存搜索本地历史记录
//        List<String> history = getHistory(accessToken);
//        int n = -1;
//        for (int i = 0; i < history.size(); i++) {
//            if (history.get(i).equals(keyword)) {
//                n = i;
//            }
//        }
//        if (n != -1) {
//            //当前历史列表已存在该keyword，删除当前位置，再放到第一位
//            history.remove(n);
//            ArrayList<String> newHistory = new ArrayList<String>();
//            newHistory.add(keyword);
//            newHistory.addAll(history);
//            setHistory(newHistory, accessToken);
//        } else {
//            //当前历史列不存在该keyword，再放到第一位
//            ArrayList<String> newHistory = new ArrayList<String>();
//            newHistory.add(keyword);
//            newHistory.addAll(history);
//            setHistory(newHistory, accessToken);
//        }
//    }


    public void saveHistory(String keyword, String accesstoken) {
        if(TextUtils.isEmpty(keyword)) return;
        //保存搜索本地历史记录
        List<String> history = getHistory(accesstoken);
        if (history.size() < 100) {
            //判断历史记录是否小于100个，小于100时，遍历列表查看当前输入的条件是否已存在
            int n = -1;
            for (int i = 0; i < history.size(); i++) {
                if (history.get(i).equals(keyword)) {
                    n = i;
                }
            }
            if (n != -1) {
                //当前历史列表已存在该keyword，删除当前位置，再放到第一位
                history.remove(n);
                ArrayList<String> newhistory = new ArrayList<String>();
                newhistory.add(keyword);
                newhistory.addAll(history);
                setHistory(newhistory, accesstoken);
            } else {
                //当前历史列不存在该keyword，再放到第一位
                ArrayList<String> newhistory = new ArrayList<String>();
                newhistory.add(keyword);
                newhistory.addAll(history);
                setHistory(newhistory, accesstoken);
            }
        } else {
            //当列表位100个时，删除最后一个，
            int n = -1;
            for (int i = 0; i < history.size(); i++) {
                if (history.get(i).equals(keyword)) {
                    n = i;
                }
            }
            if (n != -1) {
                //当前历史列表已存在该keyword，删除当前位置，再放到第一位
                history.remove(n);
                ArrayList<String> newhistory = new ArrayList<String>();
                newhistory.add(keyword);
                newhistory.addAll(history);
                setHistory(newhistory, accesstoken);
            } else {
                //当前历史列不存在该keyword，再放到第一位
                history.remove(history.size() - 1);
                ArrayList<String> newhistory = new ArrayList<String>();
                newhistory.add(keyword);
                newhistory.addAll(history);
                setHistory(newhistory, accesstoken);
            }
        }
    }



    /**
     * 获取搜索历史记录
     *
     * @return
     */
    public ArrayList<String> getHistory(String key) {
        String values = prefs.getString(key + "history", "");
        ArrayList<String> list = (ArrayList<String>) ObjectUtil.toObject(values);
        return list == null ? new ArrayList<String>() : list;
    }

    /**
     * 设置搜索历史记录
     */
    public void setHistory(ArrayList<String> history, String key) {
        String values = ObjectUtil.toString(history);
        prefs.edit().putString(key + "history", values).apply();
    }

    /**
     * 清空数据
     */
    public void clear(){
        prefs.edit().clear().apply();
    }
}
