package com.yingnanwang.statuschecker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.yingnanwang.statuschecker.R;
import com.yingnanwang.statuschecker.adapter.RecyclerViewAdapter;
import com.yingnanwang.statuschecker.dataClass.CardViewInfoClass;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerFragment extends Fragment {

    private static final int ITEM_COUNT = 10;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<CardViewInfoClass> mContentItems = new ArrayList<>();

    public static ViewPagerFragment newInstance() {
        return new ViewPagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        mAdapter = new RecyclerViewAdapter(mContentItems);
        mRecyclerView.setAdapter(mAdapter);

        // get data from http
        for (int i = 0; i < ITEM_COUNT; ++i) {
            mContentItems.add(new CardViewInfoClass(String.valueOf(i)));
        }
        mAdapter.notifyDataSetChanged();

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView);
    }
}
