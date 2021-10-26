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

public class BaseApplication extends Application {

    //当前定位的信息
    public static AMapLocation aMapLocation;
    //获取用户数据
    public static UserInfo userInfo;
    //项目列表
    public static List<ProjectBean> projectBeanList = new ArrayList<>();
    //当前的设备列表
    public static ProjectBean currentProject;


    @Override
    public void onCreate() {
        super.onCreate();


    }
}
