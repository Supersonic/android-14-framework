package com.android.internal.org.bouncycastle.jcajce;

import com.android.internal.org.bouncycastle.util.Selector;
import com.android.internal.org.bouncycastle.util.Store;
import com.android.internal.org.bouncycastle.util.StoreException;
import java.security.cert.CRL;
import java.util.Collection;
/* loaded from: classes4.dex */
public interface PKIXCRLStore<T extends CRL> extends Store<T> {
    @Override // com.android.internal.org.bouncycastle.util.Store
    Collection<T> getMatches(Selector<T> selector) throws StoreException;
}
