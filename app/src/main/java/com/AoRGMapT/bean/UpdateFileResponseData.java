package com.AoRGMapT.bean;


import java.util.List;

public class UpdateFileResponseData extends ResponseDataItem {

    private FileData data;

    @Override
    public FileData getData() {
        return data;
    }

    public void setData(FileData data) {
        this.data = data;
    }

    public class FileData {
        private String path;
        private String bucketName;
        private String bucketType;
        private String bucketId;
        private List<File> files;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getBucketType() {
            return bucketType;
        }

        public void setBucketType(String bucketType) {
            this.bucketType = bucketType;
        }

        public String getBucketId() {
            return bucketId;
        }

        public void setBucketId(String bucketId) {
            this.bucketId = bucketId;
        }

        public List<File> getFiles() {
            return files;
        }

        public void setFiles(List<File> files) {
            this.files = files;
        }
    }


}
