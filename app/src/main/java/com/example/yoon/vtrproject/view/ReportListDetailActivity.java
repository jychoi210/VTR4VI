package com.example.yoon.vtrproject.view;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoon.vtrproject.MyApplication;
import com.example.yoon.vtrproject.R;
import com.example.yoon.vtrproject.WritingActivity;
import com.example.yoon.vtrproject.repo.DataRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ReportListDetailActivity extends AppCompatActivity implements ReportContract.ViewDetail {
    private ReportContract.PresenterDetail mPresenter;
    private ReportDetailAdapter reportDetailAdapter ;

    private ListView reportBodyList;
    private FloatingActionButton fabUp, fabDown, commBtn, dataBtn;
    private List<Boolean> readStatus;
    private int currentSpeaking;
    private String filename;
    Beeper ddock;

    //add
    private int currentSelected = -1;
    private int preSelected = currentSelected - 1;

    //STT
    Intent intent;
    SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlist_details);
        fabUp = findViewById(R.id.up);
        commBtn = findViewById(R.id.sttComm);
        dataBtn = findViewById(R.id.sttData);
        fabDown = findViewById(R.id.down);
        reportBodyList = findViewById(R.id.listview);
        final int index = getIntent().getIntExtra("index", -1);

        //STT
        //textView = findViewById(R.id.sttResult);

        if (index >= 0) {
            mPresenter = new ReportDetailPresenter(this);
            int size = mPresenter.getCount(index);
            if (size > 0) {
                readStatus = Arrays.asList(new Boolean[size]);
                Collections.fill(readStatus, Boolean.FALSE);

                reportDetailAdapter = new ReportDetailAdapter();
                reportBodyList.setAdapter(reportDetailAdapter);
                mPresenter.loadBodyText();

                /*
                원래 재생버튼
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPresenter.isSpeaking()) {
                            mPresenter.stopSpeak();
                            fab.setImageResource(R.drawable.ic_play);
                        } else {
                            mPresenter.startSpeak();
                        }
                    }
                });
                */

                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

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

                fabUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentSelected = currentSelected > 0 ? --currentSelected : reportDetailAdapter.getCount() - 1;
                        preSelected = currentSelected - 1;
                        reportDetailAdapter.notifyDataSetChanged();
                        mPresenter.stopSpeak();
                    }
                });

                fabDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentSelected = currentSelected < (reportDetailAdapter.getCount() - 1) ? ++currentSelected : 0;
                        preSelected = currentSelected - 1;
                        reportDetailAdapter.notifyDataSetChanged();
                        mPresenter.stopSpeak();
                    }
                });


            } else {
                Toast.makeText(this, "Text size error", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Text index error", Toast.LENGTH_SHORT).show();
            finish();
        }

        MyApplication myApp = (MyApplication) getApplication();
        filename = myApp.getGlobalString();

        ddock = new Beeper(this, R.raw.ddok);
    }

    @Override
    public void onBackPressed() {
        Log.d("backpressed","backpressed");
        int index = getIntent().getIntExtra("index", -1);
        MyApplication myApp = (MyApplication)getApplication();
        myApp.setCurrent(index);
        startActivity(new Intent(ReportListDetailActivity.this, ReportListActivity.class));
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){

            case KeyEvent.KEYCODE_DPAD_UP:
                onBackPressed();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                fabUp.performClick();
                Log.d("Left_test","pressed X Key");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                fabDown.performClick();
                Log.d("Right_test","pressed B Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                commBtn.performClick();
                Log.d("X_text","pressed command btn");
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                dataBtn.performClick();
                Log.d("X_text","pressed data btn");
                return true;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.initTTS();
        mPresenter.startSpeak();
        mPresenter.stopSpeak();
    }


    @Override
    public void addItem(String text) {
        reportDetailAdapter.addItem(text);
    }

    public void addJY(int position, String text) { reportDetailAdapter.addJY(position, text);}

    @Override
    public void speaking(int pos) {
        //fab.setImageResource(R.drawable.ic_pause);
        currentSpeaking = pos;
        reportDetailAdapter.notifyDataSetChanged();
        reportBodyList.smoothScrollToPosition(pos);
    }

    @Override
    public void speakDone(int pos) {
        readStatus.set(pos, true);
        reportDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void speakEnd() {
        //fab.setImageResource(R.drawable.ic_play);
        currentSpeaking = -1;
        reportDetailAdapter.notifyDataSetChanged();
        Toast.makeText(this, "End speaking", Toast.LENGTH_SHORT).show();
    }

    public class ReportDetailAdapter extends BaseAdapter {
        private ArrayList<String> bodyTextList = new ArrayList<>();

        @Override
        public int getCount() {
            return bodyTextList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            final Context context = parent.getContext();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reportlist_detail_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.speak_icon);
                viewHolder.text = convertView.findViewById(R.id.body_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text.setText(bodyTextList.get(position));


            //읽어진 것은 색을 파란색으로 지정
            /*
            if (readStatus.get(position)) {
                viewHolder.text.setTextColor(Color.BLUE);
            } else {
                viewHolder.text.setTextColor(Color.BLACK);
            }
            */


            //지금 읽고 있는 것은 아이콘 표시
            /*
            if (position == currentSpeaking) {
                viewHolder.icon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.icon.setVisibility(View.INVISIBLE);
            }
            */

            if (position == currentSelected) {
                convertView.setBackgroundColor(Color.rgb(173, 216, 230));
                Log.d("currentSelected", "" + currentSelected);
                String a = (String) viewHolder.text.getText();
                Log.d("a", "" + a);
                mPresenter.startSpeakJY(a);
                viewHolder.icon.setVisibility(View.VISIBLE);
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setCurrentText(a);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.icon.setVisibility(View.INVISIBLE);
            }

            if (position == preSelected){
                Log.d("preSelected", "" + preSelected);
                String a = (String) viewHolder.text.getText();
                Log.d("a", "" + a);
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setPreText(a);
            }

            return convertView;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public String getItem(int position) {
            return bodyTextList.get(position);
        }


        public void addItem(String text) {
            bodyTextList.add(text);
        }

        public void addJY(int position, String text){ bodyTextList.add(position, text); }

        class ViewHolder {
            ImageView icon;
            TextView text;
        }

    }

    private RecognitionListener listener = new RecognitionListener() {
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
                //textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();
                MediaPlayer player;

                //if (s1.equals("수정")) {
                if (s1.equals("modify") || s1.equals("modify ")) {

                    myApp.setTag("modify");

                //}else if (s1.equals("추가")) {
                }else if (s1.equals("add") || s1.equals("add ")) {

                    myApp.setTag("add");

                //} else if (s1.contains("삭제")) {
                } else if (s1.contains("delete") || s1.equals("delete ")) {
                    delete();
                }else{
                    player = MediaPlayer.create(ReportListDetailActivity.this, R.raw.ddok);
                    player.start();
                }
            }
        }


        public void delete() {
            {
                MyApplication myApp = (MyApplication) getApplication();
                FileReader fileReader = null;
                BufferedReader bufferedReader = null;
                try {
                    if (Build.VERSION.SDK_INT > 22) {
                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                    }

                    Log.d("delete", "" + myApp.getCurrentText());
                    fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                    bufferedReader = new BufferedReader(fileReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String line1 = line.replace("" + myApp.getPreText() + " " + myApp.getCurrentText()+"", "");
                        line1 = line1.replace("  "," ");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html"));
                        buf.append("" + line1);
                        Log.d("result", line1);
                        buf.close();
                    }

                    //startActivity(new Intent(ReportListDetailActivity.this, ReportListActivity.class));

                    reportDetailAdapter.bodyTextList.remove(currentSelected);
                    currentSelected = currentSelected-1;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) try { bufferedReader.close(); } catch (Exception ex) { /* Do Nothing */ }
                    if (fileReader != null) try { fileReader.close(); } catch (Exception ex) { /* Do Nothing */ }
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

    private RecognitionListener listener2 = new RecognitionListener() {
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
                //textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();
                MediaPlayer player;

                if (myApp.getTag()=="modify"){
                //if (s1.contains("수정")) {
                    Log.d("currentSelectedText",""+myApp.getCurrentText());
                    int index = getIntent().getIntExtra("index", -1);
                    String bodyText = DataRepository.get().getBodyText(index);
                    Log.d("bodyText",bodyText);

                    try {
                        if (Build.VERSION.SDK_INT > 22) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                        }


                        FileReader fileReader = null;
                        BufferedReader bufferedReader = null;

                        fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                        bufferedReader = new BufferedReader(fileReader);
                        String line;
                        //s1 = s1.replace(" ","");
                        line = bufferedReader.readLine();
                       // while ((line = bufferedReader.readLine()) != null) {
                        Log.d("line",line);
                        Log.d("currentText",myApp.getCurrentText());
                        String tempBT = bodyText;
                        String temp = myApp.getCurrentText();
                        tempBT = bodyText.replace(temp,s1);
                        Log.d("bodyText",bodyText);
                        Log.d("tempBT",tempBT);
                        String line1 = line.replace(bodyText,tempBT);
                        Log.d("changeLine",line1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html"));
                        buf.write(line1);
                        buf.close();

                        Log.d("Result", "success");

                        reportDetailAdapter.bodyTextList.set(currentSelected,s1);
                        reportDetailAdapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Result","fail");
                    }

                }
                else if (myApp.getTag()=="add"){
                //else if (s1.contains("추가")) {
                    add(s1);
                }else{

                }
            }
        }

        public void add(String s1){
            String s2 = s1.replace("추가 ", "");
            s2 = s2.replace("추 가 ", "");
            addJY(currentSelected+1,s2);
            try {

                //runtime error - 파일 불러와서 고치기에 대한 에러
                if(Build.VERSION.SDK_INT>22){
                    requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
                }


                FileReader fileReader = null;
                BufferedReader bufferedReader = null;

                fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                bufferedReader = new BufferedReader(fileReader);
                String line;
                StringBuffer sb = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    int num = 0;
                    String currentdata;
                    MyApplication myApp = (MyApplication) getApplication();
                    currentdata = myApp.getCurrentText();
                    int index = line.indexOf(currentdata);
                    Log.d("index",""+index);
                    num = currentdata.length();
                    sb = new StringBuffer(line);
                    sb.insert(index+num," "+s2);
                    Log.d("sb",sb.toString());
                }

                //BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" +  "a.html"));
                //buf.write(sb.toString());
                //buf.close();
                //startActivity(new Intent(ReportListDetailActivity.this, ReportListActivity.class));
                Log.d("Result","success");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Result","fail");
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

    /*@Override
    public void onBackPressed() {
        int index = getIntent().getIntExtra("index", -1);
        MyApplication myApp = (MyApplication)getApplication();
        myApp.setCurrent(index);
        startActivity(new Intent(ReportListDetailActivity.this, ReportListActivity.class));
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }*/

}

