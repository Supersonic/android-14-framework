package android.media;

import android.app.ActivityThread;
import android.app.Application;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.Signature;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaDrm;
import android.media.metrics.LogSessionId;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.Looper;
import android.p008os.Parcel;
import android.p008os.PersistableBundle;
import android.text.format.DateFormat;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes2.dex */
public final class MediaDrm implements AutoCloseable {
    public static final int CERTIFICATE_TYPE_NONE = 0;
    public static final int CERTIFICATE_TYPE_X509 = 1;
    private static final int DRM_EVENT = 200;
    public static final int EVENT_KEY_EXPIRED = 3;
    public static final int EVENT_KEY_REQUIRED = 2;
    public static final int EVENT_PROVISION_REQUIRED = 1;
    public static final int EVENT_SESSION_RECLAIMED = 5;
    public static final int EVENT_VENDOR_DEFINED = 4;
    private static final int EXPIRATION_UPDATE = 201;
    public static final int HDCP_LEVEL_UNKNOWN = 0;
    public static final int HDCP_NONE = 1;
    public static final int HDCP_NO_DIGITAL_OUTPUT = Integer.MAX_VALUE;
    public static final int HDCP_V1 = 2;
    public static final int HDCP_V2 = 3;
    public static final int HDCP_V2_1 = 4;
    public static final int HDCP_V2_2 = 5;
    public static final int HDCP_V2_3 = 6;
    private static final int KEY_STATUS_CHANGE = 202;
    public static final int KEY_TYPE_OFFLINE = 2;
    public static final int KEY_TYPE_RELEASE = 3;
    public static final int KEY_TYPE_STREAMING = 1;
    public static final int OFFLINE_LICENSE_STATE_RELEASED = 2;
    public static final int OFFLINE_LICENSE_STATE_UNKNOWN = 0;
    public static final int OFFLINE_LICENSE_STATE_USABLE = 1;
    private static final String PERMISSION = "android.permission.ACCESS_DRM_CERTIFICATES";
    public static final String PROPERTY_ALGORITHMS = "algorithms";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_DEVICE_UNIQUE_ID = "deviceUniqueId";
    public static final String PROPERTY_VENDOR = "vendor";
    public static final String PROPERTY_VERSION = "version";
    public static final int SECURITY_LEVEL_HW_SECURE_ALL = 5;
    public static final int SECURITY_LEVEL_HW_SECURE_CRYPTO = 3;
    public static final int SECURITY_LEVEL_HW_SECURE_DECODE = 4;
    public static final int SECURITY_LEVEL_MAX = 6;
    public static final int SECURITY_LEVEL_SW_SECURE_CRYPTO = 1;
    public static final int SECURITY_LEVEL_SW_SECURE_DECODE = 2;
    public static final int SECURITY_LEVEL_UNKNOWN = 0;
    private static final int SESSION_LOST_STATE = 203;
    private static final String TAG = "MediaDrm";
    private final String mAppPackageName;
    private final CloseGuard mCloseGuard;
    private final AtomicBoolean mClosed = new AtomicBoolean();
    private final Map<Integer, ListenerWithExecutor> mListenerMap;
    private long mNativeContext;
    private final Map<ByteBuffer, PlaybackComponent> mPlaybackComponentMap;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ArrayProperty {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface CertificateType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface DrmEvent {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Deprecated
    /* loaded from: classes2.dex */
    public @interface HdcpLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface KeyType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MediaDrmErrorCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface OfflineLicenseState {
    }

    /* loaded from: classes2.dex */
    public interface OnEventListener {
        void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2);
    }

    /* loaded from: classes2.dex */
    public interface OnExpirationUpdateListener {
        void onExpirationUpdate(MediaDrm mediaDrm, byte[] bArr, long j);
    }

    /* loaded from: classes2.dex */
    public interface OnKeyStatusChangeListener {
        void onKeyStatusChange(MediaDrm mediaDrm, byte[] bArr, List<KeyStatus> list, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface OnSessionLostStateListener {
        void onSessionLostState(MediaDrm mediaDrm, byte[] bArr);
    }

    @Retention(RetentionPolicy.SOURCE)
    @Deprecated
    /* loaded from: classes2.dex */
    public @interface SecurityLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface StringProperty {
    }

    private native void closeSessionNative(byte[] bArr);

    public static final native byte[] decryptNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    public static final native byte[] encryptNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    private native KeyRequest getKeyRequestNative(byte[] bArr, byte[] bArr2, String str, int i, HashMap<String, String> hashMap) throws NotProvisionedException;

    private native PersistableBundle getMetricsNative();

    private native ProvisionRequest getProvisionRequestNative(int i, String str);

    private static final native byte[] getSupportedCryptoSchemesNative();

    private static final native boolean isCryptoSchemeSupportedNative(byte[] bArr, String str, int i);

    private static final native void native_init();

    private final native void native_setup(Object obj, byte[] bArr, String str);

    private native byte[] openSessionNative(int i) throws NotProvisionedException, ResourceBusyException;

    private native Certificate provideProvisionResponseNative(byte[] bArr) throws DeniedByServerException;

    public static final native void setCipherAlgorithmNative(MediaDrm mediaDrm, byte[] bArr, String str);

    public static final native void setMacAlgorithmNative(MediaDrm mediaDrm, byte[] bArr, String str);

    public native void setPlaybackId(byte[] bArr, String str);

    public static final native byte[] signNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3);

    private static final native byte[] signRSANative(MediaDrm mediaDrm, byte[] bArr, String str, byte[] bArr2, byte[] bArr3);

    public static final native boolean verifyNative(MediaDrm mediaDrm, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    public native int getConnectedHdcpLevel();

    public native List<LogMessage> getLogMessages();

    public native int getMaxHdcpLevel();

    public native int getMaxSessionCount();

    public native List<byte[]> getOfflineLicenseKeySetIds();

    public native int getOfflineLicenseState(byte[] bArr);

    public native int getOpenSessionCount();

    public native byte[] getPropertyByteArray(String str);

    public native String getPropertyString(String str);

    public native byte[] getSecureStop(byte[] bArr);

    public native List<byte[]> getSecureStopIds();

    public native List<byte[]> getSecureStops();

    public native int getSecurityLevel(byte[] bArr);

    public final native void native_release();

    public native byte[] provideKeyResponse(byte[] bArr, byte[] bArr2) throws NotProvisionedException, DeniedByServerException;

    public native HashMap<String, String> queryKeyStatus(byte[] bArr);

    public native void releaseSecureStops(byte[] bArr);

    public native void removeAllSecureStops();

    public native void removeKeys(byte[] bArr);

    public native void removeOfflineLicense(byte[] bArr);

    public native void removeSecureStop(byte[] bArr);

    public native boolean requiresSecureDecoder(String str, int i);

    public native void restoreKeys(byte[] bArr, byte[] bArr2);

    public native void setPropertyByteArray(String str, byte[] bArr);

    public native void setPropertyString(String str, String str2);

    public static final boolean isCryptoSchemeSupported(UUID uuid) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), null, 0);
    }

