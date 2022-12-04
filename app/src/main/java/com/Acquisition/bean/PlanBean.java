package com.Acquisition.bean;

import java.util.List;

public class PlanBean {

    private boolean complete;

    public PlanBean(boolean complete) {
        this.complete = complete;
    }

    public PlanBean() {
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }


    private String recorder;
    private String extendData;
    private String updateUser;
    private String remark;
    private String updateTime;
    private String taskType;
    private int isDeleted;
    private String createTime;
    private String sitePhotos;
    private String recordDate;
    private String createUser;
    private String location;
    private String sitePhotos3;
    private String wellName;
    private String id;
    private String sitePhotos2;
    private String sitePhotos5;
    private String projectId;
    private String sitePhotos4;
    private String sitePhotos6;
    private int status;
    private List<PhotoFile> files;
    private List<PhotoFile> files2;
    private List<PhotoFile> files3;
    private List<PhotoFile> files4;
    private List<PhotoFile> files5;
    private List<PhotoFile> files6;
    private int key;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public List<PhotoFile> getFiles() {
        return files;
    }

    public void setFiles(List<PhotoFile> files) {
        this.files = files;
    }

    public List<PhotoFile> getFiles2() {
        return files2;
    }

    public void setFiles2(List<PhotoFile> files2) {
        this.files2 = files2;
    }

    public List<PhotoFile> getFiles3() {
        return files3;
    }

    public void setFiles3(List<PhotoFile> files3) {
        this.files3 = files3;
    }

    public List<PhotoFile> getFiles4() {
        return files4;
    }

    public void setFiles4(List<PhotoFile> files4) {
        this.files4 = files4;
    }

    public List<PhotoFile> getFiles5() {
        return files5;
    }

    public void setFiles5(List<PhotoFile> files5) {
        this.files5 = files5;
    }

    public List<PhotoFile> getFiles6() {
        return files6;
    }

    public void setFiles6(List<PhotoFile> files6) {
        this.files6 = files6;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public String getExtendData() {
        return extendData;
    }

    public void setExtendData(String extendData) {
        this.extendData = extendData;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSitePhotos() {
        return sitePhotos;
    }

    public void setSitePhotos(String sitePhotos) {
        this.sitePhotos = sitePhotos;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSitePhotos3() {
        return sitePhotos3;
    }

    public void setSitePhotos3(String sitePhotos3) {
        this.sitePhotos3 = sitePhotos3;
    }

    public String getWellName() {
        return wellName;
    }

    public void setWellName(String wellName) {
        this.wellName = wellName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSitePhotos2() {
        return sitePhotos2;
    }

    public void setSitePhotos2(String sitePhotos2) {
        this.sitePhotos2 = sitePhotos2;
    }

    public String getSitePhotos5() {
        return sitePhotos5;
    }

    public void setSitePhotos5(String sitePhotos5) {
        this.sitePhotos5 = sitePhotos5;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSitePhotos4() {
        return sitePhotos4;
    }

    public void setSitePhotos4(String sitePhotos4) {
        this.sitePhotos4 = sitePhotos4;
    }

    public String getSitePhotos6() {
        return sitePhotos6;
    }

    public void setSitePhotos6(String sitePhotos6) {
        this.sitePhotos6 = sitePhotos6;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public class PhotoFile {
        private String fileName;
        private String extendData;
        private String filePath;
        private Long updateUser;
        private String bucketId;
        private String remark;
        private String updateTime;
        private String source;
        private String expiryDate;
        private int sortNo;
        private int isDeleted;
        private String createTime;
        private Long fileSize;
        private String fileExtension;
        private Long createUser;
        private String id;
        private String fileType;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getExtendData() {
            return extendData;
        }

        public void setExtendData(String extendData) {
            this.extendData = extendData;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Long getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(Long updateUser) {
            this.updateUser = updateUser;
        }

        public String getBucketId() {
            return bucketId;
        }

        public void setBucketId(String bucketId) {
            this.bucketId = bucketId;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public int getSortNo() {
            return sortNo;
        }

        public void setSortNo(int sortNo) {
            this.sortNo = sortNo;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(int isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public Long getCreateUser() {
            return createUser;
        }

        public void setCreateUser(Long createUser) {
            this.createUser = createUser;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
    }
}
