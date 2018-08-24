package com.example.sudal.tellingmamapapa;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String FIREBASE_POST_URL = "https://console.firebase.google.com/u/2/project/tellingmamapapa/database/tellingmamapapa/data";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    static ArrayList<itemFairyTale> itemList = new ArrayList<>();
    static ArrayList<itemFairyTale> rmList = new ArrayList<>();//추천 목록
    FairyTaleAdapter adapter;
    FairyTaleAdapter rmAdapter;
    RecyclerView recyclerView;
    RecyclerView rmRecyclerView;//추천 목록

    int spanCount = 2;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        recyclerView = (RecyclerView) findViewById(R.id.fairyRecycler);
        rmRecyclerView = (RecyclerView) findViewById(R.id.recommendRecycler);

        itemList = new ArrayList<itemFairyTale>();
        itemList.clear();
        adapter = new FairyTaleAdapter(itemList);

        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Fairy");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    itemList.add(new itemFairyTale(R.drawable.cat1, data.child("name").getValue().toString(), data.child("explain").getValue().toString()));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rmList.clear();
        rmList.add(new itemFairyTale(R.drawable.cat1, "나는 고양이로소이다", "나 만한 고양이 없지"));
        rmList.add(new itemFairyTale(R.drawable.cat2, "냐옹냐옹", "해석하시오(150점)"));
        rmList.add(new itemFairyTale(R.drawable.cat2, "세계최강야옹이", "내 고양이가 알고보니 순수 혈통~~~"));
        rmList.add(new itemFairyTale(R.drawable.cat1, "므야아옹", "멍뭉멍멍"));


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4;
        }

        rmAdapter = new FairyTaleAdapter(rmList);
        rmAdapter.setItemClick(new FairyTaleAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("itemList", rmList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        rmRecyclerView.setAdapter(rmAdapter);
        rmRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        adapter.setItemClick(new FairyTaleAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("itemList", itemList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

    }

    private void initDatabase() {

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("Fairy");

        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

}
