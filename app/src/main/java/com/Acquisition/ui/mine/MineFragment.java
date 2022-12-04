package com.Acquisition.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.Acquisition.BaseApplication;
import com.Acquisition.bean.ProjectBean;
import com.Acquisition.bean.TaskListResponseData;
import com.Acquisition.Data.Acquisition.databinding.FragmentMineBinding;
import com.Acquisition.util.ChooseHomeDialog;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.RequestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;

public class MineFragment extends Fragment {

    private final String TAG = "MineFragment";

    //当前项目1
    private ProjectBean bean1 = new ProjectBean();
    // 当前项目2
    private ProjectBean bean2 = new ProjectBean();

    private MineViewModel mineViewModel;
    private FragmentMineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mineViewModel =
                new ViewModelProvider(this).get(MineViewModel.class);

        binding = FragmentMineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initData();

        binding.ivSwitch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseHomeDialog.getInstance().setProjectBean(bean1).showDialog(MineFragment.this.getActivity(), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //刷新项目信息
                        if (BaseApplication.currentProject != null) {
                            //显示当前项目信息
                            bean1 = (ProjectBean) v.getTag();
                            binding.tvProjectName1.setText(bean1.getProjectName());
                            setPieData();
                        }
                    }
                });
            }
        });

        binding.ivSwitch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseHomeDialog.getInstance().setProjectBean(bean2).showDialog(MineFragment.this.getActivity(), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //刷新项目信息
                        if (BaseApplication.currentProject != null) {
                            //显示当前项目信息
                            bean2 = (ProjectBean) v.getTag();
                            binding.tvProjectName2.setText(bean2.getProjectName());
                            setChartData();
                        }
                    }
                });
            }
        });

        return root;
    }

    /**
     * 获取数据
     */
    private void initData() {

        //显示用户信息
        if (BaseApplication.userInfo != null) {
            binding.name.setText(BaseApplication.userInfo.getUserName());
            binding.account.setText(BaseApplication.userInfo.getAccount());
        }
        //显示项目信息
        if (BaseApplication.currentProject != null) {
            bean1 = BaseApplication.currentProject;
            bean2 = BaseApplication.currentProject;
        }
        if (bean1 != null) {
            binding.tvProjectName1.setText(bean1.getProjectName());
            binding.tvProjectName2.setText(bean2.getProjectName());
        }

        //获取饼状图
        setPieData();
        setChartData();
    }

    /**
     * 设置饼状图
     */
    private void setPieData() {
        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue(bean1.getTaskCount(), ChartUtils.COLOR_GREEN));
        values.add(new SliceValue(bean1.getTaskLocalCount(), ChartUtils.COLOR_RED));
        PieChartData mPieChartData = new PieChartData(values);
        mPieChartData.setHasLabels(false);
        mPieChartData.setHasLabelsOnlyForSelected(true);
        mPieChartData.setHasLabelsOutside(false);
        mPieChartData.setHasCenterCircle(false);
        binding.pcvMain.setPieChartData(mPieChartData);
        binding.tvCollectData.setText("采集数据共 " + bean1.getTaskCount());
        binding.tvSubmitted.setText("已提交数据 " + bean1.getTaskCount());
        binding.tvNotSubmitted.setText("未提交数据 " + bean1.getTaskLocalCount());
    }

    /**
     * 设置柱状图
     */
    private void setChartData() {

        DataAcquisitionUtil.getInstance().getTasksList(bean2.getId(), new RequestUtil.OnResponseListener<TaskListResponseData>() {
            @Override
            public void onsuccess(TaskListResponseData taskListResponseData) {
                setChart(taskListResponseData);
            }

            @Override
            public void fail(String code, String message) {

            }
        });
    }

    private void setChart(TaskListResponseData data) {
        if (data != null && data.getData() != null) {
            setColumnDatasByParams(data);
        }

    }


    /**
     * 根据不同的参数 决定绘制什么样的柱状图
     *
     * @param data 总列数
     */
    private void setColumnDatasByParams(TaskListResponseData data) {

        Map<String, Integer> map = LocalDataUtil.getIntance(this.getContext()).queryTaskTypeCount(bean2.getProjectId());

        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<>();
        //双重for循环给每个子列设置随机的值和随机的颜色
        for (int i = 0; i < data.getData().size(); ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < 2; ++j) {
                //确定是否反向
                int negativeSign = 1;
                //根据反向值 设置列的值
                if (j == 0) {
                    values.add(new SubcolumnValue(data.getData().get(i).getTaskCount(), ChartUtils.COLOR_GREEN));
                } else {

                    if (map.get(data.getData().get(i).getTaskType()) != null) {
                        values.add(new SubcolumnValue(map.get(data.getData().get(i).getTaskType()), ChartUtils.COLOR_RED));
                    }
                }
            }

            /*===== 柱状图相关设置 =====*/
            Column column = new Column(values);
            column.setHasLabels(true);                    //没有标签
            column.setHasLabelsOnlyForSelected(true);  //点击只放大
            columns.add(column);
            axisValues.add(new AxisValue(i, data.getData().get(i).getTaskType().toCharArray()));
        }
        ColumnChartData mColumnChartData = new ColumnChartData(columns);               //设置数据
        mColumnChartData.setStacked(true);                          //设置是否堆叠

        /*===== 坐标轴相关设置 类似于Line Charts =====*/

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setValues(axisValues);
        mColumnChartData.setAxisXBottom(axisX);
        mColumnChartData.setAxisYLeft(axisY);

        binding.ccvMain.setColumnChartData(mColumnChartData);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}