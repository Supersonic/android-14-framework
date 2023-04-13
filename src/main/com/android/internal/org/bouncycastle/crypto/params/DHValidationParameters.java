package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class DHValidationParameters {
    private int counter;
    private byte[] seed;

    public DHValidationParameters(byte[] seed, int counter) {
        this.seed = Arrays.clone(seed);
        this.counter = counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public boolean equals(Object o) {
        if (o instanceof DHValidationParameters) {
            DHValidationParameters other = (DHValidationParameters) o;
            if (other.counter != this.counter) {
                return false;
            }
            return Arrays.areEqual(this.seed, other.seed);
        }
        return false;
    }

    public int hashCode() {
        return this.counter ^ Arrays.hashCode(this.seed);
    }
}
