package com.example.yoon.vtrproject.view;

public class ReportListItem {
    private String tagData;
    private String strData;

    public void setData(String tag, String data) {
        tagData = tag;
        strData = data;
    }

    public String getTagData() { return this.tagData; }

    public String getStrData() {
        return this.strData;
    }
}

