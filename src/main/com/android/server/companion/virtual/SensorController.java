package com.android.server.companion.virtual;

import android.companion.virtual.sensor.IVirtualSensorCallback;
import android.companion.virtual.sensor.VirtualSensor;
import android.companion.virtual.sensor.VirtualSensorConfig;
import android.companion.virtual.sensor.VirtualSensorEvent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.sensors.SensorManagerInternal;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class SensorController {
    public static AtomicInteger sNextDirectChannelHandle = new AtomicInteger(1);
    public final Object mLock;
    public final SensorManagerInternal.RuntimeSensorCallback mRuntimeSensorCallback;
    @GuardedBy({"mLock"})
    public final Map<IBinder, SensorDescriptor> mSensorDescriptors = new ArrayMap();
    public final SensorManagerInternal mSensorManagerInternal = (SensorManagerInternal) LocalServices.getService(SensorManagerInternal.class);
    public final VirtualDeviceManagerInternal mVdmInternal = (VirtualDeviceManagerInternal) LocalServices.getService(VirtualDeviceManagerInternal.class);
    public final int mVirtualDeviceId;

    public static /* synthetic */ void lambda$addSensorForTesting$0() {
    }

    public SensorController(Object obj, int i, IVirtualSensorCallback iVirtualSensorCallback) {
        this.mLock = obj;
        this.mVirtualDeviceId = i;
        this.mRuntimeSensorCallback = new RuntimeSensorCallbackWrapper(iVirtualSensorCallback);
    }

    public void close() {
        synchronized (this.mLock) {
            Iterator<Map.Entry<IBinder, SensorDescriptor>> it = this.mSensorDescriptors.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry<IBinder, SensorDescriptor> next = it.next();
                it.remove();
                closeSensorDescriptorLocked(next.getKey(), next.getValue());
            }
        }
    }

    public int createSensor(IBinder iBinder, VirtualSensorConfig virtualSensorConfig) {
        Objects.requireNonNull(iBinder);
        Objects.requireNonNull(virtualSensorConfig);
        try {
            return createSensorInternal(iBinder, virtualSensorConfig);
        } catch (SensorCreationException e) {
            throw new RuntimeException("Failed to create virtual sensor '" + virtualSensorConfig.getName() + "'.", e);
        }
    }

    public final int createSensorInternal(IBinder iBinder, VirtualSensorConfig virtualSensorConfig) throws SensorCreationException {
        if (virtualSensorConfig.getType() <= 0) {
            throw new SensorCreationException("Received an invalid virtual sensor type.");
        }
        int createRuntimeSensor = this.mSensorManagerInternal.createRuntimeSensor(this.mVirtualDeviceId, virtualSensorConfig.getType(), virtualSensorConfig.getName(), virtualSensorConfig.getVendor() == null ? "" : virtualSensorConfig.getVendor(), virtualSensorConfig.getFlags(), this.mRuntimeSensorCallback);
        if (createRuntimeSensor <= 0) {
            throw new SensorCreationException("Received an invalid virtual sensor handle.");
        }
        try {
            BinderDeathRecipient binderDeathRecipient = new BinderDeathRecipient(iBinder);
            iBinder.linkToDeath(binderDeathRecipient, 0);
            synchronized (this.mLock) {
                this.mSensorDescriptors.put(iBinder, new SensorDescriptor(createRuntimeSensor, virtualSensorConfig.getType(), virtualSensorConfig.getName(), binderDeathRecipient));
            }
            return createRuntimeSensor;
        } catch (RemoteException e) {
            this.mSensorManagerInternal.removeRuntimeSensor(createRuntimeSensor);
            throw new SensorCreationException("Client died before sensor could be created.", e);
        }
    }

    public boolean sendSensorEvent(IBinder iBinder, VirtualSensorEvent virtualSensorEvent) {
        boolean sendSensorEvent;
        Objects.requireNonNull(iBinder);
        Objects.requireNonNull(virtualSensorEvent);
        synchronized (this.mLock) {
            SensorDescriptor sensorDescriptor = this.mSensorDescriptors.get(iBinder);
            if (sensorDescriptor == null) {
                throw new IllegalArgumentException("Could not send sensor event for given token");
            }
            sendSensorEvent = this.mSensorManagerInternal.sendSensorEvent(sensorDescriptor.getHandle(), sensorDescriptor.getType(), virtualSensorEvent.getTimestampNanos(), virtualSensorEvent.getValues());
        }
        return sendSensorEvent;
    }

    public void unregisterSensor(IBinder iBinder) {
        Objects.requireNonNull(iBinder);
        synchronized (this.mLock) {
            SensorDescriptor remove = this.mSensorDescriptors.remove(iBinder);
            if (remove == null) {
                throw new IllegalArgumentException("Could not unregister sensor for given token");
            }
            closeSensorDescriptorLocked(iBinder, remove);
        }
    }

    @GuardedBy({"mLock"})
    public final void closeSensorDescriptorLocked(IBinder iBinder, SensorDescriptor sensorDescriptor) {
        iBinder.unlinkToDeath(sensorDescriptor.getDeathRecipient(), 0);
        this.mSensorManagerInternal.removeRuntimeSensor(sensorDescriptor.getHandle());
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("    SensorController: ");
        synchronized (this.mLock) {
            printWriter.println("      Active descriptors: ");
            for (SensorDescriptor sensorDescriptor : this.mSensorDescriptors.values()) {
                printWriter.println("        handle: " + sensorDescriptor.getHandle());
                printWriter.println("          type: " + sensorDescriptor.getType());
                printWriter.println("          name: " + sensorDescriptor.getName());
            }
        }
    }

    @VisibleForTesting
    public void addSensorForTesting(IBinder iBinder, int i, int i2, String str) {
        synchronized (this.mLock) {
            this.mSensorDescriptors.put(iBinder, new SensorDescriptor(i, i2, str, new IBinder.DeathRecipient() { // from class: com.android.server.companion.virtual.SensorController$$ExternalSyntheticLambda0
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    SensorController.lambda$addSensorForTesting$0();
                }
            }));
        }
    }

    @VisibleForTesting
    public Map<IBinder, SensorDescriptor> getSensorDescriptors() {
        Map<IBinder, SensorDescriptor> map;
        synchronized (this.mLock) {
            map = this.mSensorDescriptors;
        }
        return map;
    }

    /* loaded from: classes.dex */
    public final class RuntimeSensorCallbackWrapper implements SensorManagerInternal.RuntimeSensorCallback {
        public IVirtualSensorCallback mCallback;

        public RuntimeSensorCallbackWrapper(IVirtualSensorCallback iVirtualSensorCallback) {
            this.mCallback = iVirtualSensorCallback;
        }

        @Override // com.android.server.sensors.SensorManagerInternal.RuntimeSensorCallback
        public int onConfigurationChanged(int i, boolean z, int i2, int i3) {
            if (this.mCallback == null) {
                Slog.e("SensorController", "No sensor callback configured for sensor handle " + i);
                return -22;
            }
            VirtualSensor virtualSensor = SensorController.this.mVdmInternal.getVirtualSensor(SensorController.this.mVirtualDeviceId, i);
            if (virtualSensor == null) {
                Slog.e("SensorController", "No sensor found for deviceId=" + SensorController.this.mVirtualDeviceId + " and sensor handle=" + i);
                return -22;
            }
            try {
                this.mCallback.onConfigurationChanged(virtualSensor, z, i2, i3);
                return 0;
            } catch (RemoteException e) {
                Slog.e("SensorController", "Failed to call sensor callback: " + e);
                return Integer.MIN_VALUE;
            }
        }

        @Override // com.android.server.sensors.SensorManagerInternal.RuntimeSensorCallback
        public int onDirectChannelCreated(ParcelFileDescriptor parcelFileDescriptor) {
            if (this.mCallback == null) {
                Slog.e("SensorController", "No sensor callback for virtual deviceId " + SensorController.this.mVirtualDeviceId);
                return -22;
            } else if (parcelFileDescriptor == null) {
                Slog.e("SensorController", "Received invalid ParcelFileDescriptor");
                return -22;
            } else {
                int andIncrement = SensorController.sNextDirectChannelHandle.getAndIncrement();
                try {
                    this.mCallback.onDirectChannelCreated(andIncrement, SharedMemory.fromFileDescriptor(parcelFileDescriptor));
                    return andIncrement;
                } catch (RemoteException e) {
                    Slog.e("SensorController", "Failed to call sensor callback: " + e);
                    return Integer.MIN_VALUE;
                }
            }
        }

        @Override // com.android.server.sensors.SensorManagerInternal.RuntimeSensorCallback
        public void onDirectChannelDestroyed(int i) {
            IVirtualSensorCallback iVirtualSensorCallback = this.mCallback;
            if (iVirtualSensorCallback == null) {
                Slog.e("SensorController", "No sensor callback for virtual deviceId " + SensorController.this.mVirtualDeviceId);
                return;
            }
            try {
                iVirtualSensorCallback.onDirectChannelDestroyed(i);
            } catch (RemoteException e) {
                Slog.e("SensorController", "Failed to call sensor callback: " + e);
            }
        }

        @Override // com.android.server.sensors.SensorManagerInternal.RuntimeSensorCallback
        public int onDirectChannelConfigured(int i, int i2, int i3) {
            if (this.mCallback == null) {
                Slog.e("SensorController", "No runtime sensor callback configured.");
                return -22;
            }
            VirtualSensor virtualSensor = SensorController.this.mVdmInternal.getVirtualSensor(SensorController.this.mVirtualDeviceId, i2);
            if (virtualSensor == null) {
                Slog.e("SensorController", "No sensor found for deviceId=" + SensorController.this.mVirtualDeviceId + " and sensor handle=" + i2);
                return -22;
            }
            try {
                this.mCallback.onDirectChannelConfigured(i, virtualSensor, i3, i2);
                if (i3 == 0) {
                    return 0;
                }
                return i2;
            } catch (RemoteException e) {
                Slog.e("SensorController", "Failed to call sensor callback: " + e);
                return Integer.MIN_VALUE;
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class SensorDescriptor {
        public final IBinder.DeathRecipient mDeathRecipient;
        public final int mHandle;
        public final String mName;
        public final int mType;

        public SensorDescriptor(int i, int i2, String str, IBinder.DeathRecipient deathRecipient) {
            this.mHandle = i;
            this.mDeathRecipient = deathRecipient;
            this.mType = i2;
            this.mName = str;
        }

        public int getHandle() {
            return this.mHandle;
        }

        public int getType() {
            return this.mType;
        }

        public String getName() {
            return this.mName;
        }

        public IBinder.DeathRecipient getDeathRecipient() {
            return this.mDeathRecipient;
        }
    }

    /* loaded from: classes.dex */
    public final class BinderDeathRecipient implements IBinder.DeathRecipient {
        public final IBinder mSensorToken;

        public BinderDeathRecipient(IBinder iBinder) {
            this.mSensorToken = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slog.e("SensorController", "Virtual sensor controller binder died");
            SensorController.this.unregisterSensor(this.mSensorToken);
        }
    }

    /* loaded from: classes.dex */
    public static class SensorCreationException extends Exception {
        public SensorCreationException(String str) {
            super(str);
        }

        public SensorCreationException(String str, Exception exc) {
            super(str, exc);
        }
    }
}
