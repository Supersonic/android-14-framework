package com.android.internal.logging;

import java.security.SecureRandom;
import java.util.Random;
/* loaded from: classes4.dex */
public class InstanceIdSequence {
    protected final int mInstanceIdMax;
    private final Random mRandom = new SecureRandom();

    public InstanceIdSequence(int instanceIdMax) {
        this.mInstanceIdMax = Math.min(Math.max(1, instanceIdMax), 1048576);
    }

    public InstanceId newInstanceId() {
        return newInstanceIdInternal(this.mRandom.nextInt(this.mInstanceIdMax) + 1);
    }

    protected InstanceId newInstanceIdInternal(int id) {
        return new InstanceId(id);
    }
}
