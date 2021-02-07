package com.ltt.Model;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Test {
    private List<JSONObject> pattern;

    public Test() {
    }

    public Test(List<JSONObject> pattern) {
        this.pattern = pattern;
    }

    public List<JSONObject> getPattern() {
        return pattern;
    }

    public void setPattern(List<JSONObject> pattern) {
        this.pattern = pattern;
    }
}
