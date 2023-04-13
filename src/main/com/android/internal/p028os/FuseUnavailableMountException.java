package com.android.internal.p028os;
/* renamed from: com.android.internal.os.FuseUnavailableMountException */
/* loaded from: classes4.dex */
public class FuseUnavailableMountException extends Exception {
    public FuseUnavailableMountException(int mountId) {
        super("AppFuse mount point " + mountId + " is unavailable");
    }
}
