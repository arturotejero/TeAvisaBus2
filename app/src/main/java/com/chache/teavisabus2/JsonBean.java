package com.chache.teavisabus2;

/**
 * Created by prest079 on 01/06/2016.
 */
public class JsonBean {

    private String line;
    private String name;
    private int node;

    public int getSecDetail() {
        return secDetail;
    }

    public void setSecDetail(int secDetail) {
        this.secDetail = secDetail;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    private int secDetail;
}
