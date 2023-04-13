package android.nfc.tech;

import android.nfc.Tag;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.util.Log;
import java.io.IOException;
/* loaded from: classes2.dex */
public final class NfcF extends BasicTagTechnology {
    public static final String EXTRA_PMM = "pmm";
    public static final String EXTRA_SC = "systemcode";
    private static final String TAG = "NFC";
    private byte[] mManufacturer;
    private byte[] mSystemCode;

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ void connect() throws IOException {
        super.connect();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ Tag getTag() {
        return super.getTag();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ boolean isConnected() {
        return super.isConnected();
    }

    @Override // android.nfc.tech.BasicTagTechnology, android.nfc.tech.TagTechnology
    public /* bridge */ /* synthetic */ void reconnect() throws IOException {
        super.reconnect();
    }

    public static NfcF get(Tag tag) {
        if (tag.hasTech(4)) {
            try {
                return new NfcF(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NfcF(Tag tag) throws RemoteException {
        super(tag, 4);
        this.mSystemCode = null;
        this.mManufacturer = null;
        Bundle extras = tag.getTechExtras(4);
        if (extras != null) {
            this.mSystemCode = extras.getByteArray(EXTRA_SC);
            this.mManufacturer = extras.getByteArray(EXTRA_PMM);
        }
    }

    public byte[] getSystemCode() {
        return this.mSystemCode;
    }

    public byte[] getManufacturer() {
        return this.mManufacturer;
    }

    public byte[] transceive(byte[] data) throws IOException {
        return transceive(data, true);
    }

    public int getMaxTransceiveLength() {
        return getMaxTransceiveLengthInternal();
    }

    public void setTimeout(int timeout) {
        try {
            int err = this.mTag.getTagService().setTimeout(4, timeout);
            if (err != 0) {
                throw new IllegalArgumentException("The supplied timeout is not valid");
            }
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
        }
    }

    public int getTimeout() {
        try {
            return this.mTag.getTagService().getTimeout(4);
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
            return 0;
        }
    }
}
