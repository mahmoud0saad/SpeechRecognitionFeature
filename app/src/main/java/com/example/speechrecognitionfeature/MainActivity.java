package com.example.speechrecognitionfeature;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.speechrecognitionfeature.utils.OnCallBack;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_PERMISSION = 100;


    Button speechBtn;
    TextView resultSpeechTV;
    ProgressBar progressBar;
    private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        speechBtn = findViewById(R.id.speech_btn);
        resultSpeechTV = findViewById(R.id.resultSpeechTV);
        progressBar = findViewById(R.id.progressBar);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new RecognizerListener(  new OnCallBack() {
            @Override
            public void onError(String response) {
                resultSpeechTV.setText(response);
                hideProgressBar();
            }

            @Override
            public void onSuccess(String response) {
                resultSpeechTV.setText(response);
                hideProgressBar();

            }
        }));

        speechBtn.setOnClickListener(view -> {

            if (checkHaveVoicePermission()) {
                showProgressBar();

                sr.startListening(getRecognizerIntent());

            } else {
                requestPermissionFromDevice();

            }


        });

    }


    private boolean checkHaveVoicePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionFromDevice() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_PERMISSION);

    }


    Intent getRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


        // to add  other language change to { US-en  ,  ar-EG }
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        return intent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgressBar();
                    sr.startListening(getRecognizerIntent());
                } else {
                    hideProgressBar();
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }



    void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }
}