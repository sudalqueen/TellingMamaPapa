package com.example.sudal.tellingmamapapa;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<itemFairyTale> itemList = new ArrayList<>();
    ArrayList<itemFairyTale> rmList = new ArrayList<>();//추천 목록
    FairyTaleAdapter adapter;
    FairyTaleAdapter rmAdapter;
    RecyclerView recyclerView;
    RecyclerView rmRecyclerView;//추천 목록

    int spanCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.fairyRecycler);
        rmRecyclerView = (RecyclerView) findViewById(R.id.recommendRecycler);

        Drawable drawable = getResources().getDrawable(R.drawable.cat1);
        Drawable drawable2 = getResources().getDrawable(R.drawable.cat2);
        rmList.add(new itemFairyTale(drawable,"나는 고양이로소이다"));
        rmList.add(new itemFairyTale(drawable2,"냐옹냐옹"));
        rmList.add(new itemFairyTale(drawable2,"세계최강야옹이"));
        rmList.add(new itemFairyTale(drawable,"므야아옹"));

        itemList.add(new itemFairyTale(drawable,"고앵이의 모험"));
        itemList.add(new itemFairyTale(drawable2,"나는 고양이다!"));
        itemList.add(new itemFairyTale(drawable,"집사가 필요해"));
        itemList.add(new itemFairyTale(drawable2,"나도 고양이 키우고 싶다.."));
        itemList.add(new itemFairyTale(drawable,"안녕 고양이 친구"));
        itemList.add(new itemFairyTale(drawable2,"야옹학개론"));
        itemList.add(new itemFairyTale(drawable,"고양이의 지구 정복기"));
        itemList.add(new itemFairyTale(drawable2,"집사를 찾아서"));

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 4;
        }

        rmAdapter = new FairyTaleAdapter(rmList);
        rmRecyclerView.setAdapter(rmAdapter);
        rmRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        adapter = new FairyTaleAdapter(itemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));


    }

}
