package android.nfc;

import com.android.internal.midi.MidiConstants;
/* loaded from: classes2.dex */
public final class NfcEvent {
    public final NfcAdapter nfcAdapter;
    public final int peerLlcpMajorVersion;
    public final int peerLlcpMinorVersion;

    NfcEvent(NfcAdapter nfcAdapter, byte peerLlcpVersion) {
        this.nfcAdapter = nfcAdapter;
        this.peerLlcpMajorVersion = (peerLlcpVersion & 240) >> 4;
        this.peerLlcpMinorVersion = peerLlcpVersion & MidiConstants.STATUS_CHANNEL_MASK;
    }
}
