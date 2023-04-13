package com.android.server;

import android.content.Context;
import com.android.internal.util.Preconditions;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public final class FactoryResetter {
    public static final AtomicBoolean sFactoryResetting = new AtomicBoolean(false);

    public static boolean isFactoryResetting() {
        return sFactoryResetting.get();
    }

    @Deprecated
    public static void setFactoryResetting(Context context) {
        Preconditions.checkCallAuthorization(context.checkCallingOrSelfPermission("android.permission.MASTER_CLEAR") == 0);
        sFactoryResetting.set(true);
    }
}
