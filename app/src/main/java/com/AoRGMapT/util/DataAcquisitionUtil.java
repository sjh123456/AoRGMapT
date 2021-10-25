package com.AoRGMapT.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.AoRGMapT.BaseApplication;
import com.AoRGMapT.bean.ProjectBean;
import com.AoRGMapT.bean.UserInfo;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAcquisitionUtil {


    private static DataAcquisitionUtil util;
    // 账号信息
    private final String account = "TEST1";
    private final String name = "地调局TEST1";
    private final String password = "1234";

    private String TAG = "DataAcquisitionUtil";

    public static DataAcquisitionUtil getInstance() {
        synchronized (DataAcquisitionUtil.class) {
            if (util == null) {
                util = new DataAcquisitionUtil();
            }
        }
        return util;
    }


    public void Login(String account, String name, String password, RequestUtil.OnResponseListener responseListener) {

        Map<String, String> header = new HashMap<>();
        header.put("User-Type", "app");
        header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");

        Map<String, String> param = new HashMap<>();
        param.put("grantType", "credible");
        param.put("account", account);
        param.put("password", password);
        param.put("userName", name);
        param.put("tenantId", "100000");

        RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/v1/token", param, header, responseListener, UserInfo.class);

    }


    /**
     * 获取项目列表
     *
     * @param responseListener
     */
    public void fieldPlanProject(RequestUtil.OnResponseListener<ProjectBean> responseListener) {


        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {

            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldPlanProject/list", param, header, responseListener, ProjectBean.class);
        } else {
            Login(account, name, password, new RequestUtil.OnResponseListener<UserInfo>() {
                @Override
                public void onsuccess(Object obj) {
                    if (obj instanceof UserInfo && obj != null) {
                        UserInfo userInfo = (UserInfo) obj;
                        if (!TextUtils.isEmpty(userInfo.getAccessToken())) {
                            BaseApplication.userInfo = userInfo;
                            Map<String, String> header = new HashMap<>();
                            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
                            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
                            Map<String, String> param = new HashMap<>();
                            param.put("pageSize", "1");
                            param.put("current", "1");
                            RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldPlanProject/list", param, header, responseListener, ProjectBean.class);
                        }
                    }
                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG + "fieldPlanProject", "errorcode:" + code + "  message:" + message);
                }
            });
        }
    }

    /**
     * 获取项目下面的任务列表
     *
     * @param responseListener
     */
    public void detailPageByJson(String projectId, RequestUtil.OnResponseListener responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("projectId", projectId);
            param.put("column", "recordDate");
            param.put("pageSize", "20");
            param.put("current", "1");
            RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/detailPageByJson", param, header, responseListener, List.class);
        } else {
            Login("TEST1", "地调局TEST1", "1234", new RequestUtil.OnResponseListener() {
                @Override
                public void onsuccess(Object o) {
                    Map<String, String> header = new HashMap<>();
                    header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
                    header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
                    Map<String, String> param = new HashMap<>();
                    param.put("projectId", projectId);
                    param.put("column", "recordDate");
                    RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/detailPageByJson", param, header, responseListener, List.class);
                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG + "fieldPlanProject", "errorcode:" + code + "  message:" + message);
                }
            });
        }
    }


    /**
     * 获取任务详情
     *
     * @param responseListener
     */
    public void detailByJson(String id, RequestUtil.OnResponseListener responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("id", id);
            RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/detailByJson", param, header, responseListener, List.class);
        } else {
            Login("TEST1", "地调局TEST1", "1234", new RequestUtil.OnResponseListener() {
                @Override
                public void onsuccess(Object o) {
                    Map<String, String> header = new HashMap<>();
                    header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
                    header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
                    Map<String, String> param = new HashMap<>();
                    param.put("id", id);
                    RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/detailByJson", param, header, responseListener, List.class);
                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG + "fieldPlanProject", "errorcode:" + code + "  message:" + message);
                }
            });
        }
    }


    /**
     * 提交/修改任务
     *
     * @param responseListener
     */
    public void submit(RequestUtil.OnResponseListener responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("projectId", "1");
            param.put("taskType", "测井");
            param.put("wellName", "JH002");
            param.put("location", "2000,2002");
            param.put("sitePhotos", "0XXX0X3");
            param.put("remark", "备注1111111");
            param.put("id", "1449219348257411074");

            RequestUtil.getInstance().requestRawHttp("http://121.36.58.193/blade-system/fieldInspectTask/submit", new Gson().toJson(param), header, responseListener, List.class);
        } else {
//            Login("TEST1", "地调局TEST1", "1234", new RequestUtil.OnResponseListener<UserInfo>() {
//                @Override
//                public void onsuccess(UserInfo o) {
//                    Map<String, String> header = new HashMap<>();
//                    header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
//                    header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
//                    Map<String, String> param = new HashMap<>();
//                    param.put("projectId", "1");
//                    param.put("taskType", "测井");
//                    param.put("wellName", "JH002");
//                    param.put("location", "2000,2002");
//                    param.put("sitePhotos", "0XXX0X3");
//                    param.put("remark", "备注1111111");
//                    param.put("id", "1449219348257411074");
//                    RequestUtil.getInstance().requestRawHttp("http://121.36.58.193/blade-system/fieldInspectTask/submit", new Gson().toJson(param), header, responseListener, List.class);
//                }
//
//                @Override
//                public void fail(String code, String message) {
//                    Log.e(TAG + "fieldPlanProject", "errorcode:" + code + "  message:" + message);
//                }
//            });
        }
    }


    /**
     * 删除任务
     *
     * @param responseListener
     */
    public void remove(RequestUtil.OnResponseListener responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("ids", "1449219348257411074,1452213081220673537");
            RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/remove", param, header, responseListener, List.class);
        } else {
//            Login("TEST1", "地调局TEST1", "1234", new RequestUtil.OnResponseListener<UserInfo>() {
//                @Override
//                public void onsuccess(UserInfo o) {
//                    Map<String, String> header = new HashMap<>();
//                    header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
//                    header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
//                    Map<String, String> param = new HashMap<>();
//                    param.put("eventId", "4");
//                    param.put("taskType", "测井");
//                    param.put("wellName", "JH002");
//                    param.put("location", "2000,2002");
//                    param.put("sitePhotos", "0XXX0X3");
//                    param.put("sitePhotos2", "0XXX0XX");
//                    param.put("sitePhotos3", "");
//                    RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/remove", param, header, responseListener, List.class);
//                }
//
//                @Override
//                public void fail(String code, String message) {
//                    Log.e(TAG + "fieldPlanProject", "errorcode:" + code + "  message:" + message);
//                }
//            });
        }
    }

    /**
     * 删除任务
     *
     * @param responseListener
     */
    public void updateFile(File file, RequestUtil.OnResponseListener responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
            header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("biz", "钻井");
            param.put("taskId", "1447820287457382403");
            param.put("groupType", "钻井");
            param.put("groupName", "钻井");
            param.put("fileIndicator", "p1");
            RequestUtil.getInstance().requestFileHttp("http://121.36.58.193/blade-system/fieldInspectTask/uploadFile", param, file, header, responseListener, List.class);
        } else {
//            Login("TEST1", "地调局TEST1", "1234", new RequestUtil.OnResponseListener<UserInfo>() {
//                @Override
//                public void onsuccess(UserInfo o) {
//                    Map<String, String> header = new HashMap<>();
//                    header.put("Authorization", "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==");
//                    header.put("X-Access-Token", BaseApplication.userInfo.getAccessToken());
//                    Map<String, String> param = new HashMap<>();
//                    param.put("eventId", "4");
//                    param.put("taskType", "测井");
//                    param.put("wellName", "JH002");
//                    param.put("location", "2000,2002");
//                    param.put("sitePhotos", "0XXX0X3");
//                    param.put("sitePhotos2", "0XXX0XX");
//                    param.put("sitePhotos3", "");
//                    RequestUtil.getInstance().requestHttp("http://121.36.58.193/blade-system/fieldInspectTask/remove", param, header, responseListener, List.class);
//                }
//
//                @Override
//                public void fail(String code, String message) {
//                    Log.e(TAG + "fieldPlanProject", "errorcode:" + code + "  message:" + message);
//                }
//            });
        }
    }
}
