package com.android.server.display;

import android.hardware.display.DisplayManagerGlobal;
import android.view.DisplayInfo;
/* loaded from: classes.dex */
public class DisplayInfoProxy {
    public DisplayInfo mInfo;

    public DisplayInfoProxy(DisplayInfo displayInfo) {
        this.mInfo = displayInfo;
    }

    public void set(DisplayInfo displayInfo) {
        this.mInfo = displayInfo;
        DisplayManagerGlobal.invalidateLocalDisplayInfoCaches();
    }

    public DisplayInfo get() {
        return this.mInfo;
    }
}
