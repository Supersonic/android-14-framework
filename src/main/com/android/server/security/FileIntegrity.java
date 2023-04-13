package com.android.server.security;

import android.annotation.SystemApi;
import android.os.ParcelFileDescriptor;
import com.android.internal.security.VerityUtils;
import java.io.File;
import java.io.IOException;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes2.dex */
public final class FileIntegrity {
    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    public static void setUpFsVerity(File file) throws IOException {
        VerityUtils.setUpFsverity(file.getAbsolutePath());
    }

    @SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
    public static void setUpFsVerity(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        VerityUtils.setUpFsverity(parcelFileDescriptor.getFd());
    }
}
