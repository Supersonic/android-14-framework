package com.android.server.p014wm;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.FastPrintWriter;
import com.android.server.p014wm.SurfaceAnimator;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.RemoteAnimationController */
/* loaded from: classes2.dex */
public class RemoteAnimationController implements IBinder.DeathRecipient {
    public boolean mCanceled;
    public final DisplayContent mDisplayContent;
    public FinishedCallback mFinishedCallback;
    public final Handler mHandler;
    public final boolean mIsActivityEmbedding;
    public boolean mIsFinishing;
    public boolean mLinkedToDeathOfRunner;
    public Runnable mOnRemoteAnimationReady;
    public final RemoteAnimationAdapter mRemoteAnimationAdapter;
    public final WindowManagerService mService;
    public final ArrayList<RemoteAnimationRecord> mPendingAnimations = new ArrayList<>();
    public final ArrayList<WallpaperAnimationAdapter> mPendingWallpaperAnimations = new ArrayList<>();
    @VisibleForTesting
    final ArrayList<NonAppWindowAnimationAdapter> mPendingNonAppAnimations = new ArrayList<>();
    public final Runnable mTimeoutRunnable = new Runnable() { // from class: com.android.server.wm.RemoteAnimationController$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            RemoteAnimationController.this.lambda$new$0();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        cancelAnimation("timeoutRunnable");
    }

    public RemoteAnimationController(WindowManagerService windowManagerService, DisplayContent displayContent, RemoteAnimationAdapter remoteAnimationAdapter, Handler handler, boolean z) {
        this.mService = windowManagerService;
        this.mDisplayContent = displayContent;
        this.mRemoteAnimationAdapter = remoteAnimationAdapter;
        this.mHandler = handler;
        this.mIsActivityEmbedding = z;
    }

    public RemoteAnimationRecord createRemoteAnimationRecord(WindowContainer windowContainer, Point point, Rect rect, Rect rect2, Rect rect3, boolean z) {
        return createRemoteAnimationRecord(windowContainer, point, rect, rect2, rect3, z, rect3 != null);
    }

