package com.example.yoon.vtrproject.view;

import android.util.Log;

import com.example.yoon.vtrproject.repo.DataRepository;
import com.example.yoon.vtrproject.repo.SpeakCallback;
import com.example.yoon.vtrproject.repo.TTSRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableCellPresenter implements ReportContract.PresenterDetail {
    private ReportContract.ViewDetail mView;
    private List<String> bodyTextItem = new ArrayList<>();

    private int currentPosition = 0;

    @Override
    public int getCount(int index) {

        String bodyText = DataRepository.get().getBodyText(index);
        Log.d("bodyText",bodyText);
        bodyText = bodyText.replace("<tbody>","");
        bodyText = bodyText.replace("</tbody>","");
        bodyText = bodyText.replace("<tr>","");
        bodyText = bodyText.replace("</tr>","");
        bodyText = bodyText.replace("<td>","");
        //bodyText = bodyText.replace("</td>","");
        if (bodyText != null) {
            String[] split = bodyText.trim().split("</td>");
            bodyTextItem = Arrays.asList(split);
            return bodyTextItem.size();
        } else {
            return -1;
        }
    }

    @Override
    public List tagVerBodyText(int index) {
        return null;
    }

    @Override
    public int getBodyText(String item) {
        String bodyText = item;
        Log.d("bodyText+presenter",bodyText);
        bodyText = bodyText.replace("<td>","");
        if (bodyText != null) {
            String[] split = bodyText.trim().split("</td>");
            bodyTextItem = Arrays.asList(split);
            return bodyTextItem.size();
        } else {
            return -1;
        }
    }

    @Override
    public void loadBodyText() {
        for (String item : bodyTextItem) {
            mView.addItem(item);
        }
    }

    @Override
    public void initTTS() {
        TTSRepository.get().setCallback(callback);
    }

    @Override
    public boolean isSpeaking() {
        return TTSRepository.get().isSpeaking();
    }




    @Override
    public void startSpeak() {
        if (currentPosition < bodyTextItem.size()) {
            TTSRepository.get().speakText(bodyTextItem.get(currentPosition));
        } else {
            mView.speakEnd();
        }
    }

    //    @Override
    public void startSpeakJY(String text){
        TTSRepository.get().speakText(text);
    }

    @Override
    public void stopSpeak() {
        TTSRepository.get().stopSpeak();
    }

    private SpeakCallback callback = new SpeakCallback() {
        @Override
        public void onStart() {

            //계속 말하는 것
            //mView.speaking(currentPosition);
        }

        @Override
        public void onCompleted() {
            //계속 말하는 것
            //mView.speakDone(currentPosition);

            //list 모두 말하는 것
            //currentPosition++;
            //startSpeak();
        }
    };

    TableCellPresenter(ReportContract.ViewDetail view) {
        mView = view;
    }

}



