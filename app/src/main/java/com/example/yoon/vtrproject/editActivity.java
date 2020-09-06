package com.example.yoon.vtrproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoon.vtrproject.repo.TTSRepository;
import com.example.yoon.vtrproject.view.FileList2Activity;
import com.example.yoon.vtrproject.view.ReportListActivity;

/**
 * TODO: Edit Description
 */
public class editActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            init();
        }

        MyApplication myApp = (MyApplication) getApplication();
        String filename = myApp.getGlobalString();
        Log.d("filename",filename);
/*
        Intent intent = getIntent();
        String filename = intent.getStringExtra("filename");
        if(filename.equals("")|filename==null)
            Toast.makeText(this,"입력된 filename이 없습니다.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"입력된 아이디는"+filename+"입니다.", Toast.LENGTH_SHORT).show();
            */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==0){
            if(grantResults[0]==0){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                init();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT ).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        TTSRepository.get().destroy();
        super.onDestroy();
    }

    private void init() {
        TTSRepository.get().init(getApplicationContext());
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                startActivity(new Intent(this, ReportListActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BUTTON_B:
                startActivity(new Intent(this, ReportListActivity.class));
                Log.d("B_test","pressed B Key");
                return true;
        }
        return false;
    }
}
