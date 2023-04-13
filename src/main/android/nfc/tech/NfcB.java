package android.nfc.tech;

import android.nfc.Tag;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import java.io.IOException;
/* loaded from: classes2.dex */
public final class NfcB extends BasicTagTechnology {
    public static final String EXTRA_APPDATA = "appdata";
    public static final String EXTRA_PROTINFO = "protinfo";
    private byte[] mAppData;
    private byte[] mProtInfo;

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

    public static NfcB get(Tag tag) {
        if (tag.hasTech(2)) {
            try {
                return new NfcB(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NfcB(Tag tag) throws RemoteException {
        super(tag, 2);
        Bundle extras = tag.getTechExtras(2);
        this.mAppData = extras.getByteArray(EXTRA_APPDATA);
        this.mProtInfo = extras.getByteArray(EXTRA_PROTINFO);
    }

    public byte[] getApplicationData() {
        return this.mAppData;
    }

    public byte[] getProtocolInfo() {
        return this.mProtInfo;
    }

    public byte[] transceive(byte[] data) throws IOException {
        return transceive(data, true);
    }

    public int getMaxTransceiveLength() {
        return getMaxTransceiveLengthInternal();
    }
}
