package com.ltt.Model;

public class Pattern {
    private String uri;
    private Integer type;//0:output    1:input   2:count

    public Pattern() {
    }

    public Pattern(String uri, Integer type) {
        this.uri = uri;
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
