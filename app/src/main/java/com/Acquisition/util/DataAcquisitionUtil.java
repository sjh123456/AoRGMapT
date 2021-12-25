package com.Acquisition.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.Acquisition.BaseApplication;
import com.Acquisition.bean.ImageBean;
import com.Acquisition.bean.PlanBean;
import com.Acquisition.bean.PlanResponseData;
import com.Acquisition.bean.ProjectResponseData;
import com.Acquisition.bean.ResponseDataItem;
import com.Acquisition.bean.StatisticsProjectResponseData;
import com.Acquisition.bean.TaskListResponseData;
import com.Acquisition.bean.UpdateFileResponseData;
import com.Acquisition.bean.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAcquisitionUtil {


    private static DataAcquisitionUtil util;
    // 账号信息
    private String account = "TEST1";
    private String name = "地调局TEST1";
    private String password = "1234";
    private final String authorization = "Basic YXVpX2NyZWRpYmxlXzAxOmF1aV9jcmVkaWJsZV9zZWNyZXQwMQ==";
    private final String AUTHORIZATION = "Authorization";
    private final String XACCESSTOKEN = "X-Access-Token";
    private final String BASEURL = "http://219.142.81.65:8081/blade-system";

    private String TAG = "DataAcquisitionUtil";

    public static DataAcquisitionUtil getInstance() {
        synchronized (DataAcquisitionUtil.class) {
            if (util == null) {
                util = new DataAcquisitionUtil();
            }
        }
        return util;
    }


    public void getShareUserInfo(Context context) {
        try {
            String szAuthority = "com.drc.bulkpackage.LoginContentProvider";
            Uri uri = Uri.parse("content://" + szAuthority + "/LoginInfo");
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor == null) {
                return;
            } else {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    name = cursor.getString(cursor.getColumnIndex("NAME"));
                    account = name;
                    password = cursor.getString(cursor.getColumnIndex("PSW"));
                }
                cursor.close();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

    }


    /**
     * 登陆接口
     *
     * @param account          账号
     * @param name             账号名
     * @param password         密码
     * @param responseListener
     */
    public void Login(String account, String name, String
            password, RequestUtil.OnResponseListener<ResponseDataItem<UserInfo>> responseListener) {

        Map<String, String> header = new HashMap<>();
        header.put("User-Type", "app");
        header.put(AUTHORIZATION, authorization);

        Map<String, String> param = new HashMap<>();
        param.put("grantType", "credible");
        param.put("account", account);
        param.put("password", password);
        param.put("userName", name);
        param.put("tenantId", "100000");
        RequestUtil.getInstance().requestHttp(BASEURL + "/v1/token", param, header, responseListener, new TypeToken<ResponseDataItem<UserInfo>>() {
        }.getType());

    }


    /**
     * 获取项目列表
     *
     * @param responseListener
     */
    public void fieldPlanProject(int pageSize, int current, RequestUtil.
            OnResponseListener<ProjectResponseData> responseListener) {


        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {

            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("pageSize", pageSize + "");
            param.put("current", current + "");
            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldPlanProject/page",
                    param,
                    header,
                    responseListener, new TypeToken<ProjectResponseData>() {
                    }.getType());
        } else {
            Login(account, name, password, new RequestUtil.OnResponseListener<ResponseDataItem<UserInfo>>() {
                @Override
                public void onsuccess(ResponseDataItem<UserInfo> obj) {
                    if (obj != null) {
                        UserInfo userInfo = obj.getData();
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getAccessToken())) {
                            //更新用户信息
                            BaseApplication.userInfo = userInfo;
                            Map<String, String> header = new HashMap<>();
                            header.put(AUTHORIZATION, authorization);
                            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
                            Map<String, String> param = new HashMap<>();
                            param.put("pageSize", pageSize + "");
                            param.put("current", current + "");
                            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldPlanProject/page",
                                    param,
                                    header,
                                    responseListener, new TypeToken<ProjectResponseData>() {
                                    }.getType());
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
    public void detailPageByJson(String projectId, int pageSize, int current, RequestUtil.
            OnResponseListener<PlanResponseData> responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("projectId", projectId);
            param.put("column", "recordDate");
            param.put("pageSize", pageSize + "");
            param.put("current", current + "");
            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldInspectTask/detailPageByJson",
                    param, header, responseListener,
                    new TypeToken<PlanResponseData>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }


    /**
     * 获取任务详情
     *
     * @param responseListener
     */
    public void detailByJson(String
                                     id, RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>> responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("id", id);
            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldInspectTask/detailByJson",
                    param, header, responseListener,
                    new TypeToken<ResponseDataItem<PlanBean>>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }


    /**
     * 提交/修改任务
     *
     * @param responseListener
     */
    public void submit
    (Map<String, Object> param, RequestUtil.OnResponseListener<ResponseDataItem<PlanBean>> responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            RequestUtil.getInstance().requestRawHttp(BASEURL + "/fieldInspectTask/submit",
                    new Gson().toJson(param), header, responseListener,
                    new TypeToken<ResponseDataItem<PlanBean>>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }


    /**
     * 删除任务
     *
     * @param responseListener
     */
    public void remove(String
                               ids, RequestUtil.OnResponseListener<ResponseDataItem> responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("ids", ids);
            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldInspectTask/remove",
                    param, header, responseListener,
                    new TypeToken<ResponseDataItem>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }

    /**
     * 更新文件
     *
     * @param responseListener
     */
    public void updateFile
    (List<ImageBean> imageBeans, Map<String, String> param, RequestUtil.OnResponseListener<ResponseDataItem<UpdateFileResponseData.FileData>> responseListener) {
        List<File> files = new ArrayList<>();
        if (imageBeans != null) {
            for (int i = 0; i < imageBeans.size(); i++) {
                ImageBean imageBean = imageBeans.get(i);
                if (!TextUtils.isEmpty(imageBean.getImagePath())) {
                    files.add(new File(imageBean.getImagePath()));
                }
            }
        }
        if (files != null && files.size() > 0 && BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            RequestUtil.getInstance().requestFileHttp(BASEURL + "/fieldInspectTask/appendFile",
                    param, files, header, responseListener
                    , new TypeToken<ResponseDataItem<UpdateFileResponseData.FileData>>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }

    /**
     * 删除文件
     *
     * @param responseListener
     */
    public void deleteFile(String taskId, String
            fileIds, RequestUtil.OnResponseListener<ResponseDataItem> responseListener) {
        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("taskId", taskId);
            param.put("fileIds", fileIds);
            RequestUtil.getInstance().requestHttp(BASEURL + "//fieldInspectTask/deleteFile",
                    param, header, responseListener
                    , new TypeToken<ResponseDataItem>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }


    /**
     * 获取项目列表
     *
     * @param responseListener
     */
    public void getBoardTasksList(int pageSize, int current, RequestUtil.
            OnResponseListener<StatisticsProjectResponseData> responseListener) {


        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {

            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("pageSize", pageSize + "");
            param.put("current", current + "");
            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldPlanProject/getBoardTasksList",
                    param,
                    header,
                    responseListener, new TypeToken<StatisticsProjectResponseData>() {
                    }.getType());
        } else {
            Login(account, name, password, new RequestUtil.OnResponseListener<ResponseDataItem<UserInfo>>() {
                @Override
                public void onsuccess(ResponseDataItem<UserInfo> obj) {
                    if (obj != null) {
                        UserInfo userInfo = obj.getData();
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getAccessToken())) {
                            //更新用户信息
                            BaseApplication.userInfo = userInfo;
                            Map<String, String> header = new HashMap<>();
                            header.put(AUTHORIZATION, authorization);
                            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
                            Map<String, String> param = new HashMap<>();
                            param.put("pageSize", pageSize + "");
                            param.put("current", current + "");
                            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldPlanProject/getBoardTasksList",
                                    param,
                                    header,
                                    responseListener, new TypeToken<StatisticsProjectResponseData>() {
                                    }.getType());
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
     * 获取任务详情
     *
     * @param responseListener
     */
    public void getTasksList(String
                                     projectId, RequestUtil.OnResponseListener<TaskListResponseData> responseListener) {

        if (BaseApplication.userInfo != null && !TextUtils.isEmpty(BaseApplication.userInfo.getAccessToken())) {
            Map<String, String> header = new HashMap<>();
            header.put(AUTHORIZATION, authorization);
            header.put(XACCESSTOKEN, BaseApplication.userInfo.getAccessToken());
            Map<String, String> param = new HashMap<>();
            param.put("projectId", projectId);
            RequestUtil.getInstance().requestHttp(BASEURL + "/fieldPlanProject/getTasksList",
                    param, header, responseListener,
                    new TypeToken<TaskListResponseData>() {
                    }.getType());
        } else {
            Log.e(TAG, "请先登陆账号");
        }
    }

}
