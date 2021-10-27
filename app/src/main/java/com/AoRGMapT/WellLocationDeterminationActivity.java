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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AoRGMapT.adapter.ImageAdapter;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.bean.WellLocationDeterminationBean;
import com.AoRGMapT.util.ChooseImageDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.LocationUtil;
import com.AoRGMapT.util.RequestUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 井位确认
 */
public class WellLocationDeterminationActivity extends AppCompatActivity {

    private final String TAG = "WellLocationDeterminationActivity";

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

    private TextView project_name;
    private EditText well_name;
    private EditText ed_altitude;
    private EditText ed_x;
    private EditText ed_y;
    private EditText ed_location;
    private EditText structural_location;
    private EditText administrative_region;
    private EditText traffic;
    private EditText geographical_situation;
    private EditText residential_area;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;

    //当前项目的id
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);


        id = getIntent().getStringExtra("id");

        mEditX = findViewById(R.id.ed_x);
        mEditY = findViewById(R.id.ed_y);
        mEditLocation = findViewById(R.id.ed_location);
        mEditAltitude = findViewById(R.id.ed_altitude);
        mEditTime = findViewById(R.id.ed_time);
        mGridTraffic = findViewById(R.id.grid_traffic);
        mGridGeography = findViewById(R.id.grid_geography);
        mGridResident = findViewById(R.id.grid_resident);
        project_name = findViewById(R.id.project_name);
        well_name = findViewById(R.id.well_name);
        ed_altitude = findViewById(R.id.ed_altitude);
        ed_x = findViewById(R.id.ed_x);
        ed_y = findViewById(R.id.ed_y);
        ed_location = findViewById(R.id.ed_location);
        structural_location = findViewById(R.id.structural_location);
        administrative_region = findViewById(R.id.administrative_region);
        traffic = findViewById(R.id.traffic);
        geographical_situation = findViewById(R.id.geographical_situation);
        residential_area = findViewById(R.id.residential_area);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WellLocationDeterminationActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> map = new HashMap<>();
                map.put("event_id", BaseApplication.currentProject.getId());
                map.put("task_type", "井位确认");
                map.put("well_name", well_name.getText().toString());
                map.put("location", ed_location.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("record_data", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                WellLocationDeterminationBean extend_data = new WellLocationDeterminationBean();
                extend_data.setAltitude(ed_altitude.getText().toString());
                extend_data.setX(ed_x.getText().toString());
                extend_data.setY(ed_y.getText().toString());
                extend_data.setStructural_location(structural_location.getText().toString());
                extend_data.setAdministrative_region(administrative_region.getText().toString());
                extend_data.setTraffic(traffic.getText().toString());
                extend_data.setGeographical_situation(geographical_situation.getText().toString());
                extend_data.setResidential_area(residential_area.getText().toString());
                map.put("extend_data", new Gson().toJson(extend_data));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            WellLocationDeterminationActivity.this.finish();

                        } else {
                            Toast.makeText(WellLocationDeterminationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(WellLocationDeterminationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        tv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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

        //设置项目名称和井号
        if (BaseApplication.currentProject != null) {
            project_name.setText(BaseApplication.currentProject.getProjectName());
            well_name.setText(BaseApplication.currentProject.getDefaultWellName());
            recorder.setText(BaseApplication.userInfo.getUserName());
        }

        if (!TextUtils.isEmpty(id)) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                        @Override
                        public void onsuccess(ResponseDataItem o) {
                            if (o.isSuccess()) {
                                WellLocationDeterminationActivity.this.finish();
                            } else {
                                Toast.makeText(WellLocationDeterminationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(WellLocationDeterminationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            DataAcquisitionUtil.getInstance().detailByJson(id, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                @Override
                public void onsuccess(ResponseDataItem<PlanBean> planBeanResponseDataItem) {
                    if (planBeanResponseDataItem != null) {
                        PlanBean planBean = planBeanResponseDataItem.getData();
                        if (planBean != null) {
                            well_name.setText(planBean.getWellName());
                            ed_location.setText(planBean.getLocation());
                            recorder.setText(planBean.getRecorder());
                            remark.setText(planBean.getRemark());
                            mEditTime.setText(planBean.getCreateTime());
                            WellLocationDeterminationBean determinationBean = new Gson().fromJson(planBean.getExtendData(), WellLocationDeterminationBean.class);
                            if (determinationBean != null) {
                                ed_altitude.setText(determinationBean.getAltitude());
                                ed_x.setText(determinationBean.getX());
                                ed_y.setText(determinationBean.getY());
                                structural_location.setText(determinationBean.getStructural_location());
                                administrative_region.setText(determinationBean.getAdministrative_region());
                                traffic.setText(determinationBean.getTraffic());
                                geographical_situation.setText(determinationBean.getGeographical_situation());
                                residential_area.setText(determinationBean.getResidential_area());
                            }
                        }
                    }

                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG, "项目详情请求失败");
                }
            });
        } else {
            tv_remove.setVisibility(View.GONE);
        }

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
            BaseApplication.aMapLocation = aMapLocation;
            if (!TextUtils.isEmpty(id)) {
                mEditX.setText(aMapLocation.getLatitude() + "");
                mEditY.setText(aMapLocation.getLongitude() + "");
                mEditLocation.setText(aMapLocation.getAddress());
                mEditAltitude.setText(aMapLocation.getAltitude() + "");
            }
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