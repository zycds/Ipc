package com.zhangyc.ipcclient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.zhangyc.ipc.IMyAidlTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientMainActivity extends AppCompatActivity {

    public static final String TAG = ClientMainActivity.class.getSimpleName();

    private Messenger mMessengerServer;
    private Handler mHandlerClient;

    static class Handler extends android.os.Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i(TAG, "handleMessage: 1");
                    break;
                case 2:
                    Log.i(TAG, "handleMessage: 2");
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandlerClient = new Handler();

        Cursor query = getContentResolver().query(Uri.parse("content://com.zhangyc.ipc.provider"), null, null, null, null);
        Log.i(TAG, "onCreate: " + query);
        if (query != null) {
            query.close();
        }


        Intent componentIntent = IntentUtils.getComponentIntent("com.zhangyc.ipc", "com.zhangyc.ipc.AidlService");
//        Intent componentIntent = new Intent();
//        componentIntent.setAction("com.zhangyc.ipc.aidl");
//        componentIntent.setPackage("com.zhangyc.ipc");
        bindService(componentIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                /*IMyAidlTest iMyAidlTest = IMyAidlTest.Stub.asInterface(service);
                try {
                    Log.i(TAG, "onServiceConnected: " + iMyAidlTest.getTestString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onServiceConnected: " + e.getLocalizedMessage());
                }*/

                 Log.i(TAG, "onServiceConnected: ");
                mMessengerServer = new Messenger(service);
                Message obtain = Message.obtain();
                obtain.what = 2;
                obtain.replyTo = new Messenger(mHandlerClient);
                try {
                    mMessengerServer.send(obtain);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onServiceConnected: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected: " + name.getClassName());

            }
        }, Context.BIND_AUTO_CREATE);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    String ipAddress = NetworkUtils.getIPAddress(true);
                    Socket socket = new Socket(ipAddress, 8090);
                    Log.i(TAG, "run: ipAddress : " + ipAddress + "  connected : " + socket.isConnected());
                    while(socket.isConnected()) {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                        bufferedWriter.write("client send to server : hello.");
                        bufferedWriter.flush();
                        bufferedWriter.close();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                        String receiveMsg ;
                        if ((receiveMsg = bufferedReader.readLine()) != null) {
                            Log.i(TAG, "run: client receive msg : " + receiveMsg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();




    }

    public void start(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.zhangyc.ipc", "com.zhangyc.ipc.BundleActivity");
        Bundle bundle = new Bundle();
        bundle.putString("key", "client");
        intent.putExtra("bundleKey", bundle);
        startActivity(intent);
    }

}
