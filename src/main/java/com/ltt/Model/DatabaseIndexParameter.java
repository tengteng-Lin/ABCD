package com.ltt.Model;

import java.util.List;

public class DatabaseIndexParameter {
    private String name;
    private List<Integer> value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getValue() {
        return value;
    }

    public void setValue(List<Integer> value) {
        this.value = value;
    }
}
