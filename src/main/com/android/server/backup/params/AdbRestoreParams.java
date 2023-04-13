package com.android.server.backup.params;

import android.os.ParcelFileDescriptor;
/* loaded from: classes.dex */
public class AdbRestoreParams extends AdbParams {
    public AdbRestoreParams(ParcelFileDescriptor parcelFileDescriptor) {
        this.f1131fd = parcelFileDescriptor;
    }
}
