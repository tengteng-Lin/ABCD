package com.ltt.Model;

import java.util.List;

public class ReturnOne {
    private String sessionId;
    private List<Block> resultList;

    public ReturnOne(String sessionId, List<Block> resultList) {
        this.sessionId = sessionId;
        this.resultList = resultList;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Block> getResultList() {
        return resultList;
    }

    public void setResultList(List<Block> resultList) {
        this.resultList = resultList;
    }
}
