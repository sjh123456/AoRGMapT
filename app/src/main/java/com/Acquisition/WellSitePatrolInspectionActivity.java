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
import com.Acquisition.bean.PlanBean;
import com.Acquisition.bean.ResponseDataItem;
import com.Acquisition.bean.WellSitePatrolInspectionBean;
import com.Acquisition.bean.WellSitePreparationBean;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.RequestUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 井场巡检
 */
public class WellSitePatrolInspectionActivity extends AppCompatActivity {
    public final static String TAG = "AnalysisAssayActivity";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] chooseTypeArray = {"是", "否"};
    //记录时间
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private int noiseIndex = 0;
    private int oilPumpingMachineIndex = 0;
    private int oilLeakageIndex = 0;
    private int oilSmellIndex = 0;
    private int pressureIndex = 0;
    private int clutterIndex = 0;
    private int circulatingWaterIndex = 0;
    private int motorTemperatureIndex = 0;
    private int hiddenDangerIndex = 0;


    private TextView project_name;

    private EditText wellName;

    private EditText fillingTime;
    private EditText teamNumber;
    private EditText weather;
    private EditText airTemperature;
    private EditText temperature;
    private Spinner noise;
    private Spinner oilPumpingMachine;
    private Spinner oilLeakage;
    private Spinner oilSmell;
    private Spinner pressure;
    private Spinner clutter;
    private Spinner circulatingWater;
    private Spinner motorTemperature;
    private Spinner hiddenDanger;


    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_site_patrol_inspection);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        project_name = findViewById(R.id.project_name);
        wellName = findViewById(R.id.wellName);

        fillingTime = findViewById(R.id.filling_time);
        teamNumber = findViewById(R.id.team_number);
        weather = findViewById(R.id.weather);
        airTemperature = findViewById(R.id.air_temperature);
        temperature = findViewById(R.id.temperature);
        noise = findViewById(R.id.noise);
        oilPumpingMachine = findViewById(R.id.oil_pumping_machine);
        oilLeakage = findViewById(R.id.oil_leakage);
        oilSmell = findViewById(R.id.oil_smell);
        pressure = findViewById(R.id.pressure);
        clutter = findViewById(R.id.clutter);
        circulatingWater = findViewById(R.id.circulating_water);
        motorTemperature = findViewById(R.id.motor_temperature);
        hiddenDanger = findViewById(R.id.hidden_danger);

        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WellSitePatrolInspectionActivity.this.finish();
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
                map.put("taskType", "井场巡检");
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
                                LocalDataUtil.getIntance(WellSitePatrolInspectionActivity.this).deletePlanInfo(key);
                            }
                            WellSitePatrolInspectionActivity.this.finish();

                        } else {
                            Toast.makeText(WellSitePatrolInspectionActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(WellSitePatrolInspectionActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
                        LocalDataUtil.getIntance(WellSitePatrolInspectionActivity.this).deletePlanInfo(key);
                        WellSitePatrolInspectionActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    WellSitePatrolInspectionActivity.this.finish();
                                } else {
                                    Toast.makeText(WellSitePatrolInspectionActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(WellSitePatrolInspectionActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                WellSitePatrolInspectionActivity.this.finish();
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

    private WellSitePatrolInspectionBean setData() {
        WellSitePatrolInspectionBean bean = new WellSitePatrolInspectionBean();
        bean.setWellName(wellName.getText().toString());
        bean.setFillingTime(fillingTime.getText().toString());
        bean.setTeamNumber(teamNumber.getText().toString());
        bean.setWeather(weather.getText().toString());
        bean.setAirTemperature(airTemperature.getText().toString());
        bean.setTemperature(temperature.getText().toString());
        bean.setNoise(chooseTypeArray[noiseIndex]);
        bean.setOilLeakage(chooseTypeArray[oilLeakageIndex]);
        bean.setOilSmell(chooseTypeArray[oilSmellIndex]);
        bean.setPressure(chooseTypeArray[pressureIndex]);
        bean.setClutter(chooseTypeArray[clutterIndex]);
        bean.setCirculatingWater(chooseTypeArray[circulatingWaterIndex]);
        bean.setMotorTemperature(chooseTypeArray[motorTemperatureIndex]);
        bean.setHiddenDanger(chooseTypeArray[hiddenDangerIndex]);
        bean.setOilPumpingMachine(chooseTypeArray[oilPumpingMachineIndex]);

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
            WellSitePatrolInspectionBean bean = new Gson().fromJson(mPlanBean.getExtendData(), WellSitePatrolInspectionBean.class);
            if (bean != null) {
                wellName.setText(bean.getWellName());

                fillingTime.setText(bean.getFillingTime());
                teamNumber.setText(bean.getTeamNumber());
                weather.setText(bean.getWeather());
                airTemperature.setText(bean.getAirTemperature());
                temperature.setText(bean.getTemperature());
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getNoise())) {
                        noise.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getOilPumpingMachine())) {
                        oilPumpingMachine.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getOilLeakage())) {
                        oilLeakage.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getOilSmell())) {
                        oilSmell.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getPressure())) {
                        pressure.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getClutter())) {
                        clutter.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getCirculatingWater())) {
                        circulatingWater.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getMotorTemperature())) {
                        motorTemperature.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < chooseTypeArray.length; i++) {
                    if (TextUtils.equals(chooseTypeArray[i], bean.getHiddenDanger())) {
                        hiddenDanger.setSelection(i);
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
        planBean.setTaskType("井场巡检");
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

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); //设置时间格式
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区
        Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
        String createDate = formatter.format(curDate);   //格式转换
        fillingTime.setText(createDate);
    }

    private void initSpinner() {
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> chooseTypeAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, chooseTypeArray);
        //设置数组适配器的布局样式
        chooseTypeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //设置下拉框的数组适配器
        noise.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        noise.setSelection(noiseIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        noise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noiseIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        oilPumpingMachine.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        oilPumpingMachine.setSelection(oilPumpingMachineIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        oilPumpingMachine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oilPumpingMachineIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        oilLeakage.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        oilLeakage.setSelection(oilLeakageIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        oilLeakage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oilLeakageIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        oilSmell.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        oilSmell.setSelection(oilSmellIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        oilSmell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oilSmellIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //设置下拉框的数组适配器
        pressure.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        pressure.setSelection(pressureIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        pressure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pressureIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        clutter.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        clutter.setSelection(clutterIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        clutter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clutterIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        circulatingWater.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        circulatingWater.setSelection(circulatingWaterIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        circulatingWater.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                circulatingWaterIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        motorTemperature.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        motorTemperature.setSelection(motorTemperatureIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        motorTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                motorTemperatureIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的数组适配器
        hiddenDanger.setAdapter(chooseTypeAdapter);
        //设置下拉框默认的显示第一项
        hiddenDanger.setSelection(hiddenDangerIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        hiddenDanger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hiddenDangerIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}