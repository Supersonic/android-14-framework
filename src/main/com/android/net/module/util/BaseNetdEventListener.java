package com.android.net.module.util;

import android.net.metrics.INetdEventListener;
/* loaded from: classes.dex */
public class BaseNetdEventListener extends INetdEventListener.Stub {
    @Override // android.net.metrics.INetdEventListener
    public String getInterfaceHash() {
        return INetdEventListener.HASH;
    }

    @Override // android.net.metrics.INetdEventListener
    public int getInterfaceVersion() {
        return 1;
    }

    @Override // android.net.metrics.INetdEventListener
    public void onConnectEvent(int i, int i2, int i3, String str, int i4, int i5) {
    }

    @Override // android.net.metrics.INetdEventListener
    public void onDnsEvent(int i, int i2, int i3, int i4, String str, String[] strArr, int i5, int i6) {
    }

    @Override // android.net.metrics.INetdEventListener
    public void onNat64PrefixEvent(int i, boolean z, String str, int i2) {
    }

    @Override // android.net.metrics.INetdEventListener
    public void onPrivateDnsValidationEvent(int i, String str, String str2, boolean z) {
    }

    @Override // android.net.metrics.INetdEventListener
    public void onTcpSocketStatsEvent(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5) {
    }

    @Override // android.net.metrics.INetdEventListener
    public void onWakeupEvent(String str, int i, int i2, int i3, byte[] bArr, String str2, String str3, int i4, int i5, long j) {
    }
}
