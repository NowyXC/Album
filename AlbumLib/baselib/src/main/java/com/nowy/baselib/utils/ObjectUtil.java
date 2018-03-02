package com.nowy.baselib.utils;

import android.content.Context;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象操作工具类
 * 主要作用是对对象的转化和序列化
 * # 对象转字符串->base64
 * # base64字符串转对象
 * # 对象转byte[]
 * # byte[]转对象
 * # 对象写到本地
 * # 读取本地的对象
 */
public class ObjectUtil {
    /**
     * 对象转字符串
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        String str = null;
        try {
            str = Base64.encodeToString(toByteArray(obj), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 字符串转对象
     *
     * @return
     */
    public static Object toObject(String base64str) {
        Object obj = null;
        try {
            obj = toObject(Base64.decode(base64str, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * Object序列化类
     *
     * @param data
     * @param filename
     * @return
     */
    public static boolean saveObject(Context context, Object data,
                                     String filename) {
        FileOutputStream out;
        ObjectOutputStream oout;
        try {
            out = context.openFileOutput(filename + ".odb",
                    Context.MODE_PRIVATE);
            oout = new ObjectOutputStream(out);
            oout.writeObject(data);
            oout.flush();
            out.flush();
            oout.close();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Object反序列化
     *
     * @return
     */
    public static Object readObject(Context context, String filename) {
        FileInputStream in = null;
        ObjectInputStream oin = null;
        Object data = null;
        try {
            in = context.openFileInput(filename + ".odb");
            oin = new ObjectInputStream(in);
            data = oin.readObject();
            oin.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