    public static final boolean isCryptoSchemeSupported(UUID uuid, String mimeType) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), mimeType, 0);
    }

    public static final boolean isCryptoSchemeSupported(UUID uuid, String mimeType, int securityLevel) {
        return isCryptoSchemeSupportedNative(getByteArrayFromUUID(uuid), mimeType, securityLevel);
    }

    public static final List<UUID> getSupportedCryptoSchemes() {
        byte[] uuidBytes = getSupportedCryptoSchemesNative();
        return getUUIDsFromByteArray(uuidBytes);
    }

    private static final byte[] getByteArrayFromUUID(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] uuidBytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte) (msb >>> ((7 - i) * 8));
            uuidBytes[i + 8] = (byte) (lsb >>> ((7 - i) * 8));
        }
        return uuidBytes;
    }

    private static final UUID getUUIDFromByteArray(byte[] uuidBytes, int off) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (uuidBytes[off + i] & 255);
            lsb = (lsb << 8) | (uuidBytes[off + i + 8] & 255);
        }
        return new UUID(msb, lsb);
    }

    private static final List<UUID> getUUIDsFromByteArray(byte[] uuidBytes) {
        Set<UUID> uuids = new LinkedHashSet<>();
        for (int off = 0; off < uuidBytes.length; off += 16) {
            uuids.add(getUUIDFromByteArray(uuidBytes, off));
        }
        return new ArrayList(uuids);
    }

    private Handler createHandler() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            Handler handler = new Handler(looper);
            return handler;
        }
        Looper looper2 = Looper.getMainLooper();
        if (looper2 != null) {
            Handler handler2 = new Handler(looper2);
            return handler2;
        }
        return null;
    }

    public MediaDrm(UUID uuid) throws UnsupportedSchemeException {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mListenerMap = new ConcurrentHashMap();
        this.mPlaybackComponentMap = new ConcurrentHashMap();
        String currentOpPackageName = ActivityThread.currentOpPackageName();
        this.mAppPackageName = currentOpPackageName;
        native_setup(new WeakReference(this), getByteArrayFromUUID(uuid), currentOpPackageName);
        closeGuard.open("release");
    }

    /* loaded from: classes2.dex */
    public static final class ErrorCodes {
        public static final int ERROR_CERTIFICATE_MALFORMED = 10;
        public static final int ERROR_CERTIFICATE_MISSING = 11;
        public static final int ERROR_CRYPTO_LIBRARY = 12;
        public static final int ERROR_FRAME_TOO_LARGE = 8;
        public static final int ERROR_GENERIC_OEM = 13;
        public static final int ERROR_GENERIC_PLUGIN = 14;
        public static final int ERROR_INIT_DATA = 15;
        public static final int ERROR_INSUFFICIENT_OUTPUT_PROTECTION = 4;
        public static final int ERROR_INSUFFICIENT_SECURITY = 7;
        public static final int ERROR_KEY_EXPIRED = 2;
        public static final int ERROR_KEY_NOT_LOADED = 16;
        public static final int ERROR_LICENSE_PARSE = 17;
        public static final int ERROR_LICENSE_POLICY = 18;
        public static final int ERROR_LICENSE_RELEASE = 19;
        public static final int ERROR_LICENSE_REQUEST_REJECTED = 20;
        public static final int ERROR_LICENSE_RESTORE = 21;
        public static final int ERROR_LICENSE_STATE = 22;
        public static final int ERROR_LOST_STATE = 9;
        public static final int ERROR_MEDIA_FRAMEWORK = 23;
        public static final int ERROR_NO_KEY = 1;
        public static final int ERROR_PROVISIONING_CERTIFICATE = 24;
        public static final int ERROR_PROVISIONING_CONFIG = 25;
        public static final int ERROR_PROVISIONING_PARSE = 26;
        public static final int ERROR_PROVISIONING_REQUEST_REJECTED = 27;
        public static final int ERROR_PROVISIONING_RETRY = 28;
        public static final int ERROR_RESOURCE_BUSY = 3;
        public static final int ERROR_RESOURCE_CONTENTION = 29;
        public static final int ERROR_SECURE_STOP_RELEASE = 30;
        public static final int ERROR_SESSION_NOT_OPENED = 5;
        public static final int ERROR_STORAGE_READ = 31;
        public static final int ERROR_STORAGE_WRITE = 32;
        public static final int ERROR_UNKNOWN = 0;
        public static final int ERROR_UNSUPPORTED_OPERATION = 6;
        public static final int ERROR_ZERO_SUBSAMPLES = 33;

        private ErrorCodes() {
        }
    }

    /* loaded from: classes2.dex */
    public static final class MediaDrmStateException extends IllegalStateException implements MediaDrmThrowable {
        private final String mDiagnosticInfo;
        private final int mErrorCode;
        private final int mErrorContext;
        private final int mOemError;
        private final int mVendorError;

        public MediaDrmStateException(int errorCode, String detailMessage) {
            this(detailMessage, errorCode, 0, 0, 0);
        }

        public MediaDrmStateException(String detailMessage, int errorCode, int vendorError, int oemError, int errorContext) {
            super(detailMessage);
            this.mErrorCode = errorCode;
            this.mVendorError = vendorError;
            this.mOemError = oemError;
            this.mErrorContext = errorContext;
            String sign = errorCode < 0 ? "neg_" : "";
            this.mDiagnosticInfo = "android.media.MediaDrm.error_" + sign + Math.abs(errorCode);
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }

        @Override // android.media.MediaDrmThrowable
        public int getVendorError() {
            return this.mVendorError;
        }

        @Override // android.media.MediaDrmThrowable
        public int getOemError() {
            return this.mOemError;
        }

        @Override // android.media.MediaDrmThrowable
        public int getErrorContext() {
            return this.mErrorContext;
        }

        public boolean isTransient() {
            int i = this.mErrorCode;
            return i == 28 || i == 29;
        }

        public String getDiagnosticInfo() {
            return this.mDiagnosticInfo;
        }
    }

    /* loaded from: classes2.dex */
    public static final class SessionException extends RuntimeException implements MediaDrmThrowable {
        public static final int ERROR_RESOURCE_CONTENTION = 1;
        public static final int ERROR_UNKNOWN = 0;
        private final int mErrorCode;
        private final int mErrorContext;
        private final int mOemError;
        private final int mVendorError;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface SessionErrorCode {
        }

        public SessionException(int errorCode, String detailMessage) {
            this(detailMessage, errorCode, 0, 0, 0);
        }

        public SessionException(String detailMessage, int errorCode, int vendorError, int oemError, int errorContext) {
            super(detailMessage);
            this.mErrorCode = errorCode;
            this.mVendorError = vendorError;
            this.mOemError = oemError;
            this.mErrorContext = errorContext;
        }

        public int getErrorCode() {
            return this.mErrorCode;
        }

        @Override // android.media.MediaDrmThrowable
        public int getVendorError() {
            return this.mVendorError;
        }

        @Override // android.media.MediaDrmThrowable
        public int getOemError() {
            return this.mOemError;
        }

        @Override // android.media.MediaDrmThrowable
        public int getErrorContext() {
            return this.mErrorContext;
        }

        public boolean isTransient() {
            return this.mErrorCode == 1;
        }
    }

    public void setOnExpirationUpdateListener(OnExpirationUpdateListener listener, Handler handler) {
        setListenerWithHandler(201, handler, listener, new MediaDrm$$ExternalSyntheticLambda1(this));
    }

    public void setOnExpirationUpdateListener(Executor executor, OnExpirationUpdateListener listener) {
        setListenerWithExecutor(201, executor, listener, new MediaDrm$$ExternalSyntheticLambda1(this));
    }

    public void clearOnExpirationUpdateListener() {
        clearGenericListener(201);
    }

    public void setOnKeyStatusChangeListener(OnKeyStatusChangeListener listener, Handler handler) {
        setListenerWithHandler(202, handler, listener, new MediaDrm$$ExternalSyntheticLambda3(this));
    }

    public void setOnKeyStatusChangeListener(Executor executor, OnKeyStatusChangeListener listener) {
        setListenerWithExecutor(202, executor, listener, new MediaDrm$$ExternalSyntheticLambda3(this));
    }

    public void clearOnKeyStatusChangeListener() {
        clearGenericListener(202);
    }

    public void setOnSessionLostStateListener(OnSessionLostStateListener listener, Handler handler) {
        setListenerWithHandler(203, handler, listener, new MediaDrm$$ExternalSyntheticLambda4(this));
    }

    public void setOnSessionLostStateListener(Executor executor, OnSessionLostStateListener listener) {
        setListenerWithExecutor(203, executor, listener, new MediaDrm$$ExternalSyntheticLambda4(this));
    }

    public void clearOnSessionLostStateListener() {
        clearGenericListener(203);
    }

    /* loaded from: classes2.dex */
    public static final class KeyStatus {
        public static final int STATUS_EXPIRED = 1;
        public static final int STATUS_INTERNAL_ERROR = 4;
        public static final int STATUS_OUTPUT_NOT_ALLOWED = 2;
        public static final int STATUS_PENDING = 3;
        public static final int STATUS_USABLE = 0;
        public static final int STATUS_USABLE_IN_FUTURE = 5;
        private final byte[] mKeyId;
        private final int mStatusCode;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface KeyStatusCode {
        }

        KeyStatus(byte[] keyId, int statusCode) {
            this.mKeyId = keyId;
            this.mStatusCode = statusCode;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public byte[] getKeyId() {
            return this.mKeyId;
        }
    }

    public void setOnEventListener(OnEventListener listener) {
        setOnEventListener(listener, (Handler) null);
    }

    public void setOnEventListener(OnEventListener listener, Handler handler) {
        setListenerWithHandler(200, handler, listener, new MediaDrm$$ExternalSyntheticLambda0(this));
    }

    public void setOnEventListener(Executor executor, OnEventListener listener) {
        setListenerWithExecutor(200, executor, listener, new MediaDrm$$ExternalSyntheticLambda0(this));
    }

    public void clearOnEventListener() {
        clearGenericListener(200);
    }

    private <T> void setListenerWithHandler(int what, Handler handler, T listener, Function<T, Consumer<ListenerArgs>> converter) {
        if (listener == null) {
            clearGenericListener(what);
            return;
        }
        HandlerExecutor executor = new HandlerExecutor(handler == null ? createHandler() : handler);
        setGenericListener(what, executor, listener, converter);
    }

    private <T> void setListenerWithExecutor(int what, Executor executor, T listener, Function<T, Consumer<ListenerArgs>> converter) {
        if (executor == null || listener == null) {
            String errMsg = String.format("executor %s listener %s", executor, listener);
            throw new IllegalArgumentException(errMsg);
        } else {
            setGenericListener(what, executor, listener, converter);
        }
    }

    private <T> void setGenericListener(int what, Executor executor, T listener, Function<T, Consumer<ListenerArgs>> converter) {
        this.mListenerMap.put(Integer.valueOf(what), new ListenerWithExecutor(executor, converter.apply(listener)));
    }

    private void clearGenericListener(int what) {
        this.mListenerMap.remove(Integer.valueOf(what));
    }

    public Consumer<ListenerArgs> createOnEventListener(final OnEventListener listener) {
        return new Consumer() { // from class: android.media.MediaDrm$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MediaDrm.this.lambda$createOnEventListener$0(listener, (MediaDrm.ListenerArgs) obj);
            }
        };
    }

    public /* synthetic */ void lambda$createOnEventListener$0(OnEventListener listener, ListenerArgs args) {
        byte[] data;
        byte[] sessionId = args.sessionId;
        if (sessionId.length == 0) {
            sessionId = null;
        }
        byte[] data2 = args.data;
        if (data2 != null && data2.length == 0) {
            data = null;
        } else {
            data = data2;
        }
        Log.m108i(TAG, "Drm event (" + args.arg1 + "," + args.arg2 + NavigationBarInflaterView.KEY_CODE_END);
        listener.onEvent(this, sessionId, args.arg1, args.arg2, data);
    }

    public Consumer<ListenerArgs> createOnKeyStatusChangeListener(final OnKeyStatusChangeListener listener) {
        return new Consumer() { // from class: android.media.MediaDrm$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MediaDrm.this.lambda$createOnKeyStatusChangeListener$1(listener, (MediaDrm.ListenerArgs) obj);
            }
        };
    }

    public /* synthetic */ void lambda$createOnKeyStatusChangeListener$1(OnKeyStatusChangeListener listener, ListenerArgs args) {
        byte[] sessionId = args.sessionId;
        if (sessionId.length > 0) {
            List<KeyStatus> keyStatusList = args.keyStatusList;
            boolean hasNewUsableKey = args.hasNewUsableKey;
            Log.m108i(TAG, "Drm key status changed");
            listener.onKeyStatusChange(this, sessionId, keyStatusList, hasNewUsableKey);
        }
    }

    public Consumer<ListenerArgs> createOnExpirationUpdateListener(final OnExpirationUpdateListener listener) {
        return new Consumer() { // from class: android.media.MediaDrm$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MediaDrm.this.lambda$createOnExpirationUpdateListener$2(listener, (MediaDrm.ListenerArgs) obj);
            }
        };
    }

    public /* synthetic */ void lambda$createOnExpirationUpdateListener$2(OnExpirationUpdateListener listener, ListenerArgs args) {
        byte[] sessionId = args.sessionId;
        if (sessionId.length > 0) {
            long expirationTime = args.expirationTime;
            Log.m108i(TAG, "Drm key expiration update: " + expirationTime);
            listener.onExpirationUpdate(this, sessionId, expirationTime);
        }
    }

    public Consumer<ListenerArgs> createOnSessionLostStateListener(final OnSessionLostStateListener listener) {
        return new Consumer() { // from class: android.media.MediaDrm$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MediaDrm.this.lambda$createOnSessionLostStateListener$3(listener, (MediaDrm.ListenerArgs) obj);
            }
        };
    }

    public /* synthetic */ void lambda$createOnSessionLostStateListener$3(OnSessionLostStateListener listener, ListenerArgs args) {
        byte[] sessionId = args.sessionId;
        Log.m108i(TAG, "Drm session lost state event: ");
        listener.onSessionLostState(this, sessionId);
    }

    /* loaded from: classes2.dex */
    public static class ListenerArgs {
        private final int arg1;
        private final int arg2;
        private final byte[] data;
        private final long expirationTime;
        private final boolean hasNewUsableKey;
        private final List<KeyStatus> keyStatusList;
        private final byte[] sessionId;

        public ListenerArgs(int arg1, int arg2, byte[] sessionId, byte[] data, long expirationTime, List<KeyStatus> keyStatusList, boolean hasNewUsableKey) {
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.sessionId = sessionId;
            this.data = data;
            this.expirationTime = expirationTime;
            this.keyStatusList = keyStatusList;
            this.hasNewUsableKey = hasNewUsableKey;
        }
    }

    /* loaded from: classes2.dex */
    public static class ListenerWithExecutor {
        private final Consumer<ListenerArgs> mConsumer;
        private final Executor mExecutor;

        public ListenerWithExecutor(Executor executor, Consumer<ListenerArgs> consumer) {
            this.mExecutor = executor;
            this.mConsumer = consumer;
        }
    }

    private List<KeyStatus> keyStatusListFromParcel(Parcel parcel) {
        int nelems = parcel.readInt();
        List<KeyStatus> keyStatusList = new ArrayList<>(nelems);
        while (true) {
            int nelems2 = nelems - 1;
            if (nelems > 0) {
                byte[] keyId = parcel.createByteArray();
                int keyStatusCode = parcel.readInt();
                keyStatusList.add(new KeyStatus(keyId, keyStatusCode));
                nelems = nelems2;
            } else {
                return keyStatusList;
            }
        }
    }

    private static void postEventFromNative(Object mediadrm_ref, int what, final int eventType, final int extra, final byte[] sessionId, final byte[] data, final long expirationTime, final List<KeyStatus> keyStatusList, final boolean hasNewUsableKey) {
        MediaDrm md = (MediaDrm) ((WeakReference) mediadrm_ref).get();
        if (md == null) {
            return;
        }
        switch (what) {
            case 200:
            case 201:
            case 202:
            case 203:
                final ListenerWithExecutor listener = md.mListenerMap.get(Integer.valueOf(what));
                if (listener != null) {
                    Runnable command = new Runnable() { // from class: android.media.MediaDrm$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            MediaDrm.lambda$postEventFromNative$4(MediaDrm.this, eventType, extra, sessionId, data, expirationTime, keyStatusList, hasNewUsableKey, listener);
                        }
                    };
                    listener.mExecutor.execute(command);
                    return;
                }
                return;
            default:
                Log.m110e(TAG, "Unknown message type " + what);
                return;
        }
    }

    public static /* synthetic */ void lambda$postEventFromNative$4(MediaDrm md, int eventType, int extra, byte[] sessionId, byte[] data, long expirationTime, List keyStatusList, boolean hasNewUsableKey, ListenerWithExecutor listener) {
        if (md.mNativeContext == 0) {
            Log.m104w(TAG, "MediaDrm went away with unhandled events");
            return;
        }
        ListenerArgs args = new ListenerArgs(eventType, extra, sessionId, data, expirationTime, keyStatusList, hasNewUsableKey);
        listener.mConsumer.accept(args);
    }

    public byte[] openSession() throws NotProvisionedException, ResourceBusyException {
        return openSession(getMaxSecurityLevel());
    }

    public byte[] openSession(int level) throws NotProvisionedException, ResourceBusyException {
        byte[] sessionId = openSessionNative(level);
        this.mPlaybackComponentMap.put(ByteBuffer.wrap(sessionId), new PlaybackComponent(sessionId));
        return sessionId;
    }

    public void closeSession(byte[] sessionId) {
        closeSessionNative(sessionId);
        this.mPlaybackComponentMap.remove(ByteBuffer.wrap(sessionId));
    }

    /* loaded from: classes2.dex */
    public static final class KeyRequest {
        public static final int REQUEST_TYPE_INITIAL = 0;
        public static final int REQUEST_TYPE_NONE = 3;
        public static final int REQUEST_TYPE_RELEASE = 2;
        public static final int REQUEST_TYPE_RENEWAL = 1;
        public static final int REQUEST_TYPE_UPDATE = 4;
        private byte[] mData;
        private String mDefaultUrl;
        private int mRequestType;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface RequestType {
        }

        KeyRequest() {
        }

        public byte[] getData() {
            byte[] bArr = this.mData;
            if (bArr == null) {
                throw new RuntimeException("KeyRequest is not initialized");
            }
            return bArr;
        }

        public String getDefaultUrl() {
            String str = this.mDefaultUrl;
            if (str == null) {
                throw new RuntimeException("KeyRequest is not initialized");
            }
            return str;
        }

        public int getRequestType() {
            return this.mRequestType;
        }
    }

    public KeyRequest getKeyRequest(byte[] scope, byte[] init, String mimeType, int keyType, HashMap<String, String> optionalParameters) throws NotProvisionedException {
        HashMap<String, String> internalParams;
        byte[] hashBytes;
        if (optionalParameters == null) {
            internalParams = new HashMap<>();
        } else {
            internalParams = new HashMap<>(optionalParameters);
        }
        byte[] rawBytes = getNewestAvailablePackageCertificateRawBytes();
        if (rawBytes == null) {
            hashBytes = null;
        } else {
            byte[] hashBytes2 = getDigestBytes(rawBytes, "SHA-256");
            hashBytes = hashBytes2;
        }
        if (hashBytes != null) {
            Base64.Encoder encoderB64 = Base64.getEncoder();
            String hashBytesB64 = encoderB64.encodeToString(hashBytes);
            internalParams.put("package_certificate_hash_bytes", hashBytesB64);
        }
        return getKeyRequestNative(scope, init, mimeType, keyType, internalParams);
    }

    private byte[] getNewestAvailablePackageCertificateRawBytes() {
        Application application = ActivityThread.currentApplication();
        if (application == null) {
            Log.m104w(TAG, "pkg cert: Application is null");
            return null;
        }
        PackageManager pm = application.getPackageManager();
        if (pm == null) {
            Log.m104w(TAG, "pkg cert: PackageManager is null");
            return null;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(this.mAppPackageName, 134217728);
        } catch (PackageManager.NameNotFoundException e) {
            Log.m103w(TAG, this.mAppPackageName, e);
        }
        if (packageInfo == null || packageInfo.signingInfo == null) {
            Log.m104w(TAG, "pkg cert: PackageInfo or SigningInfo is null");
            return null;
        }
        Signature[] signers = packageInfo.signingInfo.getApkContentsSigners();
        if (signers != null && signers.length == 1) {
            return signers[0].toByteArray();
        }
        Log.m104w(TAG, "pkg cert: " + signers.length + " signers");
        return null;
    }

    private static byte[] getDigestBytes(byte[] rawBytes, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            return messageDigest.digest(rawBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.m103w(TAG, algorithm, e);
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static final class ProvisionRequest {
        private byte[] mData;
        private String mDefaultUrl;

        ProvisionRequest() {
        }

        public byte[] getData() {
            byte[] bArr = this.mData;
            if (bArr == null) {
                throw new RuntimeException("ProvisionRequest is not initialized");
            }
            return bArr;
        }

        public String getDefaultUrl() {
            String str = this.mDefaultUrl;
            if (str == null) {
                throw new RuntimeException("ProvisionRequest is not initialized");
            }
            return str;
        }
    }

    public ProvisionRequest getProvisionRequest() {
        return getProvisionRequestNative(0, "");
    }

    public void provideProvisionResponse(byte[] response) throws DeniedByServerException {
        provideProvisionResponseNative(response);
    }

    public void releaseAllSecureStops() {
        removeAllSecureStops();
    }

    public static final int getMaxSecurityLevel() {
        return 6;
    }

    public PersistableBundle getMetrics() {
        PersistableBundle bundle = getMetricsNative();
        return bundle;
    }

    /* loaded from: classes2.dex */
    public final class CryptoSession {
        private byte[] mSessionId;

        CryptoSession(byte[] sessionId, String cipherAlgorithm, String macAlgorithm) {
            MediaDrm.this = this$0;
            this.mSessionId = sessionId;
            MediaDrm.setCipherAlgorithmNative(this$0, sessionId, cipherAlgorithm);
            MediaDrm.setMacAlgorithmNative(this$0, sessionId, macAlgorithm);
        }

        public byte[] encrypt(byte[] keyid, byte[] input, byte[] iv) {
            return MediaDrm.encryptNative(MediaDrm.this, this.mSessionId, keyid, input, iv);
        }

        public byte[] decrypt(byte[] keyid, byte[] input, byte[] iv) {
            return MediaDrm.decryptNative(MediaDrm.this, this.mSessionId, keyid, input, iv);
        }

        public byte[] sign(byte[] keyid, byte[] message) {
            return MediaDrm.signNative(MediaDrm.this, this.mSessionId, keyid, message);
        }

        public boolean verify(byte[] keyid, byte[] message, byte[] signature) {
            return MediaDrm.verifyNative(MediaDrm.this, this.mSessionId, keyid, message, signature);
        }
    }

    public CryptoSession getCryptoSession(byte[] sessionId, String cipherAlgorithm, String macAlgorithm) {
        return new CryptoSession(sessionId, cipherAlgorithm, macAlgorithm);
    }

    /* loaded from: classes2.dex */
    public static final class CertificateRequest {
        private byte[] mData;
        private String mDefaultUrl;

        CertificateRequest(byte[] data, String defaultUrl) {
            this.mData = data;
            this.mDefaultUrl = defaultUrl;
        }

        public byte[] getData() {
            return this.mData;
        }

        public String getDefaultUrl() {
            return this.mDefaultUrl;
        }
    }

    public CertificateRequest getCertificateRequest(int certType, String certAuthority) {
        ProvisionRequest provisionRequest = getProvisionRequestNative(certType, certAuthority);
        return new CertificateRequest(provisionRequest.getData(), provisionRequest.getDefaultUrl());
    }

    /* loaded from: classes2.dex */
    public static final class Certificate {
        private byte[] mCertificateData;
        private byte[] mWrappedKey;

        Certificate() {
        }

        public byte[] getWrappedPrivateKey() {
            byte[] bArr = this.mWrappedKey;
            if (bArr == null) {
                throw new RuntimeException("Certificate is not initialized");
            }
            return bArr;
        }

        public byte[] getContent() {
            byte[] bArr = this.mCertificateData;
            if (bArr == null) {
                throw new RuntimeException("Certificate is not initialized");
            }
            return bArr;
        }
    }

    public Certificate provideCertificateResponse(byte[] response) throws DeniedByServerException {
        return provideProvisionResponseNative(response);
    }

    public byte[] signRSA(byte[] sessionId, String algorithm, byte[] wrappedKey, byte[] message) {
        return signRSANative(this, sessionId, algorithm, wrappedKey, message);
    }

    public boolean requiresSecureDecoder(String mime) {
        return requiresSecureDecoder(mime, getMaxSecurityLevel());
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            release();
        } finally {
            super.finalize();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        release();
    }

    @Deprecated
    public void release() {
        this.mCloseGuard.close();
        if (this.mClosed.compareAndSet(false, true)) {
            native_release();
            this.mPlaybackComponentMap.clear();
        }
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    /* loaded from: classes2.dex */
    public static final class MetricsConstants {
        public static final String CLOSE_SESSION_ERROR_COUNT = "drm.mediadrm.close_session.error.count";
        public static final String CLOSE_SESSION_ERROR_LIST = "drm.mediadrm.close_session.error.list";
        public static final String CLOSE_SESSION_OK_COUNT = "drm.mediadrm.close_session.ok.count";
        public static final String EVENT_KEY_EXPIRED_COUNT = "drm.mediadrm.event.KEY_EXPIRED.count";
        public static final String EVENT_KEY_NEEDED_COUNT = "drm.mediadrm.event.KEY_NEEDED.count";
        public static final String EVENT_PROVISION_REQUIRED_COUNT = "drm.mediadrm.event.PROVISION_REQUIRED.count";
        public static final String EVENT_SESSION_RECLAIMED_COUNT = "drm.mediadrm.event.SESSION_RECLAIMED.count";
        public static final String EVENT_VENDOR_DEFINED_COUNT = "drm.mediadrm.event.VENDOR_DEFINED.count";
        public static final String GET_DEVICE_UNIQUE_ID_ERROR_COUNT = "drm.mediadrm.get_device_unique_id.error.count";
        public static final String GET_DEVICE_UNIQUE_ID_ERROR_LIST = "drm.mediadrm.get_device_unique_id.error.list";
        public static final String GET_DEVICE_UNIQUE_ID_OK_COUNT = "drm.mediadrm.get_device_unique_id.ok.count";
        public static final String GET_KEY_REQUEST_ERROR_COUNT = "drm.mediadrm.get_key_request.error.count";
        public static final String GET_KEY_REQUEST_ERROR_LIST = "drm.mediadrm.get_key_request.error.list";
        public static final String GET_KEY_REQUEST_OK_COUNT = "drm.mediadrm.get_key_request.ok.count";
        public static final String GET_KEY_REQUEST_OK_TIME_MICROS = "drm.mediadrm.get_key_request.ok.average_time_micros";
        public static final String GET_PROVISION_REQUEST_ERROR_COUNT = "drm.mediadrm.get_provision_request.error.count";
        public static final String GET_PROVISION_REQUEST_ERROR_LIST = "drm.mediadrm.get_provision_request.error.list";
        public static final String GET_PROVISION_REQUEST_OK_COUNT = "drm.mediadrm.get_provision_request.ok.count";
        public static final String KEY_STATUS_EXPIRED_COUNT = "drm.mediadrm.key_status.EXPIRED.count";
        public static final String KEY_STATUS_INTERNAL_ERROR_COUNT = "drm.mediadrm.key_status.INTERNAL_ERROR.count";
        public static final String KEY_STATUS_OUTPUT_NOT_ALLOWED_COUNT = "drm.mediadrm.key_status_change.OUTPUT_NOT_ALLOWED.count";
        public static final String KEY_STATUS_PENDING_COUNT = "drm.mediadrm.key_status_change.PENDING.count";
        public static final String KEY_STATUS_USABLE_COUNT = "drm.mediadrm.key_status_change.USABLE.count";
        public static final String OPEN_SESSION_ERROR_COUNT = "drm.mediadrm.open_session.error.count";
        public static final String OPEN_SESSION_ERROR_LIST = "drm.mediadrm.open_session.error.list";
        public static final String OPEN_SESSION_OK_COUNT = "drm.mediadrm.open_session.ok.count";
        public static final String PROVIDE_KEY_RESPONSE_ERROR_COUNT = "drm.mediadrm.provide_key_response.error.count";
        public static final String PROVIDE_KEY_RESPONSE_ERROR_LIST = "drm.mediadrm.provide_key_response.error.list";
        public static final String PROVIDE_KEY_RESPONSE_OK_COUNT = "drm.mediadrm.provide_key_response.ok.count";
        public static final String PROVIDE_KEY_RESPONSE_OK_TIME_MICROS = "drm.mediadrm.provide_key_response.ok.average_time_micros";
        public static final String PROVIDE_PROVISION_RESPONSE_ERROR_COUNT = "drm.mediadrm.provide_provision_response.error.count";
        public static final String PROVIDE_PROVISION_RESPONSE_ERROR_LIST = "drm.mediadrm.provide_provision_response.error.list";
        public static final String PROVIDE_PROVISION_RESPONSE_OK_COUNT = "drm.mediadrm.provide_provision_response.ok.count";
        public static final String SESSION_END_TIMES_MS = "drm.mediadrm.session_end_times_ms";
        public static final String SESSION_START_TIMES_MS = "drm.mediadrm.session_start_times_ms";

        private MetricsConstants() {
        }
    }

    public PlaybackComponent getPlaybackComponent(byte[] sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId is null");
        }
        return this.mPlaybackComponentMap.get(ByteBuffer.wrap(sessionId));
    }

    /* loaded from: classes2.dex */
    public final class PlaybackComponent {
        private LogSessionId mLogSessionId = LogSessionId.LOG_SESSION_ID_NONE;
        private final byte[] mSessionId;

        public PlaybackComponent(byte[] sessionId) {
            MediaDrm.this = this$0;
            this.mSessionId = sessionId;
        }

        public void setLogSessionId(LogSessionId logSessionId) {
            Objects.requireNonNull(logSessionId);
            if (logSessionId.getStringId() == null) {
                throw new IllegalArgumentException("playbackId is null");
            }
            MediaDrm.this.setPlaybackId(this.mSessionId, logSessionId.getStringId());
            this.mLogSessionId = logSessionId;
        }

        public LogSessionId getLogSessionId() {
            return this.mLogSessionId;
        }
    }

    /* loaded from: classes2.dex */
    public static final class LogMessage {
        private final String message;
        private final int priority;
        private final long timestampMillis;

        public final long getTimestampMillis() {
            return this.timestampMillis;
        }

        public final int getPriority() {
            return this.priority;
        }

        public final String getMessage() {
            return this.message;
        }

        private LogMessage(long timestampMillis, int priority, String message) {
            this.timestampMillis = timestampMillis;
            if (priority < 2 || priority > 7) {
                throw new IllegalArgumentException("invalid log priority " + priority);
            }
            this.priority = priority;
            this.message = message;
        }

        private char logPriorityChar() {
            switch (this.priority) {
                case 2:
                    return 'V';
                case 3:
                    return 'D';
                case 4:
                    return 'I';
                case 5:
                    return 'W';
                case 6:
                    return DateFormat.DAY;
                case 7:
                    return 'F';
                default:
                    return 'U';
            }
        }

        public String toString() {
            return String.format("LogMessage{%s %c %s}", Instant.ofEpochMilli(this.timestampMillis), Character.valueOf(logPriorityChar()), this.message);
        }
    }
}
