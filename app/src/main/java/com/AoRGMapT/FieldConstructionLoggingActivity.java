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
import com.AoRGMapT.bean.FieldConstructionLoggingBean;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
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
 * 测井
 */
public class FieldConstructionLoggingActivity extends AppCompatActivity {

    private final String TAG = "FieldConstructionLoggingActivity";

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


    //0早会记录 1分段测井曲线 2 现场照片 
    private int mChooseImageType = 0;

    //当前项目的id
    private String id;

    private TextView project_name;
    private EditText well_name;
    private EditText horizon;
    private EditText top_boundary_depth;
    private EditText bottom_boundary_depth;
    private EditText thickness;
    private EditText interpretation_conclusion;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_construction_logging);
        id = getIntent().getStringExtra("id");

        mEditTime = findViewById(R.id.ed_time);
        mGridMorningMeeting = findViewById(R.id.grid_morning_meeting);
        mGridSubsection = findViewById(R.id.grid_subsection);
        mGridScene = findViewById(R.id.grid_scene);
        project_name = findViewById(R.id.project_name);
        well_name = findViewById(R.id.well_name);
        horizon = findViewById(R.id.horizon);
        top_boundary_depth = findViewById(R.id.top_boundary_depth);
        bottom_boundary_depth = findViewById(R.id.bottom_boundary_depth);
        thickness = findViewById(R.id.thickness);
        interpretation_conclusion = findViewById(R.id.interpretation_conclusion);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldConstructionLoggingActivity.this.finish();
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
                FieldConstructionLoggingBean extend_data = new FieldConstructionLoggingBean();
                extend_data.setHorizon(horizon.getText().toString());
                extend_data.setThickness(thickness.getText().toString());
                extend_data.setBottom_boundary_depth(bottom_boundary_depth.getText().toString());
                extend_data.setInterpretation_conclusion(interpretation_conclusion.getText().toString());
                extend_data.setTop_boundary_depth(top_boundary_depth.getText().toString());

                map.put("extend_data", new Gson().toJson(extend_data));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            FieldConstructionLoggingActivity.this.finish();

                        } else {
                            Toast.makeText(FieldConstructionLoggingActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(FieldConstructionLoggingActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(FieldConstructionLoggingActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(FieldConstructionLoggingActivity.this);
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
                mSubsectionImageBeans.remove(position);
                mGridSubsection.setAdapter(mSubsectionImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSubsectionImageBeans.size() == 7) {
                    Toast.makeText(FieldConstructionLoggingActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(FieldConstructionLoggingActivity.this);
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
                    Toast.makeText(FieldConstructionLoggingActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(FieldConstructionLoggingActivity.this);
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
                                FieldConstructionLoggingActivity.this.finish();
                            } else {
                                Toast.makeText(FieldConstructionLoggingActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void fail(String code, String message) {
                            Toast.makeText(FieldConstructionLoggingActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                            FieldConstructionLoggingBean bean = new Gson().fromJson(planBean.getExtendData(), FieldConstructionLoggingBean.class);
                            if (bean != null) {
                                horizon.setText(bean.getHorizon());
                                bottom_boundary_depth.setText(bean.getBottom_boundary_depth());
                                thickness.setText(bean.getThickness());
                                interpretation_conclusion.setText(bean.getInterpretation_conclusion());
                                top_boundary_depth.setText(bean.getTop_boundary_depth());
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
                        mSubsectionImageBeans.add(mSubsectionImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridSubsection.setAdapter(mSubsectionImageAdapter);
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
                    mSubsectionImageBeans.add(mSubsectionImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridSubsection.setAdapter(mSubsectionImageAdapter);
                } else if (mChooseImageType == 2) {
                    mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridScene.setAdapter(mSceneImageAdapter);
                }
            }
        }

    }

}