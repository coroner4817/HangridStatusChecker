package com.yingnanwang.statuschecker.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.yingnanwang.statuschecker.util.ActivityCollector;
import com.yingnanwang.statuschecker.util.SocketService;


public class BaseActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    public SocketService.SocketBinder mBinder = null;
    private SocketService mService = null;
    private ServiceConnection conn = null;
    private boolean hasOnResume = false;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SocketService.LISTENER_MSG_ERROR:
                    if(hasOnResume){
                        Toast.makeText(getBaseActivityContext(), "网络错误: " + msg.obj, Toast.LENGTH_LONG).show();
                    }
                    break;
                case SocketService.LISTENER_MSG_RECV:
                    Log.i(TAG, "HeartBeat Recv: "+msg.obj.toString());
                    break;
                default:
            }
        }
    };

    public Context getBaseActivityContext(){
        return BaseActivity.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    public interface BaseActivityCallBackListener{
        void BaseActivity_onConnected();
        void BaseActivity_onDisConnected();
    }
    private BaseActivityCallBackListener mBaseListener = null;
    public void setBaseActivityCallBackListener(BaseActivityCallBackListener l){
        this.mBaseListener = l;
    }

    @Override
    protected void onResume() {
        hasOnResume = true;
        super.onResume();
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (SocketService.SocketBinder)service;
                mService = ((SocketService.SocketBinder) service).getService();
                mService.setSocketServiceCallBackListener(new SocketService.SocketServiceCallBackListener() {
                    @Override
                    public void onError(int error_code) {
                        Log.e(TAG, "onError: " + error_code);

                        Message message = new Message();
                        message.what = SocketService.LISTENER_MSG_ERROR;
                        message.obj = error_code;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onReceive(String msg) {
                        Message message = new Message();
                        message.what = SocketService.LISTENER_MSG_RECV;
                        message.obj = msg;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onConnected() {
                        if(getBaseActivityContext().getClass().getSimpleName().equals("MainActivity") && mBaseListener!=null){
                            mBaseListener.BaseActivity_onConnected();
                        }
                    }

                    @Override
                    public void onDisConnected() {
                        if(getBaseActivityContext().getClass().getSimpleName().equals("MainActivity") && mBaseListener!=null){
                            mBaseListener.BaseActivity_onDisConnected();
                        }
                    }
                });
            }
        };

        SocketService.actionBind(getBaseActivityContext(), conn);
    }

    // for send request
    public SocketService.SocketBinder getServiceBinder(){
        return mBinder;
    }

    @Override
    protected void onPause() {
        hasOnResume = false;
        super.onPause();
        SocketService.actionUnBind(getBaseActivityContext(), conn);
        conn = null;
        mBinder = null;
        mService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
