package com.android.server.p014wm;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.ShortcutServiceInternal;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.MergedConfiguration;
import android.util.Slog;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;
import android.view.InputChannel;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.window.ClientWindowFrames;
import android.window.OnBackInvokedCallbackInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.logging.MetricsLoggerWrapper;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.wm.Session */
/* loaded from: classes2.dex */
public class Session extends IWindowSession.Stub implements IBinder.DeathRecipient {
    public AlertWindowNotification mAlertWindowNotification;
    public final IWindowSessionCallback mCallback;
    public final boolean mCanAddInternalSystemWindow;
    public final boolean mCanCreateSystemApplicationOverlay;
    public final boolean mCanHideNonSystemOverlayWindows;
    public final boolean mCanSetUnrestrictedGestureExclusion;
    public final boolean mCanStartTasksFromRecents;
    public final DragDropController mDragDropController;
    public float mLastReportedAnimatorScale;
    public String mPackageName;
    public final int mPid;
    public String mRelayoutTag;
    public final WindowManagerService mService;
    public final boolean mSetsUnrestrictedKeepClearAreas;
    public boolean mShowingAlertWindowNotificationAllowed;
    public final String mStringName;
    public SurfaceSession mSurfaceSession;
    public final int mUid;
    public int mNumWindow = 0;
    public final ArraySet<WindowSurfaceController> mAppOverlaySurfaces = new ArraySet<>();
    public final ArraySet<WindowSurfaceController> mAlertWindowSurfaces = new ArraySet<>();
    public boolean mClientDead = false;
    public final InsetsSourceControl.Array mDummyControls = new InsetsSourceControl.Array();

    public Session(WindowManagerService windowManagerService, IWindowSessionCallback iWindowSessionCallback) {
        this.mService = windowManagerService;
        this.mCallback = iWindowSessionCallback;
        int callingUid = Binder.getCallingUid();
        this.mUid = callingUid;
        int callingPid = Binder.getCallingPid();
        this.mPid = callingPid;
        this.mLastReportedAnimatorScale = windowManagerService.getCurrentAnimatorScale();
        this.mCanAddInternalSystemWindow = windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == 0;
        this.mCanHideNonSystemOverlayWindows = windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.HIDE_NON_SYSTEM_OVERLAY_WINDOWS") == 0 || windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.HIDE_OVERLAY_WINDOWS") == 0;
        this.mCanCreateSystemApplicationOverlay = windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.SYSTEM_APPLICATION_OVERLAY") == 0;
        this.mCanStartTasksFromRecents = windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.START_TASKS_FROM_RECENTS") == 0;
        this.mSetsUnrestrictedKeepClearAreas = windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.SET_UNRESTRICTED_KEEP_CLEAR_AREAS") == 0;
        this.mCanSetUnrestrictedGestureExclusion = windowManagerService.mContext.checkCallingOrSelfPermission("android.permission.SET_UNRESTRICTED_GESTURE_EXCLUSION") == 0;
        this.mShowingAlertWindowNotificationAllowed = windowManagerService.mShowAlertWindowNotifications;
        this.mDragDropController = windowManagerService.mDragDropController;
        StringBuilder sb = new StringBuilder();
        sb.append("Session{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" ");
        sb.append(callingPid);
        if (callingUid < 10000) {
            sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
            sb.append(callingUid);
        } else {
            sb.append(":u");
            sb.append(UserHandle.getUserId(callingUid));
            sb.append('a');
            sb.append(UserHandle.getAppId(callingUid));
        }
        sb.append("}");
        this.mStringName = sb.toString();
        try {
            iWindowSessionCallback.asBinder().linkToDeath(this, 0);
        } catch (RemoteException unused) {
        }
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(StartingSurfaceController.TAG, "Window Session Crash", e);
            }
            throw e;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mCallback.asBinder().unlinkToDeath(this, 0);
                this.mClientDead = true;
                killSessionLocked();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public int addToDisplay(IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, int i3, InputChannel inputChannel, InsetsState insetsState, InsetsSourceControl.Array array, Rect rect, float[] fArr) {
        return this.mService.addWindow(this, iWindow, layoutParams, i, i2, UserHandle.getUserId(this.mUid), i3, inputChannel, insetsState, array, rect, fArr);
    }

