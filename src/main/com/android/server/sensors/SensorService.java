package com.android.server.sensors;

import android.content.Context;
import android.util.ArrayMap;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ConcurrentUtils;
import com.android.server.LocalServices;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.SystemService;
import com.android.server.sensors.SensorManagerInternal;
import com.android.server.sensors.SensorService;
import com.android.server.utils.TimingsTraceAndSlog;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
/* loaded from: classes2.dex */
public class SensorService extends SystemService {
    public static final String START_NATIVE_SENSOR_SERVICE = "StartNativeSensorService";
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final ArrayMap<SensorManagerInternal.ProximityActiveListener, ProximityListenerProxy> mProximityListeners;
    @GuardedBy({"mLock"})
    public long mPtr;
    @GuardedBy({"mLock"})
    public final Set<Integer> mRuntimeSensorHandles;
    @GuardedBy({"mLock"})
    public Future<?> mSensorServiceStart;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void registerProximityActiveListenerNative(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int registerRuntimeSensorNative(long j, int i, int i2, String str, String str2, int i3, SensorManagerInternal.RuntimeSensorCallback runtimeSensorCallback);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean sendRuntimeSensorEventNative(long j, int i, int i2, long j2, float[] fArr);

    private static native long startSensorServiceNative(SensorManagerInternal.ProximityActiveListener proximityActiveListener);

    private static native void unregisterProximityActiveListenerNative(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void unregisterRuntimeSensorNative(long j, int i);

    public SensorService(Context context) {
        super(context);
        Object obj = new Object();
        this.mLock = obj;
        this.mProximityListeners = new ArrayMap<>();
        this.mRuntimeSensorHandles = new HashSet();
        synchronized (obj) {
            this.mSensorServiceStart = SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.sensors.SensorService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SensorService.this.lambda$new$0();
                }
            }, START_NATIVE_SENSOR_SERVICE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        TimingsTraceAndSlog newAsyncLog = TimingsTraceAndSlog.newAsyncLog();
        newAsyncLog.traceBegin(START_NATIVE_SENSOR_SERVICE);
        long startSensorServiceNative = startSensorServiceNative(new ProximityListenerDelegate());
        synchronized (this.mLock) {
            this.mPtr = startSensorServiceNative;
        }
        newAsyncLog.traceEnd();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        LocalServices.addService(SensorManagerInternal.class, new LocalService());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 200) {
            ConcurrentUtils.waitForFutureNoInterrupt(this.mSensorServiceStart, START_NATIVE_SENSOR_SERVICE);
            synchronized (this.mLock) {
                this.mSensorServiceStart = null;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class LocalService extends SensorManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.sensors.SensorManagerInternal
        public int createRuntimeSensor(int i, int i2, String str, String str2, int i3, SensorManagerInternal.RuntimeSensorCallback runtimeSensorCallback) {
            int registerRuntimeSensorNative;
            synchronized (SensorService.this.mLock) {
                registerRuntimeSensorNative = SensorService.registerRuntimeSensorNative(SensorService.this.mPtr, i, i2, str, str2, i3, runtimeSensorCallback);
                SensorService.this.mRuntimeSensorHandles.add(Integer.valueOf(registerRuntimeSensorNative));
            }
            return registerRuntimeSensorNative;
        }

        @Override // com.android.server.sensors.SensorManagerInternal
        public void removeRuntimeSensor(int i) {
            synchronized (SensorService.this.mLock) {
                if (SensorService.this.mRuntimeSensorHandles.contains(Integer.valueOf(i))) {
                    SensorService.this.mRuntimeSensorHandles.remove(Integer.valueOf(i));
                    SensorService.unregisterRuntimeSensorNative(SensorService.this.mPtr, i);
                }
            }
        }

        @Override // com.android.server.sensors.SensorManagerInternal
        public boolean sendSensorEvent(int i, int i2, long j, float[] fArr) {
            synchronized (SensorService.this.mLock) {
                if (SensorService.this.mRuntimeSensorHandles.contains(Integer.valueOf(i))) {
                    return SensorService.sendRuntimeSensorEventNative(SensorService.this.mPtr, i, i2, j, fArr);
                }
                return false;
            }
        }

        @Override // com.android.server.sensors.SensorManagerInternal
        public void addProximityActiveListener(Executor executor, SensorManagerInternal.ProximityActiveListener proximityActiveListener) {
            Objects.requireNonNull(executor, "executor must not be null");
            Objects.requireNonNull(proximityActiveListener, "listener must not be null");
            ProximityListenerProxy proximityListenerProxy = new ProximityListenerProxy(executor, proximityActiveListener);
            synchronized (SensorService.this.mLock) {
                if (SensorService.this.mProximityListeners.containsKey(proximityActiveListener)) {
                    throw new IllegalArgumentException("listener already registered");
                }
                SensorService.this.mProximityListeners.put(proximityActiveListener, proximityListenerProxy);
                if (SensorService.this.mProximityListeners.size() == 1) {
                    SensorService.registerProximityActiveListenerNative(SensorService.this.mPtr);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class ProximityListenerProxy implements SensorManagerInternal.ProximityActiveListener {
        public final Executor mExecutor;
        public final SensorManagerInternal.ProximityActiveListener mListener;

        public ProximityListenerProxy(Executor executor, SensorManagerInternal.ProximityActiveListener proximityActiveListener) {
            this.mExecutor = executor;
            this.mListener = proximityActiveListener;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProximityActive$0(boolean z) {
            this.mListener.onProximityActive(z);
        }

        @Override // com.android.server.sensors.SensorManagerInternal.ProximityActiveListener
        public void onProximityActive(final boolean z) {
            this.mExecutor.execute(new Runnable() { // from class: com.android.server.sensors.SensorService$ProximityListenerProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SensorService.ProximityListenerProxy.this.lambda$onProximityActive$0(z);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public class ProximityListenerDelegate implements SensorManagerInternal.ProximityActiveListener {
        public ProximityListenerDelegate() {
        }

        @Override // com.android.server.sensors.SensorManagerInternal.ProximityActiveListener
        public void onProximityActive(boolean z) {
            int i;
            ProximityListenerProxy[] proximityListenerProxyArr;
            synchronized (SensorService.this.mLock) {
                proximityListenerProxyArr = (ProximityListenerProxy[]) SensorService.this.mProximityListeners.values().toArray(new ProximityListenerProxy[0]);
            }
            for (ProximityListenerProxy proximityListenerProxy : proximityListenerProxyArr) {
                proximityListenerProxy.onProximityActive(z);
            }
        }
    }
}
