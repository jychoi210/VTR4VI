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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoon.vtrproject.MyApplication;
import com.example.yoon.vtrproject.R;
import com.example.yoon.vtrproject.repo.DataRepository;
import com.example.yoon.vtrproject.sendHtml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ReportListActivity extends AppCompatActivity implements ReportContract.ViewMain {

    private ReportListAdapter reportListAdapter;
    //    private int currentSelected = -1;
    private int currentSelected;
    private FloatingActionButton fabUp, fabSelect, fabDown, commBtn, dataBtn;
    private ReportContract.PresenterMain mPresenter;
    private ArrayList<ReportListItem> reportListItemList = new ArrayList<>();
    //
    private String filename;
    Beeper ddock;

    //STT
    Intent intent;
    Intent intentTable;
    Intent intentCell;
    Intent intentback;
    SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    //Button sttBtn;
    TextView textView;

    //Search
    EditText editTextFilter;

    //Filter Position 확인
    ArrayList<Integer> positionList;
    Boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        final MyApplication myApp = (MyApplication) getApplication();
        int current = myApp.getCurrent();
        currentSelected = current;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlist_main);

        final int checkValue = getIntent().getIntExtra("check", -1);
        final int checkCell = getIntent().getIntExtra("checkCell", -1);
        final int testVal = getIntent().getIntExtra("testValue", -1);

        Log.d("testvalue",""+testVal);

        fabUp = findViewById(R.id.up);
        fabSelect = findViewById(R.id.select);
        fabDown = findViewById(R.id.down);
        commBtn = findViewById(R.id.sttComm);
        dataBtn = findViewById(R.id.sttData);
        //STT
        textView = findViewById(R.id.sttResult);
        //sttBtn = findViewById(R.id.edit);


        if(checkValue > -1){
            intentTable = new Intent(ReportListActivity.this, TableListDetailActivity.class);
            intentTable.putExtra("checkValue", checkValue);
            startActivity(intentTable);
            finish();
        }

        if(checkCell > -1){
            intentCell = new Intent(ReportListActivity.this, TableCellDetailActivity.class);
            intentCell.putExtra("checkCellValue", checkCell);
            startActivity(intentCell);
            finish();
        }

        if(testVal > -1){
            intentback = new Intent(ReportListActivity.this, TableListDetailActivity.class);
            intentback.putExtra("testVal",testVal);
            startActivity(intentback);
            finish();
        }



        reportListAdapter = new ReportListAdapter();
        final ListView reportListView = findViewById(R.id.listview);
        reportListView.setAdapter(reportListAdapter);
