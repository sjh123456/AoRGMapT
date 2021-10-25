package com.AoRGMapT.ui.home;

import android.os.Bundle;
import android.os.Handler;
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

import com.AoRGMapT.BaseApplication;
import com.AoRGMapT.R;
import com.AoRGMapT.adapter.HomeProjectAdapter;
import com.AoRGMapT.adapter.WorkProjectAdapter;
import com.AoRGMapT.bean.ProjectBean;
import com.AoRGMapT.bean.UserInfo;
import com.AoRGMapT.bean.WorkItemBean;
import com.AoRGMapT.databinding.FragmentHomeBinding;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.RequestUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private HomeProjectAdapter adapter;
    private WorkProjectAdapter workProjectAdapter;

    private List<ProjectBean> mProjectBeans = new ArrayList<>();

    private List<WorkItemBean> workItemBeans = new ArrayList<>();

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

        //binding.refreshLayout.setEnableOverScroll(true);


        binding.lingrefresh.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);

                DataAcquisitionUtil.getInstance().fieldPlanProject(new RequestUtil.OnResponseListener<ProjectBean>() {
                    @Override
                    public void onsuccess(Object projectBeans) {
                        try {
                            mProjectBeans.clear();
                            mProjectBeans.addAll((List<ProjectBean>) projectBeans);
                            adapter.notifyDataSetChanged();
                            refreshLayout.finishRefreshing();
                            Toast.makeText(HomeFragment.this.getContext(), "下拉刷新", Toast.LENGTH_SHORT).show();
                            Toast.makeText(HomeFragment.this.getContext(), "上拉加载", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                DataAcquisitionUtil.getInstance().fieldPlanProject(new RequestUtil.OnResponseListener<ProjectBean>() {
                    @Override
                    public void onsuccess(Object projectBeans) {
                        try {
                            mProjectBeans.addAll((List<ProjectBean>) projectBeans);
                            adapter.notifyDataSetChanged();
                            refreshLayout.finishLoadmore();
                            Toast.makeText(HomeFragment.this.getContext(), "上拉加载", Toast.LENGTH_SHORT).show();
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
        });


        //项目列表
        adapter = new HomeProjectAdapter(mProjectBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeFragment.this.getContext());
        binding.rl.setLayoutManager(layoutManager);
        binding.rl.setAdapter(adapter);
        initData();


        //工作列表
        initWorkProjectData();
        binding.workGrid.setLayoutManager(new GridLayoutManager(HomeFragment.this.getContext(), 4));
        workProjectAdapter = new WorkProjectAdapter(workItemBeans, this.getContext());
        binding.workGrid.setAdapter(workProjectAdapter);

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


    }

    //获取项目数据
    private void initData() {

        DataAcquisitionUtil.getInstance().fieldPlanProject(new RequestUtil.OnResponseListener<ProjectBean>() {
            @Override
            public void onsuccess(Object projectBeans) {
                try {
                    mProjectBeans.addAll((List<ProjectBean>) projectBeans);
                    adapter.notifyDataSetChanged();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}