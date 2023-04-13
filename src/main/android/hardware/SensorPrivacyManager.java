package android.hardware;

import android.annotation.SystemApi;
import android.content.Context;
import android.hardware.ISensorPrivacyListener;
import android.hardware.ISensorPrivacyManager;
import android.hardware.SensorPrivacyManager;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class SensorPrivacyManager {
    public static final int TOGGLE_TYPE_HARDWARE = 2;
    public static final int TOGGLE_TYPE_SOFTWARE = 1;
    private static SensorPrivacyManager sInstance;
    private final Context mContext;
    private final ISensorPrivacyManager mService;
    private static final String LOG_TAG = SensorPrivacyManager.class.getSimpleName();
    public static final String EXTRA_SENSOR = SensorPrivacyManager.class.getName() + ".extra.sensor";
    public static final String EXTRA_ALL_SENSORS = SensorPrivacyManager.class.getName() + ".extra.all_sensors";
    public static final String EXTRA_TOGGLE_TYPE = SensorPrivacyManager.class.getName() + ".extra.toggle_type";
    private static final Object sInstanceLock = new Object();
    private IBinder token = new Binder();
    private final Object mLock = new Object();
    private final ArrayMap<Pair<Integer, Integer>, Boolean> mToggleSupportCache = new ArrayMap<>();
    private final ArrayMap<OnSensorPrivacyChangedListener, Executor> mToggleListeners = new ArrayMap<>();
    private final ArrayMap<Pair<Integer, OnSensorPrivacyChangedListener>, OnSensorPrivacyChangedListener> mLegacyToggleListeners = new ArrayMap<>();
    private final ISensorPrivacyListener mIToggleListener = new BinderC08611();
    private boolean mToggleListenerRegistered = false;
    private Boolean mRequiresAuthentication = null;
    private final ArrayMap<OnAllSensorPrivacyChangedListener, ISensorPrivacyListener> mListeners = new ArrayMap<>();

    /* loaded from: classes.dex */
    public interface OnAllSensorPrivacyChangedListener {
        void onAllSensorPrivacyChanged(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ToggleType {
    }

    /* loaded from: classes.dex */
    public static class Sensors {
        public static final int CAMERA = 2;
        public static final int MICROPHONE = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface Sensor {
        }

        private Sensors() {
        }
    }

    /* loaded from: classes.dex */
    public static class Sources {
        public static final int DIALOG = 3;
        public static final int OTHER = 5;
        public static final int QS_TILE = 1;
        public static final int SAFETY_CENTER = 6;
        public static final int SETTINGS = 2;
        public static final int SHELL = 4;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface Source {
        }

        private Sources() {
        }
    }

    /* loaded from: classes.dex */
    public static class StateTypes {
        public static final int DISABLED = 2;
        public static final int ENABLED = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface StateType {
        }

        private StateTypes() {
        }
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface OnSensorPrivacyChangedListener {
        @Deprecated
        void onSensorPrivacyChanged(int i, boolean z);

        default void onSensorPrivacyChanged(SensorPrivacyChangedParams params) {
            onSensorPrivacyChanged(params.mSensor, params.mEnabled);
        }

        /* loaded from: classes.dex */
        public static class SensorPrivacyChangedParams {
            private boolean mEnabled;
            private int mSensor;
            private int mToggleType;

            private SensorPrivacyChangedParams(int toggleType, int sensor, boolean enabled) {
                this.mToggleType = toggleType;
                this.mSensor = sensor;
                this.mEnabled = enabled;
            }

            public int getToggleType() {
                return this.mToggleType;
            }

            public int getSensor() {
                return this.mSensor;
            }

            public boolean isEnabled() {
                return this.mEnabled;
            }
        }
    }

    /* renamed from: android.hardware.SensorPrivacyManager$1 */
    /* loaded from: classes.dex */
    class BinderC08611 extends ISensorPrivacyListener.Stub {
        BinderC08611() {
        }

        @Override // android.hardware.ISensorPrivacyListener
        public void onSensorPrivacyChanged(final int toggleType, final int sensor, final boolean enabled) {
            synchronized (SensorPrivacyManager.this.mLock) {
                for (int i = 0; i < SensorPrivacyManager.this.mToggleListeners.size(); i++) {
                    final OnSensorPrivacyChangedListener listener = (OnSensorPrivacyChangedListener) SensorPrivacyManager.this.mToggleListeners.keyAt(i);
                    ((Executor) SensorPrivacyManager.this.mToggleListeners.valueAt(i)).execute(new Runnable() { // from class: android.hardware.SensorPrivacyManager$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SensorPrivacyManager.OnSensorPrivacyChangedListener.this.onSensorPrivacyChanged(new SensorPrivacyManager.OnSensorPrivacyChangedListener.SensorPrivacyChangedParams(toggleType, sensor, enabled));
                        }
                    });
                }
            }
        }
    }

    private SensorPrivacyManager(Context context, ISensorPrivacyManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public static SensorPrivacyManager getInstance(Context context) {
        SensorPrivacyManager sensorPrivacyManager;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                try {
                    IBinder b = ServiceManager.getServiceOrThrow(Context.SENSOR_PRIVACY_SERVICE);
                    ISensorPrivacyManager service = ISensorPrivacyManager.Stub.asInterface(b);
                    sInstance = new SensorPrivacyManager(context, service);
                } catch (ServiceManager.ServiceNotFoundException e) {
                    throw new IllegalStateException(e);
                }
            }
            sensorPrivacyManager = sInstance;
        }
        return sensorPrivacyManager;
    }

    public static SensorPrivacyManager getInstance(Context context, ISensorPrivacyManager service) {
        SensorPrivacyManager sensorPrivacyManager;
        synchronized (sInstanceLock) {
            sensorPrivacyManager = new SensorPrivacyManager(context, service);
            sInstance = sensorPrivacyManager;
        }
        return sensorPrivacyManager;
    }

    public boolean supportsSensorToggle(int sensor) {
        return supportsSensorToggle(1, sensor);
    }

    public boolean supportsSensorToggle(int toggleType, int sensor) {
        boolean booleanValue;
        try {
            Pair key = new Pair(Integer.valueOf(toggleType), Integer.valueOf(sensor));
            synchronized (this.mLock) {
                Boolean val = this.mToggleSupportCache.get(key);
                if (val == null) {
                    val = Boolean.valueOf(this.mService.supportsSensorToggle(toggleType, sensor));
                    this.mToggleSupportCache.put(key, val);
                }
                booleanValue = val.booleanValue();
            }
            return booleanValue;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void addSensorPrivacyListener(int sensor, OnSensorPrivacyChangedListener listener) {
        addSensorPrivacyListener(sensor, this.mContext.getMainExecutor(), listener);
    }

    public void addSensorPrivacyListener(int sensor, int userId, OnSensorPrivacyChangedListener listener) {
        addSensorPrivacyListener(sensor, this.mContext.getMainExecutor(), listener);
    }

    @SystemApi
    public void addSensorPrivacyListener(final int sensor, Executor executor, final OnSensorPrivacyChangedListener listener) {
        Pair<Integer, OnSensorPrivacyChangedListener> pair = new Pair<>(Integer.valueOf(sensor), listener);
        OnSensorPrivacyChangedListener toggleListener = new OnSensorPrivacyChangedListener() { // from class: android.hardware.SensorPrivacyManager.2
            @Override // android.hardware.SensorPrivacyManager.OnSensorPrivacyChangedListener
            public void onSensorPrivacyChanged(OnSensorPrivacyChangedListener.SensorPrivacyChangedParams params) {
                if (params.getSensor() == sensor) {
                    listener.onSensorPrivacyChanged(params);
                }
            }

            @Override // android.hardware.SensorPrivacyManager.OnSensorPrivacyChangedListener
            public void onSensorPrivacyChanged(int sensor2, boolean enabled) {
            }
        };
        synchronized (this.mLock) {
            this.mLegacyToggleListeners.put(pair, toggleListener);
            addSensorPrivacyListenerLocked(executor, toggleListener);
        }
    }

    @SystemApi
    public void addSensorPrivacyListener(OnSensorPrivacyChangedListener listener) {
        addSensorPrivacyListener(this.mContext.getMainExecutor(), listener);
    }

    @SystemApi
    public void addSensorPrivacyListener(Executor executor, OnSensorPrivacyChangedListener listener) {
        synchronized (this.mLock) {
            addSensorPrivacyListenerLocked(executor, listener);
        }
    }

    private void addSensorPrivacyListenerLocked(Executor executor, OnSensorPrivacyChangedListener listener) {
        if (!this.mToggleListenerRegistered) {
            try {
                this.mService.addToggleSensorPrivacyListener(this.mIToggleListener);
                this.mToggleListenerRegistered = true;
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }
        if (this.mToggleListeners.containsKey(listener)) {
            throw new IllegalArgumentException("listener is already registered");
        }
        this.mToggleListeners.put(listener, executor);
    }

    @SystemApi
    public void removeSensorPrivacyListener(int sensor, OnSensorPrivacyChangedListener listener) {
        Pair<Integer, OnSensorPrivacyChangedListener> pair = new Pair<>(Integer.valueOf(sensor), listener);
        synchronized (this.mLock) {
            OnSensorPrivacyChangedListener onToggleSensorPrivacyChangedListener = this.mLegacyToggleListeners.remove(pair);
            if (onToggleSensorPrivacyChangedListener != null) {
                removeSensorPrivacyListenerLocked(onToggleSensorPrivacyChangedListener);
            }
        }
    }

    @SystemApi
    public void removeSensorPrivacyListener(OnSensorPrivacyChangedListener listener) {
        synchronized (this.mLock) {
            removeSensorPrivacyListenerLocked(listener);
        }
    }

    private void removeSensorPrivacyListenerLocked(OnSensorPrivacyChangedListener listener) {
        this.mToggleListeners.remove(listener);
        if (this.mToggleListeners.size() == 0) {
            try {
                this.mService.removeToggleSensorPrivacyListener(this.mIToggleListener);
                this.mToggleListenerRegistered = false;
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    @Deprecated
    public boolean isSensorPrivacyEnabled(int sensor) {
        return isSensorPrivacyEnabled(1, sensor);
    }

    @SystemApi
    public boolean isSensorPrivacyEnabled(int toggleType, int sensor) {
        try {
            return this.mService.isToggleSensorPrivacyEnabled(toggleType, sensor);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean areAnySensorPrivacyTogglesEnabled(int sensor) {
        try {
            return this.mService.isCombinedToggleSensorPrivacyEnabled(sensor);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setSensorPrivacy(int sensor, boolean enable) {
        setSensorPrivacy(resolveSourceFromCurrentContext(), sensor, enable, -2);
    }

    private int resolveSourceFromCurrentContext() {
        String packageName = this.mContext.getOpPackageName();
        if (Objects.equals(packageName, this.mContext.getPackageManager().getPermissionControllerPackageName())) {
            return 6;
        }
        return 5;
    }

    public void setSensorPrivacy(int source, int sensor, boolean enable) {
        setSensorPrivacy(source, sensor, enable, -2);
    }

    public void setSensorPrivacy(int source, int sensor, boolean enable, int userId) {
        try {
            this.mService.setToggleSensorPrivacy(userId, source, sensor, enable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setSensorPrivacyForProfileGroup(int source, int sensor, boolean enable) {
        setSensorPrivacyForProfileGroup(source, sensor, enable, -2);
    }

    public void setSensorPrivacyForProfileGroup(int source, int sensor, boolean enable, int userId) {
        try {
            this.mService.setToggleSensorPrivacyForProfileGroup(userId, source, sensor, enable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void suppressSensorPrivacyReminders(int sensor, boolean suppress) {
        suppressSensorPrivacyReminders(sensor, suppress, -2);
    }

    public void suppressSensorPrivacyReminders(int sensor, boolean suppress, int userId) {
        try {
            this.mService.suppressToggleSensorPrivacyReminders(userId, sensor, this.token, suppress);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requiresAuthentication() {
        if (this.mRequiresAuthentication == null) {
            try {
                this.mRequiresAuthentication = Boolean.valueOf(this.mService.requiresAuthentication());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return this.mRequiresAuthentication.booleanValue();
    }

    public void showSensorUseDialog(int sensor) {
        try {
            this.mService.showSensorUseDialog(sensor);
        } catch (RemoteException e) {
            Log.m109e(LOG_TAG, "Received exception while trying to show sensor use dialog", e);
        }
    }

    public void setAllSensorPrivacy(boolean enable) {
        try {
            this.mService.setSensorPrivacy(enable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addAllSensorPrivacyListener(final OnAllSensorPrivacyChangedListener listener) {
        synchronized (this.mListeners) {
            ISensorPrivacyListener iListener = this.mListeners.get(listener);
            if (iListener == null) {
                iListener = new ISensorPrivacyListener.Stub() { // from class: android.hardware.SensorPrivacyManager.3
                    @Override // android.hardware.ISensorPrivacyListener
                    public void onSensorPrivacyChanged(int toggleType, int sensor, boolean enabled) {
                        listener.onAllSensorPrivacyChanged(enabled);
                    }
                };
                this.mListeners.put(listener, iListener);
            }
            try {
                this.mService.addSensorPrivacyListener(iListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void removeAllSensorPrivacyListener(OnAllSensorPrivacyChangedListener listener) {
        synchronized (this.mListeners) {
            ISensorPrivacyListener iListener = this.mListeners.get(listener);
            if (iListener != null) {
                this.mListeners.remove(iListener);
                try {
                    this.mService.removeSensorPrivacyListener(iListener);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    public boolean isAllSensorPrivacyEnabled() {
        try {
            return this.mService.isSensorPrivacyEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
