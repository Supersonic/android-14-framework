package android.media;

import android.content.Context;
import android.hardware.cas.AidlCasPluginDescriptor;
import android.hardware.cas.ICas;
import android.hardware.cas.ICasListener;
import android.hardware.cas.IMediaCasService;
import android.hardware.cas.V1_0.HidlCasPluginDescriptor;
import android.hardware.cas.V1_0.ICas;
import android.hardware.cas.V1_2.ICas;
import android.hardware.cas.V1_2.ICasListener;
import android.media.MediaCasException;
import android.media.p007tv.tunerresourcemanager.CasSessionRequest;
import android.media.p007tv.tunerresourcemanager.ResourceClientProfile;
import android.media.p007tv.tunerresourcemanager.TunerResourceManager;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IHwBinder;
import android.p008os.IHwInterface;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.util.Log;
import android.util.Singleton;
import com.android.internal.util.FrameworkStatsLog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MediaCas implements AutoCloseable {
    public static final int PLUGIN_STATUS_PHYSICAL_MODULE_CHANGED = 0;
    public static final int PLUGIN_STATUS_SESSION_NUMBER_CHANGED = 1;
    public static final int SCRAMBLING_MODE_AES128 = 9;
    public static final int SCRAMBLING_MODE_AES_CBC = 14;
    public static final int SCRAMBLING_MODE_AES_ECB = 10;
    public static final int SCRAMBLING_MODE_AES_SCTE52 = 11;
    public static final int SCRAMBLING_MODE_DVB_CISSA_V1 = 6;
    public static final int SCRAMBLING_MODE_DVB_CSA1 = 1;
    public static final int SCRAMBLING_MODE_DVB_CSA2 = 2;
    public static final int SCRAMBLING_MODE_DVB_CSA3_ENHANCE = 5;
    public static final int SCRAMBLING_MODE_DVB_CSA3_MINIMAL = 4;
    public static final int SCRAMBLING_MODE_DVB_CSA3_STANDARD = 3;
    public static final int SCRAMBLING_MODE_DVB_IDSA = 7;
    public static final int SCRAMBLING_MODE_MULTI2 = 8;
    public static final int SCRAMBLING_MODE_RESERVED = 0;
    public static final int SCRAMBLING_MODE_TDES_ECB = 12;
    public static final int SCRAMBLING_MODE_TDES_SCTE52 = 13;
    public static final int SESSION_USAGE_LIVE = 0;
    public static final int SESSION_USAGE_PLAYBACK = 1;
    public static final int SESSION_USAGE_RECORD = 2;
    public static final int SESSION_USAGE_TIMESHIFT = 3;
    private static final String TAG = "MediaCas";
    private static final Singleton<IMediaCasService> sService = new Singleton<IMediaCasService>() { // from class: android.media.MediaCas.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IMediaCasService create() {
            try {
                Log.m112d(MediaCas.TAG, "Trying to get AIDL service");
                IMediaCasService serviceAidl = IMediaCasService.Stub.asInterface(ServiceManager.getService(IMediaCasService.DESCRIPTOR + "/default"));
                if (serviceAidl != null) {
                    return serviceAidl;
                }
                return null;
            } catch (Exception e) {
                Log.m112d(MediaCas.TAG, "Failed to get cas AIDL service");
                return null;
            }
        }
    };
    private static final Singleton<android.hardware.cas.V1_0.IMediaCasService> sServiceHidl = new Singleton<android.hardware.cas.V1_0.IMediaCasService>() { // from class: android.media.MediaCas.2
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public android.hardware.cas.V1_0.IMediaCasService create() {
            try {
                Log.m112d(MediaCas.TAG, "Trying to get cas@1.2 service");
                android.hardware.cas.V1_2.IMediaCasService serviceV12 = android.hardware.cas.V1_2.IMediaCasService.getService(true);
                if (serviceV12 != null) {
                    return serviceV12;
                }
            } catch (Exception e) {
                Log.m112d(MediaCas.TAG, "Failed to get cas@1.2 service");
            }
            try {
                Log.m112d(MediaCas.TAG, "Trying to get cas@1.1 service");
                android.hardware.cas.V1_1.IMediaCasService serviceV11 = android.hardware.cas.V1_1.IMediaCasService.getService(true);
                if (serviceV11 != null) {
                    return serviceV11;
                }
            } catch (Exception e2) {
                Log.m112d(MediaCas.TAG, "Failed to get cas@1.1 service");
            }
            try {
                Log.m112d(MediaCas.TAG, "Trying to get cas@1.0 service");
                return android.hardware.cas.V1_0.IMediaCasService.getService(true);
            } catch (Exception e3) {
                Log.m112d(MediaCas.TAG, "Failed to get cas@1.0 service");
                return null;
            }
        }
    };
    private int mCasSystemId;
    private int mClientId;
    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    private EventListener mListener;
    private int mPriorityHint;
    private String mTvInputServiceSessionId;
    private int mUserId;
    private ICas mICas = null;
    private android.hardware.cas.V1_0.ICas mICasHidl = null;
    private android.hardware.cas.V1_1.ICas mICasHidl11 = null;
    private android.hardware.cas.V1_2.ICas mICasHidl12 = null;
    private TunerResourceManager mTunerResourceManager = null;
    private final Map<Session, Integer> mSessionMap = new HashMap();
    private final ICasListener.Stub mBinder = new ICasListener.Stub() { // from class: android.media.MediaCas.3
        @Override // android.hardware.cas.ICasListener
        public void onEvent(int event, int arg, byte[] data) throws RemoteException {
            if (MediaCas.this.mEventHandler != null) {
                MediaCas.this.mEventHandler.sendMessage(MediaCas.this.mEventHandler.obtainMessage(0, event, arg, data));
            }
        }

        @Override // android.hardware.cas.ICasListener
        public void onSessionEvent(byte[] sessionId, int event, int arg, byte[] data) throws RemoteException {
            if (MediaCas.this.mEventHandler != null) {
                Message msg = MediaCas.this.mEventHandler.obtainMessage();
                msg.what = 1;
                msg.arg1 = event;
                msg.arg2 = arg;
                Bundle bundle = new Bundle();
                bundle.putByteArray("sessionId", sessionId);
                bundle.putByteArray("data", data);
                msg.setData(bundle);
                MediaCas.this.mEventHandler.sendMessage(msg);
            }
        }

        @Override // android.hardware.cas.ICasListener
        public void onStatusUpdate(byte status, int arg) throws RemoteException {
            if (MediaCas.this.mEventHandler != null) {
                MediaCas.this.mEventHandler.sendMessage(MediaCas.this.mEventHandler.obtainMessage(2, status, arg));
            }
        }

        @Override // android.hardware.cas.ICasListener
        public synchronized String getInterfaceHash() throws RemoteException {
            return "notfrozen";
        }

        @Override // android.hardware.cas.ICasListener
        public int getInterfaceVersion() throws RemoteException {
            return 1;
        }
    };
    private final ICasListener.Stub mBinderHidl = new ICasListener.Stub() { // from class: android.media.MediaCas.4
        @Override // android.hardware.cas.V1_0.ICasListener
        public void onEvent(int event, int arg, ArrayList<Byte> data) throws RemoteException {
            if (MediaCas.this.mEventHandler != null) {
                MediaCas.this.mEventHandler.sendMessage(MediaCas.this.mEventHandler.obtainMessage(0, event, arg, data));
            }
        }

        @Override // android.hardware.cas.V1_1.ICasListener
        public void onSessionEvent(ArrayList<Byte> sessionId, int event, int arg, ArrayList<Byte> data) throws RemoteException {
            if (MediaCas.this.mEventHandler != null) {
                Message msg = MediaCas.this.mEventHandler.obtainMessage();
                msg.what = 1;
                msg.arg1 = event;
                msg.arg2 = arg;
                Bundle bundle = new Bundle();
                bundle.putByteArray("sessionId", MediaCas.this.toBytes(sessionId));
                bundle.putByteArray("data", MediaCas.this.toBytes(data));
                msg.setData(bundle);
                MediaCas.this.mEventHandler.sendMessage(msg);
            }
        }

        @Override // android.hardware.cas.V1_2.ICasListener
        public void onStatusUpdate(byte status, int arg) throws RemoteException {
            if (MediaCas.this.mEventHandler != null) {
                MediaCas.this.mEventHandler.sendMessage(MediaCas.this.mEventHandler.obtainMessage(2, status, arg));
            }
        }
    };
    private final TunerResourceManager.ResourcesReclaimListener mResourceListener = new TunerResourceManager.ResourcesReclaimListener() { // from class: android.media.MediaCas.5
        @Override // android.media.p007tv.tunerresourcemanager.TunerResourceManager.ResourcesReclaimListener
        public void onReclaimResources() {
            synchronized (MediaCas.this.mSessionMap) {
                List<Session> sessionList = new ArrayList<>(MediaCas.this.mSessionMap.keySet());
                for (Session casSession : sessionList) {
                    casSession.close();
                }
            }
            MediaCas.this.mEventHandler.sendMessage(MediaCas.this.mEventHandler.obtainMessage(3));
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PluginStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ScramblingMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SessionUsage {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IMediaCasService getService() {
        return sService.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static android.hardware.cas.V1_0.IMediaCasService getServiceHidl() {
        return sServiceHidl.get();
    }

    private void validateInternalStates() {
        if (this.mICas == null && this.mICasHidl == null) {
            throw new IllegalStateException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanupAndRethrowIllegalState() {
        this.mICas = null;
        this.mICasHidl = null;
        this.mICasHidl11 = null;
        this.mICasHidl12 = null;
        throw new IllegalStateException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class EventHandler extends Handler {
        private static final String DATA_KEY = "data";
        private static final int MSG_CAS_EVENT = 0;
        private static final int MSG_CAS_RESOURCE_LOST = 3;
        private static final int MSG_CAS_SESSION_EVENT = 1;
        private static final int MSG_CAS_STATUS_EVENT = 2;
        private static final String SESSION_KEY = "sessionId";

        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                byte[] data = msg.obj == null ? new byte[0] : (byte[]) msg.obj;
                MediaCas.this.mListener.onEvent(MediaCas.this, msg.arg1, msg.arg2, data);
            } else if (msg.what == 1) {
                Bundle bundle = msg.getData();
                byte[] sessionId = bundle.getByteArray("sessionId");
                byte[] data2 = bundle.getByteArray("data");
                EventListener eventListener = MediaCas.this.mListener;
                MediaCas mediaCas = MediaCas.this;
                eventListener.onSessionEvent(mediaCas, mediaCas.createFromSessionId(sessionId), msg.arg1, msg.arg2, data2);
            } else if (msg.what == 2) {
                if (msg.arg1 == 1 && MediaCas.this.mTunerResourceManager != null) {
                    MediaCas.this.mTunerResourceManager.updateCasInfo(MediaCas.this.mCasSystemId, msg.arg2);
                }
                MediaCas.this.mListener.onPluginStatusUpdate(MediaCas.this, msg.arg1, msg.arg2);
            } else if (msg.what == 3) {
                MediaCas.this.mListener.onResourceLost(MediaCas.this);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class PluginDescriptor {
        private final int mCASystemId;
        private final String mName;

        private PluginDescriptor() {
            this.mCASystemId = 65535;
            this.mName = null;
        }

        PluginDescriptor(AidlCasPluginDescriptor descriptor) {
            this.mCASystemId = descriptor.caSystemId;
            this.mName = descriptor.name;
        }

        PluginDescriptor(HidlCasPluginDescriptor descriptor) {
            this.mCASystemId = descriptor.caSystemId;
            this.mName = descriptor.name;
        }

        public int getSystemId() {
            return this.mCASystemId;
        }

        public String getName() {
            return this.mName;
        }

        public String toString() {
            return "PluginDescriptor {" + this.mCASystemId + ", " + this.mName + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<Byte> toByteArray(byte[] data, int offset, int length) {
        ArrayList<Byte> byteArray = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            byteArray.add(Byte.valueOf(data[offset + i]));
        }
        return byteArray;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<Byte> toByteArray(byte[] data) {
        if (data == null) {
            return new ArrayList<>();
        }
        return toByteArray(data, 0, data.length);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] toBytes(ArrayList<Byte> byteArray) {
        byte[] data = null;
        if (byteArray != null) {
            data = new byte[byteArray.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = byteArray.get(i).byteValue();
            }
        }
        return data;
    }

    /* loaded from: classes2.dex */
    public final class Session implements AutoCloseable {
        boolean mIsClosed = false;
        final byte[] mSessionId;

        Session(byte[] sessionId) {
            this.mSessionId = sessionId;
        }

        private void validateSessionInternalStates() {
            if (MediaCas.this.mICas == null && MediaCas.this.mICasHidl == null) {
                throw new IllegalStateException();
            }
            if (this.mIsClosed) {
                MediaCasStateException.throwExceptionIfNeeded(3);
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof Session) {
                return Arrays.equals(this.mSessionId, ((Session) obj).mSessionId);
            }
            return false;
        }

        public void setPrivateData(byte[] data) throws MediaCasException {
            validateSessionInternalStates();
            try {
                if (MediaCas.this.mICas != null) {
                    try {
                        MediaCas.this.mICas.setSessionPrivateData(this.mSessionId, data);
                    } catch (ServiceSpecificException se) {
                        MediaCasException.throwExceptionIfNeeded(se.errorCode);
                    }
                    return;
                }
                MediaCasException.throwExceptionIfNeeded(MediaCas.this.mICasHidl.setSessionPrivateData(MediaCas.this.toByteArray(this.mSessionId), MediaCas.this.toByteArray(data, 0, data.length)));
            } catch (RemoteException e) {
                MediaCas.this.cleanupAndRethrowIllegalState();
            }
        }

        public void processEcm(byte[] data, int offset, int length) throws MediaCasException {
            validateSessionInternalStates();
            try {
                if (MediaCas.this.mICas != null) {
                    try {
                        MediaCas.this.mICas.processEcm(this.mSessionId, Arrays.copyOfRange(data, offset, length + offset));
                    } catch (ServiceSpecificException se) {
                        MediaCasException.throwExceptionIfNeeded(se.errorCode);
                    }
                    return;
                }
                MediaCasException.throwExceptionIfNeeded(MediaCas.this.mICasHidl.processEcm(MediaCas.this.toByteArray(this.mSessionId), MediaCas.this.toByteArray(data, offset, length)));
            } catch (RemoteException e) {
                MediaCas.this.cleanupAndRethrowIllegalState();
            }
        }

        public void processEcm(byte[] data) throws MediaCasException {
            processEcm(data, 0, data.length);
        }

        public void sendSessionEvent(int event, int arg, byte[] data) throws MediaCasException {
            validateSessionInternalStates();
            if (MediaCas.this.mICas != null) {
                if (data == null) {
                    try {
                        data = new byte[0];
                    } catch (RemoteException e) {
                        MediaCas.this.cleanupAndRethrowIllegalState();
                        return;
                    }
                }
                MediaCas.this.mICas.sendSessionEvent(this.mSessionId, event, arg, data);
            } else if (MediaCas.this.mICasHidl11 == null) {
                Log.m112d(MediaCas.TAG, "Send Session Event isn't supported by cas@1.0 interface");
                throw new MediaCasException.UnsupportedCasException("Send Session Event is not supported");
            } else {
                try {
                    MediaCasException.throwExceptionIfNeeded(MediaCas.this.mICasHidl11.sendSessionEvent(MediaCas.this.toByteArray(this.mSessionId), event, arg, MediaCas.this.toByteArray(data)));
                } catch (RemoteException e2) {
                    MediaCas.this.cleanupAndRethrowIllegalState();
                }
            }
        }

        public byte[] getSessionId() {
            validateSessionInternalStates();
            return this.mSessionId;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            validateSessionInternalStates();
            try {
                if (MediaCas.this.mICas != null) {
                    MediaCas.this.mICas.closeSession(this.mSessionId);
                } else {
                    MediaCasStateException.throwExceptionIfNeeded(MediaCas.this.mICasHidl.closeSession(MediaCas.this.toByteArray(this.mSessionId)));
                }
                this.mIsClosed = true;
                MediaCas.this.removeSessionFromResourceMap(this);
            } catch (RemoteException e) {
                MediaCas.this.cleanupAndRethrowIllegalState();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Session createFromSessionId(byte[] sessionId) {
        if (sessionId == null || sessionId.length == 0) {
            return null;
        }
        return new Session(sessionId);
    }

    public static boolean isSystemIdSupported(int CA_system_id) {
        IMediaCasService service = sService.get();
        if (service != null) {
            try {
                return service.isSystemIdSupported(CA_system_id);
            } catch (RemoteException e) {
                return false;
            }
        }
        android.hardware.cas.V1_0.IMediaCasService serviceHidl = sServiceHidl.get();
        if (serviceHidl != null) {
            try {
                return serviceHidl.isSystemIdSupported(CA_system_id);
            } catch (RemoteException e2) {
            }
        }
        return false;
    }

    public static PluginDescriptor[] enumeratePlugins() {
        IMediaCasService service = sService.get();
        if (service != null) {
            try {
                AidlCasPluginDescriptor[] descriptors = service.enumeratePlugins();
                if (descriptors.length == 0) {
                    return null;
                }
                PluginDescriptor[] results = new PluginDescriptor[descriptors.length];
                for (int i = 0; i < results.length; i++) {
                    results[i] = new PluginDescriptor(descriptors[i]);
                }
                return results;
            } catch (RemoteException e) {
            }
        }
        android.hardware.cas.V1_0.IMediaCasService serviceHidl = sServiceHidl.get();
        if (serviceHidl != null) {
            try {
                ArrayList<HidlCasPluginDescriptor> descriptors2 = serviceHidl.enumeratePlugins();
                if (descriptors2.size() == 0) {
                    return null;
                }
                PluginDescriptor[] results2 = new PluginDescriptor[descriptors2.size()];
                for (int i2 = 0; i2 < results2.length; i2++) {
                    results2[i2] = new PluginDescriptor(descriptors2.get(i2));
                }
                return results2;
            } catch (RemoteException e2) {
            }
        }
        return null;
    }

    private void createPlugin(int casSystemId) throws MediaCasException.UnsupportedCasException {
        try {
            try {
                this.mCasSystemId = casSystemId;
                this.mUserId = Process.myUid();
                IMediaCasService service = getService();
                if (service != null) {
                    Log.m112d(TAG, "Use CAS AIDL interface to create plugin");
                    this.mICas = service.createPlugin(casSystemId, this.mBinder);
                } else {
                    android.hardware.cas.V1_0.IMediaCasService serviceV10 = getServiceHidl();
                    android.hardware.cas.V1_2.IMediaCasService serviceV12 = android.hardware.cas.V1_2.IMediaCasService.castFrom((IHwInterface) serviceV10);
                    if (serviceV12 == null) {
                        android.hardware.cas.V1_1.IMediaCasService serviceV11 = android.hardware.cas.V1_1.IMediaCasService.castFrom((IHwInterface) serviceV10);
                        if (serviceV11 == null) {
                            Log.m112d(TAG, "Used cas@1_0 interface to create plugin");
                            this.mICasHidl = serviceV10.createPlugin(casSystemId, this.mBinderHidl);
                        } else {
                            Log.m112d(TAG, "Used cas@1.1 interface to create plugin");
                            android.hardware.cas.V1_1.ICas createPluginExt = serviceV11.createPluginExt(casSystemId, this.mBinderHidl);
                            this.mICasHidl11 = createPluginExt;
                            this.mICasHidl = createPluginExt;
                        }
                    } else {
                        Log.m112d(TAG, "Used cas@1.2 interface to create plugin");
                        android.hardware.cas.V1_2.ICas castFrom = android.hardware.cas.V1_2.ICas.castFrom((IHwInterface) serviceV12.createPluginExt(casSystemId, this.mBinderHidl));
                        this.mICasHidl12 = castFrom;
                        this.mICasHidl11 = castFrom;
                        this.mICasHidl = castFrom;
                    }
                }
                if (this.mICas == null && this.mICasHidl == null) {
                    throw new MediaCasException.UnsupportedCasException("Unsupported casSystemId " + casSystemId);
                }
            } catch (Exception e) {
                Log.m110e(TAG, "Failed to create plugin: " + e);
                this.mICas = null;
                this.mICasHidl = null;
                throw new MediaCasException.UnsupportedCasException("Unsupported casSystemId " + casSystemId);
            }
        } catch (Throwable th) {
            if (this.mICas == null && this.mICasHidl == null) {
                throw new MediaCasException.UnsupportedCasException("Unsupported casSystemId " + casSystemId);
            }
            throw th;
        }
    }

    private void registerClient(Context context, String tvInputServiceSessionId, int priorityHint) {
        TunerResourceManager tunerResourceManager = (TunerResourceManager) context.getSystemService(Context.TV_TUNER_RESOURCE_MGR_SERVICE);
        this.mTunerResourceManager = tunerResourceManager;
        if (tunerResourceManager != null) {
            int[] clientId = new int[1];
            ResourceClientProfile profile = new ResourceClientProfile();
            profile.tvInputSessionId = tvInputServiceSessionId;
            profile.useCase = priorityHint;
            this.mTunerResourceManager.registerClientProfile(profile, context.getMainExecutor(), this.mResourceListener, clientId);
            this.mClientId = clientId[0];
        }
    }

    public MediaCas(int casSystemId) throws MediaCasException.UnsupportedCasException {
        createPlugin(casSystemId);
    }

    public MediaCas(Context context, int casSystemId, String tvInputServiceSessionId, int priorityHint) throws MediaCasException.UnsupportedCasException {
        Objects.requireNonNull(context, "context must not be null");
        createPlugin(casSystemId);
        registerClient(context, tvInputServiceSessionId, priorityHint);
    }

    public MediaCas(Context context, int casSystemId, String tvInputServiceSessionId, int priorityHint, Handler handler, EventListener listener) throws MediaCasException.UnsupportedCasException {
        Objects.requireNonNull(context, "context must not be null");
        setEventListener(listener, handler);
        createPlugin(casSystemId);
        registerClient(context, tvInputServiceSessionId, priorityHint);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IHwBinder getBinder() {
        if (this.mICas != null) {
            return null;
        }
        validateInternalStates();
        return this.mICasHidl.asBinder();
    }

    public boolean isAidlHal() {
        return this.mICas != null;
    }

    /* loaded from: classes2.dex */
    public interface EventListener {
        void onEvent(MediaCas mediaCas, int i, int i2, byte[] bArr);

        default void onSessionEvent(MediaCas mediaCas, Session session, int event, int arg, byte[] data) {
            Log.m112d(MediaCas.TAG, "Received MediaCas Session event");
        }

        default void onPluginStatusUpdate(MediaCas mediaCas, int status, int arg) {
            Log.m112d(MediaCas.TAG, "Received MediaCas Plugin Status event");
        }

        default void onResourceLost(MediaCas mediaCas) {
            Log.m112d(MediaCas.TAG, "Received MediaCas Resource Reclaim event");
        }
    }

    public void setEventListener(EventListener listener, Handler handler) {
        this.mListener = listener;
        if (listener == null) {
            this.mEventHandler = null;
            return;
        }
        Looper looper = handler != null ? handler.getLooper() : null;
        if (looper == null) {
            Looper myLooper = Looper.myLooper();
            looper = myLooper;
            if (myLooper == null) {
                Looper mainLooper = Looper.getMainLooper();
                looper = mainLooper;
                if (mainLooper == null) {
                    HandlerThread handlerThread = this.mHandlerThread;
                    if (handlerThread == null || !handlerThread.isAlive()) {
                        HandlerThread handlerThread2 = new HandlerThread("MediaCasEventThread", -2);
                        this.mHandlerThread = handlerThread2;
                        handlerThread2.start();
                    }
                    looper = this.mHandlerThread.getLooper();
                }
            }
        }
        this.mEventHandler = new EventHandler(looper);
    }

    public void setPrivateData(byte[] data) throws MediaCasException {
        validateInternalStates();
        try {
            ICas iCas = this.mICas;
            if (iCas != null) {
                try {
                    iCas.setPrivateData(data);
                } catch (ServiceSpecificException se) {
                    MediaCasException.throwExceptionIfNeeded(se.errorCode);
                }
            } else {
                MediaCasException.throwExceptionIfNeeded(this.mICasHidl.setPrivateData(toByteArray(data, 0, data.length)));
            }
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
    }

    /* loaded from: classes2.dex */
    private class OpenSessionCallback implements ICas.openSessionCallback {
        public Session mSession;
        public int mStatus;

        private OpenSessionCallback() {
        }

        @Override // android.hardware.cas.V1_0.ICas.openSessionCallback
        public void onValues(int status, ArrayList<Byte> sessionId) {
            this.mStatus = status;
            MediaCas mediaCas = MediaCas.this;
            this.mSession = mediaCas.createFromSessionId(mediaCas.toBytes(sessionId));
        }
    }

    /* loaded from: classes2.dex */
    private class OpenSession_1_2_Callback implements ICas.openSession_1_2Callback {
        public Session mSession;
        public int mStatus;

        private OpenSession_1_2_Callback() {
        }

        @Override // android.hardware.cas.V1_2.ICas.openSession_1_2Callback
        public void onValues(int status, ArrayList<Byte> sessionId) {
            this.mStatus = status;
            MediaCas mediaCas = MediaCas.this;
            this.mSession = mediaCas.createFromSessionId(mediaCas.toBytes(sessionId));
        }
    }

    private int getSessionResourceHandle() throws MediaCasException {
        validateInternalStates();
        int[] sessionResourceHandle = {-1};
        if (this.mTunerResourceManager != null) {
            CasSessionRequest casSessionRequest = new CasSessionRequest();
            casSessionRequest.clientId = this.mClientId;
            casSessionRequest.casSystemId = this.mCasSystemId;
            if (!this.mTunerResourceManager.requestCasSession(casSessionRequest, sessionResourceHandle)) {
                throw new MediaCasException.InsufficientResourceException("insufficient resource to Open Session");
            }
        }
        return sessionResourceHandle[0];
    }

    private void addSessionToResourceMap(Session session, int sessionResourceHandle) {
        if (sessionResourceHandle != -1) {
            synchronized (this.mSessionMap) {
                this.mSessionMap.put(session, Integer.valueOf(sessionResourceHandle));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeSessionFromResourceMap(Session session) {
        synchronized (this.mSessionMap) {
            if (this.mSessionMap.get(session) != null) {
                this.mTunerResourceManager.releaseCasSession(this.mSessionMap.get(session).intValue(), this.mClientId);
                this.mSessionMap.remove(session);
            }
        }
    }

    public Session openSession() throws MediaCasException {
        int sessionResourceHandle = getSessionResourceHandle();
        try {
            android.hardware.cas.ICas iCas = this.mICas;
            if (iCas != null) {
                try {
                    byte[] sessionId = iCas.openSessionDefault();
                    Session session = createFromSessionId(sessionId);
                    Log.m112d(TAG, "Write Stats Log for succeed to Open Session.");
                    FrameworkStatsLog.write(280, this.mUserId, this.mCasSystemId, 1);
                    return session;
                } catch (ServiceSpecificException se) {
                    MediaCasException.throwExceptionIfNeeded(se.errorCode);
                }
            } else if (this.mICasHidl != null) {
                OpenSessionCallback cb = new OpenSessionCallback();
                this.mICasHidl.openSession(cb);
                MediaCasException.throwExceptionIfNeeded(cb.mStatus);
                addSessionToResourceMap(cb.mSession, sessionResourceHandle);
                Log.m112d(TAG, "Write Stats Log for succeed to Open Session.");
                FrameworkStatsLog.write(280, this.mUserId, this.mCasSystemId, 1);
                return cb.mSession;
            }
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
        Log.m112d(TAG, "Write Stats Log for fail to Open Session.");
        FrameworkStatsLog.write(280, this.mUserId, this.mCasSystemId, 2);
        return null;
    }

    public Session openSession(int sessionUsage, int scramblingMode) throws MediaCasException {
        int sessionResourceHandle = getSessionResourceHandle();
        android.hardware.cas.ICas iCas = this.mICas;
        if (iCas != null) {
            try {
                byte[] sessionId = iCas.openSession(sessionUsage, scramblingMode);
                Session session = createFromSessionId(sessionId);
                addSessionToResourceMap(session, sessionResourceHandle);
                Log.m112d(TAG, "Write Stats Log for succeed to Open Session.");
                FrameworkStatsLog.write(280, this.mUserId, this.mCasSystemId, 1);
                return session;
            } catch (RemoteException | ServiceSpecificException e) {
                cleanupAndRethrowIllegalState();
            }
        }
        if (this.mICasHidl12 == null) {
            Log.m112d(TAG, "Open Session with scrambling mode is only supported by cas@1.2+ interface");
            throw new MediaCasException.UnsupportedCasException("Open Session with scrambling mode is not supported");
        }
        try {
            OpenSession_1_2_Callback cb = new OpenSession_1_2_Callback();
            this.mICasHidl12.openSession_1_2(sessionUsage, scramblingMode, cb);
            MediaCasException.throwExceptionIfNeeded(cb.mStatus);
            addSessionToResourceMap(cb.mSession, sessionResourceHandle);
            Log.m112d(TAG, "Write Stats Log for succeed to Open Session.");
            FrameworkStatsLog.write(280, this.mUserId, this.mCasSystemId, 1);
            return cb.mSession;
        } catch (RemoteException e2) {
            cleanupAndRethrowIllegalState();
            Log.m112d(TAG, "Write Stats Log for fail to Open Session.");
            FrameworkStatsLog.write(280, this.mUserId, this.mCasSystemId, 2);
            return null;
        }
    }

    public void processEmm(byte[] data, int offset, int length) throws MediaCasException {
        validateInternalStates();
        try {
            android.hardware.cas.ICas iCas = this.mICas;
            if (iCas != null) {
                try {
                    iCas.processEmm(Arrays.copyOfRange(data, offset, length));
                } catch (ServiceSpecificException se) {
                    MediaCasException.throwExceptionIfNeeded(se.errorCode);
                }
            } else {
                MediaCasException.throwExceptionIfNeeded(this.mICasHidl.processEmm(toByteArray(data, offset, length)));
            }
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void processEmm(byte[] data) throws MediaCasException {
        processEmm(data, 0, data.length);
    }

    public void sendEvent(int event, int arg, byte[] data) throws MediaCasException {
        validateInternalStates();
        try {
            android.hardware.cas.ICas iCas = this.mICas;
            if (iCas != null) {
                if (data == null) {
                    try {
                        data = new byte[0];
                    } catch (ServiceSpecificException se) {
                        MediaCasException.throwExceptionIfNeeded(se.errorCode);
                        return;
                    }
                }
                iCas.sendEvent(event, arg, data);
                return;
            }
            MediaCasException.throwExceptionIfNeeded(this.mICasHidl.sendEvent(event, arg, toByteArray(data)));
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void provision(String provisionString) throws MediaCasException {
        validateInternalStates();
        try {
            android.hardware.cas.ICas iCas = this.mICas;
            if (iCas != null) {
                try {
                    iCas.provision(provisionString);
                } catch (ServiceSpecificException se) {
                    MediaCasException.throwExceptionIfNeeded(se.errorCode);
                }
            } else {
                MediaCasException.throwExceptionIfNeeded(this.mICasHidl.provision(provisionString));
            }
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void refreshEntitlements(int refreshType, byte[] refreshData) throws MediaCasException {
        validateInternalStates();
        try {
            android.hardware.cas.ICas iCas = this.mICas;
            if (iCas != null) {
                if (refreshData == null) {
                    try {
                        refreshData = new byte[0];
                    } catch (ServiceSpecificException se) {
                        MediaCasException.throwExceptionIfNeeded(se.errorCode);
                        return;
                    }
                }
                iCas.refreshEntitlements(refreshType, refreshData);
                return;
            }
            MediaCasException.throwExceptionIfNeeded(this.mICasHidl.refreshEntitlements(refreshType, toByteArray(refreshData)));
        } catch (RemoteException e) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void forceResourceLost() {
        TunerResourceManager.ResourcesReclaimListener resourcesReclaimListener = this.mResourceListener;
        if (resourcesReclaimListener != null) {
            resourcesReclaimListener.onReclaimResources();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        android.hardware.cas.ICas iCas = this.mICas;
        if (iCas != null) {
            try {
                iCas.release();
            } catch (RemoteException e) {
            } catch (Throwable th) {
                this.mICas = null;
                throw th;
            }
            this.mICas = null;
        } else {
            android.hardware.cas.V1_0.ICas iCas2 = this.mICasHidl;
            if (iCas2 != null) {
                try {
                    iCas2.release();
                } catch (RemoteException e2) {
                } catch (Throwable th2) {
                    this.mICasHidl12 = null;
                    this.mICasHidl11 = null;
                    this.mICasHidl = null;
                    throw th2;
                }
                this.mICasHidl12 = null;
                this.mICasHidl11 = null;
                this.mICasHidl = null;
            }
        }
        TunerResourceManager tunerResourceManager = this.mTunerResourceManager;
        if (tunerResourceManager != null) {
            tunerResourceManager.unregisterClientProfile(this.mClientId);
            this.mTunerResourceManager = null;
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mHandlerThread = null;
        }
    }

    protected void finalize() {
        close();
    }
}
