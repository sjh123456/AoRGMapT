package com.AoRGMapT;

import android.app.Application;
import android.widget.Toast;

import com.AoRGMapT.bean.ProjectBean;
import com.AoRGMapT.bean.UserInfo;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.RequestUtil;
import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseApplication extends Application {

    //当前定位的信息
    public static AMapLocation aMapLocation;
    //获取用户数据
    public static UserInfo userInfo;
    //项目列表
    public static List<ProjectBean> projectBeanList = new ArrayList<>();
    public static List<ProjectBean> projectBeanDetailList=new ArrayList<>();
    //当前的设备列表
    public static ProjectBean currentProject;
    //获取本地项目的设备列表数量信息
    public static List<Map<String,Integer>> planSizeList=new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();


    }
}
