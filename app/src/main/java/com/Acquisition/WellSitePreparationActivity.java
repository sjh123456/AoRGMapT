package com.Acquisition;

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
import com.Acquisition.bean.WellSitePreparationBean;
import com.Acquisition.util.ChooseImageDialog;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.EncapsulationImageUrl;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.RequestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss

    private TextView project_name;
    private EditText wellName;
    private EditText headwaters;
    private EditText electrify;
    private EditText well_pad_leveling;
    private EditText environmental_protection_guarantee;
    private EditText young_crops;
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
        setContentView(R.layout.activity_well_site_preparation);

        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);

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
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WellSitePreparationActivity.this.finish();
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
                            //上传成功之后，删除本地项目
                            if (key != -1) {
                                LocalDataUtil.getIntance(WellSitePreparationActivity.this).deletePlanInfo(key);
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

        if (!TextUtils.isEmpty(id) || key != -1) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (key != -1) {
                        LocalDataUtil.getIntance(WellSitePreparationActivity.this).deletePlanInfo(key);
                        WellSitePreparationActivity.this.finish();
                    } else {
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
                WellSitePreparationActivity.this.finish();
            }
        });

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
                mWaterImageBeans.clear();
                mWaterImageBeans.addAll(imageBeans);
                for (ImageBean image : mWaterImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mWaterImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos2())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos2(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mPowerOnImageBeans.clear();
                mPowerOnImageBeans.addAll(imageBeans);
                for (ImageBean image : mPowerOnImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mPowerOnImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos3())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos3(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mLevelingConditionsImageBeans.clear();
                mLevelingConditionsImageBeans.addAll(imageBeans);
                for (ImageBean image : mLevelingConditionsImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mLevelingConditionsImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos4())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos4(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mEnvironmentalImageBeans.clear();
                mEnvironmentalImageBeans.addAll(imageBeans);
                for (ImageBean image : mEnvironmentalImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mEnvironmentalImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos5())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos5(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mYoungCropImageBeans.clear();
                mYoungCropImageBeans.addAll(imageBeans);
                for (ImageBean image : mYoungCropImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mYoungCropImageAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 显示项目信息
     */
    private void showPlanInfo() {
        if (mPlanBean != null) {
            wellName.setText(mPlanBean.getWellName());
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
            WellSitePreparationBean sitePreparationBean = new Gson().fromJson(mPlanBean.getExtendData(), WellSitePreparationBean.class);
            if (sitePreparationBean != null) {
                electrify.setText(sitePreparationBean.getElectrify());
                young_crops.setText(sitePreparationBean.getYoung_crops());
                environmental_protection_guarantee.setText(sitePreparationBean.getEnvironmental_protection_guarantee());
                headwaters.setText(sitePreparationBean.getHeadwaters());
                well_pad_leveling.setText(sitePreparationBean.getWell_pad_leveling());
            }
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
        }
    }

    //将信息保存在本地
    private void saveLocal() {
        if (BaseApplication.currentProject == null) {
            return;
        }
        PlanBean planBean = new PlanBean();
        planBean.setProjectId(BaseApplication.currentProject.getId());
        planBean.setTaskType("井场准备");
        planBean.setWellName(wellName.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        WellSitePreparationBean bean = new WellSitePreparationBean();
        bean.setElectrify(electrify.getText().toString());
        bean.setHeadwaters(headwaters.getText().toString());
        bean.setWell_pad_leveling(well_pad_leveling.getText().toString());
        bean.setYoung_crops(young_crops.getText().toString());
        bean.setEnvironmental_protection_guarantee(environmental_protection_guarantee.getText().toString());
        planBean.setExtendData(new Gson().toJson(bean));
        if (mWaterImageBeans != null) {
            for (ImageBean image : mWaterImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos(new Gson().toJson(mWaterImageBeans));
        }
        if (mPowerOnImageBeans != null) {
            for (ImageBean image : mPowerOnImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos2(new Gson().toJson(mPowerOnImageBeans));
        }
        if (mLevelingConditionsImageBeans != null) {
            for (ImageBean image : mLevelingConditionsImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos3(new Gson().toJson(mLevelingConditionsImageBeans));
        }
        if (mEnvironmentalImageBeans != null) {
            for (ImageBean image : mEnvironmentalImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos4(new Gson().toJson(mEnvironmentalImageBeans));
        }
        if (mYoungCropImageBeans != null) {
            for (ImageBean image : mYoungCropImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos5(new Gson().toJson(mYoungCropImageBeans));
        }
        if (key != -1) {
            planBean.setKey(key);
            LocalDataUtil.getIntance(this).updatePlanInfo(planBean);

        } else {
            LocalDataUtil.getIntance(this).addLocalPlanInfo(planBean);
        }
    }


    /**
     * 设置当前时间
     */
    private void setCurrentTime() {

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
                mWaterImageBeans.add(mWaterImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridWater.setAdapter(mWaterImageAdapter);
            } else if (mChooseImageType == 1) {
                mPowerOnImageBeans.add(mPowerOnImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridPowerOn.setAdapter(mPowerOnImageAdapter);
            } else if (mChooseImageType == 2) {
                mLevelingConditionsImageBeans.add(mLevelingConditionsImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridLevelingConditions.setAdapter(mLevelingConditionsImageAdapter);
            } else if (mChooseImageType == 3) {
                mEnvironmentalImageBeans.add(mEnvironmentalImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridEnvironmental.setAdapter(mEnvironmentalImageAdapter);
            } else if (mChooseImageType == 4) {
                mYoungCropImageBeans.add(mYoungCropImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridYoungCrops.setAdapter(mYoungCropImageAdapter);
            }
        }


    }

}
