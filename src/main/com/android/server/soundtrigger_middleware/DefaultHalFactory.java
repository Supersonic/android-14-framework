package com.android.server.soundtrigger_middleware;

import android.hardware.soundtrigger3.ISoundTriggerHw;
import android.os.HwBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class DefaultHalFactory implements HalFactory {
    public static final ICaptureStateNotifier mCaptureStateNotifier = new ExternalCaptureStateTracker();

    @Override // com.android.server.soundtrigger_middleware.HalFactory
    public ISoundTriggerHal create() {
        try {
            int i = SystemProperties.getInt("debug.soundtrigger_middleware.use_mock_hal", 0);
            if (i == 0) {
                String str = ISoundTriggerHw.class.getCanonicalName() + "/default";
                if (ServiceManager.isDeclared(str)) {
                    Log.i("SoundTriggerMiddlewareDefaultHalFactory", "Connecting to default soundtrigger3.ISoundTriggerHw");
                    return new SoundTriggerHw3Compat(ServiceManager.waitForService(str), new Runnable() { // from class: com.android.server.soundtrigger_middleware.DefaultHalFactory$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SystemProperties.set("sys.audio.restart.hal", "1");
                        }
                    });
                }
                Log.i("SoundTriggerMiddlewareDefaultHalFactory", "Connecting to default soundtrigger-V2.x.ISoundTriggerHw");
                return SoundTriggerHw2Compat.create(android.hardware.soundtrigger.V2_0.ISoundTriggerHw.getService(true), new Runnable() { // from class: com.android.server.soundtrigger_middleware.DefaultHalFactory$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SystemProperties.set("sys.audio.restart.hal", "1");
                    }
                }, mCaptureStateNotifier);
            } else if (i == 2) {
                Log.i("SoundTriggerMiddlewareDefaultHalFactory", "Connecting to mock soundtrigger-V2.x.ISoundTriggerHw");
                HwBinder.setTrebleTestingOverride(true);
                final android.hardware.soundtrigger.V2_0.ISoundTriggerHw service = android.hardware.soundtrigger.V2_0.ISoundTriggerHw.getService("mock", true);
                ISoundTriggerHal create = SoundTriggerHw2Compat.create(service, new Runnable() { // from class: com.android.server.soundtrigger_middleware.DefaultHalFactory$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        DefaultHalFactory.lambda$create$2(android.hardware.soundtrigger.V2_0.ISoundTriggerHw.this);
                    }
                }, mCaptureStateNotifier);
                HwBinder.setTrebleTestingOverride(false);
                return create;
            } else if (i == 3) {
                final String str2 = ISoundTriggerHw.class.getCanonicalName() + "/mock";
                Log.i("SoundTriggerMiddlewareDefaultHalFactory", "Connecting to mock soundtrigger3.ISoundTriggerHw");
                return new SoundTriggerHw3Compat(ServiceManager.waitForService(str2), new Runnable() { // from class: com.android.server.soundtrigger_middleware.DefaultHalFactory$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        DefaultHalFactory.lambda$create$3(str2);
                    }
                });
            } else {
                throw new RuntimeException("Unknown HAL mock version: " + i);
            }
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public static /* synthetic */ void lambda$create$2(android.hardware.soundtrigger.V2_0.ISoundTriggerHw iSoundTriggerHw) {
        try {
            iSoundTriggerHw.debug(null, new ArrayList<>(Arrays.asList("reboot")));
        } catch (Exception e) {
            Log.e("SoundTriggerMiddlewareDefaultHalFactory", "Failed to reboot mock HAL", e);
        }
    }

    public static /* synthetic */ void lambda$create$3(String str) {
        try {
            ServiceManager.waitForService(str).shellCommand(null, null, null, new String[]{"reboot"}, null, null);
        } catch (Exception e) {
            Log.e("SoundTriggerMiddlewareDefaultHalFactory", "Failed to reboot mock HAL", e);
        }
    }
}
