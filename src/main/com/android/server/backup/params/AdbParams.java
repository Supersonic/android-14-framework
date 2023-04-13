package com.android.server.backup.params;

import android.app.backup.IFullBackupRestoreObserver;
import android.os.ParcelFileDescriptor;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class AdbParams {
    public String curPassword;
    public String encryptPassword;

    /* renamed from: fd */
    public ParcelFileDescriptor f1131fd;
    public final AtomicBoolean latch = new AtomicBoolean(false);
    public IFullBackupRestoreObserver observer;
}
