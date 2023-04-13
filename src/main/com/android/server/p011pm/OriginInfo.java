package com.android.server.p011pm;

import java.io.File;
/* renamed from: com.android.server.pm.OriginInfo */
/* loaded from: classes2.dex */
public final class OriginInfo {
    public final boolean mExisting;
    public final File mFile;
    public final File mResolvedFile;
    public final String mResolvedPath;
    public final boolean mStaged;

    public static OriginInfo fromNothing() {
        return new OriginInfo(null, false, false);
    }

    public static OriginInfo fromExistingFile(File file) {
        return new OriginInfo(file, false, true);
    }

    public static OriginInfo fromStagedFile(File file) {
        return new OriginInfo(file, true, false);
    }

    public OriginInfo(File file, boolean z, boolean z2) {
        this.mFile = file;
        this.mStaged = z;
        this.mExisting = z2;
        if (file != null) {
            this.mResolvedPath = file.getAbsolutePath();
            this.mResolvedFile = file;
            return;
        }
        this.mResolvedPath = null;
        this.mResolvedFile = null;
    }
}
