package android.p009se.omapi;

import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.util.Log;
import java.io.IOException;
import java.util.NoSuchElementException;
/* renamed from: android.se.omapi.Session */
/* loaded from: classes3.dex */
public final class Session {
    private static final String TAG = "OMAPI.Session";
    private final Object mLock = new Object();
    private final Reader mReader;
    private final SEService mService;
    private final ISecureElementSession mSession;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Session(SEService service, ISecureElementSession session, Reader reader) {
        if (service == null || reader == null || session == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        this.mService = service;
        this.mReader = reader;
        this.mSession = session;
    }

    public Reader getReader() {
        return this.mReader;
    }

    public byte[] getATR() {
        if (!this.mService.isConnected()) {
            throw new IllegalStateException("service not connected to system");
        }
        try {
            return this.mSession.getAtr();
        } catch (RemoteException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void close() {
        if (!this.mService.isConnected()) {
            Log.m110e(TAG, "service not connected to system");
            return;
        }
        synchronized (this.mLock) {
            try {
                this.mSession.close();
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error closing session", e);
            }
        }
    }

    public boolean isClosed() {
        try {
            return this.mSession.isClosed();
        } catch (RemoteException e) {
            return true;
        }
    }

    public void closeChannels() {
        if (!this.mService.isConnected()) {
            Log.m110e(TAG, "service not connected to system");
            return;
        }
        synchronized (this.mLock) {
            try {
                this.mSession.closeChannels();
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error closing channels", e);
            }
        }
    }

    public Channel openBasicChannel(byte[] aid, byte p2) throws IOException {
        if (!this.mService.isConnected()) {
            throw new IllegalStateException("service not connected to system");
        }
        synchronized (this.mLock) {
            try {
                try {
                    ISecureElementChannel channel = this.mSession.openBasicChannel(aid, p2, this.mReader.getSEService().getListener());
                    if (channel == null) {
                        return null;
                    }
                    return new Channel(this.mService, this, channel);
                } catch (RemoteException e) {
                    throw new IllegalStateException(e.getMessage());
                } catch (ServiceSpecificException e2) {
                    if (e2.errorCode == 1) {
                        throw new IOException(e2.getMessage());
                    }
                    if (e2.errorCode == 2) {
                        throw new NoSuchElementException(e2.getMessage());
                    }
                    throw new IllegalStateException(e2.getMessage());
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public Channel openBasicChannel(byte[] aid) throws IOException {
        return openBasicChannel(aid, (byte) 0);
    }

    public Channel openLogicalChannel(byte[] aid, byte p2) throws IOException {
        if (!this.mService.isConnected()) {
            throw new IllegalStateException("service not connected to system");
        }
        synchronized (this.mLock) {
            try {
                try {
                    ISecureElementChannel channel = this.mSession.openLogicalChannel(aid, p2, this.mReader.getSEService().getListener());
                    if (channel == null) {
                        return null;
                    }
                    return new Channel(this.mService, this, channel);
                } catch (RemoteException e) {
                    throw new IllegalStateException(e.getMessage());
                } catch (ServiceSpecificException e2) {
                    if (e2.errorCode == 1) {
                        throw new IOException(e2.getMessage());
                    }
                    if (e2.errorCode == 2) {
                        throw new NoSuchElementException(e2.getMessage());
                    }
                    throw new IllegalStateException(e2.getMessage());
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public Channel openLogicalChannel(byte[] aid) throws IOException {
        return openLogicalChannel(aid, (byte) 0);
    }
}
