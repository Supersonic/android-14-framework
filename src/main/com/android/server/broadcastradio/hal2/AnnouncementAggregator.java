package com.android.server.broadcastradio.hal2;

import android.hardware.radio.Announcement;
import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class AnnouncementAggregator extends ICloseHandle.Stub {
    public final IBinder.DeathRecipient mDeathRecipient;
    @GuardedBy({"mLock"})
    public boolean mIsClosed;
    public final IAnnouncementListener mListener;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final Collection<ModuleWatcher> mModuleWatchers;

    public AnnouncementAggregator(IAnnouncementListener iAnnouncementListener, Object obj) {
        DeathRecipient deathRecipient = new DeathRecipient();
        this.mDeathRecipient = deathRecipient;
        this.mModuleWatchers = new ArrayList();
        this.mIsClosed = false;
        Objects.requireNonNull(iAnnouncementListener);
        this.mListener = iAnnouncementListener;
        Objects.requireNonNull(obj);
        this.mLock = obj;
        try {
            iAnnouncementListener.asBinder().linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public class ModuleWatcher extends IAnnouncementListener.Stub {
        public List<Announcement> currentList;
        public ICloseHandle mCloseHandle;

        public ModuleWatcher() {
            this.currentList = new ArrayList();
        }

        public void onListUpdated(List<Announcement> list) {
            Objects.requireNonNull(list);
            this.currentList = list;
            AnnouncementAggregator.this.onListUpdated();
        }

        public void setCloseHandle(ICloseHandle iCloseHandle) {
            Objects.requireNonNull(iCloseHandle);
            this.mCloseHandle = iCloseHandle;
        }

        public void close() throws RemoteException {
            ICloseHandle iCloseHandle = this.mCloseHandle;
            if (iCloseHandle != null) {
                iCloseHandle.close();
            }
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
            } catch (RemoteException unused) {
            }
        }
    }

    public final void onListUpdated() {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                Slog.e("BcRadio2Srv.AnnAggr", "Announcement aggregator is closed, it shouldn't receive callbacks");
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (ModuleWatcher moduleWatcher : this.mModuleWatchers) {
                arrayList.addAll(moduleWatcher.currentList);
            }
            try {
                this.mListener.onListUpdated(arrayList);
            } catch (RemoteException e) {
                Slog.e("BcRadio2Srv.AnnAggr", "mListener.onListUpdated() failed: ", e);
            }
        }
    }

    public void watchModule(RadioModule radioModule, int[] iArr) {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                throw new IllegalStateException("Failed to watch modulesince announcement aggregator has already been closed");
            }
            ModuleWatcher moduleWatcher = new ModuleWatcher();
            try {
                moduleWatcher.setCloseHandle(radioModule.addAnnouncementListener(iArr, moduleWatcher));
                this.mModuleWatchers.add(moduleWatcher);
            } catch (RemoteException e) {
                Slog.e("BcRadio2Srv.AnnAggr", "Failed to add announcement listener", e);
            }
        }
    }

    public void close() throws RemoteException {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mIsClosed = true;
            this.mListener.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
            for (ModuleWatcher moduleWatcher : this.mModuleWatchers) {
                moduleWatcher.close();
            }
            this.mModuleWatchers.clear();
        }
    }
}
