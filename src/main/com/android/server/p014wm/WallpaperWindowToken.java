package com.android.server.p014wm;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.WallpaperWindowToken */
/* loaded from: classes2.dex */
public class WallpaperWindowToken extends WindowToken {
    public boolean mShowWhenLocked;

    @Override // com.android.server.p014wm.WindowContainer
    public WallpaperWindowToken asWallpaperToken() {
        return this;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean fillsParent() {
        return true;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean onChildVisibleRequestedChanged(WindowContainer windowContainer) {
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean showWallpaper() {
        return false;
    }

    public WallpaperWindowToken(WindowManagerService windowManagerService, IBinder iBinder, boolean z, DisplayContent displayContent, boolean z2, Bundle bundle) {
        super(windowManagerService, iBinder, 2013, z, displayContent, z2, false, false, bundle);
        this.mShowWhenLocked = false;
        displayContent.mWallpaperController.addWallpaperToken(this);
        setWindowingMode(1);
    }

    @Override // com.android.server.p014wm.WindowToken
    public void setExiting(boolean z) {
        super.setExiting(z);
        this.mDisplayContent.mWallpaperController.removeWallpaperToken(this);
    }

    public void setShowWhenLocked(boolean z) {
        if (z == this.mShowWhenLocked) {
            return;
        }
        this.mShowWhenLocked = z;
        if (this.mDisplayContent.mWallpaperController.mEnableSeparateLockScreenEngine) {
            getParent().positionChildAt(z ? Integer.MIN_VALUE : Integer.MAX_VALUE, this, false);
        }
    }

    public boolean canShowWhenLocked() {
        return this.mShowWhenLocked;
    }

    public void sendWindowWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) {
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            try {
                ((WindowState) this.mChildren.get(size)).mClient.dispatchWallpaperCommand(str, i, i2, i3, bundle, z);
                z = false;
            } catch (RemoteException unused) {
            }
        }
    }

    public void updateWallpaperOffset(boolean z) {
        WallpaperController wallpaperController = this.mDisplayContent.mWallpaperController;
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            if (wallpaperController.updateWallpaperOffset((WindowState) this.mChildren.get(size), z)) {
                z = false;
            }
        }
    }

    public void updateWallpaperWindows(boolean z) {
        boolean z2;
        WindowState wallpaperTarget;
        if (this.mVisibleRequested != z) {
            if (ProtoLogCache.WM_DEBUG_WALLPAPER_enabled) {
                ProtoLogImpl.d(ProtoLogGroup.WM_DEBUG_WALLPAPER, 733466617, 12, (String) null, new Object[]{String.valueOf(this.token), Boolean.valueOf(z)});
            }
            setVisibility(z);
            z2 = true;
        } else {
            z2 = false;
        }
        if (this.mTransitionController.isShellTransitionsEnabled()) {
            if (!this.mTransitionController.useShellTransitionsRotation() && z2 && z && (wallpaperTarget = this.mDisplayContent.mWallpaperController.getWallpaperTarget()) != null && wallpaperTarget.mToken.hasFixedRotationTransform()) {
                linkFixedRotationTransform(wallpaperTarget.mToken);
                return;
            }
            return;
        }
        WindowState wallpaperTarget2 = this.mDisplayContent.mWallpaperController.getWallpaperTarget();
        if (z && wallpaperTarget2 != null) {
            RecentsAnimationController recentsAnimationController = this.mWmService.getRecentsAnimationController();
            if (recentsAnimationController != null && recentsAnimationController.isAnimatingTask(wallpaperTarget2.getTask())) {
                recentsAnimationController.linkFixedRotationTransformIfNeeded(this);
            } else {
                ActivityRecord activityRecord = wallpaperTarget2.mActivityRecord;
                if ((activityRecord == null || activityRecord.isVisibleRequested()) && wallpaperTarget2.mToken.hasFixedRotationTransform()) {
                    linkFixedRotationTransform(wallpaperTarget2.mToken);
                }
            }
        }
        setVisible(z);
    }

    public final void setVisible(boolean z) {
        boolean isClientVisible = isClientVisible();
        setClientVisible(z);
        if (!z || isClientVisible) {
            return;
        }
        for (int size = this.mChildren.size() - 1; size >= 0; size--) {
            ((WindowState) this.mChildren.get(size)).requestUpdateWallpaperIfNeeded();
        }
    }

    public void setVisibility(boolean z) {
        if (this.mVisibleRequested != z) {
            this.mTransitionController.collect(this);
            setVisibleRequested(z);
        }
        if (z || !(this.mTransitionController.inTransition() || getDisplayContent().mAppTransition.isRunning())) {
            commitVisibility(z);
        }
    }

    public void commitVisibility(boolean z) {
        if (z == isVisible()) {
            return;
        }
        if (ProtoLogCache.WM_DEBUG_APP_TRANSITIONS_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS, 3593205, 60, (String) null, new Object[]{String.valueOf(this), Boolean.valueOf(isVisible()), Boolean.valueOf(this.mVisibleRequested)});
        }
        setVisibleRequested(z);
        setVisible(z);
    }

    public boolean hasVisibleNotDrawnWallpaper() {
        if (isVisible()) {
            for (int size = this.mChildren.size() - 1; size >= 0; size--) {
                WindowState windowState = (WindowState) this.mChildren.get(size);
                if (!windowState.isDrawn() && windowState.isVisible()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public void forAllWallpaperWindows(Consumer<WallpaperWindowToken> consumer) {
        consumer.accept(this);
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean setVisibleRequested(boolean z) {
        if (super.setVisibleRequested(z)) {
            setInsetsFrozen(!z);
            return true;
        }
        return false;
    }

    @Override // com.android.server.p014wm.WindowContainer
    public boolean isVisible() {
        return isClientVisible();
    }

    @Override // com.android.server.p014wm.WindowToken
    public String toString() {
        if (this.stringName == null) {
            this.stringName = "WallpaperWindowToken{" + Integer.toHexString(System.identityHashCode(this)) + " token=" + this.token + '}';
        }
        return this.stringName;
    }
}
