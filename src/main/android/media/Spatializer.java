package android.media;

import android.annotation.SystemApi;
import android.media.CallbackUtil;
import android.media.ISpatializerCallback;
import android.media.ISpatializerHeadToSoundStagePoseCallback;
import android.media.ISpatializerHeadTrackerAvailableCallback;
import android.media.ISpatializerHeadTrackingModeCallback;
import android.media.ISpatializerOutputCallback;
import android.media.Spatializer;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.SafeCloseable;
import android.p008os.RemoteException;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class Spatializer {
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int HEAD_TRACKING_MODE_DISABLED = -1;
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int HEAD_TRACKING_MODE_OTHER = 0;
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int HEAD_TRACKING_MODE_RELATIVE_DEVICE = 2;
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int HEAD_TRACKING_MODE_RELATIVE_WORLD = 1;
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int HEAD_TRACKING_MODE_UNSUPPORTED = -2;
    public static final int SPATIALIZER_IMMERSIVE_LEVEL_MCHAN_BED_PLUS_OBJECTS = 2;
    public static final int SPATIALIZER_IMMERSIVE_LEVEL_MULTICHANNEL = 1;
    public static final int SPATIALIZER_IMMERSIVE_LEVEL_NONE = 0;
    public static final int SPATIALIZER_IMMERSIVE_LEVEL_OTHER = -1;
    private static final String TAG = "Spatializer";
    private final AudioManager mAm;
    private SpatializerOutputDispatcherStub mOutputDispatcher;
    private CallbackUtil.ListenerInfo<OnSpatializerOutputChangedListener> mOutputListener;
    private SpatializerPoseDispatcherStub mPoseDispatcher;
    private CallbackUtil.ListenerInfo<OnHeadToSoundstagePoseUpdatedListener> mPoseListener;
    private final CallbackUtil.LazyListenerManager<OnSpatializerStateChangedListener> mStateListenerMgr = new CallbackUtil.LazyListenerManager<>();
    private final CallbackUtil.LazyListenerManager<OnHeadTrackingModeChangedListener> mHeadTrackingListenerMgr = new CallbackUtil.LazyListenerManager<>();
    private final CallbackUtil.LazyListenerManager<OnHeadTrackerAvailableListener> mHeadTrackerListenerMgr = new CallbackUtil.LazyListenerManager<>();
    private final Object mPoseListenerLock = new Object();
    private final Object mOutputListenerLock = new Object();

    /* loaded from: classes2.dex */
    public @interface HeadTrackingMode {
    }

    /* loaded from: classes2.dex */
    public @interface HeadTrackingModeSet {
    }

    /* loaded from: classes2.dex */
    public @interface HeadTrackingModeSupported {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ImmersiveAudioLevel {
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    /* loaded from: classes2.dex */
    public interface OnHeadToSoundstagePoseUpdatedListener {
        void onHeadToSoundstagePoseUpdated(Spatializer spatializer, float[] fArr);
    }

    /* loaded from: classes2.dex */
    public interface OnHeadTrackerAvailableListener {
        void onHeadTrackerAvailableChanged(Spatializer spatializer, boolean z);
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    /* loaded from: classes2.dex */
    public interface OnHeadTrackingModeChangedListener {
        void onDesiredHeadTrackingModeChanged(Spatializer spatializer, int i);

        void onHeadTrackingModeChanged(Spatializer spatializer, int i);
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    /* loaded from: classes2.dex */
    public interface OnSpatializerOutputChangedListener {
        void onSpatializerOutputChanged(Spatializer spatializer, int i);
    }

    /* loaded from: classes2.dex */
    public interface OnSpatializerStateChangedListener {
        void onSpatializerAvailableChanged(Spatializer spatializer, boolean z);

        void onSpatializerEnabledChanged(Spatializer spatializer, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Spatializer(AudioManager am) {
        this.mAm = (AudioManager) Objects.requireNonNull(am);
    }

    public boolean isEnabled() {
        try {
            return AudioManager.getService().isSpatializerEnabled();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error querying isSpatializerEnabled, returning false", e);
            return false;
        }
    }

    public boolean isAvailable() {
        try {
            return AudioManager.getService().isSpatializerAvailable();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error querying isSpatializerAvailable, returning false", e);
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public boolean isAvailableForDevice(AudioDeviceAttributes device) {
        Objects.requireNonNull(device);
        try {
            return AudioManager.getService().isSpatializerAvailableForDevice(device);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public boolean hasHeadTracker(AudioDeviceAttributes device) {
        Objects.requireNonNull(device);
        try {
            return AudioManager.getService().hasHeadTracker(device);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setHeadTrackerEnabled(boolean enabled, AudioDeviceAttributes device) {
        Objects.requireNonNull(device);
        try {
            AudioManager.getService().setHeadTrackerEnabled(enabled, device);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public boolean isHeadTrackerEnabled(AudioDeviceAttributes device) {
        Objects.requireNonNull(device);
        try {
            return AudioManager.getService().isHeadTrackerEnabled(device);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean isHeadTrackerAvailable() {
        try {
            return AudioManager.getService().isHeadTrackerAvailable();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public void addOnHeadTrackerAvailableListener(Executor executor, OnHeadTrackerAvailableListener listener) {
        this.mHeadTrackerListenerMgr.addListener(executor, listener, "addOnHeadTrackerAvailableListener", new Supplier() { // from class: android.media.Spatializer$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                CallbackUtil.DispatcherStub lambda$addOnHeadTrackerAvailableListener$0;
                lambda$addOnHeadTrackerAvailableListener$0 = Spatializer.this.lambda$addOnHeadTrackerAvailableListener$0();
                return lambda$addOnHeadTrackerAvailableListener$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CallbackUtil.DispatcherStub lambda$addOnHeadTrackerAvailableListener$0() {
        return new SpatializerHeadTrackerAvailableDispatcherStub();
    }

    public void removeOnHeadTrackerAvailableListener(OnHeadTrackerAvailableListener listener) {
        this.mHeadTrackerListenerMgr.removeListener(listener, "removeOnHeadTrackerAvailableListener");
    }

    public static final String headtrackingModeToString(int mode) {
        switch (mode) {
            case -2:
                return "HEAD_TRACKING_MODE_UNSUPPORTED";
            case -1:
                return "HEAD_TRACKING_MODE_DISABLED";
            case 0:
                return "HEAD_TRACKING_MODE_OTHER";
            case 1:
                return "HEAD_TRACKING_MODE_RELATIVE_WORLD";
            case 2:
                return "HEAD_TRACKING_MODE_RELATIVE_DEVICE";
            default:
                return "head tracking mode unknown " + mode;
        }
    }

    public int getImmersiveAudioLevel() {
        try {
            int level = AudioManager.getService().getSpatializerImmersiveAudioLevel();
            return level;
        } catch (Exception e) {
            return 0;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setEnabled(boolean enabled) {
        try {
            AudioManager.getService().setSpatializerEnabled(enabled);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling setSpatializerEnabled", e);
        }
    }

    public boolean canBeSpatialized(AudioAttributes attributes, AudioFormat format) {
        try {
            return AudioManager.getService().canBeSpatialized((AudioAttributes) Objects.requireNonNull(attributes), (AudioFormat) Objects.requireNonNull(format));
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error querying canBeSpatialized for attr:" + attributes + " format:" + format + " returning false", e);
            return false;
        }
    }

    public void addOnSpatializerStateChangedListener(Executor executor, OnSpatializerStateChangedListener listener) {
        this.mStateListenerMgr.addListener(executor, listener, "addOnSpatializerStateChangedListener", new Supplier() { // from class: android.media.Spatializer$$ExternalSyntheticLambda2
            @Override // java.util.function.Supplier
            public final Object get() {
                CallbackUtil.DispatcherStub lambda$addOnSpatializerStateChangedListener$1;
                lambda$addOnSpatializerStateChangedListener$1 = Spatializer.this.lambda$addOnSpatializerStateChangedListener$1();
                return lambda$addOnSpatializerStateChangedListener$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CallbackUtil.DispatcherStub lambda$addOnSpatializerStateChangedListener$1() {
        return new SpatializerInfoDispatcherStub();
    }

    public void removeOnSpatializerStateChangedListener(OnSpatializerStateChangedListener listener) {
        this.mStateListenerMgr.removeListener(listener, "removeOnSpatializerStateChangedListener");
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public List<AudioDeviceAttributes> getCompatibleAudioDevices() {
        try {
            return AudioManager.getService().getSpatializerCompatibleAudioDevices();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error querying getSpatializerCompatibleAudioDevices(),  returning empty list", e);
            return new ArrayList(0);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void addCompatibleAudioDevice(AudioDeviceAttributes ada) {
        try {
            AudioManager.getService().addSpatializerCompatibleAudioDevice((AudioDeviceAttributes) Objects.requireNonNull(ada));
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling addSpatializerCompatibleAudioDevice(), ", e);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void removeCompatibleAudioDevice(AudioDeviceAttributes ada) {
        try {
            AudioManager.getService().removeSpatializerCompatibleAudioDevice((AudioDeviceAttributes) Objects.requireNonNull(ada));
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling removeSpatializerCompatibleAudioDevice(), ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class SpatializerInfoDispatcherStub extends ISpatializerCallback.Stub implements CallbackUtil.DispatcherStub {
        private SpatializerInfoDispatcherStub() {
        }

        @Override // android.media.CallbackUtil.DispatcherStub
        public void register(boolean register) {
            try {
                if (register) {
                    AudioManager unused = Spatializer.this.mAm;
                    AudioManager.getService().registerSpatializerCallback(this);
                } else {
                    AudioManager unused2 = Spatializer.this.mAm;
                    AudioManager.getService().unregisterSpatializerCallback(this);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        @Override // android.media.ISpatializerCallback
        public void dispatchSpatializerEnabledChanged(final boolean enabled) {
            Spatializer.this.mStateListenerMgr.callListeners(new CallbackUtil.CallbackMethod() { // from class: android.media.Spatializer$SpatializerInfoDispatcherStub$$ExternalSyntheticLambda1
                @Override // android.media.CallbackUtil.CallbackMethod
                public final void callbackMethod(Object obj) {
                    Spatializer.SpatializerInfoDispatcherStub.this.lambda$dispatchSpatializerEnabledChanged$0(enabled, (Spatializer.OnSpatializerStateChangedListener) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchSpatializerEnabledChanged$0(boolean enabled, OnSpatializerStateChangedListener listener) {
            listener.onSpatializerEnabledChanged(Spatializer.this, enabled);
        }

        @Override // android.media.ISpatializerCallback
        public void dispatchSpatializerAvailableChanged(final boolean available) {
            Spatializer.this.mStateListenerMgr.callListeners(new CallbackUtil.CallbackMethod() { // from class: android.media.Spatializer$SpatializerInfoDispatcherStub$$ExternalSyntheticLambda0
                @Override // android.media.CallbackUtil.CallbackMethod
                public final void callbackMethod(Object obj) {
                    Spatializer.SpatializerInfoDispatcherStub.this.lambda$dispatchSpatializerAvailableChanged$1(available, (Spatializer.OnSpatializerStateChangedListener) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchSpatializerAvailableChanged$1(boolean available, OnSpatializerStateChangedListener listener) {
            listener.onSpatializerAvailableChanged(Spatializer.this, available);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public int getHeadTrackingMode() {
        try {
            return AudioManager.getService().getActualHeadTrackingMode();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling getActualHeadTrackingMode", e);
            return -2;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public int getDesiredHeadTrackingMode() {
        try {
            return AudioManager.getService().getDesiredHeadTrackingMode();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling getDesiredHeadTrackingMode", e);
            return -2;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public List<Integer> getSupportedHeadTrackingModes() {
        try {
            int[] modes = AudioManager.getService().getSupportedHeadTrackingModes();
            ArrayList<Integer> list = new ArrayList<>(0);
            for (int mode : modes) {
                list.add(Integer.valueOf(mode));
            }
            return list;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling getSupportedHeadTrackModes", e);
            return new ArrayList(0);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setDesiredHeadTrackingMode(int mode) {
        try {
            AudioManager.getService().setDesiredHeadTrackingMode(mode);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling setDesiredHeadTrackingMode to " + mode, e);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void recenterHeadTracker() {
        try {
            AudioManager.getService().recenterHeadTracker();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling recenterHeadTracker", e);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void addOnHeadTrackingModeChangedListener(Executor executor, OnHeadTrackingModeChangedListener listener) {
        this.mHeadTrackingListenerMgr.addListener(executor, listener, "addOnHeadTrackingModeChangedListener", new Supplier() { // from class: android.media.Spatializer$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                CallbackUtil.DispatcherStub lambda$addOnHeadTrackingModeChangedListener$2;
                lambda$addOnHeadTrackingModeChangedListener$2 = Spatializer.this.lambda$addOnHeadTrackingModeChangedListener$2();
                return lambda$addOnHeadTrackingModeChangedListener$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CallbackUtil.DispatcherStub lambda$addOnHeadTrackingModeChangedListener$2() {
        return new SpatializerHeadTrackingDispatcherStub();
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void removeOnHeadTrackingModeChangedListener(OnHeadTrackingModeChangedListener listener) {
        this.mHeadTrackingListenerMgr.removeListener(listener, "removeOnHeadTrackingModeChangedListener");
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setOnHeadToSoundstagePoseUpdatedListener(Executor executor, OnHeadToSoundstagePoseUpdatedListener listener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(listener);
        synchronized (this.mPoseListenerLock) {
            if (this.mPoseListener != null) {
                throw new IllegalStateException("Trying to overwrite existing listener");
            }
            this.mPoseListener = new CallbackUtil.ListenerInfo<>(listener, executor);
            this.mPoseDispatcher = new SpatializerPoseDispatcherStub();
            try {
                AudioManager.getService().registerHeadToSoundstagePoseCallback(this.mPoseDispatcher);
            } catch (RemoteException e) {
                this.mPoseListener = null;
                this.mPoseDispatcher = null;
            }
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void clearOnHeadToSoundstagePoseUpdatedListener() {
        synchronized (this.mPoseListenerLock) {
            if (this.mPoseDispatcher == null) {
                throw new IllegalStateException("No listener to clear");
            }
            try {
                AudioManager.getService().unregisterHeadToSoundstagePoseCallback(this.mPoseDispatcher);
            } catch (RemoteException e) {
            }
            this.mPoseListener = null;
            this.mPoseDispatcher = null;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setGlobalTransform(float[] transform) {
        if (((float[]) Objects.requireNonNull(transform)).length != 6) {
            throw new IllegalArgumentException("transform array must be of size 6, was " + transform.length);
        }
        try {
            AudioManager.getService().setSpatializerGlobalTransform(transform);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling setGlobalTransform", e);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setEffectParameter(int key, byte[] value) {
        Objects.requireNonNull(value);
        try {
            AudioManager.getService().setSpatializerParameter(key, value);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling setEffectParameter", e);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void getEffectParameter(int key, byte[] value) {
        Objects.requireNonNull(value);
        try {
            AudioManager.getService().getSpatializerParameter(key, value);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling getEffectParameter", e);
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public int getOutput() {
        try {
            return AudioManager.getService().getSpatializerOutput();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Error calling getSpatializerOutput", e);
            return 0;
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void setOnSpatializerOutputChangedListener(Executor executor, OnSpatializerOutputChangedListener listener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(listener);
        synchronized (this.mOutputListenerLock) {
            if (this.mOutputListener != null) {
                throw new IllegalStateException("Trying to overwrite existing listener");
            }
            this.mOutputListener = new CallbackUtil.ListenerInfo<>(listener, executor);
            this.mOutputDispatcher = new SpatializerOutputDispatcherStub();
            try {
                AudioManager.getService().registerSpatializerOutputCallback(this.mOutputDispatcher);
                this.mOutputDispatcher.dispatchSpatializerOutputChanged(getOutput());
            } catch (RemoteException e) {
                this.mOutputListener = null;
                this.mOutputDispatcher = null;
            }
        }
    }

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public void clearOnSpatializerOutputChangedListener() {
        synchronized (this.mOutputListenerLock) {
            if (this.mOutputDispatcher == null) {
                throw new IllegalStateException("No listener to clear");
            }
            try {
                AudioManager.getService().unregisterSpatializerOutputCallback(this.mOutputDispatcher);
            } catch (RemoteException e) {
            }
            this.mOutputListener = null;
            this.mOutputDispatcher = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class SpatializerHeadTrackingDispatcherStub extends ISpatializerHeadTrackingModeCallback.Stub implements CallbackUtil.DispatcherStub {
        private SpatializerHeadTrackingDispatcherStub() {
        }

        @Override // android.media.CallbackUtil.DispatcherStub
        public void register(boolean register) {
            try {
                if (register) {
                    AudioManager unused = Spatializer.this.mAm;
                    AudioManager.getService().registerSpatializerHeadTrackingCallback(this);
                } else {
                    AudioManager unused2 = Spatializer.this.mAm;
                    AudioManager.getService().unregisterSpatializerHeadTrackingCallback(this);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        @Override // android.media.ISpatializerHeadTrackingModeCallback
        public void dispatchSpatializerActualHeadTrackingModeChanged(final int mode) {
            Spatializer.this.mHeadTrackingListenerMgr.callListeners(new CallbackUtil.CallbackMethod() { // from class: android.media.Spatializer$SpatializerHeadTrackingDispatcherStub$$ExternalSyntheticLambda0
                @Override // android.media.CallbackUtil.CallbackMethod
                public final void callbackMethod(Object obj) {
                    Spatializer.SpatializerHeadTrackingDispatcherStub.this.lambda$dispatchSpatializerActualHeadTrackingModeChanged$0(mode, (Spatializer.OnHeadTrackingModeChangedListener) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchSpatializerActualHeadTrackingModeChanged$0(int mode, OnHeadTrackingModeChangedListener listener) {
            listener.onHeadTrackingModeChanged(Spatializer.this, mode);
        }

        @Override // android.media.ISpatializerHeadTrackingModeCallback
        public void dispatchSpatializerDesiredHeadTrackingModeChanged(final int mode) {
            Spatializer.this.mHeadTrackingListenerMgr.callListeners(new CallbackUtil.CallbackMethod() { // from class: android.media.Spatializer$SpatializerHeadTrackingDispatcherStub$$ExternalSyntheticLambda1
                @Override // android.media.CallbackUtil.CallbackMethod
                public final void callbackMethod(Object obj) {
                    Spatializer.SpatializerHeadTrackingDispatcherStub.this.lambda$dispatchSpatializerDesiredHeadTrackingModeChanged$1(mode, (Spatializer.OnHeadTrackingModeChangedListener) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchSpatializerDesiredHeadTrackingModeChanged$1(int mode, OnHeadTrackingModeChangedListener listener) {
            listener.onDesiredHeadTrackingModeChanged(Spatializer.this, mode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class SpatializerHeadTrackerAvailableDispatcherStub extends ISpatializerHeadTrackerAvailableCallback.Stub implements CallbackUtil.DispatcherStub {
        private SpatializerHeadTrackerAvailableDispatcherStub() {
        }

        @Override // android.media.CallbackUtil.DispatcherStub
        public void register(boolean register) {
            try {
                AudioManager unused = Spatializer.this.mAm;
                AudioManager.getService().registerSpatializerHeadTrackerAvailableCallback(this, register);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        @Override // android.media.ISpatializerHeadTrackerAvailableCallback
        public void dispatchSpatializerHeadTrackerAvailable(final boolean available) {
            Spatializer.this.mHeadTrackerListenerMgr.callListeners(new CallbackUtil.CallbackMethod() { // from class: android.media.Spatializer$SpatializerHeadTrackerAvailableDispatcherStub$$ExternalSyntheticLambda0
                @Override // android.media.CallbackUtil.CallbackMethod
                public final void callbackMethod(Object obj) {
                    Spatializer.SpatializerHeadTrackerAvailableDispatcherStub.this.lambda$dispatchSpatializerHeadTrackerAvailable$0(available, (Spatializer.OnHeadTrackerAvailableListener) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchSpatializerHeadTrackerAvailable$0(boolean available, OnHeadTrackerAvailableListener listener) {
            listener.onHeadTrackerAvailableChanged(Spatializer.this, available);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class SpatializerPoseDispatcherStub extends ISpatializerHeadToSoundStagePoseCallback.Stub {
        private SpatializerPoseDispatcherStub() {
        }

        @Override // android.media.ISpatializerHeadToSoundStagePoseCallback
        public void dispatchPoseChanged(final float[] pose) {
            final CallbackUtil.ListenerInfo<OnHeadToSoundstagePoseUpdatedListener> listener;
            synchronized (Spatializer.this.mPoseListenerLock) {
                listener = Spatializer.this.mPoseListener;
            }
            if (listener == null) {
                return;
            }
            SafeCloseable ignored = ClearCallingIdentityContext.create();
            try {
                listener.mExecutor.execute(new Runnable() { // from class: android.media.Spatializer$SpatializerPoseDispatcherStub$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Spatializer.SpatializerPoseDispatcherStub.this.lambda$dispatchPoseChanged$0(listener, pose);
                    }
                });
                if (ignored != null) {
                    ignored.close();
                }
            } catch (Throwable th) {
                if (ignored != null) {
                    try {
                        ignored.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchPoseChanged$0(CallbackUtil.ListenerInfo listener, float[] pose) {
            ((OnHeadToSoundstagePoseUpdatedListener) listener.mListener).onHeadToSoundstagePoseUpdated(Spatializer.this, pose);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class SpatializerOutputDispatcherStub extends ISpatializerOutputCallback.Stub {
        private SpatializerOutputDispatcherStub() {
        }

        @Override // android.media.ISpatializerOutputCallback
        public void dispatchSpatializerOutputChanged(final int output) {
            final CallbackUtil.ListenerInfo<OnSpatializerOutputChangedListener> listener;
            synchronized (Spatializer.this.mOutputListenerLock) {
                listener = Spatializer.this.mOutputListener;
            }
            if (listener == null) {
                return;
            }
            SafeCloseable ignored = ClearCallingIdentityContext.create();
            try {
                listener.mExecutor.execute(new Runnable() { // from class: android.media.Spatializer$SpatializerOutputDispatcherStub$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Spatializer.SpatializerOutputDispatcherStub.this.lambda$dispatchSpatializerOutputChanged$0(listener, output);
                    }
                });
                if (ignored != null) {
                    ignored.close();
                }
            } catch (Throwable th) {
                if (ignored != null) {
                    try {
                        ignored.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchSpatializerOutputChanged$0(CallbackUtil.ListenerInfo listener, int output) {
            ((OnSpatializerOutputChangedListener) listener.mListener).onSpatializerOutputChanged(Spatializer.this, output);
        }
    }
}
