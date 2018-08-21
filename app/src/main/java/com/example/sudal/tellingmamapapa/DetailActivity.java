package com.example.sudal.tellingmamapapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView title;
    TextView detail;
    Button detailBtn, recordBtn;
    ImageView imageView;

    ArrayList<itemFairyTale> itemList = new ArrayList<>();

    itemFairyTale item;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);
        imageView = (ImageView) findViewById(R.id.image);
        detailBtn = (Button) findViewById(R.id.detailBtn);
        recordBtn = (Button) findViewById(R.id.recordBtn);

        Intent intent = getIntent();
        if (intent != null) {
            itemList = (ArrayList<itemFairyTale>) intent.getSerializableExtra("itemList");
            position = intent.getIntExtra("position", -1);
        }
        item = itemList.get(position);
        title.setText(item.getTitle());
        imageView.setImageResource(item.getImage());
        detail.setText(item.getExplane());


        detailBtn.setOnClickListener(onClickListener);
        recordBtn.setOnClickListener(onClickListener);
    }

    Button.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.detailBtn:
                    if (detail.getVisibility() == View.GONE) {
                        detail.setVisibility(View.VISIBLE);
                    } else {
                        detail.setVisibility(View.GONE);
                    }
                    break;
                case R.id.recordBtn:
                    Intent intent = new Intent(DetailActivity.this, Record2WavActivity.class);
                    intent.putExtra("item",item);
                    startActivity(intent);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
