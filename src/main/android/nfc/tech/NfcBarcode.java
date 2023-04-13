package android.nfc.tech;

import android.nfc.Tag;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import java.io.IOException;
/* loaded from: classes2.dex */
public final class NfcBarcode extends BasicTagTechnology {
    public static final String EXTRA_BARCODE_TYPE = "barcodetype";
    public static final int TYPE_KOVIO = 1;
    public static final int TYPE_UNKNOWN = -1;
    private int mType;

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

    public static NfcBarcode get(Tag tag) {
        if (tag.hasTech(10)) {
            try {
                return new NfcBarcode(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NfcBarcode(Tag tag) throws RemoteException {
        super(tag, 10);
        Bundle extras = tag.getTechExtras(10);
        if (extras != null) {
            this.mType = extras.getInt(EXTRA_BARCODE_TYPE);
            return;
        }
        throw new NullPointerException("NfcBarcode tech extras are null.");
    }

    public int getType() {
        return this.mType;
    }

    public byte[] getBarcode() {
        switch (this.mType) {
            case 1:
                return this.mTag.getId();
            default:
                return null;
        }
    }
}
