package android.security;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.security.IGenerateRkpKeyService;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/* loaded from: classes3.dex */
public class GenerateRkpKey {
    private static final int NOTIFY_EMPTY = 0;
    private static final int NOTIFY_KEY_GENERATED = 1;
    private static final String TAG = "GenerateRkpKey";
    private static final int TIMEOUT_MS = 1000;
    private IGenerateRkpKeyService mBinder;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: android.security.GenerateRkpKey.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            GenerateRkpKey.this.mBinder = IGenerateRkpKeyService.Stub.asInterface(service);
            GenerateRkpKey.this.mCountDownLatch.countDown();
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName className) {
            GenerateRkpKey.this.mCountDownLatch.countDown();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            GenerateRkpKey.this.mBinder = null;
        }
    };
    private Context mContext;
    private CountDownLatch mCountDownLatch;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Status {
    }

    public GenerateRkpKey(Context context) {
        this.mContext = context;
    }

    private int bindAndSendCommand(int command, int securityLevel) throws RemoteException {
        Intent intent = new Intent(IGenerateRkpKeyService.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        int returnCode = 0;
        if (comp == null) {
            return 0;
        }
        intent.setComponent(comp);
        this.mCountDownLatch = new CountDownLatch(1);
        Executor executor = Executors.newCachedThreadPool();
        if (!this.mContext.bindService(intent, 1, executor, this.mConnection)) {
            throw new RemoteException("Failed to bind to GenerateRkpKeyService");
        }
        try {
            this.mCountDownLatch.await(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.m109e(TAG, "Interrupted: ", e);
        }
        IGenerateRkpKeyService iGenerateRkpKeyService = this.mBinder;
        if (iGenerateRkpKeyService == null) {
            Log.m110e(TAG, "Binder object is null; failed to bind to GenerateRkpKeyService.");
            returnCode = 8;
        } else {
            switch (command) {
                case 0:
                    returnCode = iGenerateRkpKeyService.generateKey(securityLevel);
                    break;
                case 1:
                    iGenerateRkpKeyService.notifyKeyGenerated(securityLevel);
                    break;
                default:
                    Log.m110e(TAG, "Invalid case for command");
                    break;
            }
        }
        this.mContext.unbindService(this.mConnection);
        return returnCode;
    }

    public int notifyEmpty(int securityLevel) throws RemoteException {
        return bindAndSendCommand(0, securityLevel);
    }

    public void notifyKeyGenerated(int securityLevel) throws RemoteException {
        bindAndSendCommand(1, securityLevel);
    }
}
