package com.android.server.p014wm;

import android.view.Display;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import java.util.HashMap;
import java.util.Objects;
/* renamed from: com.android.server.wm.RefreshRatePolicy */
/* loaded from: classes2.dex */
public class RefreshRatePolicy {
    public final Display.Mode mDefaultMode;
    public final DisplayInfo mDisplayInfo;
    public final HighRefreshRateDenylist mHighRefreshRateDenylist;
    public final Display.Mode mLowRefreshRateMode;
    public float mMaxSupportedRefreshRate;
    public float mMinSupportedRefreshRate;
    public final PackageRefreshRate mNonHighRefreshRatePackages = new PackageRefreshRate();
    public final WindowManagerService mWmService;

    /* renamed from: com.android.server.wm.RefreshRatePolicy$PackageRefreshRate */
    /* loaded from: classes2.dex */
    public class PackageRefreshRate {
        public final HashMap<String, SurfaceControl.RefreshRateRange> mPackages = new HashMap<>();

        public PackageRefreshRate() {
        }

        public void add(String str, float f, float f2) {
            this.mPackages.put(str, new SurfaceControl.RefreshRateRange(Math.max(RefreshRatePolicy.this.mMinSupportedRefreshRate, f), Math.min(RefreshRatePolicy.this.mMaxSupportedRefreshRate, f2)));
        }

        public SurfaceControl.RefreshRateRange get(String str) {
            return this.mPackages.get(str);
        }

        public void remove(String str) {
            this.mPackages.remove(str);
        }
    }

    public RefreshRatePolicy(WindowManagerService windowManagerService, DisplayInfo displayInfo, HighRefreshRateDenylist highRefreshRateDenylist) {
        this.mDisplayInfo = displayInfo;
        Display.Mode defaultMode = displayInfo.getDefaultMode();
        this.mDefaultMode = defaultMode;
        this.mLowRefreshRateMode = findLowRefreshRateMode(displayInfo, defaultMode);
        this.mHighRefreshRateDenylist = highRefreshRateDenylist;
        this.mWmService = windowManagerService;
    }

    public final Display.Mode findLowRefreshRateMode(DisplayInfo displayInfo, Display.Mode mode) {
        float[] defaultRefreshRates = displayInfo.getDefaultRefreshRates();
        float refreshRate = mode.getRefreshRate();
        this.mMinSupportedRefreshRate = refreshRate;
        this.mMaxSupportedRefreshRate = refreshRate;
        for (int length = defaultRefreshRates.length - 1; length >= 0; length--) {
            this.mMinSupportedRefreshRate = Math.min(this.mMinSupportedRefreshRate, defaultRefreshRates[length]);
            this.mMaxSupportedRefreshRate = Math.max(this.mMaxSupportedRefreshRate, defaultRefreshRates[length]);
            float f = defaultRefreshRates[length];
            if (f >= 60.0f && f < refreshRate) {
                refreshRate = f;
            }
        }
        return displayInfo.findDefaultModeByRefreshRate(refreshRate);
    }

    public void addRefreshRateRangeForPackage(String str, float f, float f2) {
        this.mNonHighRefreshRatePackages.add(str, f, f2);
        this.mWmService.requestTraversal();
    }

    public void removeRefreshRateRangeForPackage(String str) {
        this.mNonHighRefreshRatePackages.remove(str);
        this.mWmService.requestTraversal();
    }

