package com.AoRGMapT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AoRGMapT.adapter.ImageAdapter;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.util.ChooseImageDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.LocationUtil;
import com.AoRGMapT.util.RequestUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 井位确认
 */
public class WellLocationDeterminationActivity extends AppCompatActivity {

    //x坐标
    private EditText mEditX;
    //y坐标
    private EditText mEditY;
    //地理位置
    private EditText mEditLocation;
    //海拔
    private EditText mEditAltitude;
    //记录时间
    private EditText mEditTime;

    //交通情况显示图片
    private GridView mGridTraffic;
    private ImageAdapter mTrafficImageAdapter;
    List<ImageBean> mTrafficImageBeans = new ArrayList<>();

    //地理情况显示图片
    private GridView mGridGeography;
    private ImageAdapter mGeographyImageAdapter;
    List<ImageBean> mGeographyImageBeans = new ArrayList<>();

    //居民情况显示图片
    private GridView mGridResident;
    private ImageAdapter mResidentImageAdapter;
    List<ImageBean> mResidentImageBeans = new ArrayList<>();

    //0交通情况 1地理情况 2 居民情况
    private int mChooseImageType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        mEditX = findViewById(R.id.ed_x);
        mEditY = findViewById(R.id.ed_y);
        mEditLocation = findViewById(R.id.ed_location);
        mEditAltitude = findViewById(R.id.ed_altitude);
        mEditTime = findViewById(R.id.ed_time);
        mGridTraffic = findViewById(R.id.grid_traffic);
        mGridGeography = findViewById(R.id.grid_geography);
        mGridResident = findViewById(R.id.grid_resident);


        setLocationInfo(BaseApplication.aMapLocation);
        LocationUtil.getInstance().startLocationAndCheckPermission(this, new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                setLocationInfo(aMapLocation);

            }
        });
        setCurrentTime();

        //交通情况的图片
        mTrafficImageBeans.add(new ImageBean(null, null, 1));
        mTrafficImageAdapter = new ImageAdapter(mTrafficImageBeans, this);
        mGridTraffic.setAdapter(mTrafficImageAdapter);
        mTrafficImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mTrafficImageBeans.remove(position);
                mGridTraffic.setAdapter(mTrafficImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mTrafficImageBeans.size() == 7) {
                    Toast.makeText(WellLocationDeterminationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(WellLocationDeterminationActivity.this);
                }

            }
        });

        //地理情况的图片
        mGeographyImageBeans.add(new ImageBean(null, null, 1));
        mGeographyImageAdapter = new ImageAdapter(mGeographyImageBeans, this);
        mGridGeography.setAdapter(mGeographyImageAdapter);
        mGeographyImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mGeographyImageBeans.remove(position);
                mGridGeography.setAdapter(mGeographyImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mGeographyImageBeans.size() == 7) {
                    Toast.makeText(WellLocationDeterminationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(WellLocationDeterminationActivity.this);
                }

            }
        });

        //居民情况的图片
        mResidentImageBeans.add(new ImageBean(null, null, 1));
        mResidentImageAdapter = new ImageAdapter(mResidentImageBeans, this);
        mGridResident.setAdapter(mResidentImageAdapter);
        mResidentImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mResidentImageBeans.remove(position);
                mGridResident.setAdapter(mResidentImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mResidentImageBeans.size() == 7) {
                    Toast.makeText(WellLocationDeterminationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(WellLocationDeterminationActivity.this);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            String picturePath = cursor.getString(columnIndex);

            cursor.close();


            Glide.with(this).asBitmap().load(new File(picturePath)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    if (mChooseImageType == 0) {
                        mTrafficImageBeans.add(mTrafficImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridTraffic.setAdapter(mTrafficImageAdapter);
                    } else if (mChooseImageType == 1) {
                        mGeographyImageBeans.add(mGeographyImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridGeography.setAdapter(mGeographyImageAdapter);
                    } else {
                        mResidentImageBeans.add(mResidentImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridResident.setAdapter(mResidentImageAdapter);
                    }
                }
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                if (mChooseImageType == 0) {
                    mTrafficImageBeans.add(mTrafficImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridTraffic.setAdapter(mTrafficImageAdapter);
                } else if (mChooseImageType == 1) {
                    mGeographyImageBeans.add(mGeographyImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridGeography.setAdapter(mGeographyImageAdapter);
                } else {
                    mResidentImageBeans.add(mResidentImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridResident.setAdapter(mResidentImageAdapter);
                }
            }
        }

    }


    /**
     * 设置定位信息
     */
    private void setLocationInfo(AMapLocation aMapLocation) {

        if (aMapLocation != null && aMapLocation.getErrorCode() == 0 && aMapLocation.getAddress() != null && !TextUtils.isEmpty(aMapLocation.getAddress())) {
            //停止定位
            LocationUtil.getInstance().stopLocation();
            mEditX.setText(aMapLocation.getLatitude() + "");
            mEditY.setText(aMapLocation.getLongitude() + "");
            mEditLocation.setText(aMapLocation.getAddress());
            mEditAltitude.setText(aMapLocation.getAltitude() + "");

            BaseApplication.aMapLocation = aMapLocation;
        }


    }

    /**
     * 设置当前时间
     */
    private void setCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        mEditTime.setText(simpleDateFormat.format(date));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            LocationUtil.getInstance().startLocation(this, new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null && aMapLocation.getAddress() != null && !TextUtils.isEmpty(aMapLocation.getAddress())) {
                        //停止定位
                        LocationUtil.getInstance().stopLocation();
                        mEditX.setText(aMapLocation.getLatitude() + "");
                        mEditY.setText(aMapLocation.getLongitude() + "");
                        mEditLocation.setText(aMapLocation.getAddress());
                    }
                }
            });
        }
    }
}