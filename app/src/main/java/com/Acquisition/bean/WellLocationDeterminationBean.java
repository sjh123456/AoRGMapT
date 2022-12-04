package com.Acquisition.bean;

public class WellLocationDeterminationBean {

    //海拔
    private String altitude;
    //x
    private String x;
    //y
    private String y;
    //构造位置
    private String structural_location;
    //行政区
    private String administrative_region;
    //交通情况
    private String traffic;
    //地理情况
    private String geographical_situation;
    //居民点情况
    private String residential_area;

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getStructural_location() {
        return structural_location;
    }

    public void setStructural_location(String structural_location) {
        this.structural_location = structural_location;
    }

    public String getAdministrative_region() {
        return administrative_region;
    }

    public void setAdministrative_region(String administrative_region) {
        this.administrative_region = administrative_region;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getGeographical_situation() {
        return geographical_situation;
    }

    public void setGeographical_situation(String geographical_situation) {
        this.geographical_situation = geographical_situation;
    }

    public String getResidential_area() {
        return residential_area;
    }

    public void setResidential_area(String residential_area) {
        this.residential_area = residential_area;
    }
}
