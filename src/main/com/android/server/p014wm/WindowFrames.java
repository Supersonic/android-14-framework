package com.android.server.p014wm;

import android.graphics.Rect;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.WindowFrames */
/* loaded from: classes2.dex */
public class WindowFrames {
    public static final StringBuilder sTmpSB = new StringBuilder();
    public boolean mContentChanged;
    public boolean mInsetsChanged;
    public boolean mParentFrameWasClippedByDisplayCutout;
    public final Rect mParentFrame = new Rect();
    public final Rect mDisplayFrame = new Rect();
    public final Rect mFrame = new Rect();
    public final Rect mLastFrame = new Rect();
    public final Rect mRelFrame = new Rect();
    public final Rect mLastRelFrame = new Rect();
    public boolean mFrameSizeChanged = false;
    public final Rect mCompatFrame = new Rect();
    public boolean mLastForceReportingResized = false;
    public boolean mForceReportingResized = false;

    public void setParentFrameWasClippedByDisplayCutout(boolean z) {
        this.mParentFrameWasClippedByDisplayCutout = z;
    }

    public boolean parentFrameWasClippedByDisplayCutout() {
        return this.mParentFrameWasClippedByDisplayCutout;
    }

    public boolean didFrameSizeChange() {
        return (this.mLastFrame.width() == this.mFrame.width() && this.mLastFrame.height() == this.mFrame.height()) ? false : true;
    }

    public boolean setReportResizeHints() {
        this.mLastForceReportingResized |= this.mForceReportingResized;
        boolean didFrameSizeChange = this.mFrameSizeChanged | didFrameSizeChange();
        this.mFrameSizeChanged = didFrameSizeChange;
        return this.mLastForceReportingResized || didFrameSizeChange;
    }

    public boolean isFrameSizeChangeReported() {
        return this.mFrameSizeChanged || didFrameSizeChange();
    }

    public void clearReportResizeHints() {
        this.mLastForceReportingResized = false;
        this.mFrameSizeChanged = false;
    }

    public void onResizeHandled() {
        this.mForceReportingResized = false;
    }

    public void forceReportingResized() {
        this.mForceReportingResized = true;
    }

    public void setContentChanged(boolean z) {
        this.mContentChanged = z;
    }

    public boolean hasContentChanged() {
        return this.mContentChanged;
    }

    public void setInsetsChanged(boolean z) {
        this.mInsetsChanged = z;
    }

    public boolean hasInsetsChanged() {
        return this.mInsetsChanged;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        this.mParentFrame.dumpDebug(protoOutputStream, 1146756268040L);
        this.mDisplayFrame.dumpDebug(protoOutputStream, 1146756268036L);
        this.mFrame.dumpDebug(protoOutputStream, 1146756268037L);
        this.mCompatFrame.dumpDebug(protoOutputStream, 1146756268048L);
        protoOutputStream.end(start);
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Frames: parent=");
        Rect rect = this.mParentFrame;
        StringBuilder sb2 = sTmpSB;
        sb.append(rect.toShortString(sb2));
        sb.append(" display=");
        sb.append(this.mDisplayFrame.toShortString(sb2));
        sb.append(" frame=");
        sb.append(this.mFrame.toShortString(sb2));
        sb.append(" last=");
        sb.append(this.mLastFrame.toShortString(sb2));
        sb.append(" insetsChanged=");
        sb.append(this.mInsetsChanged);
        printWriter.println(sb.toString());
    }

    public String getInsetsChangedInfo() {
        return "forceReportingResized=" + this.mLastForceReportingResized + " insetsChanged=" + this.mInsetsChanged;
    }
}
