package android.accounts;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public interface AccountManagerFuture<V> {
    boolean cancel(boolean z);

    V getResult() throws OperationCanceledException, IOException, AuthenticatorException;

    V getResult(long j, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException;

    boolean isCancelled();

    boolean isDone();
}
