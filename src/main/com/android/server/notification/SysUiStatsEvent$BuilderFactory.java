package com.android.server.notification;

import android.util.StatsEvent;
/* loaded from: classes2.dex */
public class SysUiStatsEvent$BuilderFactory {
    public SysUiStatsEvent$Builder newBuilder() {
        return new SysUiStatsEvent$Builder(StatsEvent.newBuilder());
    }
}
