package com.zhangyc.ipc;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.blankj.utilcode.util.NetworkUtils;
import com.zhangyc.imageloader.ImageLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BundleActivity extends AppCompatActivity {

    public static final String TAG = BundleActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle);
        Log.i(TAG, "onCreate: ");

        ImageLoader.Companion.getInstance().load(R.drawable.ic_launcher_background);

        final ExecutorService executorService = Executors.newCachedThreadPool();

        Bundle bundleKey = getIntent().getBundleExtra("bundleKey");
        if (bundleKey != null) {
            String key = bundleKey.getString("key");
            Log.i(TAG, "onCreate: " + key);
        }



        ServerSocket socket = null;
        try {
            socket = new ServerSocket(8090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ServerSocket serverSocket = socket;
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Log.i(TAG, "run: client.");
                while (true) {
                    try {
                        Log.i(TAG, "run: ");
                        Socket accept = serverSocket.accept();
                        Log.i(TAG, "run: " + accept);
                        executorService.execute(new Client(accept));
                    } catch (IOException e) {
                        Log.e(TAG, "run: " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();



    }

    class Client implements Runnable {



        private Socket mSocket;

        private BufferedReader mBufferedReader;
        private BufferedWriter mBufferedWriter;


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        Client(Socket socket) {
            mSocket = socket;
            if (mSocket != null) {
                try {
                    String ipAddress = NetworkUtils.getIPAddress(true);
                    Log.i(TAG, "run: ipAddress : " + ipAddress + "  connected : " + mSocket.isConnected());
                    mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), StandardCharsets.UTF_8));
                    mBufferedWriter.write("connect server success.");
                    mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            Log.i(TAG, "run: " + mSocket);
            while (mSocket != null && mSocket.isConnected()) {
                if (mBufferedReader != null) {
                    String receiveMsg;
                    try {
                        if ((receiveMsg = mBufferedReader.readLine()) != null) {
                            Log.i(TAG, "run: receive client msg : " + receiveMsg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }




}
