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
import com.AoRGMapT.bean.CoreDescriptionBean;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.bean.WellLocationDeterminationBean;
import com.AoRGMapT.util.ChooseImageDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.EncapsulationImageUrl;
import com.AoRGMapT.util.LocalDataUtil;
import com.AoRGMapT.util.RequestUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 岩心描述
 */
public class CoreDescriptionActivity extends AppCompatActivity {

    private final static String TAG = "CoreDescriptionActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //记录时间
    private EditText mEditTime;

    //交通情况显示图片
    private GridView mGridSample;
    private ImageAdapter mSampleImageAdapter;
    List<ImageBean> mSampleImageBeans = new ArrayList<>();
    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private TextView project_name;
    private EditText wellName;
    private EditText sample_type;
    private EditText horizon;
    private EditText top_boundary_depth;
    private EditText bottom_boundary_depth;
    private EditText footage;
    private EditText long_heart;
    private EditText harvest_rate;
    private EditText core_description;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_description);

        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        mGridSample = findViewById(R.id.grid_sample);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        sample_type = findViewById(R.id.sample_type);
        horizon = findViewById(R.id.horizon);
        top_boundary_depth = findViewById(R.id.top_boundary_depth);
        bottom_boundary_depth = findViewById(R.id.bottom_boundary_depth);
        footage = findViewById(R.id.footage);
        long_heart = findViewById(R.id.long_heart);
        harvest_rate = findViewById(R.id.harvest_rate);
        core_description = findViewById(R.id.core_description);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoreDescriptionActivity.this.finish();
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
                map.put("taskType", "岩心描述");
                map.put("wellName", wellName.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                CoreDescriptionBean bean = new CoreDescriptionBean();
                bean.setCore_description(core_description.getText().toString());
                bean.setFootage(footage.getText().toString());
                bean.setBottom_boundary_depth(bottom_boundary_depth.getText().toString());
                bean.setHarvest_rate(harvest_rate.getText().toString());
                bean.setHorizon(horizon.getText().toString());
                bean.setTop_boundary_depth(top_boundary_depth.getText().toString());
                bean.setLong_heart(long_heart.getText().toString());
                bean.setSample_type(sample_type.getText().toString());
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
                                LocalDataUtil.getIntance(CoreDescriptionActivity.this).deletePlanInfo(key);
                            }
                            CoreDescriptionActivity.this.finish();

                        } else {
                            Toast.makeText(CoreDescriptionActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(CoreDescriptionActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        setCurrentTime();

        //交通情况的图片
        mSampleImageBeans.add(new ImageBean(null, null, 1));
        mSampleImageAdapter = new ImageAdapter(mSampleImageBeans, this);
        mGridSample.setAdapter(mSampleImageAdapter);
        mSampleImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mSampleImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mSampleImageBeans.remove(position);
                mGridSample.setAdapter(mSampleImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSampleImageBeans.size() == 7) {
                    Toast.makeText(CoreDescriptionActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    ChooseImageDialog.getInstance().show(CoreDescriptionActivity.this);
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
                        LocalDataUtil.getIntance(CoreDescriptionActivity.this).deletePlanInfo(key);
                        CoreDescriptionActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    CoreDescriptionActivity.this.finish();
                                } else {
                                    Toast.makeText(CoreDescriptionActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(CoreDescriptionActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                CoreDescriptionActivity.this.finish();
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
                mSampleImageBeans.clear();
                mSampleImageBeans.addAll(imageBeans);
                for (ImageBean image : mSampleImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mSampleImageAdapter.notifyDataSetChanged();
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
                                    mSampleImageBeans.add(mSampleImageBeans.size() - 1, imageBean);
                                }
                                mSampleImageAdapter.notifyDataSetChanged();
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
            CoreDescriptionBean bean = new Gson().fromJson(mPlanBean.getExtendData(), CoreDescriptionBean.class);
            if (bean != null) {
                core_description.setText(bean.getCore_description());
                footage.setText(bean.getFootage());
                horizon.setText(bean.getHorizon());
                bottom_boundary_depth.setText(bean.getBottom_boundary_depth());
                top_boundary_depth.setText(bean.getTop_boundary_depth());
                harvest_rate.setText(bean.getHarvest_rate());
                long_heart.setText(bean.getLong_heart());
                sample_type.setText(bean.getSample_type());
            }
        }
    }

    //将信息保存在本地
    private void saveLocal() {
        if (BaseApplication.currentProject == null) {
            return;
        }
        PlanBean planBean = new PlanBean();
        planBean.setProjectId(BaseApplication.currentProject.getId());
        planBean.setTaskType("岩心描述");
        planBean.setWellName(wellName.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        CoreDescriptionBean bean = new CoreDescriptionBean();
        bean.setCore_description(core_description.getText().toString());
        bean.setFootage(footage.getText().toString());
        bean.setBottom_boundary_depth(bottom_boundary_depth.getText().toString());
        bean.setHarvest_rate(harvest_rate.getText().toString());
        bean.setHorizon(horizon.getText().toString());
        bean.setTop_boundary_depth(top_boundary_depth.getText().toString());
        bean.setLong_heart(long_heart.getText().toString());
        bean.setSample_type(sample_type.getText().toString());
        planBean.setExtendData(new Gson().toJson(bean));
        if (mSampleImageBeans != null) {
            for (ImageBean image : mSampleImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos(new Gson().toJson(mSampleImageBeans));
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
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        mEditTime.setText(simpleDateFormat.format(date));
    }

    //新增图片
    private void addPhotos(String taskid, PlanBean planBean) {

        if (mSampleImageBeans != null && mSampleImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "岩心描述", "p1", mSampleImageBeans, photoFiles);
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
            mSampleImageBeans.add(mSampleImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
            mGridSample.setAdapter(mSampleImageAdapter);
        }


    }
}