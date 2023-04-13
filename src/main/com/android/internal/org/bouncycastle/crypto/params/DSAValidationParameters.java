package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class DSAValidationParameters {
    private int counter;
    private byte[] seed;
    private int usageIndex;

    public DSAValidationParameters(byte[] seed, int counter) {
        this(seed, counter, -1);
    }

    public DSAValidationParameters(byte[] seed, int counter, int usageIndex) {
        this.seed = Arrays.clone(seed);
        this.counter = counter;
        this.usageIndex = usageIndex;
    }

    public int getCounter() {
        return this.counter;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public int getUsageIndex() {
        return this.usageIndex;
    }

    public int hashCode() {
        return this.counter ^ Arrays.hashCode(this.seed);
    }

    public boolean equals(Object o) {
        if (o instanceof DSAValidationParameters) {
            DSAValidationParameters other = (DSAValidationParameters) o;
            if (other.counter != this.counter) {
                return false;
            }
            return Arrays.areEqual(this.seed, other.seed);
        }
        return false;
    }
}
