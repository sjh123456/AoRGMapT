package com.AoRGMapT;

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

import androidx.appcompat.app.AppCompatActivity;

import com.AoRGMapT.adapter.ImageAdapter;
import com.AoRGMapT.bean.FieldConstructionLoggingBean;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.util.ChooseImageDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.EncapsulationImageUrl;
import com.AoRGMapT.util.RequestUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测井施工
 */
public class FieldConstructionLoggingSiteDailyActivity extends AppCompatActivity {

    private final String TAG = "FieldConstructionLoggingActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //记录时间
    private EditText mEditTime;

    //早会记录
    private GridView mGridMorningMeeting;
    private ImageAdapter mMorningMeetingImageAdapter;
    List<ImageBean> mMorningMeetingImageBeans = new ArrayList<>();

    //分段测井曲线
    private GridView mGridSubsection;
    private ImageAdapter mSubsectionImageAdapter;
    List<ImageBean> mSubsectionImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();
    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0早会记录 1分段测井曲线 2 现场照片 
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
        setContentView(R.layout.activity_field_construction_logging_site_daily);
        id = getIntent().getStringExtra("id");

        mEditTime = findViewById(R.id.ed_time);
        mGridMorningMeeting = findViewById(R.id.grid_morning_meeting);
        mGridSubsection = findViewById(R.id.grid_subsection);
        mGridScene = findViewById(R.id.grid_scene);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldConstructionLoggingSiteDailyActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("projectId", BaseApplication.currentProject.getId());
                map.put("taskType", "测井施工-现场日报");
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
                            FieldConstructionLoggingSiteDailyActivity.this.finish();

                        } else {
                            Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        setCurrentTime();

        //验收意见与专家名单
        mMorningMeetingImageBeans.add(new ImageBean(null, null, 1));
        mMorningMeetingImageAdapter = new ImageAdapter(mMorningMeetingImageBeans, this);
        mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
        mMorningMeetingImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mMorningMeetingImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mMorningMeetingImageBeans.remove(position);
                mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mMorningMeetingImageBeans.size() == 7) {
                    Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(FieldConstructionLoggingSiteDailyActivity.this);
                }

            }
        });

        //整改情况说明
        mSubsectionImageBeans.add(new ImageBean(null, null, 1));
        mSubsectionImageAdapter = new ImageAdapter(mSubsectionImageBeans, this);
        mGridSubsection.setAdapter(mSubsectionImageAdapter);
        mSubsectionImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mSubsectionImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mSubsectionImageBeans.remove(position);
                mGridSubsection.setAdapter(mSubsectionImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSubsectionImageBeans.size() == 7) {
                    Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(FieldConstructionLoggingSiteDailyActivity.this);
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
                    Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(FieldConstructionLoggingSiteDailyActivity.this);
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
                                FieldConstructionLoggingSiteDailyActivity.this.finish();
                            } else {
                                Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(FieldConstructionLoggingSiteDailyActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                            String time = mPlanBean.getCreateTime();
                            try {
                                Date date = simpleDateFormat.parse(mPlanBean.getCreateTime());
                                time = simpleDateFormat.format(date);
                            } catch (Exception ex) {
                                Log.e(TAG, "");
                            }
                            mEditTime.setText(time);
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, imageBean);
                                }
                                mMorningMeetingImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos2()) && mPlanBean.getFiles2() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mSubsectionImageBeans.add(mSubsectionImageBeans.size() - 1, imageBean);
                                }
                                mSubsectionImageAdapter.notifyDataSetChanged();
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
       // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        mEditTime.setText(simpleDateFormat.format(date));
    }

    //新增图片
    private void addPhotos(String taskid, PlanBean planBean) {

        if (mMorningMeetingImageBeans != null && mMorningMeetingImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "测井施工", "p1", mMorningMeetingImageBeans, photoFiles);
        }
        if (mSubsectionImageBeans != null && mSubsectionImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "测井施工", "p2", mSubsectionImageBeans, photoFiles2);
        }
        if (mSceneImageBeans != null && mSceneImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "测井施工", "p3", mSceneImageBeans, photoFiles3);
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
                mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
            } else if (mChooseImageType == 1) {
                mSubsectionImageBeans.add(mSubsectionImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridSubsection.setAdapter(mSubsectionImageAdapter);
            } else if (mChooseImageType == 2) {
                mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridScene.setAdapter(mSceneImageAdapter);
            }


        }

    }

}