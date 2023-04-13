package android.content.p001pm;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.content.pm.InstallationFile */
/* loaded from: classes.dex */
public final class InstallationFile {
    private final InstallationFileParcel mParcel;

    public InstallationFile(int location, String name, long lengthBytes, byte[] metadata, byte[] signature) {
        InstallationFileParcel installationFileParcel = new InstallationFileParcel();
        this.mParcel = installationFileParcel;
        installationFileParcel.location = location;
        installationFileParcel.name = name;
        installationFileParcel.size = lengthBytes;
        installationFileParcel.metadata = metadata;
        installationFileParcel.signature = signature;
    }

    public int getLocation() {
        return this.mParcel.location;
    }

    public String getName() {
        return this.mParcel.name;
    }

    public long getLengthBytes() {
        return this.mParcel.size;
    }

    public byte[] getMetadata() {
        return this.mParcel.metadata;
    }

    public byte[] getSignature() {
        return this.mParcel.signature;
    }

    public InstallationFileParcel getData() {
        return this.mParcel;
    }
}
