package com.android.server.vibrator;

import android.os.Binder;
import android.os.IVibratorStateListener;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.VibratorInfo;
import android.os.vibrator.PrebakedSegment;
import android.os.vibrator.PrimitiveSegment;
import android.os.vibrator.RampSegment;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.util.function.Consumer;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes2.dex */
public final class VibratorController {
    public volatile float mCurrentAmplitude;
    public volatile boolean mIsUnderExternalControl;
    public volatile boolean mIsVibrating;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final NativeWrapper mNativeWrapper;
    public volatile VibratorInfo mVibratorInfo;
    public volatile boolean mVibratorInfoLoadSuccessful;
    public final RemoteCallbackList<IVibratorStateListener> mVibratorStateListeners;

    /* loaded from: classes2.dex */
    public interface OnVibrationCompleteListener {
        void onComplete(int i, long j);
    }

    public VibratorController(int i, OnVibrationCompleteListener onVibrationCompleteListener) {
        this(i, onVibrationCompleteListener, new NativeWrapper());
    }

    @VisibleForTesting
    public VibratorController(int i, OnVibrationCompleteListener onVibrationCompleteListener, NativeWrapper nativeWrapper) {
        this.mLock = new Object();
        this.mVibratorStateListeners = new RemoteCallbackList<>();
        this.mNativeWrapper = nativeWrapper;
        nativeWrapper.init(i, onVibrationCompleteListener);
        VibratorInfo.Builder builder = new VibratorInfo.Builder(i);
        this.mVibratorInfoLoadSuccessful = nativeWrapper.getInfo(builder);
        this.mVibratorInfo = builder.build();
        if (this.mVibratorInfoLoadSuccessful) {
            return;
        }
        Slog.e("VibratorController", "Vibrator controller initialization failed to load some HAL info for vibrator " + i);
    }

