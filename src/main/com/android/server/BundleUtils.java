package com.android.server;

import android.os.Bundle;
/* loaded from: classes.dex */
public final class BundleUtils {
    public static boolean isEmpty(Bundle bundle) {
        return bundle == null || bundle.size() == 0;
    }

    public static Bundle clone(Bundle bundle) {
        return bundle != null ? new Bundle(bundle) : new Bundle();
    }
}
