package com.android.server.timedetector;

import android.app.time.TimeConfiguration;
import com.android.server.timezonedetector.StateChangeListener;
/* loaded from: classes2.dex */
public interface ServiceConfigAccessor {
    void addConfigurationInternalChangeListener(StateChangeListener stateChangeListener);

    ConfigurationInternal getConfigurationInternal(int i);

    ConfigurationInternal getCurrentUserConfigurationInternal();

    boolean updateConfiguration(int i, TimeConfiguration timeConfiguration, boolean z);
}
