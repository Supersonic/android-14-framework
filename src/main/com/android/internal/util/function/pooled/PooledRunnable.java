package com.android.internal.util.function.pooled;

import android.p008os.TraceNameSupplier;
import com.android.internal.util.FunctionalUtils;
/* loaded from: classes3.dex */
public interface PooledRunnable extends PooledLambda, Runnable, FunctionalUtils.ThrowingRunnable, TraceNameSupplier {
    @Override // com.android.internal.util.function.pooled.PooledSupplier.OfInt, com.android.internal.util.function.pooled.PooledSupplier.OfLong, com.android.internal.util.function.pooled.PooledSupplier.OfDouble
    PooledRunnable recycleOnUse();
}
