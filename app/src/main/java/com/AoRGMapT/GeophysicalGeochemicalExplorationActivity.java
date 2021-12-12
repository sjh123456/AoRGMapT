package com.AoRGMapT;

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

import com.AoRGMapT.bean.GeophysicalGeochemicalExplorationBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.EditUtil;
import com.AoRGMapT.util.LocalDataUtil;
import com.AoRGMapT.util.RequestUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GeophysicalGeochemicalExplorationActivity extends AppCompatActivity {

    public final static String TAG = "GeophysicalGeochemicalExploration";
    private static final int DECIMAL_DIGITS = 2;//小数的位数
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String[] workAreaTypeArray = {"地震工区", "重力工区", "磁法工区", "电法工区", "化探工区", "其他"};
    private String[] workAreaCategoryArray = {"1、重", "2、磁", "3、电", "4、震", "5、化探", "6、其他"};
    private String[] collectDataArray = {"是", "否"};
    //记录时间
    private EditText mEditTime;

    //当前的项目
    private PlanBean mPlanBean;
    //当前项目的id
    private String id;
    //本地的key
    private int key = -1;

    private int workAreaTypeIndex = 0;
    private int workAreaCategoryIndex = 0;
    private int collectDataIndex = 0;


    private TextView project_name;

    private EditText work_area_name;
    private Spinner work_area_type;
    private Spinner work_area_category;
    private EditText scale;
    private EditText work_area;
    private EditText network_density;
    private EditText line_length;
    private EditText administrative_division;
    //private EditText number_sampling_blocks;
    private EditText number_measuring_lines;
    private EditText basin_name;
    private EditText secondary_construction_unit;
    private EditText tertiary_construction_unit;
    private EditText mining_right_block;
    private EditText surface_conditions;
    private Spinner collect_data;
    private EditText number_documents;
    private EditText construction_start_time;
    private EditText construction_termination_time;
    private EditText construction_unit;
    private EditText construction_director;
    private EditText deployment_unit;
    private EditText deployment_year;
    private EditText deployment_leader;
    private EditText topic_name;
    private EditText topic_number;

    private EditText recorder;
    private EditText remark;
    private TextView tv_save;
    private TextView tv_remove;
    private TextView tv_local_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geophysical_geochemical_exploration);
        id = getIntent().getStringExtra("id");
        key = getIntent().getIntExtra("key", -1);
        mEditTime = findViewById(R.id.ed_time);
        project_name = findViewById(R.id.project_name);
        work_area_name = findViewById(R.id.work_area_name);
        work_area_type = findViewById(R.id.work_area_type);
        work_area_category = findViewById(R.id.work_area_category);
        scale = findViewById(R.id.scale);
        work_area = findViewById(R.id.work_area);
        network_density = findViewById(R.id.network_density);
        line_length = findViewById(R.id.line_length);
        administrative_division = findViewById(R.id.administrative_division);
        //number_sampling_blocks = findViewById(R.id.number_sampling_blocks);
        number_measuring_lines = findViewById(R.id.number_measuring_lines);
        basin_name = findViewById(R.id.basin_name);
        secondary_construction_unit = findViewById(R.id.secondary_construction_unit);
        tertiary_construction_unit = findViewById(R.id.tertiary_construction_unit);
        mining_right_block = findViewById(R.id.mining_right_block);
        surface_conditions = findViewById(R.id.surface_conditions);
        collect_data = findViewById(R.id.collect_data);
        number_documents = findViewById(R.id.number_documents);
        construction_start_time = findViewById(R.id.construction_start_time);
        construction_termination_time = findViewById(R.id.construction_termination_time);
        construction_unit = findViewById(R.id.construction_unit);
        construction_director = findViewById(R.id.construction_director);
        deployment_unit = findViewById(R.id.deployment_unit);
        deployment_year = findViewById(R.id.deployment_year);
        deployment_leader = findViewById(R.id.deployment_leader);
        topic_name = findViewById(R.id.topic_name);
        topic_number = findViewById(R.id.topic_number);
        recorder = findViewById(R.id.recorder);
        remark = findViewById(R.id.remark);
        tv_save = findViewById(R.id.tv_save);
        tv_remove = findViewById(R.id.tv_remove);
        tv_local_save = findViewById(R.id.tv_local_save);
        EditUtil.setPoint(work_area, DECIMAL_DIGITS);
        EditUtil.setPoint(line_length, DECIMAL_DIGITS);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeophysicalGeochemicalExplorationActivity.this.finish();
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
                map.put("taskType", "物化探");
                map.put("recorder", recorder.getText().toString());
                map.put("recordDate", mEditTime.getText().toString());
                map.put("remark", remark.getText().toString());
                if (!TextUtils.isEmpty(id)) {
                    map.put("id", id);
                }
                map.put("extendData", new Gson().toJson(setData()));
                DataAcquisitionUtil.getInstance().submit(map, new RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>>() {
                    @Override
                    public void onsuccess(ResponseDataItem<PlanBean> responseDataItem) {

                        if (responseDataItem.isSuccess()) {
                            //上传成功之后，删除本地项目
                            if (key != -1) {
                                LocalDataUtil.getIntance(GeophysicalGeochemicalExplorationActivity.this).deletePlanInfo(key);
                            }
                            GeophysicalGeochemicalExplorationActivity.this.finish();

                        } else {
                            Toast.makeText(GeophysicalGeochemicalExplorationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void fail(String code, String message) {
                        Toast.makeText(GeophysicalGeochemicalExplorationActivity.this, "提交失败，请重新提交", Toast.LENGTH_SHORT).show();

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
                        LocalDataUtil.getIntance(GeophysicalGeochemicalExplorationActivity.this).deletePlanInfo(key);
                        GeophysicalGeochemicalExplorationActivity.this.finish();
                    } else {
                        DataAcquisitionUtil.getInstance().remove(id, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                            @Override
                            public void onsuccess(ResponseDataItem o) {
                                if (o.isSuccess()) {
                                    GeophysicalGeochemicalExplorationActivity.this.finish();
                                } else {
                                    Toast.makeText(GeophysicalGeochemicalExplorationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void fail(String code, String message) {
                                Toast.makeText(GeophysicalGeochemicalExplorationActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
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
                GeophysicalGeochemicalExplorationActivity.this.finish();
            }
        });

    }


    private GeophysicalGeochemicalExplorationBean setData() {
        GeophysicalGeochemicalExplorationBean bean = new GeophysicalGeochemicalExplorationBean();
        bean.setWorkAreaName(work_area_name.getText().toString());
        bean.setWorkAreaType(workAreaTypeArray[workAreaTypeIndex]);
        bean.setWorkAreaCategory(workAreaCategoryArray[workAreaCategoryIndex]);
        bean.setScale(scale.getText().toString());
        bean.setWorkArea(work_area.getText().toString());
        bean.setNetworkDensity(network_density.getText().toString());
        bean.setLineLength(line_length.getText().toString());
        bean.setAdministrativeDivision(administrative_division.getText().toString());
        //bean.setNumber_sampling_blocks(number_sampling_blocks.getText().toString());
        bean.setNumberMeasuringLines(number_measuring_lines.getText().toString());
        bean.setBasinName(basin_name.getText().toString());
        bean.setSecondaryConstructionUnit(secondary_construction_unit.getText().toString());
        bean.setTertiaryConstructionUnit(tertiary_construction_unit.getText().toString());
        bean.setMiningRightblock(mining_right_block.getText().toString());
        bean.setSurfaceConditions(surface_conditions.getText().toString());
        bean.setCollectData(collectDataArray[collectDataIndex]);
        bean.setNumberDocuments(number_documents.getText().toString());
        bean.setConstructionStartTime(construction_start_time.getText().toString());
        bean.setConstructionTerminationTime(construction_termination_time.getText().toString());
        bean.setConstructionUnit(construction_unit.getText().toString());
        bean.setConstructionDirector(construction_director.getText().toString());
        bean.setDeploymentUnit(deployment_unit.getText().toString());
        bean.setDeploymentYear(deployment_year.getText().toString());
        bean.setDeploymentLeader(deployment_leader.getText().toString());
        bean.setTopicName(topic_name.getText().toString());
        bean.setTopicNumber(topic_number.getText().toString());
        return bean;
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
            GeophysicalGeochemicalExplorationBean bean = new Gson().fromJson(mPlanBean.getExtendData(), GeophysicalGeochemicalExplorationBean.class);
            if (bean != null) {
                work_area_name.setText(bean.getWorkAreaName());
                for (int i = 0; i < workAreaTypeArray.length; i++) {
                    if (TextUtils.equals(workAreaTypeArray[i], bean.getWorkAreaType())) {
                        work_area_type.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < workAreaCategoryArray.length; i++) {
                    if (TextUtils.equals(workAreaCategoryArray[i], bean.getWorkAreaCategory())) {
                        work_area_category.setSelection(i);
                        break;
                    }
                }
                scale.setText(bean.getScale());
                work_area.setText(bean.getWorkArea());
                network_density.setText(bean.getNetworkDensity());
                line_length.setText(bean.getLineLength());
                administrative_division.setText(bean.getAdministrativeDivision());
                //number_sampling_blocks.setText(bean.getNumber_sampling_blocks());
                number_measuring_lines.setText(bean.getNumberMeasuringLines());
                basin_name.setText(bean.getBasinName());
                secondary_construction_unit.setText(bean.getSecondaryConstructionUnit());
                tertiary_construction_unit.setText(bean.getTertiaryConstructionUnit());
                mining_right_block.setText(bean.getMiningRightblock());
                surface_conditions.setText(bean.getSurfaceConditions());
                for (int i = 0; i < collectDataArray.length; i++) {
                    if (TextUtils.equals(collectDataArray[i], bean.getCollectData())) {
                        collect_data.setSelection(i);
                        break;
                    }
                }
                construction_start_time.setText(bean.getConstructionStartTime());
                construction_termination_time.setText(bean.getConstructionTerminationTime());
                construction_unit.setText(bean.getConstructionUnit());
                construction_director.setText(bean.getConstructionDirector());
                deployment_unit.setText(bean.getDeploymentUnit());
                deployment_year.setText(bean.getDeploymentYear());
                deployment_leader.setText(bean.getDeploymentLeader());
                topic_name.setText(bean.getTopicName());
                topic_number.setText(bean.getTopicNumber());
                number_documents.setText(bean.getNumberDocuments());
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
        planBean.setTaskType("物化探");
        planBean.setRecorder(recorder.getText().toString());
        planBean.setRecordDate(mEditTime.getText().toString());
        planBean.setCreateTime(mEditTime.getText().toString());
        planBean.setRemark(remark.getText().toString());
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
        ArrayAdapter<String> workAreaTypeAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, workAreaTypeArray);
        //设置数组适配器的布局样式
        workAreaTypeAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框

        //设置下拉框的标题，不设置就没有难看的标题了
//        sp.setPrompt("请选择行星");
        //设置下拉框的数组适配器
        work_area_type.setAdapter(workAreaTypeAdapter);
        //设置下拉框默认的显示第一项
        work_area_type.setSelection(workAreaTypeIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        work_area_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workAreaTypeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> workAreaCategoryAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, workAreaCategoryArray);
        //设置数组适配器的布局样式
        workAreaCategoryAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框

        //设置下拉框的标题，不设置就没有难看的标题了
//        sp.setPrompt("请选择行星");
        //设置下拉框的数组适配器
        work_area_category.setAdapter(workAreaCategoryAdapter);
        //设置下拉框默认的显示第一项
        work_area_category.setSelection(workAreaCategoryIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        work_area_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workAreaCategoryIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> collectDataAdapter = new ArrayAdapter<String>(this, R.layout.item_dropdown, collectDataArray);
        //设置数组适配器的布局样式
        collectDataAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框

        //设置下拉框的标题，不设置就没有难看的标题了
//        sp.setPrompt("请选择行星");
        //设置下拉框的数组适配器
        collect_data.setAdapter(collectDataAdapter);
        //设置下拉框默认的显示第一项
        collect_data.setSelection(collectDataIndex);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        collect_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collectDataIndex = position;
                if (position == 0) {
                    //是
                    construction_start_time.setEnabled(false);
                    construction_termination_time.setEnabled(false);
                    construction_unit.setEnabled(false);
                    construction_director.setEnabled(false);
                    deployment_unit.setEnabled(false);
                    deployment_year.setEnabled(false);
                    deployment_leader.setEnabled(false);
                    topic_name.setEnabled(false);
                    topic_number.setEnabled(false);
                } else {
                    //否
                    construction_start_time.setEnabled(true);
                    construction_termination_time.setEnabled(true);
                    construction_unit.setEnabled(true);
                    construction_director.setEnabled(true);
                    deployment_unit.setEnabled(true);
                    deployment_year.setEnabled(true);
                    deployment_leader.setEnabled(true);
                    topic_name.setEnabled(true);
                    topic_number.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}