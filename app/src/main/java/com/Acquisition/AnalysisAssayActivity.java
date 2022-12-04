package com.Acquisition;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.bean.AnalysisAssayBean;
import com.Acquisition.bean.PlanBean;
import com.Acquisition.bean.ResponseDataItem;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.RequestUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnalysisAssayActivity extends AppCompatActivity {
    public final static String TAG = "AnalysisAssayActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] sampleTypeArray = {"岩石", "泥浆", "原油", "天然气", "非烃气", "地层水", "其他"};
    private String[] sampleSourceCategoryArray = {"井下", "浅地表", "槽", "浅井头", "坑", "探", "地表露"};
    private String[] coringMethodArray = {"岩屑", "井壁取心", "岩心"};
    //记录时间
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private int sampleTypeIndex = 0;
    private int sampleSourceCategoryIndex = 0;
    private int coringMethodIndex = 0;


    private TextView project_name;

    private EditText wellName;
    private Spinner sample_type;
    private EditText horizon;
    private EditText rock_name;
    private EditText analysis_test;
    private Spinner sample_source_category;
    private EditText sample_field_number;
    private EditText well_section;
    private Spinner coring_method;
    private EditText topic_name;
    private EditText topic_number;
    private EditText analysis_test_unit;
    private EditText analysis_test_date;
    private EditText sample_sender;

    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_assay);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);
        sample_type = findViewById(R.id.sample_type);
        horizon = findViewById(R.id.horizon);
        rock_name = findViewById(R.id.rock_name);
        analysis_test = findViewById(R.id.analysis_test);
        sample_source_category = findViewById(R.id.sample_source_category);
        sample_field_number = findViewById(R.id.sample_field_number);
        well_section = findViewById(R.id.well_section);
        coring_method = findViewById(R.id.coring_method);
        sample_sender = findViewById(R.id.sample_sender);
        analysis_test_date = findViewById(R.id.analysis_test_date);
        analysis_test_unit = findViewById(R.id.analysis_test_unit);
        topic_name = findViewById(R.id.topic_name);
        topic_number = findViewById(R.id.topic_number);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalysisAssayActivity.this.finish();
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
                map.put("taskType", "分析化验");
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                map.put("wellName", wellName.getText().toString());
                map.put("extendData", new Gson().toJson(setData()));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            //上传成功之后，删除本地项目
                            if (key != -1) {
                                LocalDataUtil.getIntance(AnalysisAssayActivity.this).deletePlanInfo(key);
                            }
                            AnalysisAssayActivity.this.finish();

                        } else {
                            Toast.makeText(AnalysisAssayActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(AnalysisAssayActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
        setCurrentTime();
        initSpinner();
        //设置项目名称和井号
        if (BaseApplication.currentProject != null) {
            project_name.setText(BaseApplication.currentProject.getProjectName());
            recorder.setText(BaseApplication.userInfo.getUserName());
            wellName.setText(BaseApplication.currentProject.getDefaultWellName());
        }

        if (!TextUtils.isEmpty(id) || key != -1) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (key != -1) {
                        LocalDataUtil.getIntance(AnalysisAssayActivity.this).deletePlanInfo(key);
                        AnalysisAssayActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    AnalysisAssayActivity.this.finish();
                                } else {
                                    Toast.makeText(AnalysisAssayActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(AnalysisAssayActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                AnalysisAssayActivity.this.finish();
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

                @SuppressLint("LongLogTag")
                @Override
                public void fail(String code, String message) {
                    Log.e(TAG, "项目详情请求失败");
                }
            });
        }
    }

    private AnalysisAssayBean setData() {
        AnalysisAssayBean bean = new AnalysisAssayBean();
        bean.setWellName(wellName.getText().toString());
        bean.setSampleType(sampleTypeArray[sampleTypeIndex]);
        bean.setHorizon(horizon.getText().toString());
        bean.setRockName(rock_name.getText().toString());
        bean.setAnalysisTest(analysis_test.getText().toString());
        bean.setSampleSourceCategory(sampleSourceCategoryArray[sampleSourceCategoryIndex]);
        bean.setSampleFieldNumber(sample_field_number.getText().toString());
        bean.setWellSection(well_section.getText().toString());
        bean.setCoringMethod(coringMethodArray[coringMethodIndex]);
        bean.setTopicName(topic_name.getText().toString());
        bean.setTopicNumber(topic_number.getText().toString());
        bean.setAnalySistestUnit(analysis_test_unit.getText().toString());
        bean.setAnalySistestSate(analysis_test_date.getText().toString());
        bean.setSampleSender(sample_sender.getText().toString());

        return bean;
    }

    /**
     * 显示项目信息
     */
    @SuppressLint("LongLogTag")
    private void showPlanInfo() {
        if (mPlanBean != null) {
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
            AnalysisAssayBean bean = new Gson().fromJson(mPlanBean.getExtendData(), AnalysisAssayBean.class);
            if (bean != null) {
                wellName.setText(bean.getWellName());
                for (int i = 0; i < sampleTypeArray.length; i++) {
                    if (TextUtils.equals(sampleTypeArray[i], bean.getSampleType())) {
                        sample_type.setSelection(i);
                        break;
                    }
                }
                horizon.setText(bean.getHorizon());
                rock_name.setText(bean.getRockName());
                analysis_test.setText(bean.getAnalysisTest());
                for (int i = 0; i < sampleSourceCategoryArray.length; i++) {
                    if (TextUtils.equals(sampleSourceCategoryArray[i], bean.getSampleSourceCategory())) {
                        sample_source_category.setSelection(i);
                        break;
                    }
                }
                sample_field_number.setText(bean.getSampleFieldNumber());
                well_section.setText(bean.getWellSection());
                topic_name.setText(bean.getTopicName());
                topic_number.setText(bean.getTopicNumber());
                analysis_test_unit.setText(bean.getAnalySistestUnit());
                analysis_test_date.setText(bean.getAnalySistestSate());
                sample_sender.setText(bean.getSampleSender());
                for (int i = 0; i < coringMethodArray.length; i++) {
                    if (TextUtils.equals(coringMethodArray[i], bean.getCoringMethod())) {
                        coring_method.setSelection(i);
                        break;
                    }
                }
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
        planBean.setTaskType("分析化验");
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        planBean.setWellName(wellName.getText().toString());
        planBean.setExtendData(new Gson().toJson(setData()));
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

    private void initSpinner() {
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> sampleTypeAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, sampleTypeArray);
        //设置数组适配器的布局样式
        sampleTypeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //设置下拉框的数组适配器
        sample_type.setAdapter(sampleTypeAdapter);
        //设置下拉框默认的显示第一项
        sample_type.setSelection(sampleTypeIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sample_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sampleTypeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> sampleSourceCategoryAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, sampleSourceCategoryArray);
        //设置数组适配器的布局样式
        sampleSourceCategoryAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //设置下拉框的数组适配器
        sample_source_category.setAdapter(sampleSourceCategoryAdapter);
        //设置下拉框默认的显示第一项
        sample_source_category.setSelection(sampleSourceCategoryIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sample_source_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sampleSourceCategoryIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> coringMethodAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, coringMethodArray);
        //设置数组适配器的布局样式
        coringMethodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //设置下拉框的数组适配器
        coring_method.setAdapter(coringMethodAdapter);
        //设置下拉框默认的显示第一项
        coring_method.setSelection(coringMethodIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        coring_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                coringMethodIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}