package com.android.server.lights;

import android.content.Context;
import android.hardware.light.HwLight;
import android.hardware.light.HwLightState;
import android.hardware.light.ILights;
import android.hardware.lights.ILightsManager;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Trace;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import com.android.server.lights.LightsService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class LightsService extends SystemService {

    /* renamed from: mH */
    public Handler f1146mH;
    public final SparseArray<LightImpl> mLightsById;
    public final LightImpl[] mLightsByType;
    @VisibleForTesting
    final LightsManagerBinderService mManagerService;
    public final LightsManager mService;
    public final Supplier<ILights> mVintfLights;

    public static native void setLight_native(int i, int i2, int i3, int i4, int i5, int i6);

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
    }

    /* loaded from: classes.dex */
    public final class LightsManagerBinderService extends ILightsManager.Stub {
        @GuardedBy({"LightsService.this"})
        public final List<Session> mSessions;

        public LightsManagerBinderService() {
            this.mSessions = new ArrayList();
        }

        /* loaded from: classes.dex */
        public final class Session implements Comparable<Session> {
            public final int mPriority;
            public final SparseArray<LightState> mRequests = new SparseArray<>();
            public final IBinder mToken;

            public Session(IBinder iBinder, int i) {
                this.mToken = iBinder;
                this.mPriority = i;
            }

            public void setRequest(int i, LightState lightState) {
                if (lightState != null) {
                    this.mRequests.put(i, lightState);
                } else {
                    this.mRequests.remove(i);
                }
            }

            @Override // java.lang.Comparable
            public int compareTo(Session session) {
                return Integer.compare(session.mPriority, this.mPriority);
            }
        }

        public List<Light> getLights() {
            ArrayList arrayList;
            LightsService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DEVICE_LIGHTS", "getLights requires CONTROL_DEVICE_LIGHTS_PERMISSION");
            synchronized (LightsService.this) {
                arrayList = new ArrayList();
                for (int i = 0; i < LightsService.this.mLightsById.size(); i++) {
                    if (!((LightImpl) LightsService.this.mLightsById.valueAt(i)).isSystemLight()) {
                        HwLight hwLight = ((LightImpl) LightsService.this.mLightsById.valueAt(i)).mHwLight;
                        arrayList.add(new Light(hwLight.f3id, hwLight.ordinal, hwLight.type));
                    }
                }
            }
            return arrayList;
        }

        public void setLightStates(IBinder iBinder, int[] iArr, LightState[] lightStateArr) {
            LightsService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DEVICE_LIGHTS", "setLightStates requires CONTROL_DEVICE_LIGHTS permission");
            boolean z = true;
            Preconditions.checkState(iArr.length == lightStateArr.length);
            synchronized (LightsService.this) {
                Session sessionLocked = getSessionLocked((IBinder) Preconditions.checkNotNull(iBinder));
                if (sessionLocked == null) {
                    z = false;
                }
                Preconditions.checkState(z, "not registered");
                checkRequestIsValid(iArr);
                for (int i = 0; i < iArr.length; i++) {
                    sessionLocked.setRequest(iArr[i], lightStateArr[i]);
                }
                invalidateLightStatesLocked();
            }
        }

        public LightState getLightState(int i) {
            LightState lightState;
            LightsService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DEVICE_LIGHTS", "getLightState(@TestApi) requires CONTROL_DEVICE_LIGHTS permission");
            synchronized (LightsService.this) {
                LightImpl lightImpl = (LightImpl) LightsService.this.mLightsById.get(i);
                if (lightImpl == null || lightImpl.isSystemLight()) {
                    throw new IllegalArgumentException("Invalid light: " + i);
                }
                lightState = new LightState(lightImpl.getColor());
            }
            return lightState;
        }

        public void openSession(final IBinder iBinder, int i) {
            LightsService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DEVICE_LIGHTS", "openSession requires CONTROL_DEVICE_LIGHTS permission");
            Preconditions.checkNotNull(iBinder);
            synchronized (LightsService.this) {
                Preconditions.checkState(getSessionLocked(iBinder) == null, "already registered");
                try {
                    iBinder.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.lights.LightsService$LightsManagerBinderService$$ExternalSyntheticLambda0
                        @Override // android.os.IBinder.DeathRecipient
                        public final void binderDied() {
                            LightsService.LightsManagerBinderService.this.lambda$openSession$0(iBinder);
                        }
                    }, 0);
                    this.mSessions.add(new Session(iBinder, i));
                    Collections.sort(this.mSessions);
                } catch (RemoteException e) {
                    Slog.e("LightsService", "Couldn't open session, client already died", e);
                    throw new IllegalArgumentException("Client is already dead.");
                }
            }
        }

        public void closeSession(IBinder iBinder) {
            LightsService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DEVICE_LIGHTS", "closeSession requires CONTROL_DEVICE_LIGHTS permission");
            Preconditions.checkNotNull(iBinder);
            lambda$openSession$0(iBinder);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(LightsService.this.getContext(), "LightsService", printWriter)) {
                synchronized (LightsService.this) {
                    if (LightsService.this.mVintfLights != null) {
                        printWriter.println("Service: aidl (" + LightsService.this.mVintfLights.get() + ")");
                    } else {
                        printWriter.println("Service: hidl");
                    }
                    printWriter.println("Lights:");
                    for (int i = 0; i < LightsService.this.mLightsById.size(); i++) {
                        LightImpl lightImpl = (LightImpl) LightsService.this.mLightsById.valueAt(i);
                        printWriter.println(String.format("  Light id=%d ordinal=%d color=%08x", Integer.valueOf(lightImpl.mHwLight.f3id), Integer.valueOf(lightImpl.mHwLight.ordinal), Integer.valueOf(lightImpl.getColor())));
                    }
                    printWriter.println("Session clients:");
                    for (Session session : this.mSessions) {
                        printWriter.println("  Session token=" + session.mToken);
                        for (int i2 = 0; i2 < session.mRequests.size(); i2++) {
                            printWriter.println(String.format("    Request id=%d color=%08x", Integer.valueOf(session.mRequests.keyAt(i2)), Integer.valueOf(session.mRequests.valueAt(i2).getColor())));
                        }
                    }
                }
            }
        }

        /* renamed from: closeSessionInternal */
        public final void lambda$openSession$0(IBinder iBinder) {
            synchronized (LightsService.this) {
                Session sessionLocked = getSessionLocked(iBinder);
                if (sessionLocked != null) {
                    this.mSessions.remove(sessionLocked);
                    invalidateLightStatesLocked();
                }
            }
        }

        public final void checkRequestIsValid(int[] iArr) {
            for (int i : iArr) {
                LightImpl lightImpl = (LightImpl) LightsService.this.mLightsById.get(i);
                Preconditions.checkState((lightImpl == null || lightImpl.isSystemLight()) ? false : true, "Invalid lightId " + i);
            }
        }

        public final void invalidateLightStatesLocked() {
            int i;
            HashMap hashMap = new HashMap();
            int size = this.mSessions.size();
            while (true) {
                size--;
                i = 0;
                if (size < 0) {
                    break;
                }
                SparseArray<LightState> sparseArray = this.mSessions.get(size).mRequests;
                while (i < sparseArray.size()) {
                    hashMap.put(Integer.valueOf(sparseArray.keyAt(i)), sparseArray.valueAt(i));
                    i++;
                }
            }
            while (i < LightsService.this.mLightsById.size()) {
                LightImpl lightImpl = (LightImpl) LightsService.this.mLightsById.valueAt(i);
                if (!lightImpl.isSystemLight()) {
                    LightState lightState = (LightState) hashMap.get(Integer.valueOf(lightImpl.mHwLight.f3id));
                    if (lightState != null) {
                        lightImpl.setColor(lightState.getColor());
                    } else {
                        lightImpl.turnOff();
                    }
                }
                i++;
            }
        }

        public final Session getSessionLocked(IBinder iBinder) {
            for (int i = 0; i < this.mSessions.size(); i++) {
                if (iBinder.equals(this.mSessions.get(i).mToken)) {
                    return this.mSessions.get(i);
                }
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public final class LightImpl extends LogicalLight {
        public int mBrightnessMode;
        public int mColor;
        public boolean mFlashing;
        public HwLight mHwLight;
        public boolean mInitialized;
        public int mLastBrightnessMode;
        public int mLastColor;
        public int mMode;
        public int mOffMS;
        public int mOnMS;
        public boolean mUseLowPersistenceForVR;
        public boolean mVrModeEnabled;

        public LightImpl(Context context, HwLight hwLight) {
            this.mHwLight = hwLight;
        }

        @Override // com.android.server.lights.LogicalLight
        public void setBrightness(float f) {
            setBrightness(f, 0);
        }

        public void setBrightness(float f, int i) {
            if (Float.isNaN(f)) {
                Slog.w("LightsService", "Brightness is not valid: " + f);
                return;
            }
            synchronized (this) {
                if (i == 2) {
                    Slog.w("LightsService", "setBrightness with LOW_PERSISTENCE unexpected #" + this.mHwLight.f3id + ": brightness=" + f);
                    return;
                }
                int brightnessFloatToInt = BrightnessSynchronizer.brightnessFloatToInt(f) & 255;
                setLightLocked((brightnessFloatToInt << 16) | (-16777216) | (brightnessFloatToInt << 8) | brightnessFloatToInt, 0, 0, 0, i);
            }
        }

        @Override // com.android.server.lights.LogicalLight
        public void setColor(int i) {
            synchronized (this) {
                setLightLocked(i, 0, 0, 0, 0);
            }
        }

        @Override // com.android.server.lights.LogicalLight
        public void setFlashing(int i, int i2, int i3, int i4) {
            synchronized (this) {
                setLightLocked(i, i2, i3, i4, 0);
            }
        }

        @Override // com.android.server.lights.LogicalLight
        public void pulse() {
            pulse(16777215, 7);
        }

        public void pulse(int i, int i2) {
            synchronized (this) {
                if (this.mColor == 0 && !this.mFlashing) {
                    setLightLocked(i, 2, i2, 1000, 0);
                    this.mColor = 0;
                    LightsService.this.f1146mH.postDelayed(new Runnable() { // from class: com.android.server.lights.LightsService$LightImpl$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            LightsService.LightImpl.this.stopFlashing();
                        }
                    }, i2);
                }
            }
        }

        @Override // com.android.server.lights.LogicalLight
        public void turnOff() {
            synchronized (this) {
                setLightLocked(0, 0, 0, 0, 0);
            }
        }

        public final void stopFlashing() {
            synchronized (this) {
                setLightLocked(this.mColor, 0, 0, 0, 0);
            }
        }

        public final void setLightLocked(int i, int i2, int i3, int i4, int i5) {
            int i6;
            if (shouldBeInLowPersistenceMode()) {
                i6 = 2;
            } else {
                if (i5 == 2) {
                    i5 = this.mLastBrightnessMode;
                }
                i6 = i5;
            }
            if (this.mInitialized && i == this.mColor && i2 == this.mMode && i3 == this.mOnMS && i4 == this.mOffMS && this.mBrightnessMode == i6) {
                return;
            }
            this.mInitialized = true;
            this.mLastColor = this.mColor;
            this.mColor = i;
            this.mMode = i2;
            this.mOnMS = i3;
            this.mOffMS = i4;
            this.mBrightnessMode = i6;
            setLightUnchecked(i, i2, i3, i4, i6);
        }

        public final void setLightUnchecked(int i, int i2, int i3, int i4, int i5) {
            Trace.traceBegin(131072L, "setLightState(" + this.mHwLight.f3id + ", 0x" + Integer.toHexString(i) + ")");
            try {
                try {
                    if (LightsService.this.mVintfLights != null) {
                        HwLightState hwLightState = new HwLightState();
                        hwLightState.color = i;
                        hwLightState.flashMode = (byte) i2;
                        hwLightState.flashOnMs = i3;
                        hwLightState.flashOffMs = i4;
                        hwLightState.brightnessMode = (byte) i5;
                        ((ILights) LightsService.this.mVintfLights.get()).setLightState(this.mHwLight.f3id, hwLightState);
                    } else {
                        LightsService.setLight_native(this.mHwLight.f3id, i, i2, i3, i4, i5);
                    }
                } catch (RemoteException | UnsupportedOperationException e) {
                    Slog.e("LightsService", "Failed issuing setLightState", e);
                }
            } finally {
                Trace.traceEnd(131072L);
            }
        }

        public final boolean shouldBeInLowPersistenceMode() {
            return this.mVrModeEnabled && this.mUseLowPersistenceForVR;
        }

        public final boolean isSystemLight() {
            byte b = this.mHwLight.type;
            return b >= 0 && b < 8;
        }

        public final int getColor() {
            return this.mColor;
        }
    }

    public LightsService(Context context) {
        this(context, new VintfHalCache(), Looper.myLooper());
    }

    @VisibleForTesting
    public LightsService(Context context, Supplier<ILights> supplier, Looper looper) {
        super(context);
        this.mLightsByType = new LightImpl[8];
        this.mLightsById = new SparseArray<>();
        this.mService = new LightsManager() { // from class: com.android.server.lights.LightsService.1
            @Override // com.android.server.lights.LightsManager
            public LogicalLight getLight(int i) {
                if (LightsService.this.mLightsByType == null || i < 0 || i >= LightsService.this.mLightsByType.length) {
                    return null;
                }
                return LightsService.this.mLightsByType[i];
            }
        };
        this.f1146mH = new Handler(looper);
        this.mVintfLights = supplier.get() == null ? null : supplier;
        populateAvailableLights(context);
        this.mManagerService = new LightsManagerBinderService();
    }

    public final void populateAvailableLights(Context context) {
        if (this.mVintfLights != null) {
            populateAvailableLightsFromAidl(context);
        } else {
            populateAvailableLightsFromHidl(context);
        }
        for (int size = this.mLightsById.size() - 1; size >= 0; size--) {
            int keyAt = this.mLightsById.keyAt(size);
            if (keyAt >= 0) {
                LightImpl[] lightImplArr = this.mLightsByType;
                if (keyAt < lightImplArr.length) {
                    lightImplArr[keyAt] = this.mLightsById.valueAt(size);
                }
            }
        }
    }

    public final void populateAvailableLightsFromAidl(Context context) {
        HwLight[] lights;
        try {
            for (HwLight hwLight : this.mVintfLights.get().getLights()) {
                this.mLightsById.put(hwLight.f3id, new LightImpl(context, hwLight));
            }
        } catch (RemoteException e) {
            Slog.e("LightsService", "Unable to get lights from HAL", e);
        }
    }

    public final void populateAvailableLightsFromHidl(Context context) {
        for (int i = 0; i < this.mLightsByType.length; i++) {
            HwLight hwLight = new HwLight();
            byte b = (byte) i;
            hwLight.f3id = b;
            hwLight.ordinal = 1;
            hwLight.type = b;
            this.mLightsById.put(b, new LightImpl(context, hwLight));
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishLocalService(LightsManager.class, this.mService);
        publishBinderService("lights", this.mManagerService);
    }

    /* loaded from: classes.dex */
    public static class VintfHalCache implements Supplier<ILights>, IBinder.DeathRecipient {
        @GuardedBy({"this"})
        public ILights mInstance;

        public VintfHalCache() {
            this.mInstance = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.function.Supplier
        public synchronized ILights get() {
            if (this.mInstance == null) {
                IBinder allowBlocking = Binder.allowBlocking(ServiceManager.waitForDeclaredService(ILights.DESCRIPTOR + "/default"));
                if (allowBlocking != null) {
                    this.mInstance = ILights.Stub.asInterface(allowBlocking);
                    try {
                        allowBlocking.linkToDeath(this, 0);
                    } catch (RemoteException unused) {
                        Slog.e("LightsService", "Unable to register DeathRecipient for " + this.mInstance);
                    }
                }
            }
            return this.mInstance;
        }

        @Override // android.os.IBinder.DeathRecipient
        public synchronized void binderDied() {
            this.mInstance = null;
        }
    }
}
