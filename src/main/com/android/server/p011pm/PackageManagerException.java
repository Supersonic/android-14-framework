package com.android.server.p011pm;

import com.android.server.p011pm.Installer;
/* renamed from: com.android.server.pm.PackageManagerException */
/* loaded from: classes2.dex */
public class PackageManagerException extends Exception {
    public final int error;
    public final int internalErrorCode;

    public static PackageManagerException ofInternalError(String str, int i) {
        return new PackageManagerException(-110, str, i);
    }

    public PackageManagerException(int i, String str, int i2) {
        super(str);
        this.error = i;
        this.internalErrorCode = i2;
    }

    public PackageManagerException(int i, String str) {
        super(str);
        this.error = i;
        this.internalErrorCode = 0;
    }

    public PackageManagerException(int i, String str, Throwable th) {
        super(str, th);
        this.error = i;
        this.internalErrorCode = 0;
    }

    public PackageManagerException(Throwable th) {
        super(th);
        this.error = -110;
        this.internalErrorCode = 0;
    }

    public static PackageManagerException from(Installer.InstallerException installerException) throws PackageManagerException {
        throw new PackageManagerException(-110, installerException.getMessage(), installerException.getCause());
    }
}
