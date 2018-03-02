package com.nowy.baselib.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

/**
 * Created by Nowy on 2017/12/22.
 * 跳转工具类，跳转到各种系统应用或者第三方应用等
 */

public class IntentUtil {
    public static final String TAG = IntentUtil.class.getSimpleName();
    /**
     * 跳转至拨号界面
     *
     * @param mContext
     *            上下文Context
     * @param phoneNumber
     *            需要呼叫的手机号码
     */
    public static void toCallAty(Context mContext, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent call = new Intent(Intent.ACTION_DIAL, uri);
        mContext.startActivity(call);
    }


    /**
     * 拨打电话，直接拨打，需要权限
     *
     * @param context
     * @param phoneNumber
     */
    public static void call(Context context, String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
        }

    }

    /**
     * 跳转至发送短信界面(自动设置接收方的号码)
     *
     * @param mContext
     *            Activity
     * @param strPhone
     *            手机号码
     * @param strMsgContext
     *            短信内容
     */
    public static void toSendMsgAty(Context mContext, String strPhone,
                                             String strMsgContext) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(strPhone)) {
            Uri uri = Uri.parse("smsto:" + strPhone);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            sendIntent.putExtra("sms_body", strMsgContext);
            mContext.startActivity(sendIntent);
        }
    }


    /**
     * 跳转至联系人选择界面
     *
     * @param mContext
     *            上下文
     * @param requestCode
     *            请求返回区分代码
     *            在onActivityResult中可以通过@see{getChosePhoneNumber()}把intent转化为手机号
     */
    public static void toContactsAty(Activity mContext, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mContext.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取选择的联系人的手机号码
     *
     * @param mContext
     *            上下文
     * @param resultCode
     *            请求返回Result状态区分代码
     * @param data
     *            onActivityResult返回的Intent
     * @return
     */
    public static String getChosePhoneNumber(Activity mContext,
                                               int resultCode, Intent data) {
        // 返回结果
        String phoneResult = "";
        if (Activity.RESULT_OK == resultCode) {
            Uri uri = data.getData();
            Cursor mCursor = mContext.managedQuery(uri, null, null, null, null);
            mCursor.moveToFirst();

            int phoneColumn = mCursor
                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int phoneNum = mCursor.getInt(phoneColumn);
            if (phoneNum > 0) {
                // 获得联系人的ID号
                int idColumn = mCursor
                        .getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = mCursor.getString(idColumn);
                // 获得联系人的电话号码的cursor;
                Cursor phones = mContext.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactId, null, null);
                if (phones.moveToFirst()) {
                    // 遍历所有的电话号码
                    for (; !phones.isAfterLast(); phones.moveToNext()) {
                        int index = phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int typeindex = phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int phone_type = phones.getInt(typeindex);
                        String phoneNumber = phones.getString(index);
                        if (phone_type == 2) {
                            phoneResult = phoneNumber;
                        }
                    }
                    if (!phones.isClosed()) {
                        phones.close();
                    }
                }
            }
            // 关闭游标
            mCursor.close();
        }

        return phoneResult;
    }



    /**
     * 跳转至拍照程序界面
     *
     * @param mContext
     *            上下文
     * @param requestCode
     *            请求返回Result区分代码
     */
    public static void toCameraAty(Activity mContext, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mContext.startActivityForResult(intent, requestCode);
    }


    /**
     * 跳转到拍照页面，指定保存路径(兼容7.0的uri权限)
     * @param mContext
     * @param filePath
     * @param authority authority FileProvider的全称。Android 7.0需要的URI权限请求问题
     *  BuildConfig.APPLICATION_ID+".FileProvider"
     * @param requestCode
     */
    public static void toCameraAty(Activity mContext,String filePath,String authority, int requestCode) {
        File outputFile = new File(filePath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdir();
        }


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri contentUri = Uri.fromFile(new File(filePath)); // 传递路径
        if(Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            contentUri = FileProvider.getUriForFile(mContext, authority, outputFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        mContext.startActivityForResult(intent, requestCode);
    }


    /**
     * 跳转至相册选择界面
     *
     * @param mContext
     *            上下文
     * @param requestCode
     *
     * 在onActivityResult里面可以调用@see{getPickedImage()} 把intent data转化为bmp
     */
    public static void toPickerAty(Activity mContext, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        mContext.startActivityForResult(intent, requestCode);
    }


    /**
     * 获得选中相册的图片
     *
     * @param mContext
     *            上下文
     * @param data
     *            onActivityResult返回的Intent
     * @return
     */

    @SuppressWarnings({ "deprecation", "unused" })
    public static Bitmap getPickedImage(Activity mContext, Intent data) {
        if (data == null) {
            return null;
        }

        Bitmap bm = null;
        Cursor cursor=null;

        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = mContext.getContentResolver();

        // 此处的用于判断接收的Activity是不是你想要的那个
        try {
            Uri originalUri = data.getData(); // 获得图片的uri
            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
            // 这里开始的第二部分，获取图片的路径：
            String[] proj = { MediaStore.Images.Media.DATA };
            // 好像是android多媒体数据库的封装接口，具体的看Android文档
            cursor = mContext.managedQuery(originalUri, proj, null,
                    null, null);
            // 按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            String path = cursor.getString(column_index);

        } catch (Exception e) {
            Logger.t(TAG).e(e.getMessage());
        }finally {
            // 不用了关闭游标
            if(Build.VERSION.SDK_INT<14){
                //在android 4.0及其以上的版本中，Cursor会自动关闭，不需要用户自己关闭
                cursor.close();
            }
        }

        return bm;
    }



    /**
     * 进入系统裁剪
     * @param inputUri 需裁剪的图片路径 Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/image.jpg")
     * @param outputUri 裁剪后图片路径 Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/image_cut.jpg")
     * @param width 裁剪后宽度(px)
     * @param height 裁剪后高度(px)
     */
    public static void toImageCutAty(Activity activity, int requestCode,
                                                Uri inputUri, Uri outputUri,
                                                int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true); // 去黑边
        intent.putExtra("scaleUpIfNeeded", true); // 去黑边
        // aspectX aspectY 裁剪框宽高比例
        intent.putExtra("aspectX", width); // 输出是X方向的比例
        intent.putExtra("aspectY", height);
        // outputX outputY 输出图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", width); // 输出X方向的像素
        intent.putExtra("outputY", height);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false); // 设置为不返回数据
        activity.startActivityForResult(intent, requestCode);
    }



    /**
     * 打开视频 (Android 7.0 uri权限注意)
     * @param mContext
     * @param videoPath
     */
    public static void openVideo(Context mContext, String videoPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(videoPath));
        intent.setDataAndType(uri, "video/*");
        mContext.startActivity(intent);
    }

    /**
     * 打开URL (Android 7.0 uri权限注意)
     * @param mContext
     * @param url
     */
    public static void openURL(Context mContext, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }

    /**
     * 下载文件(Android 7.0 uri权限注意)
     * @param context
     * @param fileUrl
     */
    public static void downloadFile(Context context, String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setDestinationInExternalPublicDir("/Download/", fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }


    /**
     * 打开图片 (Android 7.0 uri权限注意)
     * @param mContext
     * @param imagePath
     */
    public static void openImage(Context mContext, String imagePath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(imagePath));
        intent.setDataAndType(uri, "image/*");
        mContext.startActivity(intent);
    }


    /**
     * 调用本地浏览器打开一个网页
     *
     * @param mContext
     *            上下文
     * @param strSiteUrl
     *            网页地址
     */
    public static void openWebSite(Context mContext, String strSiteUrl) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strSiteUrl));
        mContext.startActivity(webIntent);
    }


    /**
     * 跳转至系统设置界面
     *
     * @param mContext
     *            上下文
     */
    public static void toSettingAty(Context mContext) {
        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
        mContext.startActivity(settingsIntent);
    }


    /**
     * 跳转至WIFI设置界面
     *
     * @param mContext
     *            上下文
     */
    public static void toWIFISettingAty(Context mContext) {
        Intent wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        mContext.startActivity(wifiSettingsIntent);
    }


    /**
     * 启动本地应用打开PDF
     *
     * @param mContext
     *            上下文
     * @param filePath
     *            文件路径
     */
    public static void openPDFFile(Context mContext, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "未检测到可打开PDF相关软件", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * 启动本地应用打开PDF
     *
     * @param mContext
     *            上下文
     * @param filePath
     *            文件路径
     */
    public static void openWordFile(Context mContext, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(path, "application/msword");
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "未检测到可打开Word文档相关软件", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * 调用WPS打开office文档 http://bbs.wps.cn/thread-22349340-1-1.html
     *
     * @param mContext
     *            上下文
     * @param filePath
     *            文件路径
     */
    public static void openOfficeByWPS(Context mContext, String filePath) {

        try {

            // 文件存在性检查
            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(mContext, filePath + "文件路径不存在",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查是否安装WPS
            String wpsPackageEng = "cn.wps.moffice_eng";// 普通版与英文版一样
            // String wpsActivity =
            // "cn.wps.moffice.documentmanager.PreStartActivity";
            String wpsActivity2 = "cn.wps.moffice.documentmanager.PreStartActivity2";// 默认第三方程序启动

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setClassName(wpsPackageEng, wpsActivity2);

            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            mContext.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "本地未安装WPS", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "打开文档失败", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 判断是否存在指定的Activity
     *
     * @param mContext
     *            上下文
     * @param packageName
     *            包名
     * @param className
     *            activity全路径类名
     * @return
     */
    public static boolean isExistActivity(Context mContext, String packageName,
                                          String className) {

        Boolean result = true;
        Intent intent = new Intent();
        intent.setClassName(packageName, className);

        if (mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            result = false;
        } else if (intent.resolveActivity(mContext.getPackageManager()) == null) {
            result = false;
        } else {
            List<ResolveInfo> list = mContext.getPackageManager()
                    .queryIntentActivities(intent, 0);
            if (list.size() == 0) {
                result = false;
            }
        }

        return result;
    }






}
