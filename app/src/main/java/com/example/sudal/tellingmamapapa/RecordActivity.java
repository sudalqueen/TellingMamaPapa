package com.example.sudal.tellingmamapapa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {
    TextView textView;
    Button prevBtn, nextBtn, recordBtn, playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }
}
