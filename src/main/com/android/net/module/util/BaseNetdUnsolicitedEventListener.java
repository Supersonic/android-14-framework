package com.android.net.module.util;

import android.net.INetdUnsolicitedEventListener;
import androidx.annotation.NonNull;
/* loaded from: classes.dex */
public class BaseNetdUnsolicitedEventListener extends INetdUnsolicitedEventListener.Stub {
    @Override // android.net.INetdUnsolicitedEventListener
    public String getInterfaceHash() {
        return "38614f80a23b92603d4851177e57c460aec1b606";
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public int getInterfaceVersion() {
        return 13;
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceAdded(@NonNull String str) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceAddressRemoved(@NonNull String str, @NonNull String str2, int i, int i2) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceAddressUpdated(@NonNull String str, String str2, int i, int i2) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceChanged(@NonNull String str, boolean z) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceClassActivityChanged(boolean z, int i, long j, int i2) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceDnsServerInfo(@NonNull String str, long j, @NonNull String[] strArr) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceLinkStateChanged(@NonNull String str, boolean z) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onInterfaceRemoved(@NonNull String str) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onQuotaLimitReached(@NonNull String str, @NonNull String str2) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onRouteChanged(boolean z, @NonNull String str, @NonNull String str2, @NonNull String str3) {
    }

    @Override // android.net.INetdUnsolicitedEventListener
    public void onStrictCleartextDetected(int i, @NonNull String str) {
    }
}
