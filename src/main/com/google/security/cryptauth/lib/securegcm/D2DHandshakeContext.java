package com.google.security.cryptauth.lib.securegcm;
/* loaded from: classes2.dex */
public class D2DHandshakeContext {
    public final long context_ptr;

    /* loaded from: classes2.dex */
    public enum Role {
        Initiator,
        Responder
    }

    private static native long create_context(boolean z, Ukey2Logger ukey2Logger);

    private static native byte[] get_next_handshake_message(long j) throws BadHandleException;

    private static native boolean is_handshake_complete(long j) throws BadHandleException;

    private static native byte[] parse_handshake_message(long j, byte[] bArr) throws BadHandleException, HandshakeException;

    private static native long to_connection_context(long j) throws HandshakeException;

    static {
        System.loadLibrary("ukey2_jni");
    }

    public D2DHandshakeContext(Role role, Ukey2Logger ukey2Logger) {
        this.context_ptr = create_context(role == Role.Initiator, ukey2Logger);
    }

    public static D2DHandshakeContext forInitiator(Ukey2Logger ukey2Logger) {
        return new D2DHandshakeContext(Role.Initiator, ukey2Logger);
    }

    public static D2DHandshakeContext forResponder(Ukey2Logger ukey2Logger) {
        return new D2DHandshakeContext(Role.Responder, ukey2Logger);
    }

    public boolean isHandshakeComplete() throws BadHandleException {
        return is_handshake_complete(this.context_ptr);
    }

    public byte[] getNextHandshakeMessage() throws BadHandleException {
        return get_next_handshake_message(this.context_ptr);
    }

    public byte[] parseHandshakeMessage(byte[] bArr) throws BadHandleException, HandshakeException {
        return parse_handshake_message(this.context_ptr, bArr);
    }

    public D2DConnectionContextV1 toConnectionContext() throws HandshakeException {
        return new D2DConnectionContextV1(to_connection_context(this.context_ptr));
    }
}
