package com.zhangyc.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class AidlService extends Service {

    private IMyAidlTest.Stub mStub = new IMyAidlTest.Stub() {
        @Override
        public String getTestString() throws RemoteException {
            return "aidl test.";
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mStub.asBinder();
    }



}
