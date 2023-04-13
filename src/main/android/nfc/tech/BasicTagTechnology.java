package android.nfc.tech;

import android.nfc.Tag;
import android.nfc.TransceiveResult;
import android.p008os.RemoteException;
import android.util.Log;
import java.io.IOException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class BasicTagTechnology implements TagTechnology {
    private static final String TAG = "NFC";
    boolean mIsConnected;
    int mSelectedTechnology;
    final Tag mTag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BasicTagTechnology(Tag tag, int tech) throws RemoteException {
        this.mTag = tag;
        this.mSelectedTechnology = tech;
    }

    @Override // android.nfc.tech.TagTechnology
    public Tag getTag() {
        return this.mTag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkConnected() {
        if (this.mTag.getConnectedTechnology() != this.mSelectedTechnology || this.mTag.getConnectedTechnology() == -1) {
            throw new IllegalStateException("Call connect() first!");
        }
    }

    @Override // android.nfc.tech.TagTechnology
    public boolean isConnected() {
        if (this.mIsConnected) {
            try {
                return this.mTag.getTagService().isPresent(this.mTag.getServiceHandle());
            } catch (RemoteException e) {
                Log.m109e(TAG, "NFC service dead", e);
                return false;
            }
        }
        return false;
    }

    @Override // android.nfc.tech.TagTechnology
    public void connect() throws IOException {
        try {
            int errorCode = this.mTag.getTagService().connect(this.mTag.getServiceHandle(), this.mSelectedTechnology);
            if (errorCode == 0) {
                if (!this.mTag.setConnectedTechnology(this.mSelectedTechnology)) {
                    Log.m110e(TAG, "Close other technology first!");
                    throw new IOException("Only one TagTechnology can be connected at a time.");
                } else {
                    this.mIsConnected = true;
                }
            } else if (errorCode == -21) {
                throw new UnsupportedOperationException("Connecting to this technology is not supported by the NFC adapter.");
            } else {
                throw new IOException();
            }
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
            throw new IOException("NFC service died");
        }
    }

    @Override // android.nfc.tech.TagTechnology
    public void reconnect() throws IOException {
        if (!this.mIsConnected) {
            throw new IllegalStateException("Technology not connected yet");
        }
        try {
            int errorCode = this.mTag.getTagService().reconnect(this.mTag.getServiceHandle());
            if (errorCode != 0) {
                this.mIsConnected = false;
                this.mTag.setTechnologyDisconnected();
                throw new IOException();
            }
        } catch (RemoteException e) {
            this.mIsConnected = false;
            this.mTag.setTechnologyDisconnected();
            Log.m109e(TAG, "NFC service dead", e);
            throw new IOException("NFC service died");
        }
    }

    @Override // android.nfc.tech.TagTechnology, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            try {
                this.mTag.getTagService().resetTimeouts();
                this.mTag.getTagService().reconnect(this.mTag.getServiceHandle());
            } catch (RemoteException e) {
                Log.m109e(TAG, "NFC service dead", e);
            }
        } finally {
            this.mIsConnected = false;
            this.mTag.setTechnologyDisconnected();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMaxTransceiveLengthInternal() {
        try {
            return this.mTag.getTagService().getMaxTransceiveLength(this.mSelectedTechnology);
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] transceive(byte[] data, boolean raw) throws IOException {
        checkConnected();
        try {
            TransceiveResult result = this.mTag.getTagService().transceive(this.mTag.getServiceHandle(), data, raw);
            if (result == null) {
                throw new IOException("transceive failed");
            }
            return result.getResponseOrThrow();
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
            throw new IOException("NFC service died");
        }
    }
}
