package com.Acquisition;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.bean.DrillingReportBean;
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
 * 钻井日报
 */
public class DrillingReportActivity extends AppCompatActivity {
    public final static String TAG = "DrillingReportActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //填报日期
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;


    private TextView project_name;

    private EditText wellName;
    private EditText teamNumber;
    private EditText drillingPerformance;
    private EditText dailyFootage;
    private EditText monthlyCumulative;
    private EditText annualAccumulation;
    private EditText designedWellDepth;
    private EditText actualWellDepth;
    private EditText drillSize;
    private EditText density;
    private EditText viscosity;
    private EditText waterLoss;
    private EditText ph;
    private EditText displacement;
    private EditText pumpPressure;
    private EditText weightOnBit;
    private EditText speed;
    private EditText houseMovingData;
    private EditText spudInData;
    private EditText openingTimes;
    private EditText reachStratum;
    private EditText friction;
    private EditText productionProfile;
    private EditText productionAging;
    private EditText drillingToolStructure;
    //填报人
    private EditText recorder;
    //值班人
    private EditText personOnDuty;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adrilling_report);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);

        project_name = findViewById(R.id.project_name);
        wellName=findViewById(R.id.wellName);
        mEditTime=findViewById(R.id.filling_data);
        teamNumber=findViewById(R.id.team_number);
        drillingPerformance=findViewById(R.id.drilling_performance);
        dailyFootage=findViewById(R.id.daily_footage);
        monthlyCumulative=findViewById(R.id.monthly_cumulative);
        annualAccumulation=findViewById(R.id.annual_accumulation);
        designedWellDepth=findViewById(R.id.designed_well_depth);
        actualWellDepth=findViewById(R.id.actual_well_depth);
        drillSize=findViewById(R.id.drill_size);
        density=findViewById(R.id.density);
        viscosity=findViewById(R.id.viscosity);
        waterLoss=findViewById(R.id.water_loss);
        ph=findViewById(R.id.ph);
        displacement=findViewById(R.id.displacement);
        pumpPressure=findViewById(R.id.pump_pressure);
        weightOnBit=findViewById(R.id.weight_on_bit);
        speed=findViewById(R.id.speed);
        houseMovingData=findViewById(R.id.house_moving_data);
        spudInData=findViewById(R.id.spud_in_data);
        openingTimes=findViewById(R.id.opening_times);
        reachStratum=findViewById(R.id.reach_stratum);
        friction=findViewById(R.id.friction);
        productionProfile=findViewById(R.id.production_profile);
        productionAging=findViewById(R.id.production_aging);
        drillingToolStructure=findViewById(R.id.drilling_tool_structure);
        productionProfile=findViewById(R.id.production_profile);
        recorder=findViewById(R.id.recorder);
        personOnDuty=findViewById(R.id.person_on_duty);

        remark = findViewById(R.id.remark);


        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrillingReportActivity.this.finish();
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
                map.put("taskType", "钻井日报");
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
                                LocalDataUtil.getIntance(DrillingReportActivity.this).deletePlanInfo(key);
                            }
                            DrillingReportActivity.this.finish();

                        } else {
                            Toast.makeText(DrillingReportActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(DrillingReportActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
                        LocalDataUtil.getIntance(DrillingReportActivity.this).deletePlanInfo(key);
                        DrillingReportActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    DrillingReportActivity.this.finish();
                                } else {
                                    Toast.makeText(DrillingReportActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(DrillingReportActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                DrillingReportActivity.this.finish();
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

    private DrillingReportBean setData() {

        DrillingReportBean drillingReportBean=new DrillingReportBean();
        drillingReportBean.setWellName(wellName.getText().toString());
        drillingReportBean.setTeamNumber(teamNumber.getText().toString());
        drillingReportBean.setDrillingPerformance(drillingPerformance.getText().toString());
        drillingReportBean.setDailyFootage(dailyFootage.getText().toString());
        drillingReportBean.setMonthlyCumulative(monthlyCumulative.getText().toString());
        drillingReportBean.setAnnualAccumulation(annualAccumulation.getText().toString());
        drillingReportBean.setDesignedWellDepth(designedWellDepth.getText().toString());
        drillingReportBean.setDrillSize(drillSize.getText().toString());
        drillingReportBean.setDensity(density.getText().toString());
        drillingReportBean.setViscosity(viscosity.getText().toString());
        drillingReportBean.setWaterLoss(waterLoss.getText().toString());
        drillingReportBean.setPh(ph.getText().toString());
        drillingReportBean.setDisplacement(displacement.getText().toString());
        drillingReportBean.setPumpPressure(pumpPressure.getText().toString());
        drillingReportBean.setWeightOnBit(weightOnBit.getText().toString());
        drillingReportBean.setSpeed(speed.getText().toString());
        drillingReportBean.setHouseMovingData(houseMovingData.getText().toString());
        drillingReportBean.setSpudInData(spudInData.getText().toString());
        drillingReportBean.setOpeningTimes(openingTimes.getText().toString());
        drillingReportBean.setReachStratum(reachStratum.getText().toString());
        drillingReportBean.setFriction(friction.getText().toString());
        drillingReportBean.setProductionProfile(productionProfile.getText().toString());
        drillingReportBean.setProductionAging(productionAging.getText().toString());
        drillingReportBean.setDrillingToolStructure(drillingToolStructure.getText().toString());
        drillingReportBean.setPersonOnDuty(personOnDuty.getText().toString());
        drillingReportBean.setActualWellDepth(actualWellDepth.getText().toString());
        return drillingReportBean;
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
            DrillingReportBean bean = new Gson().fromJson(mPlanBean.getExtendData(), DrillingReportBean.class);
            if (bean != null) {
                wellName.setText(bean.getWellName());
                teamNumber.setText(bean.getTeamNumber());
                drillingPerformance.setText(bean.getDrillingPerformance());
                dailyFootage.setText(bean.getDailyFootage());
                monthlyCumulative.setText(bean.getMonthlyCumulative());
                annualAccumulation.setText(bean.getAnnualAccumulation());
                designedWellDepth.setText(bean.getDesignedWellDepth());
                actualWellDepth.setText(bean.getActualWellDepth());
                drillSize.setText(bean.getDrillSize());
                density.setText(bean.getDensity());
                viscosity.setText(bean.getViscosity());
                waterLoss.setText(bean.getWaterLoss());
                ph.setText(bean.getPh());
                displacement.setText(bean.getDisplacement());
                pumpPressure.setText(bean.getPumpPressure());
                weightOnBit.setText(bean.getWeightOnBit());
                speed.setText(bean.getSpeed());
                houseMovingData.setText(bean.getHouseMovingData());
                spudInData.setText(bean.getSpudInData());
                openingTimes.setText(bean.getOpeningTimes());
                reachStratum.setText(bean.getReachStratum());
                friction.setText(bean.getFriction());
                productionProfile.setText(bean.getProductionProfile());
                productionAging.setText(bean.getProductionAging());
                drillingToolStructure.setText(bean.getDrillingToolStructure());
                personOnDuty.setText(bean.getPersonOnDuty());

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
        planBean.setTaskType("钻井日报");
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
        houseMovingData.setText(simpleDateFormat.format(date));
        spudInData.setText(simpleDateFormat.format(date));
    }


}