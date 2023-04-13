package android.media;

import android.hardware.cas.IDescrambler;
import android.hardware.cas.V1_0.IDescramblerBase;
import android.media.MediaCas;
import android.media.MediaCasException;
import android.media.MediaCodec;
import android.p008os.IHwBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class MediaDescrambler implements AutoCloseable {
    public static final byte SCRAMBLE_CONTROL_EVEN_KEY = 2;
    public static final byte SCRAMBLE_CONTROL_ODD_KEY = 3;
    public static final byte SCRAMBLE_CONTROL_RESERVED = 1;
    public static final byte SCRAMBLE_CONTROL_UNSCRAMBLED = 0;
    public static final byte SCRAMBLE_FLAG_PES_HEADER = 1;
    private static final String TAG = "MediaDescrambler";
    private DescramblerWrapper mIDescrambler;
    private boolean mIsAidlHal;
    private long mNativeContext;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface DescramblerWrapper {
        IHwBinder asBinder();

        int descramble(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, MediaCodec.CryptoInfo cryptoInfo) throws RemoteException;

        void release() throws RemoteException;

        boolean requiresSecureDecoderComponent(String str) throws RemoteException;

        void setMediaCasSession(byte[] bArr) throws RemoteException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final native int native_descramble(byte b, byte b2, int i, int[] iArr, int[] iArr2, ByteBuffer byteBuffer, int i2, int i3, ByteBuffer byteBuffer2, int i4, int i5) throws RemoteException;

    private static final native void native_init();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void native_release();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void native_setup(IHwBinder iHwBinder);

    /* loaded from: classes2.dex */
    private class AidlDescrambler implements DescramblerWrapper {
        IDescrambler mAidlDescrambler;

        AidlDescrambler(IDescrambler aidlDescrambler) throws Exception {
            if (aidlDescrambler != null) {
                this.mAidlDescrambler = aidlDescrambler;
                return;
            }
            throw new Exception("Descrambler could not be created");
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public IHwBinder asBinder() {
            return null;
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public int descramble(ByteBuffer src, ByteBuffer dst, MediaCodec.CryptoInfo cryptoInfo) throws RemoteException {
            throw new RemoteException("Not supported");
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public boolean requiresSecureDecoderComponent(String mime) throws RemoteException {
            throw new RemoteException("Not supported");
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public void setMediaCasSession(byte[] sessionId) throws RemoteException {
            throw new RemoteException("Not supported");
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public void release() throws RemoteException {
            this.mAidlDescrambler.release();
        }
    }

    /* loaded from: classes2.dex */
    private class HidlDescrambler implements DescramblerWrapper {
        IDescramblerBase mHidlDescrambler;

        HidlDescrambler(IDescramblerBase hidlDescrambler) throws Exception {
            if (hidlDescrambler != null) {
                this.mHidlDescrambler = hidlDescrambler;
                MediaDescrambler.this.native_setup(hidlDescrambler.asBinder());
                return;
            }
            throw new Exception("Descrambler could not be created");
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public IHwBinder asBinder() {
            return this.mHidlDescrambler.asBinder();
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public int descramble(ByteBuffer srcBuf, ByteBuffer dstBuf, MediaCodec.CryptoInfo cryptoInfo) throws RemoteException {
            try {
                return MediaDescrambler.this.native_descramble(cryptoInfo.key[0], cryptoInfo.key[1], cryptoInfo.numSubSamples, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData, srcBuf, srcBuf.position(), srcBuf.limit(), dstBuf, dstBuf.position(), dstBuf.limit());
            } catch (RemoteException e) {
                MediaDescrambler.this.cleanupAndRethrowIllegalState();
                return -1;
            } catch (ServiceSpecificException e2) {
                MediaCasStateException.throwExceptionIfNeeded(e2.errorCode, e2.getMessage());
                return -1;
            }
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public boolean requiresSecureDecoderComponent(String mime) throws RemoteException {
            return this.mHidlDescrambler.requiresSecureDecoderComponent(mime);
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public void setMediaCasSession(byte[] sessionId) throws RemoteException {
            ArrayList<Byte> byteArray = new ArrayList<>();
            if (sessionId != null) {
                int length = sessionId.length;
                byteArray = new ArrayList<>(length);
                for (byte b : sessionId) {
                    byteArray.add(Byte.valueOf(b));
                }
            }
            MediaCasStateException.throwExceptionIfNeeded(this.mHidlDescrambler.setMediaCasSession(byteArray));
        }

        @Override // android.media.MediaDescrambler.DescramblerWrapper
        public void release() throws RemoteException {
            this.mHidlDescrambler.release();
            MediaDescrambler.this.native_release();
        }
    }

    private final void validateInternalStates() {
        if (this.mIDescrambler == null) {
            throw new IllegalStateException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void cleanupAndRethrowIllegalState() {
        this.mIDescrambler = null;
        throw new IllegalStateException();
    }

    public MediaDescrambler(int CA_system_id) throws MediaCasException.UnsupportedCasException {
        try {
            try {
                if (MediaCas.getService() != null) {
                    this.mIDescrambler = new AidlDescrambler(MediaCas.getService().createDescrambler(CA_system_id));
                    this.mIsAidlHal = true;
                } else if (MediaCas.getServiceHidl() != null) {
                    this.mIDescrambler = new HidlDescrambler(MediaCas.getServiceHidl().createDescrambler(CA_system_id));
                    this.mIsAidlHal = false;
                } else {
                    throw new Exception("No CAS service found!");
                }
                if (this.mIDescrambler == null) {
                    throw new MediaCasException.UnsupportedCasException("Unsupported CA_system_id " + CA_system_id);
                }
            } catch (Exception e) {
                Log.m110e(TAG, "Failed to create descrambler: " + e);
                this.mIDescrambler = null;
                throw new MediaCasException.UnsupportedCasException("Unsupported CA_system_id " + CA_system_id);
            }
        } catch (Throwable th) {
            if (this.mIDescrambler == null) {
                throw new MediaCasException.UnsupportedCasException("Unsupported CA_system_id " + CA_system_id);
            }
            throw th;
        }
    }

    public boolean isAidlHal() {
        return this.mIsAidlHal;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IHwBinder getBinder() {
        validateInternalStates();
        return this.mIDescrambler.asBinder();
    }

    public final boolean requiresSecureDecoderComponent(String mime) {
        validateInternalStates();
        try {
            return this.mIDescrambler.requiresSecureDecoderComponent(mime);
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
            return true;
        }
    }

    public final void setMediaCasSession(MediaCas.Session session) {
        validateInternalStates();
        try {
            this.mIDescrambler.setMediaCasSession(session.mSessionId);
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
    }

    public final int descramble(ByteBuffer srcBuf, ByteBuffer dstBuf, MediaCodec.CryptoInfo cryptoInfo) {
        validateInternalStates();
        if (cryptoInfo.numSubSamples <= 0) {
            throw new IllegalArgumentException("Invalid CryptoInfo: invalid numSubSamples=" + cryptoInfo.numSubSamples);
        }
        if (cryptoInfo.numBytesOfClearData == null && cryptoInfo.numBytesOfEncryptedData == null) {
            throw new IllegalArgumentException("Invalid CryptoInfo: clearData and encryptedData size arrays are both null!");
        }
        if (cryptoInfo.numBytesOfClearData != null && cryptoInfo.numBytesOfClearData.length < cryptoInfo.numSubSamples) {
            throw new IllegalArgumentException("Invalid CryptoInfo: numBytesOfClearData is too small!");
        }
        if (cryptoInfo.numBytesOfEncryptedData != null && cryptoInfo.numBytesOfEncryptedData.length < cryptoInfo.numSubSamples) {
            throw new IllegalArgumentException("Invalid CryptoInfo: numBytesOfEncryptedData is too small!");
        }
        if (cryptoInfo.key == null || cryptoInfo.key.length != 16) {
            throw new IllegalArgumentException("Invalid CryptoInfo: key array is invalid!");
        }
        try {
            return this.mIDescrambler.descramble(srcBuf, dstBuf, cryptoInfo);
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
            return -1;
        } catch (ServiceSpecificException e2) {
            MediaCasStateException.throwExceptionIfNeeded(e2.errorCode, e2.getMessage());
            return -1;
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        DescramblerWrapper descramblerWrapper = this.mIDescrambler;
        if (descramblerWrapper != null) {
            try {
                descramblerWrapper.release();
            } catch (RemoteException e) {
            } catch (Throwable th) {
                this.mIDescrambler = null;
                throw th;
            }
            this.mIDescrambler = null;
        }
    }

    protected void finalize() {
        close();
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
