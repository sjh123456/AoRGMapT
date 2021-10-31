package com.AoRGMapT.bean;

import android.text.TextUtils;

import com.AoRGMapT.BaseApplication;

public class ProjectBean {

    private int completedNum;
    private int imcompletedNum;

    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
    private int isDeleted;
    private int status;
    private String id;
    private String year;
    private String projectName;
    private String projectGroup;
    private String projectLeader;
    private String defaultWellName;
    private String remark;
    private String recorder;
    private String recordDate;
    private String projectId;
    private int taskCount;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        if (TextUtils.isEmpty(this.id)) {
            return projectId;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(String projectLeader) {
        this.projectLeader = projectLeader;
    }

    public String getDefaultWellName() {

        return defaultWellName;
    }

    public void setDefaultWellName(String defaultWellName) {
        this.defaultWellName = defaultWellName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public ProjectBean(int completedNum, int imcompletedNum) {
        this.completedNum = completedNum;
        this.imcompletedNum = imcompletedNum;
    }


    public int getCompletedNum() {
        return completedNum;
    }

    public void setCompletedNum(int completedNum) {
        this.completedNum = completedNum;
    }

    public int getImcompletedNum() {
        return imcompletedNum;
    }

    public void setImcompletedNum(int imcompletedNum) {
        this.imcompletedNum = imcompletedNum;
    }
}
