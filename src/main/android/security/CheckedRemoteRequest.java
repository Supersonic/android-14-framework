package android.security;

import android.p008os.RemoteException;
@FunctionalInterface
/* loaded from: classes3.dex */
interface CheckedRemoteRequest<R> {
    R execute() throws RemoteException;
}
