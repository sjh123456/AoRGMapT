package com.AoRGMapT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.AoRGMapT.adapter.ImageAdapter;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.bean.WellLocationDeterminationBean;
import com.AoRGMapT.bean.WellSitePreparationBean;
import com.AoRGMapT.util.ChooseImageDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.EncapsulationImageUrl;
import com.AoRGMapT.util.RequestUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//井场准备
public class WellSitePreparationActivity extends AppCompatActivity {

    private final String TAG = "TAG";

    //记录时间
    private EditText mEditTime;

    //水源情况
    private GridView mGridWater;
    private ImageAdapter mWaterImageAdapter;
    List<ImageBean> mWaterImageBeans = new ArrayList<>();

    //通电情况
    private GridView mGridPowerOn;
    private ImageAdapter mPowerOnImageAdapter;
    List<ImageBean> mPowerOnImageBeans = new ArrayList<>();

    //井场平整条件
    private GridView mGridLevelingConditions;
    private ImageAdapter mLevelingConditionsImageAdapter;
    List<ImageBean> mLevelingConditionsImageBeans = new ArrayList<>();

    //环保保障措施
    private GridView mGridEnvironmental;
    private ImageAdapter mEnvironmentalImageAdapter;
    List<ImageBean> mEnvironmentalImageBeans = new ArrayList<>();

    //青苗补偿情况
    private GridView mGridYoungCrops;
    private ImageAdapter mYoungCropImageAdapter;
    List<ImageBean> mYoungCropImageBeans = new ArrayList<>();

    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0水源情况 1通电情况 2 井场平整条件  3环保保障措施 4青苗补偿情况
    private int mChooseImageType = 0;


    private TextView project_name;
    private EditText wellName;
    private EditText headwaters;
    private EditText electrify;
    private EditText well_pad_leveling;
    private EditText environmental_protection_guarantee;
    private EditText young_crops;
    private EditText recorder;
    private EditText ed_time;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;


