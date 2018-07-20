package com.example.sudal.tellingmamapapa;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<itemFairyTale> itemList = new ArrayList<>();
    static ArrayList<itemFairyTale> rmList = new ArrayList<>();//추천 목록
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

        rmList.add(new itemFairyTale(R.drawable.cat1,"나는 고양이로소이다","나 만한 고양이 없지"));
        rmList.add(new itemFairyTale(R.drawable.cat2,"냐옹냐옹","해석하시오(150점)"));
        rmList.add(new itemFairyTale(R.drawable.cat2,"세계최강야옹이","내 고양이가 알고보니 순수 혈통~~~"));
        rmList.add(new itemFairyTale(R.drawable.cat1,"므야아옹","멍뭉멍멍"));

        itemList.add(new itemFairyTale(R.drawable.cat1,"고앵이의 모험","~비밀의 집사를 찾아서~"));
        itemList.add(new itemFairyTale(R.drawable.cat2,"나는 고양이다!","냐눙냔어우엉"));
        itemList.add(new itemFairyTale(R.drawable.cat1,"집사가 필요해","내가 할래"));
        itemList.add(new itemFairyTale(R.drawable.cat2,"나도 고양이 키우고 싶다..","강아지도 키우고파"));
        itemList.add(new itemFairyTale(R.drawable.cat1,"안녕 고양이 친구","난 강아지 친구얌"));
        itemList.add(new itemFairyTale(R.drawable.cat2,"야옹학개론","야옹야옹 314페이지를 펴세요"));
        itemList.add(new itemFairyTale(R.drawable.cat1,"고양이의 지구 정복기","내가 왔도댱"));
        itemList.add(new itemFairyTale(R.drawable.cat2,"집사를 찾아서","내 캔따개야 어딨니~"));

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 4;
        }

        rmAdapter = new FairyTaleAdapter(rmList);
        rmAdapter.setItemClick(new FairyTaleAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("itemList",rmList);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        rmRecyclerView.setAdapter(rmAdapter);
        rmRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        adapter = new FairyTaleAdapter(itemList);
        adapter.setItemClick(new FairyTaleAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("itemList",itemList);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));


    }

}
