package com.android.server.display;

import android.os.Trace;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayAddress;
import com.android.internal.annotations.GuardedBy;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class DisplayDeviceRepository implements DisplayAdapter.Listener {
    public static final Boolean DEBUG = Boolean.FALSE;
    @GuardedBy({"mSyncRoot"})
    public final List<DisplayDevice> mDisplayDevices = new ArrayList();
    @GuardedBy({"mSyncRoot"})
    public final List<Listener> mListeners = new ArrayList();
    public final PersistentDataStore mPersistentDataStore;
    public final DisplayManagerService.SyncRoot mSyncRoot;

    /* loaded from: classes.dex */
    public interface Listener {
        void onDisplayDeviceChangedLocked(DisplayDevice displayDevice, int i);

        void onDisplayDeviceEventLocked(DisplayDevice displayDevice, int i);

        void onTraversalRequested();
    }

    public DisplayDeviceRepository(DisplayManagerService.SyncRoot syncRoot, PersistentDataStore persistentDataStore) {
        this.mSyncRoot = syncRoot;
        this.mPersistentDataStore = persistentDataStore;
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    @Override // com.android.server.display.DisplayAdapter.Listener
    public void onDisplayDeviceEvent(DisplayDevice displayDevice, int i) {
        String str;
        Boolean bool = DEBUG;
        if (bool.booleanValue()) {
            str = "DisplayDeviceRepository#onDisplayDeviceEvent (event=" + i + ")";
            Trace.beginAsyncSection(str, 0);
        } else {
            str = null;
        }
        if (i == 1) {
            handleDisplayDeviceAdded(displayDevice);
        } else if (i == 2) {
            handleDisplayDeviceChanged(displayDevice);
        } else if (i == 3) {
            handleDisplayDeviceRemoved(displayDevice);
        }
        if (bool.booleanValue()) {
            Trace.endAsyncSection(str, 0);
        }
    }

    @Override // com.android.server.display.DisplayAdapter.Listener
    public void onTraversalRequested() {
        int size = this.mListeners.size();
        for (int i = 0; i < size; i++) {
            this.mListeners.get(i).onTraversalRequested();
        }
    }

    public boolean containsLocked(DisplayDevice displayDevice) {
        return this.mDisplayDevices.contains(displayDevice);
    }

    public int sizeLocked() {
        return this.mDisplayDevices.size();
    }

    public void forEachLocked(Consumer<DisplayDevice> consumer) {
        int size = this.mDisplayDevices.size();
        for (int i = 0; i < size; i++) {
            consumer.accept(this.mDisplayDevices.get(i));
        }
    }

    public DisplayDevice getByAddressLocked(DisplayAddress displayAddress) {
        for (int size = this.mDisplayDevices.size() - 1; size >= 0; size--) {
            DisplayDevice displayDevice = this.mDisplayDevices.get(size);
            if (displayAddress.equals(displayDevice.getDisplayDeviceInfoLocked().address)) {
                return displayDevice;
            }
        }
        return null;
    }

    public DisplayDevice getByUniqueIdLocked(String str) {
        for (int size = this.mDisplayDevices.size() - 1; size >= 0; size--) {
            DisplayDevice displayDevice = this.mDisplayDevices.get(size);
            if (displayDevice.getUniqueId().equals(str)) {
                return displayDevice;
            }
        }
        return null;
    }

    public final void handleDisplayDeviceAdded(DisplayDevice displayDevice) {
        synchronized (this.mSyncRoot) {
            DisplayDeviceInfo displayDeviceInfoLocked = displayDevice.getDisplayDeviceInfoLocked();
            if (this.mDisplayDevices.contains(displayDevice)) {
                Slog.w("DisplayDeviceRepository", "Attempted to add already added display device: " + displayDeviceInfoLocked);
                return;
            }
            Slog.i("DisplayDeviceRepository", "Display device added: " + displayDeviceInfoLocked);
            displayDevice.mDebugLastLoggedDeviceInfo = displayDeviceInfoLocked;
            this.mDisplayDevices.add(displayDevice);
            sendEventLocked(displayDevice, 1);
        }
    }

    public final void handleDisplayDeviceChanged(DisplayDevice displayDevice) {
        synchronized (this.mSyncRoot) {
            DisplayDeviceInfo displayDeviceInfoLocked = displayDevice.getDisplayDeviceInfoLocked();
            if (!this.mDisplayDevices.contains(displayDevice)) {
                Slog.w("DisplayDeviceRepository", "Attempted to change non-existent display device: " + displayDeviceInfoLocked);
                return;
            }
            Boolean bool = DEBUG;
            if (bool.booleanValue()) {
                Trace.traceBegin(131072L, "handleDisplayDeviceChanged");
            }
            int diff = displayDevice.mDebugLastLoggedDeviceInfo.diff(displayDeviceInfoLocked);
            if (diff == 1) {
                Slog.i("DisplayDeviceRepository", "Display device changed state: \"" + displayDeviceInfoLocked.name + "\", " + Display.stateToString(displayDeviceInfoLocked.state));
            } else if (diff != 8) {
                Slog.i("DisplayDeviceRepository", "Display device changed: " + displayDeviceInfoLocked);
            }
            if ((diff & 4) != 0) {
                this.mPersistentDataStore.setColorMode(displayDevice, displayDeviceInfoLocked.colorMode);
                this.mPersistentDataStore.saveIfNeeded();
            }
            displayDevice.mDebugLastLoggedDeviceInfo = displayDeviceInfoLocked;
            displayDevice.applyPendingDisplayDeviceInfoChangesLocked();
            sendChangedEventLocked(displayDevice, diff);
            if (bool.booleanValue()) {
                Trace.traceEnd(131072L);
            }
        }
    }

    public final void handleDisplayDeviceRemoved(DisplayDevice displayDevice) {
        synchronized (this.mSyncRoot) {
            DisplayDeviceInfo displayDeviceInfoLocked = displayDevice.getDisplayDeviceInfoLocked();
            if (!this.mDisplayDevices.remove(displayDevice)) {
                Slog.w("DisplayDeviceRepository", "Attempted to remove non-existent display device: " + displayDeviceInfoLocked);
                return;
            }
            Slog.i("DisplayDeviceRepository", "Display device removed: " + displayDeviceInfoLocked);
            displayDevice.mDebugLastLoggedDeviceInfo = displayDeviceInfoLocked;
            sendEventLocked(displayDevice, 3);
        }
    }

    public final void sendEventLocked(DisplayDevice displayDevice, int i) {
        int size = this.mListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mListeners.get(i2).onDisplayDeviceEventLocked(displayDevice, i);
        }
    }

    @GuardedBy({"mSyncRoot"})
    public final void sendChangedEventLocked(DisplayDevice displayDevice, int i) {
        int size = this.mListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mListeners.get(i2).onDisplayDeviceChangedLocked(displayDevice, i);
        }
    }
}
