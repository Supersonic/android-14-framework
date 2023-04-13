package com.android.server.backup.remote;

import android.app.backup.IBackupCallback;
import android.os.RemoteException;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes.dex */
public class FutureBackupCallback extends IBackupCallback.Stub {
    public final CompletableFuture<RemoteResult> mFuture;

    public FutureBackupCallback(CompletableFuture<RemoteResult> completableFuture) {
        this.mFuture = completableFuture;
    }

    public void operationComplete(long j) throws RemoteException {
        this.mFuture.complete(RemoteResult.m64of(j));
    }
}
