package com.example.yoon.vtrproject;

import android.app.Application;

public class MyApplication extends Application {

    private String mtag = "first";
    public String getTag() { return mtag; }
    public void setTag(String tag) { this.mtag = tag; }

    private String mGlobalString;
    public String getGlobalString()
    {
        return mGlobalString;
    }
    public void setGlobalString(String globalString)
    {
        this.mGlobalString = globalString;
    }

    private int mchapter = 0;
    public int getChapter() { return mchapter; }
    public void setChapter(int chapter) { this.mchapter = chapter;}

    private int msection = 0;
    public int getSection() { return msection; }
    public void setSection(int section) { this.msection = section; }

    private int mcurrent = -1;
    public int getCurrent() { return mcurrent; }
    public void setCurrent(int current) { this.mcurrent = current; }

    private String mcurrentText;
    public String getCurrentText() { return mcurrentText; }
    public void setCurrentText(String currentText) { this.mcurrentText = currentText; }

    private String mpreText;
    public String getPreText() { return mpreText; }
    public void setPreText(String preText) { this.mpreText = preText; }

    private int mselectedIndex;
    public int getselectedIndex() { return mselectedIndex; }
    public void setselectedIndex(int selectedIndex) { this.mselectedIndex = selectedIndex; }

}
