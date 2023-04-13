package com.android.server.timezonedetector;
/* loaded from: classes2.dex */
public interface CurrentUserIdentityInjector {
    public static final CurrentUserIdentityInjector REAL = new Real();

    /* loaded from: classes2.dex */
    public static class Real implements CurrentUserIdentityInjector {
    }
}
