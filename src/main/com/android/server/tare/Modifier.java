package com.android.server.tare;

import android.util.IndentingPrintWriter;
/* loaded from: classes2.dex */
public abstract class Modifier {
    public abstract void dump(IndentingPrintWriter indentingPrintWriter);

    public long getModifiedCostToProduce(long j) {
        return j;
    }

    public long getModifiedPrice(long j) {
        return j;
    }

    public abstract void setup();

    public abstract void tearDown();
}
