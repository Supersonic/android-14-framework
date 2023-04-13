package android.media;

import android.annotation.SystemApi;
import android.content.Context;
import android.media.AudioDeviceVolumeManager;
import android.media.CallbackUtil;
import android.media.IAudioDeviceVolumeDispatcher;
import android.media.IAudioService;
import android.media.IDeviceVolumeBehaviorDispatcher;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
@SystemApi
/* loaded from: classes2.dex */
public class AudioDeviceVolumeManager {
    public static final int ADJUST_MODE_END = 2;
    public static final int ADJUST_MODE_NORMAL = 0;
    public static final int ADJUST_MODE_START = 1;
    private static final String TAG = "AudioDeviceVolumeManager";
    private static IAudioService sService;
    private DeviceVolumeDispatcherStub mDeviceVolumeDispatcherStub;
    private ArrayList<ListenerInfo> mDeviceVolumeListeners;
    private final String mPackageName;
    private final Object mDeviceVolumeListenerLock = new Object();
    private final CallbackUtil.LazyListenerManager<OnDeviceVolumeBehaviorChangedListener> mDeviceVolumeBehaviorChangedListenerMgr = new CallbackUtil.LazyListenerManager<>();

    /* loaded from: classes2.dex */
    public interface OnAudioDeviceVolumeChangedListener {
        void onAudioDeviceVolumeAdjusted(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo, int i, int i2);

        void onAudioDeviceVolumeChanged(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo);
    }

