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
 * 复耕复垦
 */
public class ReclamationActivity extends AppCompatActivity {

    private final static String TAG = "ReclamationActivity";

    //记录时间
    private EditText mEditTime;

    //复耕复垦方案
    private GridView mGridPogramme;
    private ImageAdapter mPogrammeImageAdapter;
    List<ImageBean> mPogrammeImageBeans = new ArrayList<>();

    //验收意见与专家名单
    private GridView mGridOpinion;
    private ImageAdapter mOpinionImageAdapter;
    List<ImageBean> mOpinionImageBeans = new ArrayList<>();

    //整改情况说明
    private GridView mGridRectification;
    private ImageAdapter mRectificationImageAdapter;
    List<ImageBean> mRectificationImageBeans = new ArrayList<>();

    //现场情况
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();

    //复耕复垦实施记录
    private GridView mGridImplementation;
    private ImageAdapter mImplementationAdapter;
    List<ImageBean> mImplementationBeans = new ArrayList<>();

    //复耕复垦转态及环评验收情况
    private GridView mGridState;
    private ImageAdapter mStateAdapter;
    List<ImageBean> mStateBeans = new ArrayList<>();

    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0复耕复垦方案 1验收意见与专家名单 2 整改情况说明  3环保保障措施 4复耕复垦实施记录 5复耕复垦转态及环评验收情况
    private int mChooseImageType = 0;

    //当前项目的id
    private String id;

    private TextView project_name;
    private EditText wellName;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamation);
        id = getIntent().getStringExtra("id");

        mEditTime = findViewById(R.id.ed_time);
        mGridPogramme = findViewById(R.id.grid_programme);
        mGridOpinion = findViewById(R.id.grid_opinion);
        mGridRectification = findViewById(R.id.grid_rectification);
        mGridScene = findViewById(R.id.grid_scene);
        mGridImplementation = findViewById(R.id.grid_implementations);
        mGridState = findViewById(R.id.grid_state);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReclamationActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("projectId", BaseApplication.currentProject.getId());
                map.put("taskType", "复耕复垦");
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

                            ReclamationActivity.this.finish();

                        } else {
                            Toast.makeText(ReclamationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(ReclamationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        //交通情况的图片
        mPogrammeImageBeans.add(new ImageBean(null, null, 1));
        mPogrammeImageAdapter = new ImageAdapter(mPogrammeImageBeans, this);
        mGridPogramme.setAdapter(mPogrammeImageAdapter);
        mPogrammeImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mPogrammeImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mPogrammeImageBeans.remove(position);
                mGridPogramme.setAdapter(mPogrammeImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mPogrammeImageBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //验收意见与专家名单
        mOpinionImageBeans.add(new ImageBean(null, null, 1));
        mOpinionImageAdapter = new ImageAdapter(mOpinionImageBeans, this);
        mGridOpinion.setAdapter(mOpinionImageAdapter);
        mOpinionImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mOpinionImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mOpinionImageBeans.remove(position);
                mGridOpinion.setAdapter(mOpinionImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mOpinionImageBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //整改情况说明
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
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //环保保障措施
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
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 3;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });
        //复耕复垦实施记录
        mImplementationBeans.add(new ImageBean(null, null, 1));
        mImplementationAdapter = new ImageAdapter(mImplementationBeans, this);
        mGridImplementation.setAdapter(mImplementationAdapter);
        mImplementationAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mImplementationBeans.remove(position);
                mGridImplementation.setAdapter(mImplementationAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mImplementationBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 4;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //青苗补偿情况
        mStateBeans.add(new ImageBean(null, null, 1));
        mStateAdapter = new ImageAdapter(mStateBeans, this);
        mGridState.setAdapter(mStateAdapter);
        mStateAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mStateBeans.remove(position);
                mGridState.setAdapter(mStateAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mStateBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 5;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        setCurrentTime();

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
                                ReclamationActivity.this.finish();
                            } else {
                                Toast.makeText(ReclamationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(ReclamationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                        }

                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mPogrammeImageBeans.add(mPogrammeImageBeans.size() - 1, imageBean);
                            }
                            mPogrammeImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mOpinionImageBeans.add(mOpinionImageBeans.size() - 1, imageBean);
                            }
                            mOpinionImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos3()) && mPlanBean.getFiles3() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, imageBean);
                            }
                            mRectificationImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos4()) && mPlanBean.getFiles4() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles3()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mSceneImageBeans.add(mSceneImageBeans.size() - 1, imageBean);
                            }
                            mSceneImageAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos5()) && mPlanBean.getFiles5() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles5()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mImplementationBeans.add(mImplementationBeans.size() - 1, imageBean);
                            }
                            mImplementationAdapter.notifyDataSetChanged();
                        }
                        if (!TextUtils.isEmpty(mPlanBean.getSitePhotos6()) && mPlanBean.getFiles6() != null) {
                            for (PlanBean.PhotoFile photo : mPlanBean.getFiles6()) {
                                ImageBean imageBean = new ImageBean(null, null, 0);
                                imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                imageBean.setId(photo.getId());
                                mStateBeans.add(mStateBeans.size() - 1, imageBean);
                            }
                            mStateAdapter.notifyDataSetChanged();
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

        if (mPogrammeImageBeans != null && mPogrammeImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "复耕复垦", "p1", mPogrammeImageBeans, photoFiles);
        }
        if (mOpinionImageBeans != null && mOpinionImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "复耕复垦", "p2", mOpinionImageBeans, photoFiles);
        }
        if (mRectificationImageBeans != null && mRectificationImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "复耕复垦", "p3", mRectificationImageBeans, photoFiles2);
        }
        if (mSceneImageBeans != null && mSceneImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles4();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "复耕复垦", "p4", mSceneImageBeans, photoFiles3);
        }
        if (mImplementationBeans != null && mImplementationBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles4 = new ArrayList<>();
            if (planBean != null) {
                photoFiles4 = planBean.getFiles5();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "复耕复垦", "p5", mImplementationBeans, photoFiles4);
        }
        if (mStateBeans != null && mStateBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles4 = new ArrayList<>();
            if (planBean != null) {
                photoFiles4 = planBean.getFiles6();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "复耕复垦", "p6", mStateBeans, photoFiles4);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = "";
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
                mPogrammeImageBeans.add(mPogrammeImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridPogramme.setAdapter(mPogrammeImageAdapter);
            } else if (mChooseImageType == 1) {
                mOpinionImageBeans.add(mOpinionImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridOpinion.setAdapter(mOpinionImageAdapter);
            } else if (mChooseImageType == 2) {
                mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridRectification.setAdapter(mRectificationImageAdapter);
            } else if (mChooseImageType == 3) {
                mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridScene.setAdapter(mSceneImageAdapter);
            } else if (mChooseImageType == 4) {
                mImplementationBeans.add(mImplementationBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridImplementation.setAdapter(mImplementationAdapter);
            } else if (mChooseImageType == 5) {
                mStateBeans.add(mStateBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridState.setAdapter(mStateAdapter);
            }
        }


    }
}