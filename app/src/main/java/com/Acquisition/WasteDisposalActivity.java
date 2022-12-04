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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.adapter.ImageAdapter;
import com.Acquisition.bean.ImageBean;
import com.Acquisition.bean.PlanBean;
import com.Acquisition.bean.ResponseDataItem;
import com.Acquisition.util.ChooseImageDialog;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.EncapsulationImageUrl;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.RequestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 废弃物处理
 */

public class WasteDisposalActivity extends AppCompatActivity {


    private final static String TAG = "WasteDisposalActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //记录时间
    private EditText mEditTime;

    //废弃物处理方案及紧急预案
    private GridView mGridTreatmentScheme;
    private ImageAdapter mTreatmentSchemeImageAdapter;
    List<ImageBean> mTreatmentSchemeImageBeans = new ArrayList<>();

    //废弃物处理记录及责任人
    private GridView mGridPersonLiable;
    private ImageAdapter mPersonLiableImageAdapter;
    List<ImageBean> mPersonLiableImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridSitePhotos;
    private ImageAdapter mSitePhotosImageAdapter;
    List<ImageBean> mSitePhotosImageBeans = new ArrayList<>();

    //废弃物处理情况总结
    private GridView mGridSummaryTreatment;
    private ImageAdapter mSummaryTreatmentImageAdapter;
    List<ImageBean> mSummaryTreatmentImageBeans = new ArrayList<>();

