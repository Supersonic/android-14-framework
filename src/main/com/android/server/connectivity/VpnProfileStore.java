package com.android.server.connectivity;

import android.security.LegacyVpnProfileStore;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class VpnProfileStore {
    @VisibleForTesting
    public boolean put(String str, byte[] bArr) {
        return LegacyVpnProfileStore.put(str, bArr);
    }

    @VisibleForTesting
    public byte[] get(String str) {
        return LegacyVpnProfileStore.get(str);
    }

    @VisibleForTesting
    public boolean remove(String str) {
        return LegacyVpnProfileStore.remove(str);
    }

    @VisibleForTesting
    public String[] list(String str) {
        return LegacyVpnProfileStore.list(str);
    }
}
