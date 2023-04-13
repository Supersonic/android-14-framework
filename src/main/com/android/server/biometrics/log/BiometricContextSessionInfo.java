package com.android.server.biometrics.log;

import com.android.internal.logging.InstanceId;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class BiometricContextSessionInfo {
    public final InstanceId mId;
    public final AtomicInteger mOrder = new AtomicInteger(0);

    public BiometricContextSessionInfo(InstanceId instanceId) {
        this.mId = instanceId;
    }

    public int getId() {
        return this.mId.getId();
    }

    public int getOrderAndIncrement() {
        return this.mOrder.getAndIncrement();
    }

    public String toString() {
        return "[sid: " + this.mId.getId() + "]";
    }
}
