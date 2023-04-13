package android.hardware.usb;

import android.hardware.usb.IUsbOperationInternal;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class UsbOperationInternal extends IUsbOperationInternal.Stub {
    private static final String TAG = "UsbPortStatus";
    public static final int USB_OPERATION_ERROR_INTERNAL = 1;
    public static final int USB_OPERATION_ERROR_NOT_SUPPORTED = 2;
    public static final int USB_OPERATION_ERROR_PORT_MISMATCH = 3;
    public static final int USB_OPERATION_SUCCESS = 0;
    private static final int USB_OPERATION_TIMEOUT_MSECS = 5000;
    private boolean mAsynchronous;
    private Consumer<Integer> mConsumer;
    private Executor mExecutor;
    private final String mId;
    final ReentrantLock mLock;
    private boolean mOperationComplete;
    private final int mOperationID;
    final Condition mOperationWait;
    private int mResult;
    private int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    @interface UsbOperationStatus {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UsbOperationInternal(int operationID, String id, Executor executor, Consumer<Integer> consumer) {
        this.mAsynchronous = false;
        this.mResult = 0;
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mLock = reentrantLock;
        this.mOperationWait = reentrantLock.newCondition();
        this.mOperationID = operationID;
        this.mId = id;
        this.mExecutor = executor;
        this.mConsumer = consumer;
        this.mAsynchronous = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UsbOperationInternal(int operationID, String id) {
        this.mAsynchronous = false;
        this.mResult = 0;
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mLock = reentrantLock;
        this.mOperationWait = reentrantLock.newCondition();
        this.mOperationID = operationID;
        this.mId = id;
    }

    @Override // android.hardware.usb.IUsbOperationInternal
    public void onOperationComplete(int status) {
        this.mLock.lock();
        try {
            this.mOperationComplete = true;
            this.mStatus = status;
            Log.m108i(TAG, "Port:" + this.mId + " opID:" + this.mOperationID + " status:" + this.mStatus);
            if (this.mAsynchronous) {
                switch (this.mStatus) {
                    case 0:
                        this.mResult = 0;
                        break;
                    case 1:
                        this.mResult = 1;
                        break;
                    case 2:
                        this.mResult = 2;
                        break;
                    case 3:
                        this.mResult = 3;
                        break;
                    default:
                        this.mResult = 4;
                        break;
                }
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.usb.UsbOperationInternal$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        UsbOperationInternal.this.lambda$onOperationComplete$0();
                    }
                });
            } else {
                this.mOperationWait.signal();
            }
        } finally {
            this.mLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onOperationComplete$0() {
        this.mConsumer.accept(Integer.valueOf(this.mResult));
    }

    public void waitForOperationComplete() {
        this.mLock.lock();
        try {
            try {
                long now = System.currentTimeMillis();
                long deadline = 5000 + now;
                do {
                    this.mOperationWait.await(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    if (this.mOperationComplete) {
                        break;
                    }
                } while (System.currentTimeMillis() < deadline);
                if (!this.mOperationComplete) {
                    Log.m110e(TAG, "Port:" + this.mId + " opID:" + this.mOperationID + " operationComplete not received in 5000msecs");
                }
            } catch (InterruptedException e) {
                Log.m110e(TAG, "Port:" + this.mId + " opID:" + this.mOperationID + " operationComplete interrupted");
            }
        } finally {
            this.mLock.unlock();
        }
    }

    public int getStatus() {
        if (this.mOperationComplete) {
            return this.mStatus;
        }
        return 1;
    }
}
