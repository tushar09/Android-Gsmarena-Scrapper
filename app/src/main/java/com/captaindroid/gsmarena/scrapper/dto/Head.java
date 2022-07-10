package com.captaindroid.gsmarena.scrapper.dto;

import java.util.ArrayList;

public class Head {
    private String name;
    private ArrayList<SubHeader> subHeaders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SubHeader> getSubHeaders() {
        return subHeaders;
    }

    public void setSubHeaders(ArrayList<SubHeader> subHeaders) {
        this.subHeaders = subHeaders;
    }
}