    public int getPreferredModeId(WindowState windowState) {
        Display.Mode mode;
        int i = windowState.mAttrs.preferredDisplayModeId;
        if (i <= 0) {
            return 0;
        }
        if (windowState.isAnimationRunningSelfOrParent()) {
            Display.Mode[] modeArr = this.mDisplayInfo.supportedModes;
            int length = modeArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    mode = null;
                    break;
                }
                mode = modeArr[i2];
                if (i == mode.getModeId()) {
                    break;
                }
                i2++;
            }
            if (mode != null) {
                int physicalWidth = mode.getPhysicalWidth();
                int physicalHeight = mode.getPhysicalHeight();
                if ((physicalWidth != this.mDefaultMode.getPhysicalWidth() || physicalHeight != this.mDefaultMode.getPhysicalHeight()) && physicalWidth == this.mDisplayInfo.getNaturalWidth() && physicalHeight == this.mDisplayInfo.getNaturalHeight()) {
                    return i;
                }
            }
            return 0;
        }
        return i;
    }

    public int calculatePriority(WindowState windowState) {
        boolean isFocused = windowState.isFocused();
        int preferredModeId = getPreferredModeId(windowState);
        if (isFocused || preferredModeId <= 0) {
            if (isFocused && preferredModeId == 0) {
                return 1;
            }
            return (!isFocused || preferredModeId <= 0) ? -1 : 0;
        }
        return 2;
    }

    /* renamed from: com.android.server.wm.RefreshRatePolicy$FrameRateVote */
    /* loaded from: classes2.dex */
    public static class FrameRateVote {
        public int mCompatibility;
        public float mRefreshRate;

        public FrameRateVote() {
            reset();
        }

        public boolean update(float f, int i) {
            if (refreshRateEquals(f) && this.mCompatibility == i) {
                return false;
            }
            this.mRefreshRate = f;
            this.mCompatibility = i;
            return true;
        }

        public boolean reset() {
            return update(0.0f, 0);
        }

        public boolean equals(Object obj) {
            if (obj instanceof FrameRateVote) {
                FrameRateVote frameRateVote = (FrameRateVote) obj;
                return refreshRateEquals(frameRateVote.mRefreshRate) && this.mCompatibility == frameRateVote.mCompatibility;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Float.valueOf(this.mRefreshRate), Integer.valueOf(this.mCompatibility));
        }

        public String toString() {
            return "mRefreshRate=" + this.mRefreshRate + ", mCompatibility=" + this.mCompatibility;
        }

        public final boolean refreshRateEquals(float f) {
            float f2 = this.mRefreshRate;
            return f2 <= f + 0.01f && f2 >= f - 0.01f;
        }
    }

    public boolean updateFrameRateVote(WindowState windowState) {
        int i;
        Display.Mode[] modeArr;
        int refreshRateSwitchingType = this.mWmService.mDisplayManagerInternal.getRefreshRateSwitchingType();
        if (refreshRateSwitchingType == 0) {
            return windowState.mFrameRateVote.reset();
        }
        if (windowState.isAnimationRunningSelfOrParent()) {
            return windowState.mFrameRateVote.reset();
        }
        if (refreshRateSwitchingType != 3 && (i = windowState.mAttrs.preferredDisplayModeId) > 0) {
            for (Display.Mode mode : this.mDisplayInfo.supportedModes) {
                if (i == mode.getModeId()) {
                    return windowState.mFrameRateVote.update(mode.getRefreshRate(), 100);
                }
            }
        }
        float f = windowState.mAttrs.preferredRefreshRate;
        if (f > 0.0f) {
            return windowState.mFrameRateVote.update(f, 0);
        }
        if (refreshRateSwitchingType != 3) {
            if (this.mHighRefreshRateDenylist.isDenylisted(windowState.getOwningPackage())) {
                return windowState.mFrameRateVote.update(this.mLowRefreshRateMode.getRefreshRate(), 100);
            }
        }
        return windowState.mFrameRateVote.reset();
    }

    public float getPreferredMinRefreshRate(WindowState windowState) {
        if (windowState.isAnimationRunningSelfOrParent()) {
            return 0.0f;
        }
        float f = windowState.mAttrs.preferredMinDisplayRefreshRate;
        if (f > 0.0f) {
            return f;
        }
        SurfaceControl.RefreshRateRange refreshRateRange = this.mNonHighRefreshRatePackages.get(windowState.getOwningPackage());
        if (refreshRateRange != null) {
            return refreshRateRange.min;
        }
        return 0.0f;
    }

    public float getPreferredMaxRefreshRate(WindowState windowState) {
        if (windowState.isAnimationRunningSelfOrParent()) {
            return 0.0f;
        }
        float f = windowState.mAttrs.preferredMaxDisplayRefreshRate;
        if (f > 0.0f) {
            return f;
        }
        SurfaceControl.RefreshRateRange refreshRateRange = this.mNonHighRefreshRatePackages.get(windowState.getOwningPackage());
        if (refreshRateRange != null) {
            return refreshRateRange.max;
        }
        return 0.0f;
    }
}
