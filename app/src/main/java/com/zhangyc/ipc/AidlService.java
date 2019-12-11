package com.zhangyc.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

public class AidlService extends Service {

    public static final String TAG = AidlService.class.getSimpleName();

    private IMyAidlTest.Stub mStub = new IMyAidlTest.Stub() {
        @Override
        public String getTestString() {
            return "aidl test.";
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mMessengerServer.getBinder();
//        return mStub.asBinder();
    }

    private Handler mHandlerServer = new Handler();
    private Messenger mMessengerServer = new Messenger(mHandlerServer);

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
                    if (msg.replyTo != null) {
                        Message message = Message.obtain();
                        message.what = 1;
                        try {
                            msg.replyTo.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

}
