package com.android.server.p014wm;

import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.view.SurfaceControl;
import android.window.DisplayAreaAppearedInfo;
import android.window.IDisplayAreaOrganizer;
import android.window.IDisplayAreaOrganizerController;
import android.window.WindowContainerToken;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.p014wm.DisplayArea;
import com.android.server.p014wm.DisplayAreaOrganizerController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.DisplayAreaOrganizerController */
/* loaded from: classes2.dex */
public class DisplayAreaOrganizerController extends IDisplayAreaOrganizerController.Stub {
    public final WindowManagerGlobalLock mGlobalLock;
    public int mNextTaskDisplayAreaFeatureId = 20002;
    public final HashMap<Integer, DisplayAreaOrganizerState> mOrganizersByFeatureIds = new HashMap<>();
    public final ActivityTaskManagerService mService;

    /* renamed from: com.android.server.wm.DisplayAreaOrganizerController$DeathRecipient */
    /* loaded from: classes2.dex */
    public class DeathRecipient implements IBinder.DeathRecipient {
        public int mFeature;
        public IDisplayAreaOrganizer mOrganizer;

        public DeathRecipient(IDisplayAreaOrganizer iDisplayAreaOrganizer, int i) {
            this.mOrganizer = iDisplayAreaOrganizer;
            this.mFeature = i;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (DisplayAreaOrganizerController.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    IDisplayAreaOrganizer organizerByFeature = DisplayAreaOrganizerController.this.getOrganizerByFeature(this.mFeature);
                    if (organizerByFeature != null) {
                        IBinder asBinder = organizerByFeature.asBinder();
                        if (!asBinder.equals(this.mOrganizer.asBinder()) && asBinder.isBinderAlive()) {
                            Slog.d("DisplayAreaOrganizerController", "Dead organizer replaced for feature=" + this.mFeature);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        ((DisplayAreaOrganizerState) DisplayAreaOrganizerController.this.mOrganizersByFeatureIds.remove(Integer.valueOf(this.mFeature))).destroy();
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.DisplayAreaOrganizerController$DisplayAreaOrganizerState */
    /* loaded from: classes2.dex */
    public class DisplayAreaOrganizerState {
        public final DeathRecipient mDeathRecipient;
        public final IDisplayAreaOrganizer mOrganizer;

        public DisplayAreaOrganizerState(IDisplayAreaOrganizer iDisplayAreaOrganizer, int i) {
            this.mOrganizer = iDisplayAreaOrganizer;
            DeathRecipient deathRecipient = new DeathRecipient(iDisplayAreaOrganizer, i);
            this.mDeathRecipient = deathRecipient;
            try {
                iDisplayAreaOrganizer.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException unused) {
            }
        }

        public void destroy() {
            final IBinder asBinder = this.mOrganizer.asBinder();
            DisplayAreaOrganizerController.this.mService.mRootWindowContainer.forAllDisplayAreas(new Consumer() { // from class: com.android.server.wm.DisplayAreaOrganizerController$DisplayAreaOrganizerState$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DisplayAreaOrganizerController.DisplayAreaOrganizerState.this.lambda$destroy$0(asBinder, (DisplayArea) obj);
                }
            });
            asBinder.unlinkToDeath(this.mDeathRecipient, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$destroy$0(IBinder iBinder, DisplayArea displayArea) {
            IDisplayAreaOrganizer iDisplayAreaOrganizer = displayArea.mOrganizer;
            if (iDisplayAreaOrganizer == null || !iDisplayAreaOrganizer.asBinder().equals(iBinder)) {
                return;
            }
            if (displayArea.isTaskDisplayArea() && displayArea.asTaskDisplayArea().mCreatedByOrganizer) {
                DisplayAreaOrganizerController.this.deleteTaskDisplayArea(displayArea.asTaskDisplayArea());
            } else {
                displayArea.setOrganizer(null);
            }
        }
    }

    public DisplayAreaOrganizerController(ActivityTaskManagerService activityTaskManagerService) {
        this.mService = activityTaskManagerService;
        this.mGlobalLock = activityTaskManagerService.mGlobalLock;
    }

    public final void enforceTaskPermission(String str) {
        ActivityTaskManagerService.enforceTaskPermission(str);
    }

    public IDisplayAreaOrganizer getOrganizerByFeature(int i) {
        DisplayAreaOrganizerState displayAreaOrganizerState = this.mOrganizersByFeatureIds.get(Integer.valueOf(i));
        if (displayAreaOrganizerState != null) {
            return displayAreaOrganizerState.mOrganizer;
        }
        return null;
    }

    public ParceledListSlice<DisplayAreaAppearedInfo> registerOrganizer(final IDisplayAreaOrganizer iDisplayAreaOrganizer, final int i) {
        ParceledListSlice<DisplayAreaAppearedInfo> parceledListSlice;
        enforceTaskPermission("registerOrganizer()");
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 251812577, 4, (String) null, new Object[]{String.valueOf(iDisplayAreaOrganizer.asBinder()), Long.valueOf(callingUid)});
                }
                if (this.mOrganizersByFeatureIds.get(Integer.valueOf(i)) != null) {
                    if (this.mOrganizersByFeatureIds.get(Integer.valueOf(i)).mOrganizer.asBinder().isBinderAlive()) {
                        throw new IllegalStateException("Replacing existing organizer currently unsupported");
                    }
                    this.mOrganizersByFeatureIds.remove(Integer.valueOf(i)).destroy();
                    Slog.d("DisplayAreaOrganizerController", "Replacing dead organizer for feature=" + i);
                }
                DisplayAreaOrganizerState displayAreaOrganizerState = new DisplayAreaOrganizerState(iDisplayAreaOrganizer, i);
                final ArrayList arrayList = new ArrayList();
                this.mService.mRootWindowContainer.forAllDisplays(new Consumer() { // from class: com.android.server.wm.DisplayAreaOrganizerController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DisplayAreaOrganizerController.this.lambda$registerOrganizer$1(i, arrayList, iDisplayAreaOrganizer, (DisplayContent) obj);
                    }
                });
                this.mOrganizersByFeatureIds.put(Integer.valueOf(i), displayAreaOrganizerState);
                parceledListSlice = new ParceledListSlice<>(arrayList);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return parceledListSlice;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerOrganizer$1(final int i, final List list, final IDisplayAreaOrganizer iDisplayAreaOrganizer, DisplayContent displayContent) {
        if (!displayContent.isTrusted()) {
            if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1699269281, 1, (String) null, new Object[]{Long.valueOf(displayContent.getDisplayId())});
                return;
            }
            return;
        }
        displayContent.forAllDisplayAreas(new Consumer() { // from class: com.android.server.wm.DisplayAreaOrganizerController$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DisplayAreaOrganizerController.this.lambda$registerOrganizer$0(i, list, iDisplayAreaOrganizer, (DisplayArea) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerOrganizer$0(int i, List list, IDisplayAreaOrganizer iDisplayAreaOrganizer, DisplayArea displayArea) {
        if (displayArea.mFeatureId != i) {
            return;
        }
        list.add(organizeDisplayArea(iDisplayAreaOrganizer, displayArea, "DisplayAreaOrganizerController.registerOrganizer"));
    }

    public void unregisterOrganizer(final IDisplayAreaOrganizer iDisplayAreaOrganizer) {
        enforceTaskPermission("unregisterTaskOrganizer()");
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 1149424314, 4, (String) null, new Object[]{String.valueOf(iDisplayAreaOrganizer.asBinder()), Long.valueOf(callingUid)});
                }
                this.mOrganizersByFeatureIds.entrySet().removeIf(new Predicate() { // from class: com.android.server.wm.DisplayAreaOrganizerController$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$unregisterOrganizer$2;
                        lambda$unregisterOrganizer$2 = DisplayAreaOrganizerController.lambda$unregisterOrganizer$2(iDisplayAreaOrganizer, (Map.Entry) obj);
                        return lambda$unregisterOrganizer$2;
                    }
                });
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ boolean lambda$unregisterOrganizer$2(IDisplayAreaOrganizer iDisplayAreaOrganizer, Map.Entry entry) {
        boolean equals = ((DisplayAreaOrganizerState) entry.getValue()).mOrganizer.asBinder().equals(iDisplayAreaOrganizer.asBinder());
        if (equals) {
            ((DisplayAreaOrganizerState) entry.getValue()).destroy();
        }
        return equals;
    }

    public DisplayAreaAppearedInfo createTaskDisplayArea(IDisplayAreaOrganizer iDisplayAreaOrganizer, int i, final int i2, String str) {
        TaskDisplayArea createTaskDisplayArea;
        DisplayAreaAppearedInfo organizeDisplayArea;
        enforceTaskPermission("createTaskDisplayArea()");
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -948446688, 1, (String) null, new Object[]{Long.valueOf(callingUid)});
                }
                DisplayContent displayContent = this.mService.mRootWindowContainer.getDisplayContent(i);
                if (displayContent == null) {
                    throw new IllegalArgumentException("createTaskDisplayArea unknown displayId=" + i);
                } else if (!displayContent.isTrusted()) {
                    throw new IllegalArgumentException("createTaskDisplayArea untrusted displayId=" + i);
                } else {
                    RootDisplayArea rootDisplayArea = (RootDisplayArea) displayContent.getItemFromDisplayAreas(new Function() { // from class: com.android.server.wm.DisplayAreaOrganizerController$$ExternalSyntheticLambda1
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            RootDisplayArea lambda$createTaskDisplayArea$3;
                            lambda$createTaskDisplayArea$3 = DisplayAreaOrganizerController.lambda$createTaskDisplayArea$3(i2, (DisplayArea) obj);
                            return lambda$createTaskDisplayArea$3;
                        }
                    });
                    TaskDisplayArea taskDisplayArea = rootDisplayArea == null ? (TaskDisplayArea) displayContent.getItemFromTaskDisplayAreas(new Function() { // from class: com.android.server.wm.DisplayAreaOrganizerController$$ExternalSyntheticLambda2
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            TaskDisplayArea lambda$createTaskDisplayArea$4;
                            lambda$createTaskDisplayArea$4 = DisplayAreaOrganizerController.lambda$createTaskDisplayArea$4(i2, (TaskDisplayArea) obj);
                            return lambda$createTaskDisplayArea$4;
                        }
                    }) : null;
                    if (rootDisplayArea == null && taskDisplayArea == null) {
                        throw new IllegalArgumentException("Can't find a parent DisplayArea with featureId=" + i2);
                    }
                    int i3 = this.mNextTaskDisplayAreaFeatureId;
                    this.mNextTaskDisplayAreaFeatureId = i3 + 1;
                    DisplayAreaOrganizerState displayAreaOrganizerState = new DisplayAreaOrganizerState(iDisplayAreaOrganizer, i3);
                    if (rootDisplayArea != null) {
                        createTaskDisplayArea = createTaskDisplayArea(rootDisplayArea, str, i3);
                    } else {
                        createTaskDisplayArea = createTaskDisplayArea(taskDisplayArea, str, i3);
                    }
                    organizeDisplayArea = organizeDisplayArea(iDisplayAreaOrganizer, createTaskDisplayArea, "DisplayAreaOrganizerController.createTaskDisplayArea");
                    this.mOrganizersByFeatureIds.put(Integer.valueOf(i3), displayAreaOrganizerState);
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return organizeDisplayArea;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ RootDisplayArea lambda$createTaskDisplayArea$3(int i, DisplayArea displayArea) {
        if (displayArea.asRootDisplayArea() == null || displayArea.mFeatureId != i) {
            return null;
        }
        return displayArea.asRootDisplayArea();
    }

