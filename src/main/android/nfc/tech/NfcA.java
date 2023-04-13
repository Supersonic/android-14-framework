package android.nfc.tech;

import android.nfc.Tag;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.util.Log;
import java.io.IOException;
/* loaded from: classes2.dex */
public final class NfcA extends BasicTagTechnology {
    public static final String EXTRA_ATQA = "atqa";
    public static final String EXTRA_SAK = "sak";
    private static final String TAG = "NFC";
    private byte[] mAtqa;
    private short mSak;

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

    public static NfcA get(Tag tag) {
        if (tag.hasTech(1)) {
            try {
                return new NfcA(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NfcA(Tag tag) throws RemoteException {
        super(tag, 1);
        Bundle extras = tag.getTechExtras(1);
        this.mSak = extras.getShort(EXTRA_SAK);
        this.mAtqa = extras.getByteArray(EXTRA_ATQA);
    }

    public byte[] getAtqa() {
        return this.mAtqa;
    }

    public short getSak() {
        return this.mSak;
    }

    public byte[] transceive(byte[] data) throws IOException {
        return transceive(data, true);
    }

    public int getMaxTransceiveLength() {
        return getMaxTransceiveLengthInternal();
    }

    public void setTimeout(int timeout) {
        try {
            int err = this.mTag.getTagService().setTimeout(1, timeout);
            if (err != 0) {
                throw new IllegalArgumentException("The supplied timeout is not valid");
            }
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
        }
    }

    public int getTimeout() {
        try {
            return this.mTag.getTagService().getTimeout(1);
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
            return 0;
        }
    }
}
