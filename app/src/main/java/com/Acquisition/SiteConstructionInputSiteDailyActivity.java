package com.Acquisition;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 录井施工
 */
public class SiteConstructionInputSiteDailyActivity extends AppCompatActivity {

    private final static String TAG = "SiteConstructionInputActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //记录时间
    private EditText mEditTime;

    //早会记录
    private GridView mGridMorningMeeting;
    private ImageAdapter mMorningMeetingImageAdapter;
    List<ImageBean> mMorningMeetingImageBeans = new ArrayList<>();

    //录井
    private GridView mGridLogging;
    private ImageAdapter mLogginfImageAdapter;
    List<ImageBean> mLoggingImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();


    //要删除的图片列表
    private List<String> deleteImageList = new ArrayList<>();
    //当前的项目
    private PlanBean mPlanBean;

    //0早会记录 1录井 2 现场照片
    private int mChooseImageType = 0;

    private TextView project_name;
    private EditText wellName;
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
        setContentView(R.layout.activity_site_construction_input_site_daily);

        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);

        mEditTime = findViewById(R.id.ed_time);
        mGridMorningMeeting = findViewById(R.id.grid_morning_meeting);
        mGridLogging = findViewById(R.id.grid_logging);
        mGridScene = findViewById(R.id.grid_scene);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SiteConstructionInputSiteDailyActivity.this.finish();
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
                map.put("taskType", "录井施工-现场日报");
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
                                LocalDataUtil.getIntance(SiteConstructionInputSiteDailyActivity.this).deletePlanInfo(key);
                            }
                            SiteConstructionInputSiteDailyActivity.this.finish();

                        } else {
                            Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(SiteConstructionInputSiteDailyActivity.this);
                }

            }
        });

        //整改情况说明
        mLoggingImageBeans.add(new ImageBean(null, null, 1));
        mLogginfImageAdapter = new ImageAdapter(mLoggingImageBeans, this);
        mGridLogging.setAdapter(mLogginfImageAdapter);
        mLogginfImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                ImageBean imageBean = mLoggingImageBeans.get(position);
                deleteImageList.add(imageBean.getId());
                mLoggingImageBeans.remove(position);
                mGridLogging.setAdapter(mLogginfImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mLoggingImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(SiteConstructionInputSiteDailyActivity.this);
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
                    Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(SiteConstructionInputSiteDailyActivity.this);
                }

            }
        });

        //设置项目名称和井号
        if (BaseApplication.currentProject != null) {
            project_name.setText(BaseApplication.currentProject.getProjectName());
            wellName.setText(BaseApplication.currentProject.getDefaultWellName());
            recorder.setText(BaseApplication.userInfo.getUserName());
        }

        if (!TextUtils.isEmpty(id)|| key != -1) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (key != -1) {
                        LocalDataUtil.getIntance(SiteConstructionInputSiteDailyActivity.this).deletePlanInfo(key);
                        SiteConstructionInputSiteDailyActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    SiteConstructionInputSiteDailyActivity.this.finish();
                                } else {
                                    Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(SiteConstructionInputSiteDailyActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                SiteConstructionInputSiteDailyActivity.this.finish();
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
                mMorningMeetingImageBeans.clear();
                mMorningMeetingImageBeans.addAll(imageBeans);
                for (ImageBean image : mMorningMeetingImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mMorningMeetingImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(planBean.getSitePhotos2())) {
                List<ImageBean> imageBeans = new Gson().fromJson(mPlanBean.getSitePhotos2(), new TypeToken<List<ImageBean>>() {
                }.getType());
                mLoggingImageBeans.clear();
                mLoggingImageBeans.addAll(imageBeans);
                for (ImageBean image : mLoggingImageBeans) {
                    image.setBitmap(BitmapFactory.decodeFile(image.getImagePath()));
                }
                mLogginfImageAdapter.notifyDataSetChanged();
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
                                    mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, imageBean);
                                }
                                mMorningMeetingImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles2() != null) {
                                for (PlanBean.PhotoFile photo : mPlanBean.getFiles2()) {
                                    ImageBean imageBean = new ImageBean(null, null, 0);
                                    imageBean.setImageUrl(EncapsulationImageUrl.encapsulation(photo.getId()));
                                    imageBean.setId(photo.getId());
                                    mLoggingImageBeans.add(mLoggingImageBeans.size() - 1, imageBean);
                                }
                                mLogginfImageAdapter.notifyDataSetChanged();
                            }
                            if (!TextUtils.isEmpty(mPlanBean.getSitePhotos()) && mPlanBean.getFiles3() != null) {
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
        planBean.setTaskType("录井施工-现场日报");
        planBean.setWellName(wellName.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        if (mMorningMeetingImageBeans != null) {
            for (ImageBean image : mMorningMeetingImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos(new Gson().toJson(mMorningMeetingImageBeans));
        }
        if (mLoggingImageBeans != null) {
            for (ImageBean image : mLoggingImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos2(new Gson().toJson(mLoggingImageBeans));
        }
        if (mSceneImageBeans != null) {
            for (ImageBean image : mSceneImageBeans) {
                image.setBitmap(null);
            }
            planBean.setSitePhotos3(new Gson().toJson(mSceneImageBeans));
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

        if (mMorningMeetingImageBeans != null && mMorningMeetingImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles = new ArrayList<>();
            if (planBean != null) {
                photoFiles = planBean.getFiles();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "录井施工", "p1", mMorningMeetingImageBeans, photoFiles);
        }
        if (mLoggingImageBeans != null && mLoggingImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles2 = new ArrayList<>();
            if (planBean != null) {
                photoFiles2 = planBean.getFiles2();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "录井施工", "p2", mLoggingImageBeans, photoFiles2);
        }
        if (mSceneImageBeans != null && mSceneImageBeans.size() > 0) {
            List<PlanBean.PhotoFile> photoFiles3 = new ArrayList<>();
            if (planBean != null) {
                photoFiles3 = planBean.getFiles3();
            }
            EncapsulationImageUrl.updatePhotos(taskid, "录井施工", "p3", mSceneImageBeans, photoFiles3);
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
                mLoggingImageBeans.add(mLoggingImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridLogging.setAdapter(mLogginfImageAdapter);
            } else if (mChooseImageType == 2) {
                mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(picturePath, BitmapFactory.decodeFile(picturePath), 0));
                mGridScene.setAdapter(mSceneImageAdapter);
            }
        }


    }

}