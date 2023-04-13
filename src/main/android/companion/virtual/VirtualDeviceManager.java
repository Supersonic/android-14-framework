package android.companion.virtual;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.companion.virtual.IVirtualDeviceActivityListener;
import android.companion.virtual.IVirtualDeviceIntentInterceptor;
import android.companion.virtual.IVirtualDeviceSoundEffectListener;
import android.companion.virtual.VirtualDeviceManager;
import android.companion.virtual.audio.VirtualAudioDevice;
import android.companion.virtual.camera.VirtualCameraDevice;
import android.companion.virtual.camera.VirtualCameraInput;
import android.companion.virtual.sensor.VirtualSensor;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplayConfig;
import android.hardware.input.VirtualDpad;
import android.hardware.input.VirtualDpadConfig;
import android.hardware.input.VirtualKeyboard;
import android.hardware.input.VirtualKeyboardConfig;
import android.hardware.input.VirtualMouse;
import android.hardware.input.VirtualMouseConfig;
import android.hardware.input.VirtualNavigationTouchpad;
import android.hardware.input.VirtualNavigationTouchpadConfig;
import android.hardware.input.VirtualTouchscreen;
import android.hardware.input.VirtualTouchscreenConfig;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Surface;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
/* loaded from: classes.dex */
public final class VirtualDeviceManager {
    public static final String ACTION_VIRTUAL_DEVICE_REMOVED = "android.companion.virtual.action.VIRTUAL_DEVICE_REMOVED";
    public static final String EXTRA_VIRTUAL_DEVICE_ID = "android.companion.virtual.extra.VIRTUAL_DEVICE_ID";
    @SystemApi
    public static final int LAUNCH_FAILURE_NO_ACTIVITY = 2;
    @SystemApi
    public static final int LAUNCH_FAILURE_PENDING_INTENT_CANCELED = 1;
    @SystemApi
    public static final int LAUNCH_SUCCESS = 0;
    private static final String TAG = "VirtualDeviceManager";
    private final Context mContext;
    private final IVirtualDeviceManager mService;

