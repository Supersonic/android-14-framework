package com.google.security.cryptauth.lib.securegcm;
/* loaded from: classes2.dex */
public class D2DConnectionContextV1 {
    public final long contextPtr;

    private static native byte[] decode_message_from_peer(long j, byte[] bArr, byte[] bArr2) throws CryptoException;

    private static native byte[] encode_message_to_peer(long j, byte[] bArr, byte[] bArr2) throws BadHandleException;

    private static native byte[] get_session_unique(long j) throws BadHandleException;

    static {
        System.loadLibrary("ukey2_jni");
    }

    public D2DConnectionContextV1(long j) {
        this.contextPtr = j;
    }

    public byte[] encodeMessageToPeer(byte[] bArr, byte[] bArr2) throws BadHandleException {
        return encode_message_to_peer(this.contextPtr, bArr, bArr2);
    }

    public byte[] decodeMessageFromPeer(byte[] bArr, byte[] bArr2) throws CryptoException {
        return decode_message_from_peer(this.contextPtr, bArr, bArr2);
    }

    public byte[] getSessionUnique() throws BadHandleException {
        return get_session_unique(this.contextPtr);
    }
}
