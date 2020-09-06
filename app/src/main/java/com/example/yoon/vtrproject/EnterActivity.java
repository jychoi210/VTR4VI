package com.example.yoon.vtrproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoon.vtrproject.view.FileList2Activity;
import com.example.yoon.vtrproject.view.FileListActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class EnterActivity extends AppCompatActivity {

    Intent intent,intent1;
    SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    Button commBtn,editBtn,listBtn,dataBtn;
    TextView textView;
    final int PERMISSION = 1;
    Beeper ddock;
    private final int PERMISSIONS_REQUEST_RESULT = 1;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION);
        }




        textView = (TextView)findViewById(R.id.sttResult);
        commBtn = (Button) findViewById(R.id.sttComm);
        editBtn = (Button) findViewById(R.id.edit);
        listBtn = (Button) findViewById(R.id.list);
        dataBtn = (Button) findViewById(R.id.sttData);

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        
        commBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);
            }
        });


        dataBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mRecognizer.setRecognitionListener(listener2);
                mRecognizer.startListening(intent);
            }
        });



        editBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), FileList2Activity.class);
                startActivity(intent1);
            }
        });


        listBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), FileListActivity.class);
                startActivity(intent2);
            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });


        Log.d("directory",""+Environment.getExternalStorageDirectory());
        MyApplication myApp = (MyApplication) getApplication();

        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                //SharedPreferences에 저장된 값들을 로그아웃 버튼을 누르면 삭제하기 위해
                //SharedPreferences를 불러옵니다. 메인에서 만든 이름으로
                Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();
                Toast.makeText(EnterActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        ddock = new Beeper(this, R.raw.ddok);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (PERMISSIONS_REQUEST_RESULT == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 요청이 됐습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "권한 요청을 해주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }
    }

    //bluetooth remote control
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                tts.speak("명령어는 파일이름,  제목,  챕터추가,  섹션추가,  문단,  라인추가,  공백추가가 있습니다. 문단을 완성했을 때는 닫기 명령 입력을 해주시고, 보고서 작성이 완료되면 완성 명령 입력을 해주세요.",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                tts.speak("위의 버튼을 누르면 음성 명령 입력을 할 수 있고, 아래 버튼을 누르면 내용 입력을 할 수 있습니다. 왼쪽 버튼을 누르면 저장되어 있는 파일을 편집할 수 있고, 오른쪽 버튼을 누르면 이전의 파일에 이어 작성할 수 있습니다.",TextToSpeech.QUEUE_FLUSH, null);
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                dataBtn.performClick();
                Log.d("X_test","pressed X Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                commBtn.performClick();
                Log.d("X_test","pressed X Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                listBtn.performClick();
                Log.d("Y_test","pressed Y Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                editBtn.performClick();
                Log.d("B_test","pressed B Key");
                return true;
        }
        return false;
    }


    private final RecognitionListener listener = new RecognitionListener() {

        boolean singleResult = true;

        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "명령인식을 시작합니다.", Toast.LENGTH_SHORT).show();
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
                //'파일이름'이 포함된 경우
                //if (s1.contains("파일이름") || s1.contains("파일 이름")) {
                if (s1.equals("파일이름") || s1.equals("파일 이름") || s1.equals("파일이름 ") || s1.equals("파일 이름 ")) {
                //if (s1.equals("filename") || s1.equals("filename ") || s1.equals("file name") || s1.equals("file name ") ) {

                    myApp.setTag("filename");

                } else if (s1.equals("제목") || s1.equals("제목 ")) {
                //} else if (s1.equals("title") || s1.equals("title ")) {

                    myApp.setTag("title");

                } else if (s1.equals("표 삽입") || s1.equals("표삽입") || s1.equals("표 삽입 ")) {
                //} else if (s1.equals("add table") || s1.equals("add table ")) {

                    myApp.setTag("table");
                    try {
                        Log.d("table data", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("<table><tbody><tr>");
                        buf.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                //}else if (s1.equals("행 추가") || s1.equals(" 행 추가") || s1.equals("행 추가 ") || s1.equals(" 행 추가 ") || s1.equals("행추가")) {
                }else if (s1.equals("add row") || s1.equals("add row ")) {

                    myApp.setTag("rowend");
                    try {
                        Log.d("table data_row end", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</tr><tr>");
                        buf.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                //}else if (s1.equals("표 끝") || s1.equals("표끝") || s1.equals("표끝 ") || s1.equals("표 끝 ")) {
                }else if (s1.equals("table end") || s1.equals("table end ")) {

                    myApp.setTag("tableend");
                    try {
                        Log.d("table end", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</tr></tbody></table>");
                        buf.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }else if (s1.equals("문단") || s1.equals("문 단") || s1.equals("문단 ")) {
                //}else if (s1.equals("paragraph") || s1.equals("paragraph ")) {

                    myApp.setTag("paragraph");

                } else if (s1.equals("닫기") || s1.equals("닫기 ")) {
                //} else if (s1.equals("close") || s1.equals("close ")) {

                    myApp.setTag("close");
                    try {

                        Log.d("/p data","close");

                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</p>");
                        buf.close();
                        //Log.d("Tag",myApp.getGlobalString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (s1.equals("라인추가") || s1.equals("라인 추가")) {

                    myApp.setTag("line");

                } else if (s1.equals("공백추가") || s1.equals("공백 추가")) {

                    myApp.setTag("space");


                //} else if (s1.equals("챕터 추가") || s1.equals("챕터추가") || s1.equals("챕터추가 ") || s1.equals("챕터 추가 ")) {
                } else if (s1.equals("chapter") || s1.equals("chapter ")) {

                    myApp.setTag("chapter");


                //} else if (s1.equals("섹션 추가") || s1.equals("섹션추가")) {
                } else if (s1.equals("section") || s1.equals("section ")) {

                    myApp.setTag("section");


                //} else if (s1.equals("완성") || s1.equals("완성 ")) {
                } else if (s1.equals("complete") || s1.equals("complete ")) {

                    myApp.setTag("complete");
                    try {
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

                    startActivity(new Intent(EnterActivity.this, editActivity.class));
                    Log.d("setTag",myApp.getTag());


                } else {

                    MediaPlayer player;

                    player = MediaPlayer.create(EnterActivity.this, R.raw.ddok);
                    player.start();
                    Log.d("twice","중복실행");

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

        int count = 0;
        int count1 = 0;

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
                        buf.append("<html><meta charset=\"utf-8\"/><style>table,td{border: 1px solid black; border-collapse: collapse;}</style>");
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

                        Log.d("/p data","close");

                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("</p>");
                        buf.close();
                        //Log.d("Tag",myApp.getGlobalString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (myApp.getTag()=="table") {
                    try {
                        Log.d("table data", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                        buf.append("<td>");
                        buf.append(s1);
                        buf.append("</td>");
                        buf.close();
                        count++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count1 = count;
                }else if (myApp.getTag()=="rowend") {
                    if(count >= 0) {
                        try {
                            count--;
                            Log.d("table data_rowend", s1);
                            Log.d("row num is", "" + count);
                            BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + myApp.getGlobalString() + ".html", true));
                            buf.append("<td>");
                            buf.append(s1);
                            buf.append("</td>");
                            buf.close();
                            if(count == 0){
                                tts.speak("한 행이 모두 끝났습니다.",TextToSpeech.QUEUE_FLUSH, null);
                                count = count1;
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{

                    }

                }else if (myApp.getTag()=="line") {
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
                }else if (myApp.getTag()=="section") {

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
                    Log.d("enter", "success");
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

                    startActivity(new Intent(EnterActivity.this, editActivity.class));


                }else {


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
