package com.Acquisition.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Acquisition.BaseApplication;
import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.Data.Acquisition.databinding.FragmentHomeBinding;
import com.Acquisition.adapter.HomeProjectAdapter;
import com.Acquisition.adapter.WorkProjectAdapter;
import com.Acquisition.bean.ProjectBean;
import com.Acquisition.bean.ProjectResponseData;
import com.Acquisition.bean.StatisticsProjectResponseData;
import com.Acquisition.bean.WorkItemBean;
import com.Acquisition.util.ChooseHomeDialog;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.LocalDataUtil;
import com.Acquisition.util.RequestUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {


    private final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private HomeProjectAdapter adapter;
    private WorkProjectAdapter workProjectAdapter;
    private WorkProjectAdapter adjustProjectAdapter;

    //分页
    private int pageSize = 20;
    private int current = 1;

    private List<ProjectBean> mProjectBeans = new ArrayList<>();

    private List<WorkItemBean> workItemBeans = new ArrayList<>();

    private List<WorkItemBean> adjustItemBeans = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        binding.lingrefresh.setAutoLoadMore(true);

        binding.lingrefresh.setEnableLoadmore(true);

        binding.lingrefresh.setEnableRefresh(true);


        binding.lingrefresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                current = 1;
                DataAcquisitionUtil.getInstance().getBoardTasksList(pageSize, current, new RequestUtil.OnResponseListener<StatisticsProjectResponseData>() {
                    @Override
                    public void onsuccess(StatisticsProjectResponseData projectBeans) {
                        try {
                            mProjectBeans.clear();
                            mProjectBeans.addAll(projectBeans.getData());
                            getLocaProjectPlanSize();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(HomeFragment.this.getContext(), "下拉刷新", Toast.LENGTH_SHORT).show();
                            //更新项目列表
                            BaseApplication.projectBeanList = mProjectBeans;
                        } catch (Exception ex) {
                            Log.e(TAG, "数据解析失败");
                        }
                        refreshLayout.finishRefreshing();
                        //打开上拉加载的功能
                        binding.lingrefresh.setEnableLoadmore(true);
                        DataAcquisitionUtil.getInstance().fieldPlanProject(pageSize, current, new RequestUtil.OnResponseListener<ProjectResponseData>() {
                            @Override
                            public void onsuccess(ProjectResponseData projectResponseData) {

                                if (projectResponseData.getData() != null
                                        && projectResponseData.getData() != null
                                        && projectResponseData.getData().getRecords() != null) {
                                    BaseApplication.projectBeanDetailList.clear();
                                    BaseApplication.projectBeanDetailList.addAll(projectResponseData.getData().getRecords());
                                    setProjectTaskCount();

                                }
                            }

                            @Override
                            public void fail(String code, String message) {

                            }
                        });

                    }

                    @Override
                    public void fail(String code, String message) {
                        Log.e(TAG, "获取项目列表失败：" + code + message);
                        Toast.makeText(HomeFragment.this.getContext(), "获取项目列表失败", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefreshing();
                        //打开上拉加载的功能
                        binding.lingrefresh.setEnableLoadmore(true);
                    }
                });


            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                current++;
                DataAcquisitionUtil.getInstance().getBoardTasksList(pageSize, current, new RequestUtil.OnResponseListener<StatisticsProjectResponseData>() {
                    @Override
                    public void onsuccess(StatisticsProjectResponseData projectBeans) {
                        try {
                            mProjectBeans.addAll(projectBeans.getData());
                            getLocaProjectPlanSize();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(HomeFragment.this.getContext(), "上拉加载", Toast.LENGTH_SHORT).show();
//                            if (projectBeans.getData().getTotal() == mProjectBeans.size()) {
//                                //当数据加载完成之后，停止上拉加载功能
//                                binding.lingrefresh.setEnableLoadmore(false);
//                            }
                            BaseApplication.projectBeanList = mProjectBeans;
                        } catch (Exception ex) {
                            Log.e(TAG, "数据解析失败");
                        }
                        refreshLayout.finishLoadmore();
                        DataAcquisitionUtil.getInstance().fieldPlanProject(pageSize, current, new RequestUtil.OnResponseListener<ProjectResponseData>() {
                            @Override
                            public void onsuccess(ProjectResponseData projectResponseData) {

                                if (projectResponseData.getData() != null
                                        && projectResponseData.getData() != null
                                        && projectResponseData.getData().getRecords() != null) {
                                    BaseApplication.projectBeanDetailList.addAll(projectResponseData.getData().getRecords());
                                    setProjectTaskCount();

                                }
                            }

                            @Override
                            public void fail(String code, String message) {

                            }
                        });
                    }

                    @Override
                    public void fail(String code, String message) {
                        Log.e(TAG, "获取项目列表失败：" + code + message);
                        Toast.makeText(HomeFragment.this.getContext(), "获取项目列表失败", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishLoadmore();
                    }
                });


            }
        });

        binding.ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseHomeDialog.getInstance().showDialog(HomeFragment.this.getActivity(), true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //刷新项目信息
                        if (BaseApplication.currentProject != null) {
                            //显示当前项目信息
                            binding.tvProjectName.setText(BaseApplication.currentProject.getProjectName());
                            binding.alreadyDoneNum.setText(BaseApplication.currentProject.getTaskCount() + "");
                            binding.waitNum.setText(BaseApplication.currentProject.getTaskLocalCount() + "");
                        }
                    }
                });
            }
        });

        //项目列表
        adapter = new HomeProjectAdapter(mProjectBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeFragment.this.getContext());
        binding.rl.setLayoutManager(layoutManager);
        binding.rl.setAdapter(adapter);
        initData();


        //工作列表
        initWorkProjectData();
        binding.workGrid.setLayoutManager(new GridLayoutManager(HomeFragment.this.getContext(), 5));
        workProjectAdapter = new WorkProjectAdapter(workItemBeans, this.getContext());
        binding.workGrid.setAdapter(workProjectAdapter);

        binding.adjustGrid.setLayoutManager(new GridLayoutManager(HomeFragment.this.getContext(), 5));
        adjustProjectAdapter = new WorkProjectAdapter(adjustItemBeans, this.getContext());
        binding.adjustGrid.setAdapter(adjustProjectAdapter);

        return root;
    }

    //初始化数据采集模块
    private void initWorkProjectData() {
        WorkItemBean bean1 = new WorkItemBean("井位确定", R.drawable.determine);
        WorkItemBean bean2 = new WorkItemBean("井场准备", R.drawable.intend);
        WorkItemBean bean3 = new WorkItemBean("开工验收", R.drawable.start);
        WorkItemBean bean4 = new WorkItemBean("钻井施工", R.drawable.borehole);
        WorkItemBean bean5 = new WorkItemBean("测井施工", R.drawable.logging);
        WorkItemBean bean6 = new WorkItemBean("录井施工", R.drawable.logging_construction);
        WorkItemBean bean7 = new WorkItemBean("岩心描述", R.drawable.core_description);
        WorkItemBean bean8 = new WorkItemBean("压裂试油", R.drawable.fracturing_test);
        WorkItemBean bean9 = new WorkItemBean("质量检查", R.drawable.quality_testing);
        WorkItemBean bean10 = new WorkItemBean("野外验收", R.drawable.field_acceptance);
        WorkItemBean bean11 = new WorkItemBean("成果验收", R.drawable.achievement);
        WorkItemBean bean12 = new WorkItemBean("复耕复垦", R.drawable.reclamation);
        WorkItemBean bean13 = new WorkItemBean("废物处理", R.drawable.disposal);

        workItemBeans.add(bean1);
        workItemBeans.add(bean2);
        workItemBeans.add(bean3);
        workItemBeans.add(bean4);
        workItemBeans.add(bean5);
        workItemBeans.add(bean6);
        workItemBeans.add(bean7);
        workItemBeans.add(bean8);
        workItemBeans.add(bean9);
        workItemBeans.add(bean10);
        workItemBeans.add(bean11);
        workItemBeans.add(bean12);
        workItemBeans.add(bean13);

        WorkItemBean adjustItem1 = new WorkItemBean("地调路线", R.drawable.route);
        WorkItemBean adjustItem2 = new WorkItemBean("物化探", R.drawable.analysis);
        WorkItemBean adjustItem3 = new WorkItemBean("分析化验", R.drawable.assay);

        adjustItemBeans.add(adjustItem1);
        adjustItemBeans.add(adjustItem2);
        adjustItemBeans.add(adjustItem3);

    }

    //获取项目数据
    private void initData() {
        DataAcquisitionUtil.getInstance().getBoardTasksList(pageSize, current, new RequestUtil.OnResponseListener<StatisticsProjectResponseData>() {
            @Override
            public void onsuccess(StatisticsProjectResponseData projectBeans) {
                try {
                    mProjectBeans.addAll(projectBeans.getData());
                    getLocaProjectPlanSize();
                    adapter.notifyDataSetChanged();
                    BaseApplication.projectBeanList = mProjectBeans;
                    if (BaseApplication.currentProject == null && mProjectBeans != null && mProjectBeans.size() > 0) {
                        BaseApplication.currentProject = mProjectBeans.get(0);
                        binding.alreadyDoneNum.setText(BaseApplication.currentProject.getTaskCount() + "");
                        //显示当前项目信息
                        binding.tvProjectName.setText(BaseApplication.currentProject.getProjectName());
                        binding.waitNum.setText(BaseApplication.currentProject.getTaskLocalCount() + "");
                    }
                    DataAcquisitionUtil.getInstance().fieldPlanProject(pageSize, current, new RequestUtil.OnResponseListener<ProjectResponseData>() {
                        @Override
                        public void onsuccess(ProjectResponseData projectResponseData) {

                            if (projectResponseData.getData() != null
                                    && projectResponseData.getData() != null
                                    && projectResponseData.getData().getRecords() != null) {
                                BaseApplication.projectBeanDetailList.clear();
                                BaseApplication.projectBeanDetailList.addAll(projectResponseData.getData().getRecords());
                                setProjectTaskCount();
                            }
                        }

                        @Override
                        public void fail(String code, String message) {

                        }
                    });


                } catch (Exception ex) {
                    Log.e(TAG, "数据解析失败");
                }
            }

            @Override
            public void fail(String code, String message) {
                Log.e(TAG, "获取项目列表失败：" + code + message);
            }
        });


    }

    //获取本地项目数量
    private void getLocaProjectPlanSize() {
        try {
            Map<String, Integer> map = LocalDataUtil.getIntance(HomeFragment.this.getContext()).queryPlanInfoCount();
            if (map != null && map.size() > 0 && mProjectBeans != null) {
                for (ProjectBean bean : mProjectBeans) {
                    if (map.get(bean.getProjectId()) != null) {
                        bean.setTaskLocalCount(map.get(bean.getProjectId()));
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "error:" + ex.getMessage());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (BaseApplication.currentProject != null) {
            //显示当前项目信息
            binding.tvProjectName.setText(BaseApplication.currentProject.getProjectName());
            binding.alreadyDoneNum.setText(BaseApplication.currentProject.getTaskCount() + "");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void setProjectTaskCount() {
        for (ProjectBean bean1 : BaseApplication.projectBeanList) {
            for (ProjectBean bean2 : BaseApplication.projectBeanDetailList) {
                if (TextUtils.equals(bean1.getId(), bean2.getId())) {
                    bean1.setDefaultWellName(bean2.getDefaultWellName());
                    break;
                }

            }

        }
    }
}