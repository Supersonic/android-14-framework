package com.android.server.p011pm;
/* renamed from: com.android.server.pm.SystemDeleteException */
/* loaded from: classes2.dex */
final class SystemDeleteException extends Exception {
    final PackageManagerException mReason;

    public SystemDeleteException(PackageManagerException packageManagerException) {
        this.mReason = packageManagerException;
    }
}