//        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (reportListAdapter.getItem(position).getTagData().contains("문단")) {
//                    startActivity(new Intent(ReportListActivity.this, ReportListDetailActivity.class));
//                }
//            }
//        });


        fabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelected = currentSelected > 0 ? --currentSelected : reportListAdapter.getCount() - 1;
                reportListAdapter.notifyDataSetChanged();
                mPresenter.stopSpeak();

                //Log.d("위로 버튼", Integer.toString(currentSelected));
            }
        });


        fabSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검색후 리스트뷰 id값에 맞게 intent 전환
                if (check) {
                    Log.d("check is true","so Select button is able");
                    //fabDown.performClick();

                    ReportListItem item = reportListAdapter.getItem(currentSelected);
                    if (item != null) {
                        mPresenter.startSpeak(item.getTagData());
                        Log.d("tag data is ",item.getTagData());
                        Intent intent;
                        //if(item.getTagData().contains("표")) {
                        if(item.getTagData().contains("table")) {
                            intent = new Intent(ReportListActivity.this, TableListDetailActivity.class);
                            Log.d("tablelistDetailActivity","open");
                        }else{
                            intent = new Intent(ReportListActivity.this, ReportListDetailActivity.class);
                        }
                        intent.putExtra("index", myApp.getselectedIndex()-1);
                        startActivity(intent);
                    }
                } else if (currentSelected == -1) {
                    Log.d("currentSelected is -1","so Select button is not able");
                    fabDown.performClick();
                } else {
                    Log.d("currentSelected isnt -1","so Select button is able");
                    ReportListItem item = reportListAdapter.getItem(currentSelected);
                    if (item != null) {
                        //                    if (!item.getTagData().contains("문단")) {
                        mPresenter.startSpeak(item.getTagData());
                        //                    } else {
                        Log.d("tag data is ",item.getTagData());
                        Intent intent;
                        //if(item.getTagData().contains("표")) {
                        if(item.getTagData().contains("table")) {
                            String tag = mPresenter.tagVerBodyText(currentSelected);
                            intent = new Intent(ReportListActivity.this, TableListDetailActivity.class);
                            Log.d("tablelistDetailActivity","open");
                            intent.putExtra("tag",tag);
                        }else{
                            intent = new Intent(ReportListActivity.this, ReportListDetailActivity.class);
                        }
                        intent.putExtra("index", currentSelected);
                        startActivity(intent);
                        //                    }
                    }
                }
            }
        });


        fabDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelected = currentSelected < (reportListAdapter.getCount() - 1) ? ++currentSelected : 0;
                reportListAdapter.notifyDataSetChanged();
                mPresenter.stopSpeak();

                //Log.d("아래로 버튼", Integer.toString(currentSelected));
            }
        });

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

        String filename1 = myApp.getGlobalString();
        Log.d("filename",filename1);

        mPresenter = new ReportListPresenter(this,filename1);
        mPresenter.parseData();

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
        filename = myApp.getGlobalString();
        Log.d("filename11",filename);

        ddock = new Beeper(ReportListActivity.this, R.raw.ddok);

        // 검색
        editTextFilter = (EditText)findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();

                if (filterText.length() > 0) {
                    reportListView.setFilterText(filterText);
                } else {
                    reportListView.clearTextFilter();
                }

                //((ReportListAdapter)reportListView.getAdapter()).getFilter().filter(filterText);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        //

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                fabUp.performClick();
                Log.d("UP_test","pressed UP Key");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                fabDown.performClick();
                Log.d("DOWN_test","pressed DOWN Key");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                fabSelect.performClick();
                Log.d("RIGHT_test","pressed RIGHT Key");
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                commBtn.performClick();
                Log.d("X_test","pressed command btn");
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                dataBtn.performClick();
                Log.d("B_test","pressed data btn");
                return true;
        }
        return false;
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"명령인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

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

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {

            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


            MediaPlayer player;

            for (int i = 0; i < 1; i++) {
                textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();
                s1 = s1.replace(".","");
                s1 = s1.replace("!","");

                //if (s1.equals("문단") || s1.equals("문 단") || s1.equals("문 단 ") || s1.equals("문단 ")) {
                if (s1.equals("paragraph") || s1.equals("paragraph ")) {

                    myApp.setTag("paragraph");

                //} else if (s1.equals("챕터") || s1.equals("챕터 ") || s1.equals("챕 터") || s1.equals("챕 터 ")) {
                } else if (s1.equals("chapter") || s1.equals("chapter ")) {

                    myApp.setTag("chapter");

                //} else if (s1.equals("섹션") || s1.equals("섹션 ") || s1.equals("섹 션") || s1.equals("섹 션 ")) {
                } else if (s1.equals("section") || s1.equals("section ")) {

                    myApp.setTag("section");

                //} else if(s1.equals("검색")){
                } else if(s1.equals("search")){

                    myApp.setTag("search");

                //}else if(s1.equals("완성") || s1.equals("완성 ") || s1.equals("완 성") || s1.equals("완 성 ")){
                }else if(s1.equals("complete") || s1.equals("complete ")){
                    set_Number();
                    startActivity(new Intent(ReportListActivity.this, ReportListActivity.class));
                    File file = new File(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                    Log.d("store", "success");
                    sendHtml sh = new sendHtml(file);
                    sh.start();
                    Log.d("send", "success");
                //}else if(s1.contains("삭제") || s1.contains("삭 제") || s1.contains("삭제 ") || s1.contains("삭 제 ")) {
                }else if(s1.contains("delete") || s1.contains("delete ")) {
                    vtr_delete();
                }else{
                    player = MediaPlayer.create(ReportListActivity.this, R.raw.ddok);
                    player.start();
                }

            }
        }

        public void vtr_delete(){
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            String tagStart = null;
            String tagEnd = null;
            try {
                Log.d("vtr_delete","currentSelected : "+currentSelected);
                if(Build.VERSION.SDK_INT>22){
                    requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
                }

                ReportListItem item = reportListAdapter.getItem(currentSelected);
                Log.d("item.getTagData",""+item.getTagData());
                if (item.getTagData() == "챕터"){
                    tagStart = "<h2>";
                    tagEnd = "</h2>";
                }else if(item.getTagData() == "섹션"){
                    tagStart = "<h3>";
                    tagEnd = "</h3>";
                }else if(item.getTagData() == "문단"){
                    tagStart = "<p>";
                    tagEnd = "</p>";
                }
                Log.d("delete",""+tagStart+item.getStrData()+tagEnd);
                fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String line1 = line.replace("" +tagStart+item.getStrData()+tagEnd,"");
                    BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" +filename+".html"));
                    buf.append(""+line1);
                    Log.d("result",line1);
                    buf.close();
                }
                reportListItemList.remove(currentSelected);
                startActivity(new Intent(ReportListActivity.this, ReportListActivity.class));
                MyApplication myApp = (MyApplication)getApplication();
                myApp.setCurrent(currentSelected);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) try { bufferedReader.close(); } catch (Exception ex) { /* Do Nothing */ }
                if (fileReader != null) try { fileReader.close(); } catch (Exception ex) { /* Do Nothing */ }
            }
        }

        public void set_Number(){
            int position = reportListItemList.size();
            int getChapter = 1;
            int getSection = 1;
            String line1 = null;

            try {

                FileReader fileReader = null;
                BufferedReader bufferedReader = null;

                fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line1 = line;
                    //Log.d("changeLine",line1);
                    //BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" + "a.html"));
                    //buf.write(line1);
                    //buf.close();
                }

                //startActivity(new Intent(ReportListDetailActivity.this, ReportListActivity.class));
                Log.d("Result", "success");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Result","fail");
            }


            for(int i = 0; i < position; i++){
                ReportListItem item = reportListAdapter.getItem(i);
                if (item.getTagData() == "챕터"){
                    //chapter일 떄, android 고치기
                    String tempstr =item.getStrData();
                    String bodyText = item.getStrData();
                    tempstr = Integer.toString(getChapter)+". "+tempstr;
                    item.setData(item.getTagData(),tempstr);

                    line1 = line1.replace(bodyText,tempstr);
                    Log.d("line1",line1);

                    for(int j = i; j < position; j++) {
                        ReportListItem next = reportListAdapter.getItem(j);
                        if (next.getTagData() == "섹션") {
                            String tmp = next.getStrData();
                            String bodyText2 = next.getStrData();
                            tmp = Integer.toString(getChapter) + "." + Integer.toString(getSection) + " " + tmp;
                            next.setData(next.getTagData(), tmp);
                            line1 = line1.replace(bodyText2,tmp);
                            Log.d("line1",line1);
                            getSection++;

                            ReportListItem next2;
                            if(j == position-1){
                                next2 = reportListAdapter.getItem(j);
                            }else{
                                next2 = reportListAdapter.getItem(j+1);
                            }

                            ReportListItem temp = next2;

                            if (temp.getTagData() != "섹션") {
                                break;
                            }

                        }else{
                            getSection=1;
                        }
                    }
                    getChapter++;
                }
            }

            try {
                if (Build.VERSION.SDK_INT > 22) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                }


                FileReader fileReader = null;
                BufferedReader bufferedReader = null;

                fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("FinalchangeLine",line1);
                    BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" +filename+ ".html"));
                    buf.write(line1);
                    buf.close();
                }

                //startActivity(new Intent(ReportListDetailActivity.this, ReportListActivity.class));
                Log.d("Result", "success");


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Result","fail");
            }


        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    private final RecognitionListener listener2 = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"내용인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

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

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {

            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


            MediaPlayer player;

            for (int i = 0; i < 1; i++) {
                textView.setText(matches.get(i));
                String s1 = matches.get(i);
                MyApplication myApp = (MyApplication) getApplication();
                Log.d("give me the tag",myApp.getTag());

                if (myApp.getTag()=="paragraph") {
                    //if (s1.contains("문단")) {
                    addItem(currentSelected+1,"paragraph",s1);
                    try {

                        //runtime error - 파일 불러와서 고치기에 대한 에러
                        if(Build.VERSION.SDK_INT>22){
                            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
                        }

                        //현재 선택된 item 가져오기(tag, str)
                        ReportListItem item = reportListAdapter.getItem(currentSelected);

                        FileReader fileReader = null;
                        BufferedReader bufferedReader = null;

                        fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/" + filename + ".html");
                        bufferedReader = new BufferedReader(fileReader);
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            int num = 0;
                            String currentdata;
                            Log.d("getTagData",""+item.getTagData());
                            if(item.getTagData()=="파일이름"){
                                currentdata = "<title>"+item.getStrData()+"</title>";
                            }else if(item.getTagData()=="제목"){
                                currentdata = "<h1>"+item.getStrData()+"</h1>";
                            }else if(item.getTagData()=="문단"){
                                currentdata = "<p>"+item.getStrData()+"</p>";
                            }else if(item.getTagData()=="챕터"){
                                currentdata = "<h2>"+item.getStrData()+"</h2>";
                            }else if(item.getTagData()=="섹션"){
                                currentdata = "<h3>"+item.getStrData()+"</h3>";
                            }else{
                                String bodyText = DataRepository.get().getBodyText(currentSelected);
                                bodyText = bodyText.replace("\n","");
                                bodyText = bodyText.replace("> <","><");
                                bodyText = bodyText.replace(">  <","><");
                                Log.d("table", bodyText);
                                currentdata = "<table>"+bodyText+"</table>";
                                Log.d("currentdata", currentdata);
                                Log.d("line", line);

                            }

                            int index = line.indexOf(currentdata);
                            sb = new StringBuffer(line);
                            num = currentdata.length();
                            sb.insert(index+num,"<p>"+s1+"</p>");
                        }

                        Log.d("p data", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" +filename+ ".html"));
                        buf.write(sb.toString());
                        buf.close();
                        startActivity(new Intent(ReportListActivity.this, ReportListActivity.class));
                        myApp.setCurrent(currentSelected+1);
                        Log.d("Result","success");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Result","fail");
                    }

                }else if (myApp.getTag()=="chapter") {
                    //else if (s1.contains("챕터") ) {
                    Log.d("currentSelected",""+currentSelected);
                    addItem(currentSelected+1,"chapter",s1);
                    try {

                        //runtime error - 파일 불러와서 고치기에 대한 에러
                        if(Build.VERSION.SDK_INT>22){
                            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
                        }

                        //현재 선택된 item 가져오기(tag, str)
                        ReportListItem item = reportListAdapter.getItem(currentSelected);

                        FileReader fileReader = null;
                        BufferedReader bufferedReader = null;

                        fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                        bufferedReader = new BufferedReader(fileReader);
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            int num = 0;
                            String currentdata;
                            Log.d("getTagData",""+item.getTagData());
                            if(item.getTagData()=="파일이름"){
                                currentdata = "<title>"+item.getStrData()+"</title>";
                            }else if(item.getTagData()=="제목"){
                                currentdata = "<h1>"+item.getStrData()+"</h1>";
                            }else if(item.getTagData()=="문단"){
                                currentdata = "<p>"+item.getStrData()+"</p>";
                            }else if(item.getTagData()=="챕터"){
                                currentdata = "<h2>"+item.getStrData()+"</h2>";
                            }else if(item.getTagData()=="섹션"){
                                currentdata = "<h3>"+item.getStrData()+"</h3>";
                            }else{
                                String bodyText = DataRepository.get().getBodyText(currentSelected);
                                bodyText = bodyText.replace("\n","");
                                bodyText = bodyText.replace("> <","><");
                                bodyText = bodyText.replace(">  <","><");
                                Log.d("table", bodyText);
                                currentdata = "<table>"+bodyText+"</table>";
                                Log.d("currentdata", currentdata);
                                Log.d("line", line);

                            }
                            int index = line.indexOf(currentdata);
                            sb = new StringBuffer(line);
                            num = currentdata.length();
                            //sb.insert(index+num,"<h2>"+myApp.getChapter()+s2+"</h2>");
                            sb.insert(index+num,"<h2>"+s1+"</h2>");
                        }

                        Log.d("chapter data", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" +filename+  ".html"));
                        buf.write(sb.toString());
                        buf.close();
                        startActivity(new Intent(ReportListActivity.this, ReportListActivity.class));
                        myApp.setCurrent(currentSelected+1);
                        Log.d("Result","success");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Result","fail");
                    }
                }else if (myApp.getTag()=="section"){
                    //else if (s1.contains("섹션") ) {
                    addItem(currentSelected+1,"section",s1);
                    try {

                        //runtime error - 파일 불러와서 고치기에 대한 에러
                        if(Build.VERSION.SDK_INT>22){
                            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
                        }

                        //현재 선택된 item 가져오기(tag, str)
                        ReportListItem item = reportListAdapter.getItem(currentSelected);

                        FileReader fileReader = null;
                        BufferedReader bufferedReader = null;

                        fileReader = new FileReader(Environment.getExternalStorageDirectory() + "/htmlData/"+filename+".html");
                        bufferedReader = new BufferedReader(fileReader);
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            int num = 0;
                            String currentdata;
                            Log.d("getTagData",""+item.getTagData());
                            if(item.getTagData()=="파일이름"){
                                currentdata = "<title>"+item.getStrData()+"</title>";
                            }else if(item.getTagData()=="제목"){
                                currentdata = "<h1>"+item.getStrData()+"</h1>";
                            }else if(item.getTagData()=="문단"){
                                currentdata = "<p>"+item.getStrData()+"</p>";
                            }else if(item.getTagData()=="챕터"){
                                currentdata = "<h2>"+item.getStrData()+"</h2>";
                            }else if(item.getTagData()=="섹션"){
                                currentdata = "<h3>"+item.getStrData()+"</h3>";
                            }else{
                                String bodyText = DataRepository.get().getBodyText(currentSelected);
                                bodyText = bodyText.replace("\n","");
                                bodyText = bodyText.replace("> <","><");
                                bodyText = bodyText.replace(">  <","><");
                                Log.d("table", bodyText);
                                currentdata = "<table>"+bodyText+"</table>";
                                Log.d("currentdata", currentdata);
                                Log.d("line", line);

                            }
                            int index = line.indexOf(currentdata);
                            sb = new StringBuffer(line);
                            num = currentdata.length();
                            sb.insert(index+num,"<h3>"+s1+"</h3>");
                        }

                        Log.d("section data", s1);
                        BufferedWriter buf = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/htmlData/" +filename+ ".html"));
                        buf.write(sb.toString());
                        buf.close();
                        startActivity(new Intent(ReportListActivity.this, ReportListActivity.class));
                        myApp.setCurrent(currentSelected+1);
                        Log.d("Result","success");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Result","fail");
                    }}
                else if(myApp.getTag()=="search"){
                    editTextFilter.setText(s1);
                }else{

                }

            }
        }


        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.initTTS();
    }

    @Override
    public void addItem(int position, String tag, String strData) {
        reportListAdapter.addItem(position, tag, strData);
    }

    @Override
    public void onError() {
        // TODO Error handling
    }

    @Override
    public void addList(String filename) {

    }

    public class ReportListAdapter extends BaseAdapter implements Filterable {
        //private ArrayList<ReportListItem> reportListItemList = new ArrayList<>();
        private ArrayList<ReportListItem> filteredItemList = reportListItemList;


        Filter listFilter;

        public ReportListAdapter() {

        }


        @Override
        public int getCount() {
            //return reportListItemList.size();
            return filteredItemList.size();
            //
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.reportlist_item, parent, false);
            }

            TextView tagDataView = convertView.findViewById(R.id.tagData);
            TextView strDataView = convertView.findViewById(R.id.strData);

            //ReportListItem reportListItem = reportListItemList.get(position);
            ReportListItem reportListItem = filteredItemList.get(position);
            //

            tagDataView.setText(reportListItem.getTagData());
            strDataView.setText(reportListItem.getStrData());

            //Log.d("currentSelected","currentSelected : "+currentSelected);
            if (currentSelected == position) {
                convertView.setBackgroundColor(Color.rgb(173,216,230));
                //block 단위 읽어주기
                ReportListItem item = reportListAdapter.getItem(currentSelected);
                if (item != null) {
                    mPresenter.startSpeak(item.getTagData());
                    mPresenter.startSpeak(item.getStrData());
                }
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public ReportListItem getItem(int position) {
            //return reportListItemList.get(position);
            return filteredItemList.get(position);
            //
        }

/*
        public void addItem(String tagData, String strData){
            ReportListItem item = new ReportListItem();
            item.setData(tagData, strData);
            reportListItemList.add(item);
        }
*/

        public void addItem(int position, String tagData, String strData){
            ReportListItem item = new ReportListItem();
            item.setData(tagData, strData);
            reportListItemList.add(position, item);
        }


        @Override
        public Filter getFilter() {
            if (listFilter == null) {
                listFilter = new ListFilter() ;
            }

            return listFilter ;
        }

        private class ListFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                MyApplication myApp = (MyApplication) getApplication();
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = reportListItemList;
                    results.count = reportListItemList.size();
                } else {
                    ArrayList<ReportListItem> itemList = new ArrayList<>();

                    // 필터링되어 갱신된 리스트뷰 id값 새로 부여
                    positionList = new ArrayList<>();
                    check = true;
                    int i = 0;
                    for (ReportListItem item : reportListItemList) {
                        i++;
                        if (item.getStrData().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            positionList.add(i);
                            Log.d("필터링 테스트", item.getStrData().toUpperCase());
                            itemList.add(item);
                        }
                    }
                    for (int j = 0; j < positionList.size(); j++) {
                        Log.d("인덱스 테스트", String.valueOf(positionList.get(j)));
                        myApp.setselectedIndex(positionList.get(j));
                    }

                    {
                        results.values = itemList;
                    }
                    results.count = itemList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemList = (ArrayList<ReportListItem>) results.values;

                // notify
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }


    }

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