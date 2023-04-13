package com.android.server.companion.securechannel;

import android.content.Context;
import android.os.Build;
import android.util.Slog;
import com.google.security.cryptauth.lib.securegcm.BadHandleException;
import com.google.security.cryptauth.lib.securegcm.CryptoException;
import com.google.security.cryptauth.lib.securegcm.D2DConnectionContextV1;
import com.google.security.cryptauth.lib.securegcm.D2DHandshakeContext;
import com.google.security.cryptauth.lib.securegcm.DefaultUkey2Logger;
import com.google.security.cryptauth.lib.securegcm.HandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;
import libcore.io.IoUtils;
import libcore.io.Streams;
/* loaded from: classes.dex */
public class SecureChannel {
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    public String mAlias;
    public final Callback mCallback;
    public D2DConnectionContextV1 mConnectionContext;
    public D2DHandshakeContext mHandshakeContext;
    public boolean mInProgress;
    public final InputStream mInput;
    public final OutputStream mOutput;
    public final byte[] mPreSharedKey;
    public D2DHandshakeContext.Role mRole;
    public volatile boolean mStopped;
    public int mVerificationResult;
    public final AttestationVerifier mVerifier;

    /* loaded from: classes.dex */
    public interface Callback {
        void onError(Throwable th);

        void onSecureConnection();

        void onSecureMessageReceived(byte[] bArr);
    }

    public SecureChannel(InputStream inputStream, OutputStream outputStream, Callback callback, Context context) {
        this(inputStream, outputStream, callback, null, new AttestationVerifier(context));
    }

    public SecureChannel(InputStream inputStream, OutputStream outputStream, Callback callback, byte[] bArr, AttestationVerifier attestationVerifier) {
        this.mInput = inputStream;
        this.mOutput = outputStream;
        this.mCallback = callback;
        this.mPreSharedKey = bArr;
        this.mVerifier = attestationVerifier;
    }

