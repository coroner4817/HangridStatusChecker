package com.yingnanwang.statuschecker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.yingnanwang.statuschecker.R;
import com.yingnanwang.statuschecker.widget.BreathButton;

import butterknife.ButterKnife;
import butterknife.Bind;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.breath_btn)
    BreathButton mBreathBtn;
    @Bind(R.id.ripple_view)
    RippleView mRippleView;

    @Bind(R.id.test_normal)
    Button normal;
    @Bind(R.id.test_error)
    Button error;
    @Bind(R.id.test_nostatus)
    Button nostatus;


    public static void actionStart(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBreathBtn.normalAnim();
            }
        });

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBreathBtn.errorAnim();
            }
        });

        nostatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBreathBtn.noStatusAnim();
            }
        });

        mRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (mBreathBtn.getStatus() != 0) {
                    ViewPagerActivity.actionStart(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
