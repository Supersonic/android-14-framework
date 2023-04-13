package com.android.server.backup.remote;

import android.os.RemoteException;
@FunctionalInterface
/* loaded from: classes.dex */
public interface RemoteCallable<T> {
    void call(T t) throws RemoteException;
}