    public RemoteAnimationRecord createRemoteAnimationRecord(WindowContainer windowContainer, Point point, Rect rect, Rect rect2, Rect rect3, boolean z, boolean z2) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 2022422429, 0, (String) null, new Object[]{String.valueOf(windowContainer)});
        }
        RemoteAnimationRecord remoteAnimationRecord = new RemoteAnimationRecord(windowContainer, point, rect, rect2, rect3, z, z2);
        this.mPendingAnimations.add(remoteAnimationRecord);
        return remoteAnimationRecord;
    }

    public void setOnRemoteAnimationReady(Runnable runnable) {
        this.mOnRemoteAnimationReady = runnable;
    }

    public boolean isFromActivityEmbedding() {
        return this.mIsActivityEmbedding;
    }

    public void goodToGo(final int i) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 873914452, 0, (String) null, (Object[]) null);
        }
        if (this.mCanceled) {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 269976641, 0, (String) null, (Object[]) null);
            }
            onAnimationFinished();
            invokeAnimationCancelled("already_cancelled");
            return;
        }
        this.mHandler.postDelayed(this.mTimeoutRunnable, this.mService.getCurrentAnimatorScale() * 10000.0f);
        this.mFinishedCallback = new FinishedCallback(this);
        final RemoteAnimationTarget[] createAppAnimations = createAppAnimations();
        if (createAppAnimations.length == 0 && !AppTransition.isKeyguardOccludeTransitOld(i)) {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -1777196134, 1, (String) null, new Object[]{Long.valueOf(this.mPendingAnimations.size())});
            }
            onAnimationFinished();
            invokeAnimationCancelled("no_app_targets");
            return;
        }
        Runnable runnable = this.mOnRemoteAnimationReady;
        if (runnable != null) {
            runnable.run();
            this.mOnRemoteAnimationReady = null;
        }
        final RemoteAnimationTarget[] createWallpaperAnimations = createWallpaperAnimations();
        final RemoteAnimationTarget[] createNonAppWindowAnimations = createNonAppWindowAnimations(i);
        this.mService.mAnimator.addAfterPrepareSurfacesRunnable(new Runnable() { // from class: com.android.server.wm.RemoteAnimationController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RemoteAnimationController.this.lambda$goodToGo$1(i, createAppAnimations, createWallpaperAnimations, createNonAppWindowAnimations);
            }
        });
        setRunningRemoteAnimation(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$goodToGo$1(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3) {
        try {
            linkToDeathOfRunner();
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 35398067, 84, (String) null, new Object[]{String.valueOf(AppTransition.appTransitionOldToString(i)), Long.valueOf(remoteAnimationTargetArr.length), Long.valueOf(remoteAnimationTargetArr2.length), Long.valueOf(remoteAnimationTargetArr3.length)});
            }
            if (AppTransition.isKeyguardOccludeTransitOld(i)) {
                EventLogTags.writeWmSetKeyguardOccluded(i == 23 ? 0 : 1, 1, i, "onAnimationStart");
            }
            this.mRemoteAnimationAdapter.getRunner().onAnimationStart(i, remoteAnimationTargetArr, remoteAnimationTargetArr2, remoteAnimationTargetArr3, this.mFinishedCallback);
        } catch (RemoteException e) {
            Slog.e(StartingSurfaceController.TAG, "Failed to start remote animation", e);
            onAnimationFinished();
        }
        if (ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS)) {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -2012562539, 0, (String) null, (Object[]) null);
            }
            writeStartDebugStatement();
        }
    }

    public void cancelAnimation(String str) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 1891501279, 0, (String) null, new Object[]{String.valueOf(str)});
        }
        synchronized (this.mService.getWindowManagerLock()) {
            if (this.mCanceled) {
                return;
            }
            this.mCanceled = true;
            onAnimationFinished();
            invokeAnimationCancelled(str);
        }
    }

    public final void writeStartDebugStatement() {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 1964565370, 0, (String) null, (Object[]) null);
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter fastPrintWriter = new FastPrintWriter(stringWriter);
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            this.mPendingAnimations.get(size).mAdapter.dump(fastPrintWriter, "");
        }
        fastPrintWriter.close();
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 835814848, 0, (String) null, new Object[]{String.valueOf(stringWriter.toString())});
        }
    }

    public final RemoteAnimationTarget[] createAppAnimations() {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -653156702, 0, (String) null, (Object[]) null);
        }
        ArrayList arrayList = new ArrayList();
        for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
            RemoteAnimationRecord remoteAnimationRecord = this.mPendingAnimations.get(size);
            RemoteAnimationTarget createRemoteAnimationTarget = remoteAnimationRecord.createRemoteAnimationTarget();
            if (createRemoteAnimationTarget != null) {
                if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -1248645819, 0, (String) null, new Object[]{String.valueOf(remoteAnimationRecord.mWindowContainer)});
                }
                arrayList.add(createRemoteAnimationTarget);
            } else {
                if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                    ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 638429464, 0, (String) null, new Object[]{String.valueOf(remoteAnimationRecord.mWindowContainer)});
                }
                RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper = remoteAnimationRecord.mAdapter;
                if (remoteAnimationAdapterWrapper != null && remoteAnimationAdapterWrapper.mCapturedFinishCallback != null) {
                    remoteAnimationRecord.mAdapter.mCapturedFinishCallback.onAnimationFinished(remoteAnimationRecord.mAdapter.mAnimationType, remoteAnimationRecord.mAdapter);
                }
                RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper2 = remoteAnimationRecord.mThumbnailAdapter;
                if (remoteAnimationAdapterWrapper2 != null && remoteAnimationAdapterWrapper2.mCapturedFinishCallback != null) {
                    remoteAnimationRecord.mThumbnailAdapter.mCapturedFinishCallback.onAnimationFinished(remoteAnimationRecord.mThumbnailAdapter.mAnimationType, remoteAnimationRecord.mThumbnailAdapter);
                }
                this.mPendingAnimations.remove(size);
            }
        }
        return (RemoteAnimationTarget[]) arrayList.toArray(new RemoteAnimationTarget[arrayList.size()]);
    }

    public final RemoteAnimationTarget[] createWallpaperAnimations() {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 594260577, 0, (String) null, (Object[]) null);
        }
        return WallpaperAnimationAdapter.startWallpaperAnimations(this.mDisplayContent, this.mRemoteAnimationAdapter.getDuration(), this.mRemoteAnimationAdapter.getStatusBarTransitionDelay(), new Consumer() { // from class: com.android.server.wm.RemoteAnimationController$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RemoteAnimationController.this.lambda$createWallpaperAnimations$2((WallpaperAnimationAdapter) obj);
            }
        }, this.mPendingWallpaperAnimations);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createWallpaperAnimations$2(WallpaperAnimationAdapter wallpaperAnimationAdapter) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mPendingWallpaperAnimations.remove(wallpaperAnimationAdapter);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public final RemoteAnimationTarget[] createNonAppWindowAnimations(int i) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -1834214907, 0, (String) null, (Object[]) null);
        }
        return NonAppWindowAnimationAdapter.startNonAppWindowAnimations(this.mService, this.mDisplayContent, i, this.mRemoteAnimationAdapter.getDuration(), this.mRemoteAnimationAdapter.getStatusBarTransitionDelay(), this.mPendingNonAppAnimations);
    }

    public final void onAnimationFinished() {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -1497837552, 1, (String) null, new Object[]{Long.valueOf(this.mPendingAnimations.size())});
        }
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mIsFinishing = true;
                unlinkToDeathOfRunner();
                releaseFinishedCallback();
                this.mService.openSurfaceTransaction();
                try {
                    if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                        ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 557227556, 0, (String) null, (Object[]) null);
                    }
                    for (int size = this.mPendingAnimations.size() - 1; size >= 0; size--) {
                        RemoteAnimationRecord remoteAnimationRecord = this.mPendingAnimations.get(size);
                        RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper = remoteAnimationRecord.mAdapter;
                        if (remoteAnimationAdapterWrapper != null) {
                            remoteAnimationAdapterWrapper.mCapturedFinishCallback.onAnimationFinished(remoteAnimationRecord.mAdapter.mAnimationType, remoteAnimationRecord.mAdapter);
                        }
                        RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper2 = remoteAnimationRecord.mThumbnailAdapter;
                        if (remoteAnimationAdapterWrapper2 != null) {
                            remoteAnimationAdapterWrapper2.mCapturedFinishCallback.onAnimationFinished(remoteAnimationRecord.mThumbnailAdapter.mAnimationType, remoteAnimationRecord.mThumbnailAdapter);
                        }
                        this.mPendingAnimations.remove(size);
                        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 972354148, 0, (String) null, new Object[]{String.valueOf(remoteAnimationRecord.mWindowContainer)});
                        }
                    }
                    for (int size2 = this.mPendingWallpaperAnimations.size() - 1; size2 >= 0; size2--) {
                        WallpaperAnimationAdapter wallpaperAnimationAdapter = this.mPendingWallpaperAnimations.get(size2);
                        wallpaperAnimationAdapter.getLeashFinishedCallback().onAnimationFinished(wallpaperAnimationAdapter.getLastAnimationType(), wallpaperAnimationAdapter);
                        this.mPendingWallpaperAnimations.remove(size2);
                        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -853404763, 0, (String) null, new Object[]{String.valueOf(wallpaperAnimationAdapter.getToken())});
                        }
                    }
                    for (int size3 = this.mPendingNonAppAnimations.size() - 1; size3 >= 0; size3--) {
                        NonAppWindowAnimationAdapter nonAppWindowAnimationAdapter = this.mPendingNonAppAnimations.get(size3);
                        nonAppWindowAnimationAdapter.getLeashFinishedCallback().onAnimationFinished(nonAppWindowAnimationAdapter.getLastAnimationType(), nonAppWindowAnimationAdapter);
                        this.mPendingNonAppAnimations.remove(size3);
                        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 1931178855, 0, (String) null, new Object[]{String.valueOf(nonAppWindowAnimationAdapter.getWindowContainer())});
                        }
                    }
                    this.mService.closeSurfaceTransaction("RemoteAnimationController#finished");
                    this.mIsFinishing = false;
                    this.mDisplayContent.forAllActivities(new Consumer() { // from class: com.android.server.wm.RemoteAnimationController$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((ActivityRecord) obj).setDropInputForAnimation(false);
                        }
                    });
                } catch (Exception e) {
                    Slog.e(StartingSurfaceController.TAG, "Failed to finish remote animation", e);
                    throw e;
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        setRunningRemoteAnimation(false);
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 248210157, 0, (String) null, (Object[]) null);
        }
    }

    public final void invokeAnimationCancelled(String str) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 1891501279, 0, (String) null, new Object[]{String.valueOf(str)});
        }
        boolean isKeyguardOccluded = this.mDisplayContent.isKeyguardOccluded();
        try {
            EventLogTags.writeWmSetKeyguardOccluded(isKeyguardOccluded ? 1 : 0, 0, 0, "onAnimationCancelled");
            this.mRemoteAnimationAdapter.getRunner().onAnimationCancelled(isKeyguardOccluded);
        } catch (RemoteException e) {
            Slog.e(StartingSurfaceController.TAG, "Failed to notify cancel", e);
        }
        this.mOnRemoteAnimationReady = null;
    }

    public final void releaseFinishedCallback() {
        FinishedCallback finishedCallback = this.mFinishedCallback;
        if (finishedCallback != null) {
            finishedCallback.release();
            this.mFinishedCallback = null;
        }
    }

    public final void setRunningRemoteAnimation(boolean z) {
        int callingPid = this.mRemoteAnimationAdapter.getCallingPid();
        int callingUid = this.mRemoteAnimationAdapter.getCallingUid();
        if (callingPid == 0) {
            throw new RuntimeException("Calling pid of remote animation was null");
        }
        WindowProcessController processController = this.mService.mAtmService.getProcessController(callingPid, callingUid);
        if (processController == null) {
            Slog.w(StartingSurfaceController.TAG, "Unable to find process with pid=" + callingPid + " uid=" + callingUid);
            return;
        }
        processController.setRunningRemoteAnimation(z);
    }

    public final void linkToDeathOfRunner() throws RemoteException {
        if (this.mLinkedToDeathOfRunner) {
            return;
        }
        this.mRemoteAnimationAdapter.getRunner().asBinder().linkToDeath(this, 0);
        this.mLinkedToDeathOfRunner = true;
    }

    public final void unlinkToDeathOfRunner() {
        if (this.mLinkedToDeathOfRunner) {
            this.mRemoteAnimationAdapter.getRunner().asBinder().unlinkToDeath(this, 0);
            this.mLinkedToDeathOfRunner = false;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        cancelAnimation("binderDied");
    }

    /* renamed from: com.android.server.wm.RemoteAnimationController$FinishedCallback */
    /* loaded from: classes2.dex */
    public static final class FinishedCallback extends IRemoteAnimationFinishedCallback.Stub {
        public RemoteAnimationController mOuter;

        public FinishedCallback(RemoteAnimationController remoteAnimationController) {
            this.mOuter = remoteAnimationController;
        }

        public void onAnimationFinished() throws RemoteException {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -2024464438, 0, (String) null, new Object[]{String.valueOf(this.mOuter)});
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                RemoteAnimationController remoteAnimationController = this.mOuter;
                if (remoteAnimationController != null) {
                    remoteAnimationController.onAnimationFinished();
                    this.mOuter = null;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void release() {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -2109864870, 0, (String) null, new Object[]{String.valueOf(this.mOuter)});
            }
            this.mOuter = null;
        }
    }

    /* renamed from: com.android.server.wm.RemoteAnimationController$RemoteAnimationRecord */
    /* loaded from: classes2.dex */
    public class RemoteAnimationRecord {
        public RemoteAnimationAdapterWrapper mAdapter;
        public int mBackdropColor = 0;
        public int mMode = 2;
        public final boolean mShowBackdrop;
        public final Rect mStartBounds;
        public RemoteAnimationTarget mTarget;
        public RemoteAnimationAdapterWrapper mThumbnailAdapter;
        public final WindowContainer mWindowContainer;

        public RemoteAnimationRecord(WindowContainer windowContainer, Point point, Rect rect, Rect rect2, Rect rect3, boolean z, boolean z2) {
            this.mThumbnailAdapter = null;
            this.mWindowContainer = windowContainer;
            this.mShowBackdrop = z;
            if (rect3 != null) {
                Rect rect4 = new Rect(rect3);
                this.mStartBounds = rect4;
                this.mAdapter = new RemoteAnimationAdapterWrapper(this, point, rect, rect2, rect4, z);
                if (z2 && RemoteAnimationController.this.mRemoteAnimationAdapter.getChangeNeedsSnapshot()) {
                    Rect rect5 = new Rect(rect3);
                    rect5.offsetTo(0, 0);
                    this.mThumbnailAdapter = new RemoteAnimationAdapterWrapper(this, new Point(0, 0), rect5, rect3, new Rect(), z);
                    return;
                }
                return;
            }
            this.mAdapter = new RemoteAnimationAdapterWrapper(this, point, rect, rect2, new Rect(), z);
            this.mStartBounds = null;
        }

        public void setBackDropColor(int i) {
            this.mBackdropColor = i;
        }

        public RemoteAnimationTarget createRemoteAnimationTarget() {
            RemoteAnimationAdapterWrapper remoteAnimationAdapterWrapper = this.mAdapter;
            if (remoteAnimationAdapterWrapper == null || remoteAnimationAdapterWrapper.mCapturedFinishCallback == null || this.mAdapter.mCapturedLeash == null) {
                return null;
            }
            RemoteAnimationTarget createRemoteAnimationTarget = this.mWindowContainer.createRemoteAnimationTarget(this);
            this.mTarget = createRemoteAnimationTarget;
            return createRemoteAnimationTarget;
        }

        public void setMode(int i) {
            this.mMode = i;
        }

        public int getMode() {
            return this.mMode;
        }

        public boolean hasAnimatingParent() {
            for (int size = RemoteAnimationController.this.mDisplayContent.mChangingContainers.size() - 1; size >= 0; size--) {
                if (this.mWindowContainer.isDescendantOf(RemoteAnimationController.this.mDisplayContent.mChangingContainers.valueAt(size))) {
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: com.android.server.wm.RemoteAnimationController$RemoteAnimationAdapterWrapper */
    /* loaded from: classes2.dex */
    public class RemoteAnimationAdapterWrapper implements AnimationAdapter {
        public int mAnimationType;
        public SurfaceAnimator.OnAnimationFinishedCallback mCapturedFinishCallback;
        public SurfaceControl mCapturedLeash;
        public final Rect mEndBounds;
        public final Rect mLocalBounds;
        public final Point mPosition;
        public final RemoteAnimationRecord mRecord;
        public final boolean mShowBackdrop;
        public final Rect mStartBounds;

        @Override // com.android.server.p014wm.AnimationAdapter
        public boolean getShowWallpaper() {
            return false;
        }

        public RemoteAnimationAdapterWrapper(RemoteAnimationRecord remoteAnimationRecord, Point point, Rect rect, Rect rect2, Rect rect3, boolean z) {
            Point point2 = new Point();
            this.mPosition = point2;
            Rect rect4 = new Rect();
            this.mEndBounds = rect4;
            Rect rect5 = new Rect();
            this.mStartBounds = rect5;
            this.mRecord = remoteAnimationRecord;
            point2.set(point.x, point.y);
            this.mLocalBounds = rect;
            rect4.set(rect2);
            rect5.set(rect3);
            this.mShowBackdrop = z;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public int getBackgroundColor() {
            return this.mRecord.mBackdropColor;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public boolean getShowBackground() {
            return this.mShowBackdrop;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -1596995693, 0, (String) null, (Object[]) null);
            }
            if (this.mStartBounds.isEmpty()) {
                Point point = this.mPosition;
                transaction.setPosition(surfaceControl, point.x, point.y);
                transaction.setWindowCrop(surfaceControl, this.mEndBounds.width(), this.mEndBounds.height());
            } else {
                Point point2 = this.mPosition;
                int i2 = point2.x;
                Rect rect = this.mStartBounds;
                int i3 = i2 + rect.left;
                Rect rect2 = this.mEndBounds;
                transaction.setPosition(surfaceControl, i3 - rect2.left, (point2.y + rect.top) - rect2.top);
                transaction.setWindowCrop(surfaceControl, this.mStartBounds.width(), this.mStartBounds.height());
            }
            this.mCapturedLeash = surfaceControl;
            this.mCapturedFinishCallback = onAnimationFinishedCallback;
            this.mAnimationType = i;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void onAnimationCancelled(SurfaceControl surfaceControl) {
            if (RemoteAnimationController.this.mIsFinishing) {
                return;
            }
            RemoteAnimationRecord remoteAnimationRecord = this.mRecord;
            if (remoteAnimationRecord.mAdapter == this) {
                remoteAnimationRecord.mAdapter = null;
            } else {
                remoteAnimationRecord.mThumbnailAdapter = null;
            }
            if (remoteAnimationRecord.mAdapter == null && remoteAnimationRecord.mThumbnailAdapter == null) {
                RemoteAnimationController.this.mPendingAnimations.remove(this.mRecord);
            }
            if (RemoteAnimationController.this.mPendingAnimations.isEmpty()) {
                RemoteAnimationController.this.cancelAnimation("allAppAnimationsCanceled");
            }
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public long getDurationHint() {
            return RemoteAnimationController.this.mRemoteAnimationAdapter.getDuration();
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public long getStatusBarTransitionsStartTime() {
            return SystemClock.uptimeMillis() + RemoteAnimationController.this.mRemoteAnimationAdapter.getStatusBarTransitionDelay();
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str);
            printWriter.print("container=");
            printWriter.println(this.mRecord.mWindowContainer);
            if (this.mRecord.mTarget != null) {
                printWriter.print(str);
                printWriter.println("Target:");
                RemoteAnimationTarget remoteAnimationTarget = this.mRecord.mTarget;
                remoteAnimationTarget.dump(printWriter, str + "  ");
                return;
            }
            printWriter.print(str);
            printWriter.println("Target: null");
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public void dumpDebug(ProtoOutputStream protoOutputStream) {
            long start = protoOutputStream.start(1146756268034L);
            RemoteAnimationTarget remoteAnimationTarget = this.mRecord.mTarget;
            if (remoteAnimationTarget != null) {
                remoteAnimationTarget.dumpDebug(protoOutputStream, 1146756268033L);
            }
            protoOutputStream.end(start);
        }
    }
}
