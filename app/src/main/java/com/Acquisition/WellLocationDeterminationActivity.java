package com.Acquisition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.adapter.ImageAdapter;
import com.Acquisition.bean.ImageBean;
import com.Acquisition.bean.PlanBean;
import com.Acquisition.bean.ResponseDataItem;
import com.Acquisition.bean.UpdateFileResponseData;
import com.Acquisition.bean.WellLocationDeterminationBean;
import com.Acquisition.util.ChooseImageDialog;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.EncapsulationImageUrl;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.LocationUtil;
import com.Acquisition.util.RequestUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 井位确定
 */
public class WellLocationDeterminationActivity extends AppCompatActivity {

    private final static String TAG = "WellLocationDeterminationActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0交通情况 1地理情况 2 居民情况
    private int mChooseImageType = 0;

    private TextView project_name;
    private EditText wellName;
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
    private TextView tv_local_save;

    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);

        mEditX = findViewById(R.id.ed_x);
        mEditY = findViewById(R.id.ed_y);
        mEditLocation = findViewById(R.id.ed_location);
        mEditAltitude = findViewById(R.id.ed_altitude);
        mEditTime = findViewById(R.id.ed_time);
        mGridTraffic = findViewById(R.id.grid_traffic);
        mGridGeography = findViewById(R.id.grid_geography);
        mGridResident = findViewById(R.id.grid_resident);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
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
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WellLocationDeterminationActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApplication.currentProject == null) {
                    return;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("projectId", BaseApplication.currentProject.getId());
                map.put("taskType", "井位确定");
                map.put("wellName", wellName.getText().toString());
                map.put("location", ed_location.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                WellLocationDeterminationBean extendData = new WellLocationDeterminationBean();
                extendData.setAltitude(mEditAltitude.getText().toString());
                extendData.setX(ed_x.getText().toString());
                extendData.setY(ed_y.getText().toString());
                extendData.setStructural_location(structural_location.getText().toString());
                extendData.setAdministrative_region(administrative_region.getText().toString());
                extendData.setTraffic(traffic.getText().toString());
                extendData.setGeographical_situation(geographical_situation.getText().toString());
                extendData.setResidential_area(residential_area.getText().toString());
                map.put("extendData", new Gson().toJson(extendData));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            //添加图片
                            addPhotos(responseDataItem.getData().getId(), mPlanBean);
                            if (deleteImageList != null && deleteImageList.size() > 0) {
                                deletePhotoFile(responseDataItem.getData().getId());
                            }
                            //上传成功之后，删除本地项目
                            if (key != -1) {
                                LocalDataUtil.getIntance(WellLocationDeterminationActivity.this).deletePlanInfo(key);
                            }
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
                ImageBean imageBean = mTrafficImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
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
                ImageBean imageBean = mGeographyImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
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
                ImageBean imageBean = mResidentImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
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
            wellName.setText(BaseApplication.currentProject.getDefaultWellName());
            recorder.setText(BaseApplication.userInfo.getUserName());
        }

        if (!TextUtils.isEmpty(id) || key != -1) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (key != -1) {
                        LocalDataUtil.getIntance(WellLocationDeterminationActivity.this).deletePlanInfo(key);
                        WellLocationDeterminationActivity.this.finish();
                    } else {
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
                }
            });
