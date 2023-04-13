package com.android.internal.org.bouncycastle.math.field;

import java.math.BigInteger;
/* loaded from: classes4.dex */
class PrimeField implements FiniteField {
    protected final BigInteger characteristic;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrimeField(BigInteger characteristic) {
        this.characteristic = characteristic;
    }

    @Override // com.android.internal.org.bouncycastle.math.field.FiniteField
    public BigInteger getCharacteristic() {
        return this.characteristic;
    }

    @Override // com.android.internal.org.bouncycastle.math.field.FiniteField
    public int getDimension() {
        return 1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PrimeField)) {
            return false;
        }
        PrimeField other = (PrimeField) obj;
        return this.characteristic.equals(other.characteristic);
    }

    public int hashCode() {
        return this.characteristic.hashCode();
    }
}
