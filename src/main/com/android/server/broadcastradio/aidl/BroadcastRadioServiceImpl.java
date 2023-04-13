package com.android.server.broadcastradio.aidl;

import android.hardware.broadcastradio.IBroadcastRadio;
import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.hardware.radio.ITuner;
import android.hardware.radio.ITunerCallback;
import android.hardware.radio.RadioManager;
import android.os.IBinder;
import android.os.IServiceCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.server.broadcastradio.RadioServiceUserController;
import com.android.server.utils.Slogf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class BroadcastRadioServiceImpl {
    public static final boolean DEBUG = Log.isLoggable("BcRadioAidlSrv", 3);
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final Map<String, Integer> mServiceNameToModuleIdMap = new ArrayMap();
    @GuardedBy({"mLock"})
    public final SparseArray<RadioModule> mModules = new SparseArray<>();
    public final IServiceCallback.Stub mServiceListener = new IServiceCallback.Stub() { // from class: com.android.server.broadcastradio.aidl.BroadcastRadioServiceImpl.1
        public void onRegistration(String str, IBinder iBinder) {
            boolean z;
            Slogf.m20i("BcRadioAidlSrv", "onRegistration for %s", str);
            synchronized (BroadcastRadioServiceImpl.this.mLock) {
                Integer num = (Integer) BroadcastRadioServiceImpl.this.mServiceNameToModuleIdMap.get(str);
                if (num == null) {
                    num = Integer.valueOf(BroadcastRadioServiceImpl.this.mNextModuleId);
                    z = true;
                } else {
                    z = false;
                }
                RadioModule tryLoadingModule = RadioModule.tryLoadingModule(num.intValue(), str, iBinder);
                if (tryLoadingModule == null) {
                    Slogf.m12w("BcRadioAidlSrv", "No module %s with id %d (HAL AIDL)", str, num);
                    return;
                }
                try {
                    tryLoadingModule.setInternalHalCallback();
                    if (BroadcastRadioServiceImpl.DEBUG) {
                        Slogf.m28d("BcRadioAidlSrv", "Loaded broadcast radio module %s with id %d (HAL AIDL)", str, num);
                    }
                    RadioModule radioModule = (RadioModule) BroadcastRadioServiceImpl.this.mModules.get(num.intValue());
                    BroadcastRadioServiceImpl.this.mModules.put(num.intValue(), tryLoadingModule);
                    if (radioModule != null) {
                        radioModule.closeSessions(0);
                    }
                    if (z) {
                        BroadcastRadioServiceImpl.this.mServiceNameToModuleIdMap.put(str, num);
                        BroadcastRadioServiceImpl.this.mNextModuleId++;
                    }
                    try {
                        tryLoadingModule.getService().asBinder().linkToDeath(new BroadcastRadioDeathRecipient(num.intValue()), num.intValue());
                    } catch (RemoteException unused) {
                        Slogf.m14w("BcRadioAidlSrv", "Service has already died, so remove its entry from mModules.");
                        BroadcastRadioServiceImpl.this.mModules.remove(num.intValue());
                    }
                } catch (RemoteException e) {
                    Slogf.wtf("BcRadioAidlSrv", e, "Broadcast radio module %s with id %d (HAL AIDL) cannot register HAL callback", str, num);
                }
            }
        }
    };
    @GuardedBy({"mLock"})
    public int mNextModuleId = 0;

    /* loaded from: classes.dex */
    public final class BroadcastRadioDeathRecipient implements IBinder.DeathRecipient {
        public final int mModuleId;

        public BroadcastRadioDeathRecipient(int i) {
            this.mModuleId = i;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slogf.m20i("BcRadioAidlSrv", "ServiceDied for module id %d", Integer.valueOf(this.mModuleId));
            synchronized (BroadcastRadioServiceImpl.this.mLock) {
                RadioModule radioModule = (RadioModule) BroadcastRadioServiceImpl.this.mModules.removeReturnOld(this.mModuleId);
                if (radioModule != null) {
                    radioModule.closeSessions(0);
                }
                for (Map.Entry entry : BroadcastRadioServiceImpl.this.mServiceNameToModuleIdMap.entrySet()) {
                    if (((Integer) entry.getValue()).intValue() == this.mModuleId) {
                        Slogf.m12w("BcRadioAidlSrv", "Service %s died, removed RadioModule with ID %d", entry.getKey(), Integer.valueOf(this.mModuleId));
                        return;
                    }
                }
            }
        }
    }

    public BroadcastRadioServiceImpl(ArrayList<String> arrayList) {
        if (DEBUG) {
            Slogf.m28d("BcRadioAidlSrv", "Initializing BroadcastRadioServiceImpl %s", IBroadcastRadio.DESCRIPTOR);
        }
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                ServiceManager.registerForNotifications(arrayList.get(i), this.mServiceListener);
            } catch (RemoteException e) {
                Slogf.m23e("BcRadioAidlSrv", e, "failed to register for service notifications for service %s", arrayList.get(i));
            }
        }
    }

    public List<RadioManager.ModuleProperties> listModules() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mModules.size());
            for (int i = 0; i < this.mModules.size(); i++) {
                arrayList.add(this.mModules.valueAt(i).getProperties());
            }
        }
        return arrayList;
    }

    public ITuner openSession(int i, RadioManager.BandConfig bandConfig, boolean z, ITunerCallback iTunerCallback, int i2) throws RemoteException {
        if (DEBUG) {
            Slogf.m30d("BcRadioAidlSrv", "Open AIDL radio session");
        }
        if (!RadioServiceUserController.isCurrentOrSystemUser()) {
            Slogf.m26e("BcRadioAidlSrv", "Cannot open tuner on AIDL HAL client for non-current user");
            throw new IllegalStateException("Cannot open session for non-current user");
        }
        Objects.requireNonNull(iTunerCallback);
        if (!z) {
            throw new IllegalArgumentException("Non-audio sessions not supported with AIDL HAL");
        }
        synchronized (this.mLock) {
            RadioModule radioModule = this.mModules.get(i);
            if (radioModule == null) {
                Slogf.m24e("BcRadioAidlSrv", "Invalid module ID %d", Integer.valueOf(i));
                return null;
            }
            TunerSession openSession = radioModule.openSession(iTunerCallback, i2);
            if (bandConfig != null) {
                openSession.setConfiguration(bandConfig);
            }
            return openSession;
        }
    }

    public ICloseHandle addAnnouncementListener(int[] iArr, IAnnouncementListener iAnnouncementListener) {
        boolean z;
        if (DEBUG) {
            Slogf.m28d("BcRadioAidlSrv", "Add AnnouncementListener with enable types %s", Arrays.toString(iArr));
        }
        AnnouncementAggregator announcementAggregator = new AnnouncementAggregator(iAnnouncementListener, this.mLock);
        synchronized (this.mLock) {
            z = false;
            for (int i = 0; i < this.mModules.size(); i++) {
                try {
                    announcementAggregator.watchModule(this.mModules.valueAt(i), iArr);
                    z = true;
                } catch (UnsupportedOperationException e) {
                    Slogf.m10w("BcRadioAidlSrv", e, "Announcements not supported for this module", new Object[0]);
                }
            }
        }
        if (!z) {
            Slogf.m14w("BcRadioAidlSrv", "There are no HAL modules that support announcements");
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
            indentingPrintWriter.printf("Radio modules [%d]:\n", new Object[]{Integer.valueOf(this.mModules.size())});
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mModules.size(); i++) {
                indentingPrintWriter.printf("Module id=%d:\n", new Object[]{Integer.valueOf(this.mModules.keyAt(i))});
                indentingPrintWriter.increaseIndent();
                this.mModules.valueAt(i).dumpInfo(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
        }
    }
}
