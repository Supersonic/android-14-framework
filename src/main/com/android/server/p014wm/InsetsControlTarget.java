package com.android.server.p014wm;

import android.view.WindowInsets;
import android.view.inputmethod.ImeTracker;
/* renamed from: com.android.server.wm.InsetsControlTarget */
/* loaded from: classes2.dex */
public interface InsetsControlTarget {
    default boolean canShowTransient() {
        return false;
    }

    default WindowState getWindow() {
        return null;
    }

    default void hideInsets(int i, boolean z, ImeTracker.Token token) {
    }

    default void notifyInsetsControlChanged() {
    }

    default void showInsets(int i, boolean z, ImeTracker.Token token) {
    }

    default boolean isRequestedVisible(int i) {
        return (WindowInsets.Type.defaultVisible() & i) != 0;
    }

    default int getRequestedVisibleTypes() {
        return WindowInsets.Type.defaultVisible();
    }

    static WindowState asWindowOrNull(InsetsControlTarget insetsControlTarget) {
        if (insetsControlTarget != null) {
            return insetsControlTarget.getWindow();
        }
        return null;
    }
}
