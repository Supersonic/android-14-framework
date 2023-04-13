package com.android.server.broadcastradio.hal2;

import android.graphics.Bitmap;
import android.hardware.broadcastradio.V2_0.ConfigFlag;
import android.hardware.broadcastradio.V2_0.ITunerSession;
import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.net.INetd;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.MutableBoolean;
import android.util.MutableInt;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.broadcastradio.RadioServiceUserController;
import com.android.server.broadcastradio.hal2.RadioModule;
import com.android.server.broadcastradio.hal2.Utils;
import com.android.server.utils.Slogf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public class TunerSession extends ITuner.Stub {
    public final ITunerCallback mCallback;
    public final RadioEventLogger mEventLogger;
    public final ITunerSession mHwSession;
    public final RadioModule mModule;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public boolean mIsClosed = false;
    @GuardedBy({"mLock"})
    public boolean mIsMuted = false;
    @GuardedBy({"mLock"})
    public ProgramInfoCache mProgramInfoCache = null;
    public RadioManager.BandConfig mDummyConfig = null;

    public TunerSession(RadioModule radioModule, ITunerSession iTunerSession, ITunerCallback iTunerCallback) {
        Objects.requireNonNull(radioModule);
        this.mModule = radioModule;
        Objects.requireNonNull(iTunerSession);
        this.mHwSession = iTunerSession;
        Objects.requireNonNull(iTunerCallback);
        this.mCallback = iTunerCallback;
        this.mEventLogger = new RadioEventLogger("BcRadio2Srv.session", 25);
    }

    public void close() {
        this.mEventLogger.logRadioEvent("Close", new Object[0]);
        close(null);
    }

    public void close(Integer num) {
        this.mEventLogger.logRadioEvent("Close on error %d", num);
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mIsClosed = true;
            if (num != null) {
                try {
                    this.mCallback.onError(num.intValue());
                } catch (RemoteException e) {
                    Slog.w("BcRadio2Srv.session", "mCallback.onError() failed: ", e);
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
            Slogf.m14w("BcRadio2Srv.session", "Cannot set configuration for HAL 2.0 client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            Objects.requireNonNull(bandConfig);
            RadioManager.BandConfig bandConfig2 = bandConfig;
            this.mDummyConfig = bandConfig;
        }
        Slog.i("BcRadio2Srv.session", "Ignoring setConfiguration - not applicable for broadcastradio HAL 2.0");
        this.mModule.fanoutAidlCallback(new RadioModule.AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.hal2.TunerSession$$ExternalSyntheticLambda5
            @Override // com.android.server.broadcastradio.hal2.RadioModule.AidlCallbackRunnable
            public final void run(ITunerCallback iTunerCallback) {
                iTunerCallback.onConfigurationChanged(bandConfig);
            }
        });
    }

    public RadioManager.BandConfig getConfiguration() {
        RadioManager.BandConfig bandConfig;
        synchronized (this.mLock) {
            checkNotClosedLocked();
            bandConfig = this.mDummyConfig;
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
            Slog.w("BcRadio2Srv.session", "Mute via RadioService is not implemented - please handle it via app");
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
        RadioEventLogger radioEventLogger = this.mEventLogger;
        Object[] objArr = new Object[2];
        objArr[0] = z ? INetd.IF_STATE_DOWN : INetd.IF_STATE_UP;
        objArr[1] = z2 ? "yes" : "no";
        radioEventLogger.logRadioEvent("Step with direction %s, skipSubChannel?  %s", objArr);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot step on HAL 2.0 client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            Convert.throwOnError("step", this.mHwSession.step(z ? false : true));
        }
    }

    public void seek(boolean z, boolean z2) throws RemoteException {
        RadioEventLogger radioEventLogger = this.mEventLogger;
        Object[] objArr = new Object[2];
        objArr[0] = z ? INetd.IF_STATE_DOWN : INetd.IF_STATE_UP;
        objArr[1] = z2 ? "yes" : "no";
        radioEventLogger.logRadioEvent("Seek with direction %s, skipSubChannel? %s", objArr);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot scan on HAL 2.0 client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            Convert.throwOnError("step", this.mHwSession.scan(z ? false : true, z2));
        }
    }

    public void tune(ProgramSelector programSelector) throws RemoteException {
        this.mEventLogger.logRadioEvent("Tune with selector %s", programSelector);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot tune on HAL 2.0 client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            Convert.throwOnError("tune", this.mHwSession.tune(Convert.programSelectorToHal(programSelector)));
        }
    }

    public void cancel() {
        Slog.i("BcRadio2Srv.session", "Cancel");
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot cancel on HAL 2.0 client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            final ITunerSession iTunerSession = this.mHwSession;
            Objects.requireNonNull(iTunerSession);
            Utils.maybeRethrow(new Utils.VoidFuncThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal2.TunerSession$$ExternalSyntheticLambda1
                @Override // com.android.server.broadcastradio.hal2.Utils.VoidFuncThrowingRemoteException
                public final void exec() {
                    ITunerSession.this.cancel();
                }
            });
        }
    }

    public void cancelAnnouncement() {
        Slog.w("BcRadio2Srv.session", "Announcements control doesn't involve cancelling at the HAL level in HAL 2.0");
    }

    public Bitmap getImage(int i) {
        this.mEventLogger.logRadioEvent("Get image for %d", Integer.valueOf(i));
        return this.mModule.getImage(i);
    }

    public boolean startBackgroundScan() {
        Slog.w("BcRadio2Srv.session", "Explicit background scan trigger is not supported with HAL 2.0");
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot start background scan on HAL 2.0 client from non-current user");
            return false;
        }
        this.mModule.fanoutAidlCallback(new RadioModule.AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.hal2.TunerSession$$ExternalSyntheticLambda4
            @Override // com.android.server.broadcastradio.hal2.RadioModule.AidlCallbackRunnable
            public final void run(ITunerCallback iTunerCallback) {
                iTunerCallback.onBackgroundScanComplete();
            }
        });
        return true;
    }

    public void startProgramListUpdates(ProgramList.Filter filter) throws RemoteException {
        this.mEventLogger.logRadioEvent("start programList updates %s", filter);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot start program list updates on HAL 2.0 client from non-current user");
            return;
        }
        if (filter == null) {
            filter = new ProgramList.Filter(new HashSet(), new HashSet(), true, false);
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            this.mProgramInfoCache = new ProgramInfoCache(filter);
        }
        this.mModule.onTunerSessionProgramListFilterChanged(this);
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
            dispatchClientUpdateChunks(programInfoCache2.filterAndUpdateFrom(programInfoCache, true));
        }
    }

    public final void dispatchClientUpdateChunks(List<ProgramList.Chunk> list) {
        if (list == null) {
            return;
        }
        for (ProgramList.Chunk chunk : list) {
            try {
                this.mCallback.onProgramListUpdated(chunk);
            } catch (RemoteException e) {
                Slog.w("BcRadio2Srv.session", "mCallback.onProgramListUpdated() failed: ", e);
            }
        }
    }

    public void stopProgramListUpdates() throws RemoteException {
        this.mEventLogger.logRadioEvent("Stop programList updates", new Object[0]);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot stop program list updates on HAL 2.0 client from non-current user");
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
        } catch (IllegalStateException unused) {
            return true;
        } catch (UnsupportedOperationException unused2) {
            return false;
        }
    }

    public boolean isConfigFlagSet(int i) {
        boolean z;
        this.mEventLogger.logRadioEvent("Is ConfigFlagSet for %s", ConfigFlag.toString(i));
        synchronized (this.mLock) {
            checkNotClosedLocked();
            final MutableInt mutableInt = new MutableInt(1);
            final MutableBoolean mutableBoolean = new MutableBoolean(false);
            try {
                this.mHwSession.isConfigFlagSet(i, new ITunerSession.isConfigFlagSetCallback() { // from class: com.android.server.broadcastradio.hal2.TunerSession$$ExternalSyntheticLambda0
                    @Override // android.hardware.broadcastradio.V2_0.ITunerSession.isConfigFlagSetCallback
                    public final void onValues(int i2, boolean z2) {
                        TunerSession.lambda$isConfigFlagSet$2(mutableInt, mutableBoolean, i2, z2);
                    }
                });
                Convert.throwOnError("isConfigFlagSet", mutableInt.value);
                z = mutableBoolean.value;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed to check flag " + ConfigFlag.toString(i), e);
            }
        }
        return z;
    }

    public static /* synthetic */ void lambda$isConfigFlagSet$2(MutableInt mutableInt, MutableBoolean mutableBoolean, int i, boolean z) {
        mutableInt.value = i;
        mutableBoolean.value = z;
    }

    public void setConfigFlag(int i, boolean z) throws RemoteException {
        this.mEventLogger.logRadioEvent("Set ConfigFlag  %s = %b", ConfigFlag.toString(i), Boolean.valueOf(z));
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot set config flag for HAL 2.0 client from non-current user");
            return;
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            Convert.throwOnError("setConfigFlag", this.mHwSession.setConfigFlag(i, z));
        }
    }

    public Map<String, String> setParameters(final Map<String, String> map) {
        Map<String, String> vendorInfoFromHal;
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m14w("BcRadio2Srv.session", "Cannot set parameters for HAL 2.0 client from non-current user");
            return new ArrayMap();
        }
        synchronized (this.mLock) {
            checkNotClosedLocked();
            vendorInfoFromHal = Convert.vendorInfoFromHal((List) Utils.maybeRethrow(new Utils.FuncThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal2.TunerSession$$ExternalSyntheticLambda3
                @Override // com.android.server.broadcastradio.hal2.Utils.FuncThrowingRemoteException
                public final Object exec() {
                    ArrayList lambda$setParameters$3;
                    lambda$setParameters$3 = TunerSession.this.lambda$setParameters$3(map);
                    return lambda$setParameters$3;
                }
            }));
        }
        return vendorInfoFromHal;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ArrayList lambda$setParameters$3(Map map) throws RemoteException {
        return this.mHwSession.setParameters(Convert.vendorInfoToHal(map));
    }

    public Map<String, String> getParameters(final List<String> list) {
        Map<String, String> vendorInfoFromHal;
        synchronized (this.mLock) {
            checkNotClosedLocked();
            vendorInfoFromHal = Convert.vendorInfoFromHal((List) Utils.maybeRethrow(new Utils.FuncThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal2.TunerSession$$ExternalSyntheticLambda2
                @Override // com.android.server.broadcastradio.hal2.Utils.FuncThrowingRemoteException
                public final Object exec() {
                    ArrayList lambda$getParameters$4;
                    lambda$getParameters$4 = TunerSession.this.lambda$getParameters$4(list);
                    return lambda$getParameters$4;
                }
            }));
        }
        return vendorInfoFromHal;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ArrayList lambda$getParameters$4(List list) throws RemoteException {
        return this.mHwSession.getParameters(Convert.listToArrayList(list));
    }

    public void dumpInfo(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.printf("TunerSession\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.printf("HIDL HAL Session: %s\n", new Object[]{this.mHwSession});
        synchronized (this.mLock) {
            Object[] objArr = new Object[1];
            objArr[0] = this.mIsClosed ? "Yes" : "No";
            indentingPrintWriter.printf("Is session closed? %s\n", objArr);
            Object[] objArr2 = new Object[1];
            objArr2[0] = this.mIsMuted ? "Yes" : "No";
            indentingPrintWriter.printf("Is muted? %s\n", objArr2);
            indentingPrintWriter.printf("ProgramInfoCache: %s\n", new Object[]{this.mProgramInfoCache});
            indentingPrintWriter.printf("Config: %s\n", new Object[]{this.mDummyConfig});
        }
        indentingPrintWriter.printf("Tuner session events:\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        this.mEventLogger.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }
}
