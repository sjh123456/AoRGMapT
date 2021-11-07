package com.AoRGMapT;

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

import com.AoRGMapT.adapter.ImageAdapter;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.bean.SiteConstructionInputBean;
import com.AoRGMapT.bean.WellLocationDeterminationBean;
import com.AoRGMapT.util.ChooseImageDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.EncapsulationImageUrl;
import com.AoRGMapT.util.LocalDataUtil;
import com.AoRGMapT.util.RequestUtil;
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
public class SiteConstructionInputCollectionContentActivity extends AppCompatActivity {

    private final static String TAG = "SiteConstructionInputActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //记录时间
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;

    //0早会记录 1录井 2 现场照片
    private int mChooseImageType = 0;

    private TextView project_name;
    private EditText wellName;
    private EditText horizon;
    private EditText top_boundary_depth;
    private EditText bottom_boundary_depth;
    private EditText thickness;
    private EditText display_level;
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
        setContentView(R.layout.activity_site_construction_input_collection_content);

        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);

        mEditTime = findViewById(R.id.ed_time);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        horizon = findViewById(R.id.horizon);
        top_boundary_depth = findViewById(R.id.top_boundary_depth);
        bottom_boundary_depth = findViewById(R.id.bottom_boundary_depth);
        display_level = findViewById(R.id.display_level);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        thickness = findViewById(R.id.thickness);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SiteConstructionInputCollectionContentActivity.this.finish();
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
                map.put("taskType", "录井施工-采集内容");
                map.put("wellName", wellName.getText().toString());
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                SiteConstructionInputBean extendData = new SiteConstructionInputBean();
                extendData.setDisplay_level(display_level.getText().toString());
                extendData.setHorizon(horizon.getText().toString());
                extendData.setBottom_boundary_depth(bottom_boundary_depth.getText().toString());
                extendData.setThickness(thickness.getText().toString());
                extendData.setTop_boundary_depth(top_boundary_depth.getText().toString());
                map.put("extendData", new Gson().toJson(extendData));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            //上传成功之后，删除本地项目
                            if (key != -1) {
                                LocalDataUtil.getIntance(SiteConstructionInputCollectionContentActivity.this).deletePlanInfo(key);
                            }
                            SiteConstructionInputCollectionContentActivity.this.finish();

                        } else {
                            Toast.makeText(SiteConstructionInputCollectionContentActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(SiteConstructionInputCollectionContentActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        setCurrentTime();

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
                        LocalDataUtil.getIntance(SiteConstructionInputCollectionContentActivity.this).deletePlanInfo(key);
                        SiteConstructionInputCollectionContentActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    SiteConstructionInputCollectionContentActivity.this.finish();
                                } else {
                                    Toast.makeText(SiteConstructionInputCollectionContentActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(SiteConstructionInputCollectionContentActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                SiteConstructionInputCollectionContentActivity.this.finish();
            }
        });

    }

    //获取展示本地信息
    private void getLocalInfo() {
        if (key != -1) {
            PlanBean planBean = LocalDataUtil.getIntance(this).queryLocalPlanInfoFromKey(key);
            mPlanBean = planBean;
            showPlanInfo();
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
            SiteConstructionInputBean determinationBean = new Gson().fromJson(mPlanBean.getExtendData(), SiteConstructionInputBean.class);
            if (determinationBean != null) {
                display_level.setText(determinationBean.getDisplay_level());
                horizon.setText(determinationBean.getHorizon());
                bottom_boundary_depth.setText(determinationBean.getBottom_boundary_depth());
                thickness.setText(determinationBean.getThickness());
                top_boundary_depth.setText(determinationBean.getTop_boundary_depth());
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
        planBean.setTaskType("录井施工-采集内容");
        planBean.setWellName(wellName.getText().toString());
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        SiteConstructionInputBean extendData = new SiteConstructionInputBean();
        extendData.setDisplay_level(display_level.getText().toString());
        extendData.setHorizon(horizon.getText().toString());
        extendData.setBottom_boundary_depth(bottom_boundary_depth.getText().toString());
        extendData.setThickness(thickness.getText().toString());
        extendData.setTop_boundary_depth(top_boundary_depth.getText().toString());
        planBean.setExtendData(new Gson().toJson(extendData));
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


}