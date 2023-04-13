package com.android.internal.org.bouncycastle.math.p025ec.endo;

import com.android.internal.org.bouncycastle.math.p025ec.ECPointMap;
/* renamed from: com.android.internal.org.bouncycastle.math.ec.endo.ECEndomorphism */
/* loaded from: classes4.dex */
public interface ECEndomorphism {
    ECPointMap getPointMap();

    boolean hasEfficientPointMap();
}
