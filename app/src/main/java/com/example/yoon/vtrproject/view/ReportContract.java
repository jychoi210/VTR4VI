package com.example.yoon.vtrproject.view;

import java.util.List;

public interface ReportContract {
    interface ViewMain {
        void addItem(int position, String tag, String strData);
        void onError();
        void addList(String filename);
    }

    interface PresenterMain {
        void parseData();
        void initTTS();
        void startSpeak(String text);
        void stopSpeak();
        String tagVerBodyText(int index);
    }

    interface ViewList {
        void onList();
        void initTTS();
        void startSpeak(String text);
        void stopSpeak();
    }

    interface ViewDetail {
        void addItem(String item);
        void speaking(int pos);
        void speakDone(int pos);
        void speakEnd();
    }

    interface PresenterDetail {
        int getCount(int index);
        List tagVerBodyText(int index);
        int getBodyText(String item);
        void loadBodyText();
        void initTTS();
        boolean isSpeaking();
        void startSpeak();
        void stopSpeak();
        void startSpeakJY(String text);
    }
}

