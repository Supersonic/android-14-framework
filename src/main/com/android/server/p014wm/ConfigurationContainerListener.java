package com.android.server.p014wm;

import android.content.res.Configuration;
/* renamed from: com.android.server.wm.ConfigurationContainerListener */
/* loaded from: classes2.dex */
public interface ConfigurationContainerListener {
    default void onMergedOverrideConfigurationChanged(Configuration configuration) {
    }

    default void onRequestedOverrideConfigurationChanged(Configuration configuration) {
    }
}
