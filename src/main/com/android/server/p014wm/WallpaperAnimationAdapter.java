package com.android.server.p014wm;

import android.app.ActivityManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.proto.ProtoOutputStream;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.p014wm.SurfaceAnimator;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.WallpaperAnimationAdapter */
/* loaded from: classes2.dex */
public class WallpaperAnimationAdapter implements AnimationAdapter {
    public Consumer<WallpaperAnimationAdapter> mAnimationCanceledRunnable;
    public SurfaceControl mCapturedLeash;
    public SurfaceAnimator.OnAnimationFinishedCallback mCapturedLeashFinishCallback;
    public long mDurationHint;
    public int mLastAnimationType;
    public long mStatusBarTransitionDelay;
    public RemoteAnimationTarget mTarget;
    public final WallpaperWindowToken mWallpaperToken;

    @Override // com.android.server.p014wm.AnimationAdapter
    public boolean getShowWallpaper() {
        return false;
    }

    public WallpaperAnimationAdapter(WallpaperWindowToken wallpaperWindowToken, long j, long j2, Consumer<WallpaperAnimationAdapter> consumer) {
        this.mWallpaperToken = wallpaperWindowToken;
        this.mDurationHint = j;
        this.mStatusBarTransitionDelay = j2;
        this.mAnimationCanceledRunnable = consumer;
    }

    public static RemoteAnimationTarget[] startWallpaperAnimations(DisplayContent displayContent, final long j, final long j2, final Consumer<WallpaperAnimationAdapter> consumer, final ArrayList<WallpaperAnimationAdapter> arrayList) {
        if (!shouldStartWallpaperAnimation(displayContent)) {
            if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 2024493888, 0, (String) null, new Object[]{String.valueOf(displayContent)});
            }
            return new RemoteAnimationTarget[0];
        }
        final ArrayList arrayList2 = new ArrayList();
        displayContent.forAllWallpaperWindows(new Consumer() { // from class: com.android.server.wm.WallpaperAnimationAdapter$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WallpaperAnimationAdapter.lambda$startWallpaperAnimations$0(j, j2, consumer, arrayList2, arrayList, (WallpaperWindowToken) obj);
            }
        });
        return (RemoteAnimationTarget[]) arrayList2.toArray(new RemoteAnimationTarget[arrayList2.size()]);
    }

    public static /* synthetic */ void lambda$startWallpaperAnimations$0(long j, long j2, Consumer consumer, ArrayList arrayList, ArrayList arrayList2, WallpaperWindowToken wallpaperWindowToken) {
        WallpaperAnimationAdapter wallpaperAnimationAdapter = new WallpaperAnimationAdapter(wallpaperWindowToken, j, j2, consumer);
        wallpaperWindowToken.startAnimation(wallpaperWindowToken.getPendingTransaction(), wallpaperAnimationAdapter, false, 16);
        arrayList.add(wallpaperAnimationAdapter.createRemoteAnimationTarget());
        arrayList2.add(wallpaperAnimationAdapter);
    }

    public static boolean shouldStartWallpaperAnimation(DisplayContent displayContent) {
        return displayContent.mWallpaperController.isWallpaperVisible();
    }

    public RemoteAnimationTarget createRemoteAnimationTarget() {
        RemoteAnimationTarget remoteAnimationTarget = new RemoteAnimationTarget(-1, -1, getLeash(), false, (Rect) null, (Rect) null, this.mWallpaperToken.getPrefixOrderIndex(), new Point(), (Rect) null, (Rect) null, this.mWallpaperToken.getWindowConfiguration(), true, (SurfaceControl) null, (Rect) null, (ActivityManager.RunningTaskInfo) null, false);
        this.mTarget = remoteAnimationTarget;
        return remoteAnimationTarget;
    }

    public SurfaceControl getLeash() {
        return this.mCapturedLeash;
    }

    public SurfaceAnimator.OnAnimationFinishedCallback getLeashFinishedCallback() {
        return this.mCapturedLeashFinishCallback;
    }

    public int getLastAnimationType() {
        return this.mLastAnimationType;
    }

    public WallpaperWindowToken getToken() {
        return this.mWallpaperToken;
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, 1073230342, 0, (String) null, (Object[]) null);
        }
        transaction.setLayer(surfaceControl, this.mWallpaperToken.getPrefixOrderIndex());
        this.mCapturedLeash = surfaceControl;
        this.mCapturedLeashFinishCallback = onAnimationFinishedCallback;
        this.mLastAnimationType = i;
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void onAnimationCancelled(SurfaceControl surfaceControl) {
        if (ProtoLogCache.WM_DEBUG_REMOTE_ANIMATIONS_enabled) {
            ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS, -760801764, 0, (String) null, (Object[]) null);
        }
        this.mAnimationCanceledRunnable.accept(this);
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public long getDurationHint() {
        return this.mDurationHint;
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public long getStatusBarTransitionsStartTime() {
        return SystemClock.uptimeMillis() + this.mStatusBarTransitionDelay;
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("token=");
        printWriter.println(this.mWallpaperToken);
        if (this.mTarget != null) {
            printWriter.print(str);
            printWriter.println("Target:");
            RemoteAnimationTarget remoteAnimationTarget = this.mTarget;
            remoteAnimationTarget.dump(printWriter, str + "  ");
            return;
        }
        printWriter.print(str);
        printWriter.println("Target: null");
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void dumpDebug(ProtoOutputStream protoOutputStream) {
        long start = protoOutputStream.start(1146756268034L);
        RemoteAnimationTarget remoteAnimationTarget = this.mTarget;
        if (remoteAnimationTarget != null) {
            remoteAnimationTarget.dumpDebug(protoOutputStream, 1146756268033L);
        }
        protoOutputStream.end(start);
    }
}