    /* loaded from: classes2.dex */
    public interface OnDeviceVolumeBehaviorChangedListener {
        void onDeviceVolumeBehaviorChanged(AudioDeviceAttributes audioDeviceAttributes, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface VolumeAdjustmentMode {
    }

    public AudioDeviceVolumeManager(Context context) {
        Objects.requireNonNull(context);
        this.mPackageName = context.getApplicationContext().getOpPackageName();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class ListenerInfo {
        final AudioDeviceAttributes mDevice;
        final Executor mExecutor;
        final boolean mHandlesVolumeAdjustment;
        final OnAudioDeviceVolumeChangedListener mListener;

        ListenerInfo(OnAudioDeviceVolumeChangedListener listener, Executor exe, AudioDeviceAttributes device, boolean handlesVolumeAdjustment) {
            this.mListener = listener;
            this.mExecutor = exe;
            this.mDevice = device;
            this.mHandlesVolumeAdjustment = handlesVolumeAdjustment;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public final class DeviceVolumeDispatcherStub extends IAudioDeviceVolumeDispatcher.Stub {
        DeviceVolumeDispatcherStub() {
        }

        public void register(boolean register, AudioDeviceAttributes device, List<VolumeInfo> volumes, boolean handlesVolumeAdjustment, int behavior) {
            try {
                AudioDeviceVolumeManager.getService().registerDeviceVolumeDispatcherForAbsoluteVolume(register, this, AudioDeviceVolumeManager.this.mPackageName, (AudioDeviceAttributes) Objects.requireNonNull(device), (List) Objects.requireNonNull(volumes), handlesVolumeAdjustment, behavior);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        @Override // android.media.IAudioDeviceVolumeDispatcher
        public void dispatchDeviceVolumeChanged(final AudioDeviceAttributes device, final VolumeInfo vol) {
            ArrayList<ListenerInfo> volumeListeners;
            synchronized (AudioDeviceVolumeManager.this.mDeviceVolumeListenerLock) {
                volumeListeners = (ArrayList) AudioDeviceVolumeManager.this.mDeviceVolumeListeners.clone();
            }
            Iterator<ListenerInfo> it = volumeListeners.iterator();
            while (it.hasNext()) {
                final ListenerInfo listenerInfo = it.next();
                if (listenerInfo.mDevice.equalTypeAddress(device)) {
                    listenerInfo.mExecutor.execute(new Runnable() { // from class: android.media.AudioDeviceVolumeManager$DeviceVolumeDispatcherStub$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            AudioDeviceVolumeManager.ListenerInfo.this.mListener.onAudioDeviceVolumeChanged(device, vol);
                        }
                    });
                }
            }
        }

        @Override // android.media.IAudioDeviceVolumeDispatcher
        public void dispatchDeviceVolumeAdjusted(final AudioDeviceAttributes device, final VolumeInfo vol, final int direction, final int mode) {
            ArrayList<ListenerInfo> volumeListeners;
            synchronized (AudioDeviceVolumeManager.this.mDeviceVolumeListenerLock) {
                volumeListeners = (ArrayList) AudioDeviceVolumeManager.this.mDeviceVolumeListeners.clone();
            }
            Iterator<ListenerInfo> it = volumeListeners.iterator();
            while (it.hasNext()) {
                final ListenerInfo listenerInfo = it.next();
                if (listenerInfo.mDevice.equalTypeAddress(device)) {
                    listenerInfo.mExecutor.execute(new Runnable() { // from class: android.media.AudioDeviceVolumeManager$DeviceVolumeDispatcherStub$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            AudioDeviceVolumeManager.ListenerInfo.this.mListener.onAudioDeviceVolumeAdjusted(device, vol, direction, mode);
                        }
                    });
                }
            }
        }
    }

    public void setDeviceAbsoluteVolumeBehavior(AudioDeviceAttributes device, VolumeInfo volume, Executor executor, OnAudioDeviceVolumeChangedListener vclistener, boolean handlesVolumeAdjustment) {
        ArrayList<VolumeInfo> volumes = new ArrayList<>(1);
        volumes.add(volume);
        setDeviceAbsoluteMultiVolumeBehavior(device, volumes, executor, vclistener, handlesVolumeAdjustment);
    }

    public void setDeviceAbsoluteMultiVolumeBehavior(AudioDeviceAttributes device, List<VolumeInfo> volumes, Executor executor, OnAudioDeviceVolumeChangedListener vclistener, boolean handlesVolumeAdjustment) {
        baseSetDeviceAbsoluteMultiVolumeBehavior(device, volumes, executor, vclistener, handlesVolumeAdjustment, 3);
    }

    public void setDeviceAbsoluteVolumeAdjustOnlyBehavior(AudioDeviceAttributes device, VolumeInfo volume, Executor executor, OnAudioDeviceVolumeChangedListener vclistener, boolean handlesVolumeAdjustment) {
        ArrayList<VolumeInfo> volumes = new ArrayList<>(1);
        volumes.add(volume);
        setDeviceAbsoluteMultiVolumeAdjustOnlyBehavior(device, volumes, executor, vclistener, handlesVolumeAdjustment);
    }

    public void setDeviceAbsoluteMultiVolumeAdjustOnlyBehavior(AudioDeviceAttributes device, List<VolumeInfo> volumes, Executor executor, OnAudioDeviceVolumeChangedListener vclistener, boolean handlesVolumeAdjustment) {
        baseSetDeviceAbsoluteMultiVolumeBehavior(device, volumes, executor, vclistener, handlesVolumeAdjustment, 5);
    }

    private void baseSetDeviceAbsoluteMultiVolumeBehavior(final AudioDeviceAttributes device, List<VolumeInfo> volumes, Executor executor, OnAudioDeviceVolumeChangedListener vclistener, boolean handlesVolumeAdjustment, int behavior) {
        Objects.requireNonNull(device);
        Objects.requireNonNull(volumes);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(vclistener);
        ListenerInfo listenerInfo = new ListenerInfo(vclistener, executor, device, handlesVolumeAdjustment);
        synchronized (this.mDeviceVolumeListenerLock) {
            if (this.mDeviceVolumeListeners == null) {
                this.mDeviceVolumeListeners = new ArrayList<>();
            }
            if (this.mDeviceVolumeListeners.size() == 0) {
                if (this.mDeviceVolumeDispatcherStub == null) {
                    this.mDeviceVolumeDispatcherStub = new DeviceVolumeDispatcherStub();
                }
            } else {
                this.mDeviceVolumeListeners.removeIf(new Predicate() { // from class: android.media.AudioDeviceVolumeManager$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean equalTypeAddress;
                        equalTypeAddress = ((AudioDeviceVolumeManager.ListenerInfo) obj).mDevice.equalTypeAddress(AudioDeviceAttributes.this);
                        return equalTypeAddress;
                    }
                });
            }
            this.mDeviceVolumeListeners.add(listenerInfo);
            this.mDeviceVolumeDispatcherStub.register(true, device, volumes, handlesVolumeAdjustment, behavior);
        }
    }

    public void addOnDeviceVolumeBehaviorChangedListener(Executor executor, OnDeviceVolumeBehaviorChangedListener listener) throws SecurityException {
        this.mDeviceVolumeBehaviorChangedListenerMgr.addListener(executor, listener, "addOnDeviceVolumeBehaviorChangedListener", new Supplier() { // from class: android.media.AudioDeviceVolumeManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                CallbackUtil.DispatcherStub lambda$addOnDeviceVolumeBehaviorChangedListener$1;
                lambda$addOnDeviceVolumeBehaviorChangedListener$1 = AudioDeviceVolumeManager.this.lambda$addOnDeviceVolumeBehaviorChangedListener$1();
                return lambda$addOnDeviceVolumeBehaviorChangedListener$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CallbackUtil.DispatcherStub lambda$addOnDeviceVolumeBehaviorChangedListener$1() {
        return new DeviceVolumeBehaviorDispatcherStub();
    }

    public void removeOnDeviceVolumeBehaviorChangedListener(OnDeviceVolumeBehaviorChangedListener listener) {
        this.mDeviceVolumeBehaviorChangedListenerMgr.removeListener(listener, "removeOnDeviceVolumeBehaviorChangedListener");
    }

    @SystemApi
    public void setDeviceVolume(VolumeInfo vi, AudioDeviceAttributes ada) {
        try {
            getService().setDeviceVolume(vi, ada, this.mPackageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public VolumeInfo getDeviceVolume(VolumeInfo vi, AudioDeviceAttributes ada) {
        try {
            return getService().getDeviceVolume(vi, ada, this.mPackageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return VolumeInfo.getDefaultVolumeInfo();
        }
    }

    public static String volumeBehaviorName(int behavior) {
        switch (behavior) {
            case 0:
                return "DEVICE_VOLUME_BEHAVIOR_VARIABLE";
            case 1:
                return "DEVICE_VOLUME_BEHAVIOR_FULL";
            case 2:
                return "DEVICE_VOLUME_BEHAVIOR_FIXED";
            case 3:
                return "DEVICE_VOLUME_BEHAVIOR_ABSOLUTE";
            case 4:
                return "DEVICE_VOLUME_BEHAVIOR_ABSOLUTE_MULTI_MODE";
            case 5:
                return "DEVICE_VOLUME_BEHAVIOR_ABSOLUTE_ADJUST_ONLY";
            default:
                return "invalid volume behavior " + behavior;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class DeviceVolumeBehaviorDispatcherStub extends IDeviceVolumeBehaviorDispatcher.Stub implements CallbackUtil.DispatcherStub {
        private DeviceVolumeBehaviorDispatcherStub() {
        }

        @Override // android.media.CallbackUtil.DispatcherStub
        public void register(boolean register) {
            try {
                AudioDeviceVolumeManager.getService().registerDeviceVolumeBehaviorDispatcher(register, this);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        @Override // android.media.IDeviceVolumeBehaviorDispatcher
        public void dispatchDeviceVolumeBehaviorChanged(final AudioDeviceAttributes device, final int volumeBehavior) {
            AudioDeviceVolumeManager.this.mDeviceVolumeBehaviorChangedListenerMgr.callListeners(new CallbackUtil.CallbackMethod() { // from class: android.media.AudioDeviceVolumeManager$DeviceVolumeBehaviorDispatcherStub$$ExternalSyntheticLambda0
                @Override // android.media.CallbackUtil.CallbackMethod
                public final void callbackMethod(Object obj) {
                    ((AudioDeviceVolumeManager.OnDeviceVolumeBehaviorChangedListener) obj).onDeviceVolumeBehaviorChanged(AudioDeviceAttributes.this, volumeBehavior);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static IAudioService getService() {
        IAudioService iAudioService = sService;
        if (iAudioService != null) {
            return iAudioService;
        }
        IBinder b = ServiceManager.getService("audio");
        IAudioService asInterface = IAudioService.Stub.asInterface(b);
        sService = asInterface;
        return asInterface;
    }
}
