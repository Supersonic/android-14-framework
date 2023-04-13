package com.android.internal.org.bouncycastle.math.field;

import com.android.internal.org.bouncycastle.util.Integers;
import java.math.BigInteger;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class GenericPolynomialExtensionField implements PolynomialExtensionField {
    protected final Polynomial minimalPolynomial;
    protected final FiniteField subfield;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenericPolynomialExtensionField(FiniteField subfield, Polynomial polynomial) {
        this.subfield = subfield;
        this.minimalPolynomial = polynomial;
    }

    @Override // com.android.internal.org.bouncycastle.math.field.FiniteField
    public BigInteger getCharacteristic() {
        return this.subfield.getCharacteristic();
    }

    @Override // com.android.internal.org.bouncycastle.math.field.FiniteField
    public int getDimension() {
        return this.subfield.getDimension() * this.minimalPolynomial.getDegree();
    }

    @Override // com.android.internal.org.bouncycastle.math.field.ExtensionField
    public FiniteField getSubfield() {
        return this.subfield;
    }

    @Override // com.android.internal.org.bouncycastle.math.field.ExtensionField
    public int getDegree() {
        return this.minimalPolynomial.getDegree();
    }

    @Override // com.android.internal.org.bouncycastle.math.field.PolynomialExtensionField
    public Polynomial getMinimalPolynomial() {
        return this.minimalPolynomial;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GenericPolynomialExtensionField) {
            GenericPolynomialExtensionField other = (GenericPolynomialExtensionField) obj;
            return this.subfield.equals(other.subfield) && this.minimalPolynomial.equals(other.minimalPolynomial);
        }
        return false;
    }

    public int hashCode() {
        return this.subfield.hashCode() ^ Integers.rotateLeft(this.minimalPolynomial.hashCode(), 16);
    }
}
