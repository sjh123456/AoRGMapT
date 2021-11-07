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
 * 开工验收
 */
public class CommencementAcceptanceActivity extends AppCompatActivity {

    private final String TAG = "CommencementAcceptanceActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //记录时间
    private EditText mEditTime;

    //验收意见与专家名单
    private GridView mGridCheck;
    private ImageAdapter mCheckImageAdapter;
    List<ImageBean> mCheckImageBeans = new ArrayList<>();

    //环评记录
    private GridView mGridEia;
    private ImageAdapter mEiaImageAdapter;
    List<ImageBean> mEiaImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();

    //成果验收记录
    private GridView mGridRectification;
    private ImageAdapter mRectificationImageAdapter;
    List<ImageBean> mRectificationImageBeans = new ArrayList<>();

    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0验收意见与专家名单 1环评记录 2 现场照片 3成果验收记录
    private int mChooseImageType = 0;


    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private EditText wellName;
    private TextView project_name;
    private EditText recorder;
    private TextView remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        setContentView(R.layout.activity_commencement_acceptance);
        mEditTime = findViewById(R.id.ed_time);
        mGridCheck = findViewById(R.id.grid_check);
        mGridEia = findViewById(R.id.grid_eia);
        mGridScene = findViewById(R.id.grid_scene);
        mGridRectification = findViewById(R.id.grid_rectification);
        tv_local_save = findViewById(R.id.tv_local_save);
        wellName = findViewById(R.id.wellName);
        project_name = findViewById(R.id.project_name);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommencementAcceptanceActivity.this.finish();
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
                map.put("taskType", "开工验收");
                map.put("wellName", wellName.getText().toString());
                map.put("recorder", recorder.getText().toString());
                //map.put("recordDate", mEditTime.getText().toString());
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
                                LocalDataUtil.getIntance(CommencementAcceptanceActivity.this).deletePlanInfo(key);
                            }
                            CommencementAcceptanceActivity.this.finish();

                        } else {
                            Toast.makeText(CommencementAcceptanceActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(CommencementAcceptanceActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        setCurrentTime();

        //验收意见与专家名单
        mCheckImageBeans.add(new ImageBean(null, null, 1));
        mCheckImageAdapter = new ImageAdapter(mCheckImageBeans, this);
        mGridCheck.setAdapter(mCheckImageAdapter);
        mCheckImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mCheckImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mCheckImageBeans.remove(position);
                mGridCheck.setAdapter(mCheckImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mCheckImageBeans.size() == 7) {
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
                }

            }
        });

        //整改情况说明
        mEiaImageBeans.add(new ImageBean(null, null, 1));
        mEiaImageAdapter = new ImageAdapter(mEiaImageBeans, this);
        mGridEia.setAdapter(mEiaImageAdapter);
        mEiaImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mEiaImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mEiaImageBeans.remove(position);
                mGridEia.setAdapter(mEiaImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mEiaImageBeans.size() == 7) {
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
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
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
                }

            }
        });
        //成果验收记录
        mRectificationImageBeans.add(new ImageBean(null, null, 1));
        mRectificationImageAdapter = new ImageAdapter(mRectificationImageBeans, this);
        mGridRectification.setAdapter(mRectificationImageAdapter);
        mRectificationImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mRectificationImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mRectificationImageBeans.remove(position);
                mGridRectification.setAdapter(mRectificationImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mRectificationImageBeans.size() == 7) {
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 3;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
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
                        LocalDataUtil.getIntance(CommencementAcceptanceActivity.this).deletePlanInfo(key);
                        CommencementAcceptanceActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {

                                if (o.isSuccess()) {
                                    CommencementAcceptanceActivity.this.finish();
                                } else {
                                    Toast.makeText(CommencementAcceptanceActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(CommencementAcceptanceActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                CommencementAcceptanceActivity.this.finish();
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
                mCheckImageBeans.clear();
                mCheckImageBeans.addAll(imageBeans);
                for (ImageBean image : mCheckImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mCheckImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos2())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos2(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mEiaImageBeans.clear();
                mEiaImageBeans.addAll(imageBeans);
                for (ImageBean image : mEiaImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mEiaImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos3())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos3(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mSceneImageBeans.clear();
                mSceneImageBeans.addAll(imageBeans);
                for (ImageBean image : mSceneImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mSceneImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos4())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos4(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mRectificationImageBeans.clear();
                mRectificationImageBeans.addAll(imageBeans);
                for (ImageBean image : mRectificationImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mRectificationImageAdapter.notifyDataSetChanged();
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
                        showPlanInfo();
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mCheckImageBeans.add(mCheckImageBeans.size() - 1, imageBean);
                            }
                            mCheckImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mEiaImageBeans.add(mEiaImageBeans.size() - 1, imageBean);
                            }
                            mEiaImageAdapter.notifyDataSetChanged();
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
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos4()) && mPlanBean.getFiles4() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles4()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, imageBean);
                            }
                            mRectificationImageAdapter.notifyDataSetChanged();
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
        }
    }

    //将信息保存在本地
    private void saveLocal() {
        if (BaseApplication.currentProject == null) {
            return;
        }
        PlanBean planBean = new PlanBean();
        planBean.setProjectId(BaseApplication.currentProject.getId());
        planBean.setTaskType("开工验收");
        planBean.setWellName(wellName.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        if (mCheckImageBeans != null) {
            for (ImageBean image : mCheckImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos(new Gson().toJson(mCheckImageBeans));
        }
        if (mEiaImageBeans != null) {
            for (ImageBean image : mEiaImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos2(new Gson().toJson(mEiaImageBeans));
        }
        if (mSceneImageBeans != null) {
            for (ImageBean image : mSceneImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos3(new Gson().toJson(mSceneImageBeans));
        }
        if (mRectificationImageBeans != null) {
            for (ImageBean image : mRectificationImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos4(new Gson().toJson(mRectificationImageBeans));
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
                mCheckImageBeans.add(mCheckImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridCheck.setAdapter(mCheckImageAdapter);
            } else if (mChooseImageType == 1) {
                mEiaImageBeans.add(mEiaImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridEia.setAdapter(mEiaImageAdapter);
            } else if (mChooseImageType == 2) {
                mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridScene.setAdapter(mSceneImageAdapter);
            } else if (mChooseImageType == 3) {
                mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridRectification.setAdapter(mRectificationImageAdapter);
            }
        }
    }

    //新增图片
    private void addPhotos(String taskid, PlanBean planBean) {


        if (mCheckImageBeans != null && mCheckImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "开工验收", "p1", mCheckImageBeans, photoFiles);
        }
        if (mEiaImageBeans != null && mEiaImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "开工验收", "p2", mEiaImageBeans, photoFiles2);
        }
        if (mSceneImageBeans != null && mSceneImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "开工验收", "p3", mSceneImageBeans, photoFiles3);
        }
        if (mRectificationImageBeans != null && mCheckImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles4 = new ArrayList<>();
            if (planBean != null) {
                photoFiles4 = planBean.getFiles4();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "开工验收", "p4", mRectificationImageBeans, photoFiles4);
        }

    }

}