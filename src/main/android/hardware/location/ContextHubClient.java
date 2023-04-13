package android.hardware.location;

import android.annotation.SystemApi;
import android.p008os.RemoteException;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
@SystemApi
/* loaded from: classes2.dex */
public class ContextHubClient implements Closeable {
    private static final String TAG = "ContextHubClient";
    private final ContextHubInfo mAttachedHub;
    private final CloseGuard mCloseGuard;
    private final boolean mPersistent;
    private IContextHubClient mClientProxy = null;
    private final AtomicBoolean mIsClosed = new AtomicBoolean(false);
    private Integer mId = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContextHubClient(ContextHubInfo hubInfo, boolean persistent) {
        this.mAttachedHub = hubInfo;
        this.mPersistent = persistent;
        if (persistent) {
            this.mCloseGuard = null;
            return;
        }
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        closeGuard.open("ContextHubClient.close");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setClientProxy(IContextHubClient clientProxy) {
        Objects.requireNonNull(clientProxy, "IContextHubClient cannot be null");
        if (this.mClientProxy != null) {
            throw new IllegalStateException("Cannot change client proxy multiple times");
        }
        this.mClientProxy = clientProxy;
        try {
            this.mId = Integer.valueOf(clientProxy.getId());
            notifyAll();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubInfo getAttachedHub() {
        return this.mAttachedHub;
    }

    public int getId() {
        Integer num = this.mId;
        if (num == null) {
            throw new IllegalStateException("ID was not set");
        }
        return num.intValue() & 65535;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (!this.mIsClosed.getAndSet(true)) {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.close();
            }
            try {
                this.mClientProxy.close();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public int sendMessageToNanoApp(NanoAppMessage message) {
        Objects.requireNonNull(message, "NanoAppMessage cannot be null");
        int maxPayloadBytes = this.mAttachedHub.getMaxPacketLengthBytes();
        byte[] payload = message.getMessageBody();
        if (payload != null && payload.length > maxPayloadBytes) {
            Log.m110e(TAG, "Message (" + payload.length + " bytes) exceeds max payload length (" + maxPayloadBytes + " bytes)");
            return 2;
        }
        try {
            return this.mClientProxy.sendMessageToNanoApp(message);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            if (!this.mPersistent) {
                close();
            }
        } finally {
            super.finalize();
        }
    }

    public synchronized void callbackFinished() {
        while (true) {
            try {
                IContextHubClient iContextHubClient = this.mClientProxy;
                if (iContextHubClient == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    iContextHubClient.callbackFinished();
                }
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }
    }
}
