package com.captaindroid.gsmarena.scrapper.dto;

import java.util.ArrayList;

public class SubHeader {
    private String name;
    private ArrayList<String> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }
}
