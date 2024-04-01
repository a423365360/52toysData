package com.service;

public interface FeishuCollector {
    void collect(String testFlag, String dt)  throws Exception;

    void update(String testFlag) throws Exception;

    void history(String testFlag, String start, String end) throws Exception;
}
