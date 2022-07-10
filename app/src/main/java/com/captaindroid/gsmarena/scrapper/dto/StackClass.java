package com.captaindroid.gsmarena.scrapper.dto;

public class StackClass {
    private String data;
    private int type;

    public StackClass(String data, int type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
