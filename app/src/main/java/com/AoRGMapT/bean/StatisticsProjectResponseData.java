package com.AoRGMapT.bean;

import java.util.List;

public class StatisticsProjectResponseData extends ResponseDataList{

    private List<ProjectBean> data;

    public List<ProjectBean> getData() {
        return data;
    }

    public void setData(List<ProjectBean> data) {
        this.data = data;
    }
}
