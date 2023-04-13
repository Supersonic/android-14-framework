package com.android.ims.internal.uce.uceservice;

import android.content.Context;
import android.content.Intent;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import com.android.ims.internal.uce.uceservice.IUceService;
/* loaded from: classes4.dex */
public class ImsUceManager {
    public static final String ACTION_UCE_SERVICE_DOWN = "com.android.ims.internal.uce.UCE_SERVICE_DOWN";
    public static final String ACTION_UCE_SERVICE_UP = "com.android.ims.internal.uce.UCE_SERVICE_UP";
    private static final String LOG_TAG = "ImsUceManager";
    private static final String UCE_SERVICE = "uce";
    public static final int UCE_SERVICE_STATUS_CLOSED = 2;
    public static final int UCE_SERVICE_STATUS_FAILURE = 0;
    public static final int UCE_SERVICE_STATUS_ON = 1;
    public static final int UCE_SERVICE_STATUS_READY = 3;
    private static final Object sLock = new Object();
    private static ImsUceManager sUceManager;
    private Context mContext;
    private IUceService mUceService = null;
    private UceServiceDeathRecipient mDeathReceipient = new UceServiceDeathRecipient();

    public static ImsUceManager getInstance(Context context) {
        ImsUceManager imsUceManager;
        synchronized (sLock) {
            if (sUceManager == null && context != null) {
                sUceManager = new ImsUceManager(context);
            }
            imsUceManager = sUceManager;
        }
        return imsUceManager;
    }

    private ImsUceManager(Context context) {
        this.mContext = context;
        createUceService(true);
    }

    public IUceService getUceServiceInstance() {
        return this.mUceService;
    }

    private String getUceServiceName() {
        return UCE_SERVICE;
    }

    public void createUceService(boolean checkService) {
        if (checkService) {
            IBinder binder = ServiceManager.checkService(getUceServiceName());
            if (binder == null) {
                return;
            }
        }
        IBinder b = ServiceManager.getService(getUceServiceName());
        if (b != null) {
            try {
                b.linkToDeath(this.mDeathReceipient, 0);
            } catch (RemoteException e) {
            }
        }
        this.mUceService = IUceService.Stub.asInterface(b);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class UceServiceDeathRecipient implements IBinder.DeathRecipient {
        private UceServiceDeathRecipient() {
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            ImsUceManager.this.mUceService = null;
            if (ImsUceManager.this.mContext != null) {
                Intent intent = new Intent(ImsUceManager.ACTION_UCE_SERVICE_DOWN);
                ImsUceManager.this.mContext.sendBroadcast(new Intent(intent));
            }
        }
    }
}
