package com.android.server.p014wm.utils;

import android.util.Size;
import android.view.DisplayCutout;
import java.util.Objects;
/* renamed from: com.android.server.wm.utils.WmDisplayCutout */
/* loaded from: classes2.dex */
public class WmDisplayCutout {
    public static final WmDisplayCutout NO_CUTOUT = new WmDisplayCutout(DisplayCutout.NO_CUTOUT, null);
    public final Size mFrameSize;
    public final DisplayCutout mInner;

    public WmDisplayCutout(DisplayCutout displayCutout, Size size) {
        this.mInner = displayCutout;
        this.mFrameSize = size;
    }

    public static WmDisplayCutout computeSafeInsets(DisplayCutout displayCutout, int i, int i2) {
        if (displayCutout == DisplayCutout.NO_CUTOUT) {
            return NO_CUTOUT;
        }
        return new WmDisplayCutout(displayCutout.replaceSafeInsets(DisplayCutout.computeSafeInsets(i, i2, displayCutout)), new Size(i, i2));
    }

    public DisplayCutout getDisplayCutout() {
        return this.mInner;
    }

    public boolean equals(Object obj) {
        if (obj instanceof WmDisplayCutout) {
            WmDisplayCutout wmDisplayCutout = (WmDisplayCutout) obj;
            return Objects.equals(this.mInner, wmDisplayCutout.mInner) && Objects.equals(this.mFrameSize, wmDisplayCutout.mFrameSize);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mInner, this.mFrameSize);
    }

    public String toString() {
        return "WmDisplayCutout{" + this.mInner + ", mFrameSize=" + this.mFrameSize + '}';
    }
}
