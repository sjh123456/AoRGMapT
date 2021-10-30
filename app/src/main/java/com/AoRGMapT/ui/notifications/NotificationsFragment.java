package com.AoRGMapT.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.AoRGMapT.BaseApplication;
import com.AoRGMapT.R;
import com.AoRGMapT.adapter.PlanAdapter;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.PlanResponseData;
import com.AoRGMapT.databinding.FragmentNotificationsBinding;
import com.AoRGMapT.ui.dashboard.DashboardFragment;
import com.AoRGMapT.ui.home.HomeFragment;
import com.AoRGMapT.util.ChooseHomeDialog;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.RequestUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private final String TAG = "NotificationsFragment";


    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private PlanAdapter planAdapter;
    private List<PlanBean> planBeans = new ArrayList<>();

    private int pageSize = 5;
    private int current = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
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
                Toast.makeText(NotificationsFragment.this.getContext(), "下拉加载", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                Toast.makeText(NotificationsFragment.this.getContext(), "上拉加载", Toast.LENGTH_SHORT).show();
                initData(true, false);
            }
        });

        binding.ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseHomeDialog.getInstance().showDialog(NotificationsFragment.this.getActivity(), true, new View.OnClickListener() {
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
        planAdapter = new PlanAdapter(planBeans, this.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationsFragment.this.getContext());
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
                        if (planBeans.size() == planResponseData.getData().getTotal()) {
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