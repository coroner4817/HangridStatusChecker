package com.yingnanwang.statuschecker.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yingnanwang.statuschecker.R;
import com.yingnanwang.statuschecker.dataClass.CardViewInfoClass;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<CardViewInfoClass> contents;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public RecyclerViewAdapter(List<CardViewInfoClass> contents) {
        this.contents = contents;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new MyViewHolder(view);
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new MyViewHolder(view);
            }
        }
        return null;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_CELL:
                holder.mTextView.setText(contents.get(position).getTitle());
                break;
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;

        public MyViewHolder(View itemView){
            super(itemView);
            mTextView = (TextView)itemView.findViewById(R.id.card_view_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
