package com.ltt.Model.Statistics;

public class ClassCount {
    private int class_id;
    private int count;
    private int dataset_local_id;

    public ClassCount(int class_id, int count, int dataset_local_id) {
        this.class_id = class_id;
        this.count = count;
        this.dataset_local_id = dataset_local_id;
    }

    public ClassCount(int class_id, int dataset_local_id) {
        this.class_id = class_id;
        this.dataset_local_id = dataset_local_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDataset_local_id() {
        return dataset_local_id;
    }

    public void setDataset_local_id(int dataset_local_id) {
        this.dataset_local_id = dataset_local_id;
    }
}