    public static /* synthetic */ TaskDisplayArea lambda$createTaskDisplayArea$4(int i, TaskDisplayArea taskDisplayArea) {
        if (taskDisplayArea.mFeatureId == i) {
            return taskDisplayArea;
        }
        return null;
    }

    public void deleteTaskDisplayArea(WindowContainerToken windowContainerToken) {
        enforceTaskPermission("deleteTaskDisplayArea()");
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -597091183, 1, (String) null, new Object[]{Long.valueOf(callingUid)});
                }
                WindowContainer fromBinder = WindowContainer.fromBinder(windowContainerToken.asBinder());
                if (fromBinder == null || fromBinder.asTaskDisplayArea() == null) {
                    throw new IllegalArgumentException("Can't resolve TaskDisplayArea from token");
                }
                TaskDisplayArea asTaskDisplayArea = fromBinder.asTaskDisplayArea();
                if (!asTaskDisplayArea.mCreatedByOrganizer) {
                    throw new IllegalArgumentException("Attempt to delete TaskDisplayArea not created by organizer TaskDisplayArea=" + asTaskDisplayArea);
                }
                this.mOrganizersByFeatureIds.remove(Integer.valueOf(asTaskDisplayArea.mFeatureId)).destroy();
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onDisplayAreaAppeared(IDisplayAreaOrganizer iDisplayAreaOrganizer, DisplayArea displayArea) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, -1980468143, 0, (String) null, new Object[]{String.valueOf(displayArea.getName())});
        }
        try {
            iDisplayAreaOrganizer.onDisplayAreaAppeared(displayArea.getDisplayAreaInfo(), new SurfaceControl(displayArea.getSurfaceControl(), "DisplayAreaOrganizerController.onDisplayAreaAppeared"));
        } catch (RemoteException unused) {
        }
    }

    public void onDisplayAreaVanished(IDisplayAreaOrganizer iDisplayAreaOrganizer, DisplayArea displayArea) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 487621047, 0, (String) null, new Object[]{String.valueOf(displayArea.getName())});
        }
        if (!iDisplayAreaOrganizer.asBinder().isBinderAlive()) {
            Slog.d("DisplayAreaOrganizerController", "Organizer died before sending onDisplayAreaVanished");
            return;
        }
        try {
            iDisplayAreaOrganizer.onDisplayAreaVanished(displayArea.getDisplayAreaInfo());
        } catch (RemoteException unused) {
        }
    }

    public void onDisplayAreaInfoChanged(IDisplayAreaOrganizer iDisplayAreaOrganizer, DisplayArea displayArea) {
        if (ProtoLogCache.WM_DEBUG_WINDOW_ORGANIZER_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER, 174572959, 0, (String) null, new Object[]{String.valueOf(displayArea.getName())});
        }
        try {
            iDisplayAreaOrganizer.onDisplayAreaInfoChanged(displayArea.getDisplayAreaInfo());
        } catch (RemoteException unused) {
        }
    }

    public final DisplayAreaAppearedInfo organizeDisplayArea(IDisplayAreaOrganizer iDisplayAreaOrganizer, DisplayArea displayArea, String str) {
        displayArea.setOrganizer(iDisplayAreaOrganizer, true);
        return new DisplayAreaAppearedInfo(displayArea.getDisplayAreaInfo(), new SurfaceControl(displayArea.getSurfaceControl(), str));
    }

    public final TaskDisplayArea createTaskDisplayArea(final RootDisplayArea rootDisplayArea, String str, int i) {
        TaskDisplayArea taskDisplayArea = new TaskDisplayArea(rootDisplayArea.mDisplayContent, rootDisplayArea.mWmService, str, i, true);
        DisplayArea displayArea = (DisplayArea) rootDisplayArea.getItemFromDisplayAreas(new Function() { // from class: com.android.server.wm.DisplayAreaOrganizerController$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                DisplayArea lambda$createTaskDisplayArea$5;
                lambda$createTaskDisplayArea$5 = DisplayAreaOrganizerController.lambda$createTaskDisplayArea$5(RootDisplayArea.this, (DisplayArea) obj);
                return lambda$createTaskDisplayArea$5;
            }
        });
        if (displayArea == null) {
            throw new IllegalStateException("Root must either contain TDA or DAG root=" + rootDisplayArea);
        }
        WindowContainer parent = displayArea.getParent();
        parent.addChild(taskDisplayArea, parent.mChildren.indexOf(displayArea) + 1);
        return taskDisplayArea;
    }

    public static /* synthetic */ DisplayArea lambda$createTaskDisplayArea$5(RootDisplayArea rootDisplayArea, DisplayArea displayArea) {
        if (displayArea.mType != DisplayArea.Type.ANY) {
            return null;
        }
        RootDisplayArea rootDisplayArea2 = displayArea.getRootDisplayArea();
        if (rootDisplayArea2 == rootDisplayArea || rootDisplayArea2 == displayArea) {
            return displayArea;
        }
        return null;
    }

    public final TaskDisplayArea createTaskDisplayArea(TaskDisplayArea taskDisplayArea, String str, int i) {
        TaskDisplayArea taskDisplayArea2 = new TaskDisplayArea(taskDisplayArea.mDisplayContent, taskDisplayArea.mWmService, str, i, true);
        taskDisplayArea.addChild(taskDisplayArea2, Integer.MAX_VALUE);
        return taskDisplayArea2;
    }

    public final void deleteTaskDisplayArea(TaskDisplayArea taskDisplayArea) {
        taskDisplayArea.setOrganizer(null);
        this.mService.mRootWindowContainer.mTaskSupervisor.beginDeferResume();
        try {
            Task remove = taskDisplayArea.remove();
            this.mService.mRootWindowContainer.mTaskSupervisor.endDeferResume();
            taskDisplayArea.removeImmediately();
            if (remove != null) {
                remove.resumeNextFocusAfterReparent();
            }
        } catch (Throwable th) {
            this.mService.mRootWindowContainer.mTaskSupervisor.endDeferResume();
            throw th;
        }
    }
}
