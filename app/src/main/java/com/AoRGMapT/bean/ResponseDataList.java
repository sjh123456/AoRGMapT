package com.AoRGMapT.bean;

import java.util.List;

public class ResponseDataList {


   /* {
        "code": 200,
            "success": true,
            "data": {
        "records": [
        {
            "createUser": null,
                "createTime": "",
                "updateUser": null,
                "updateTime": "",
                "isDeleted": 0,
                "status": 1,
                "id": "1",
                "year": "2021",
                "projectName": "测试项目001",
                "projectGroup": "",
                "projectLeader": "",
                "defaultWellName": "JH001",
                "remark": "",
                "recorder": "",
                "recordDate": ""
        }
        ],
        "total": 4,
                "size": 1,
                "current": 1,
                "orders": [],
        "optimizeCountSql": true,
                "hitCount": false,
                "countId": "",
                "maxLimit": null,
                "searchCount": true,
                "pages": 4
    },
        "msg": "操作成功"
    }*/


    private int code;
    private boolean success;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }



    public class DataItem<E> {

        private List<E> records;
        private int total;
        private int size;
        private int current;
        private List<Object> orders;
        private boolean optimizeCountSql;
        private boolean hitCount;
        private String countId;
        private String maxLimit;
        private boolean searchCount;
        private int pages;

        public List<E> getRecords() {
            return records;
        }

        public void setRecords(List<E> records) {
            this.records = records;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public List<Object> getOrders() {
            return orders;
        }

        public void setOrders(List<Object> orders) {
            this.orders = orders;
        }

        public boolean isOptimizeCountSql() {
            return optimizeCountSql;
        }

        public void setOptimizeCountSql(boolean optimizeCountSql) {
            this.optimizeCountSql = optimizeCountSql;
        }

        public boolean isHitCount() {
            return hitCount;
        }

        public void setHitCount(boolean hitCount) {
            this.hitCount = hitCount;
        }

        public String getCountId() {
            return countId;
        }

        public void setCountId(String countId) {
            this.countId = countId;
        }

        public String getMaxLimit() {
            return maxLimit;
        }

        public void setMaxLimit(String maxLimit) {
            this.maxLimit = maxLimit;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }
    }

}
