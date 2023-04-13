package com.android.server.broadcastradio.hal2;

import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.RadioManager;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.broadcastradio.RadioServiceUserController;
import com.android.server.utils.Slogf;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class BroadcastRadioService {
    @GuardedBy({"mLock"})
    public int mNextModuleId;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final Map<String, Integer> mServiceNameToModuleIdMap = new HashMap();
    @GuardedBy({"mLock"})
    public final Map<Integer, RadioModule> mModules = new HashMap();
    public IServiceNotification.Stub mServiceListener = new IServiceNotification.Stub() { // from class: com.android.server.broadcastradio.hal2.BroadcastRadioService.1
        @Override // android.hidl.manager.V1_0.IServiceNotification
        public void onRegistration(String str, String str2, boolean z) {
            boolean z2;
            Slog.v("BcRadio2Srv", "onRegistration(" + str + ", " + str2 + ", " + z + ")");
            synchronized (BroadcastRadioService.this.mLock) {
                Integer num = (Integer) BroadcastRadioService.this.mServiceNameToModuleIdMap.get(str2);
                if (num == null) {
                    num = Integer.valueOf(BroadcastRadioService.this.mNextModuleId);
                    z2 = true;
                } else {
                    z2 = false;
                }
                RadioModule tryLoadingModule = RadioModule.tryLoadingModule(num.intValue(), str2);
                if (tryLoadingModule == null) {
                    return;
                }
                Slog.v("BcRadio2Srv", "loaded broadcast radio module " + num + ": " + str2 + " (HAL 2.0)");
                RadioModule radioModule = (RadioModule) BroadcastRadioService.this.mModules.put(num, tryLoadingModule);
                if (radioModule != null) {
                    radioModule.closeSessions(0);
                }
                if (z2) {
                    BroadcastRadioService.this.mServiceNameToModuleIdMap.put(str2, num);
                    BroadcastRadioService.this.mNextModuleId++;
                }
                try {
                    tryLoadingModule.getService().linkToDeath(BroadcastRadioService.this.mDeathRecipient, num.intValue());
                } catch (RemoteException unused) {
                    BroadcastRadioService.this.mModules.remove(num);
                }
            }
        }
    };
    public IHwBinder.DeathRecipient mDeathRecipient = new IHwBinder.DeathRecipient() { // from class: com.android.server.broadcastradio.hal2.BroadcastRadioService.2
        public void serviceDied(long j) {
            Slog.v("BcRadio2Srv", "serviceDied(" + j + ")");
            synchronized (BroadcastRadioService.this.mLock) {
                int i = (int) j;
                RadioModule radioModule = (RadioModule) BroadcastRadioService.this.mModules.remove(Integer.valueOf(i));
                if (radioModule != null) {
                    radioModule.closeSessions(0);
                }
                for (Map.Entry entry : BroadcastRadioService.this.mServiceNameToModuleIdMap.entrySet()) {
                    if (((Integer) entry.getValue()).intValue() == i) {
                        Slog.i("BcRadio2Srv", "service " + ((String) entry.getKey()) + " died; removed RadioModule with ID " + i);
                        return;
                    }
                }
            }
        }
    };

    public BroadcastRadioService(int i) {
        this.mNextModuleId = i;
        try {
            IServiceManager service = IServiceManager.getService();
            if (service == null) {
                Slog.e("BcRadio2Srv", "failed to get HIDL Service Manager");
            } else {
                service.registerForNotifications("android.hardware.broadcastradio@2.0::IBroadcastRadio", "", this.mServiceListener);
            }
        } catch (RemoteException e) {
            Slog.e("BcRadio2Srv", "failed to register for service notifications: ", e);
        }
    }

    @VisibleForTesting
    public BroadcastRadioService(int i, IServiceManager iServiceManager) {
        this.mNextModuleId = i;
        Objects.requireNonNull(iServiceManager, "Service manager cannot be null");
        try {
            iServiceManager.registerForNotifications("android.hardware.broadcastradio@2.0::IBroadcastRadio", "", this.mServiceListener);
        } catch (RemoteException e) {
            Slog.e("BcRadio2Srv", "Failed to register for service notifications: ", e);
        }
    }

    public Collection<RadioManager.ModuleProperties> listModules() {
        Collection<RadioManager.ModuleProperties> collection;
        Slog.v("BcRadio2Srv", "List HIDL 2.0 modules");
        synchronized (this.mLock) {
            collection = (Collection) this.mModules.values().stream().map(new Function() { // from class: com.android.server.broadcastradio.hal2.BroadcastRadioService$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    RadioManager.ModuleProperties properties;
                    properties = ((RadioModule) obj).getProperties();
                    return properties;
                }
            }).collect(Collectors.toList());
        }
        return collection;
    }

    public boolean hasModule(int i) {
        boolean containsKey;
        synchronized (this.mLock) {
            containsKey = this.mModules.containsKey(Integer.valueOf(i));
        }
        return containsKey;
    }

    public boolean hasAnyModules() {
        boolean z;
        synchronized (this.mLock) {
            z = !this.mModules.isEmpty();
        }
        return z;
    }

    public ITuner openSession(int i, RadioManager.BandConfig bandConfig, boolean z, ITunerCallback iTunerCallback) throws RemoteException {
        RadioModule radioModule;
        Slog.v("BcRadio2Srv", "Open HIDL 2.0 session with module id " + i);
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m26e("BcRadio2Srv", "Cannot open tuner on HAL 2.0 client for non-current user");
            throw new IllegalStateException("Cannot open session for non-current user");
        }
        Objects.requireNonNull(iTunerCallback);
        if (!z) {
            throw new IllegalArgumentException("Non-audio sessions not supported with HAL 2.0");
        }
        synchronized (this.mLock) {
            radioModule = this.mModules.get(Integer.valueOf(i));
            if (radioModule == null) {
                throw new IllegalArgumentException("Invalid module ID");
            }
        }
        TunerSession openSession = radioModule.openSession(iTunerCallback);
        if (bandConfig != null) {
            openSession.setConfiguration(bandConfig);
        }
        return openSession;
    }

    public ICloseHandle addAnnouncementListener(int[] iArr, IAnnouncementListener iAnnouncementListener) {
        boolean z;
        Slog.v("BcRadio2Srv", "Add announcementListener");
        AnnouncementAggregator announcementAggregator = new AnnouncementAggregator(iAnnouncementListener, this.mLock);
        synchronized (this.mLock) {
            z = false;
            for (RadioModule radioModule : this.mModules.values()) {
                try {
                    announcementAggregator.watchModule(radioModule, iArr);
                    z = true;
                } catch (UnsupportedOperationException e) {
                    Slog.v("BcRadio2Srv", "Announcements not supported for this module", e);
                }
            }
        }
        if (!z) {
            Slog.i("BcRadio2Srv", "There are no HAL modules that support announcements");
        }
        return announcementAggregator;
    }

    public void dumpInfo(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.printf("Next module id available: %d\n", new Object[]{Integer.valueOf(this.mNextModuleId)});
            indentingPrintWriter.printf("ServiceName to module id map:\n", new Object[0]);
            indentingPrintWriter.increaseIndent();
            for (Map.Entry<String, Integer> entry : this.mServiceNameToModuleIdMap.entrySet()) {
                indentingPrintWriter.printf("Service name: %s, module id: %d\n", new Object[]{entry.getKey(), entry.getValue()});
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.printf("Radio modules:\n", new Object[0]);
            indentingPrintWriter.increaseIndent();
            for (Map.Entry<Integer, RadioModule> entry2 : this.mModules.entrySet()) {
                indentingPrintWriter.printf("Module id=%d:\n", new Object[]{entry2.getKey()});
                indentingPrintWriter.increaseIndent();
                entry2.getValue().dumpInfo(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
        }
    }
}
