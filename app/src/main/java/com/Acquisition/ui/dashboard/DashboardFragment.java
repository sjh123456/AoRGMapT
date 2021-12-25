package com.Acquisition.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Acquisition.BaseApplication;
import com.Acquisition.Data.Acquisition.databinding.FragmentDashboardBinding;
import com.Acquisition.adapter.PlanAdapter;
import com.Acquisition.bean.PlanBean;
import com.Acquisition.bean.PlanResponseData;
import com.Acquisition.util.ChooseHomeDialog;
import com.Acquisition.util.DataAcquisitionUtil;
import com.Acquisition.util.RequestUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {


    private final String TAG = "DashboardFragment";

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private PlanAdapter planAdapter;
    private List<PlanBean> planBeans = new ArrayList<>();
    private int pageSize = 20;
    private int current = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.lingrefresh.setAutoLoadMore(true);

        binding.lingrefresh.setEnableLoadmore(true);

        binding.lingrefresh.setEnableRefresh(true);

        //binding.refreshLayout.setEnableOverScroll(true);

        binding.lingrefresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                initData(false, true);
                Toast.makeText(DashboardFragment.this.getContext(), "下拉加载", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                Toast.makeText(DashboardFragment.this.getContext(), "上拉加载", Toast.LENGTH_SHORT).show();
                initData(true, false);
            }
        });
        binding.ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseHomeDialog.getInstance().showDialog(DashboardFragment.this.getActivity(),true ,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //刷新项目信息
                        if (BaseApplication.currentProject != null) {
                            //显示当前项目信息
                            binding.tvName.setText(BaseApplication.currentProject.getProjectName());
                            initData(false, true);
                        }
                    }
                });
            }
        });

        //计划列表
        planAdapter = new PlanAdapter(planBeans,this.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(DashboardFragment.this.getContext());
        binding.rlPlan.setLayoutManager(layoutManager);
        binding.rlPlan.setAdapter(planAdapter);
        initData(false, false);

        return root;
    }


    /**
     * 获取数据
     *
     * @param load
     * @param refresh
     */
    private void initData(boolean load, boolean refresh) {

        if (BaseApplication.currentProject != null) {

            if (load) {
                current++;
            } else if (refresh) {
                current = 1;
                planBeans.clear();
            }

            DataAcquisitionUtil.getInstance().detailPageByJson(BaseApplication.currentProject.getId(), pageSize, current, new RequestUtil.OnResponseListener<PlanResponseData>() {
                @Override
                public void onsuccess(PlanResponseData planResponseData) {
                    if (planResponseData != null && planResponseData.getData() != null) {
                        if (planResponseData.getData().getRecords() != null) {
                            planBeans.addAll(planResponseData.getData().getRecords());
                        }
                    }
                    if (load) {
                        binding.lingrefresh.finishLoadmore();
                        if(planBeans.size()==planResponseData.getData().getTotal()){
                            binding.lingrefresh.setEnableLoadmore(false);
                        }
                    } else if (refresh) {
                        binding.lingrefresh.finishRefreshing();
                        binding.lingrefresh.setEnableLoadmore(true);
                    }

                    planAdapter.notifyDataSetChanged();



                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG, "获取项目任务列表失败");
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BaseApplication.currentProject != null) {
            //显示当前项目信息
            binding.tvName.setText(BaseApplication.currentProject.getProjectName());
        }
    }
}