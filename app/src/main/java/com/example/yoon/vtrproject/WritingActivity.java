package com.example.yoon.vtrproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoon.vtrproject.view.FileListActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class WritingActivity extends AppCompatActivity {

    Intent intent,intent1;
    SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    Button commBtn, dataBtn,editBtn,listBtn;
    TextView textView;
    final int PERMISSION = 1;
    private String filename;
    Beeper ddock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writing);


        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        textView = (TextView)findViewById(R.id.sttResult);
        dataBtn = (Button) findViewById(R.id.sttData);
        commBtn = (Button) findViewById(R.id.sttComm);
        editBtn = (Button) findViewById(R.id.edit);

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        commBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);
            }
        });


        dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer.setRecognitionListener(listener2);
                mRecognizer.startListening(intent);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), editActivity.class);
                startActivity(intent1);
            }
        });


        Log.d("directory",""+ Environment.getExternalStorageDirectory());
        MyApplication myApp = (MyApplication) getApplication();

        Intent intent1 = getIntent();
        filename = intent1.getStringExtra("filename");

        String nohtmlFN = filename.replace(".html","");
        myApp.setGlobalString(nohtmlFN);

        ddock = new Beeper(this, R.raw.ddok);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BUTTON_X:
                commBtn.performClick();
                Log.d("X_test","pressed X Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                listBtn.performClick();
                Log.d("A_test","pressed A Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                dataBtn.performClick();
                Log.d("X_test","pressed X Key");
                return true;
        }
        return false;
    }


    private final RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {

            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < 1; i++) {
                textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();

                if (s1.equals("파일이름") || s1.equals("파일 이름") || s1.equals("파일이름 ") || s1.equals("파일 이름 ")) {

                    myApp.setTag("filename");

                } else if (s1.equals("제목") || s1.equals("제목 ")) {

                    myApp.setTag("title");

                } else if (s1.equals("문단") || s1.equals("문 단") || s1.equals("문단 ")) {

                    myApp.setTag("paragraph");

                } else if (s1.equals("닫기") || s1.equals("닫기 ")) {

                    myApp.setTag("close");

                } else if (s1.equals("라인추가") || s1.equals("라인 추가")) {

                    myApp.setTag("line");

                } else if (s1.equals("공백추가") || s1.equals("공백 추가")) {

                    myApp.setTag("space");


                } else if (s1.equals("챕터 추가") || s1.equals("챕터추가") || s1.equals("챕터추가 ") || s1.equals("챕터 추가 ")) {

                    myApp.setTag("chapter");


                } else if (s1.equals("섹션 추가") || s1.equals("섹션추가")) {

                    myApp.setTag("section");


                } else if (s1.equals("완성")) {

                    myApp.setTag("complete");
                    try {
                        myApp.setTag("complete");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</body>");
                        buf.append("</html>");
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File file = new File(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html");
                    Log.d("store", "success");


                    myApp.setChapter(0);
                    myApp.setSection(0);

                    startActivity(new Intent(WritingActivity.this, editActivity.class));


                } else {

                    MediaPlayer player;

                    player = MediaPlayer.create(WritingActivity.this, R.raw.ddok);
                    player.start();

                }
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    private final RecognitionListener listener2 = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "내용인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onResults(Bundle results) {

            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < 1; i++) {
                textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();
                Log.d("give me the tag",myApp.getTag());

                //'파일이름'이 포함된 경우
                //if (s1.contains("파일이름") || s1.contains("파일 이름")) {

                if (myApp.getTag().equals("filename")) {
                    Log.d("success","success");
                    //filePath 지정 (android 내장 storage /htmlData
                    String filePath = Environment.getExternalStorageDirectory() + "/htmlData/";
                    File file = new File(filePath);
                    //file이 없다면 디렉토리 생성
                    if (!file.exists()) {
                        file.mkdir();
                    }

                    myApp.setGlobalString(s1);
                    //myApp.setTag("filename");
                    Log.d("filename data", s1);


                    try {
                        //파일이름의 html 파일 생성하기
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        //내용 입력
                        buf.append("<html><meta charset=\"utf-8\"/>");
                        buf.append("<title>");
                        buf.append(s1);
                        buf.append("</title><body style='font-family: MalgunGothic;'>");
                        //buffer 닫기
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="title") {

                    try {
                        Log.d("h1 data", s1);
                        myApp.setTag("title");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("<h1>");
                        buf.append(s1);
                        buf.append("</h1>");
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (myApp.getTag()=="paragraph") {

                    try {

                        Log.d("p data", s1);

                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("<p>");
                        buf.append(s1);
                        buf.close();
                        //Log.d("Tag",myApp.getGlobalString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="close") {
                    //else if (s1.equals("close")) {
                    try {

                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</p>");
                        buf.close();
                        //Log.d("Tag",myApp.getGlobalString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="line") {
                    //else if (s1.contains("line")) {

                    try {

                        Log.d("s1", s1);
                        myApp.setTag("line");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</br>");
                        buf.close();
                        //Log.d("Tag",myApp.getGlobalString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="space") {
                    try {
                        Log.d("s2", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("&nbsp;");
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="chapter") {

                    try {
                        myApp.setChapter(myApp.getChapter() + 1);
                        myApp.setSection(0);
                        Log.d("s2", s1);
                        myApp.setTag("chapter");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("<h2>");
                        //buf.append("" + myApp.getChapter() + ". ");
                        buf.append(s1);
                        buf.append("</h2>");
                        //buf.append("<br/>");
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="section") {

                    try {
                        myApp.setSection(myApp.getSection() + 1);
                        Log.d("s2", s1);
                        myApp.setTag("section");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("<h3>");
                        //buf.append("" + myApp.getChapter() + "." + myApp.getSection() + " ");
                        buf.append(s1);
                        buf.append("</h3>");
                        //buf.append("<br/>");
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="complete") {
                    //  else if (s1.equals("complete")) {
                    try {
                        myApp.setTag("complete");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</body>");
                        buf.append("</html>");
                        buf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File file = new File(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html");
                    Log.d("store", "success");


                    myApp.setChapter(0);
                    myApp.setSection(0);

                    startActivity(new Intent(WritingActivity.this, editActivity.class));


                } else {


                }
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    class Beeper{
        MediaPlayer player;
        Beeper(Context context, int id){
            player = MediaPlayer.create(context, id);
        }

        void play(){
            player.seekTo(0);
            player.start();
        }
    }
}