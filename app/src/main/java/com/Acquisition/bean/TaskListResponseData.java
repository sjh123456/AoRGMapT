package com.Acquisition.bean;

import java.util.List;

public class TaskListResponseData extends ResponseDataList{

    private List<TaskInfo> data;

    public List<TaskInfo> getData() {
        return data;
    }

    public void setData(List<TaskInfo> data) {
        this.data = data;
    }

    public class TaskInfo{
        private String taskType;
        private int taskCount;

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public int getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(int taskCount) {
            this.taskCount = taskCount;
        }
    }
}
