package com.AoRGMapT.bean;

public class PlanResponseData extends ResponseDataList{

    private DataItem<PlanBean> data;

    public DataItem<PlanBean> getData() {
        return data;
    }

    public void setData(DataItem<PlanBean> data) {
        this.data = data;
    }
}
