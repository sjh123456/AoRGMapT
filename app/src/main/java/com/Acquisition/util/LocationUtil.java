package com.Acquisition.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class LocationUtil {

    private final String TAG="LocationUtil";


    private static LocationUtil mLocationUtil;

    public static LocationUtil getInstance() {

        synchronized (LocationUtil.class) {
            if (mLocationUtil == null) {
                mLocationUtil = new LocationUtil();
            }
        }
        return mLocationUtil;
    }


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public void startLocation(Context context, AMapLocationListener mLocationListener) {

        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(context);

            //设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取一次定位结果：
//该方法默认为false。
            mLocationOption.setOnceLocation(true);

//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);

            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置是否允许模拟位置,默认为true，允许模拟位置
            mLocationOption.setMockEnable(true);
            //给定位客户端对象设置定位参数
            if (null != mLocationClient) {
                mLocationClient.setLocationOption(mLocationOption);
                //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
                mLocationClient.stopLocation();
                mLocationClient.startLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }


    public void startLocationAndCheckPermission(Context context, AMapLocationListener mLocationListener) {
        String[] permission = {Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION};
        if (!XXPermissions.isGranted(context, permission)) {
            XXPermissions.with(context).permission(permission)
                    .request(new OnPermissionCallback() {
                                 @Override
                                 public void onGranted(List<String> permissions, boolean all) {
                                     startLocation(context, mLocationListener);
                                 }


                                 @Override
                                 public void onDenied(List<String> permissions, boolean never) {
                                     Toast.makeText(context, "请先打开定位权限", Toast.LENGTH_SHORT).show();

                                 }
                             }
                    );

        } else {
            startLocation(context, mLocationListener);
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    public void checkLocationPermission(int locationRequestCode, Activity context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            checkLocationPermission29(locationRequestCode, context);
        } else {
            checkLocationPermission28(locationRequestCode, context);
        }


    }

    @TargetApi(28)
    public void checkLocationPermission28(int locationRequestCode, Activity context) {
        if (!checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, context) ||
                !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION, context)) {
            String[] permList = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            // requestPermissions(permList, locationRequestCode);
            ActivityCompat.requestPermissions(context, permList, locationRequestCode);

        }
    }

    @TargetApi(29)
    private void checkLocationPermission29(int locationRequestCode, Activity context) {
        if (!checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, context) ||
                !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION, context) ||
                !checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, context)) {
            String[] permList = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
            ActivityCompat.requestPermissions(context, permList, locationRequestCode);
        }

    }

//    @TargetApi(30)
//    private void checkLocationPermission30(int backgroundLocationRequestCode, Context context) {
//        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, context)) {
//            new AlertDialog.Builder(context).setTitle("请打开位置权限").setMessage("请打开位置权限").setPositiveButton("请打开位置权限", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    ActivityCompat.requestPermissions(context, permList, locationRequestCode);
//                }
//            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            }).create().show();
//        }
//
//    }


    private boolean checkSinglePermission(String permission, Context context) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }


}
