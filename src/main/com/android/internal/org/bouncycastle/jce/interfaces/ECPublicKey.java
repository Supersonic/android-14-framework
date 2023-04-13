package com.android.internal.org.bouncycastle.jce.interfaces;

import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import java.security.PublicKey;
/* loaded from: classes4.dex */
public interface ECPublicKey extends ECKey, PublicKey {
    ECPoint getQ();
}
