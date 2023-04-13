package com.android.server.power;

import com.android.internal.annotations.VisibleForTesting;
@VisibleForTesting
/* loaded from: classes2.dex */
interface SystemPropertiesWrapper {
    String get(String str, String str2);

    void set(String str, String str2);
}
