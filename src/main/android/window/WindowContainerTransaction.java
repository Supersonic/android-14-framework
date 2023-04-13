package android.window;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.p001pm.ShortcutInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.view.SurfaceControl;
import android.window.ITaskFragmentOrganizer;
import android.window.TaskFragmentOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class WindowContainerTransaction implements Parcelable {
    public static final Parcelable.Creator<WindowContainerTransaction> CREATOR = new Parcelable.Creator<WindowContainerTransaction>() { // from class: android.window.WindowContainerTransaction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WindowContainerTransaction createFromParcel(Parcel in) {
            return new WindowContainerTransaction(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WindowContainerTransaction[] newArray(int size) {
            return new WindowContainerTransaction[size];
        }
    };
    private final ArrayMap<IBinder, Change> mChanges;
    private IBinder mErrorCallbackToken;
    private final ArrayList<HierarchyOp> mHierarchyOps;
    private ITaskFragmentOrganizer mTaskFragmentOrganizer;

    public WindowContainerTransaction() {
        this.mChanges = new ArrayMap<>();
        this.mHierarchyOps = new ArrayList<>();
    }

    private WindowContainerTransaction(Parcel in) {
        ArrayMap<IBinder, Change> arrayMap = new ArrayMap<>();
        this.mChanges = arrayMap;
        ArrayList<HierarchyOp> arrayList = new ArrayList<>();
        this.mHierarchyOps = arrayList;
        in.readMap(arrayMap, null);
        in.readTypedList(arrayList, HierarchyOp.CREATOR);
        this.mErrorCallbackToken = in.readStrongBinder();
        this.mTaskFragmentOrganizer = ITaskFragmentOrganizer.Stub.asInterface(in.readStrongBinder());
    }

    private Change getOrCreateChange(IBinder token) {
        Change out = this.mChanges.get(token);
        if (out == null) {
            Change out2 = new Change();
            this.mChanges.put(token, out2);
            return out2;
        }
        return out;
    }

    public WindowContainerTransaction setBounds(WindowContainerToken container, Rect bounds) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mConfiguration.windowConfiguration.setBounds(bounds);
        chg.mConfigSetMask |= 536870912;
        chg.mWindowSetMask |= 1;
        return this;
    }

    public WindowContainerTransaction setAppBounds(WindowContainerToken container, Rect appBounds) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mConfiguration.windowConfiguration.setAppBounds(appBounds);
        chg.mConfigSetMask |= 536870912;
        chg.mWindowSetMask |= 2;
        return this;
    }

    public WindowContainerTransaction setScreenSizeDp(WindowContainerToken container, int w, int h) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mConfiguration.screenWidthDp = w;
        chg.mConfiguration.screenHeightDp = h;
        chg.mConfigSetMask |= 1024;
        return this;
    }

    public WindowContainerTransaction scheduleFinishEnterPip(WindowContainerToken container, Rect bounds) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mPinnedBounds = new Rect(bounds);
        chg.mChangeMask |= 4;
        return this;
    }

    public WindowContainerTransaction setBoundsChangeTransaction(WindowContainerToken container, SurfaceControl.Transaction t) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mBoundsChangeTransaction = t;
        chg.mChangeMask |= 2;
        return this;
    }

    public WindowContainerTransaction setBoundsChangeTransaction(WindowContainerToken task, Rect surfaceBounds) {
        Change chg = getOrCreateChange(task.asBinder());
        if (chg.mBoundsChangeSurfaceBounds == null) {
            chg.mBoundsChangeSurfaceBounds = new Rect();
        }
        chg.mBoundsChangeSurfaceBounds.set(surfaceBounds);
        chg.mChangeMask |= 16;
        return this;
    }

    public WindowContainerTransaction setActivityWindowingMode(WindowContainerToken container, int windowingMode) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mActivityWindowingMode = windowingMode;
        return this;
    }

    public WindowContainerTransaction setWindowingMode(WindowContainerToken container, int windowingMode) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mWindowingMode = windowingMode;
        return this;
    }

    public WindowContainerTransaction setFocusable(WindowContainerToken container, boolean focusable) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mFocusable = focusable;
        chg.mChangeMask |= 1;
        return this;
    }

    public WindowContainerTransaction setHidden(WindowContainerToken container, boolean hidden) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mHidden = hidden;
        chg.mChangeMask |= 8;
        return this;
    }

    public WindowContainerTransaction setSmallestScreenWidthDp(WindowContainerToken container, int widthDp) {
        Change cfg = getOrCreateChange(container.asBinder());
        cfg.mConfiguration.smallestScreenWidthDp = widthDp;
        cfg.mConfigSetMask |= 2048;
        return this;
    }

    public WindowContainerTransaction setIgnoreOrientationRequest(WindowContainerToken container, boolean ignoreOrientationRequest) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mIgnoreOrientationRequest = ignoreOrientationRequest;
        chg.mChangeMask |= 32;
        return this;
    }

    public WindowContainerTransaction setForceTranslucent(WindowContainerToken container, boolean forceTranslucent) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mForceTranslucent = forceTranslucent;
        chg.mChangeMask |= 128;
        return this;
    }

    public WindowContainerTransaction setDoNotPip(WindowContainerToken container) {
        Change chg = getOrCreateChange(container.asBinder());
        chg.mChangeMask |= 64;
        return this;
    }

    public WindowContainerTransaction setRelativeBounds(WindowContainerToken container, Rect relBounds) {
        Change chg = getOrCreateChange(container.asBinder());
        if (chg.mRelativeBounds == null) {
            chg.mRelativeBounds = new Rect();
        }
        chg.mRelativeBounds.set(relBounds);
        chg.mChangeMask |= 512;
        chg.mConfigSetMask |= 536870912;
        chg.mWindowSetMask |= 1;
        return this;
    }

    public WindowContainerTransaction reparent(WindowContainerToken child, WindowContainerToken parent, boolean onTop) {
        this.mHierarchyOps.add(HierarchyOp.createForReparent(child.asBinder(), parent == null ? null : parent.asBinder(), onTop));
        return this;
    }

    public WindowContainerTransaction reorder(WindowContainerToken child, boolean onTop) {
        this.mHierarchyOps.add(HierarchyOp.createForReorder(child.asBinder(), onTop));
        return this;
    }

    public WindowContainerTransaction reparentTasks(WindowContainerToken currentParent, WindowContainerToken newParent, int[] windowingModes, int[] activityTypes, boolean onTop, boolean reparentTopOnly) {
        this.mHierarchyOps.add(HierarchyOp.createForChildrenTasksReparent(currentParent != null ? currentParent.asBinder() : null, newParent != null ? newParent.asBinder() : null, windowingModes, activityTypes, onTop, reparentTopOnly));
        return this;
    }

    public WindowContainerTransaction reparentTasks(WindowContainerToken currentParent, WindowContainerToken newParent, int[] windowingModes, int[] activityTypes, boolean onTop) {
        return reparentTasks(currentParent, newParent, windowingModes, activityTypes, onTop, false);
    }

    public WindowContainerTransaction setLaunchRoot(WindowContainerToken container, int[] windowingModes, int[] activityTypes) {
        this.mHierarchyOps.add(HierarchyOp.createForSetLaunchRoot(container.asBinder(), windowingModes, activityTypes));
        return this;
    }

    public WindowContainerTransaction setAdjacentRoots(WindowContainerToken root1, WindowContainerToken root2) {
        this.mHierarchyOps.add(HierarchyOp.createForAdjacentRoots(root1.asBinder(), root2.asBinder()));
        return this;
    }

    public WindowContainerTransaction setLaunchAdjacentFlagRoot(WindowContainerToken container) {
        this.mHierarchyOps.add(HierarchyOp.createForSetLaunchAdjacentFlagRoot(container.asBinder(), false));
        return this;
    }

    public WindowContainerTransaction clearLaunchAdjacentFlagRoot(WindowContainerToken container) {
        this.mHierarchyOps.add(HierarchyOp.createForSetLaunchAdjacentFlagRoot(container.asBinder(), true));
        return this;
    }

    public WindowContainerTransaction startTask(int taskId, Bundle options) {
        this.mHierarchyOps.add(HierarchyOp.createForTaskLaunch(taskId, options));
        return this;
    }

    public WindowContainerTransaction removeTask(WindowContainerToken containerToken) {
        this.mHierarchyOps.add(HierarchyOp.createForRemoveTask(containerToken.asBinder()));
        return this;
    }

    public WindowContainerTransaction setDragResizing(WindowContainerToken container, boolean dragResizing) {
        Change change = getOrCreateChange(container.asBinder());
        change.mChangeMask |= 256;
        change.mDragResizing = dragResizing;
        return this;
    }

    public WindowContainerTransaction sendPendingIntent(PendingIntent sender, Intent intent, Bundle options) {
        this.mHierarchyOps.add(new HierarchyOp.Builder(7).setLaunchOptions(options).setPendingIntent(sender).setActivityIntent(intent).build());
        return this;
    }

    public WindowContainerTransaction startShortcut(String callingPackage, ShortcutInfo shortcutInfo, Bundle options) {
        this.mHierarchyOps.add(HierarchyOp.createForStartShortcut(callingPackage, shortcutInfo, options));
        return this;
    }

    public WindowContainerTransaction createTaskFragment(TaskFragmentCreationParams taskFragmentCreationParams) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(0).setTaskFragmentCreationParams(taskFragmentCreationParams).build();
        return addTaskFragmentOperation(taskFragmentCreationParams.getFragmentToken(), operation);
    }

    public WindowContainerTransaction deleteTaskFragment(IBinder fragmentToken) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(1).build();
        return addTaskFragmentOperation(fragmentToken, operation);
    }

    public WindowContainerTransaction startActivityInTaskFragment(IBinder fragmentToken, IBinder callerToken, Intent activityIntent, Bundle activityOptions) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(2).setActivityToken(callerToken).setActivityIntent(activityIntent).setBundle(activityOptions).build();
        return addTaskFragmentOperation(fragmentToken, operation);
    }

    public WindowContainerTransaction reparentActivityToTaskFragment(IBinder fragmentToken, IBinder activityToken) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(3).setActivityToken(activityToken).build();
        return addTaskFragmentOperation(fragmentToken, operation);
    }

    public WindowContainerTransaction setAdjacentTaskFragments(IBinder fragmentToken1, IBinder fragmentToken2, TaskFragmentAdjacentParams params) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(4).setSecondaryFragmentToken(fragmentToken2).setBundle(params != null ? params.toBundle() : null).build();
        return addTaskFragmentOperation(fragmentToken1, operation);
    }

    public WindowContainerTransaction clearAdjacentTaskFragments(IBinder fragmentToken) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(5).build();
        return addTaskFragmentOperation(fragmentToken, operation);
    }

    public WindowContainerTransaction restoreTransientOrder(WindowContainerToken container) {
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(9).setContainer(container.asBinder()).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public WindowContainerTransaction addRectInsetsProvider(WindowContainerToken receiverWindowContainer, Rect insetsProviderFrame, int[] insetsTypes) {
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(10).setContainer(receiverWindowContainer.asBinder()).setInsetsProviderFrame(insetsProviderFrame).setInsetsTypes(insetsTypes).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public WindowContainerTransaction removeInsetsProvider(WindowContainerToken receiverWindowContainer, int[] insetsTypes) {
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(11).setContainer(receiverWindowContainer.asBinder()).setInsetsTypes(insetsTypes).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public WindowContainerTransaction requestFocusOnTaskFragment(IBinder fragmentToken) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(6).build();
        return addTaskFragmentOperation(fragmentToken, operation);
    }

    public WindowContainerTransaction finishActivity(IBinder activityToken) {
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(14).setContainer(activityToken).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public WindowContainerTransaction setCompanionTaskFragment(IBinder fragmentToken, IBinder companionFragmentToken) {
        TaskFragmentOperation operation = new TaskFragmentOperation.Builder(7).setSecondaryFragmentToken(companionFragmentToken).build();
        return addTaskFragmentOperation(fragmentToken, operation);
    }

    public WindowContainerTransaction addTaskFragmentOperation(IBinder fragmentToken, TaskFragmentOperation taskFragmentOperation) {
        Objects.requireNonNull(fragmentToken);
        Objects.requireNonNull(taskFragmentOperation);
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(17).setContainer(fragmentToken).setTaskFragmentOperation(taskFragmentOperation).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public WindowContainerTransaction setAlwaysOnTop(WindowContainerToken windowContainer, boolean alwaysOnTop) {
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(12).setContainer(windowContainer.asBinder()).setAlwaysOnTop(alwaysOnTop).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public WindowContainerTransaction setErrorCallbackToken(IBinder errorCallbackToken) {
        if (this.mErrorCallbackToken != null) {
            throw new IllegalStateException("Can't set multiple error token for one transaction.");
        }
        this.mErrorCallbackToken = errorCallbackToken;
        return this;
    }

    public WindowContainerTransaction setTaskFragmentOrganizer(ITaskFragmentOrganizer organizer) {
        this.mTaskFragmentOrganizer = organizer;
        return this;
    }

    public WindowContainerTransaction clearAdjacentRoots(WindowContainerToken root) {
        this.mHierarchyOps.add(HierarchyOp.createForClearAdjacentRoots(root.asBinder()));
        return this;
    }

    public WindowContainerTransaction setReparentLeafTaskIfRelaunch(WindowContainerToken windowContainer, boolean reparentLeafTaskIfRelaunch) {
        HierarchyOp hierarchyOp = new HierarchyOp.Builder(16).setContainer(windowContainer.asBinder()).setReparentLeafTaskIfRelaunch(reparentLeafTaskIfRelaunch).build();
        this.mHierarchyOps.add(hierarchyOp);
        return this;
    }

    public void merge(WindowContainerTransaction other, boolean transfer) {
        IBinder taskFragmentOrganizerAsBinder;
        IBinder iBinder;
        int n = other.mChanges.size();
        for (int i = 0; i < n; i++) {
            IBinder key = other.mChanges.keyAt(i);
            Change existing = this.mChanges.get(key);
            if (existing == null) {
                existing = new Change();
                this.mChanges.put(key, existing);
            }
            existing.merge(other.mChanges.valueAt(i), transfer);
        }
        int n2 = other.mHierarchyOps.size();
        for (int i2 = 0; i2 < n2; i2++) {
            this.mHierarchyOps.add(transfer ? other.mHierarchyOps.get(i2) : new HierarchyOp(other.mHierarchyOps.get(i2)));
        }
        IBinder iBinder2 = this.mErrorCallbackToken;
        if (iBinder2 != null && (iBinder = other.mErrorCallbackToken) != null && iBinder2 != iBinder) {
            throw new IllegalArgumentException("Can't merge two WCTs with different error token");
        }
        ITaskFragmentOrganizer iTaskFragmentOrganizer = this.mTaskFragmentOrganizer;
        if (iTaskFragmentOrganizer != null) {
            taskFragmentOrganizerAsBinder = iTaskFragmentOrganizer.asBinder();
        } else {
            taskFragmentOrganizerAsBinder = null;
        }
        ITaskFragmentOrganizer iTaskFragmentOrganizer2 = other.mTaskFragmentOrganizer;
        IBinder otherTaskFragmentOrganizerAsBinder = iTaskFragmentOrganizer2 != null ? iTaskFragmentOrganizer2.asBinder() : null;
        if (!Objects.equals(taskFragmentOrganizerAsBinder, otherTaskFragmentOrganizerAsBinder)) {
            throw new IllegalArgumentException("Can't merge two WCTs from different TaskFragmentOrganizers");
        }
        IBinder iBinder3 = this.mErrorCallbackToken;
        if (iBinder3 == null) {
            iBinder3 = other.mErrorCallbackToken;
        }
        this.mErrorCallbackToken = iBinder3;
    }

    public boolean isEmpty() {
        return this.mChanges.isEmpty() && this.mHierarchyOps.isEmpty();
    }

    public Map<IBinder, Change> getChanges() {
        return this.mChanges;
    }

    public List<HierarchyOp> getHierarchyOps() {
        return this.mHierarchyOps;
    }

    public IBinder getErrorCallbackToken() {
        return this.mErrorCallbackToken;
    }

    public ITaskFragmentOrganizer getTaskFragmentOrganizer() {
        return this.mTaskFragmentOrganizer;
    }

    public String toString() {
        return "WindowContainerTransaction { changes = " + this.mChanges + " hops = " + this.mHierarchyOps + " errorCallbackToken=" + this.mErrorCallbackToken + " taskFragmentOrganizer=" + this.mTaskFragmentOrganizer + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(this.mChanges);
        dest.writeTypedList(this.mHierarchyOps);
        dest.writeStrongBinder(this.mErrorCallbackToken);
        dest.writeStrongInterface(this.mTaskFragmentOrganizer);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes4.dex */
    public static class Change implements Parcelable {
        public static final int CHANGE_BOUNDS_TRANSACTION = 2;
        public static final int CHANGE_BOUNDS_TRANSACTION_RECT = 16;
        public static final int CHANGE_DRAG_RESIZING = 256;
        public static final int CHANGE_FOCUSABLE = 1;
        public static final int CHANGE_FORCE_NO_PIP = 64;
        public static final int CHANGE_FORCE_TRANSLUCENT = 128;
        public static final int CHANGE_HIDDEN = 8;
        public static final int CHANGE_IGNORE_ORIENTATION_REQUEST = 32;
        public static final int CHANGE_PIP_CALLBACK = 4;
        public static final int CHANGE_RELATIVE_BOUNDS = 512;
        public static final Parcelable.Creator<Change> CREATOR = new Parcelable.Creator<Change>() { // from class: android.window.WindowContainerTransaction.Change.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Change createFromParcel(Parcel in) {
                return new Change(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Change[] newArray(int size) {
                return new Change[size];
            }
        };
        private int mActivityWindowingMode;
        private Rect mBoundsChangeSurfaceBounds;
        private SurfaceControl.Transaction mBoundsChangeTransaction;
        private int mChangeMask;
        private int mConfigSetMask;
        private final Configuration mConfiguration;
        private boolean mDragResizing;
        private boolean mFocusable;
        private boolean mForceTranslucent;
        private boolean mHidden;
        private boolean mIgnoreOrientationRequest;
        private Rect mPinnedBounds;
        private Rect mRelativeBounds;
        private int mWindowSetMask;
        private int mWindowingMode;

        public Change() {
            this.mConfiguration = new Configuration();
            this.mFocusable = true;
            this.mHidden = false;
            this.mIgnoreOrientationRequest = false;
            this.mForceTranslucent = false;
            this.mDragResizing = false;
            this.mChangeMask = 0;
            this.mConfigSetMask = 0;
            this.mWindowSetMask = 0;
            this.mPinnedBounds = null;
            this.mBoundsChangeTransaction = null;
            this.mBoundsChangeSurfaceBounds = null;
            this.mRelativeBounds = null;
            this.mActivityWindowingMode = -1;
            this.mWindowingMode = -1;
        }

        protected Change(Parcel in) {
            Configuration configuration = new Configuration();
            this.mConfiguration = configuration;
            this.mFocusable = true;
            this.mHidden = false;
            this.mIgnoreOrientationRequest = false;
            this.mForceTranslucent = false;
            this.mDragResizing = false;
            this.mChangeMask = 0;
            this.mConfigSetMask = 0;
            this.mWindowSetMask = 0;
            this.mPinnedBounds = null;
            this.mBoundsChangeTransaction = null;
            this.mBoundsChangeSurfaceBounds = null;
            this.mRelativeBounds = null;
            this.mActivityWindowingMode = -1;
            this.mWindowingMode = -1;
            configuration.readFromParcel(in);
            this.mFocusable = in.readBoolean();
            this.mHidden = in.readBoolean();
            this.mIgnoreOrientationRequest = in.readBoolean();
            this.mForceTranslucent = in.readBoolean();
            this.mDragResizing = in.readBoolean();
            this.mChangeMask = in.readInt();
            this.mConfigSetMask = in.readInt();
            this.mWindowSetMask = in.readInt();
            if ((this.mChangeMask & 4) != 0) {
                Rect rect = new Rect();
                this.mPinnedBounds = rect;
                rect.readFromParcel(in);
            }
            if ((this.mChangeMask & 2) != 0) {
                this.mBoundsChangeTransaction = SurfaceControl.Transaction.CREATOR.createFromParcel(in);
            }
            if ((this.mChangeMask & 16) != 0) {
                Rect rect2 = new Rect();
                this.mBoundsChangeSurfaceBounds = rect2;
                rect2.readFromParcel(in);
            }
            if ((this.mChangeMask & 512) != 0) {
                Rect rect3 = new Rect();
                this.mRelativeBounds = rect3;
                rect3.readFromParcel(in);
            }
            this.mWindowingMode = in.readInt();
            this.mActivityWindowingMode = in.readInt();
        }

        public void merge(Change other, boolean transfer) {
            this.mConfiguration.setTo(other.mConfiguration, other.mConfigSetMask, other.mWindowSetMask);
            this.mConfigSetMask |= other.mConfigSetMask;
            this.mWindowSetMask |= other.mWindowSetMask;
            int i = other.mChangeMask;
            if ((i & 1) != 0) {
                this.mFocusable = other.mFocusable;
            }
            if (transfer && (i & 2) != 0) {
                this.mBoundsChangeTransaction = other.mBoundsChangeTransaction;
                other.mBoundsChangeTransaction = null;
            }
            if ((i & 4) != 0) {
                this.mPinnedBounds = transfer ? other.mPinnedBounds : new Rect(other.mPinnedBounds);
            }
            int i2 = other.mChangeMask;
            if ((i2 & 8) != 0) {
                this.mHidden = other.mHidden;
            }
            if ((i2 & 32) != 0) {
                this.mIgnoreOrientationRequest = other.mIgnoreOrientationRequest;
            }
            if ((i2 & 128) != 0) {
                this.mForceTranslucent = other.mForceTranslucent;
            }
            if ((i2 & 256) != 0) {
                this.mDragResizing = other.mDragResizing;
            }
            this.mChangeMask = i2 | this.mChangeMask;
            int i3 = other.mActivityWindowingMode;
            if (i3 >= 0) {
                this.mActivityWindowingMode = i3;
            }
            int i4 = other.mWindowingMode;
            if (i4 >= 0) {
                this.mWindowingMode = i4;
            }
            Rect rect = other.mBoundsChangeSurfaceBounds;
            if (rect != null) {
                if (!transfer) {
                    rect = new Rect(other.mBoundsChangeSurfaceBounds);
                }
                this.mBoundsChangeSurfaceBounds = rect;
            }
            Rect rect2 = other.mRelativeBounds;
            if (rect2 != null) {
                if (!transfer) {
                    rect2 = new Rect(other.mRelativeBounds);
                }
                this.mRelativeBounds = rect2;
            }
        }

        public int getWindowingMode() {
            return this.mWindowingMode;
        }

        public int getActivityWindowingMode() {
            return this.mActivityWindowingMode;
        }

        public Configuration getConfiguration() {
            return this.mConfiguration;
        }

        public boolean getFocusable() {
            if ((this.mChangeMask & 1) == 0) {
                throw new RuntimeException("Focusable not set. check CHANGE_FOCUSABLE first");
            }
            return this.mFocusable;
        }

        public boolean getHidden() {
            if ((this.mChangeMask & 8) == 0) {
                throw new RuntimeException("Hidden not set. check CHANGE_HIDDEN first");
            }
            return this.mHidden;
        }

        public boolean getIgnoreOrientationRequest() {
            if ((this.mChangeMask & 32) == 0) {
                throw new RuntimeException("IgnoreOrientationRequest not set. Check CHANGE_IGNORE_ORIENTATION_REQUEST first");
            }
            return this.mIgnoreOrientationRequest;
        }

        public boolean getForceTranslucent() {
            if ((this.mChangeMask & 128) == 0) {
                throw new RuntimeException("Force translucent not set. Check CHANGE_FORCE_TRANSLUCENT first");
            }
            return this.mForceTranslucent;
        }

        public boolean getDragResizing() {
            if ((this.mChangeMask & 256) == 0) {
                throw new RuntimeException("Drag resizing not set. Check CHANGE_DRAG_RESIZING first");
            }
            return this.mDragResizing;
        }

        public int getChangeMask() {
            return this.mChangeMask;
        }

        public int getConfigSetMask() {
            return this.mConfigSetMask;
        }

        public int getWindowSetMask() {
            return this.mWindowSetMask;
        }

        public Rect getEnterPipBounds() {
            return this.mPinnedBounds;
        }

        public SurfaceControl.Transaction getBoundsChangeTransaction() {
            return this.mBoundsChangeTransaction;
        }

        public Rect getBoundsChangeSurfaceBounds() {
            return this.mBoundsChangeSurfaceBounds;
        }

        public Rect getRelativeBounds() {
            return this.mRelativeBounds;
        }

        public String toString() {
            int i = this.mConfigSetMask;
            boolean changesBounds = ((i & 536870912) == 0 || (this.mWindowSetMask & 1) == 0) ? false : true;
            boolean changesAppBounds = ((536870912 & i) == 0 || (this.mWindowSetMask & 2) == 0) ? false : true;
            boolean changesSs = (i & 1024) != 0;
            boolean changesSss = (i & 2048) != 0;
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            if (changesBounds) {
                sb.append("bounds:" + this.mConfiguration.windowConfiguration.getBounds() + ",");
            }
            if (changesAppBounds) {
                sb.append("appbounds:" + this.mConfiguration.windowConfiguration.getAppBounds() + ",");
            }
            if (changesSss) {
                sb.append("ssw:" + this.mConfiguration.smallestScreenWidthDp + ",");
            }
            if (changesSs) {
                sb.append("sw/h:" + this.mConfiguration.screenWidthDp + "x" + this.mConfiguration.screenHeightDp + ",");
            }
            if ((1 & this.mChangeMask) != 0) {
                sb.append("focusable:" + this.mFocusable + ",");
            }
            if ((this.mChangeMask & 256) != 0) {
                sb.append("dragResizing:" + this.mDragResizing + ",");
            }
            if (this.mBoundsChangeTransaction != null) {
                sb.append("hasBoundsTransaction,");
            }
            if ((this.mChangeMask & 32) != 0) {
                sb.append("ignoreOrientationRequest:" + this.mIgnoreOrientationRequest + ",");
            }
            if ((this.mChangeMask & 512) != 0) {
                sb.append("relativeBounds:").append(this.mRelativeBounds).append(",");
            }
            sb.append("}");
            return sb.toString();
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            this.mConfiguration.writeToParcel(dest, flags);
            dest.writeBoolean(this.mFocusable);
            dest.writeBoolean(this.mHidden);
            dest.writeBoolean(this.mIgnoreOrientationRequest);
            dest.writeBoolean(this.mForceTranslucent);
            dest.writeBoolean(this.mDragResizing);
            dest.writeInt(this.mChangeMask);
            dest.writeInt(this.mConfigSetMask);
            dest.writeInt(this.mWindowSetMask);
            Rect rect = this.mPinnedBounds;
            if (rect != null) {
                rect.writeToParcel(dest, flags);
            }
            SurfaceControl.Transaction transaction = this.mBoundsChangeTransaction;
            if (transaction != null) {
                transaction.writeToParcel(dest, flags);
            }
            Rect rect2 = this.mBoundsChangeSurfaceBounds;
            if (rect2 != null) {
                rect2.writeToParcel(dest, flags);
            }
            Rect rect3 = this.mRelativeBounds;
            if (rect3 != null) {
                rect3.writeToParcel(dest, flags);
            }
            dest.writeInt(this.mWindowingMode);
            dest.writeInt(this.mActivityWindowingMode);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public static final class HierarchyOp implements Parcelable {
        public static final Parcelable.Creator<HierarchyOp> CREATOR = new Parcelable.Creator<HierarchyOp>() { // from class: android.window.WindowContainerTransaction.HierarchyOp.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public HierarchyOp createFromParcel(Parcel in) {
                return new HierarchyOp(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public HierarchyOp[] newArray(int size) {
                return new HierarchyOp[size];
            }
        };
        public static final int HIERARCHY_OP_TYPE_ADD_RECT_INSETS_PROVIDER = 10;
        public static final int HIERARCHY_OP_TYPE_ADD_TASK_FRAGMENT_OPERATION = 17;
        public static final int HIERARCHY_OP_TYPE_CHILDREN_TASKS_REPARENT = 2;
        public static final int HIERARCHY_OP_TYPE_CLEAR_ADJACENT_ROOTS = 15;
        public static final int HIERARCHY_OP_TYPE_FINISH_ACTIVITY = 14;
        public static final int HIERARCHY_OP_TYPE_LAUNCH_TASK = 5;
        public static final int HIERARCHY_OP_TYPE_PENDING_INTENT = 7;
        public static final int HIERARCHY_OP_TYPE_REMOVE_INSETS_PROVIDER = 11;
        public static final int HIERARCHY_OP_TYPE_REMOVE_TASK = 13;
        public static final int HIERARCHY_OP_TYPE_REORDER = 1;
        public static final int HIERARCHY_OP_TYPE_REPARENT = 0;
        public static final int HIERARCHY_OP_TYPE_RESTORE_TRANSIENT_ORDER = 9;
        public static final int HIERARCHY_OP_TYPE_SET_ADJACENT_ROOTS = 4;
        public static final int HIERARCHY_OP_TYPE_SET_ALWAYS_ON_TOP = 12;
        public static final int HIERARCHY_OP_TYPE_SET_LAUNCH_ADJACENT_FLAG_ROOT = 6;
        public static final int HIERARCHY_OP_TYPE_SET_LAUNCH_ROOT = 3;
        public static final int HIERARCHY_OP_TYPE_SET_REPARENT_LEAF_TASK_IF_RELAUNCH = 16;
        public static final int HIERARCHY_OP_TYPE_START_SHORTCUT = 8;
        public static final String LAUNCH_KEY_SHORTCUT_CALLING_PACKAGE = "android:transaction.hop.shortcut_calling_package";
        public static final String LAUNCH_KEY_TASK_ID = "android:transaction.hop.taskId";
        private Intent mActivityIntent;
        private int[] mActivityTypes;
        private boolean mAlwaysOnTop;
        private IBinder mContainer;
        private Rect mInsetsProviderFrame;
        private int[] mInsetsTypes;
        private Bundle mLaunchOptions;
        private PendingIntent mPendingIntent;
        private IBinder mReparent;
        private boolean mReparentLeafTaskIfRelaunch;
        private boolean mReparentTopOnly;
        private ShortcutInfo mShortcutInfo;
        private TaskFragmentOperation mTaskFragmentOperation;
        private boolean mToTop;
        private final int mType;
        private int[] mWindowingModes;

        public static HierarchyOp createForReparent(IBinder container, IBinder reparent, boolean toTop) {
            return new Builder(0).setContainer(container).setReparentContainer(reparent).setToTop(toTop).build();
        }

        public static HierarchyOp createForReorder(IBinder container, boolean toTop) {
            return new Builder(1).setContainer(container).setReparentContainer(container).setToTop(toTop).build();
        }

        public static HierarchyOp createForChildrenTasksReparent(IBinder currentParent, IBinder newParent, int[] windowingModes, int[] activityTypes, boolean onTop, boolean reparentTopOnly) {
            return new Builder(2).setContainer(currentParent).setReparentContainer(newParent).setWindowingModes(windowingModes).setActivityTypes(activityTypes).setToTop(onTop).setReparentTopOnly(reparentTopOnly).build();
        }

        public static HierarchyOp createForSetLaunchRoot(IBinder container, int[] windowingModes, int[] activityTypes) {
            return new Builder(3).setContainer(container).setWindowingModes(windowingModes).setActivityTypes(activityTypes).build();
        }

        public static HierarchyOp createForAdjacentRoots(IBinder root1, IBinder root2) {
            return new Builder(4).setContainer(root1).setReparentContainer(root2).build();
        }

        public static HierarchyOp createForTaskLaunch(int taskId, Bundle options) {
            Bundle fullOptions = options == null ? new Bundle() : options;
            fullOptions.putInt(LAUNCH_KEY_TASK_ID, taskId);
            return new Builder(5).setToTop(true).setLaunchOptions(fullOptions).build();
        }

        public static HierarchyOp createForStartShortcut(String callingPackage, ShortcutInfo shortcutInfo, Bundle options) {
            Bundle fullOptions = options == null ? new Bundle() : options;
            fullOptions.putString(LAUNCH_KEY_SHORTCUT_CALLING_PACKAGE, callingPackage);
            return new Builder(8).setShortcutInfo(shortcutInfo).setLaunchOptions(fullOptions).build();
        }

        public static HierarchyOp createForSetLaunchAdjacentFlagRoot(IBinder container, boolean clearRoot) {
            return new Builder(6).setContainer(container).setToTop(clearRoot).build();
        }

        public static HierarchyOp createForRemoveTask(IBinder container) {
            return new Builder(13).setContainer(container).build();
        }

        public static HierarchyOp createForClearAdjacentRoots(IBinder root) {
            return new Builder(15).setContainer(root).build();
        }

        private HierarchyOp(int type) {
            this.mType = type;
        }

        public HierarchyOp(HierarchyOp copy) {
            this.mType = copy.mType;
            this.mContainer = copy.mContainer;
            this.mReparent = copy.mReparent;
            this.mInsetsTypes = copy.mInsetsTypes;
            this.mInsetsProviderFrame = copy.mInsetsProviderFrame;
            this.mToTop = copy.mToTop;
            this.mReparentTopOnly = copy.mReparentTopOnly;
            this.mWindowingModes = copy.mWindowingModes;
            this.mActivityTypes = copy.mActivityTypes;
            this.mLaunchOptions = copy.mLaunchOptions;
            this.mActivityIntent = copy.mActivityIntent;
            this.mTaskFragmentOperation = copy.mTaskFragmentOperation;
            this.mPendingIntent = copy.mPendingIntent;
            this.mShortcutInfo = copy.mShortcutInfo;
            this.mAlwaysOnTop = copy.mAlwaysOnTop;
            this.mReparentLeafTaskIfRelaunch = copy.mReparentLeafTaskIfRelaunch;
        }

        protected HierarchyOp(Parcel in) {
            this.mType = in.readInt();
            this.mContainer = in.readStrongBinder();
            this.mReparent = in.readStrongBinder();
            this.mInsetsTypes = in.createIntArray();
            if (in.readInt() != 0) {
                this.mInsetsProviderFrame = Rect.CREATOR.createFromParcel(in);
            } else {
                this.mInsetsProviderFrame = null;
            }
            this.mToTop = in.readBoolean();
            this.mReparentTopOnly = in.readBoolean();
            this.mWindowingModes = in.createIntArray();
            this.mActivityTypes = in.createIntArray();
            this.mLaunchOptions = in.readBundle();
            this.mActivityIntent = (Intent) in.readTypedObject(Intent.CREATOR);
            this.mTaskFragmentOperation = (TaskFragmentOperation) in.readTypedObject(TaskFragmentOperation.CREATOR);
            this.mPendingIntent = (PendingIntent) in.readTypedObject(PendingIntent.CREATOR);
            this.mShortcutInfo = (ShortcutInfo) in.readTypedObject(ShortcutInfo.CREATOR);
            this.mAlwaysOnTop = in.readBoolean();
            this.mReparentLeafTaskIfRelaunch = in.readBoolean();
        }

        public int getType() {
            return this.mType;
        }

        public boolean isReparent() {
            return this.mType == 0;
        }

        public IBinder getNewParent() {
            return this.mReparent;
        }

        public int[] getInsetsTypes() {
            return this.mInsetsTypes;
        }

        public Rect getInsetsProviderFrame() {
            return this.mInsetsProviderFrame;
        }

        public IBinder getContainer() {
            return this.mContainer;
        }

        public IBinder getAdjacentRoot() {
            return this.mReparent;
        }

        public boolean getToTop() {
            return this.mToTop;
        }

        public boolean getReparentTopOnly() {
            return this.mReparentTopOnly;
        }

        public int[] getWindowingModes() {
            return this.mWindowingModes;
        }

        public int[] getActivityTypes() {
            return this.mActivityTypes;
        }

        public Bundle getLaunchOptions() {
            return this.mLaunchOptions;
        }

        public Intent getActivityIntent() {
            return this.mActivityIntent;
        }

        public boolean isAlwaysOnTop() {
            return this.mAlwaysOnTop;
        }

        public boolean isReparentLeafTaskIfRelaunch() {
            return this.mReparentLeafTaskIfRelaunch;
        }

        public TaskFragmentOperation getTaskFragmentOperation() {
            return this.mTaskFragmentOperation;
        }

        public PendingIntent getPendingIntent() {
            return this.mPendingIntent;
        }

        public ShortcutInfo getShortcutInfo() {
            return this.mShortcutInfo;
        }

        public String toString() {
            switch (this.mType) {
                case 0:
                    return "{reparent: " + this.mContainer + " to " + (this.mToTop ? "top of " : "bottom of ") + this.mReparent + "}";
                case 1:
                    return "{reorder: " + this.mContainer + " to " + (this.mToTop ? "top" : "bottom") + "}";
                case 2:
                    return "{ChildrenTasksReparent: from=" + this.mContainer + " to=" + this.mReparent + " mToTop=" + this.mToTop + " mReparentTopOnly=" + this.mReparentTopOnly + " mWindowingMode=" + Arrays.toString(this.mWindowingModes) + " mActivityType=" + Arrays.toString(this.mActivityTypes) + "}";
                case 3:
                    return "{SetLaunchRoot: container=" + this.mContainer + " mWindowingMode=" + Arrays.toString(this.mWindowingModes) + " mActivityType=" + Arrays.toString(this.mActivityTypes) + "}";
                case 4:
                    return "{SetAdjacentRoot: container=" + this.mContainer + " adjacentRoot=" + this.mReparent + "}";
                case 5:
                    return "{LaunchTask: " + this.mLaunchOptions + "}";
                case 6:
                    return "{SetAdjacentFlagRoot: container=" + this.mContainer + " clearRoot=" + this.mToTop + "}";
                case 7:
                case 9:
                default:
                    return "{mType=" + this.mType + " container=" + this.mContainer + " reparent=" + this.mReparent + " mToTop=" + this.mToTop + " mWindowingMode=" + Arrays.toString(this.mWindowingModes) + " mActivityType=" + Arrays.toString(this.mActivityTypes) + "}";
                case 8:
                    return "{StartShortcut: options=" + this.mLaunchOptions + " info=" + this.mShortcutInfo + "}";
                case 10:
                    return "{addRectInsetsProvider: container=" + this.mContainer + " insetsProvidingFrame=" + this.mInsetsProviderFrame + " insetsType=" + Arrays.toString(this.mInsetsTypes) + "}";
                case 11:
                    return "{removeLocalInsetsProvider: container=" + this.mContainer + " insetsType=" + Arrays.toString(this.mInsetsTypes) + "}";
                case 12:
                    return "{setAlwaysOnTop: container=" + this.mContainer + " alwaysOnTop=" + this.mAlwaysOnTop + "}";
                case 13:
                    return "{RemoveTask: task=" + this.mContainer + "}";
                case 14:
                    return "{finishActivity: activity=" + this.mContainer + "}";
                case 15:
                    return "{ClearAdjacentRoot: container=" + this.mContainer + "}";
                case 16:
                    return "{setReparentLeafTaskIfRelaunch: container= " + this.mContainer + " reparentLeafTaskIfRelaunch= " + this.mReparentLeafTaskIfRelaunch + "}";
                case 17:
                    return "{addTaskFragmentOperation: fragmentToken= " + this.mContainer + " operation= " + this.mTaskFragmentOperation + "}";
            }
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mType);
            dest.writeStrongBinder(this.mContainer);
            dest.writeStrongBinder(this.mReparent);
            dest.writeIntArray(this.mInsetsTypes);
            if (this.mInsetsProviderFrame != null) {
                dest.writeInt(1);
                this.mInsetsProviderFrame.writeToParcel(dest, 0);
            } else {
                dest.writeInt(0);
            }
            dest.writeBoolean(this.mToTop);
            dest.writeBoolean(this.mReparentTopOnly);
            dest.writeIntArray(this.mWindowingModes);
            dest.writeIntArray(this.mActivityTypes);
            dest.writeBundle(this.mLaunchOptions);
            dest.writeTypedObject(this.mActivityIntent, flags);
            dest.writeTypedObject(this.mTaskFragmentOperation, flags);
            dest.writeTypedObject(this.mPendingIntent, flags);
            dest.writeTypedObject(this.mShortcutInfo, flags);
            dest.writeBoolean(this.mAlwaysOnTop);
            dest.writeBoolean(this.mReparentLeafTaskIfRelaunch);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Builder {
            private Intent mActivityIntent;
            private int[] mActivityTypes;
            private boolean mAlwaysOnTop;
            private IBinder mContainer;
            private Rect mInsetsProviderFrame;
            private int[] mInsetsTypes;
            private Bundle mLaunchOptions;
            private PendingIntent mPendingIntent;
            private IBinder mReparent;
            private boolean mReparentLeafTaskIfRelaunch;
            private boolean mReparentTopOnly;
            private ShortcutInfo mShortcutInfo;
            private TaskFragmentOperation mTaskFragmentOperation;
            private boolean mToTop;
            private final int mType;
            private int[] mWindowingModes;

            Builder(int type) {
                this.mType = type;
            }

            Builder setContainer(IBinder container) {
                this.mContainer = container;
                return this;
            }

            Builder setReparentContainer(IBinder reparentContainer) {
                this.mReparent = reparentContainer;
                return this;
            }

            Builder setInsetsTypes(int[] insetsTypes) {
                this.mInsetsTypes = insetsTypes;
                return this;
            }

            Builder setInsetsProviderFrame(Rect insetsProviderFrame) {
                this.mInsetsProviderFrame = insetsProviderFrame;
                return this;
            }

            Builder setToTop(boolean toTop) {
                this.mToTop = toTop;
                return this;
            }

            Builder setReparentTopOnly(boolean reparentTopOnly) {
                this.mReparentTopOnly = reparentTopOnly;
                return this;
            }

            Builder setWindowingModes(int[] windowingModes) {
                this.mWindowingModes = windowingModes;
                return this;
            }

            Builder setActivityTypes(int[] activityTypes) {
                this.mActivityTypes = activityTypes;
                return this;
            }

            Builder setLaunchOptions(Bundle launchOptions) {
                this.mLaunchOptions = launchOptions;
                return this;
            }

            Builder setActivityIntent(Intent activityIntent) {
                this.mActivityIntent = activityIntent;
                return this;
            }

            Builder setPendingIntent(PendingIntent sender) {
                this.mPendingIntent = sender;
                return this;
            }

            Builder setAlwaysOnTop(boolean alwaysOnTop) {
                this.mAlwaysOnTop = alwaysOnTop;
                return this;
            }

            Builder setTaskFragmentOperation(TaskFragmentOperation taskFragmentOperation) {
                this.mTaskFragmentOperation = taskFragmentOperation;
                return this;
            }

            Builder setReparentLeafTaskIfRelaunch(boolean reparentLeafTaskIfRelaunch) {
                this.mReparentLeafTaskIfRelaunch = reparentLeafTaskIfRelaunch;
                return this;
            }

            Builder setShortcutInfo(ShortcutInfo shortcutInfo) {
                this.mShortcutInfo = shortcutInfo;
                return this;
            }

            HierarchyOp build() {
                int[] iArr;
                HierarchyOp hierarchyOp = new HierarchyOp(this.mType);
                hierarchyOp.mContainer = this.mContainer;
                hierarchyOp.mReparent = this.mReparent;
                int[] iArr2 = this.mWindowingModes;
                if (iArr2 != null) {
                    iArr = Arrays.copyOf(iArr2, iArr2.length);
                } else {
                    iArr = null;
                }
                hierarchyOp.mWindowingModes = iArr;
                int[] iArr3 = this.mActivityTypes;
                hierarchyOp.mActivityTypes = iArr3 != null ? Arrays.copyOf(iArr3, iArr3.length) : null;
                hierarchyOp.mInsetsTypes = this.mInsetsTypes;
                hierarchyOp.mInsetsProviderFrame = this.mInsetsProviderFrame;
                hierarchyOp.mToTop = this.mToTop;
                hierarchyOp.mReparentTopOnly = this.mReparentTopOnly;
                hierarchyOp.mLaunchOptions = this.mLaunchOptions;
                hierarchyOp.mActivityIntent = this.mActivityIntent;
                hierarchyOp.mPendingIntent = this.mPendingIntent;
                hierarchyOp.mAlwaysOnTop = this.mAlwaysOnTop;
                hierarchyOp.mTaskFragmentOperation = this.mTaskFragmentOperation;
                hierarchyOp.mShortcutInfo = this.mShortcutInfo;
                hierarchyOp.mReparentLeafTaskIfRelaunch = this.mReparentLeafTaskIfRelaunch;
                return hierarchyOp;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class TaskFragmentAdjacentParams {
        private static final String DELAY_PRIMARY_LAST_ACTIVITY_REMOVAL = "android:transaction.adjacent.option.delay_primary_removal";
        private static final String DELAY_SECONDARY_LAST_ACTIVITY_REMOVAL = "android:transaction.adjacent.option.delay_secondary_removal";
        private boolean mDelayPrimaryLastActivityRemoval;
        private boolean mDelaySecondaryLastActivityRemoval;

        public TaskFragmentAdjacentParams() {
        }

        public TaskFragmentAdjacentParams(Bundle bundle) {
            this.mDelayPrimaryLastActivityRemoval = bundle.getBoolean(DELAY_PRIMARY_LAST_ACTIVITY_REMOVAL);
            this.mDelaySecondaryLastActivityRemoval = bundle.getBoolean(DELAY_SECONDARY_LAST_ACTIVITY_REMOVAL);
        }

        public void setShouldDelayPrimaryLastActivityRemoval(boolean delay) {
            this.mDelayPrimaryLastActivityRemoval = delay;
        }

        public void setShouldDelaySecondaryLastActivityRemoval(boolean delay) {
            this.mDelaySecondaryLastActivityRemoval = delay;
        }

        public boolean shouldDelayPrimaryLastActivityRemoval() {
            return this.mDelayPrimaryLastActivityRemoval;
        }

        public boolean shouldDelaySecondaryLastActivityRemoval() {
            return this.mDelaySecondaryLastActivityRemoval;
        }

        Bundle toBundle() {
            Bundle b = new Bundle();
            b.putBoolean(DELAY_PRIMARY_LAST_ACTIVITY_REMOVAL, this.mDelayPrimaryLastActivityRemoval);
            b.putBoolean(DELAY_SECONDARY_LAST_ACTIVITY_REMOVAL, this.mDelaySecondaryLastActivityRemoval);
            return b;
        }
    }
}
