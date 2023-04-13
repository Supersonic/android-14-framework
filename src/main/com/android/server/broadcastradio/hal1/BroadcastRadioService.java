package com.android.server.broadcastradio.hal1;

import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.RadioManager;
import com.android.server.broadcastradio.RadioServiceUserController;
import com.android.server.utils.Slogf;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class BroadcastRadioService {
    public static final String TAG = "BcRadio1Srv";
    public final long mNativeContext = nativeInit();
    public final Object mLock = new Object();

    private native void nativeFinalize(long j);

    private native long nativeInit();

    private native List<RadioManager.ModuleProperties> nativeLoadModules(long j);

    private native Tuner nativeOpenTuner(long j, int i, RadioManager.BandConfig bandConfig, boolean z, ITunerCallback iTunerCallback);

    public void finalize() throws Throwable {
        nativeFinalize(this.mNativeContext);
        super.finalize();
    }

    public List<RadioManager.ModuleProperties> loadModules() {
        List<RadioManager.ModuleProperties> list;
        synchronized (this.mLock) {
            List<RadioManager.ModuleProperties> nativeLoadModules = nativeLoadModules(this.mNativeContext);
            Objects.requireNonNull(nativeLoadModules);
            list = nativeLoadModules;
        }
        return list;
    }

    public ITuner openTuner(int i, RadioManager.BandConfig bandConfig, boolean z, ITunerCallback iTunerCallback) {
        Tuner nativeOpenTuner;
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m26e(TAG, "Cannot open tuner on HAL 1.x client for non-current user");
            throw new IllegalStateException("Cannot open tuner for non-current user");
        }
        synchronized (this.mLock) {
            nativeOpenTuner = nativeOpenTuner(this.mNativeContext, i, bandConfig, z, iTunerCallback);
        }
        return nativeOpenTuner;
    }
}
