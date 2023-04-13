package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.DisplayAddress;
import android.view.DisplayInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.display.DisplayDeviceRepository;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.layout.DisplayIdProducer;
import com.android.server.display.layout.Layout;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class LogicalDisplayMapper implements DisplayDeviceRepository.Listener {
    public static int sNextNonDefaultDisplayId = 1;
    public boolean mBootCompleted;
    public Layout mCurrentLayout;
    public final SparseIntArray mDeviceDisplayGroupIds;
    public int mDeviceState;
    public int mDeviceStateToBeAppliedAfterBoot;
    public final DeviceStateToLayoutMap mDeviceStateToLayoutMap;
    public final SparseBooleanArray mDeviceStatesOnWhichToSleep;
    public final SparseBooleanArray mDeviceStatesOnWhichToWakeUp;
    public final DisplayDeviceRepository mDisplayDeviceRepo;
    public final ArrayMap<String, Integer> mDisplayGroupIdsByName;
    public final SparseArray<DisplayGroup> mDisplayGroups;
    public final SparseIntArray mDisplayGroupsToUpdate;
    public final LogicalDisplayMapperHandler mHandler;
    public final DisplayIdProducer mIdProducer;
    public boolean mInteractive;
    public final Listener mListener;
    public final SparseArray<LogicalDisplay> mLogicalDisplays;
    public final SparseIntArray mLogicalDisplaysToUpdate;
    public int mNextNonDefaultGroupId;
    public int mPendingDeviceState;
    public final PowerManager mPowerManager;
    public final boolean mSingleDisplayDemoMode;
    public final boolean mSupportsConcurrentInternalDisplays;
    public final DisplayManagerService.SyncRoot mSyncRoot;
    public final DisplayInfo mTempDisplayInfo;
    public final DisplayInfo mTempNonOverrideDisplayInfo;
    public final SparseIntArray mUpdatedDisplayGroups;
    public final SparseIntArray mUpdatedLogicalDisplays;
    public final ArrayMap<String, Integer> mVirtualDeviceDisplayMapping;

    /* loaded from: classes.dex */
    public interface Listener {
        void onDisplayGroupEventLocked(int i, int i2);

        void onLogicalDisplayEventLocked(LogicalDisplay logicalDisplay, int i);

        void onTraversalRequested();
    }

    public final int assignLayerStackLocked(int i) {
        return i;
    }

    public static /* synthetic */ int lambda$new$0(boolean z) {
        if (z) {
            return 0;
        }
        int i = sNextNonDefaultDisplayId;
        sNextNonDefaultDisplayId = i + 1;
        return i;
    }

    public LogicalDisplayMapper(Context context, DisplayDeviceRepository displayDeviceRepository, Listener listener, DisplayManagerService.SyncRoot syncRoot, Handler handler) {
        this(context, displayDeviceRepository, listener, syncRoot, handler, new DeviceStateToLayoutMap(new DisplayIdProducer() { // from class: com.android.server.display.LogicalDisplayMapper$$ExternalSyntheticLambda2
            @Override // com.android.server.display.layout.DisplayIdProducer
            public final int getId(boolean z) {
                int lambda$new$1;
                lambda$new$1 = LogicalDisplayMapper.lambda$new$1(z);
                return lambda$new$1;
            }
        }));
    }

    public static /* synthetic */ int lambda$new$1(boolean z) {
        if (z) {
            return 0;
        }
        int i = sNextNonDefaultDisplayId;
        sNextNonDefaultDisplayId = i + 1;
        return i;
    }

    public LogicalDisplayMapper(Context context, DisplayDeviceRepository displayDeviceRepository, Listener listener, DisplayManagerService.SyncRoot syncRoot, Handler handler, DeviceStateToLayoutMap deviceStateToLayoutMap) {
        this.mTempDisplayInfo = new DisplayInfo();
        this.mTempNonOverrideDisplayInfo = new DisplayInfo();
        this.mLogicalDisplays = new SparseArray<>();
        this.mDisplayGroups = new SparseArray<>();
        this.mDeviceDisplayGroupIds = new SparseIntArray();
        this.mDisplayGroupIdsByName = new ArrayMap<>();
        this.mUpdatedLogicalDisplays = new SparseIntArray();
        this.mUpdatedDisplayGroups = new SparseIntArray();
        this.mLogicalDisplaysToUpdate = new SparseIntArray();
        this.mDisplayGroupsToUpdate = new SparseIntArray();
        this.mVirtualDeviceDisplayMapping = new ArrayMap<>();
        this.mNextNonDefaultGroupId = 1;
        this.mIdProducer = new DisplayIdProducer() { // from class: com.android.server.display.LogicalDisplayMapper$$ExternalSyntheticLambda3
            @Override // com.android.server.display.layout.DisplayIdProducer
            public final int getId(boolean z) {
                int lambda$new$0;
                lambda$new$0 = LogicalDisplayMapper.lambda$new$0(z);
                return lambda$new$0;
            }
        };
        this.mCurrentLayout = null;
        this.mDeviceState = -1;
        this.mPendingDeviceState = -1;
        this.mDeviceStateToBeAppliedAfterBoot = -1;
        this.mBootCompleted = false;
        this.mSyncRoot = syncRoot;
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mPowerManager = powerManager;
        this.mInteractive = powerManager.isInteractive();
        this.mHandler = new LogicalDisplayMapperHandler(handler.getLooper());
        this.mDisplayDeviceRepo = displayDeviceRepository;
        this.mListener = listener;
        this.mSingleDisplayDemoMode = SystemProperties.getBoolean("persist.demo.singledisplay", false);
        this.mSupportsConcurrentInternalDisplays = context.getResources().getBoolean(17891827);
        this.mDeviceStatesOnWhichToWakeUp = toSparseBooleanArray(context.getResources().getIntArray(17236029));
        this.mDeviceStatesOnWhichToSleep = toSparseBooleanArray(context.getResources().getIntArray(17236028));
        displayDeviceRepository.addListener(this);
        this.mDeviceStateToLayoutMap = deviceStateToLayoutMap;
    }

    @Override // com.android.server.display.DisplayDeviceRepository.Listener
    public void onDisplayDeviceEventLocked(DisplayDevice displayDevice, int i) {
        if (i == 1) {
            handleDisplayDeviceAddedLocked(displayDevice);
        } else if (i != 3) {
        } else {
            handleDisplayDeviceRemovedLocked(displayDevice);
            updateLogicalDisplaysLocked();
        }
    }

    @Override // com.android.server.display.DisplayDeviceRepository.Listener
    public void onDisplayDeviceChangedLocked(DisplayDevice displayDevice, int i) {
        finishStateTransitionLocked(false);
        updateLogicalDisplaysLocked(i);
    }

    @Override // com.android.server.display.DisplayDeviceRepository.Listener
    public void onTraversalRequested() {
        this.mListener.onTraversalRequested();
    }

    public LogicalDisplay getDisplayLocked(int i) {
        return getDisplayLocked(i, true);
    }

    public LogicalDisplay getDisplayLocked(int i, boolean z) {
        LogicalDisplay logicalDisplay = this.mLogicalDisplays.get(i);
        if (logicalDisplay == null || logicalDisplay.isEnabledLocked() || z) {
            return logicalDisplay;
        }
        return null;
    }

    public LogicalDisplay getDisplayLocked(DisplayDevice displayDevice) {
        return getDisplayLocked(displayDevice, true);
    }

    public LogicalDisplay getDisplayLocked(DisplayDevice displayDevice, boolean z) {
        if (displayDevice == null) {
            return null;
        }
        int size = this.mLogicalDisplays.size();
        for (int i = 0; i < size; i++) {
            LogicalDisplay valueAt = this.mLogicalDisplays.valueAt(i);
            if (valueAt.getPrimaryDisplayDeviceLocked() == displayDevice) {
                if (valueAt.isEnabledLocked() || z) {
                    return valueAt;
                }
                return null;
            }
        }
        return null;
    }

    public int[] getDisplayIdsLocked(int i, boolean z) {
        int size = this.mLogicalDisplays.size();
        int[] iArr = new int[size];
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            LogicalDisplay valueAt = this.mLogicalDisplays.valueAt(i3);
            if ((valueAt.isEnabledLocked() || z) && valueAt.getDisplayInfoLocked().hasAccess(i)) {
                iArr[i2] = this.mLogicalDisplays.keyAt(i3);
                i2++;
            }
        }
        return i2 != size ? Arrays.copyOfRange(iArr, 0, i2) : iArr;
    }

    public void forEachLocked(Consumer<LogicalDisplay> consumer) {
        int size = this.mLogicalDisplays.size();
        for (int i = 0; i < size; i++) {
            consumer.accept(this.mLogicalDisplays.valueAt(i));
        }
    }

    @VisibleForTesting
    public int getDisplayGroupIdFromDisplayIdLocked(int i) {
        LogicalDisplay displayLocked = getDisplayLocked(i);
        if (displayLocked == null) {
            return -1;
        }
        int size = this.mDisplayGroups.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mDisplayGroups.valueAt(i2).containsLocked(displayLocked)) {
                return this.mDisplayGroups.keyAt(i2);
            }
        }
        return -1;
    }

    public DisplayGroup getDisplayGroupLocked(int i) {
        return this.mDisplayGroups.get(i);
    }

    public DisplayInfo getDisplayInfoForStateLocked(int i, int i2) {
        Layout.Display byId;
        Layout layout = this.mDeviceStateToLayoutMap.get(i);
        if (layout == null || (byId = layout.getById(i2)) == null) {
            return null;
        }
        DisplayDevice byAddressLocked = this.mDisplayDeviceRepo.getByAddressLocked(byId.getAddress());
        if (byAddressLocked == null) {
            Slog.w("LogicalDisplayMapper", "The display device (" + byId.getAddress() + "), is not available for the display state " + this.mDeviceState);
            return null;
        }
        LogicalDisplay displayLocked = getDisplayLocked(byAddressLocked, true);
        if (displayLocked == null) {
            Slog.w("LogicalDisplayMapper", "The logical display associated with address (" + byId.getAddress() + "), is not available for the display state " + this.mDeviceState);
            return null;
        }
        DisplayInfo displayInfo = new DisplayInfo(displayLocked.getDisplayInfoLocked());
        displayInfo.displayId = i2;
        return displayInfo;
    }

    public void dumpLocked(PrintWriter printWriter) {
        printWriter.println("LogicalDisplayMapper:");
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mSingleDisplayDemoMode=" + this.mSingleDisplayDemoMode);
        indentingPrintWriter.println("mCurrentLayout=" + this.mCurrentLayout);
        indentingPrintWriter.println("mDeviceStatesOnWhichToWakeUp=" + this.mDeviceStatesOnWhichToWakeUp);
        indentingPrintWriter.println("mDeviceStatesOnWhichToSleep=" + this.mDeviceStatesOnWhichToSleep);
        indentingPrintWriter.println("mInteractive=" + this.mInteractive);
        indentingPrintWriter.println("mBootCompleted=" + this.mBootCompleted);
        indentingPrintWriter.println();
        indentingPrintWriter.println("mDeviceState=" + this.mDeviceState);
        indentingPrintWriter.println("mPendingDeviceState=" + this.mPendingDeviceState);
        indentingPrintWriter.println("mDeviceStateToBeAppliedAfterBoot=" + this.mDeviceStateToBeAppliedAfterBoot);
        int size = this.mLogicalDisplays.size();
        indentingPrintWriter.println();
        indentingPrintWriter.println("Logical Displays: size=" + size);
        for (int i = 0; i < size; i++) {
            int keyAt = this.mLogicalDisplays.keyAt(i);
            indentingPrintWriter.println("Display " + keyAt + XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            this.mLogicalDisplays.valueAt(i).dumpLocked(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
        }
        this.mDeviceStateToLayoutMap.dumpLocked(indentingPrintWriter);
    }

    public void associateDisplayDeviceWithVirtualDevice(DisplayDevice displayDevice, int i) {
        this.mVirtualDeviceDisplayMapping.put(displayDevice.getUniqueId(), Integer.valueOf(i));
    }

    public void setDeviceStateLocked(int i, boolean z) {
        if (!this.mBootCompleted) {
            this.mDeviceStateToBeAppliedAfterBoot = i;
            return;
        }
        Slog.i("LogicalDisplayMapper", "Requesting Transition to state: " + i + ", from state=" + this.mDeviceState + ", interactive=" + this.mInteractive + ", mBootCompleted=" + this.mBootCompleted);
        resetLayoutLocked(this.mDeviceState, i, true);
        this.mPendingDeviceState = i;
        this.mDeviceStateToBeAppliedAfterBoot = -1;
        boolean shouldDeviceBeWoken = shouldDeviceBeWoken(i, this.mDeviceState, this.mInteractive, this.mBootCompleted);
        boolean shouldDeviceBePutToSleep = shouldDeviceBePutToSleep(this.mPendingDeviceState, this.mDeviceState, z, this.mInteractive, this.mBootCompleted);
        if (areAllTransitioningDisplaysOffLocked() && !shouldDeviceBeWoken && !shouldDeviceBePutToSleep) {
            transitionToPendingStateLocked();
            return;
        }
        updateLogicalDisplaysLocked();
        if (shouldDeviceBeWoken || shouldDeviceBePutToSleep) {
            if (shouldDeviceBeWoken) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.display.LogicalDisplayMapper$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LogicalDisplayMapper.this.lambda$setDeviceStateLocked$2();
                    }
                });
            } else if (shouldDeviceBePutToSleep) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.display.LogicalDisplayMapper$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        LogicalDisplayMapper.this.lambda$setDeviceStateLocked$3();
                    }
                });
            }
        }
        this.mHandler.sendEmptyMessageDelayed(1, 500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDeviceStateLocked$2() {
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 12, "server.display:unfold");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDeviceStateLocked$3() {
        this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 13, 2);
    }

    public void onBootCompleted() {
        synchronized (this.mSyncRoot) {
            this.mBootCompleted = true;
            int i = this.mDeviceStateToBeAppliedAfterBoot;
            if (i != -1) {
                setDeviceStateLocked(i, false);
            }
        }
    }

    public void onEarlyInteractivityChange(boolean z) {
        synchronized (this.mSyncRoot) {
            if (this.mInteractive != z) {
                this.mInteractive = z;
                finishStateTransitionLocked(false);
            }
        }
    }

    @VisibleForTesting
    public boolean shouldDeviceBeWoken(int i, int i2, boolean z, boolean z2) {
        return this.mDeviceStatesOnWhichToWakeUp.get(i) && !this.mDeviceStatesOnWhichToWakeUp.get(i2) && !z && z2;
    }

    @VisibleForTesting
    public boolean shouldDeviceBePutToSleep(int i, int i2, boolean z, boolean z2, boolean z3) {
        return i2 != -1 && this.mDeviceStatesOnWhichToSleep.get(i) && !this.mDeviceStatesOnWhichToSleep.get(i2) && !z && z2 && z3;
    }

    public final boolean areAllTransitioningDisplaysOffLocked() {
        DisplayDevice primaryDisplayDeviceLocked;
        int size = this.mLogicalDisplays.size();
        for (int i = 0; i < size; i++) {
            LogicalDisplay valueAt = this.mLogicalDisplays.valueAt(i);
            if (valueAt.isInTransitionLocked() && (primaryDisplayDeviceLocked = valueAt.getPrimaryDisplayDeviceLocked()) != null && primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked().state != 1) {
                return false;
            }
        }
        return true;
    }

    public final void transitionToPendingStateLocked() {
        resetLayoutLocked(this.mDeviceState, this.mPendingDeviceState, false);
        this.mDeviceState = this.mPendingDeviceState;
        this.mPendingDeviceState = -1;
        applyLayoutLocked();
        updateLogicalDisplaysLocked();
    }

    public final void finishStateTransitionLocked(boolean z) {
        int i = this.mPendingDeviceState;
        if (i == -1) {
            return;
        }
        boolean z2 = false;
        boolean z3 = this.mDeviceStatesOnWhichToWakeUp.get(i) && !this.mDeviceStatesOnWhichToWakeUp.get(this.mDeviceState) && !this.mInteractive && this.mBootCompleted;
        boolean z4 = this.mDeviceStatesOnWhichToSleep.get(this.mPendingDeviceState) && !this.mDeviceStatesOnWhichToSleep.get(this.mDeviceState) && this.mInteractive && this.mBootCompleted;
        if (areAllTransitioningDisplaysOffLocked() && !z3 && !z4) {
            z2 = true;
        }
        if (z2 || z) {
            transitionToPendingStateLocked();
            this.mHandler.removeMessages(1);
        }
    }

    public final void handleDisplayDeviceAddedLocked(DisplayDevice displayDevice) {
        if ((displayDevice.getDisplayDeviceInfoLocked().flags & 1) != 0) {
            initializeDefaultDisplayDeviceLocked(displayDevice);
        }
        createNewLogicalDisplayLocked(displayDevice, this.mIdProducer.getId(false));
        applyLayoutLocked();
        updateLogicalDisplaysLocked();
    }

    public final void handleDisplayDeviceRemovedLocked(DisplayDevice displayDevice) {
        Layout layout = this.mDeviceStateToLayoutMap.get(-1);
        Layout.Display byId = layout.getById(0);
        if (byId == null) {
            return;
        }
        DisplayDeviceInfo displayDeviceInfoLocked = displayDevice.getDisplayDeviceInfoLocked();
        this.mVirtualDeviceDisplayMapping.remove(displayDevice.getUniqueId());
        if (byId.getAddress().equals(displayDeviceInfoLocked.address)) {
            layout.removeDisplayLocked(0);
            for (int i = 0; i < this.mLogicalDisplays.size(); i++) {
                DisplayDevice primaryDisplayDeviceLocked = this.mLogicalDisplays.valueAt(i).getPrimaryDisplayDeviceLocked();
                if (primaryDisplayDeviceLocked != null) {
                    DisplayDeviceInfo displayDeviceInfoLocked2 = primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked();
                    if ((displayDeviceInfoLocked2.flags & 1) != 0 && !displayDeviceInfoLocked2.address.equals(displayDeviceInfoLocked.address)) {
                        layout.createDisplayLocked(displayDeviceInfoLocked2.address, true, true, "", this.mIdProducer, null, 0);
                        applyLayoutLocked();
                        return;
                    }
                }
            }
        }
    }

    public final void updateLogicalDisplaysLocked() {
        updateLogicalDisplaysLocked(-1);
    }

    public final void updateLogicalDisplaysLocked(int i) {
        int size = this.mLogicalDisplays.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }
            int keyAt = this.mLogicalDisplays.keyAt(size);
            LogicalDisplay valueAt = this.mLogicalDisplays.valueAt(size);
            this.mTempDisplayInfo.copyFrom(valueAt.getDisplayInfoLocked());
            valueAt.getNonOverrideDisplayInfoLocked(this.mTempNonOverrideDisplayInfo);
            valueAt.updateLocked(this.mDisplayDeviceRepo);
            DisplayInfo displayInfoLocked = valueAt.getDisplayInfoLocked();
            int i2 = this.mUpdatedLogicalDisplays.get(keyAt, 0);
            boolean z = i2 != 0;
            if (!valueAt.isValidLocked()) {
                this.mUpdatedLogicalDisplays.delete(keyAt);
                DisplayGroup displayGroupLocked = getDisplayGroupLocked(getDisplayGroupIdFromDisplayIdLocked(keyAt));
                if (displayGroupLocked != null) {
                    displayGroupLocked.removeDisplayLocked(valueAt);
                }
                if (z) {
                    Slog.i("LogicalDisplayMapper", "Removing display: " + keyAt);
                    this.mLogicalDisplaysToUpdate.put(keyAt, 3);
                } else {
                    this.mLogicalDisplays.removeAt(size);
                }
            } else {
                if (!z) {
                    Slog.i("LogicalDisplayMapper", "Adding new display: " + keyAt + ": " + displayInfoLocked);
                    assignDisplayGroupLocked(valueAt);
                    this.mLogicalDisplaysToUpdate.put(keyAt, 1);
                } else if (!TextUtils.equals(this.mTempDisplayInfo.uniqueId, displayInfoLocked.uniqueId)) {
                    assignDisplayGroupLocked(valueAt);
                    this.mLogicalDisplaysToUpdate.put(keyAt, 4);
                } else {
                    if (!this.mTempDisplayInfo.equals(displayInfoLocked)) {
                        assignDisplayGroupLocked(valueAt);
                        if (i == 8) {
                            this.mLogicalDisplaysToUpdate.put(keyAt, 7);
                        } else {
                            this.mLogicalDisplaysToUpdate.put(keyAt, 2);
                        }
                    } else if (i2 == 1) {
                        this.mLogicalDisplaysToUpdate.put(keyAt, 6);
                    } else if (!valueAt.getPendingFrameRateOverrideUids().isEmpty()) {
                        this.mLogicalDisplaysToUpdate.put(keyAt, 5);
                    } else {
                        valueAt.getNonOverrideDisplayInfoLocked(this.mTempDisplayInfo);
                        if (!this.mTempNonOverrideDisplayInfo.equals(this.mTempDisplayInfo)) {
                            this.mLogicalDisplaysToUpdate.put(keyAt, 2);
                        }
                    }
                    this.mUpdatedLogicalDisplays.put(keyAt, 2);
                }
                this.mUpdatedLogicalDisplays.put(keyAt, 2);
            }
            size--;
        }
        for (int size2 = this.mDisplayGroups.size() - 1; size2 >= 0; size2--) {
            int keyAt2 = this.mDisplayGroups.keyAt(size2);
            DisplayGroup valueAt2 = this.mDisplayGroups.valueAt(size2);
            boolean z2 = this.mUpdatedDisplayGroups.indexOfKey(keyAt2) > -1;
            int changeCountLocked = valueAt2.getChangeCountLocked();
            if (valueAt2.isEmptyLocked()) {
                this.mUpdatedDisplayGroups.delete(keyAt2);
                if (z2) {
                    this.mDisplayGroupsToUpdate.put(keyAt2, 3);
                }
            } else {
                if (!z2) {
                    this.mDisplayGroupsToUpdate.put(keyAt2, 1);
                } else if (this.mUpdatedDisplayGroups.get(keyAt2) != changeCountLocked) {
                    this.mDisplayGroupsToUpdate.put(keyAt2, 2);
                }
                this.mUpdatedDisplayGroups.put(keyAt2, changeCountLocked);
            }
        }
        sendUpdatesForDisplaysLocked(6);
        sendUpdatesForGroupsLocked(1);
        sendUpdatesForDisplaysLocked(3);
        sendUpdatesForDisplaysLocked(2);
        sendUpdatesForDisplaysLocked(5);
        sendUpdatesForDisplaysLocked(4);
        sendUpdatesForDisplaysLocked(1);
        sendUpdatesForDisplaysLocked(7);
        sendUpdatesForGroupsLocked(2);
        sendUpdatesForGroupsLocked(3);
        this.mLogicalDisplaysToUpdate.clear();
        this.mDisplayGroupsToUpdate.clear();
    }

    public final void sendUpdatesForDisplaysLocked(int i) {
        for (int size = this.mLogicalDisplaysToUpdate.size() - 1; size >= 0; size--) {
            if (this.mLogicalDisplaysToUpdate.valueAt(size) == i) {
                int keyAt = this.mLogicalDisplaysToUpdate.keyAt(size);
                this.mListener.onLogicalDisplayEventLocked(getDisplayLocked(keyAt), i);
                if (i == 3) {
                    this.mLogicalDisplays.delete(keyAt);
                }
            }
        }
    }

    public final void sendUpdatesForGroupsLocked(int i) {
        for (int size = this.mDisplayGroupsToUpdate.size() - 1; size >= 0; size--) {
            if (this.mDisplayGroupsToUpdate.valueAt(size) == i) {
                int keyAt = this.mDisplayGroupsToUpdate.keyAt(size);
                this.mListener.onDisplayGroupEventLocked(keyAt, i);
                if (i == 3) {
                    this.mDisplayGroups.delete(keyAt);
                    int indexOfValue = this.mDeviceDisplayGroupIds.indexOfValue(keyAt);
                    if (indexOfValue >= 0) {
                        this.mDeviceDisplayGroupIds.removeAt(indexOfValue);
                    }
                }
            }
        }
    }

    public final void assignDisplayGroupLocked(LogicalDisplay logicalDisplay) {
        int displayIdLocked = logicalDisplay.getDisplayIdLocked();
        Integer num = this.mVirtualDeviceDisplayMapping.get(logicalDisplay.getPrimaryDisplayDeviceLocked().getUniqueId());
        int displayGroupIdFromDisplayIdLocked = getDisplayGroupIdFromDisplayIdLocked(displayIdLocked);
        Integer valueOf = (num == null || this.mDeviceDisplayGroupIds.indexOfKey(num.intValue()) <= 0) ? null : Integer.valueOf(this.mDeviceDisplayGroupIds.get(num.intValue()));
        DisplayGroup displayGroupLocked = getDisplayGroupLocked(displayGroupIdFromDisplayIdLocked);
        boolean needsOwnDisplayGroupLocked = logicalDisplay.needsOwnDisplayGroupLocked();
        boolean z = true;
        boolean z2 = displayGroupIdFromDisplayIdLocked != 0;
        boolean z3 = (needsOwnDisplayGroupLocked || num == null) ? false : true;
        if (valueOf == null || displayGroupIdFromDisplayIdLocked != valueOf.intValue()) {
            z = false;
        }
        if (displayGroupIdFromDisplayIdLocked == -1 || z2 != needsOwnDisplayGroupLocked || z != z3) {
            displayGroupIdFromDisplayIdLocked = assignDisplayGroupIdLocked(needsOwnDisplayGroupLocked, logicalDisplay.getDisplayGroupNameLocked(), z3, num);
        }
        DisplayGroup displayGroupLocked2 = getDisplayGroupLocked(displayGroupIdFromDisplayIdLocked);
        if (displayGroupLocked2 == null) {
            displayGroupLocked2 = new DisplayGroup(displayGroupIdFromDisplayIdLocked);
            this.mDisplayGroups.append(displayGroupIdFromDisplayIdLocked, displayGroupLocked2);
        }
        if (displayGroupLocked != displayGroupLocked2) {
            if (displayGroupLocked != null) {
                displayGroupLocked.removeDisplayLocked(logicalDisplay);
            }
            displayGroupLocked2.addDisplayLocked(logicalDisplay);
            logicalDisplay.updateDisplayGroupIdLocked(displayGroupIdFromDisplayIdLocked);
            StringBuilder sb = new StringBuilder();
            sb.append("Setting new display group ");
            sb.append(displayGroupIdFromDisplayIdLocked);
            sb.append(" for display ");
            sb.append(displayIdLocked);
            sb.append(", from previous group: ");
            sb.append(displayGroupLocked != null ? Integer.valueOf(displayGroupLocked.getGroupId()) : "null");
            Slog.i("LogicalDisplayMapper", sb.toString());
        }
    }

    public final void resetLayoutLocked(int i, int i2, boolean z) {
        Layout layout = this.mDeviceStateToLayoutMap.get(i);
        Layout layout2 = this.mDeviceStateToLayoutMap.get(i2);
        int size = this.mLogicalDisplays.size();
        for (int i3 = 0; i3 < size; i3++) {
            LogicalDisplay valueAt = this.mLogicalDisplays.valueAt(i3);
            int displayIdLocked = valueAt.getDisplayIdLocked();
            DisplayDevice primaryDisplayDeviceLocked = valueAt.getPrimaryDisplayDeviceLocked();
            if (primaryDisplayDeviceLocked != null) {
                DisplayAddress displayAddress = primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked().address;
                Layout.Display byAddress = displayAddress != null ? layout.getByAddress(displayAddress) : null;
                Layout.Display byAddress2 = displayAddress != null ? layout2.getByAddress(displayAddress) : null;
                if (valueAt.isInTransitionLocked() || (byAddress == null || byAddress.isEnabled()) != (byAddress2 == null || byAddress2.isEnabled()) || (byAddress != null && byAddress2 != null && byAddress.getLogicalDisplayId() != byAddress2.getLogicalDisplayId()) || ((byAddress == null) != (byAddress2 == null))) {
                    if (z != valueAt.isInTransitionLocked()) {
                        Slog.i("LogicalDisplayMapper", "Set isInTransition on display " + displayIdLocked + ": " + z);
                    }
                    valueAt.setIsInTransitionLocked(z);
                    this.mUpdatedLogicalDisplays.put(displayIdLocked, 1);
                }
            }
        }
    }

    public final void applyLayoutLocked() {
        Layout layout = this.mCurrentLayout;
        this.mCurrentLayout = this.mDeviceStateToLayoutMap.get(this.mDeviceState);
        Slog.i("LogicalDisplayMapper", "Applying layout: " + this.mCurrentLayout + ", Previous layout: " + layout);
        int size = this.mCurrentLayout.size();
        for (int i = 0; i < size; i++) {
            Layout.Display at = this.mCurrentLayout.getAt(i);
            DisplayAddress address = at.getAddress();
            DisplayDevice byAddressLocked = this.mDisplayDeviceRepo.getByAddressLocked(address);
            if (byAddressLocked == null) {
                Slog.w("LogicalDisplayMapper", "The display device (" + address + "), is not available for the display state " + this.mDeviceState);
            } else {
                int logicalDisplayId = at.getLogicalDisplayId();
                LogicalDisplay displayLocked = getDisplayLocked(logicalDisplayId);
                if (displayLocked == null) {
                    displayLocked = createNewLogicalDisplayLocked(null, logicalDisplayId);
                }
                LogicalDisplay displayLocked2 = getDisplayLocked(byAddressLocked);
                if (displayLocked != displayLocked2) {
                    displayLocked.swapDisplaysLocked(displayLocked2);
                }
                DisplayDeviceConfig displayDeviceConfig = byAddressLocked.getDisplayDeviceConfig();
                displayLocked.setDevicePositionLocked(at.getPosition());
                displayLocked.setLeadDisplayLocked(at.getLeadDisplayId());
                displayLocked.updateLayoutLimitedRefreshRateLocked(displayDeviceConfig.getRefreshRange(at.getRefreshRateZoneId()));
                displayLocked.updateRefreshRateThermalThrottling(displayDeviceConfig.getRefreshRateThrottlingData(at.getRefreshRateThermalThrottlingMapId()));
                setEnabledLocked(displayLocked, at.isEnabled());
                displayLocked.setBrightnessThrottlingDataIdLocked(at.getBrightnessThrottlingMapId() == null ? "default" : at.getBrightnessThrottlingMapId());
                displayLocked.setDisplayGroupNameLocked(at.getDisplayGroupName());
            }
        }
    }

    public final LogicalDisplay createNewLogicalDisplayLocked(DisplayDevice displayDevice, int i) {
        LogicalDisplay logicalDisplay = new LogicalDisplay(i, assignLayerStackLocked(i), displayDevice);
        logicalDisplay.updateLocked(this.mDisplayDeviceRepo);
        if (logicalDisplay.getDisplayInfoLocked().type == 1 && this.mDeviceStateToLayoutMap.size() > 1) {
            logicalDisplay.setEnabledLocked(false);
        }
        this.mLogicalDisplays.put(i, logicalDisplay);
        return logicalDisplay;
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0010, code lost:
        if (r3 != 1) goto L5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setEnabledLocked(LogicalDisplay logicalDisplay, boolean z) {
        boolean z2;
        int displayIdLocked = logicalDisplay.getDisplayIdLocked();
        DisplayInfo displayInfoLocked = logicalDisplay.getDisplayInfoLocked();
        if (this.mSingleDisplayDemoMode) {
            int i = displayInfoLocked.type;
            z2 = true;
        }
        z2 = false;
        if (z && z2) {
            Slog.i("LogicalDisplayMapper", "Not creating a logical display for a secondary display because single display demo mode is enabled: " + logicalDisplay.getDisplayInfoLocked());
            z = false;
        }
        if (logicalDisplay.isEnabledLocked() != z) {
            Slog.i("LogicalDisplayMapper", "SetEnabled on display " + displayIdLocked + ": " + z);
            logicalDisplay.setEnabledLocked(z);
        }
    }

    public final int assignDisplayGroupIdLocked(boolean z, String str, boolean z2, Integer num) {
        if (z2 && num != null) {
            int i = this.mDeviceDisplayGroupIds.get(num.intValue());
            if (i == 0) {
                int i2 = this.mNextNonDefaultGroupId;
                this.mNextNonDefaultGroupId = i2 + 1;
                this.mDeviceDisplayGroupIds.put(num.intValue(), i2);
                return i2;
            }
            return i;
        } else if (z) {
            Integer num2 = this.mDisplayGroupIdsByName.get(str);
            if (num2 == null) {
                int i3 = this.mNextNonDefaultGroupId;
                this.mNextNonDefaultGroupId = i3 + 1;
                num2 = Integer.valueOf(i3);
                this.mDisplayGroupIdsByName.put(str, num2);
            }
            return num2.intValue();
        } else {
            return 0;
        }
    }

    public final void initializeDefaultDisplayDeviceLocked(DisplayDevice displayDevice) {
        Layout layout = this.mDeviceStateToLayoutMap.get(-1);
        if (layout.getById(0) != null) {
            return;
        }
        layout.createDisplayLocked(displayDevice.getDisplayDeviceInfoLocked().address, true, true, "", this.mIdProducer, null, -1);
    }

    public final SparseBooleanArray toSparseBooleanArray(int[] iArr) {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(2);
        for (int i = 0; iArr != null && i < iArr.length; i++) {
            sparseBooleanArray.put(iArr[i], true);
        }
        return sparseBooleanArray;
    }

    /* loaded from: classes.dex */
    public class LogicalDisplayMapperHandler extends Handler {
        public LogicalDisplayMapperHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            synchronized (LogicalDisplayMapper.this.mSyncRoot) {
                LogicalDisplayMapper.this.finishStateTransitionLocked(true);
            }
        }
    }
}
