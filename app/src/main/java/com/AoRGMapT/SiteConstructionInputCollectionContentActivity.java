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

    //当前项目的id
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_construction_input_collection_content);

        id = getIntent().getStringExtra("id");

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

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SiteConstructionInputCollectionContentActivity.this.finish();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        if (!TextUtils.isEmpty(id)) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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


}