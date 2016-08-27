package com.yingnanwang.statuschecker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.yingnanwang.statuschecker.R;
import com.yingnanwang.statuschecker.fragment.ViewPagerFragment;

import butterknife.ButterKnife;
import butterknife.Bind;


public class ViewPagerActivity extends BaseActivity {

    private static final String TAG = "ViewPagerActivity";

    @Bind(R.id.materialViewPager)
    MaterialViewPager mViewPager;

    Toolbar toolbar;

    public static void actionStart(Context context){
        Intent intent = new Intent(context, ViewPagerActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        ButterKnife.bind(this);

        setTitle("");

        toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }


        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ViewPagerFragment.newInstance();
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "西安";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page % 4) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(R.color.green, "");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(R.color.blue, "");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(R.color.cyan, "");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(R.color.red, "");
                }
                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
