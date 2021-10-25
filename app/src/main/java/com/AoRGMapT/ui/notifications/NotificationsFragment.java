package com.AoRGMapT.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
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

import com.AoRGMapT.R;
import com.AoRGMapT.adapter.PlanAdapter;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.databinding.FragmentNotificationsBinding;
import com.AoRGMapT.ui.dashboard.DashboardFragment;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private PlanAdapter planAdapter;
    private List<PlanBean> planBeans = new ArrayList<>();

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
                new Handler().postDelayed(() -> {
                    initData();
                    planAdapter.notifyDataSetChanged();
                    Toast.makeText(NotificationsFragment.this.getContext(), "下拉刷新", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefreshing();
                }, 1000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                new Handler().postDelayed(() -> {
                    Toast.makeText(NotificationsFragment.this.getContext(), "上拉加载", Toast.LENGTH_SHORT).show();
                    initData();
                    planAdapter.notifyDataSetChanged();
                    refreshLayout.finishLoadmore();
                }, 1000);
            }
        });

        initData();
        //计划列表
        planAdapter = new PlanAdapter(planBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationsFragment.this.getContext());
        binding.rlPlan.setLayoutManager(layoutManager);
        binding.rlPlan.setAdapter(planAdapter);
        planAdapter.notifyDataSetChanged();

        return root;
    }

    private void initData() {

        for (int i = 0; i < 10; i++) {
            PlanBean projectBean = new PlanBean(false);
            planBeans.add(projectBean);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}