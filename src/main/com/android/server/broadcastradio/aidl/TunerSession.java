package com.android.server.broadcastradio.aidl;

import android.graphics.Bitmap;
import android.hardware.broadcastradio.ConfigFlag$$;
import android.hardware.broadcastradio.IBroadcastRadio;
import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.net.INetd;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import com.android.internal.annotations.GuardedBy;
import com.android.server.broadcastradio.RadioServiceUserController;
import com.android.server.broadcastradio.aidl.RadioModule;
import com.android.server.utils.Slogf;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TunerSession extends ITuner.Stub {
    public final ITunerCallback mCallback;
    @GuardedBy({"mLock"})
    public boolean mIsClosed;
    @GuardedBy({"mLock"})
    public boolean mIsMuted;
    public final Object mLock = new Object();
    public final RadioLogger mLogger;
    public final RadioModule mModule;
    @GuardedBy({"mLock"})
    public RadioManager.BandConfig mPlaceHolderConfig;
    @GuardedBy({"mLock"})
    public ProgramInfoCache mProgramInfoCache;
    public final IBroadcastRadio mService;
    public final int mTargetSdkVersion;

    public TunerSession(RadioModule radioModule, IBroadcastRadio iBroadcastRadio, ITunerCallback iTunerCallback, int i) {
        Objects.requireNonNull(radioModule, "radioModule cannot be null");
        this.mModule = radioModule;
        Objects.requireNonNull(iBroadcastRadio, "service cannot be null");
        this.mService = iBroadcastRadio;
        Objects.requireNonNull(iTunerCallback, "callback cannot be null");
        this.mCallback = iTunerCallback;
        this.mTargetSdkVersion = i;
        this.mLogger = new RadioLogger("BcRadioAidlSrv.session", 25);
    }

    public void close() {
        this.mLogger.logRadioEvent("Close tuner", new Object[0]);
        close(null);
    }

    public void close(Integer num) {
        if (num == null) {
            this.mLogger.logRadioEvent("Close tuner session on error null", new Object[0]);
        } else {
            this.mLogger.logRadioEvent("Close tuner session on error %d", num);
        }
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mIsClosed = true;
            if (num != null) {
                try {
                    this.mCallback.onError(num.intValue());
                } catch (RemoteException e) {
                    Slogf.m10w("BcRadioAidlSrv.session", e, "mCallback.onError(%s) failed", num);
                }
            }
            this.mModule.onTunerSessionClosed(this);
        }
    }

    public boolean isClosed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsClosed;
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final void checkNotClosedLocked() {
        if (this.mIsClosed) {
            throw new IllegalStateException("Tuner is closed, no further operations are allowed");
        }
    }

    public void setConfiguration(final RadioManager.BandConfig bandConfig) {
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot set configuration for AIDL HAL client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            Objects.requireNonNull(bandConfig, "config cannot be null");
            RadioManager.BandConfig bandConfig2 = bandConfig;
            this.mPlaceHolderConfig = bandConfig;
        }
        Slogf.m22i("BcRadioAidlSrv.session", "Ignoring setConfiguration - not applicable for broadcastradio HAL AIDL");
        this.mModule.fanoutAidlCallback(new RadioModule.AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.aidl.TunerSession$$ExternalSyntheticLambda1
            @Override // com.android.server.broadcastradio.aidl.RadioModule.AidlCallbackRunnable
            public final void run(ITunerCallback iTunerCallback, int i) {
                iTunerCallback.onConfigurationChanged(bandConfig);
            }
        });
    }

    public RadioManager.BandConfig getConfiguration() {
        RadioManager.BandConfig bandConfig;
        synchronized (this.mLock) {
            checkNotClosedLocked();
            bandConfig = this.mPlaceHolderConfig;
        }
        return bandConfig;
    }

    public void setMuted(boolean z) {
        synchronized (this.mLock) {
            checkNotClosedLocked();
            if (this.mIsMuted == z) {
                return;
            }
            this.mIsMuted = z;
            Slogf.m12w("BcRadioAidlSrv.session", "Mute %b via RadioService is not implemented - please handle it via app", Boolean.valueOf(z));
        }
    }

    public boolean isMuted() {
        boolean z;
        synchronized (this.mLock) {
            checkNotClosedLocked();
            z = this.mIsMuted;
        }
        return z;
    }

    public void step(boolean z, boolean z2) throws RemoteException {
        RadioLogger radioLogger = this.mLogger;
        Object[] objArr = new Object[2];
        objArr[0] = z ? INetd.IF_STATE_DOWN : INetd.IF_STATE_UP;
        objArr[1] = z2 ? "yes" : "no";
        radioLogger.logRadioEvent("Step with direction %s, skipSubChannel?  %s", objArr);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot step on AIDL HAL client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                this.mService.step(z ? false : true);
            } catch (RuntimeException e) {
                throw ConversionUtils.throwOnError(e, "step");
            }
        }
    }

    public void seek(boolean z, boolean z2) throws RemoteException {
        RadioLogger radioLogger = this.mLogger;
        Object[] objArr = new Object[2];
        objArr[0] = z ? INetd.IF_STATE_DOWN : INetd.IF_STATE_UP;
        objArr[1] = z2 ? "yes" : "no";
        radioLogger.logRadioEvent("Seek with direction %s, skipSubChannel? %s", objArr);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot scan on AIDL HAL client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                this.mService.seek(z ? false : true, z2);
            } catch (RuntimeException e) {
                throw ConversionUtils.throwOnError(e, "seek");
            }
        }
    }

    public void tune(ProgramSelector programSelector) throws RemoteException {
        this.mLogger.logRadioEvent("Tune with selector %s", programSelector);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot tune on AIDL HAL client from non-current user");
            return;
        }
        android.hardware.broadcastradio.ProgramSelector programSelectorToHalProgramSelector = ConversionUtils.programSelectorToHalProgramSelector(programSelector);
        if (programSelectorToHalProgramSelector == null) {
            throw new IllegalArgumentException("tune: INVALID_ARGUMENTS for program selector");
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                this.mService.tune(programSelectorToHalProgramSelector);
            } catch (RuntimeException e) {
                throw ConversionUtils.throwOnError(e, "tune");
            }
        }
    }

    public void cancel() {
        Slogf.m22i("BcRadioAidlSrv.session", "Cancel");
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot cancel on AIDL HAL client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                this.mService.cancel();
            } catch (RemoteException e) {
                Slogf.m26e("BcRadioAidlSrv.session", "Failed to cancel tuner session");
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void cancelAnnouncement() {
        Slogf.m14w("BcRadioAidlSrv.session", "Announcements control doesn't involve cancelling at the HAL level in AIDL");
    }

    public Bitmap getImage(int i) {
        this.mLogger.logRadioEvent("Get image for %d", Integer.valueOf(i));
        return this.mModule.getImage(i);
    }

    public boolean startBackgroundScan() {
        Slogf.m14w("BcRadioAidlSrv.session", "Explicit background scan trigger is not supported with HAL AIDL");
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot start background scan on AIDL HAL client from non-current user");
            return false;
        }
        this.mModule.fanoutAidlCallback(new RadioModule.AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.aidl.TunerSession$$ExternalSyntheticLambda0
            @Override // com.android.server.broadcastradio.aidl.RadioModule.AidlCallbackRunnable
            public final void run(ITunerCallback iTunerCallback, int i) {
                iTunerCallback.onBackgroundScanComplete();
            }
        });
        return true;
    }

    public void startProgramListUpdates(ProgramList.Filter filter) throws RemoteException {
        this.mLogger.logRadioEvent("Start programList updates %s", filter);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot start program list updates on AIDL HAL client from non-current user");
            return;
        }
        if (filter == null) {
            filter = new ProgramList.Filter(new ArraySet(), new ArraySet(), true, false);
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            this.mProgramInfoCache = new ProgramInfoCache(filter);
        }
        this.mModule.onTunerSessionProgramListFilterChanged(this);
    }

    public int getTargetSdkVersion() {
        return this.mTargetSdkVersion;
    }

    public ProgramList.Filter getProgramListFilter() {
        ProgramList.Filter filter;
        synchronized (this.mLock) {
            ProgramInfoCache programInfoCache = this.mProgramInfoCache;
            filter = programInfoCache == null ? null : programInfoCache.getFilter();
        }
        return filter;
    }

    public void onMergedProgramListUpdateFromHal(ProgramList.Chunk chunk) {
        synchronized (this.mLock) {
            ProgramInfoCache programInfoCache = this.mProgramInfoCache;
            if (programInfoCache == null) {
                return;
            }
            dispatchClientUpdateChunks(programInfoCache.filterAndApplyChunk(chunk));
        }
    }

    public void updateProgramInfoFromHalCache(ProgramInfoCache programInfoCache) {
        synchronized (this.mLock) {
            ProgramInfoCache programInfoCache2 = this.mProgramInfoCache;
            if (programInfoCache2 == null) {
                return;
            }
            dispatchClientUpdateChunks(programInfoCache2.filterAndUpdateFromInternal(programInfoCache, true));
        }
    }

    public final void dispatchClientUpdateChunks(List<ProgramList.Chunk> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                if (!ConversionUtils.isAtLeastU(getTargetSdkVersion())) {
                    this.mCallback.onProgramListUpdated(ConversionUtils.convertChunkToTargetSdkVersion(list.get(i), getTargetSdkVersion()));
                } else {
                    this.mCallback.onProgramListUpdated(list.get(i));
                }
            } catch (RemoteException e) {
                Slogf.m10w("BcRadioAidlSrv.session", e, "mCallback.onProgramListUpdated() failed", new Object[0]);
            }
        }
    }

    public void stopProgramListUpdates() throws RemoteException {
        this.mLogger.logRadioEvent("Stop programList updates", new Object[0]);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot stop program list updates on AIDL HAL client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            this.mProgramInfoCache = null;
        }
        this.mModule.onTunerSessionProgramListFilterChanged(this);
    }

    public boolean isConfigFlagSupported(int i) {
        try {
            isConfigFlagSet(i);
            return true;
        } catch (IllegalStateException | UnsupportedOperationException unused) {
            return false;
        }
    }

    public boolean isConfigFlagSet(int i) {
        boolean isConfigFlagSet;
        this.mLogger.logRadioEvent("is ConfigFlag %s set? ", ConfigFlag$$.toString(i));
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                try {
                    isConfigFlagSet = this.mService.isConfigFlagSet(i);
                } catch (RemoteException e) {
                    throw new RuntimeException("Failed to check flag " + ConfigFlag$$.toString(i), e);
                }
            } catch (RuntimeException e2) {
                throw ConversionUtils.throwOnError(e2, "isConfigFlagSet");
            }
        }
        return isConfigFlagSet;
    }

    public void setConfigFlag(int i, boolean z) throws RemoteException {
        this.mLogger.logRadioEvent("set ConfigFlag %s to %b ", ConfigFlag$$.toString(i), Boolean.valueOf(z));
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot set config flag for AIDL HAL client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                this.mService.setConfigFlag(i, z);
            } catch (RuntimeException e) {
                throw ConversionUtils.throwOnError(e, "setConfigFlag");
            }
        }
    }

    public Map<String, String> setParameters(Map<String, String> map) {
        Map<String, String> vendorInfoFromHalVendorKeyValues;
        this.mLogger.logRadioEvent("Set parameters ", new Object[0]);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadioAidlSrv.session", "Cannot set parameters for AIDL HAL client from non-current user");
            return new ArrayMap();
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                vendorInfoFromHalVendorKeyValues = ConversionUtils.vendorInfoFromHalVendorKeyValues(this.mService.setParameters(ConversionUtils.vendorInfoToHalVendorKeyValues(map)));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return vendorInfoFromHalVendorKeyValues;
    }

    public Map<String, String> getParameters(List<String> list) {
        Map<String, String> vendorInfoFromHalVendorKeyValues;
        this.mLogger.logRadioEvent("Get parameters ", new Object[0]);
        synchronized (this.mLock) {
            checkNotClosedLocked();
            try {
                vendorInfoFromHalVendorKeyValues = ConversionUtils.vendorInfoFromHalVendorKeyValues(this.mService.getParameters((String[]) list.toArray(new String[0])));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return vendorInfoFromHalVendorKeyValues;
    }

    public void dumpInfo(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.printf("TunerSession\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        synchronized (this.mLock) {
            Object[] objArr = new Object[1];
            objArr[0] = this.mIsClosed ? "Yes" : "No";
            indentingPrintWriter.printf("Is session closed? %s\n", objArr);
            Object[] objArr2 = new Object[1];
            objArr2[0] = this.mIsMuted ? "Yes" : "No";
            indentingPrintWriter.printf("Is muted? %s\n", objArr2);
            indentingPrintWriter.printf("ProgramInfoCache: %s\n", new Object[]{this.mProgramInfoCache});
            indentingPrintWriter.printf("Config: %s\n", new Object[]{this.mPlaceHolderConfig});
        }
        indentingPrintWriter.printf("Tuner session events:\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        this.mLogger.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }
}
