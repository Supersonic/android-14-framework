package com.android.internal.org.bouncycastle.jcajce;

import com.android.internal.org.bouncycastle.util.Selector;
import com.android.internal.org.bouncycastle.util.Store;
import com.android.internal.org.bouncycastle.util.StoreException;
import java.security.cert.Certificate;
import java.util.Collection;
/* loaded from: classes4.dex */
public interface PKIXCertStore<T extends Certificate> extends Store<T> {
    @Override // com.android.internal.org.bouncycastle.util.Store
    Collection<T> getMatches(Selector<T> selector) throws StoreException;
}