    public int addToDisplayAsUser(IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, int i3, int i4, InputChannel inputChannel, InsetsState insetsState, InsetsSourceControl.Array array, Rect rect, float[] fArr) {
        return this.mService.addWindow(this, iWindow, layoutParams, i, i2, i3, i4, inputChannel, insetsState, array, rect, fArr);
    }

    public int addToDisplayWithoutInputChannel(IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, InsetsState insetsState, Rect rect, float[] fArr) {
        return this.mService.addWindow(this, iWindow, layoutParams, i, i2, UserHandle.getUserId(this.mUid), WindowInsets.Type.defaultVisible(), null, insetsState, this.mDummyControls, rect, fArr);
    }

    public void remove(IWindow iWindow) {
        this.mService.removeWindow(this, iWindow);
    }

    public void prepareToReplaceWindows(IBinder iBinder, boolean z) {
        this.mService.setWillReplaceWindows(iBinder, z);
    }

    public boolean cancelDraw(IWindow iWindow) {
        return this.mService.cancelDraw(this, iWindow);
    }

    public int relayout(IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, int i3, int i4, int i5, int i6, ClientWindowFrames clientWindowFrames, MergedConfiguration mergedConfiguration, SurfaceControl surfaceControl, InsetsState insetsState, InsetsSourceControl.Array array, Bundle bundle) {
        Trace.traceBegin(32L, this.mRelayoutTag);
        int relayoutWindow = this.mService.relayoutWindow(this, iWindow, layoutParams, i, i2, i3, i4, i5, i6, clientWindowFrames, mergedConfiguration, surfaceControl, insetsState, array, bundle);
        Trace.traceEnd(32L);
        return relayoutWindow;
    }

    public void relayoutAsync(IWindow iWindow, WindowManager.LayoutParams layoutParams, int i, int i2, int i3, int i4, int i5, int i6) {
        relayout(iWindow, layoutParams, i, i2, i3, i4, i5, i6, null, null, null, null, null, null);
    }

    public boolean outOfMemory(IWindow iWindow) {
        return this.mService.outOfMemoryWindow(this, iWindow);
    }

    public void setInsets(IWindow iWindow, int i, Rect rect, Rect rect2, Region region) {
        this.mService.setInsetsWindow(this, iWindow, i, rect, rect2, region);
    }

    public void clearTouchableRegion(IWindow iWindow) {
        this.mService.clearTouchableRegion(this, iWindow);
    }

