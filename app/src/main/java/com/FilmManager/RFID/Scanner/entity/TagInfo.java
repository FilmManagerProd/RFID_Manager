package com.FilmManager.RFID.Scanner.entity;

public class TagInfo {
    private int index;
    private boolean errorTag;
    private String epc;
    private int count;
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public boolean isErrorTag() {
        return errorTag;
    }
    public void setErrorTag(boolean errorTag) {
        this.errorTag = errorTag;
    }
    public String getEpc() {
        return epc;
    }
    public void setEpc(String epc) {
        this.epc = epc;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}