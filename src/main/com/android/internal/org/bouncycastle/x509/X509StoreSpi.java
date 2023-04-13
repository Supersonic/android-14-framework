package com.android.internal.org.bouncycastle.x509;

import com.android.internal.org.bouncycastle.util.Selector;
import java.util.Collection;
/* loaded from: classes4.dex */
public abstract class X509StoreSpi {
    public abstract Collection engineGetMatches(Selector selector);

    public abstract void engineInit(X509StoreParameters x509StoreParameters);
}
