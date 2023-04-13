package com.android.server.notification;

import android.util.StatsEvent;
/* loaded from: classes2.dex */
public class SysUiStatsEvent$Builder {
    public final StatsEvent.Builder mBuilder;

    public SysUiStatsEvent$Builder(StatsEvent.Builder builder) {
        this.mBuilder = builder;
    }

    public StatsEvent build() {
        return this.mBuilder.build();
    }

    public SysUiStatsEvent$Builder setAtomId(int i) {
        this.mBuilder.setAtomId(i);
        return this;
    }

    public SysUiStatsEvent$Builder writeInt(int i) {
        this.mBuilder.writeInt(i);
        return this;
    }

    public SysUiStatsEvent$Builder addBooleanAnnotation(byte b, boolean z) {
        this.mBuilder.addBooleanAnnotation(b, z);
        return this;
    }

    public SysUiStatsEvent$Builder writeString(String str) {
        this.mBuilder.writeString(str);
        return this;
    }

    public SysUiStatsEvent$Builder writeBoolean(boolean z) {
        this.mBuilder.writeBoolean(z);
        return this;
    }

    public SysUiStatsEvent$Builder writeByteArray(byte[] bArr) {
        this.mBuilder.writeByteArray(bArr);
        return this;
    }
}
