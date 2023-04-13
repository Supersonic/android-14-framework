package android.hardware.location;

import android.annotation.SystemApi;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@SystemApi
/* loaded from: classes2.dex */
public class ContextHubTransaction<T> {
    public static final int RESULT_FAILED_AT_HUB = 5;
    public static final int RESULT_FAILED_BAD_PARAMS = 2;
    public static final int RESULT_FAILED_BUSY = 4;
    public static final int RESULT_FAILED_HAL_UNAVAILABLE = 8;
    public static final int RESULT_FAILED_SERVICE_INTERNAL_FAILURE = 7;
    public static final int RESULT_FAILED_TIMEOUT = 6;
    public static final int RESULT_FAILED_UNINITIALIZED = 3;
    public static final int RESULT_FAILED_UNKNOWN = 1;
    public static final int RESULT_SUCCESS = 0;
    private static final String TAG = "ContextHubTransaction";
    public static final int TYPE_DISABLE_NANOAPP = 3;
    public static final int TYPE_ENABLE_NANOAPP = 2;
    public static final int TYPE_LOAD_NANOAPP = 0;
    public static final int TYPE_QUERY_NANOAPPS = 4;
    public static final int TYPE_UNLOAD_NANOAPP = 1;
    private Response<T> mResponse;
    private int mTransactionType;
    private Executor mExecutor = null;
    private OnCompleteListener<T> mListener = null;
    private final CountDownLatch mDoneSignal = new CountDownLatch(1);
    private boolean mIsResponseSet = false;

    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface OnCompleteListener<L> {
        void onComplete(ContextHubTransaction<L> contextHubTransaction, Response<L> response);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Result {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Type {
    }

    /* loaded from: classes2.dex */
    public static class Response<R> {
        private R mContents;
        private int mResult;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Response(int result, R contents) {
            this.mResult = result;
            this.mContents = contents;
        }

        public int getResult() {
            return this.mResult;
        }

        public R getContents() {
            return this.mContents;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContextHubTransaction(int type) {
        this.mTransactionType = type;
    }

    public static String typeToString(int type, boolean upperCase) {
        switch (type) {
            case 0:
                return upperCase ? "Load" : "load";
            case 1:
                return upperCase ? "Unload" : "unload";
            case 2:
                return upperCase ? "Enable" : "enable";
            case 3:
                return upperCase ? "Disable" : "disable";
            case 4:
                return upperCase ? "Query" : "query";
            default:
                return upperCase ? "Unknown" : "unknown";
        }
    }

    public int getType() {
        return this.mTransactionType;
    }

    public Response<T> waitForResponse(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        boolean success = this.mDoneSignal.await(timeout, unit);
        if (!success) {
            throw new TimeoutException("Timed out while waiting for transaction");
        }
        return this.mResponse;
    }

    public void setOnCompleteListener(OnCompleteListener<T> listener, Executor executor) {
        synchronized (this) {
            Objects.requireNonNull(listener, "OnCompleteListener cannot be null");
            Objects.requireNonNull(executor, "Executor cannot be null");
            if (this.mListener != null) {
                throw new IllegalStateException("Cannot set ContextHubTransaction listener multiple times");
            }
            this.mListener = listener;
            this.mExecutor = executor;
            if (this.mDoneSignal.getCount() == 0) {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.location.ContextHubTransaction$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContextHubTransaction.this.lambda$setOnCompleteListener$0();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setOnCompleteListener$0() {
        this.mListener.onComplete(this, this.mResponse);
    }

    public void setOnCompleteListener(OnCompleteListener<T> listener) {
        setOnCompleteListener(listener, new HandlerExecutor(Handler.getMain()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setResponse(Response<T> response) {
        synchronized (this) {
            Objects.requireNonNull(response, "Response cannot be null");
            if (this.mIsResponseSet) {
                throw new IllegalStateException("Cannot set response of ContextHubTransaction multiple times");
            }
            this.mResponse = response;
            this.mIsResponseSet = true;
            this.mDoneSignal.countDown();
            if (this.mListener != null) {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.location.ContextHubTransaction$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContextHubTransaction.this.lambda$setResponse$1();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setResponse$1() {
        this.mListener.onComplete(this, this.mResponse);
    }
}