    public void start() {
        new Thread(new Runnable() { // from class: com.android.server.companion.securechannel.SecureChannel$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SecureChannel.this.lambda$start$0();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$start$0() {
        try {
            exchangeHandshake();
            exchangeAuthentication();
            this.mInProgress = false;
            this.mCallback.onSecureConnection();
            while (!this.mStopped) {
                receiveSecureMessage();
            }
        } catch (Exception e) {
            if (this.mStopped) {
                return;
            }
            Slog.e("CDM_SecureChannel", "Secure channel encountered an error.", e);
            stop();
            this.mCallback.onError(e);
        }
    }

    public void stop() {
        if (DEBUG) {
            Slog.d("CDM_SecureChannel", "Stopping secure channel.");
        }
        this.mStopped = true;
        this.mInProgress = false;
        IoUtils.closeQuietly(this.mInput);
        IoUtils.closeQuietly(this.mOutput);
        KeyStoreUtils.cleanUp(this.mAlias);
    }

    public void establishSecureConnection() throws IOException, SecureChannelException {
        if (isSecured()) {
            Slog.d("CDM_SecureChannel", "Channel is already secure.");
        } else if (this.mInProgress) {
            Slog.w("CDM_SecureChannel", "Channel has already started establishing secure connection.");
        } else {
            try {
                initiateHandshake();
                this.mInProgress = true;
            } catch (BadHandleException e) {
                throw new SecureChannelException("Failed to initiate handshake protocol.", e);
            }
        }
    }

    public void sendSecureMessage(byte[] bArr) throws IOException {
        if (!isSecured()) {
            Slog.d("CDM_SecureChannel", "Cannot send a message without a secure connection.");
            throw new IllegalStateException("Channel is not secured yet.");
        }
        try {
            sendMessage(MessageType.SECURE_MESSAGE, bArr);
        } catch (BadHandleException e) {
            throw new SecureChannelException("Failed to encrypt data.", e);
        }
    }

    public final void receiveSecureMessage() throws IOException, CryptoException {
        if (!isSecured()) {
            Slog.d("CDM_SecureChannel", "Received a message without a secure connection. Message will be ignored.");
            this.mCallback.onError(new IllegalStateException("Connection is not secure."));
            return;
        }
        try {
            this.mCallback.onSecureMessageReceived(readMessage(MessageType.SECURE_MESSAGE));
        } catch (SecureChannelException e) {
            Slog.w("CDM_SecureChannel", "Ignoring received message.", e);
        }
    }

    public final byte[] readMessage(MessageType messageType) throws IOException, SecureChannelException, CryptoException {
        if (DEBUG) {
            if (isSecured()) {
                Slog.d("CDM_SecureChannel", "Waiting to receive next secure message.");
            } else {
                Slog.d("CDM_SecureChannel", "Waiting to receive next message.");
            }
        }
        byte[] bArr = new byte[6];
        Streams.readFully(this.mInput, bArr);
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        int i = wrap.getInt();
        short s = wrap.getShort();
        if (i != 1) {
            Streams.skipByReading(this.mInput, Long.MAX_VALUE);
            throw new SecureChannelException("Secure channel version mismatch. Currently on version 1. Skipping rest of data.");
        } else if (s != messageType.mValue) {
            Streams.skipByReading(this.mInput, Long.MAX_VALUE);
            throw new SecureChannelException("Unexpected message type. Expected " + messageType.name() + "; Found " + MessageType.from(s).name() + ". Skipping rest of data.");
        } else {
            byte[] bArr2 = new byte[4];
            Streams.readFully(this.mInput, bArr2);
            try {
                byte[] bArr3 = new byte[ByteBuffer.wrap(bArr2).getInt()];
                Streams.readFully(this.mInput, bArr3);
                return !MessageType.shouldEncrypt(messageType) ? bArr3 : this.mConnectionContext.decodeMessageFromPeer(bArr3, bArr);
            } catch (OutOfMemoryError e) {
                throw new SecureChannelException("Payload is too large.", e);
            }
        }
    }

    public final void sendMessage(MessageType messageType, byte[] bArr) throws IOException, BadHandleException {
        synchronized (this.mOutput) {
            byte[] array = ByteBuffer.allocate(6).putInt(1).putShort(messageType.mValue).array();
            if (MessageType.shouldEncrypt(messageType)) {
                bArr = this.mConnectionContext.encodeMessageToPeer(bArr, array);
            }
            this.mOutput.write(array);
            this.mOutput.write(ByteBuffer.allocate(4).putInt(bArr.length).array());
            this.mOutput.write(bArr);
            this.mOutput.flush();
        }
    }

    public final void initiateHandshake() throws IOException, BadHandleException {
        if (this.mConnectionContext != null) {
            Slog.d("CDM_SecureChannel", "Ukey2 handshake is already completed.");
            return;
        }
        this.mRole = D2DHandshakeContext.Role.Initiator;
        this.mHandshakeContext = D2DHandshakeContext.forInitiator(DefaultUkey2Logger.INSTANCE);
        if (DEBUG) {
            Slog.d("CDM_SecureChannel", "Sending Ukey2 Client Init message");
        }
        sendMessage(MessageType.HANDSHAKE_INIT, this.mHandshakeContext.getNextHandshakeMessage());
    }

    public final void exchangeHandshake() throws IOException, HandshakeException, BadHandleException, CryptoException {
        if (this.mConnectionContext != null) {
            Slog.d("CDM_SecureChannel", "Ukey2 handshake is already completed.");
            return;
        }
        MessageType messageType = MessageType.HANDSHAKE_INIT;
        byte[] readMessage = readMessage(messageType);
        if (this.mHandshakeContext == null) {
            this.mRole = D2DHandshakeContext.Role.Responder;
            this.mHandshakeContext = D2DHandshakeContext.forResponder(DefaultUkey2Logger.INSTANCE);
            boolean z = DEBUG;
            if (z) {
                Slog.d("CDM_SecureChannel", "Receiving Ukey2 Client Init message");
            }
            this.mHandshakeContext.parseHandshakeMessage(readMessage);
            if (z) {
                Slog.d("CDM_SecureChannel", "Sending Ukey2 Server Init message");
            }
            sendMessage(messageType, this.mHandshakeContext.getNextHandshakeMessage());
            if (z) {
                Slog.d("CDM_SecureChannel", "Receiving Ukey2 Client Finish message");
            }
            this.mHandshakeContext.parseHandshakeMessage(readMessage(MessageType.HANDSHAKE_FINISH));
        } else {
            boolean z2 = DEBUG;
            if (z2) {
                Slog.d("CDM_SecureChannel", "Receiving Ukey2 Server Init message");
            }
            this.mHandshakeContext.parseHandshakeMessage(readMessage);
            if (z2) {
                Slog.d("CDM_SecureChannel", "Sending Ukey2 Client Finish message");
            }
            sendMessage(MessageType.HANDSHAKE_FINISH, this.mHandshakeContext.getNextHandshakeMessage());
        }
        if (this.mHandshakeContext.isHandshakeComplete()) {
            if (DEBUG) {
                Slog.d("CDM_SecureChannel", "Ukey2 Handshake completed successfully");
            }
            this.mConnectionContext = this.mHandshakeContext.toConnectionContext();
            return;
        }
        Slog.e("CDM_SecureChannel", "Failed to complete Ukey2 Handshake");
        throw new IllegalStateException("Ukey2 Handshake did not complete as expected.");
    }

    public final void exchangeAuthentication() throws IOException, GeneralSecurityException, BadHandleException, CryptoException {
        if (this.mPreSharedKey != null) {
            exchangePreSharedKey();
        }
        if (this.mVerifier != null) {
            exchangeAttestation();
        }
    }

    public final void exchangePreSharedKey() throws IOException, GeneralSecurityException, BadHandleException, CryptoException {
        boolean z = DEBUG;
        if (z) {
            Slog.d("CDM_SecureChannel", "Exchanging pre-shared keys.");
        }
        MessageType messageType = MessageType.PRE_SHARED_KEY;
        sendMessage(messageType, constructToken(this.mRole, this.mPreSharedKey));
        byte[] readMessage = readMessage(messageType);
        D2DHandshakeContext.Role role = this.mRole;
        D2DHandshakeContext.Role role2 = D2DHandshakeContext.Role.Initiator;
        if (role == role2) {
            role2 = D2DHandshakeContext.Role.Responder;
        }
        if (!Arrays.equals(readMessage, constructToken(role2, this.mPreSharedKey))) {
            throw new SecureChannelException("Failed to verify the hash of pre-shared key.");
        }
        if (z) {
            Slog.d("CDM_SecureChannel", "The pre-shared key was successfully authenticated.");
        }
    }

    public final void exchangeAttestation() throws IOException, GeneralSecurityException, BadHandleException, CryptoException {
        if (this.mVerificationResult == 1) {
            Slog.d("CDM_SecureChannel", "Remote attestation was already verified.");
            return;
        }
        boolean z = DEBUG;
        if (z) {
            Slog.d("CDM_SecureChannel", "Exchanging device attestation.");
        }
        if (this.mAlias == null) {
            this.mAlias = generateAlias();
        }
        KeyStoreUtils.generateAttestationKeyPair(this.mAlias, constructToken(this.mRole, this.mConnectionContext.getSessionUnique()));
        byte[] encodedCertificateChain = KeyStoreUtils.getEncodedCertificateChain(this.mAlias);
        MessageType messageType = MessageType.ATTESTATION;
        sendMessage(messageType, encodedCertificateChain);
        byte[] readMessage = readMessage(messageType);
        D2DHandshakeContext.Role role = this.mRole;
        D2DHandshakeContext.Role role2 = D2DHandshakeContext.Role.Initiator;
        if (role == role2) {
            role2 = D2DHandshakeContext.Role.Responder;
        }
        this.mVerificationResult = this.mVerifier.verifyAttestation(readMessage, constructToken(role2, this.mConnectionContext.getSessionUnique()));
        byte[] array = ByteBuffer.allocate(4).putInt(this.mVerificationResult).array();
        MessageType messageType2 = MessageType.AVF_RESULT;
        sendMessage(messageType2, array);
        if (ByteBuffer.wrap(readMessage(messageType2)).getInt() != 1) {
            throw new SecureChannelException("Remote device failed to verify local attestation.");
        }
        if (this.mVerificationResult != 1) {
            throw new SecureChannelException("Failed to verify remote attestation.");
        }
        if (z) {
            Slog.d("CDM_SecureChannel", "Remote attestation was successfully verified.");
        }
    }

    public final boolean isSecured() {
        if (this.mConnectionContext == null) {
            return false;
        }
        return this.mVerifier == null || this.mVerificationResult == 1;
    }

    public final byte[] constructToken(D2DHandshakeContext.Role role, byte[] bArr) throws GeneralSecurityException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = role.name().getBytes(StandardCharsets.UTF_8);
        return messageDigest.digest(ByteBuffer.allocate(bytes.length + bArr.length).put(bytes).put(bArr).array());
    }

    public final String generateAlias() {
        String str;
        do {
            str = "secure-channel-" + UUID.randomUUID();
        } while (KeyStoreUtils.aliasExists(str));
        return str;
    }

    /* loaded from: classes.dex */
    public enum MessageType {
        HANDSHAKE_INIT(18505),
        HANDSHAKE_FINISH(18502),
        PRE_SHARED_KEY(20555),
        ATTESTATION(16724),
        AVF_RESULT(22098),
        SECURE_MESSAGE(21325),
        UNKNOWN(0);
        
        private final short mValue;

        MessageType(int i) {
            this.mValue = (short) i;
        }

        public static MessageType from(short s) {
            MessageType[] values;
            for (MessageType messageType : values()) {
                if (s == messageType.mValue) {
                    return messageType;
                }
            }
            return UNKNOWN;
        }

        public static boolean shouldEncrypt(MessageType messageType) {
            return (messageType == HANDSHAKE_INIT || messageType == HANDSHAKE_FINISH) ? false : true;
        }
    }
}