    public void finishDrawing(IWindow iWindow, SurfaceControl.Transaction transaction, int i) {
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "finishDrawing: " + this.mPackageName);
        }
        this.mService.finishDrawingWindow(this, iWindow, transaction, i);
        Trace.traceEnd(32L);
    }

    public boolean performHapticFeedback(int i, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mService.mPolicy.performHapticFeedback(this.mUid, this.mPackageName, i, z, null);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void performHapticFeedbackAsync(int i, boolean z) {
        performHapticFeedback(i, z);
    }

    public IBinder performDrag(IWindow iWindow, int i, SurfaceControl surfaceControl, int i2, float f, float f2, float f3, float f4, ClipData clipData) {
        validateAndResolveDragMimeTypeExtras(clipData, Binder.getCallingUid(), Binder.getCallingPid(), this.mPackageName);
        validateDragFlags(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mDragDropController.performDrag(this.mPid, this.mUid, iWindow, i, surfaceControl, i2, f, f2, f3, f4, clipData);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean dropForAccessibility(IWindow iWindow, int i, int i2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mDragDropController.dropForAccessibility(iWindow, i, i2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    public void validateDragFlags(int i) {
        if ((i & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0 && !this.mCanStartTasksFromRecents) {
            throw new SecurityException("Requires START_TASKS_FROM_RECENTS permission");
        }
    }

    @VisibleForTesting
    public void validateAndResolveDragMimeTypeExtras(ClipData clipData, int i, int i2, String str) {
        ClipDescription description = clipData != null ? clipData.getDescription() : null;
        if (description == null) {
            return;
        }
        boolean hasMimeType = description.hasMimeType("application/vnd.android.activity");
        boolean hasMimeType2 = description.hasMimeType("application/vnd.android.shortcut");
        boolean hasMimeType3 = description.hasMimeType("application/vnd.android.task");
        int i3 = (hasMimeType ? 1 : 0) + (hasMimeType2 ? 1 : 0) + (hasMimeType3 ? 1 : 0);
        if (i3 == 0) {
            return;
        }
        if (i3 > 1) {
            throw new IllegalArgumentException("Can not specify more than one of activity, shortcut, or task mime types");
        }
        if (clipData.getItemCount() == 0) {
            throw new IllegalArgumentException("Unexpected number of items (none)");
        }
        int i4 = 0;
        for (int i5 = 0; i5 < clipData.getItemCount(); i5++) {
            if (clipData.getItemAt(i5).getIntent() == null) {
                throw new IllegalArgumentException("Unexpected item, expected an intent");
            }
        }
        if (hasMimeType) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            while (i4 < clipData.getItemCount()) {
                try {
                    ClipData.Item itemAt = clipData.getItemAt(i4);
                    Intent intent = itemAt.getIntent();
                    PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("android.intent.extra.PENDING_INTENT");
                    UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
                    if (pendingIntent == null || userHandle == null) {
                        throw new IllegalArgumentException("Clip data must include the pending intent to launch and its associated user to launch for.");
                    }
                    itemAt.setActivityInfo(this.mService.mAtmService.resolveActivityInfoForIntent(this.mService.mAmInternal.getIntentForIntentSender(pendingIntent.getIntentSender().getTarget()), null, userHandle.getIdentifier(), i, i2));
                    i4++;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        } else if (!hasMimeType2) {
            if (hasMimeType3) {
                if (!this.mCanStartTasksFromRecents) {
                    throw new SecurityException("Requires START_TASKS_FROM_RECENTS permission");
                }
                while (i4 < clipData.getItemCount()) {
                    ClipData.Item itemAt2 = clipData.getItemAt(i4);
                    int intExtra = itemAt2.getIntent().getIntExtra("android.intent.extra.TASK_ID", -1);
                    if (intExtra == -1) {
                        throw new IllegalArgumentException("Clip item must include the task id.");
                    }
                    Task anyTaskForId = this.mService.mRoot.anyTaskForId(intExtra);
                    if (anyTaskForId == null) {
                        throw new IllegalArgumentException("Invalid task id.");
                    }
                    if (anyTaskForId.getRootActivity() != null) {
                        itemAt2.setActivityInfo(anyTaskForId.getRootActivity().info);
                    } else {
                        itemAt2.setActivityInfo(this.mService.mAtmService.resolveActivityInfoForIntent(anyTaskForId.intent, null, anyTaskForId.mUserId, i, i2));
                    }
                    i4++;
                }
            }
        } else if (!this.mCanStartTasksFromRecents) {
            throw new SecurityException("Requires START_TASKS_FROM_RECENTS permission");
        } else {
            for (int i6 = 0; i6 < clipData.getItemCount(); i6++) {
                ClipData.Item itemAt3 = clipData.getItemAt(i6);
                Intent intent2 = itemAt3.getIntent();
                String stringExtra = intent2.getStringExtra("android.intent.extra.shortcut.ID");
                String stringExtra2 = intent2.getStringExtra("android.intent.extra.PACKAGE_NAME");
                UserHandle userHandle2 = (UserHandle) intent2.getParcelableExtra("android.intent.extra.USER");
                if (TextUtils.isEmpty(stringExtra) || TextUtils.isEmpty(stringExtra2) || userHandle2 == null) {
                    throw new IllegalArgumentException("Clip item must include the package name, shortcut id, and the user to launch for.");
                }
                Intent[] createShortcutIntents = ((ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class)).createShortcutIntents(UserHandle.getUserId(i), str, stringExtra2, stringExtra, userHandle2.getIdentifier(), i2, i);
                if (createShortcutIntents == null || createShortcutIntents.length == 0) {
                    throw new IllegalArgumentException("Invalid shortcut id");
                }
                itemAt3.setActivityInfo(this.mService.mAtmService.resolveActivityInfoForIntent(createShortcutIntents[0], null, userHandle2.getIdentifier(), i, i2));
            }
        }
    }

    public void reportDropResult(IWindow iWindow, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mDragDropController.reportDropResult(iWindow, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void cancelDragAndDrop(IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mDragDropController.cancelDragAndDrop(iBinder, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dragRecipientEntered(IWindow iWindow) {
        this.mDragDropController.dragRecipientEntered(iWindow);
    }

    public void dragRecipientExited(IWindow iWindow) {
        this.mDragDropController.dragRecipientExited(iWindow);
    }

    public boolean startMovingTask(IWindow iWindow, float f, float f2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mService.mTaskPositioningController.startMovingTask(iWindow, f, f2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void finishMovingTask(IWindow iWindow) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.mTaskPositioningController.finishTaskPositioning(iWindow);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void reportSystemGestureExclusionChanged(IWindow iWindow, List<Rect> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.reportSystemGestureExclusionChanged(this, iWindow, list);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void reportKeepClearAreasChanged(IWindow iWindow, List<Rect> list, List<Rect> list2) {
        if (!this.mSetsUnrestrictedKeepClearAreas && !list2.isEmpty()) {
            list2 = Collections.emptyList();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.reportKeepClearAreasChanged(this, iWindow, list, list2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void actionOnWallpaper(IBinder iBinder, BiConsumer<WallpaperController, WindowState> biConsumer) {
        WindowState windowForClientLocked = this.mService.windowForClientLocked(this, iBinder, true);
        biConsumer.accept(windowForClientLocked.getDisplayContent().mWallpaperController, windowForClientLocked);
    }

    public void setWallpaperPosition(IBinder iBinder, final float f, final float f2, final float f3, final float f4) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                actionOnWallpaper(iBinder, new BiConsumer() { // from class: com.android.server.wm.Session$$ExternalSyntheticLambda5
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((WallpaperController) obj).setWindowWallpaperPosition((WindowState) obj2, f, f2, f3, f4);
                    }
                });
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setWallpaperZoomOut(IBinder iBinder, final float f) {
        if (Float.compare(0.0f, f) > 0 || Float.compare(1.0f, f) < 0 || Float.isNaN(f)) {
            throw new IllegalArgumentException("Zoom must be a valid float between 0 and 1: " + f);
        }
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                actionOnWallpaper(iBinder, new BiConsumer() { // from class: com.android.server.wm.Session$$ExternalSyntheticLambda3
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((WallpaperController) obj).setWallpaperZoomOut((WindowState) obj2, f);
                    }
                });
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setShouldZoomOutWallpaper(IBinder iBinder, final boolean z) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                actionOnWallpaper(iBinder, new BiConsumer() { // from class: com.android.server.wm.Session$$ExternalSyntheticLambda1
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((WallpaperController) obj).setShouldZoomOutWallpaper((WindowState) obj2, z);
                    }
                });
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void wallpaperOffsetsComplete(final IBinder iBinder) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                actionOnWallpaper(iBinder, new BiConsumer() { // from class: com.android.server.wm.Session$$ExternalSyntheticLambda4
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        WindowState windowState = (WindowState) obj2;
                        ((WallpaperController) obj).wallpaperOffsetsComplete(iBinder);
                    }
                });
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void setWallpaperDisplayOffset(IBinder iBinder, final int i, final int i2) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                actionOnWallpaper(iBinder, new BiConsumer() { // from class: com.android.server.wm.Session$$ExternalSyntheticLambda2
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((WallpaperController) obj).setWindowWallpaperDisplayOffset((WindowState) obj2, i, i2);
                    }
                });
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public Bundle sendWallpaperCommand(IBinder iBinder, String str, int i, int i2, int i3, Bundle bundle, boolean z) {
        Bundle sendWindowWallpaperCommand;
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                WindowState windowForClientLocked = this.mService.windowForClientLocked(this, iBinder, true);
                sendWindowWallpaperCommand = windowForClientLocked.getDisplayContent().mWallpaperController.sendWindowWallpaperCommand(windowForClientLocked, str, i, i2, i3, bundle, z);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return sendWindowWallpaperCommand;
    }

    public void wallpaperCommandComplete(final IBinder iBinder, Bundle bundle) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                actionOnWallpaper(iBinder, new BiConsumer() { // from class: com.android.server.wm.Session$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        WindowState windowState = (WindowState) obj2;
                        ((WallpaperController) obj).wallpaperCommandComplete(iBinder);
                    }
                });
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void onRectangleOnScreenRequested(IBinder iBinder, Rect rect) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mService.onRectangleOnScreenRequested(iBinder, rect);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public IWindowId getWindowId(IBinder iBinder) {
        return this.mService.getWindowId(iBinder);
    }

    public void pokeDrawLock(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.pokeDrawLock(this, iBinder);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updatePointerIcon(IWindow iWindow) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.updatePointerIcon(iWindow);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateTapExcludeRegion(IWindow iWindow, Region region) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.updateTapExcludeRegion(iWindow, region);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateRequestedVisibleTypes(IWindow iWindow, int i) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowState windowForClientLocked = this.mService.windowForClientLocked(this, iWindow, false);
                if (windowForClientLocked != null) {
                    windowForClientLocked.setRequestedVisibleTypes(i);
                    windowForClientLocked.getDisplayContent().getInsetsPolicy().onInsetsModified(windowForClientLocked);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public void windowAddedLocked() {
        if (this.mPackageName == null) {
            WindowProcessController process = this.mService.mAtmService.mProcessMap.getProcess(this.mPid);
            if (process != null) {
                this.mPackageName = process.mInfo.packageName;
                this.mRelayoutTag = "relayoutWindow: " + this.mPackageName;
            } else {
                Slog.e(StartingSurfaceController.TAG, "Unknown process pid=" + this.mPid);
            }
        }
        if (this.mSurfaceSession == null) {
            SurfaceSession surfaceSession = new SurfaceSession();
            this.mSurfaceSession = surfaceSession;
            if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 608694300, 0, (String) null, new Object[]{String.valueOf(surfaceSession)});
            }
            this.mService.mSessions.add(this);
            if (this.mLastReportedAnimatorScale != this.mService.getCurrentAnimatorScale()) {
                this.mService.dispatchNewAnimatorScaleLocked(this);
            }
        }
        this.mNumWindow++;
    }

    public void windowRemovedLocked() {
        this.mNumWindow--;
        killSessionLocked();
    }

    public void onWindowSurfaceVisibilityChanged(WindowSurfaceController windowSurfaceController, boolean z, int i) {
        boolean remove;
        boolean remove2;
        if (WindowManager.LayoutParams.isSystemAlertWindowType(i)) {
            if (!this.mCanAddInternalSystemWindow && !this.mCanCreateSystemApplicationOverlay) {
                if (z) {
                    remove2 = this.mAlertWindowSurfaces.add(windowSurfaceController);
                    MetricsLoggerWrapper.logAppOverlayEnter(this.mUid, this.mPackageName, remove2, i, true);
                } else {
                    remove2 = this.mAlertWindowSurfaces.remove(windowSurfaceController);
                    MetricsLoggerWrapper.logAppOverlayExit(this.mUid, this.mPackageName, remove2, i, true);
                }
                if (remove2) {
                    if (this.mAlertWindowSurfaces.isEmpty()) {
                        cancelAlertWindowNotification();
                    } else if (this.mAlertWindowNotification == null) {
                        AlertWindowNotification alertWindowNotification = new AlertWindowNotification(this.mService, this.mPackageName);
                        this.mAlertWindowNotification = alertWindowNotification;
                        if (this.mShowingAlertWindowNotificationAllowed) {
                            alertWindowNotification.post();
                        }
                    }
                }
            }
            if (i != 2038) {
                return;
            }
            if (z) {
                remove = this.mAppOverlaySurfaces.add(windowSurfaceController);
                MetricsLoggerWrapper.logAppOverlayEnter(this.mUid, this.mPackageName, remove, i, false);
            } else {
                remove = this.mAppOverlaySurfaces.remove(windowSurfaceController);
                MetricsLoggerWrapper.logAppOverlayExit(this.mUid, this.mPackageName, remove, i, false);
            }
            if (remove) {
                setHasOverlayUi(!this.mAppOverlaySurfaces.isEmpty());
            }
        }
    }

    public void setShowingAlertWindowNotificationAllowed(boolean z) {
        this.mShowingAlertWindowNotificationAllowed = z;
        AlertWindowNotification alertWindowNotification = this.mAlertWindowNotification;
        if (alertWindowNotification != null) {
            if (z) {
                alertWindowNotification.post();
            } else {
                alertWindowNotification.cancel(false);
            }
        }
    }

    public final void killSessionLocked() {
        if (this.mNumWindow > 0 || !this.mClientDead) {
            return;
        }
        this.mService.mSessions.remove(this);
        SurfaceSession surfaceSession = this.mSurfaceSession;
        if (surfaceSession == null) {
            return;
        }
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, -86763148, 0, (String) null, new Object[]{String.valueOf(surfaceSession)});
        }
        try {
            this.mSurfaceSession.kill();
        } catch (Exception e) {
            Slog.w(StartingSurfaceController.TAG, "Exception thrown when killing surface session " + this.mSurfaceSession + " in session " + this + ": " + e.toString());
        }
        this.mSurfaceSession = null;
        this.mAlertWindowSurfaces.clear();
        this.mAppOverlaySurfaces.clear();
        setHasOverlayUi(false);
        cancelAlertWindowNotification();
    }

    public final void setHasOverlayUi(boolean z) {
        this.mService.f1164mH.obtainMessage(58, this.mPid, z ? 1 : 0).sendToTarget();
    }

    public final void cancelAlertWindowNotification() {
        AlertWindowNotification alertWindowNotification = this.mAlertWindowNotification;
        if (alertWindowNotification == null) {
            return;
        }
        alertWindowNotification.cancel(true);
        this.mAlertWindowNotification = null;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("mNumWindow=");
        printWriter.print(this.mNumWindow);
        printWriter.print(" mCanAddInternalSystemWindow=");
        printWriter.print(this.mCanAddInternalSystemWindow);
        printWriter.print(" mAppOverlaySurfaces=");
        printWriter.print(this.mAppOverlaySurfaces);
        printWriter.print(" mAlertWindowSurfaces=");
        printWriter.print(this.mAlertWindowSurfaces);
        printWriter.print(" mClientDead=");
        printWriter.print(this.mClientDead);
        printWriter.print(" mSurfaceSession=");
        printWriter.println(this.mSurfaceSession);
        printWriter.print(str);
        printWriter.print("mPackageName=");
        printWriter.println(this.mPackageName);
    }

    public String toString() {
        return this.mStringName;
    }

    public boolean hasAlertWindowSurfaces(DisplayContent displayContent) {
        for (int size = this.mAlertWindowSurfaces.size() - 1; size >= 0; size--) {
            if (this.mAlertWindowSurfaces.valueAt(size).mAnimator.mWin.getDisplayContent() == displayContent) {
                return true;
            }
        }
        return false;
    }

    public void grantInputChannel(int i, SurfaceControl surfaceControl, IWindow iWindow, IBinder iBinder, int i2, int i3, int i4, IBinder iBinder2, IBinder iBinder3, String str, InputChannel inputChannel) {
        if (iBinder == null && !this.mCanAddInternalSystemWindow) {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.grantInputChannel(this, this.mUid, this.mPid, i, surfaceControl, iWindow, iBinder, i2, this.mCanAddInternalSystemWindow ? i3 : 0, i4, iBinder2, iBinder3, str, inputChannel);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void updateInputChannel(IBinder iBinder, int i, SurfaceControl surfaceControl, int i2, int i3, Region region) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            WindowManagerService windowManagerService = this.mService;
            if (!this.mCanAddInternalSystemWindow) {
                i3 = 0;
            }
            windowManagerService.updateInputChannel(iBinder, i, surfaceControl, i2, i3, region);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void grantEmbeddedWindowFocus(IWindow iWindow, IBinder iBinder, boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (iWindow == null) {
                if (!this.mCanAddInternalSystemWindow) {
                    throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
                }
                this.mService.grantEmbeddedWindowFocus(this, iBinder, z);
            } else {
                this.mService.grantEmbeddedWindowFocus(this, iWindow, iBinder, z);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean transferEmbeddedTouchFocusToHost(IWindow iWindow) {
        if (iWindow == null) {
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mService.transferEmbeddedTouchFocusToHost(iWindow);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void generateDisplayHash(IWindow iWindow, Rect rect, String str, RemoteCallback remoteCallback) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mService.generateDisplayHash(this, iWindow, rect, str, remoteCallback);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setOnBackInvokedCallbackInfo(IWindow iWindow, OnBackInvokedCallbackInfo onBackInvokedCallbackInfo) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                WindowState windowForClientLocked = this.mService.windowForClientLocked(this, iWindow, false);
                if (windowForClientLocked == null) {
                    Slog.i(StartingSurfaceController.TAG, "setOnBackInvokedCallback(): No window state for package:" + this.mPackageName);
                } else {
                    windowForClientLocked.setOnBackInvokedCallbackInfo(onBackInvokedCallbackInfo);
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }
}