    @SystemApi
    /* loaded from: classes.dex */
    public interface IntentInterceptorCallback {
        void onIntentIntercepted(Intent intent);
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PendingIntentLaunchStatus {
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface SoundEffectListener {
        void onPlaySoundEffect(int i);
    }

    public VirtualDeviceManager(IVirtualDeviceManager service, Context context) {
        this.mService = service;
        this.mContext = context;
    }

    @SystemApi
    public VirtualDevice createVirtualDevice(int associationId, VirtualDeviceParams params) {
        try {
            return new VirtualDevice(this.mService, this.mContext, associationId, params);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<android.companion.virtual.VirtualDevice> getVirtualDevices() {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            Log.m104w(TAG, "Failed to retrieve virtual devices; no virtual device manager service.");
            return new ArrayList();
        }
        try {
            return iVirtualDeviceManager.getVirtualDevices();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDevicePolicy(int deviceId, int policyType) {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            Log.m104w(TAG, "Failed to retrieve device policy; no virtual device manager service.");
            return 0;
        }
        try {
            return iVirtualDeviceManager.getDevicePolicy(deviceId, policyType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDeviceIdForDisplayId(int displayId) {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            Log.m104w(TAG, "Failed to retrieve virtual devices; no virtual device manager service.");
            return 0;
        }
        try {
            return iVirtualDeviceManager.getDeviceIdForDisplayId(displayId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isValidVirtualDeviceId(int deviceId) {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            Log.m104w(TAG, "Failed to retrieve virtual devices; no virtual device manager service.");
            return false;
        }
        try {
            return iVirtualDeviceManager.isValidVirtualDeviceId(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getAudioPlaybackSessionId(int deviceId) {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            return 0;
        }
        try {
            return iVirtualDeviceManager.getAudioPlaybackSessionId(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getAudioRecordingSessionId(int deviceId) {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            return 0;
        }
        try {
            return iVirtualDeviceManager.getAudioRecordingSessionId(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void playSoundEffect(int deviceId, int effectType) {
        IVirtualDeviceManager iVirtualDeviceManager = this.mService;
        if (iVirtualDeviceManager == null) {
            Log.m104w(TAG, "Failed to dispatch sound effect; no virtual device manager service.");
            return;
        }
        try {
            iVirtualDeviceManager.playSoundEffect(deviceId, effectType);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static class VirtualDevice implements AutoCloseable {
        private final IVirtualDeviceActivityListener mActivityListenerBinder;
        private final ArrayMap<ActivityListener, ActivityListenerDelegate> mActivityListeners;
        private final Object mActivityListenersLock;
        private final Context mContext;
        private final ArrayMap<IntentInterceptorCallback, VirtualIntentInterceptorDelegate> mIntentInterceptorListeners;
        private final Object mIntentInterceptorListenersLock;
        private final IVirtualDeviceManager mService;
        private final IVirtualDeviceSoundEffectListener mSoundEffectListener;
        private final ArrayMap<SoundEffectListener, SoundEffectListenerDelegate> mSoundEffectListeners;
        private final Object mSoundEffectListenersLock;
        private VirtualAudioDevice mVirtualAudioDevice;
        private VirtualCameraDevice mVirtualCameraDevice;
        private final IVirtualDevice mVirtualDevice;

        private VirtualDevice(IVirtualDeviceManager service, Context context, int associationId, VirtualDeviceParams params) throws RemoteException {
            this.mActivityListenersLock = new Object();
            this.mActivityListeners = new ArrayMap<>();
            this.mIntentInterceptorListenersLock = new Object();
            this.mIntentInterceptorListeners = new ArrayMap<>();
            this.mSoundEffectListenersLock = new Object();
            this.mSoundEffectListeners = new ArrayMap<>();
            IVirtualDeviceActivityListener.Stub stub = new IVirtualDeviceActivityListener.Stub() { // from class: android.companion.virtual.VirtualDeviceManager.VirtualDevice.1
                @Override // android.companion.virtual.IVirtualDeviceActivityListener
                public void onTopActivityChanged(int displayId, ComponentName topActivity, int userId) {
                    long token = Binder.clearCallingIdentity();
                    try {
                        synchronized (VirtualDevice.this.mActivityListenersLock) {
                            for (int i = 0; i < VirtualDevice.this.mActivityListeners.size(); i++) {
                                ((ActivityListenerDelegate) VirtualDevice.this.mActivityListeners.valueAt(i)).onTopActivityChanged(displayId, topActivity);
                                ((ActivityListenerDelegate) VirtualDevice.this.mActivityListeners.valueAt(i)).onTopActivityChanged(displayId, topActivity, userId);
                            }
                        }
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                }

                @Override // android.companion.virtual.IVirtualDeviceActivityListener
                public void onDisplayEmpty(int displayId) {
                    long token = Binder.clearCallingIdentity();
                    try {
                        synchronized (VirtualDevice.this.mActivityListenersLock) {
                            for (int i = 0; i < VirtualDevice.this.mActivityListeners.size(); i++) {
                                ((ActivityListenerDelegate) VirtualDevice.this.mActivityListeners.valueAt(i)).onDisplayEmpty(displayId);
                            }
                        }
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                }
            };
            this.mActivityListenerBinder = stub;
            IVirtualDeviceSoundEffectListener.Stub stub2 = new IVirtualDeviceSoundEffectListener.Stub() { // from class: android.companion.virtual.VirtualDeviceManager.VirtualDevice.2
                @Override // android.companion.virtual.IVirtualDeviceSoundEffectListener
                public void onPlaySoundEffect(int soundEffect) {
                    long token = Binder.clearCallingIdentity();
                    try {
                        synchronized (VirtualDevice.this.mSoundEffectListenersLock) {
                            for (int i = 0; i < VirtualDevice.this.mSoundEffectListeners.size(); i++) {
                                ((SoundEffectListenerDelegate) VirtualDevice.this.mSoundEffectListeners.valueAt(i)).onPlaySoundEffect(soundEffect);
                            }
                        }
                    } finally {
                        Binder.restoreCallingIdentity(token);
                    }
                }
            };
            this.mSoundEffectListener = stub2;
            this.mService = service;
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            this.mVirtualDevice = service.createVirtualDevice(new Binder(), applicationContext.getPackageName(), associationId, params, stub, stub2);
        }

        public int getDeviceId() {
            try {
                return this.mVirtualDevice.getDeviceId();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public Context createContext() {
            try {
                return this.mContext.createDeviceContext(this.mVirtualDevice.getDeviceId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public List<VirtualSensor> getVirtualSensorList() {
            try {
                return this.mVirtualDevice.getVirtualSensorList();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void launchPendingIntent(int displayId, PendingIntent pendingIntent, Executor executor, IntConsumer listener) {
            try {
                this.mVirtualDevice.launchPendingIntent(displayId, pendingIntent, new ResultReceiverC05793(new Handler(Looper.getMainLooper()), executor, listener));
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        /* renamed from: android.companion.virtual.VirtualDeviceManager$VirtualDevice$3 */
        /* loaded from: classes.dex */
        class ResultReceiverC05793 extends ResultReceiver {
            final /* synthetic */ Executor val$executor;
            final /* synthetic */ IntConsumer val$listener;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            ResultReceiverC05793(Handler handler, Executor executor, IntConsumer intConsumer) {
                super(handler);
                this.val$executor = executor;
                this.val$listener = intConsumer;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.ResultReceiver
            public void onReceiveResult(final int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                Executor executor = this.val$executor;
                final IntConsumer intConsumer = this.val$listener;
                executor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceManager$VirtualDevice$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        intConsumer.accept(resultCode);
                    }
                });
            }
        }

        @Deprecated
        public VirtualDisplay createVirtualDisplay(int width, int height, int densityDpi, Surface surface, int flags, Executor executor, VirtualDisplay.Callback callback) {
            VirtualDisplayConfig.Builder builder = new VirtualDisplayConfig.Builder(getVirtualDisplayName(), width, height, densityDpi).setFlags(flags);
            if (surface != null) {
                builder.setSurface(surface);
            }
            return createVirtualDisplay(builder.build(), executor, callback);
        }

        public VirtualDisplay createVirtualDisplay(VirtualDisplayConfig config, Executor executor, VirtualDisplay.Callback callback) {
            IVirtualDisplayCallback callbackWrapper = new DisplayManagerGlobal.VirtualDisplayCallback(callback, executor);
            try {
                int displayId = this.mService.createVirtualDisplay(config, callbackWrapper, this.mVirtualDevice, this.mContext.getPackageName());
                DisplayManagerGlobal displayManager = DisplayManagerGlobal.getInstance();
                return displayManager.createVirtualDisplayWrapper(config, this.mContext, callbackWrapper, displayId);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            try {
                this.mVirtualDevice.close();
                VirtualAudioDevice virtualAudioDevice = this.mVirtualAudioDevice;
                if (virtualAudioDevice != null) {
                    virtualAudioDevice.close();
                    this.mVirtualAudioDevice = null;
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public VirtualDpad createVirtualDpad(VirtualDpadConfig config) {
            try {
                IBinder token = new Binder("android.hardware.input.VirtualDpad:" + config.getInputDeviceName());
                this.mVirtualDevice.createVirtualDpad(config, token);
                return new VirtualDpad(this.mVirtualDevice, token);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public VirtualKeyboard createVirtualKeyboard(VirtualKeyboardConfig config) {
            try {
                IBinder token = new Binder("android.hardware.input.VirtualKeyboard:" + config.getInputDeviceName());
                this.mVirtualDevice.createVirtualKeyboard(config, token);
                return new VirtualKeyboard(this.mVirtualDevice, token);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        @Deprecated
        public VirtualKeyboard createVirtualKeyboard(VirtualDisplay display, String inputDeviceName, int vendorId, int productId) {
            VirtualKeyboardConfig keyboardConfig = new VirtualKeyboardConfig.Builder().setVendorId(vendorId).setProductId(productId).setInputDeviceName(inputDeviceName).setAssociatedDisplayId(display.getDisplay().getDisplayId()).build();
            return createVirtualKeyboard(keyboardConfig);
        }

        public VirtualMouse createVirtualMouse(VirtualMouseConfig config) {
            try {
                IBinder token = new Binder("android.hardware.input.VirtualMouse:" + config.getInputDeviceName());
                this.mVirtualDevice.createVirtualMouse(config, token);
                return new VirtualMouse(this.mVirtualDevice, token);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        @Deprecated
        public VirtualMouse createVirtualMouse(VirtualDisplay display, String inputDeviceName, int vendorId, int productId) {
            VirtualMouseConfig mouseConfig = new VirtualMouseConfig.Builder().setVendorId(vendorId).setProductId(productId).setInputDeviceName(inputDeviceName).setAssociatedDisplayId(display.getDisplay().getDisplayId()).build();
            return createVirtualMouse(mouseConfig);
        }

        public VirtualTouchscreen createVirtualTouchscreen(VirtualTouchscreenConfig config) {
            try {
                IBinder token = new Binder("android.hardware.input.VirtualTouchscreen:" + config.getInputDeviceName());
                this.mVirtualDevice.createVirtualTouchscreen(config, token);
                return new VirtualTouchscreen(this.mVirtualDevice, token);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public VirtualNavigationTouchpad createVirtualNavigationTouchpad(VirtualNavigationTouchpadConfig config) {
            try {
                IBinder token = new Binder("android.hardware.input.VirtualNavigationTouchpad:" + config.getInputDeviceName());
                this.mVirtualDevice.createVirtualNavigationTouchpad(config, token);
                return new VirtualNavigationTouchpad(this.mVirtualDevice, token);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        @Deprecated
        public VirtualTouchscreen createVirtualTouchscreen(VirtualDisplay display, String inputDeviceName, int vendorId, int productId) {
            Point size = new Point();
            display.getDisplay().getSize(size);
            VirtualTouchscreenConfig touchscreenConfig = new VirtualTouchscreenConfig.Builder(size.f76x, size.f77y).setVendorId(vendorId).setProductId(productId).setInputDeviceName(inputDeviceName).setAssociatedDisplayId(display.getDisplay().getDisplayId()).build();
            return createVirtualTouchscreen(touchscreenConfig);
        }

        public VirtualAudioDevice createVirtualAudioDevice(VirtualDisplay display, Executor executor, VirtualAudioDevice.AudioConfigurationChangeCallback callback) {
            if (this.mVirtualAudioDevice == null) {
                this.mVirtualAudioDevice = new VirtualAudioDevice(this.mContext, this.mVirtualDevice, display, executor, callback, new VirtualAudioDevice.CloseListener() { // from class: android.companion.virtual.VirtualDeviceManager$VirtualDevice$$ExternalSyntheticLambda0
                    @Override // android.companion.virtual.audio.VirtualAudioDevice.CloseListener
                    public final void onClosed() {
                        VirtualDeviceManager.VirtualDevice.this.lambda$createVirtualAudioDevice$0();
                    }
                });
            }
            return this.mVirtualAudioDevice;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$createVirtualAudioDevice$0() {
            this.mVirtualAudioDevice = null;
        }

        public VirtualCameraDevice createVirtualCameraDevice(String cameraName, CameraCharacteristics characteristics, VirtualCameraInput virtualCameraInput, Executor executor) {
            VirtualCameraDevice virtualCameraDevice = this.mVirtualCameraDevice;
            if (virtualCameraDevice != null) {
                virtualCameraDevice.close();
            }
            int deviceId = getDeviceId();
            VirtualCameraDevice virtualCameraDevice2 = new VirtualCameraDevice(deviceId, cameraName, characteristics, virtualCameraInput, executor);
            this.mVirtualCameraDevice = virtualCameraDevice2;
            return virtualCameraDevice2;
        }

        public void setShowPointerIcon(boolean showPointerIcon) {
            try {
                this.mVirtualDevice.setShowPointerIcon(showPointerIcon);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        private String getVirtualDisplayName() {
            try {
                return "VirtualDevice_" + this.mVirtualDevice.getDeviceId();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void addActivityListener(Executor executor, ActivityListener listener) {
            ActivityListenerDelegate delegate = new ActivityListenerDelegate((ActivityListener) Objects.requireNonNull(listener), (Executor) Objects.requireNonNull(executor));
            synchronized (this.mActivityListenersLock) {
                this.mActivityListeners.put(listener, delegate);
            }
        }

        public void removeActivityListener(ActivityListener listener) {
            synchronized (this.mActivityListenersLock) {
                this.mActivityListeners.remove(Objects.requireNonNull(listener));
            }
        }

        public void addSoundEffectListener(Executor executor, SoundEffectListener soundEffectListener) {
            SoundEffectListenerDelegate delegate = new SoundEffectListenerDelegate((Executor) Objects.requireNonNull(executor), (SoundEffectListener) Objects.requireNonNull(soundEffectListener));
            synchronized (this.mSoundEffectListenersLock) {
                this.mSoundEffectListeners.put(soundEffectListener, delegate);
            }
        }

        public void removeSoundEffectListener(SoundEffectListener soundEffectListener) {
            synchronized (this.mSoundEffectListenersLock) {
                this.mSoundEffectListeners.remove(Objects.requireNonNull(soundEffectListener));
            }
        }

        public void registerIntentInterceptor(IntentFilter interceptorFilter, Executor executor, IntentInterceptorCallback interceptorCallback) {
            Objects.requireNonNull(executor);
            Objects.requireNonNull(interceptorFilter);
            Objects.requireNonNull(interceptorCallback);
            VirtualIntentInterceptorDelegate delegate = new VirtualIntentInterceptorDelegate(executor, interceptorCallback);
            try {
                this.mVirtualDevice.registerIntentInterceptor(delegate, interceptorFilter);
                synchronized (this.mIntentInterceptorListenersLock) {
                    this.mIntentInterceptorListeners.put(interceptorCallback, delegate);
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void unregisterIntentInterceptor(IntentInterceptorCallback interceptorCallback) {
            VirtualIntentInterceptorDelegate delegate;
            Objects.requireNonNull(interceptorCallback);
            synchronized (this.mIntentInterceptorListenersLock) {
                delegate = this.mIntentInterceptorListeners.remove(interceptorCallback);
            }
            if (delegate != null) {
                try {
                    this.mVirtualDevice.unregisterIntentInterceptor(delegate);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface ActivityListener {
        void onDisplayEmpty(int i);

        void onTopActivityChanged(int i, ComponentName componentName);

        default void onTopActivityChanged(int displayId, ComponentName topActivity, int userId) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActivityListenerDelegate {
        private final ActivityListener mActivityListener;
        private final Executor mExecutor;

        ActivityListenerDelegate(ActivityListener listener, Executor executor) {
            this.mActivityListener = listener;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTopActivityChanged$0(int displayId, ComponentName topActivity) {
            this.mActivityListener.onTopActivityChanged(displayId, topActivity);
        }

        public void onTopActivityChanged(final int displayId, final ComponentName topActivity) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceManager$ActivityListenerDelegate$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VirtualDeviceManager.ActivityListenerDelegate.this.lambda$onTopActivityChanged$0(displayId, topActivity);
                }
            });
        }

        public void onTopActivityChanged(final int displayId, final ComponentName topActivity, final int userId) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceManager$ActivityListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VirtualDeviceManager.ActivityListenerDelegate.this.lambda$onTopActivityChanged$1(displayId, topActivity, userId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTopActivityChanged$1(int displayId, ComponentName topActivity, int userId) {
            this.mActivityListener.onTopActivityChanged(displayId, topActivity, userId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDisplayEmpty$2(int displayId) {
            this.mActivityListener.onDisplayEmpty(displayId);
        }

        public void onDisplayEmpty(final int displayId) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceManager$ActivityListenerDelegate$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VirtualDeviceManager.ActivityListenerDelegate.this.lambda$onDisplayEmpty$2(displayId);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class VirtualIntentInterceptorDelegate extends IVirtualDeviceIntentInterceptor.Stub {
        private final Executor mExecutor;
        private final IntentInterceptorCallback mIntentInterceptorCallback;

        private VirtualIntentInterceptorDelegate(Executor executor, IntentInterceptorCallback interceptorCallback) {
            this.mExecutor = executor;
            this.mIntentInterceptorCallback = interceptorCallback;
        }

        @Override // android.companion.virtual.IVirtualDeviceIntentInterceptor
        public void onIntentIntercepted(final Intent intent) {
            long token = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceManager$VirtualIntentInterceptorDelegate$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDeviceManager.VirtualIntentInterceptorDelegate.this.lambda$onIntentIntercepted$0(intent);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onIntentIntercepted$0(Intent intent) {
            this.mIntentInterceptorCallback.onIntentIntercepted(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SoundEffectListenerDelegate {
        private final Executor mExecutor;
        private final SoundEffectListener mSoundEffectListener;

        private SoundEffectListenerDelegate(Executor executor, SoundEffectListener soundEffectCallback) {
            this.mSoundEffectListener = soundEffectCallback;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPlaySoundEffect$0(int effectType) {
            this.mSoundEffectListener.onPlaySoundEffect(effectType);
        }

        public void onPlaySoundEffect(final int effectType) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceManager$SoundEffectListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VirtualDeviceManager.SoundEffectListenerDelegate.this.lambda$onPlaySoundEffect$0(effectType);
                }
            });
        }
    }
}
