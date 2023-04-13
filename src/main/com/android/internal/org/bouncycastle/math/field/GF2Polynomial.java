package com.android.internal.org.bouncycastle.math.field;

import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
class GF2Polynomial implements Polynomial {
    protected final int[] exponents;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GF2Polynomial(int[] exponents) {
        this.exponents = Arrays.clone(exponents);
    }

    @Override // com.android.internal.org.bouncycastle.math.field.Polynomial
    public int getDegree() {
        int[] iArr = this.exponents;
        return iArr[iArr.length - 1];
    }

    @Override // com.android.internal.org.bouncycastle.math.field.Polynomial
    public int[] getExponentsPresent() {
        return Arrays.clone(this.exponents);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GF2Polynomial)) {
            return false;
        }
        GF2Polynomial other = (GF2Polynomial) obj;
        return Arrays.areEqual(this.exponents, other.exponents);
    }

    public int hashCode() {
        return Arrays.hashCode(this.exponents);
    }
}
