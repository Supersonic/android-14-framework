package android.p008os;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* renamed from: android.os.SynchronousResultReceiver */
/* loaded from: classes3.dex */
public class SynchronousResultReceiver extends ResultReceiver {
    private final CompletableFuture<Result> mFuture;
    private final String mName;

    /* renamed from: android.os.SynchronousResultReceiver$Result */
    /* loaded from: classes3.dex */
    public static class Result {
        public Bundle bundle;
        public int resultCode;

        public Result(int resultCode, Bundle bundle) {
            this.resultCode = resultCode;
            this.bundle = bundle;
        }
    }

    public SynchronousResultReceiver() {
        super((Handler) null);
        this.mFuture = new CompletableFuture<>();
        this.mName = null;
    }

    public SynchronousResultReceiver(String name) {
        super((Handler) null);
        this.mFuture = new CompletableFuture<>();
        this.mName = name;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.p008os.ResultReceiver
    public final void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        this.mFuture.complete(new Result(resultCode, resultData));
    }

    public String getName() {
        return this.mName;
    }

    public Result awaitResult(long timeoutMillis) throws TimeoutException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (timeoutMillis >= 0) {
            try {
                return this.mFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                timeoutMillis -= deadline - System.currentTimeMillis();
            } catch (ExecutionException e2) {
                throw new AssertionError("Error receiving response", e2);
            }
        }
        throw new TimeoutException();
    }
}
