package com.android.server.p011pm;
/* renamed from: com.android.server.pm.ReconcileFailure */
/* loaded from: classes2.dex */
final class ReconcileFailure extends PackageManagerException {
    public static ReconcileFailure ofInternalError(String str, int i) {
        return new ReconcileFailure(str, i);
    }

    public ReconcileFailure(String str, int i) {
        super(-110, "Reconcile failed: " + str, i);
    }

    public ReconcileFailure(int i, String str) {
        super(i, "Reconcile failed: " + str);
    }

    public ReconcileFailure(PackageManagerException packageManagerException) {
        this(packageManagerException.error, packageManagerException.getMessage());
    }
}
