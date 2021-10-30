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
import com.AoRGMapT.bean.FracturingTestBean;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.bean.WellLocationDeterminationBean;
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

/**
 * 压裂试油
 */
public class FracturingTestActivity extends AppCompatActivity {

    private final static String TAG = "FracturingTestActivity";

    //记录时间
    private EditText mEditTime;

    //压裂工作方法总结
    private GridView mGridFracture;
    private ImageAdapter mFractureImageAdapter;
    List<ImageBean> mFractureImageBeans = new ArrayList<>();

    //试油工作总结
    private GridView mGridOilTest;
    private ImageAdapter mOilTestImageAdapter;
    List<ImageBean> mOilTestImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();

    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0压裂工作方法总结 1试油工作总结 2 现场照片
    private int mChooseImageType = 0;

    //当前项目的id
    private String id;

    private TextView project_name;
    private EditText wellName;
    private EditText horizon;
    private EditText rupture_name;
    private EditText fracturing_time;
    private EditText rupture_pressure;
    private EditText fracturing_fluid_consumption;
    private EditText sand_addition;
    private EditText perforation_depth;
    private EditText perforation_thickness;
    private EditText daily_oil_production;
    private EditText cumulative_oil_production;
    private EditText daily_gas_production;
    private EditText cumulative_gas_production;
    private EditText daily_water_yield;
    private EditText cumulative_water_production;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fracturing_test);
        id = getIntent().getStringExtra("id");

        mEditTime = findViewById(R.id.ed_time);
        mGridFracture = findViewById(R.id.grid_fracture);
        mGridOilTest = findViewById(R.id.grid_oil_test);
        mGridScene = findViewById(R.id.grid_scene);

        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        horizon = findViewById(R.id.horizon);
        rupture_name = findViewById(R.id.rupture_name);
        fracturing_time = findViewById(R.id.fracturing_time);
        rupture_pressure = findViewById(R.id.rupture_pressure);
        fracturing_fluid_consumption = findViewById(R.id.fracturing_fluid_consumption);
        sand_addition = findViewById(R.id.sand_addition);
        perforation_depth = findViewById(R.id.perforation_depth);
        perforation_thickness = findViewById(R.id.perforation_thickness);
        daily_oil_production = findViewById(R.id.daily_oil_production);
        cumulative_oil_production = findViewById(R.id.cumulative_oil_production);
        daily_gas_production = findViewById(R.id.daily_gas_production);
        cumulative_gas_production = findViewById(R.id.cumulative_gas_production);
        daily_water_yield = findViewById(R.id.daily_water_yield);
        cumulative_water_production = findViewById(R.id.cumulative_water_production);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FracturingTestActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("projectId", BaseApplication.currentProject.getId());
                map.put("taskType", "压裂试油");
                map.put("wellName", wellName.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                FracturingTestBean extendData = new FracturingTestBean();
                extendData.setHorizon(horizon.getText().toString());
                extendData.setCumulative_oil_production(cumulative_oil_production.getText().toString());
                extendData.setCumulative_gas_production(cumulative_gas_production.getText().toString());
                extendData.setFracturing_time(fracturing_time.getText().toString());
                extendData.setCumulative_water_production(cumulative_water_production.getText().toString());
                extendData.setDaily_gas_production(daily_gas_production.getText().toString());
                extendData.setFracturing_fluid_consumption(fracturing_fluid_consumption.getText().toString());
                extendData.setDaily_oil_production(daily_oil_production.getText().toString());
                extendData.setPerforation_depth(perforation_depth.getText().toString());
                extendData.setDaily_water_yield(daily_water_yield.getText().toString());
                extendData.setRupture_name(rupture_name.getText().toString());
                extendData.setSand_addition(sand_addition.getText().toString());
                extendData.setRupture_pressure(rupture_pressure.getText().toString());
                map.put("extendData", new Gson().toJson(extendData));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {

                            //添加图片
                            addPhotos(responseDataItem.getData().getId(), mPlanBean);
                            if (deleteImageList != null && deleteImageList.size() > 0) {
                                EncapsulationImageUrl.deletePhotoFile(responseDataItem.getData().getId(), deleteImageList);
                            }
                            FracturingTestActivity.this.finish();

                        } else {
                            Toast.makeText(FracturingTestActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(FracturingTestActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        setCurrentTime();

        //验收意见与专家名单
        mFractureImageBeans.add(new ImageBean(null, null, 1));
        mFractureImageAdapter = new ImageAdapter(mFractureImageBeans, this);
        mGridFracture.setAdapter(mFractureImageAdapter);
        mFractureImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mFractureImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mFractureImageBeans.remove(position);
                mGridFracture.setAdapter(mFractureImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mFractureImageBeans.size() == 7) {
                    Toast.makeText(FracturingTestActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(FracturingTestActivity.this);
                }

            }
        });

        //整改情况说明
        mOilTestImageBeans.add(new ImageBean(null, null, 1));
        mOilTestImageAdapter = new ImageAdapter(mOilTestImageBeans, this);
        mGridOilTest.setAdapter(mOilTestImageAdapter);
        mOilTestImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mOilTestImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mOilTestImageBeans.remove(position);
                mGridOilTest.setAdapter(mOilTestImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mOilTestImageBeans.size() == 7) {
                    Toast.makeText(FracturingTestActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(FracturingTestActivity.this);
                }

            }
        });
        //现场照片
        mSceneImageBeans.add(new ImageBean(null, null, 1));
        mSceneImageAdapter = new ImageAdapter(mSceneImageBeans, this);
        mGridScene.setAdapter(mSceneImageAdapter);
        mSceneImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mSceneImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mSceneImageBeans.remove(position);
                mGridScene.setAdapter(mSceneImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSceneImageBeans.size() == 7) {
                    Toast.makeText(FracturingTestActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(FracturingTestActivity.this);
                }

            }
        });

        //设置项目名称和井号
        if (BaseApplication.currentProject != null) {
            project_name.setText(BaseApplication.currentProject.getProjectName());
            wellName.setText(BaseApplication.currentProject.getDefaultWellName());
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
                                FracturingTestActivity.this.finish();
                            } else {
                                Toast.makeText(FracturingTestActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(FracturingTestActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                            FracturingTestBean fracturingTestBean = new Gson().fromJson(mPlanBean.getExtendData(), FracturingTestBean.class);
                            if (fracturingTestBean != null) {
                                fracturing_time.setText(fracturingTestBean.getFracturing_time());
                                cumulative_oil_production.setText(fracturingTestBean.getCumulative_oil_production());
                                cumulative_gas_production.setText(fracturingTestBean.getCumulative_gas_production());
                                fracturing_fluid_consumption.setText(fracturingTestBean.getFracturing_fluid_consumption());
                                cumulative_water_production.setText(fracturingTestBean.getCumulative_water_production());
                                horizon.setText(fracturingTestBean.getHorizon());
                                daily_gas_production.setText(fracturingTestBean.getDaily_gas_production());
                                daily_oil_production.setText(fracturingTestBean.getDaily_oil_production());
                                daily_water_yield.setText(fracturingTestBean.getDaily_water_yield());
                                rupture_name.setText(fracturingTestBean.getRupture_name());
                                perforation_depth.setText(fracturingTestBean.getPerforation_depth());
                                rupture_pressure.setText(fracturingTestBean.getRupture_pressure());
                                sand_addition.setText(fracturingTestBean.getSand_addition());
                            }

                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mFractureImageBeans.add(mFractureImageBeans.size() - 1, imageBean);
                                }
                                mFractureImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mOilTestImageBeans.add(mOilTestImageBeans.size() - 1, imageBean);
                                }
                                mOilTestImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos3()) && mPlanBean.getFiles3() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles3()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mSceneImageBeans.add(mSceneImageBeans.size() - 1, imageBean);
                                }
                                mSceneImageAdapter.notifyDataSetChanged();
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

        if (mFractureImageBeans != null && mFractureImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "压裂试油", "p1", mFractureImageBeans, photoFiles);
        }
        if (mOilTestImageBeans != null && mOilTestImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "压裂试油", "p2", mOilTestImageBeans, photoFiles2);
        }
        if (mSceneImageBeans != null && mSceneImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "压裂试油", "p3", mSceneImageBeans, photoFiles3);
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
                mFractureImageBeans.add(mFractureImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridFracture.setAdapter(mFractureImageAdapter);
            } else if (mChooseImageType == 1) {
                mOilTestImageBeans.add(mOilTestImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridOilTest.setAdapter(mOilTestImageAdapter);
            } else if (mChooseImageType == 2) {
                mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridScene.setAdapter(mSceneImageAdapter);
            }
        }


    }

}


