package com.nowy.baselib.utils.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.nowy.baselib.activity.BasePermissionAty;
import com.nowy.baselib.utils.T;


/**
 * Created by Nowy on 2017/6/28.
 * 权限请求帮助类
 */

public class PermissionHelper {
    public static final int REQ_PERMISSION_NORMAL = 5;//通用权限
    public static final int REQ_PERMISSION_CAMERA = 6;//摄像头
    public static final int REQ_PERMISSION_CALL = 7;//打电话
    public static final int REQ_PERMISSION_ALBUM=8;//系统相册

    public interface PermissionListener {
        void onSuccess();

        void onFailure();
    }


    /**
     * 请求摄像头和SD卡
     *
     * @param activity
     * @param listener
     */
    public static void reqCameraAndSDcard(final BasePermissionAty activity, final PermissionListener listener) {
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_CAMERA, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (XPermissionUtils.isCameraEnable()) {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        } else {
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "相机、读写SD卡");
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s("获取相机权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "相机、读写SD卡");
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            new AlertDialog.Builder(activity).setTitle("温馨提示")
                                    .setMessage("我们需要相机权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(activity, deniedPermissions,
                                                    REQ_PERMISSION_CAMERA);
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    /**
     * 请求拨号权限
     *
     * @param activity
     * @param listener
     */
    public static void reqCall(final BasePermissionAty activity, final PermissionListener listener) {
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_CALL, new String[]{
                        Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s( "获取拨号权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, "拨号");
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            new AlertDialog.Builder(activity).setTitle("温馨提示")
                                    .setMessage("我们需要拨号权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(activity, deniedPermissions,
                                                    REQ_PERMISSION_CALL);
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }


    /**
     * 请求权限
     *
     * @param activity
     * @param listener
     * @param tips       对话框弹出提示："我们需要"+tips+"权限才能正常使用该功能"
     * @param permission 权限数组
     */
    public static void reqPermission(final BasePermissionAty activity,
                                     final PermissionListener listener,
                                     final String tips, String... permission) {
        XPermissionUtils.requestPermissions(activity, REQ_PERMISSION_NORMAL, permission,
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        T.s("获取" + tips + "权限失败");
                        if (listener != null) {
                            listener.onFailure();
                        }
                        if (alwaysDenied) { // 拒绝后不再询问 -> 提示跳转到设置
                            PermissionDialogUtil.showPermissionManagerDialog(activity, tips);
                        } else {    // 拒绝 -> 提示此公告的意义，并可再次尝试获取权限
                            new AlertDialog.Builder(activity).setTitle("温馨提示")
                                    .setMessage("我们需要" + tips + "权限才能正常使用该功能")
                                    .setNegativeButton("取消", null)
                                    .setPositiveButton("验证权限", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            XPermissionUtils.requestPermissionsAgain(activity, deniedPermissions,
                                                    REQ_PERMISSION_NORMAL);
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }









}
