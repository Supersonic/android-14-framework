package android.p009se.omapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p009se.omapi.ISecureElementListener;
import android.p009se.omapi.ISecureElementService;
import android.util.Log;
import java.util.HashMap;
import java.util.concurrent.Executor;
/* renamed from: android.se.omapi.SEService */
/* loaded from: classes3.dex */
public final class SEService {
    public static final String ACTION_SECURE_ELEMENT_STATE_CHANGED = "android.se.omapi.action.SECURE_ELEMENT_STATE_CHANGED";
    public static final String EXTRA_READER_NAME = "android.se.omapi.extra.READER_NAME";
    public static final String EXTRA_READER_STATE = "android.se.omapi.extra.READER_STATE";
    public static final int IO_ERROR = 1;
    public static final int NO_SUCH_ELEMENT_ERROR = 2;
    private static final String TAG = "OMAPI.SEService";
    private static final String UICC_TERMINAL = "SIM";
    private ServiceConnection mConnection;
    private final Context mContext;
    private volatile ISecureElementService mSecureElementService;
    private SEListener mSEListener = new SEListener();
    private final Object mLock = new Object();
    private final HashMap<String, Reader> mReaders = new HashMap<>();

    /* renamed from: android.se.omapi.SEService$OnConnectedListener */
    /* loaded from: classes3.dex */
    public interface OnConnectedListener {
        void onConnected();
    }

    /* renamed from: android.se.omapi.SEService$SEListener */
    /* loaded from: classes3.dex */
    private class SEListener extends ISecureElementListener.Stub {
        public Executor mExecutor;
        public OnConnectedListener mListener;

        private SEListener() {
            this.mListener = null;
            this.mExecutor = null;
        }

        @Override // android.p009se.omapi.ISecureElementListener.Stub, android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public void onConnected() {
            Executor executor;
            if (this.mListener != null && (executor = this.mExecutor) != null) {
                executor.execute(new Runnable() { // from class: android.se.omapi.SEService.SEListener.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SEListener.this.mListener.onConnected();
                    }
                });
            }
        }

        @Override // android.p009se.omapi.ISecureElementListener
        public String getInterfaceHash() {
            return "894069bcfe4f35ceb2088278ddf87c83adee8014";
        }

        @Override // android.p009se.omapi.ISecureElementListener
        public int getInterfaceVersion() {
            return 1;
        }
    }

    public SEService(Context context, Executor executor, OnConnectedListener listener) {
        if (context == null || listener == null || executor == null) {
            throw new NullPointerException("Arguments must not be null");
        }
        this.mContext = context;
        this.mSEListener.mListener = listener;
        this.mSEListener.mExecutor = executor;
        this.mConnection = new ServiceConnection() { // from class: android.se.omapi.SEService.1
            @Override // android.content.ServiceConnection
            public synchronized void onServiceConnected(ComponentName className, IBinder service) {
                SEService.this.mSecureElementService = ISecureElementService.Stub.asInterface(service);
                if (SEService.this.mSEListener != null) {
                    SEService.this.mSEListener.onConnected();
                }
                Log.m108i(SEService.TAG, "Service onServiceConnected");
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName className) {
                SEService.this.mSecureElementService = null;
                Log.m108i(SEService.TAG, "Service onServiceDisconnected");
            }
        };
        Intent intent = new Intent(ISecureElementService.class.getName());
        intent.setClassName("com.android.se", "com.android.se.SecureElementService");
        boolean bindingSuccessful = context.bindService(intent, this.mConnection, 1);
        if (bindingSuccessful) {
            Log.m108i(TAG, "bindService successful");
        }
    }

    public boolean isConnected() {
        return this.mSecureElementService != null;
    }

    public Reader[] getReaders() {
        loadReaders();
        return (Reader[]) this.mReaders.values().toArray(new Reader[0]);
    }

    public Reader getUiccReader(int slotNumber) {
        if (slotNumber < 1) {
            throw new IllegalArgumentException("slotNumber should be larger than 0");
        }
        loadReaders();
        String readerName = UICC_TERMINAL + slotNumber;
        Reader reader = this.mReaders.get(readerName);
        if (reader == null) {
            throw new IllegalArgumentException("Reader:" + readerName + " doesn't exist");
        }
        return reader;
    }

    public void shutdown() {
        synchronized (this.mLock) {
            if (this.mSecureElementService != null) {
                for (Reader reader : this.mReaders.values()) {
                    try {
                        reader.closeSessions();
                    } catch (Exception e) {
                    }
                }
            }
            try {
                this.mContext.unbindService(this.mConnection);
            } catch (IllegalArgumentException e2) {
            }
            this.mSecureElementService = null;
        }
    }

    public String getVersion() {
        return "3.3";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ISecureElementListener getListener() {
        return this.mSEListener;
    }

    private ISecureElementReader getReader(String name) {
        try {
            return this.mSecureElementService.getReader(name);
        } catch (RemoteException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void loadReaders() {
        if (this.mSecureElementService == null) {
            throw new IllegalStateException("service not connected to system");
        }
        try {
            String[] readerNames = this.mSecureElementService.getReaders();
            for (String readerName : readerNames) {
                if (this.mReaders.get(readerName) == null) {
                    try {
                        this.mReaders.put(readerName, new Reader(this, readerName, getReader(readerName)));
                    } catch (Exception e) {
                        Log.m109e(TAG, "Error adding Reader: " + readerName, e);
                    }
                }
            }
        } catch (RemoteException e2) {
            throw e2.rethrowAsRuntimeException();
        }
    }
}
