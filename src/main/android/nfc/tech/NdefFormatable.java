package android.nfc.tech;

import android.nfc.FormatException;
import android.nfc.INfcTag;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.p008os.RemoteException;
import android.util.Log;
import java.io.IOException;
/* loaded from: classes2.dex */
public final class NdefFormatable extends BasicTagTechnology {
    private static final String TAG = "NFC";

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

    public static NdefFormatable get(Tag tag) {
        if (tag.hasTech(7)) {
            try {
                return new NdefFormatable(tag);
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    public NdefFormatable(Tag tag) throws RemoteException {
        super(tag, 7);
    }

    public void format(NdefMessage firstMessage) throws IOException, FormatException {
        format(firstMessage, false);
    }

    public void formatReadOnly(NdefMessage firstMessage) throws IOException, FormatException {
        format(firstMessage, true);
    }

    void format(NdefMessage firstMessage, boolean makeReadOnly) throws IOException, FormatException {
        checkConnected();
        try {
            int serviceHandle = this.mTag.getServiceHandle();
            INfcTag tagService = this.mTag.getTagService();
            int errorCode = tagService.formatNdef(serviceHandle, MifareClassic.KEY_DEFAULT);
            switch (errorCode) {
                case -8:
                    throw new FormatException();
                case -1:
                    throw new IOException();
                case 0:
                    if (!tagService.isNdef(serviceHandle)) {
                        throw new IOException();
                    }
                    if (firstMessage != null) {
                        int errorCode2 = tagService.ndefWrite(serviceHandle, firstMessage);
                        switch (errorCode2) {
                            case -8:
                                throw new FormatException();
                            case -1:
                                throw new IOException();
                            case 0:
                                break;
                            default:
                                throw new IOException();
                        }
                    }
                    if (makeReadOnly) {
                        int errorCode3 = tagService.ndefMakeReadOnly(serviceHandle);
                        switch (errorCode3) {
                            case -8:
                                throw new IOException();
                            case -1:
                                throw new IOException();
                            case 0:
                                break;
                            default:
                                throw new IOException();
                        }
                    }
                    return;
                default:
                    throw new IOException();
            }
        } catch (RemoteException e) {
            Log.m109e(TAG, "NFC service dead", e);
        }
    }
}