    //废弃物处理验收意见
    private GridView mGridTreatment;
    private ImageAdapter mTreatmentImageAdapter;
    List<ImageBean> mTreatmentImageBeans = new ArrayList<>();

    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0验收意见与专家名单 1整改情况说明 2 现场照片 3成果验收记录
    private int mChooseImageType = 0;

    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private TextView project_name;
    private EditText wellName;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_disposal);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        mGridTreatmentScheme = findViewById(R.id.grid_treatment_scheme);
        mGridPersonLiable = findViewById(R.id.grid_person_liable);
        mGridSitePhotos = findViewById(R.id.grid_site_photos);
        mGridSummaryTreatment = findViewById(R.id.grid_summary_treatment);
        mGridTreatment = findViewById(R.id.grid_treatment);

        tv_local_save = findViewById(R.id.tv_local_save);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WasteDisposalActivity.this.finish();
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
                map.put("taskType", "废物处理");
                map.put("wellName", wellName.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
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
                                LocalDataUtil.getIntance(WasteDisposalActivity.this).deletePlanInfo(key);
                            }
                            WasteDisposalActivity.this.finish();

                        } else {
                            Toast.makeText(WasteDisposalActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(WasteDisposalActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        setCurrentTime();

        //废弃物处理方案及紧急预案
        mTreatmentSchemeImageBeans.add(new ImageBean(null, null, 1));
        mTreatmentSchemeImageAdapter = new ImageAdapter(mTreatmentSchemeImageBeans, this);
        mGridTreatmentScheme.setAdapter(mTreatmentSchemeImageAdapter);
        mTreatmentSchemeImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mTreatmentSchemeImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mTreatmentSchemeImageBeans.remove(position);
                mGridTreatmentScheme.setAdapter(mTreatmentImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mTreatmentSchemeImageBeans.size() == 7) {
                    Toast.makeText(WasteDisposalActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(WasteDisposalActivity.this);
                }

            }
        });

        //废弃物处理记录及责任人
        mPersonLiableImageBeans.add(new ImageBean(null, null, 1));
        mPersonLiableImageAdapter = new ImageAdapter(mPersonLiableImageBeans, this);
        mGridPersonLiable.setAdapter(mPersonLiableImageAdapter);
        mPersonLiableImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mPersonLiableImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mPersonLiableImageBeans.remove(position);
                mGridPersonLiable.setAdapter(mPersonLiableImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mPersonLiableImageBeans.size() == 7) {
                    Toast.makeText(WasteDisposalActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(WasteDisposalActivity.this);
                }

            }
        });

        //现场照片
        mSitePhotosImageBeans.add(new ImageBean(null, null, 1));
        mSitePhotosImageAdapter = new ImageAdapter(mSitePhotosImageBeans, this);
        mGridSitePhotos.setAdapter(mSitePhotosImageAdapter);
        mSitePhotosImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mSitePhotosImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mSitePhotosImageBeans.remove(position);
                mGridSitePhotos.setAdapter(mSitePhotosImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSitePhotosImageBeans.size() == 7) {
                    Toast.makeText(WasteDisposalActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(WasteDisposalActivity.this);
                }

            }
        });

        //废弃物处理情况总结
        mSummaryTreatmentImageBeans.add(new ImageBean(null, null, 1));
        mSummaryTreatmentImageAdapter = new ImageAdapter(mSummaryTreatmentImageBeans, this);
        mGridSummaryTreatment.setAdapter(mSummaryTreatmentImageAdapter);
        mSummaryTreatmentImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mSummaryTreatmentImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mSummaryTreatmentImageBeans.remove(position);
                mGridSummaryTreatment.setAdapter(mSummaryTreatmentImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSummaryTreatmentImageBeans.size() == 7) {
                    Toast.makeText(WasteDisposalActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 3;
                    ChooseImageDialog.getInstance().show(WasteDisposalActivity.this);
                }

            }
        });


        //废弃物处理验收意见
        mTreatmentImageBeans.add(new ImageBean(null, null, 1));
        mTreatmentImageAdapter = new ImageAdapter(mTreatmentImageBeans, this);
        mGridTreatment.setAdapter(mTreatmentImageAdapter);
        mTreatmentImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mTreatmentImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mTreatmentImageBeans.remove(position);
                mGridTreatment.setAdapter(mTreatmentImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mTreatmentImageBeans.size() == 7) {
                    Toast.makeText(WasteDisposalActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 4;
                    ChooseImageDialog.getInstance().show(WasteDisposalActivity.this);
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
                        LocalDataUtil.getIntance(WasteDisposalActivity.this).deletePlanInfo(key);
                        WasteDisposalActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    WasteDisposalActivity.this.finish();
                                } else {
                                    Toast.makeText(WasteDisposalActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(WasteDisposalActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                WasteDisposalActivity.this.finish();
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
                mTreatmentSchemeImageBeans.clear();
                mTreatmentSchemeImageBeans.addAll(imageBeans);
                for (ImageBean image : mTreatmentSchemeImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mTreatmentSchemeImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos2())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos2(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mPersonLiableImageBeans.clear();
                mPersonLiableImageBeans.addAll(imageBeans);
                for (ImageBean image : mPersonLiableImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mPersonLiableImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos3())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos3(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mSitePhotosImageBeans.clear();
                mSitePhotosImageBeans.addAll(imageBeans);
                for (ImageBean image : mSitePhotosImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mSitePhotosImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos4())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos4(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mSummaryTreatmentImageBeans.clear();
                mSummaryTreatmentImageBeans.addAll(imageBeans);
                for (ImageBean image : mSummaryTreatmentImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mSummaryTreatmentImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos5())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos5(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mTreatmentImageBeans.clear();
                mTreatmentImageBeans.addAll(imageBeans);
                for (ImageBean image : mTreatmentImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mTreatmentImageAdapter.notifyDataSetChanged();
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
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mTreatmentSchemeImageBeans.add(mTreatmentSchemeImageBeans.size() - 1, imageBean);
                            }
                            mTreatmentSchemeImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mPersonLiableImageBeans.add(mPersonLiableImageBeans.size() - 1, imageBean);
                            }
                            mPersonLiableImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos3()) && mPlanBean.getFiles3() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles3()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mSitePhotosImageBeans.add(mSitePhotosImageBeans.size() - 1, imageBean);
                            }
                            mSitePhotosImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos4()) && mPlanBean.getFiles4() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles4()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mSummaryTreatmentImageBeans.add(mSummaryTreatmentImageBeans.size() - 1, imageBean);
                            }
                            mSummaryTreatmentImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos5()) && mPlanBean.getFiles5() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles5()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mTreatmentImageBeans.add(mTreatmentImageBeans.size() - 1, imageBean);
                            }
                            mTreatmentImageAdapter.notifyDataSetChanged();
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
        planBean.setTaskType("废物处理");
        planBean.setWellName(wellName.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        if (mTreatmentSchemeImageBeans != null) {
            for (ImageBean image : mTreatmentSchemeImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos(new Gson().toJson(mTreatmentSchemeImageBeans));
        }
        if (mPersonLiableImageBeans != null) {
            for (ImageBean image : mPersonLiableImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos2(new Gson().toJson(mPersonLiableImageBeans));
        }
        if (mSitePhotosImageBeans != null) {
            for (ImageBean image : mSitePhotosImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos3(new Gson().toJson(mSitePhotosImageBeans));
        }
        if (mSummaryTreatmentImageBeans != null) {
            for (ImageBean image : mSummaryTreatmentImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos4(new Gson().toJson(mSummaryTreatmentImageBeans));
        }
        if (mTreatmentImageBeans != null) {
            for (ImageBean image : mTreatmentImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos5(new Gson().toJson(mTreatmentImageBeans));
        }
        if (key != -1) {
            planBean.setKey(key);
            LocalDataUtil.getIntance(this).updatePlanInfo(planBean);

        } else {
            LocalDataUtil.getIntance(this).addLocalPlanInfo(planBean);
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

        if (mTreatmentSchemeImageBeans != null && mTreatmentSchemeImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "废物处理", "p1", mTreatmentSchemeImageBeans, photoFiles);
        }
        if (mPersonLiableImageBeans != null && mPersonLiableImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "废物处理", "p2", mPersonLiableImageBeans, photoFiles2);
        }
        if (mSitePhotosImageBeans != null && mSitePhotosImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "废物处理", "p3", mSitePhotosImageBeans, photoFiles3);
        }
        if (mSummaryTreatmentImageBeans != null && mSummaryTreatmentImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles4 = new ArrayList<>();
            if (planBean != null) {
                photoFiles4 = planBean.getFiles4();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "废物处理", "p4", mSummaryTreatmentImageBeans, photoFiles4);
        }

        if (mTreatmentImageBeans != null && mTreatmentImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles5 = new ArrayList<>();
            if (planBean != null) {
                photoFiles5 = planBean.getFiles5();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "废物处理", "p5", mTreatmentImageBeans, photoFiles5);
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
                mTreatmentSchemeImageBeans.add(mTreatmentSchemeImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridTreatmentScheme.setAdapter(mTreatmentSchemeImageAdapter);
            } else if (mChooseImageType == 1) {
                mPersonLiableImageBeans.add(mPersonLiableImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridPersonLiable.setAdapter(mPersonLiableImageAdapter);
            } else if (mChooseImageType == 2) {
                mSitePhotosImageBeans.add(mSitePhotosImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridSitePhotos.setAdapter(mSitePhotosImageAdapter);
            } else if (mChooseImageType == 3) {
                mSummaryTreatmentImageBeans.add(mSummaryTreatmentImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridSummaryTreatment.setAdapter(mSummaryTreatmentImageAdapter);
            }else if (mChooseImageType == 4) {
                mTreatmentImageBeans.add(mTreatmentImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridTreatment.setAdapter(mTreatmentImageAdapter);
            }
        }


    }

}