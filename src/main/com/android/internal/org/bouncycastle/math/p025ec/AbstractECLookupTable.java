package com.android.internal.org.bouncycastle.math.p025ec;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.AbstractECLookupTable */
/* loaded from: classes4.dex */
public abstract class AbstractECLookupTable implements ECLookupTable {
    @Override // com.android.internal.org.bouncycastle.math.p025ec.ECLookupTable
    public ECPoint lookupVar(int index) {
        return lookup(index);
    }
}
