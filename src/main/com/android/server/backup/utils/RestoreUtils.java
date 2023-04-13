package com.android.server.backup.utils;

import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.backup.FileMetadata;
import com.android.server.backup.restore.RestoreDeleteObserver;
import com.android.server.backup.restore.RestorePolicy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
/* loaded from: classes.dex */
public class RestoreUtils {
    public static boolean installApk(InputStream inputStream, Context context, RestoreDeleteObserver restoreDeleteObserver, HashMap<String, Signature[]> hashMap, HashMap<String, RestorePolicy> hashMap2, FileMetadata fileMetadata, String str, BytesReadListener bytesReadListener, int i) {
        Slog.d("BackupManagerService", "Installing from backup: " + fileMetadata.packageName);
        try {
            new LocalIntentReceiver();
            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(1);
            sessionParams.setInstallerPackageName(str);
            int createSession = packageInstaller.createSession(sessionParams);
            try {
                PackageInstaller.Session openSession = packageInstaller.openSession(createSession);
                OutputStream openWrite = openSession.openWrite(fileMetadata.packageName, 0L, fileMetadata.size);
                try {
                    byte[] bArr = new byte[32768];
                    long j = fileMetadata.size;
                    while (j > 0) {
                        long j2 = 32768;
                        if (j2 >= j) {
                            j2 = j;
                        }
                        int read = inputStream.read(bArr, 0, (int) j2);
                        if (read >= 0) {
                            bytesReadListener.onBytesRead(read);
                        }
                        openWrite.write(bArr, 0, read);
                        j -= read;
                    }
                    if (openWrite != null) {
                        openWrite.close();
                    }
                    openSession.abandon();
                    openSession.close();
                    return hashMap2.get(fileMetadata.packageName) == RestorePolicy.ACCEPT;
                } catch (Throwable th) {
                    if (openWrite != null) {
                        try {
                            openWrite.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Exception e) {
                packageInstaller.abandonSession(createSession);
                throw e;
            }
        } catch (IOException unused) {
            Slog.e("BackupManagerService", "Unable to transcribe restored apk for install");
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class LocalIntentReceiver {
        public IIntentSender.Stub mLocalSender;
        public final Object mLock;
        @GuardedBy({"mLock"})
        public Intent mResult;

        public LocalIntentReceiver() {
            this.mLock = new Object();
            this.mResult = null;
            this.mLocalSender = new IIntentSender.Stub() { // from class: com.android.server.backup.utils.RestoreUtils.LocalIntentReceiver.1
                public void send(int i, Intent intent, String str, IBinder iBinder, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
                    synchronized (LocalIntentReceiver.this.mLock) {
                        LocalIntentReceiver.this.mResult = intent;
                        LocalIntentReceiver.this.mLock.notifyAll();
                    }
                }
            };
        }
    }
}
