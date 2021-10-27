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
import com.AoRGMapT.bean.SiteConstructionWellDrillingBean;
import com.AoRGMapT.bean.WellLocationDeterminationBean;
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

/**
 * 钻井
 */
public class SiteConstructionWellDrillingActivity extends AppCompatActivity {

    private final String TAG = "SiteConstructionWellDrillingActivity";

    //记录时间
    private EditText mEditTime;

    //早会记录
    private GridView mGridMorningMeeting;
    private ImageAdapter mMorningMeetingImageAdapter;
    List<ImageBean> mMorningMeetingImageBeans = new ArrayList<>();

    //大小班
    private GridView mGridClass;
    private ImageAdapter mClassImageAdapter;
    List<ImageBean> mClassImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();


    //0早会记录 1大小班 2 现场照片
    private int mChooseImageType = 0;

    //当前项目的id
    private String id;

    private TextView project_name;
    private EditText well_name;
    private EditText construction_days;
    private EditText well_depth;
    private EditText daily_footage;
    private EditText stratum;
    private EditText construction_unit;
    private EditText recorder;
    private EditText ed_time;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_construction);

        id = getIntent().getStringExtra("id");

        mEditTime = findViewById(R.id.ed_time);
        mGridMorningMeeting = findViewById(R.id.grid_morning_meeting);
        mGridClass = findViewById(R.id.grid_classes);
        mGridScene = findViewById(R.id.grid_scene);
        project_name = findViewById(R.id.project_name);
        well_name = findViewById(R.id.well_name);
        construction_days = findViewById(R.id.construction_days);
        well_depth = findViewById(R.id.well_depth);
        daily_footage = findViewById(R.id.daily_footage);
        stratum = findViewById(R.id.stratum);
        construction_unit = findViewById(R.id.construction_unit);
        recorder = findViewById(R.id.recorder);
        ed_time = findViewById(R.id.ed_time);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SiteConstructionWellDrillingActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> map = new HashMap<>();
                map.put("event_id", BaseApplication.currentProject.getId());
                map.put("task_type", "钻井施工");
                map.put("well_name", well_name.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("record_data", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                SiteConstructionWellDrillingBean extend_data = new SiteConstructionWellDrillingBean();
                extend_data.setConstruction_days(construction_days.getText().toString());
                extend_data.setConstruction_unit(construction_unit.getText().toString());
                extend_data.setDaily_footage(daily_footage.getText().toString());
                extend_data.setWell_depth(well_depth.getText().toString());
                extend_data.setStratum(stratum.getText().toString());
                map.put("extend_data", new Gson().toJson(extend_data));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            SiteConstructionWellDrillingActivity.this.finish();

                        } else {
                            Toast.makeText(SiteConstructionWellDrillingActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(SiteConstructionWellDrillingActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
                mMorningMeetingImageBeans.remove(position);
                mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mMorningMeetingImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionWellDrillingActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(SiteConstructionWellDrillingActivity.this);
                }

            }
        });

        //整改情况说明
        mClassImageBeans.add(new ImageBean(null, null, 1));
        mClassImageAdapter = new ImageAdapter(mClassImageBeans, this);
        mGridClass.setAdapter(mClassImageAdapter);
        mClassImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mClassImageBeans.remove(position);
                mGridClass.setAdapter(mClassImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mClassImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionWellDrillingActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(SiteConstructionWellDrillingActivity.this);
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
                mSceneImageBeans.remove(position);
                mGridScene.setAdapter(mSceneImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSceneImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionWellDrillingActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(SiteConstructionWellDrillingActivity.this);
                }

            }
        });

        //设置项目名称和井号
        if (BaseApplication.currentProject != null) {
            project_name.setText(BaseApplication.currentProject.getProjectName());
            well_name.setText(BaseApplication.currentProject.getDefaultWellName());
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
                                SiteConstructionWellDrillingActivity.this.finish();
                            } else {
                                Toast.makeText(SiteConstructionWellDrillingActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(SiteConstructionWellDrillingActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                            SiteConstructionWellDrillingBean bean = new Gson().fromJson(planBean.getExtendData(), SiteConstructionWellDrillingBean.class);
                            if (bean != null) {
                                construction_days.setText(bean.getConstruction_days());
                                construction_unit.setText(bean.getConstruction_unit());
                                well_depth.setText(bean.getWell_depth());
                                stratum.setText(bean.getStratum());
                                daily_footage.setText(bean.getDaily_footage());
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
                        mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
                    } else if (mChooseImageType == 1) {
                        mClassImageBeans.add(mClassImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridClass.setAdapter(mClassImageAdapter);
                    } else if (mChooseImageType == 2) {
                        mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridScene.setAdapter(mSceneImageAdapter);
                    }
                }
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                if (mChooseImageType == 0) {
                    mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
                } else if (mChooseImageType == 1) {
                    mClassImageBeans.add(mClassImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridClass.setAdapter(mClassImageAdapter);
                } else if (mChooseImageType == 2) {
                    mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridScene.setAdapter(mSceneImageAdapter);
                }
            }
        }

    }

}