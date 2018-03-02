package com.nowy.baselib.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AssetsUtil {
    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 从assets 文件夹中获取文件并读取数据
    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param context
     * @param path    路径 "./"  或""  表示Assets的根目录
     * @param extName 扩展名"*" 或"" ：表示返回所有 ;".apk" 返回apk的文件
     * @return
     */
    public static ArrayList<File> getFileList(Context context, boolean debug, String path, String extName) {
        ArrayList<File> mFileList = new ArrayList<File>();

        String[] fileNames;
        try {
            fileNames = context.getResources().getAssets().list(path);
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i].toLowerCase().endsWith(extName) || extName.equals("*")) {
                    File file = new File(fileNames[i]);
                    mFileList.add(file);

                    if (debug) {
                        System.out.println(fileNames[i]);
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mFileList;
    }

    public static String[] getFileStrings(Context context, boolean debug, String path, String extName) {
        String[] fileNames = null;
        ArrayList<String> fileStrings = new ArrayList<String>();
        int count = 0;
        try {
            fileNames = context.getResources().getAssets().list(path);
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i].toLowerCase().endsWith(extName) || extName.equals("*")) {
                    fileStrings.add(fileNames[i]);
                    count = count + 1;
                    if (debug) {
                        System.out.println(fileNames[i]);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return (String[]) fileStrings.toArray(new String[0]);
        //return fileNames;
    }

    public static String[] getSubArray() {
        String[] strArray1 = new String[]{"D ", "A ", "B ", "C "};
        String[] strArray2 = java.util.Arrays.copyOf(strArray1, 2);


        for (int i = 0; i < strArray2.length; i++) System.out.print(strArray2[i] + ",");
        System.out.println("");
        return strArray2;
    }


}