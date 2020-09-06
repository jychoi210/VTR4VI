package com.example.yoon.vtrproject.view;

import android.os.Environment;


import com.example.yoon.vtrproject.repo.SpeakCallback;
import com.example.yoon.vtrproject.repo.TTSRepository;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListPresenter implements ReportContract.ViewList {

    private ReportContract.ViewMain mView;
    private List<String> itemList = new ArrayList<>();
    private File file;


    FileListPresenter(ReportContract.ViewMain view){
        mView = view;
    }

    @Override
    public void initTTS(){
        TTSRepository.get().setCallback(callback);
    }

    @Override
    public void startSpeak(String text){
        TTSRepository.get().speakText(text);
    }


    @Override
    public void stopSpeak() {
        TTSRepository.get().stopSpeak();
    }

    private SpeakCallback callback = new SpeakCallback() {
        @Override
        public void onStart() {

        }

        @Override
        public void onCompleted() {

        }
    };

    @Override
    public void onList() {
        itemList = new ArrayList();

        String path = Environment.getExternalStorageDirectory().toString();
        file = new File(path+"/htmlData");
        File list[] = file.listFiles();

        for(int i = 0; i<list.length; i++){
            itemList.add(list[i].getName());
        }
        for(int i = 0; i <list.length; i++) {

            mView.addList(itemList.get(i));
        }

    }


    /*private ListListener listener = new ListListener() {
        @Override
        public void onList() {
            itemList = new ArrayList();

            String path = Environment.getExternalStorageDirectory().toString();
            file = new File(path+"/htmlData");
            File list[] = file.listFiles();

            for(int i = 0; i<list.length; i++){
                itemList.add(list[i].getName());
            }
            for(int i = 0; i <list.length; i++) {

                mView.addList(itemList.get(i));
            }

        }

        @Override
        public void onError() {
            mView.onError();
        }
    };*/


}
