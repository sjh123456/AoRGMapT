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

    //0水源情况 1通电情况 2 井场平整条件  3环保保障措施 4青苗补偿情况
    private int mChooseImageType = 0;


    private TextView project_name;
    private EditText well_name;
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
        well_name = findViewById(R.id.well_name);
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
                Map<String, String> map = new HashMap<>();
                map.put("event_id", BaseApplication.currentProject.getId());
                map.put("task_type", "井位确认");
                map.put("well_name", well_name.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("record_data", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());

                WellSitePreparationBean bean = new WellSitePreparationBean();
                bean.setElectrify(electrify.getText().toString());
                bean.setHeadwaters(headwaters.getText().toString());
                bean.setWell_pad_leveling(well_pad_leveling.getText().toString());
                bean.setYoung_crops(young_crops.getText().toString());
                bean.setEnvironmental_protection_guarantee(environmental_protection_guarantee.getText().toString());
                map.put("extend_data", new Gson().toJson(bean));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
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
            well_name.setText(BaseApplication.currentProject.getDefaultWellName());
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
                        PlanBean planBean = planBeanResponseDataItem.getData();
                        if (planBean != null) {
                            well_name.setText(planBean.getWellName());
                            recorder.setText(planBean.getRecorder());
                            remark.setText(planBean.getRemark());
                            mEditTime.setText(planBean.getCreateTime());
                            WellSitePreparationBean sitePreparationBean = new Gson().fromJson(planBean.getExtendData(), WellSitePreparationBean.class);
                            if (sitePreparationBean != null) {
                                electrify.setText(sitePreparationBean.getElectrify());
                                young_crops.setText(sitePreparationBean.getYoung_crops());
                                environmental_protection_guarantee.setText(sitePreparationBean.getEnvironmental_protection_guarantee());
                                headwaters.setText(sitePreparationBean.getHeadwaters());
                                well_pad_leveling.setText(sitePreparationBean.getWell_pad_leveling());
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
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                if (mChooseImageType == 0) {
                    mWaterImageBeans.add(mWaterImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridWater.setAdapter(mWaterImageAdapter);
                } else if (mChooseImageType == 1) {
                    mPowerOnImageBeans.add(mPowerOnImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridPowerOn.setAdapter(mPowerOnImageAdapter);
                } else if (mChooseImageType == 2) {
                    mLevelingConditionsImageBeans.add(mLevelingConditionsImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridLevelingConditions.setAdapter(mLevelingConditionsImageAdapter);
                } else if (mChooseImageType == 3) {
                    mEnvironmentalImageBeans.add(mEnvironmentalImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridEnvironmental.setAdapter(mEnvironmentalImageAdapter);
                } else if (mChooseImageType == 4) {
                    mYoungCropImageBeans.add(mYoungCropImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridYoungCrops.setAdapter(mYoungCropImageAdapter);
                }
            }
        }

    }
}