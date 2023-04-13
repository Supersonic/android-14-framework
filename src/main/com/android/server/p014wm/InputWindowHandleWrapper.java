package com.android.server.p014wm;

import android.graphics.Region;
import android.os.IBinder;
import android.view.IWindow;
import android.view.InputApplicationHandle;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
import java.util.Objects;
/* renamed from: com.android.server.wm.InputWindowHandleWrapper */
/* loaded from: classes2.dex */
public class InputWindowHandleWrapper {
    public boolean mChanged = true;
    public final InputWindowHandle mHandle;

    public InputWindowHandleWrapper(InputWindowHandle inputWindowHandle) {
        this.mHandle = inputWindowHandle;
    }

    public boolean isChanged() {
        return this.mChanged;
    }

    public void forceChange() {
        this.mChanged = true;
    }

    public void applyChangesToSurface(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        transaction.setInputWindowInfo(surfaceControl, this.mHandle);
        this.mChanged = false;
    }

    public int getDisplayId() {
        return this.mHandle.displayId;
    }

    public boolean isFocusable() {
        return (this.mHandle.inputConfig & 4) == 0;
    }

    public boolean isPaused() {
        return (this.mHandle.inputConfig & 128) != 0;
    }

    public boolean isTrustedOverlay() {
        return (this.mHandle.inputConfig & 256) != 0;
    }

    public boolean hasWallpaper() {
        return (this.mHandle.inputConfig & 32) != 0;
    }

    public InputApplicationHandle getInputApplicationHandle() {
        return this.mHandle.inputApplicationHandle;
    }

    public void setInputApplicationHandle(InputApplicationHandle inputApplicationHandle) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.inputApplicationHandle == inputApplicationHandle) {
            return;
        }
        inputWindowHandle.inputApplicationHandle = inputApplicationHandle;
        this.mChanged = true;
    }

    public void setToken(IBinder iBinder) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.token == iBinder) {
            return;
        }
        inputWindowHandle.token = iBinder;
        this.mChanged = true;
    }

    public void setName(String str) {
        if (Objects.equals(this.mHandle.name, str)) {
            return;
        }
        this.mHandle.name = str;
        this.mChanged = true;
    }

    public void setLayoutParamsFlags(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.layoutParamsFlags == i) {
            return;
        }
        inputWindowHandle.layoutParamsFlags = i;
        this.mChanged = true;
    }

    public void setLayoutParamsType(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.layoutParamsType == i) {
            return;
        }
        inputWindowHandle.layoutParamsType = i;
        this.mChanged = true;
    }

    public void setDispatchingTimeoutMillis(long j) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.dispatchingTimeoutMillis == j) {
            return;
        }
        inputWindowHandle.dispatchingTimeoutMillis = j;
        this.mChanged = true;
    }

    public void setTouchableRegion(Region region) {
        if (this.mHandle.touchableRegion.equals(region)) {
            return;
        }
        this.mHandle.touchableRegion.set(region);
        this.mChanged = true;
    }

    public void clearTouchableRegion() {
        if (this.mHandle.touchableRegion.isEmpty()) {
            return;
        }
        this.mHandle.touchableRegion.setEmpty();
        this.mChanged = true;
    }

    public void setFocusable(boolean z) {
        if (isFocusable() == z) {
            return;
        }
        this.mHandle.setInputConfig(4, !z);
        this.mChanged = true;
    }

    public void setTouchOcclusionMode(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.touchOcclusionMode == i) {
            return;
        }
        inputWindowHandle.touchOcclusionMode = i;
        this.mChanged = true;
    }

    public void setHasWallpaper(boolean z) {
        if (hasWallpaper() == z) {
            return;
        }
        this.mHandle.setInputConfig(32, z);
        this.mChanged = true;
    }

    public void setPaused(boolean z) {
        if (isPaused() == z) {
            return;
        }
        this.mHandle.setInputConfig(128, z);
        this.mChanged = true;
    }

    public void setTrustedOverlay(boolean z) {
        if (isTrustedOverlay() == z) {
            return;
        }
        this.mHandle.setInputConfig(256, z);
        this.mChanged = true;
    }

    public void setOwnerPid(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.ownerPid == i) {
            return;
        }
        inputWindowHandle.ownerPid = i;
        this.mChanged = true;
    }

    public void setOwnerUid(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.ownerUid == i) {
            return;
        }
        inputWindowHandle.ownerUid = i;
        this.mChanged = true;
    }

    public void setPackageName(String str) {
        if (Objects.equals(this.mHandle.packageName, str)) {
            return;
        }
        this.mHandle.packageName = str;
        this.mChanged = true;
    }

    public void setDisplayId(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.displayId == i) {
            return;
        }
        inputWindowHandle.displayId = i;
        this.mChanged = true;
    }

    public void setSurfaceInset(int i) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.surfaceInset == i) {
            return;
        }
        inputWindowHandle.surfaceInset = i;
        this.mChanged = true;
    }

    public void setScaleFactor(float f) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.scaleFactor == f) {
            return;
        }
        inputWindowHandle.scaleFactor = f;
        this.mChanged = true;
    }

    public void setTouchableRegionCrop(SurfaceControl surfaceControl) {
        if (this.mHandle.touchableRegionSurfaceControl.get() == surfaceControl) {
            return;
        }
        this.mHandle.setTouchableRegionCrop(surfaceControl);
        this.mChanged = true;
    }

    public void setReplaceTouchableRegionWithCrop(boolean z) {
        InputWindowHandle inputWindowHandle = this.mHandle;
        if (inputWindowHandle.replaceTouchableRegionWithCrop == z) {
            return;
        }
        inputWindowHandle.replaceTouchableRegionWithCrop = z;
        this.mChanged = true;
    }

    public void setWindowToken(IWindow iWindow) {
        if (this.mHandle.getWindow() == iWindow) {
            return;
        }
        this.mHandle.setWindowToken(iWindow);
        this.mChanged = true;
    }

    public void setInputConfigMasked(int i, int i2) {
        int i3 = i & i2;
        InputWindowHandle inputWindowHandle = this.mHandle;
        int i4 = inputWindowHandle.inputConfig;
        if (i3 == (i4 & i2)) {
            return;
        }
        inputWindowHandle.inputConfig = i3 | ((~i2) & i4);
        this.mChanged = true;
    }

    public String toString() {
        return this.mHandle + ", changed=" + this.mChanged;
    }
}
