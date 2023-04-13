package com.android.server.broadcastradio;

import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.hardware.radio.IRadioService;
import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.RadioManager;
import android.os.RemoteException;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.broadcastradio.hal2.AnnouncementAggregator;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public final class IRadioServiceHidlImpl extends IRadioService.Stub {
    public final com.android.server.broadcastradio.hal1.BroadcastRadioService mHal1;
    public final com.android.server.broadcastradio.hal2.BroadcastRadioService mHal2;
    public final Object mLock = new Object();
    public final BroadcastRadioService mService;
    @GuardedBy({"mLock"})
    public final List<RadioManager.ModuleProperties> mV1Modules;

    public IRadioServiceHidlImpl(BroadcastRadioService broadcastRadioService) {
        Objects.requireNonNull(broadcastRadioService, "broadcast radio service cannot be null");
        this.mService = broadcastRadioService;
        com.android.server.broadcastradio.hal1.BroadcastRadioService broadcastRadioService2 = new com.android.server.broadcastradio.hal1.BroadcastRadioService();
        this.mHal1 = broadcastRadioService2;
        List<RadioManager.ModuleProperties> loadModules = broadcastRadioService2.loadModules();
        this.mV1Modules = loadModules;
        OptionalInt max = loadModules.stream().mapToInt(new ToIntFunction() { // from class: com.android.server.broadcastradio.IRadioServiceHidlImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((RadioManager.ModuleProperties) obj).getId();
            }
        }).max();
        this.mHal2 = new com.android.server.broadcastradio.hal2.BroadcastRadioService(max.isPresent() ? max.getAsInt() + 1 : 0);
    }

    @VisibleForTesting
    public IRadioServiceHidlImpl(BroadcastRadioService broadcastRadioService, com.android.server.broadcastradio.hal1.BroadcastRadioService broadcastRadioService2, com.android.server.broadcastradio.hal2.BroadcastRadioService broadcastRadioService3) {
        Objects.requireNonNull(broadcastRadioService, "Broadcast radio service cannot be null");
        this.mService = broadcastRadioService;
        Objects.requireNonNull(broadcastRadioService2, "Broadcast radio service implementation for HIDL 1 HAL cannot be null");
        this.mHal1 = broadcastRadioService2;
        this.mV1Modules = broadcastRadioService2.loadModules();
        Objects.requireNonNull(broadcastRadioService3, "Broadcast radio service implementation for HIDL 2 HAL cannot be null");
        this.mHal2 = broadcastRadioService3;
    }

    public List<RadioManager.ModuleProperties> listModules() {
        ArrayList arrayList;
        this.mService.enforcePolicyAccess();
        Collection<RadioManager.ModuleProperties> listModules = this.mHal2.listModules();
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mV1Modules.size() + listModules.size());
            arrayList.addAll(this.mV1Modules);
        }
        arrayList.addAll(listModules);
        return arrayList;
    }

    public ITuner openTuner(int i, RadioManager.BandConfig bandConfig, boolean z, ITunerCallback iTunerCallback, int i2) throws RemoteException {
        if (isDebugEnabled()) {
            Slog.d("BcRadioSrvHidl", "Opening module " + i);
        }
        this.mService.enforcePolicyAccess();
        Objects.requireNonNull(iTunerCallback, "Callback must not be null");
        synchronized (this.mLock) {
            if (this.mHal2.hasModule(i)) {
                return this.mHal2.openSession(i, bandConfig, z, iTunerCallback);
            }
            return this.mHal1.openTuner(i, bandConfig, z, iTunerCallback);
        }
    }

    public ICloseHandle addAnnouncementListener(int[] iArr, IAnnouncementListener iAnnouncementListener) {
        if (isDebugEnabled()) {
            Slog.d("BcRadioSrvHidl", "Adding announcement listener for " + Arrays.toString(iArr));
        }
        Objects.requireNonNull(iArr, "Enabled announcement types cannot be null");
        Objects.requireNonNull(iAnnouncementListener, "Announcement listener cannot be null");
        this.mService.enforcePolicyAccess();
        synchronized (this.mLock) {
            if (!this.mHal2.hasAnyModules()) {
                Slog.w("BcRadioSrvHidl", "There are no HAL 2.0 modules registered");
                return new AnnouncementAggregator(iAnnouncementListener, this.mLock);
            }
            return this.mHal2.addAnnouncementListener(iArr, iAnnouncementListener);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.printf("BroadcastRadioService\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.printf("HAL1: %s\n", new Object[]{this.mHal1});
        indentingPrintWriter.increaseIndent();
        synchronized (this.mLock) {
            indentingPrintWriter.printf("Modules of HAL1: %s\n", new Object[]{this.mV1Modules});
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.printf("HAL2:\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        this.mHal2.dumpInfo(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }

    public static boolean isDebugEnabled() {
        return Log.isLoggable("BcRadioSrvHidl", 3);
    }
}
