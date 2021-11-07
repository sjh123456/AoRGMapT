package com.AoRGMapT.util;

import android.content.Context;

import com.AoRGMapT.bean.PlanBean;

import java.util.List;
import java.util.Map;

public class LocalDataUtil {

    public static LocalDataUtil localDataUtil;
    private MyDatabaseHelper databaseHelper;

    public LocalDataUtil(Context context) {
        databaseHelper = new MyDatabaseHelper(context, "planInfo.db3", null, 1);
    }

    public static LocalDataUtil getIntance(Context context) {
        synchronized (LocalDataUtil.class) {
            if (localDataUtil == null) {
                localDataUtil = new LocalDataUtil(context);
            }
        }
        return localDataUtil;
    }

    //添加本地项目信息
    public void addLocalPlanInfo(PlanBean planBean) {
        databaseHelper.inserDate(planBean);
    }

    //查询本地项目信息

    public List<PlanBean> queryLocalPlanListFromPeojectId(String projectId, int count, int pagesize) {

        return databaseHelper.queryPlanList(projectId, count, pagesize);
    }

    //根据key查询项目信息
    public PlanBean queryLocalPlanInfoFromKey(int key) {

        return databaseHelper.queryLocalPlanInfoFromKey(key);
    }


    //查询本地项目数量
    public Map<String, Integer> queryPlanInfoCount() {
        return databaseHelper.queryPlanInfoCount();
    }

    //更新项目信息
    public void updatePlanInfo(PlanBean planBean) {

        databaseHelper.updatePlan(planBean);
    }


    public void deletePlanInfo(int key) {
        databaseHelper.deletePlan(key);
    }


    /**
     * 获取项目类型本地的数据
     *
     * @param projectId
     * @return
     */
    public Map<String, Integer> queryTaskTypeCount(String projectId) {
        return databaseHelper.queryTaskTypeCount(projectId);
    }


}
