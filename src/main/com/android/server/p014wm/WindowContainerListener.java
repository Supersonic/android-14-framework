package com.android.server.p014wm;
/* renamed from: com.android.server.wm.WindowContainerListener */
/* loaded from: classes2.dex */
public interface WindowContainerListener extends ConfigurationContainerListener {
    default void onDisplayChanged(DisplayContent displayContent) {
    }

    default void onRemoved() {
    }

    default void onVisibleRequestedChanged(boolean z) {
    }
}
