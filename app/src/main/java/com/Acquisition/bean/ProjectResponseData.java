package com.Acquisition.bean;

public class ProjectResponseData extends ResponseDataList{

    private DataItem<ProjectBean> data;

    public DataItem<ProjectBean> getData() {
        return data;
    }

    public void setData(DataItem<ProjectBean> data) {
        this.data = data;
    }
}
