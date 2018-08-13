package com.example.sudal.tellingmamapapa;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FairyTaleAdapter extends RecyclerView.Adapter<FairyTaleAdapter.ItemViewHolder> {
    ArrayList<itemFairyTale> itemList;
    public static final int VIEW_RECOMMEND = 0;
    public static final int VIEW_BUTTON = 1;
    public static final int VIEW_ITEM = 2;

    //아이템 클릭시 실행 함수
    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view,int position);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public FairyTaleAdapter(ArrayList<itemFairyTale> itemList) {
        this.itemList = itemList;
    }

    @Override
    public FairyTaleAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FairyTaleAdapter.ItemViewHolder holder, final int position) {
        itemFairyTale item = itemList.get(position);
        holder.img.setImageResource(item.getImage());
        holder.title.setText(item.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(itemClick != null){
                    itemClick.onClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void clear(){
        itemList.clear();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView title;

        public ItemViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.gridImg);
            title = (TextView) itemView.findViewById(R.id.gridTitle);
        }
    }
}
