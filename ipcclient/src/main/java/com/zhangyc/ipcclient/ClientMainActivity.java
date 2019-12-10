package com.zhangyc.ipcclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setAction("com.zhangyc.ipc.aidl");
        intent.setPackage("com.zhangyc.ipc");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMyAidlTest iMyAidlTest = IMyAidlTest.Stub.asInterface(service);
                try {
                    Log.i(TAG, "onServiceConnected: " + iMyAidlTest.getTestString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onServiceConnected: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

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
