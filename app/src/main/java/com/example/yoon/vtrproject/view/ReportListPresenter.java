package com.example.yoon.vtrproject.view;

import android.util.Log;

import com.example.yoon.vtrproject.repo.DataRepository;
import com.example.yoon.vtrproject.repo.SpeakCallback;
import com.example.yoon.vtrproject.repo.TTSRepository;

import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportListPresenter implements ReportContract.PresenterMain {
    private ReportContract.ViewMain mView;
    private List<Node> itemList = new ArrayList<>();
    private List<String> bodyTextItem = new ArrayList<>();
    private Map<String, String> matcher = new HashMap<>();
    String fn;


    public interface LoadListener {
        void onLoad();
        void onError();
    }

    ReportListPresenter(ReportContract.ViewMain view,String filename) {
        mView = view;
        buildTagMap();
        fn = filename;
    }

    private void buildTagMap() {
        matcher.put("title", "파일이름");
        matcher.put("h1", "제목");
        matcher.put("h2", "챕터");
        matcher.put("h3", "섹션");
        matcher.put("p", "문단");
        matcher.put("br", "라인");
        matcher.put("table","표");
    }

    @Override
    public void parseData() {
        Log.d("fn",fn);
        DataRepository.get().load(listener,fn);
    }

    @Override
    public void initTTS() {
        TTSRepository.get().setCallback(callback);
    }

    @Override
    public void startSpeak(String text) {
        TTSRepository.get().speakTag(text);
    }

    @Override
    public void stopSpeak() { TTSRepository.get().stopSpeak();}


    private SpeakCallback callback = new SpeakCallback() {
        @Override
        public void onStart() {
        }

        @Override
        public void onCompleted() {
            // Ignore
        }
    };

    @Override
    public String tagVerBodyText(int index) {
        itemList = DataRepository.get().getOverview();
        int i = 0;
        for (Node item : itemList) {
            if (item.nodeName() == "table") {
                String s = item.childNode(0).toString();
                s = s.replace("\n", "");
                s = s.replace("<tbody>", "");
                s = s.replace("</tbody>", "");
                return s;
            }
        }
        return null;
    }

    private LoadListener listener = new LoadListener() {
        @Override
        public void onLoad() {

            itemList = DataRepository.get().getOverview();
            int i = 0;
            for (Node item : itemList) {
                if(item.nodeName()=="table"){
                    String s = item.childNode(0).toString();
                    s = s.replace("\n","");
                    s = s.replace("<tbody>","");
                    s = s.replace("<tr>","행 시작");
                    s = s.replace("<td>","");
                    s = s.replace("</tbody>","");
                    s = s.replace("</tr>","행 끝");
                    s = s.replace("</td>","");
                    Log.d("table string",s);
                    mView.addItem(i, matcher.get(item.nodeName()), s);
                }
                else{mView.addItem(i, matcher.get(item.nodeName()), item.childNode(0).toString());}
                i++;
            }
        }

        @Override
        public void onError() {
            mView.onError();
        }
    };
}
