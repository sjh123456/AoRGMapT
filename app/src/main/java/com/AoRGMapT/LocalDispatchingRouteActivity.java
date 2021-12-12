package com.AoRGMapT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
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

import com.AoRGMapT.bean.CoreDescriptionBean;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.LocalDispatchingRouteBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.EditUtil;
import com.AoRGMapT.util.EncapsulationImageUrl;
import com.AoRGMapT.util.LocalDataUtil;
import com.AoRGMapT.util.RequestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDispatchingRouteActivity extends AppCompatActivity {

    public final static String TAG = "LocalDispatchingRouteActivity";
    private static final int DECIMAL_DIGITS = 2;//小数的位数
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] spinner = {"北京54", "西安80", "WGS84"};
    //记录时间
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private int spinnerSelectedIndex = 0;

    private TextView project_name;
    private EditText route_name;
    private EditText route_num;
    private EditText starting_point;
    private EditText end_position;
    private EditText starting_point_coordinates;
    private EditText end_coordinates;
    private Spinner earth_ellipsoid;
    private EditText scale;
    private EditText number_sampling_blocks;
    private EditText surface_conditions;
    private EditText measurement_start_time;
    private EditText measurement_termination_time;
    private EditText unit_measurement;
    private EditText person_in_charge;
    private EditText topic_name;
    private EditText topic_number;
    private EditText number_documents;
    private EditText length_pit_trenching;
    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_dispatching_route);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        project_name = findViewById(R.id.project_name);
        route_name = findViewById(R.id.route_name);
        route_num = findViewById(R.id.route_num);
        starting_point = findViewById(R.id.starting_point);
        end_position = findViewById(R.id.end_position);
        starting_point_coordinates = findViewById(R.id.starting_point_coordinates);
        end_coordinates = findViewById(R.id.end_coordinates);
        earth_ellipsoid = findViewById(R.id.earth_ellipsoid);
        scale = findViewById(R.id.scale);
        number_sampling_blocks = findViewById(R.id.number_sampling_blocks);
        surface_conditions = findViewById(R.id.surface_conditions);
        measurement_start_time = findViewById(R.id.measurement_start_time);
        measurement_termination_time = findViewById(R.id.measurement_termination_time);
        unit_measurement = findViewById(R.id.unit_measurement);
        person_in_charge = findViewById(R.id.person_in_charge);
        topic_name = findViewById(R.id.topic_name);
        topic_number = findViewById(R.id.topic_number);
        length_pit_trenching = findViewById(R.id.length_pit_trenching);
        number_documents = findViewById(R.id.number_documents);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);
        EditUtil.setPoint(length_pit_trenching,DECIMAL_DIGITS);
        EditUtil.setPoint(number_sampling_blocks,DECIMAL_DIGITS);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDispatchingRouteActivity.this.finish();
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
                map.put("taskType", "地调路线");
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                LocalDispatchingRouteBean bean = new LocalDispatchingRouteBean();
                bean.setRouteName(route_name.getText().toString());
                bean.setRouteNum(route_num.getText().toString());
                bean.setStartingPoint(starting_point.getText().toString());
                bean.setEndPosition(end_position.getText().toString());
                bean.setStartingPointCoordinates(starting_point_coordinates.getText().toString());
                bean.setEndCoordinates(end_coordinates.getText().toString());
                bean.setEarthEllipsoid(spinner[spinnerSelectedIndex]);
                bean.setScale(scale.getText().toString());
                bean.setNumberSamplingBlocks(number_sampling_blocks.getText().toString());
                bean.setSurfaceConditions(surface_conditions.getText().toString());
                bean.setMeasurementStartTime(measurement_start_time.getText().toString());
                bean.setMeasurementTerminationTime(measurement_termination_time.getText().toString());
                bean.setUnitMeasurement(unit_measurement.getText().toString());
                bean.setPersonInCharge(person_in_charge.getText().toString());
                bean.setTopicName(topic_name.getText().toString());
                bean.setLengthPitTrenching(length_pit_trenching.getText().toString());
                bean.setTopicNumber(topic_number.getText().toString());
                bean.setNumberDocuments(number_documents.getText().toString());
                map.put("extendData", new Gson().toJson(bean));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            //上传成功之后，删除本地项目
                            if (key != -1) {
                                LocalDataUtil.getIntance(LocalDispatchingRouteActivity.this).deletePlanInfo(key);
                            }
                            LocalDispatchingRouteActivity.this.finish();

                        } else {
                            Toast.makeText(LocalDispatchingRouteActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(LocalDispatchingRouteActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
        }

        if (!TextUtils.isEmpty(id) || key != -1) {

            tv_remove.setVisibility(View.VISIBLE);
            tv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (key != -1) {
                        LocalDataUtil.getIntance(LocalDispatchingRouteActivity.this).deletePlanInfo(key);
                        LocalDispatchingRouteActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    LocalDispatchingRouteActivity.this.finish();
                                } else {
                                    Toast.makeText(LocalDispatchingRouteActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(LocalDispatchingRouteActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                LocalDispatchingRouteActivity.this.finish();
            }
        });
    }

    //将信息保存在本地
    private void saveLocal() {
        if (BaseApplication.currentProject == null) {
            return;
        }
        PlanBean planBean = new PlanBean();
        planBean.setProjectId(BaseApplication.currentProject.getId());
        planBean.setTaskType("地调路线");
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
        LocalDispatchingRouteBean bean = new LocalDispatchingRouteBean();
        bean.setRouteName(route_name.getText().toString());
        bean.setRouteNum(route_num.getText().toString());
        bean.setStartingPoint(starting_point.getText().toString());
        bean.setEndPosition(end_position.getText().toString());
        bean.setStartingPointCoordinates(starting_point_coordinates.getText().toString());
        bean.setEndCoordinates(end_coordinates.getText().toString());
        bean.setEarthEllipsoid(spinner[spinnerSelectedIndex]);
        bean.setScale(scale.getText().toString());
        bean.setNumberSamplingBlocks(number_sampling_blocks.getText().toString());
        bean.setSurfaceConditions(surface_conditions.getText().toString());
        bean.setMeasurementStartTime(measurement_start_time.getText().toString());
        bean.setMeasurementTerminationTime(measurement_termination_time.getText().toString());
        bean.setUnitMeasurement(unit_measurement.getText().toString());
        bean.setPersonInCharge(person_in_charge.getText().toString());
        bean.setTopicName(topic_name.getText().toString());
        bean.setTopicNumber(topic_number.getText().toString());
        bean.setNumberDocuments(number_documents.getText().toString());
        bean.setLengthPitTrenching(length_pit_trenching.getText().toString());
        planBean.setExtendData(new Gson().toJson(bean));
        if (key != -1) {
            planBean.setKey(key);
            LocalDataUtil.getIntance(this).updatePlanInfo(planBean);

        } else {
            LocalDataUtil.getIntance(this).addLocalPlanInfo(planBean);
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


    //获取展示本地信息

    private void getLocalInfo() {
        if (key != -1) {
            PlanBean planBean = LocalDataUtil.getIntance(this).queryLocalPlanInfoFromKey(key);
            mPlanBean = planBean;
            showPlanInfo();

        }
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
            LocalDispatchingRouteBean bean = new Gson().fromJson(mPlanBean.getExtendData(), LocalDispatchingRouteBean.class);
            if (bean != null) {
                route_name.setText(bean.getRouteName());
                route_num.setText(bean.getRouteNum());
                starting_point.setText(bean.getStartingPoint());
                end_position.setText(bean.getEndPosition());
                starting_point_coordinates.setText(bean.getStartingPointCoordinates());
                end_coordinates.setText(bean.getEndCoordinates());
                for (int i=0;i<spinner.length;i++) {
                    if(TextUtils.equals(spinner[i],bean.getEarthEllipsoid())){
                        earth_ellipsoid.setSelection(i);
                        break;
                    }
                }

                scale.setText(bean.getScale());
                number_sampling_blocks.setText(bean.getNumberSamplingBlocks());
                surface_conditions.setText(bean.getSurfaceConditions());
                measurement_start_time.setText(bean.getMeasurementStartTime());
                measurement_termination_time.setText(bean.getMeasurementTerminationTime());
                unit_measurement.setText(bean.getUnitMeasurement());
                person_in_charge.setText(bean.getPersonInCharge());
                topic_name.setText(bean.getTopicName());
                topic_number.setText(bean.getTopicNumber());
                number_documents.setText(bean.getNumberDocuments());
                length_pit_trenching.setText(bean.getLengthPitTrenching());
            }
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
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, spinner);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框

        //设置下拉框的标题，不设置就没有难看的标题了
//        sp.setPrompt("请选择行星");
        //设置下拉框的数组适配器
        earth_ellipsoid.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        earth_ellipsoid.setSelection(spinnerSelectedIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        earth_ellipsoid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




}