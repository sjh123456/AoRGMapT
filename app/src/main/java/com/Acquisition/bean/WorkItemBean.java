package com.Acquisition.bean;

public class WorkItemBean {


    private String name;
    private Integer png;

    public WorkItemBean(String name, Integer png) {
        this.name = name;
        this.png = png;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPng() {
        return png;
    }

    public void setPng(Integer png) {
        this.png = png;
    }

}
