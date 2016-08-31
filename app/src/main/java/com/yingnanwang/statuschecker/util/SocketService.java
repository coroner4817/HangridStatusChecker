package com.yingnanwang.statuschecker.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class SocketService extends Service {

    private static final String TAG = "SocketService";

    private static final int ERR_CONNECTION = 100;
    private static final int ERR_SEND = 200;
    private static final int ERR_RECV = 300;
    private static final int ERR_STAT = 400;
    private static final int ERR_SOCKET_CLOSE = 500;

    public static final int LISTENER_MSG_ERROR = 0;
    public static final int LISTENER_MSG_RECV = 1;

    private final int HEART_BEAT_RATE = 3000;
    private String host;
    private int port;
    private Socket socket = null;
    private OutputStream outputstream = null;
    private InputStream inputstream = null;
    private volatile boolean runningHeartBeat = false;
    private volatile boolean serviceDestory = false;
    private int handlerMsgCount = 0;


    public static void actionStart(Context context, String _host, int port){
        Intent intent = new Intent(context, SocketService.class);
        intent.putExtra("_host_", _host);
        intent.putExtra("_port_", port);
        context.startService(intent);
    }

    public static void actionStop(Context context){
        Intent intent = new Intent(context, SocketService.class);
        context.stopService(intent);
    }

    public static void actionBind(Context context, ServiceConnection conn){
        Log.d(TAG, "actionBind: "+context.toString());
        Intent intent = new Intent(context, SocketService.class);
        context.bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public static void actionUnBind(Context context, ServiceConnection conn){
        Log.d(TAG, "actionUnBind: "+context.toString());
        context.unbindService(conn);
    }




    public class SocketBinder extends Binder {
        public SocketService getService(){
            return SocketService.this;
        }

        public String sendRequest(final String msg){
            if(sendMsg(msg)){
                String re = recvRequestMsg();
                if(re==null){
                    if(listener!=null){
                        listener.onError(ERR_CONNECTION);
                    }
                }
                return re;
            }
            if(listener!=null){
                listener.onError(ERR_CONNECTION);
            }
            return null;
        }
    }

    public interface SocketServiceCallBackListener {
        void onError(int error_code);
        void onReceive(String msg);
        void onConnected();
        void onDisConnected();
    }

    private SocketServiceCallBackListener listener = null;
    public void setSocketServiceCallBackListener(SocketServiceCallBackListener l){
        this.listener = l;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SocketBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.host = intent.getStringExtra("_host_");
        this.port = intent.getIntExtra("_port_", 8256);
        initAndStartHeartBeat();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initAndStartHeartBeat(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(initSocket()){
                    startHeartBeat();
                }else{
                    stopAndReInitSocket();
                }
            }
        }).start();
    }

    private void heartbeatWhile(){
        while (runningHeartBeat && !serviceDestory) {
            if(!sendMsg(getData2Send() + "\n")){
                stopAndReInitSocket();
                break;
            }
            Log.i(TAG, "Send HeartBeat");
            if(!recvMsg()){
                stopAndReInitSocket();
                break;
            }
            try{
                Thread.sleep(HEART_BEAT_RATE);
            }catch (Exception e){
                stopAndReInitSocket();
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean initSocket(){
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 3000);
            outputstream = socket.getOutputStream();
            inputstream = socket.getInputStream();
            socket.setSoTimeout(3000);
            Thread.sleep(50);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private boolean sendMsg(String msg){
        try{
            if(isSocketAvilable(1)){
                outputstream.write(msg.getBytes());
                outputstream.flush();
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

    private boolean recvMsg(){
        try{
            if(isSocketAvilable(0)){
                byte[] buff = new byte[1024];
                int dataLen = inputstream.read(buff);
                if(dataLen > 0){
                    String recv = new String(Arrays.copyOf(buff, dataLen)).trim();
                    if(listener!=null){
                        listener.onReceive(recv);
                    }
                }
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

    private String recvRequestMsg(){
        try{
            if(isSocketAvilable(0)){
                byte[] buff = new byte[1024];
                int dataLen = inputstream.read(buff);
                if(dataLen > 0){
                    return new String(Arrays.copyOf(buff, dataLen)).trim();
                }
            }
        }catch(Exception e){

        }
        return null;
    }

    private void stopHeartBeat(){
        if(listener!=null){
            listener.onDisConnected();
        }
        if(runningHeartBeat){
            runningHeartBeat = false;
        }
    }

    private void startHeartBeat(){
        Log.e(TAG, "startHeartBeat");
        handlerMsgCount = 0;
        if(listener!=null){
            listener.onConnected();
        }
        if(!runningHeartBeat){
            runningHeartBeat = true;
            heartbeatWhile();
        }
    }

    private boolean isSocketAvilable(int mode){
        if(mode == 1){
            // send
            if(!socket.isClosed() && socket.isConnected() && !socket.isOutputShutdown()){
                return true;
            }else{
                return false;
            }
        }else if(mode == 0){
            // recv
            if(!socket.isClosed() && socket.isConnected() && !socket.isInputShutdown()){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    private String getData2Send(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date())+" HeartBeat from "+
                socket.getLocalAddress().toString();
    }

    private void socketClose(){
        try{
            if(!socket.isClosed()){
                if(outputstream!=null){
                    outputstream.close();
                }
                if(inputstream!=null){
                    inputstream.close();
                }
                socket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == ERR_SOCKET_CLOSE && !serviceDestory){
                reInitSocketAndHeartBeat();
            }
        }
    };

    private void reInitSocketAndHeartBeat(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "reInitSocketAndHeartBeat");
                if (!initSocket()){
                    try{
                        Thread.sleep(3000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    sendHandlerMsg();
                }else{
                    startHeartBeat();
                }
            }
        }).start();
    }

    private void sendHandlerMsg(){

        if(handlerMsgCount % 5 == 0 && listener!=null){
            listener.onError(ERR_CONNECTION);
        }
        if(listener!=null){
            handlerMsgCount += 1;
        }
        Message message = new Message();
        message.what = ERR_SOCKET_CLOSE;
        mHandler.sendMessage(message);
    }

    private void stopAndReInitSocket(){
        stopHeartBeat();
        socketClose();
        sendHandlerMsg();
    }

    @Override
    public void onDestroy() {
        serviceDestory = true;
        stopHeartBeat();
        socketClose();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }
}
