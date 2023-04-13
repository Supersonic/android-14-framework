package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.p008os.IBinder;
/* loaded from: classes.dex */
public interface BaseClientRequest extends ObjectPoolItem {
    void execute(ClientTransactionHandler clientTransactionHandler, IBinder iBinder, PendingTransactionActions pendingTransactionActions);

    default void preExecute(ClientTransactionHandler client, IBinder token) {
    }

    default void postExecute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
    }
}