    //当前项目的id
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_site_preparation);

        id = getIntent().getStringExtra("id");

        mEditTime = findViewById(R.id.ed_time);
        mGridWater = findViewById(R.id.grid_water);
        mGridPowerOn = findViewById(R.id.grid_power_on);
        mGridLevelingConditions = findViewById(R.id.grid_leveling_conditions);
        mGridEnvironmental = findViewById(R.id.grid_environmental);
        mGridYoungCrops = findViewById(R.id.grid_young_crops);


        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        headwaters = findViewById(R.id.headwaters);
        electrify = findViewById(R.id.electrify);
        well_pad_leveling = findViewById(R.id.well_pad_leveling);
        environmental_protection_guarantee = findViewById(R.id.environmental_protection_guarantee);
        young_crops = findViewById(R.id.young_crops);
        recorder = findViewById(R.id.recorder);
        ed_time = findViewById(R.id.ed_time);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WellSitePreparationActivity.this.finish();
            }
        });


        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("projectId", BaseApplication.currentProject.getId());
                map.put("taskType", "井场准备");
                map.put("wellName", wellName.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                WellSitePreparationBean bean = new WellSitePreparationBean();
                bean.setElectrify(electrify.getText().toString());
                bean.setHeadwaters(headwaters.getText().toString());
                bean.setWell_pad_leveling(well_pad_leveling.getText().toString());
                bean.setYoung_crops(young_crops.getText().toString());
                bean.setEnvironmental_protection_guarantee(environmental_protection_guarantee.getText().toString());
                map.put("extendData", new Gson().toJson(bean));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            //添加图片
                            addPhotos(responseDataItem.getData().getId(), mPlanBean);
                            if (deleteImageList != null && deleteImageList.size() > 0) {
                                EncapsulationImageUrl.deletePhotoFile(responseDataItem.getData().getId(), deleteImageList);
                            }
                            WellSitePreparationActivity.this.finish();

                        } else {
                            Toast.makeText(WellSitePreparationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(WellSitePreparationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });


        //交通情况的图片
        mWaterImageBeans.add(new ImageBean(null, null, 1));
        mWaterImageAdapter = new ImageAdapter(mWaterImageBeans, this);
        mGridWater.setAdapter(mWaterImageAdapter);
        mWaterImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mWaterImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mWaterImageBeans.remove(position);
                mGridWater.setAdapter(mWaterImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mWaterImageBeans.size() == 7) {
                    Toast.makeText(WellSitePreparationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(WellSitePreparationActivity.this);
                }

            }
        });

        //通电情况
        mPowerOnImageBeans.add(new ImageBean(null, null, 1));
        mPowerOnImageAdapter = new ImageAdapter(mPowerOnImageBeans, this);
        mGridPowerOn.setAdapter(mPowerOnImageAdapter);
        mPowerOnImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mPowerOnImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mPowerOnImageBeans.remove(position);
                mGridPowerOn.setAdapter(mPowerOnImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mPowerOnImageBeans.size() == 7) {
                    Toast.makeText(WellSitePreparationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(WellSitePreparationActivity.this);
                }

            }
        });

        //井场平整条件
        mLevelingConditionsImageBeans.add(new ImageBean(null, null, 1));
        mLevelingConditionsImageAdapter = new ImageAdapter(mLevelingConditionsImageBeans, this);
        mGridLevelingConditions.setAdapter(mLevelingConditionsImageAdapter);
        mLevelingConditionsImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mLevelingConditionsImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mLevelingConditionsImageBeans.remove(position);
                mGridLevelingConditions.setAdapter(mLevelingConditionsImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mLevelingConditionsImageBeans.size() == 7) {
                    Toast.makeText(WellSitePreparationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(WellSitePreparationActivity.this);
                }

            }
        });

        //环保保障措施
        mEnvironmentalImageBeans.add(new ImageBean(null, null, 1));
        mEnvironmentalImageAdapter = new ImageAdapter(mEnvironmentalImageBeans, this);
        mGridEnvironmental.setAdapter(mEnvironmentalImageAdapter);
        mEnvironmentalImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mEnvironmentalImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mEnvironmentalImageBeans.remove(position);
                mGridEnvironmental.setAdapter(mEnvironmentalImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mEnvironmentalImageBeans.size() == 7) {
                    Toast.makeText(WellSitePreparationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 3;
                    ChooseImageDialog.getInstance().show(WellSitePreparationActivity.this);
                }

            }
        });
        //青苗补偿情况
        mYoungCropImageBeans.add(new ImageBean(null, null, 1));
        mYoungCropImageAdapter = new ImageAdapter(mYoungCropImageBeans, this);
        mGridYoungCrops.setAdapter(mYoungCropImageAdapter);
        mYoungCropImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mYoungCropImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mYoungCropImageBeans.remove(position);
                mGridYoungCrops.setAdapter(mYoungCropImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mYoungCropImageBeans.size() == 7) {
                    Toast.makeText(WellSitePreparationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 4;
                    ChooseImageDialog.getInstance().show(WellSitePreparationActivity.this);
                }

            }
        });
        //设置项目名称和井号
        if (BaseApplication.currentProject != null) {
            project_name.setText(BaseApplication.currentProject.getProjectName());
            wellName.setText(BaseApplication.currentProject.getDefaultWellName());
            recorder.setText(BaseApplication.userInfo.getUserName());
        }
        setCurrentTime();

        if (!TextUtils.isEmpty(id)) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                        @Override
                        public void onsuccess(ResponseDataItem o) {
                            if (o.isSuccess()) {
                                WellSitePreparationActivity.this.finish();
                            } else {
                                Toast.makeText(WellSitePreparationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(WellSitePreparationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            DataAcquisitionUtil.getInstance().detailByJson(id, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                @Override
                public void onsuccess(ResponseDataItem<PlanBean> planBeanResponseDataItem) {
                    if (planBeanResponseDataItem != null) {
                        mPlanBean = planBeanResponseDataItem.getData();
                        if (mPlanBean != null) {
                            wellName.setText(mPlanBean.getWellName());
                            recorder.setText(mPlanBean.getRecorder());
                            remark.setText(mPlanBean.getRemark());
                            mEditTime.setText(mPlanBean.getCreateTime());
                            WellSitePreparationBean sitePreparationBean = new Gson().fromJson(mPlanBean.getExtendData(), WellSitePreparationBean.class);
                            if (sitePreparationBean != null) {
                                electrify.setText(sitePreparationBean.getElectrify());
                                young_crops.setText(sitePreparationBean.getYoung_crops());
                                environmental_protection_guarantee.setText(sitePreparationBean.getEnvironmental_protection_guarantee());
                                headwaters.setText(sitePreparationBean.getHeadwaters());
                                well_pad_leveling.setText(sitePreparationBean.getWell_pad_leveling());
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mWaterImageBeans.add(mWaterImageBeans.size() - 1, imageBean);
                                }
                                mWaterImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mPowerOnImageBeans.add(mPowerOnImageBeans.size() - 1, imageBean);
                                }
                                mPowerOnImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos3()) && mPlanBean.getFiles3() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles3()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mLevelingConditionsImageBeans.add(mLevelingConditionsImageBeans.size() - 1, imageBean);
                                }
                                mLevelingConditionsImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos4()) && mPlanBean.getFiles4() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles4()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mEnvironmentalImageBeans.add(mEnvironmentalImageBeans.size() - 1, imageBean);
                                }
                                mEnvironmentalImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos5()) && mPlanBean.getFiles5() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles5()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mYoungCropImageBeans.add(mYoungCropImageBeans.size() - 1, imageBean);
                                }
                                mYoungCropImageAdapter.notifyDataSetChanged();
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


    /**
     * 设置当前时间
     */
    private void setCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        mEditTime.setText(simpleDateFormat.format(date));
    }

    //新增图片
    private void addPhotos(String taskid, PlanBean planBean) {

        if (mWaterImageBeans != null && mWaterImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "成果验收", "p1", mWaterImageBeans, photoFiles);
        }
        if (mPowerOnImageBeans != null && mPowerOnImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "成果验收", "p2", mPowerOnImageBeans, photoFiles2);
        }
        if (mLevelingConditionsImageBeans != null && mLevelingConditionsImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "成果验收", "p3", mLevelingConditionsImageBeans, photoFiles3);
        }
        if (mEnvironmentalImageBeans != null && mEnvironmentalImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles4 = new ArrayList<>();
            if (planBean != null) {
                photoFiles4 = planBean.getFiles4();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "成果验收", "p4", mEnvironmentalImageBeans, photoFiles4);
        }
        if (mYoungCropImageBeans != null && mYoungCropImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles4 = new ArrayList<>();
            if (planBean != null) {
                photoFiles4 = planBean.getFiles5();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "成果验收", "p5", mYoungCropImageBeans, photoFiles4);
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
                mWaterImageBeans.add(mWaterImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                mGridWater.setAdapter(mWaterImageAdapter);
            } else if (mChooseImageType == 1) {
                mPowerOnImageBeans.add(mPowerOnImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                mGridPowerOn.setAdapter(mPowerOnImageAdapter);
            } else if (mChooseImageType == 2) {
                mLevelingConditionsImageBeans.add(mLevelingConditionsImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                mGridLevelingConditions.setAdapter(mLevelingConditionsImageAdapter);
            } else if (mChooseImageType == 3) {
                mEnvironmentalImageBeans.add(mEnvironmentalImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                mGridEnvironmental.setAdapter(mEnvironmentalImageAdapter);
            } else if (mChooseImageType == 4) {
                mYoungCropImageBeans.add(mYoungCropImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                mGridYoungCrops.setAdapter(mYoungCropImageAdapter);
            }
        }


    }

}
