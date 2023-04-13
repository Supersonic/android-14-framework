package com.android.server.broadcastradio.aidl;

import android.hardware.radio.Announcement;
import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.utils.Slogf;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AnnouncementAggregator extends ICloseHandle.Stub {
    public static final boolean DEBUG = Log.isLoggable("BcRadioAidlSrv.AnnAggr", 3);
    public final IBinder.DeathRecipient mDeathRecipient;
    @GuardedBy({"mLock"})
    public boolean mIsClosed;
    public final IAnnouncementListener mListener;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final List<ModuleWatcher> mModuleWatchers;

    public AnnouncementAggregator(IAnnouncementListener iAnnouncementListener, Object obj) {
        DeathRecipient deathRecipient = new DeathRecipient();
        this.mDeathRecipient = deathRecipient;
        this.mModuleWatchers = new ArrayList();
        Objects.requireNonNull(iAnnouncementListener, "listener cannot be null");
        this.mListener = iAnnouncementListener;
        Objects.requireNonNull(obj, "lock cannot be null");
        this.mLock = obj;
        try {
            iAnnouncementListener.asBinder().linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public final class ModuleWatcher extends IAnnouncementListener.Stub {
        public ICloseHandle mCloseHandle;
        public List<Announcement> mCurrentList;

        public ModuleWatcher() {
            this.mCurrentList = new ArrayList();
        }

        public void onListUpdated(List<Announcement> list) {
            if (AnnouncementAggregator.DEBUG) {
                Slogf.m28d("BcRadioAidlSrv.AnnAggr", "onListUpdate for %s", list);
            }
            Objects.requireNonNull(list, "active cannot be null");
            this.mCurrentList = list;
            AnnouncementAggregator.this.onListUpdated();
        }

        public void setCloseHandle(ICloseHandle iCloseHandle) {
            if (AnnouncementAggregator.DEBUG) {
                Slogf.m28d("BcRadioAidlSrv.AnnAggr", "Set close handle %s", iCloseHandle);
            }
            Objects.requireNonNull(iCloseHandle, "closeHandle cannot be null");
            this.mCloseHandle = iCloseHandle;
        }

        public void close() throws RemoteException {
            if (AnnouncementAggregator.DEBUG) {
                Slogf.m30d("BcRadioAidlSrv.AnnAggr", "Close module watcher.");
            }
            ICloseHandle iCloseHandle = this.mCloseHandle;
            if (iCloseHandle != null) {
                iCloseHandle.close();
            }
        }

        public void dumpInfo(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.printf("ModuleWatcher:\n", new Object[0]);
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.printf("Close handle: %s\n", new Object[]{this.mCloseHandle});
            indentingPrintWriter.printf("Current announcement list: %s\n", new Object[]{this.mCurrentList});
            indentingPrintWriter.decreaseIndent();
        }
    }

    /* loaded from: classes.dex */
    public class DeathRecipient implements IBinder.DeathRecipient {
        public DeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            try {
                AnnouncementAggregator.this.close();
            } catch (RemoteException e) {
                Slogf.m23e("BcRadioAidlSrv.AnnAggr", e, "Cannot close Announcement aggregator for DeathRecipient", new Object[0]);
            }
        }
    }

    public final void onListUpdated() {
        if (DEBUG) {
            Slogf.m30d("BcRadioAidlSrv.AnnAggr", "onListUpdated()");
        }
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                Slogf.m26e("BcRadioAidlSrv.AnnAggr", "Announcement aggregator is closed, it shouldn't receive callbacks");
                return;
            }
            ArrayList arrayList = new ArrayList(this.mModuleWatchers.size());
            for (int i = 0; i < this.mModuleWatchers.size(); i++) {
                arrayList.addAll(this.mModuleWatchers.get(i).mCurrentList);
            }
            try {
                this.mListener.onListUpdated(arrayList);
            } catch (RemoteException e) {
                Slogf.m23e("BcRadioAidlSrv.AnnAggr", e, "mListener.onListUpdated() failed", new Object[0]);
            }
        }
    }

    public void watchModule(RadioModule radioModule, int[] iArr) {
        if (DEBUG) {
            Slogf.m28d("BcRadioAidlSrv.AnnAggr", "Watch module for %s with enabled types %s", radioModule, Arrays.toString(iArr));
        }
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                throw new IllegalStateException("Failed to watch modulesince announcement aggregator has already been closed");
            }
            ModuleWatcher moduleWatcher = new ModuleWatcher();
            try {
                moduleWatcher.setCloseHandle(radioModule.addAnnouncementListener(moduleWatcher, iArr));
                this.mModuleWatchers.add(moduleWatcher);
            } catch (RemoteException e) {
                Slogf.m23e("BcRadioAidlSrv.AnnAggr", e, "Failed to add announcement listener", new Object[0]);
            }
        }
    }

    public void close() throws RemoteException {
        if (DEBUG) {
            Slogf.m30d("BcRadioAidlSrv.AnnAggr", "Close watchModule");
        }
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                Slogf.m14w("BcRadioAidlSrv.AnnAggr", "Announcement aggregator has already been closed.");
                return;
            }
            this.mListener.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
            for (int i = 0; i < this.mModuleWatchers.size(); i++) {
                ModuleWatcher moduleWatcher = this.mModuleWatchers.get(i);
                try {
                    moduleWatcher.close();
                } catch (Exception e) {
                    Slogf.m24e("BcRadioAidlSrv.AnnAggr", "Failed to close module watcher %s: %s", moduleWatcher, e);
                }
            }
            this.mModuleWatchers.clear();
            this.mIsClosed = true;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.printf("AnnouncementAggregator\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        synchronized (this.mLock) {
            Object[] objArr = new Object[1];
            objArr[0] = this.mIsClosed ? "Yes" : "No";
            indentingPrintWriter.printf("Is session closed? %s\n", objArr);
            indentingPrintWriter.printf("Module Watchers [%d]:\n", new Object[]{Integer.valueOf(this.mModuleWatchers.size())});
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mModuleWatchers.size(); i++) {
                this.mModuleWatchers.get(i).dumpInfo(indentingPrintWriter);
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
    }
}
