package com.ltt.Model;



public class Block {
    private Integer doc_id;
    private String title;
    private String repository;
    private String format;
    private String time;

    private String dataset_local_id;

    public Block(Integer doc_id, String title, String repository, String format, String time, String dataset_local_id) {
        this.doc_id = doc_id;
        this.title = title;
        this.repository = repository;
        this.format = format;
        this.time = time;
        this.dataset_local_id = dataset_local_id;
    }

    public Block(String title, String repository, String format, String time, String dataset_local_id) {
        this.title = title;
        this.repository = repository;
        this.format = format;
        this.time = time;
        this.dataset_local_id = dataset_local_id;
    }

    public Integer getId() {
        return doc_id;
    }

    public void setId(Integer id) {
        this.doc_id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer doc_id) {
        this.doc_id = doc_id;
    }

    public String getDataset_local_id() {
        return dataset_local_id;
    }

    public void setDataset_local_id(String dataset_local_id) {
        this.dataset_local_id = dataset_local_id;
    }
}
