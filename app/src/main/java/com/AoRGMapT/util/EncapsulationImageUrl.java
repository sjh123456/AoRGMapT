package com.AoRGMapT.util;

import android.util.Log;

import com.AoRGMapT.BaseApplication;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ResponseDataItem;
import com.AoRGMapT.bean.UpdateFileResponseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncapsulationImageUrl {

    private final static String TAG = "EncapsulationImageUrl";

    public static String encapsulation(String srcId) {

        String imageurl = "http://121.36.58.193/blade-system/fieldInspectTask/downloadFileBySrcId?srcId=" + srcId + "&&X-Access-Token=" + BaseApplication.userInfo.getAccessToken();

        return imageurl;
    }


    //上传图片信息
    public static void updatePhotos(String taskid, String name, String fileIndicator, List<ImageBean> imageBeans, List<PlanBean.PhotoFile> photoFiles) {

        String bucketId = "";
        if (photoFiles != null && photoFiles.size() > 0) {
            bucketId = photoFiles.get(0).getBucketId();
        }

        Map<String, String> param = new HashMap<>();
        param.put("biz", name);
        param.put("taskId", taskid);
        param.put("bucketName", name);
        param.put("bucketType", name);
        param.put("bucketId", bucketId);
        param.put("fileIndicator", fileIndicator);
        DataAcquisitionUtil.getInstance().updateFile(imageBeans, param, new RequestUtil.OnResponseListener<ResponseDataItem<UpdateFileResponseData.FileData>>() {
            @Override
            public void onsuccess(ResponseDataItem<UpdateFileResponseData.FileData> fileDataResponseDataItem) {
                if (fileDataResponseDataItem.isSuccess()) {
                    Log.e(TAG, fileIndicator + "图片提交成功");
                } else {
                    Log.e(TAG, fileIndicator + "图片提交失败");
                }
            }

            @Override
            public void fail(String code, String message) {
                Log.e(TAG, "图片提交失败");
            }
        });
    }


    public static void deletePhotoFile(String taskid, List<String> deleteImageList) {

        if (deleteImageList != null && deleteImageList.size() > 0) {
            String fileids = "";
            for (int i = 0; i < deleteImageList.size(); i++) {
                if (i == deleteImageList.size() - 1) {
                    fileids = fileids + deleteImageList.get(i);
                } else {
                    fileids = fileids + deleteImageList.get(i) + ",";
                }

            }

            DataAcquisitionUtil.getInstance().deleteFile(taskid, fileids, new RequestUtil.OnResponseListener<ResponseDataItem>() {
                @Override
                public void onsuccess(ResponseDataItem responseDataItem) {
                    if (responseDataItem.isSuccess()) {
                        Log.e(TAG, "删除文件成功");
                    } else {
                        Log.e(TAG, "删除文件失败");
                    }

                }

                @Override
                public void fail(String code, String message) {
                    Log.e(TAG, "删除文件失败");
                }
            });
        }
    }

}
