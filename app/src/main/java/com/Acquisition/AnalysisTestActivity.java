package com.Acquisition;

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

import androidx.appcompat.app.AppCompatActivity;

import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.bean.AnalysisAssayBean;
import com.Acquisition.bean.AnalysisTestBean;
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

/**
 * 分析测试
 */
public class AnalysisTestActivity extends AppCompatActivity {
    public final static String TAG = "AnalysisTestActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //记录时间
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;


    private TextView project_name;

    private EditText wellName;

    private EditText physicalProperty;
    private EditText rockOre;
    private EditText crudeOil;
    private EditText inorganic;
    private EditText gas;
    private EditText geochemicalBasis;
    private EditText chromatographic;
    private EditText colorQualityAnalysis;
    private EditText isotope;


    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_test);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);

        physicalProperty = findViewById(R.id.physical_property);
        rockOre = findViewById(R.id.rock_ore);
        crudeOil = findViewById(R.id.crude_oil);
        inorganic = findViewById(R.id.inorganic);
        gas = findViewById(R.id.gas);
        geochemicalBasis = findViewById(R.id.geochemical_basis);
        chromatographic = findViewById(R.id.chromatographic);
        colorQualityAnalysis = findViewById(R.id.color_quality_analysis);
        isotope = findViewById(R.id.isotope);

        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalysisTestActivity.this.finish();
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
                map.put("taskType", "分析测试");
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
                                LocalDataUtil.getIntance(AnalysisTestActivity.this).deletePlanInfo(key);
                            }
                            AnalysisTestActivity.this.finish();

                        } else {
                            Toast.makeText(AnalysisTestActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(AnalysisTestActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
        setCurrentTime();
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
                        LocalDataUtil.getIntance(AnalysisTestActivity.this).deletePlanInfo(key);
                        AnalysisTestActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    AnalysisTestActivity.this.finish();
                                } else {
                                    Toast.makeText(AnalysisTestActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(AnalysisTestActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                AnalysisTestActivity.this.finish();
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

    private AnalysisTestBean setData() {
        AnalysisTestBean bean = new AnalysisTestBean();
        bean.setWellName(wellName.getText().toString());

        bean.setPhysicalProperty(physicalProperty.getText().toString());
        bean.setRockOre(rockOre.getText().toString());
        bean.setCrudeOil(crudeOil.getText().toString());
        bean.setInorganic(inorganic.getText().toString());
        bean.setGas(gas.getText().toString());
        bean.setGeochemicalBasis(geochemicalBasis.getText().toString());
        bean.setChromatographic(chromatographic.getText().toString());
        bean.setColorQualityAnalysis(colorQualityAnalysis.getText().toString());
        bean.setIsotope(isotope.getText().toString());

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
            AnalysisTestBean bean = new Gson().fromJson(mPlanBean.getExtendData(), AnalysisTestBean.class);
            if (bean != null) {
                wellName.setText(bean.getWellName());
                physicalProperty.setText(bean.getPhysicalProperty());
                rockOre.setText(bean.getRockOre());
                crudeOil.setText(bean.getCrudeOil());
                inorganic.setText(bean.getInorganic());
                gas.setText(bean.getGas());
                geochemicalBasis.setText(bean.getGeochemicalBasis());
                chromatographic.setText(bean.getChromatographic());
                colorQualityAnalysis.setText(bean.getColorQualityAnalysis());
                isotope.setText(bean.getIsotope());

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
        planBean.setTaskType("分析测试");
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


}