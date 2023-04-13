package com.android.server.p014wm;

import android.app.IApplicationThread;
import android.app.servertransaction.ActivityLifecycleItem;
import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.ClientTransactionItem;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
/* renamed from: com.android.server.wm.ClientLifecycleManager */
/* loaded from: classes2.dex */
public class ClientLifecycleManager {
    public void scheduleTransaction(ClientTransaction clientTransaction) throws RemoteException {
        IApplicationThread client = clientTransaction.getClient();
        clientTransaction.schedule();
        if (client instanceof Binder) {
            return;
        }
        clientTransaction.recycle();
    }

    public void scheduleTransaction(IApplicationThread iApplicationThread, IBinder iBinder, ActivityLifecycleItem activityLifecycleItem) throws RemoteException {
        scheduleTransaction(transactionWithState(iApplicationThread, iBinder, activityLifecycleItem));
    }

    public void scheduleTransaction(IApplicationThread iApplicationThread, IBinder iBinder, ClientTransactionItem clientTransactionItem) throws RemoteException {
        scheduleTransaction(transactionWithCallback(iApplicationThread, iBinder, clientTransactionItem));
    }

    public void scheduleTransaction(IApplicationThread iApplicationThread, ClientTransactionItem clientTransactionItem) throws RemoteException {
        scheduleTransaction(transactionWithCallback(iApplicationThread, null, clientTransactionItem));
    }

    public static ClientTransaction transactionWithState(IApplicationThread iApplicationThread, IBinder iBinder, ActivityLifecycleItem activityLifecycleItem) {
        ClientTransaction obtain = ClientTransaction.obtain(iApplicationThread, iBinder);
        obtain.setLifecycleStateRequest(activityLifecycleItem);
        return obtain;
    }

    public static ClientTransaction transactionWithCallback(IApplicationThread iApplicationThread, IBinder iBinder, ClientTransactionItem clientTransactionItem) {
        ClientTransaction obtain = ClientTransaction.obtain(iApplicationThread, iBinder);
        obtain.addCallback(clientTransactionItem);
        return obtain;
    }
}
