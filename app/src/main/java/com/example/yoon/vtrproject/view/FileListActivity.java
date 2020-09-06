package com.example.yoon.vtrproject.view;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yoon.vtrproject.EnterActivity;
import com.example.yoon.vtrproject.MainActivity;
import com.example.yoon.vtrproject.MyApplication;
import com.example.yoon.vtrproject.OnSingleClickListener;
import com.example.yoon.vtrproject.R;
import com.example.yoon.vtrproject.WritingActivity;
import com.example.yoon.vtrproject.editActivity;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.speech.tts.TextToSpeech.ERROR;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class FileListActivity extends AppCompatActivity implements ReportContract.ViewMain {

    private ArrayList<FileListItem> fileListItemList = new ArrayList<>();
    private FloatingActionButton fabUp, fabSelect, fabDown, commBtn;
    private FileListAdapter fileListAdapter;
    Intent intent1;
    SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private ReportContract.ViewList mList;
    private int currentSelected = -1;
    private TextToSpeech tts;              // TTS 변수 선언

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        MyApplication myApp = (MyApplication) getApplication();

        setContentView(R.layout.filelist_main);
        fabUp = findViewById(R.id.up);
        fabSelect = findViewById(R.id.select);
        fabDown = findViewById(R.id.down);
        commBtn = findViewById(R.id.edit);

        //filename = myApp.getGlobalString();
        //Log.d("파일이름은",filename);

        fileListAdapter = new FileListAdapter();
        final ListView fileListView = findViewById(R.id.listview);
        fileListView.setAdapter(fileListAdapter);


        fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelected = currentSelected > 0 ? --currentSelected : fileListAdapter.getCount() - 1;
                fileListAdapter.notifyDataSetChanged();
                //mList.stopSpeak();
            }
        });

        fabSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileListItem item = fileListAdapter.getItem(currentSelected);
                if (item != null) {
//                    if (!item.getTagData().contains("문단")) {
                    //mList.startSpeak(item.getFnData());
//                    } else {
                    Intent intent = new Intent(FileListActivity.this, WritingActivity.class);
                    String fn = item.getFnData();
                    intent.putExtra("filename", item.getFnData());
                    startActivity(intent);
                    Log.d("cs",""+item.getFnData());
//                    }
                }
            }

        });

        fabDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelected = currentSelected < (fileListAdapter.getCount() - 1) ? ++currentSelected : 0;
                fileListAdapter.notifyDataSetChanged();
                //mList.stopSpeak();
            }
        });

        mList = new FileListPresenter(this);
        mList.onList();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        commBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent1);
            }
        });

        intent1=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

    }

    //bluetooth remote control
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                fabUp.performClick();
                Log.d("UP_test","pressed UP Key");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                fabSelect.performClick();
                Log.d("RIGHT_test","pressed RIGHT Key");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                fabDown.performClick();
                Log.d("DOWN_test","pressed DOWN Key");
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
                //textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();
                //'파일이름'이 포함된 경우
                //if (s1.contains("파일이름") || s1.contains("파일 이름")) {
                if (s1.equals("삭제")){

                    file_delete();

                } else {

                    MediaPlayer player;

                    player = MediaPlayer.create(FileListActivity.this, R.raw.ddok);
                    player.start();
                    Log.d("twice","중복실행");

                }
            }

        }

        public void file_delete() {
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            String tagStart = null;
            String tagEnd = null;
            try {

                if (Build.VERSION.SDK_INT > 22) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                }

                FileListItem item = fileListAdapter.getItem(currentSelected);

                fileListItemList.remove(item);
                Log.d("file_delete", "currentSelected : " + item.getFnData());
                File file = new File(Environment.getExternalStorageDirectory() + "/htmlData/"+item.getFnData());

                file.delete();

                startActivity(new Intent(FileListActivity.this, FileListActivity.class));
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setCurrent(currentSelected);

            } finally {
                if (bufferedReader != null) try {
                    bufferedReader.close();
                } catch (Exception ex) { /* Do Nothing */ }
                if (fileReader != null) try {
                    fileReader.close();
                } catch (Exception ex) { /* Do Nothing */ }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        mList.initTTS();
    }

    @Override
    public void addItem(int position, String tag, String strData) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void addList(String filename) {
        fileListAdapter.addItem(filename);
    }

    public class FileListAdapter extends BaseAdapter{
        private ArrayList<FileListItem> fileListItemList = new ArrayList<>();

        @Override
        public int getCount() {return fileListItemList.size(); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.filelist_item, parent, false);
            }

            TextView fnDataView = convertView.findViewById(R.id.fnData);

            FileListItem fileListItem = fileListItemList.get(position);

            fnDataView.setText(fileListItem.getFnData());

            if (currentSelected == position) {
                convertView.setBackgroundColor(Color.rgb(173,216,230));
                //block 단위 읽어주기
                FileListItem item = fileListAdapter.getItem(currentSelected);
                if (item != null) {
                    String fn = item.getFnData();
                    fn = (String) fn.replace(".html","");
                    Log.d("startspeak",fn);
                    //mList.startSpeak("test");
                    tts.speak(fn,TextToSpeech.QUEUE_FLUSH, null);
                }
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public FileListItem getItem(int position) {
            return fileListItemList.get(position);
        }

        public void addItem(String filename){
            FileListItem item = new FileListItem();
            item.setData(filename);
            fileListItemList.add(item);
        }


    }


}