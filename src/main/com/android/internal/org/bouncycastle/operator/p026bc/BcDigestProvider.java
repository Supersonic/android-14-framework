package com.android.internal.org.bouncycastle.operator.p026bc;

import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
/* renamed from: com.android.internal.org.bouncycastle.operator.bc.BcDigestProvider */
/* loaded from: classes4.dex */
public interface BcDigestProvider {
    ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException;
}
