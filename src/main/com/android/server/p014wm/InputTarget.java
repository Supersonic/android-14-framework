package com.android.server.p014wm;

import android.util.proto.ProtoOutputStream;
import android.view.IWindow;
/* renamed from: com.android.server.wm.InputTarget */
/* loaded from: classes2.dex */
public interface InputTarget {
    boolean canScreenshotIme();

    void dumpProto(ProtoOutputStream protoOutputStream, long j, int i);

    ActivityRecord getActivityRecord();

    DisplayContent getDisplayContent();

    int getDisplayId();

    IWindow getIWindow();

    InsetsControlTarget getImeControlTarget();

    int getPid();

    WindowState getWindowState();

    void handleTapOutsideFocusInsideSelf();

    void handleTapOutsideFocusOutsideSelf();

    boolean isInputMethodClientFocus(int i, int i2);

    boolean receiveFocusFromTapOutside();

    boolean shouldControlIme();
}
