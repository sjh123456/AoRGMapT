package com.Acquisition.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestUtil<T> {

    private final static String TAG = "RequestUtil";

    //
    private static RequestUtil requestUtil;

    public static RequestUtil getInstance() {

        synchronized (RequestUtil.class) {
            if (requestUtil == null) {
                requestUtil = new RequestUtil();
            }
        }
        return requestUtil;
    }

    private OnResponseListener<T> mOnResponseListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (mOnResponseListener != null) {
                        String code = msg.getData().getString("code");
                        String message = msg.getData().getString("message");
                        mOnResponseListener.fail(code, message);
                    }

                } else if (msg.what == 2) {
                    if (mOnResponseListener != null) {
                        mOnResponseListener.onsuccess((T) msg.obj);
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    };


    public void requestHttp(String url, Map<String, String> params, Map<String, String> header, OnResponseListener<T> onResponseListener, Type type) {
        mOnResponseListener = onResponseListener;
        OkHttpClient okHttpClient = new OkHttpClient();
        //http://121.36.58.193/blade-system/v1/token?grantType=credible&account=TEST1&password=1234&userName=地调局TEST1&tenantId=100000
        //http://api.k780.com:88/?app=weather.future&weaid=1&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json

        //添加header信息
        Headers.Builder builder_header = new Headers.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            builder_header.add(k, v);
        }
        Headers headers = builder_header.build();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = builder.build();
        Request request1 = new Request.Builder()
                .url(url)
                .post(requestBody).headers(headers)
                .build();
        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
                Message message = new Message();
                message.what = 1;
                Bundle data = new Bundle();
                data.putString("code", "-1");
                data.putString("message", e.getMessage());
                message.setData(data);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String res = response.body().string();

                    Log.d("TAG", "————》" + res);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        T t = gson.fromJson(res, type);
                        Message message = new Message();
                        message.obj = t;
                        message.what = 2;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 1;
                        Bundle data = new Bundle();
                        data.putString("code", response.code() + "");
                        data.putString("message", response.message());
                        message.setData(data);
                        handler.sendMessage(message);
                    }
                } catch (Exception ex) {
                    Message message = new Message();
                    message.what = 1;
                    Bundle data = new Bundle();
                    data.putString("code", "-1");
                    data.putString("message", ex.getMessage());
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void requestFileHttp(String url, Map<String, String> params, List<File> files, Map<String, String> header, OnResponseListener<T> onResponseListener, Type type) {
        this.mOnResponseListener = onResponseListener;
        OkHttpClient okHttpClient = new OkHttpClient();
        //添加header信息
        Headers.Builder builder_header = new Headers.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            builder_header.add(k, v);
        }
        Headers headers = builder_header.build();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (File file : files) {
            builder.addFormDataPart(file.getName(), file.getName(),
                    RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
        RequestBody requestBody = builder.build();
        Request request1 = new Request.Builder()
                .url(url)
                .post(requestBody).headers(headers)
                .build();
        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
                Message message = new Message();
                message.what = 1;
                Bundle data = new Bundle();
                data.putString("code", "-1");
                data.putString("message", e.getMessage());
                message.setData(data);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("TAG", "————》" + res);
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    T t = gson.fromJson(res, type);
                    Message message = new Message();
                    message.obj = t;
                    message.what = 2;
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = 1;
                    Bundle data = new Bundle();
                    data.putString("code", response.code() + "");
                    data.putString("message", response.message());
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        });
    }


    public void requestRawHttp(String url, String json, Map<String, String> header, OnResponseListener<T> onResponseListener, Type type) {
        OkHttpClient okHttpClient = new OkHttpClient();

        this.mOnResponseListener = onResponseListener;

        //添加header信息
        Headers.Builder builder_header = new Headers.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            builder_header.add(k, v);
        }
        Headers headers = builder_header.build();


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request1 = new Request.Builder()
                .url(url)
                .post(requestBody).headers(headers)
                .build();
        okHttpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());

                Message message = new Message();
                message.what = 1;
                Bundle data = new Bundle();
                data.putString("code", "-1");
                data.putString("message", e.getMessage());
                message.setData(data);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("TAG", "————》" + res);
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    T t = gson.fromJson(res, type);
                    Message message = new Message();
                    message.obj = t;
                    message.what = 2;
                    handler.sendMessage(message);
                } else {

                    Message message = new Message();
                    message.what = 1;
                    Bundle data = new Bundle();
                    data.putString("code", response.code() + "");
                    data.putString("message", response.message());
                    message.setData(data);
                    handler.sendMessage(message);
                }
            }
        });
    }

    public interface OnResponseListener<T> {
        void onsuccess(T t);

        void fail(String code, String message);
    }

    class ResponseObject implements Serializable {
        private int code;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        private boolean success;
        private String data;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public ResponseObject(int code, boolean success, String data, String msg) {
            this.code = code;
            this.success = success;
            this.data = data;
            this.msg = msg;
        }
    }


    private class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
