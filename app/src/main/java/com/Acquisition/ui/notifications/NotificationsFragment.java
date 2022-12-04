package com.Acquisition.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Acquisition.BaseApplication;
import com.Acquisition.adapter.PlanAdapter;
import com.Acquisition.bean.PlanBean;
import com.Acquisition.Data.Acquisition.databinding.FragmentNotificationsBinding;
import com.Acquisition.util.ChooseHomeDialog;
import com.Acquisition.util.LocalDataUtil;
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

    private int pageSize = 20;
    private int current = 0;

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
        planAdapter.setLocal(true);
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
                current = 0;
                planBeans.clear();
            }

            List<PlanBean> list = LocalDataUtil.getIntance(this.getContext()).queryLocalPlanListFromPeojectId(BaseApplication.currentProject.getId(),current,pageSize);
            if (list != null) {
                planBeans.addAll(list);
            }

            if (load) {
                binding.lingrefresh.finishLoadmore();
                if (planBeans.size() < current * pageSize) {
                    binding.lingrefresh.setEnableLoadmore(false);
                }
            } else if (refresh) {
                binding.lingrefresh.finishRefreshing();
                binding.lingrefresh.setEnableLoadmore(true);
            }

            planAdapter.notifyDataSetChanged();
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