    public boolean registerVibratorStateListener(IVibratorStateListener iVibratorStateListener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                if (this.mVibratorStateListeners.register(iVibratorStateListener)) {
                    lambda$notifyListenerOnVibrating$0(iVibratorStateListener, this.mIsVibrating);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean unregisterVibratorStateListener(IVibratorStateListener iVibratorStateListener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mVibratorStateListeners.unregister(iVibratorStateListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void reloadVibratorInfoIfNeeded() {
        if (this.mVibratorInfoLoadSuccessful) {
            return;
        }
        synchronized (this.mLock) {
            if (this.mVibratorInfoLoadSuccessful) {
                return;
            }
            int id = this.mVibratorInfo.getId();
            VibratorInfo.Builder builder = new VibratorInfo.Builder(id);
            this.mVibratorInfoLoadSuccessful = this.mNativeWrapper.getInfo(builder);
            this.mVibratorInfo = builder.build();
            if (!this.mVibratorInfoLoadSuccessful) {
                Slog.e("VibratorController", "Failed retry of HAL getInfo for vibrator " + id);
            }
        }
    }

    public boolean isVibratorInfoLoadSuccessful() {
        return this.mVibratorInfoLoadSuccessful;
    }

    public VibratorInfo getVibratorInfo() {
        return this.mVibratorInfo;
    }

    public boolean isVibrating() {
        return this.mIsVibrating;
    }

    public float getCurrentAmplitude() {
        return this.mCurrentAmplitude;
    }

    public boolean isUnderExternalControl() {
        return this.mIsUnderExternalControl;
    }

    public boolean hasCapability(long j) {
        return this.mVibratorInfo.hasCapability(j);
    }

    public void setExternalControl(boolean z) {
        if (this.mVibratorInfo.hasCapability(8L)) {
            synchronized (this.mLock) {
                this.mIsUnderExternalControl = z;
                this.mNativeWrapper.setExternalControl(z);
            }
        }
    }

    public void updateAlwaysOn(int i, PrebakedSegment prebakedSegment) {
        if (this.mVibratorInfo.hasCapability(64L)) {
            synchronized (this.mLock) {
                if (prebakedSegment == null) {
                    this.mNativeWrapper.alwaysOnDisable(i);
                } else {
                    this.mNativeWrapper.alwaysOnEnable(i, prebakedSegment.getEffectId(), prebakedSegment.getEffectStrength());
                }
            }
        }
    }

    public void setAmplitude(float f) {
        synchronized (this.mLock) {
            if (this.mVibratorInfo.hasCapability(4L)) {
                this.mNativeWrapper.setAmplitude(f);
            }
            if (this.mIsVibrating) {
                this.mCurrentAmplitude = f;
            }
        }
    }

    /* renamed from: on */
    public long m9on(long j, long j2) {
        long m5on;
        synchronized (this.mLock) {
            m5on = this.mNativeWrapper.m5on(j, j2);
            if (m5on > 0) {
                this.mCurrentAmplitude = -1.0f;
                notifyListenerOnVibrating(true);
            }
        }
        return m5on;
    }

    /* renamed from: on */
    public long m8on(PrebakedSegment prebakedSegment, long j) {
        long perform;
        synchronized (this.mLock) {
            perform = this.mNativeWrapper.perform(prebakedSegment.getEffectId(), prebakedSegment.getEffectStrength(), j);
            if (perform > 0) {
                this.mCurrentAmplitude = -1.0f;
                notifyListenerOnVibrating(true);
            }
        }
        return perform;
    }

    /* renamed from: on */
    public long m7on(PrimitiveSegment[] primitiveSegmentArr, long j) {
        long compose;
        if (this.mVibratorInfo.hasCapability(32L)) {
            synchronized (this.mLock) {
                compose = this.mNativeWrapper.compose(primitiveSegmentArr, j);
                if (compose > 0) {
                    this.mCurrentAmplitude = -1.0f;
                    notifyListenerOnVibrating(true);
                }
            }
            return compose;
        }
        return 0L;
    }

    /* renamed from: on */
    public long m6on(RampSegment[] rampSegmentArr, long j) {
        long composePwle;
        if (this.mVibratorInfo.hasCapability(1024L)) {
            synchronized (this.mLock) {
                composePwle = this.mNativeWrapper.composePwle(rampSegmentArr, this.mVibratorInfo.getDefaultBraking(), j);
                if (composePwle > 0) {
                    this.mCurrentAmplitude = -1.0f;
                    notifyListenerOnVibrating(true);
                }
            }
            return composePwle;
        }
        return 0L;
    }

    public void off() {
        synchronized (this.mLock) {
            this.mNativeWrapper.off();
            this.mCurrentAmplitude = 0.0f;
            notifyListenerOnVibrating(false);
        }
    }

    public void reset() {
        setExternalControl(false);
        off();
    }

    public String toString() {
        return "VibratorController{mVibratorInfo=" + this.mVibratorInfo + ", mVibratorInfoLoadSuccessful=" + this.mVibratorInfoLoadSuccessful + ", mIsVibrating=" + this.mIsVibrating + ", mCurrentAmplitude=" + this.mCurrentAmplitude + ", mIsUnderExternalControl=" + this.mIsUnderExternalControl + ", mVibratorStateListeners count=" + this.mVibratorStateListeners.getRegisteredCallbackCount() + '}';
    }

    @GuardedBy({"mLock"})
    public final void notifyListenerOnVibrating(final boolean z) {
        if (this.mIsVibrating != z) {
            this.mIsVibrating = z;
            this.mVibratorStateListeners.broadcast(new Consumer() { // from class: com.android.server.vibrator.VibratorController$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VibratorController.this.lambda$notifyListenerOnVibrating$0(z, (IVibratorStateListener) obj);
                }
            });
        }
    }

    /* renamed from: notifyStateListener */
    public final void lambda$notifyListenerOnVibrating$0(IVibratorStateListener iVibratorStateListener, boolean z) {
        try {
            iVibratorStateListener.onVibrating(z);
        } catch (RemoteException | RuntimeException e) {
            Slog.e("VibratorController", "Vibrator state listener failed to call", e);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class NativeWrapper {
        public long mNativePtr = 0;

        private static native void alwaysOnDisable(long j, long j2);

        private static native void alwaysOnEnable(long j, long j2, long j3, long j4);

        private static native boolean getInfo(long j, VibratorInfo.Builder builder);

        private static native long getNativeFinalizer();

        private static native boolean isAvailable(long j);

        private static native long nativeInit(int i, OnVibrationCompleteListener onVibrationCompleteListener);

        private static native void off(long j);

        /* renamed from: on */
        private static native long m4on(long j, long j2, long j3);

        private static native long performComposedEffect(long j, PrimitiveSegment[] primitiveSegmentArr, long j2);

        private static native long performEffect(long j, long j2, long j3, long j4);

        private static native long performPwleEffect(long j, RampSegment[] rampSegmentArr, int i, long j2);

        private static native void setAmplitude(long j, float f);

        private static native void setExternalControl(long j, boolean z);

        public void init(int i, OnVibrationCompleteListener onVibrationCompleteListener) {
            this.mNativePtr = nativeInit(i, onVibrationCompleteListener);
            long nativeFinalizer = getNativeFinalizer();
            if (nativeFinalizer != 0) {
                NativeAllocationRegistry.createMalloced(VibratorController.class.getClassLoader(), nativeFinalizer).registerNativeAllocation(this, this.mNativePtr);
            }
        }

        public boolean isAvailable() {
            return isAvailable(this.mNativePtr);
        }

        /* renamed from: on */
        public long m5on(long j, long j2) {
            return m4on(this.mNativePtr, j, j2);
        }

        public void off() {
            off(this.mNativePtr);
        }

        public void setAmplitude(float f) {
            setAmplitude(this.mNativePtr, f);
        }

        public long perform(long j, long j2, long j3) {
            return performEffect(this.mNativePtr, j, j2, j3);
        }

        public long compose(PrimitiveSegment[] primitiveSegmentArr, long j) {
            return performComposedEffect(this.mNativePtr, primitiveSegmentArr, j);
        }

        public long composePwle(RampSegment[] rampSegmentArr, int i, long j) {
            return performPwleEffect(this.mNativePtr, rampSegmentArr, i, j);
        }

        public void setExternalControl(boolean z) {
            setExternalControl(this.mNativePtr, z);
        }

        public void alwaysOnEnable(long j, long j2, long j3) {
            alwaysOnEnable(this.mNativePtr, j, j2, j3);
        }

        public void alwaysOnDisable(long j) {
            alwaysOnDisable(this.mNativePtr, j);
        }

        public boolean getInfo(VibratorInfo.Builder builder) {
            return getInfo(this.mNativePtr, builder);
        }
    }
}