//判断显示本地还是云端
            if (key != -1) {
                getLocalInfo();
            } else {
                tv_local_save.setVisibility(View.GONE);
                getOnlineInfo();
            }


        } else {
            tv_remove.setVisibility(View.GONE);
        }

        tv_local_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocal();
                WellLocationDeterminationActivity.this.finish();
            }
        });
    }

    //将信息保存在本地
    private void saveLocal() {
        if (BaseApplication.currentProject == null) {
            return;
        }
        PlanBean planBean = new PlanBean();
        planBean.setProjectId(BaseApplication.currentProject.getId());
        planBean.setTaskType("井位确定");
        planBean.setWellName(wellName.getText().toString());
        planBean.setLocation(ed_location.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        WellLocationDeterminationBean extendData = new WellLocationDeterminationBean();
        extendData.setAltitude(mEditAltitude.getText().toString());
        extendData.setX(ed_x.getText().toString());
        extendData.setY(ed_y.getText().toString());
        extendData.setStructural_location(structural_location.getText().toString());
        extendData.setAdministrative_region(administrative_region.getText().toString());
        extendData.setTraffic(traffic.getText().toString());
        extendData.setGeographical_situation(geographical_situation.getText().toString());
        extendData.setResidential_area(residential_area.getText().toString());
        planBean.setExtendData(new Gson().toJson(extendData));
        if (mTrafficImageBeans != null) {
            for (ImageBean image : mTrafficImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos(new Gson().toJson(mTrafficImageBeans));
        }
        if (mGeographyImageBeans != null) {
            for (ImageBean image : mGeographyImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos2(new Gson().toJson(mGeographyImageBeans));
        }
        if (mResidentImageBeans != null) {
            for (ImageBean image : mResidentImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos3(new Gson().toJson(mResidentImageBeans));
        }
        if (key != -1) {
            planBean.setKey(key);
            LocalDataUtil.getIntance(this).updatePlanInfo(planBean);

        } else {
            LocalDataUtil.getIntance(this).addLocalPlanInfo(planBean);
        }
    }

    //获取线上信息
    private void getOnlineInfo() {
        if (!TextUtils.isEmpty(id)) {
            DataAcquisitionUtil.getInstance().detailByJson(id, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                @Override
                public void onsuccess(ResponseDataItem<PlanBean> planBeanResponseDataItem) {
                    if (planBeanResponseDataItem != null) {
                        mPlanBean = planBeanResponseDataItem.getData();
                        if (mPlanBean != null) {
                            showPlanInfo();
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                                //交通图片
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mTrafficImageBeans.add(mTrafficImageBeans.size() - 1, imageBean);
                                }
                                mTrafficImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                                //地理情况
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mGeographyImageBeans.add(mGeographyImageBeans.size() - 1, imageBean);
                                }
                                mGeographyImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos3()) && mPlanBean.getFiles3() != null) {
                                //居民点情况
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles3()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mResidentImageBeans.add(mResidentImageBeans.size() - 1, imageBean);
                                }
                                mResidentImageAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG, "项目详情请求失败");
                }
            });
        }
    }

    //获取展示本地信息
    private void getLocalInfo() {
        if (key != -1) {
            PlanBean planBean = LocalDataUtil.getIntance(this).queryLocalPlanInfoFromKey(key);
            mPlanBean = planBean;
            showPlanInfo();
            if (!TextUtils.isEmpty(planBean.getSitePhotos())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mTrafficImageBeans.clear();
                mTrafficImageBeans.addAll(imageBeans);
                for (ImageBean image : mTrafficImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mTrafficImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos2())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos2(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mGeographyImageBeans.clear();
                mGeographyImageBeans.addAll(imageBeans);
                for (ImageBean image : mGeographyImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mGeographyImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos3())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos3(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mResidentImageBeans.clear();
                mResidentImageBeans.addAll(imageBeans);
                for (ImageBean image : mResidentImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mResidentImageAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 显示项目信息
     */
    private void showPlanInfo() {
        if (mPlanBean != null) {
            wellName.setText(mPlanBean.getWellName());
            ed_location.setText(mPlanBean.getLocation());
            recorder.setText(mPlanBean.getRecorder());
            remark.setText(mPlanBean.getRemark());
            String time = mPlanBean.getCreateTime();
            try {
                Date date = simpleDateFormat.parse(mPlanBean.getCreateTime());
                time = simpleDateFormat.format(date);
            } catch (Exception ex) {
                Log.e(TAG, "");
            }
            mEditTime.setText(time);
            WellLocationDeterminationBean determinationBean = new Gson().fromJson(mPlanBean.getExtendData(), WellLocationDeterminationBean.class);
            if (determinationBean != null) {
                mEditAltitude.setText(determinationBean.getAltitude());
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = null;

        if (requestCode == 100 && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            picturePath = ChooseImageDialog.getInstance().getPhotoFile().getAbsolutePath();
        }
        if (!TextUtils.isEmpty(picturePath)) {

            if (mChooseImageType == 0) {
                mTrafficImageBeans.add(mTrafficImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridTraffic.setAdapter(mTrafficImageAdapter);
            } else if (mChooseImageType == 1) {
                mGeographyImageBeans.add(mGeographyImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridGeography.setAdapter(mGeographyImageAdapter);
            } else {
                mResidentImageBeans.add(mResidentImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridResident.setAdapter(mResidentImageAdapter);
            }
        }


    }


    //上传图片信息
    private void updatePhotos(String taskid, String fileIndicator, List<ImageBean> imageBeans, List<PlanBean.PhotoFile> photoFiles) {

        String bucketId = "";
        if (photoFiles != null && photoFiles.size() > 0) {
            bucketId = photoFiles.get(0).getBucketId();
        }

        Map<String, String> param = new HashMap<>();
        param.put("biz", "井位确定");
        param.put("taskId", taskid);
        param.put("bucketName", "井位确定");
        param.put("bucketType", "井位确定");
        param.put("bucketId", bucketId);
        param.put("fileIndicator", fileIndicator);
        DataAcquisitionUtil.getInstance().updateFile(imageBeans, param, new RequestUtil.OnResponseListener<ResponseDataItem<UpdateFileResponseData.FileData>>() {
            @Override
            public void onsuccess(ResponseDataItem<UpdateFileResponseData.FileData> fileDataResponseDataItem) {
                if (fileDataResponseDataItem.isSuccess()) {
                    Log.e(TAG, fileIndicator + "图片提交成功");
                } else {
                    Log.e(TAG, fileIndicator + "图片提交失败");
                }
            }

            @Override
            public void fail(String code, String message) {
                Log.e(TAG, "图片提交失败");
            }
        });
    }

    //新增图片
    private void addPhotos(String taskid, PlanBean planBean) {
        if (mTrafficImageBeans != null && mTrafficImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            updatePhotos(taskid, "p1", mTrafficImageBeans, photoFiles);
        }
        if (mGeographyImageBeans != null && mGeographyImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles2();
            }
            updatePhotos(taskid, "p2", mGeographyImageBeans, photoFiles);
        }
        if (mResidentImageBeans != null && mResidentImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles3();
            }
            updatePhotos(taskid, "p3", mResidentImageBeans, photoFiles);
        }
    }


    //删除照片信息
    private void deletePhotoFile(String taskid) {

        if (deleteImageList != null && deleteImageList.size() > 0) {
            String fileids = "";
            for (int i = 0; i < deleteImageList.size(); i++) {
                if (i == deleteImageList.size() - 1) {
                    fileids = fileids + deleteImageList.get(i);
                } else {
                    fileids = fileids + deleteImageList.get(i) + ",";
                }

            }

            DataAcquisitionUtil.getInstance().deleteFile(taskid, fileids, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                @Override
                public void onsuccess(ResponseDataItem responseDataItem) {
                    if (responseDataItem.isSuccess()) {
                        Log.e(TAG, "删除文件成功");
                    } else {
                        Log.e(TAG, "删除文件失败");
                    }

                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG, "删除文件失败");
                }
            });
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
            if (TextUtils.isEmpty(id)) {
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
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
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