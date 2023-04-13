package com.android.server.wallpaper;

import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Binder;
import android.os.Debug;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class WallpaperDisplayHelper {
    public static final String TAG = "WallpaperDisplayHelper";
    public final SparseArray<DisplayData> mDisplayDatas = new SparseArray<>();
    public final DisplayManager mDisplayManager;
    public final WindowManagerInternal mWindowManagerInternal;

    /* loaded from: classes2.dex */
    public static final class DisplayData {
        public final int mDisplayId;
        public int mWidth = -1;
        public int mHeight = -1;
        public final Rect mPadding = new Rect(0, 0, 0, 0);

        public DisplayData(int i) {
            this.mDisplayId = i;
        }
    }

    public WallpaperDisplayHelper(DisplayManager displayManager, WindowManagerInternal windowManagerInternal) {
        this.mDisplayManager = displayManager;
        this.mWindowManagerInternal = windowManagerInternal;
    }

    public DisplayData getDisplayDataOrCreate(int i) {
        DisplayData displayData = this.mDisplayDatas.get(i);
        if (displayData == null) {
            DisplayData displayData2 = new DisplayData(i);
            ensureSaneWallpaperDisplaySize(displayData2, i);
            this.mDisplayDatas.append(i, displayData2);
            return displayData2;
        }
        return displayData;
    }

    public void removeDisplayData(int i) {
        this.mDisplayDatas.remove(i);
    }

    public void ensureSaneWallpaperDisplaySize(DisplayData displayData, int i) {
        int maximumSizeDimension = getMaximumSizeDimension(i);
        if (displayData.mWidth < maximumSizeDimension) {
            displayData.mWidth = maximumSizeDimension;
        }
        if (displayData.mHeight < maximumSizeDimension) {
            displayData.mHeight = maximumSizeDimension;
        }
    }

    public int getMaximumSizeDimension(int i) {
        Display display = this.mDisplayManager.getDisplay(i);
        if (display == null) {
            String str = TAG;
            Slog.w(str, "Invalid displayId=" + i + " " + Debug.getCallers(4));
            display = this.mDisplayManager.getDisplay(0);
        }
        return display.getMaximumSizeDimension();
    }

    public void forEachDisplayData(Consumer<DisplayData> consumer) {
        for (int size = this.mDisplayDatas.size() - 1; size >= 0; size--) {
            consumer.accept(this.mDisplayDatas.valueAt(size));
        }
    }

    public Display[] getDisplays() {
        return this.mDisplayManager.getDisplays();
    }

    public DisplayInfo getDisplayInfo(int i) {
        DisplayInfo displayInfo = new DisplayInfo();
        this.mDisplayManager.getDisplay(i).getDisplayInfo(displayInfo);
        return displayInfo;
    }

    public boolean isUsableDisplay(int i, int i2) {
        return isUsableDisplay(this.mDisplayManager.getDisplay(i), i2);
    }

    public boolean isUsableDisplay(Display display, int i) {
        if (display == null || !display.hasAccess(i)) {
            return false;
        }
        int displayId = display.getDisplayId();
        if (displayId == 0) {
            return true;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mWindowManagerInternal.shouldShowSystemDecorOnDisplay(displayId);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isValidDisplay(int i) {
        return this.mDisplayManager.getDisplay(i) != null;
    }
}
