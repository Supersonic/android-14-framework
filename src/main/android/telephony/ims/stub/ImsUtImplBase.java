package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.telephony.ims.ImsUtListener;
import android.telephony.ims.stub.ImsUtImplBase;
import android.util.Log;
import com.android.ims.internal.IImsUt;
import com.android.ims.internal.IImsUtListener;
import com.android.internal.telephony.util.TelephonyUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
@SystemApi
/* loaded from: classes3.dex */
public class ImsUtImplBase {
    public static final int CALL_BARRING_ALL = 7;
    public static final int CALL_BARRING_ALL_INCOMING = 1;
    public static final int CALL_BARRING_ALL_OUTGOING = 2;
    public static final int CALL_BARRING_ANONYMOUS_INCOMING = 6;
    public static final int CALL_BARRING_INCOMING_ALL_SERVICES = 9;
    public static final int CALL_BARRING_OUTGOING_ALL_SERVICES = 8;
    public static final int CALL_BARRING_OUTGOING_INTL = 3;
    public static final int CALL_BARRING_OUTGOING_INTL_EXCL_HOME = 4;
    public static final int CALL_BARRING_SPECIFIC_INCOMING_CALLS = 10;
    public static final int CALL_BLOCKING_INCOMING_WHEN_ROAMING = 5;
    public static final int INVALID_RESULT = -1;
    private static final String TAG = "ImsUtImplBase";
    private Executor mExecutor = new PendingIntent$$ExternalSyntheticLambda1();
    private final IImsUt.Stub mServiceImpl = new BinderC33061();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallBarringMode {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.stub.ImsUtImplBase$1 */
    /* loaded from: classes3.dex */
    public class BinderC33061 extends IImsUt.Stub {
        private final Object mLock = new Object();
        private ImsUtListener mUtListener;

        BinderC33061() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$close$0() {
            ImsUtImplBase.this.close();
        }

        @Override // com.android.ims.internal.IImsUt
        public void close() throws RemoteException {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUtImplBase.BinderC33061.this.lambda$close$0();
                }
            }, "close");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCallBarring$1(int cbType) {
            return Integer.valueOf(ImsUtImplBase.this.queryCallBarring(cbType));
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCallBarring(final int cbType) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda7
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCallBarring$1;
                    lambda$queryCallBarring$1 = ImsUtImplBase.BinderC33061.this.lambda$queryCallBarring$1(cbType);
                    return lambda$queryCallBarring$1;
                }
            }, "queryCallBarring")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCallForward$2(int condition, String number) {
            return Integer.valueOf(ImsUtImplBase.this.queryCallForward(condition, number));
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCallForward(final int condition, final String number) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda13
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCallForward$2;
                    lambda$queryCallForward$2 = ImsUtImplBase.BinderC33061.this.lambda$queryCallForward$2(condition, number);
                    return lambda$queryCallForward$2;
                }
            }, "queryCallForward")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCallWaiting$3() {
            return Integer.valueOf(ImsUtImplBase.this.queryCallWaiting());
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCallWaiting() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda17
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCallWaiting$3;
                    lambda$queryCallWaiting$3 = ImsUtImplBase.BinderC33061.this.lambda$queryCallWaiting$3();
                    return lambda$queryCallWaiting$3;
                }
            }, "queryCallWaiting")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCLIR$4() {
            return Integer.valueOf(ImsUtImplBase.this.queryCLIR());
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCLIR() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda15
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCLIR$4;
                    lambda$queryCLIR$4 = ImsUtImplBase.BinderC33061.this.lambda$queryCLIR$4();
                    return lambda$queryCLIR$4;
                }
            }, "queryCLIR")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCLIP$5() {
            return Integer.valueOf(ImsUtImplBase.this.queryCLIP());
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCLIP() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda4
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCLIP$5;
                    lambda$queryCLIP$5 = ImsUtImplBase.BinderC33061.this.lambda$queryCLIP$5();
                    return lambda$queryCLIP$5;
                }
            }, "queryCLIP")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCOLR$6() {
            return Integer.valueOf(ImsUtImplBase.this.queryCOLR());
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCOLR() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda18
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCOLR$6;
                    lambda$queryCOLR$6 = ImsUtImplBase.BinderC33061.this.lambda$queryCOLR$6();
                    return lambda$queryCOLR$6;
                }
            }, "queryCOLR")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCOLP$7() {
            return Integer.valueOf(ImsUtImplBase.this.queryCOLP());
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCOLP() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda12
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCOLP$7;
                    lambda$queryCOLP$7 = ImsUtImplBase.BinderC33061.this.lambda$queryCOLP$7();
                    return lambda$queryCOLP$7;
                }
            }, "queryCOLP")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$transact$8(Bundle ssInfo) {
            return Integer.valueOf(ImsUtImplBase.this.transact(ssInfo));
        }

        @Override // com.android.ims.internal.IImsUt
        public int transact(final Bundle ssInfo) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda20
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$transact$8;
                    lambda$transact$8 = ImsUtImplBase.BinderC33061.this.lambda$transact$8(ssInfo);
                    return lambda$transact$8;
                }
            }, "transact")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCallBarring$9(int cbType, int action, String[] barrList) {
            return Integer.valueOf(ImsUtImplBase.this.updateCallBarring(cbType, action, barrList));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCallBarring(final int cbType, final int action, final String[] barrList) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda10
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCallBarring$9;
                    lambda$updateCallBarring$9 = ImsUtImplBase.BinderC33061.this.lambda$updateCallBarring$9(cbType, action, barrList);
                    return lambda$updateCallBarring$9;
                }
            }, "updateCallBarring")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCallForward$10(int action, int condition, String number, int serviceClass, int timeSeconds) {
            return Integer.valueOf(ImsUtImplBase.this.updateCallForward(action, condition, number, serviceClass, timeSeconds));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCallForward(final int action, final int condition, final String number, final int serviceClass, final int timeSeconds) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCallForward$10;
                    lambda$updateCallForward$10 = ImsUtImplBase.BinderC33061.this.lambda$updateCallForward$10(action, condition, number, serviceClass, timeSeconds);
                    return lambda$updateCallForward$10;
                }
            }, "updateCallForward")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCallWaiting$11(boolean enable, int serviceClass) {
            return Integer.valueOf(ImsUtImplBase.this.updateCallWaiting(enable, serviceClass));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCallWaiting(final boolean enable, final int serviceClass) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCallWaiting$11;
                    lambda$updateCallWaiting$11 = ImsUtImplBase.BinderC33061.this.lambda$updateCallWaiting$11(enable, serviceClass);
                    return lambda$updateCallWaiting$11;
                }
            }, "updateCallWaiting")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCLIR$12(int clirMode) {
            return Integer.valueOf(ImsUtImplBase.this.updateCLIR(clirMode));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCLIR(final int clirMode) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda11
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCLIR$12;
                    lambda$updateCLIR$12 = ImsUtImplBase.BinderC33061.this.lambda$updateCLIR$12(clirMode);
                    return lambda$updateCLIR$12;
                }
            }, "updateCLIR")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCLIP$13(boolean enable) {
            return Integer.valueOf(ImsUtImplBase.this.updateCLIP(enable));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCLIP(final boolean enable) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda14
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCLIP$13;
                    lambda$updateCLIP$13 = ImsUtImplBase.BinderC33061.this.lambda$updateCLIP$13(enable);
                    return lambda$updateCLIP$13;
                }
            }, "updateCLIP")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCOLR$14(int presentation) {
            return Integer.valueOf(ImsUtImplBase.this.updateCOLR(presentation));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCOLR(final int presentation) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCOLR$14;
                    lambda$updateCOLR$14 = ImsUtImplBase.BinderC33061.this.lambda$updateCOLR$14(presentation);
                    return lambda$updateCOLR$14;
                }
            }, "updateCOLR")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCOLP$15(boolean enable) {
            return Integer.valueOf(ImsUtImplBase.this.updateCOLP(enable));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCOLP(final boolean enable) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda9
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCOLP$15;
                    lambda$updateCOLP$15 = ImsUtImplBase.BinderC33061.this.lambda$updateCOLP$15(enable);
                    return lambda$updateCOLP$15;
                }
            }, "updateCOLP")).intValue();
        }

        @Override // com.android.ims.internal.IImsUt
        public void setListener(final IImsUtListener listener) throws RemoteException {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    ImsUtImplBase.BinderC33061.this.lambda$setListener$16(listener);
                }
            }, "setListener");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setListener$16(IImsUtListener listener) {
            ImsUtListener imsUtListener = this.mUtListener;
            if (imsUtListener != null && !imsUtListener.getListenerInterface().asBinder().isBinderAlive()) {
                Log.m104w(ImsUtImplBase.TAG, "setListener: discarding dead Binder");
                this.mUtListener = null;
            }
            ImsUtListener imsUtListener2 = this.mUtListener;
            if (imsUtListener2 != null && listener != null && Objects.equals(imsUtListener2.getListenerInterface().asBinder(), listener.asBinder())) {
                return;
            }
            if (listener == null) {
                this.mUtListener = null;
            } else if (listener != null && this.mUtListener == null) {
                this.mUtListener = new ImsUtListener(listener);
            } else {
                Log.m104w(ImsUtImplBase.TAG, "setListener is being called when there is already an active listener");
                this.mUtListener = new ImsUtListener(listener);
            }
            ImsUtImplBase.this.setListener(this.mUtListener);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCallBarringForServiceClass$17(int cbType, int serviceClass) {
            return Integer.valueOf(ImsUtImplBase.this.queryCallBarringForServiceClass(cbType, serviceClass));
        }

        @Override // com.android.ims.internal.IImsUt
        public int queryCallBarringForServiceClass(final int cbType, final int serviceClass) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda8
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCallBarringForServiceClass$17;
                    lambda$queryCallBarringForServiceClass$17 = ImsUtImplBase.BinderC33061.this.lambda$queryCallBarringForServiceClass$17(cbType, serviceClass);
                    return lambda$queryCallBarringForServiceClass$17;
                }
            }, "queryCallBarringForServiceClass")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCallBarringForServiceClass$18(int cbType, int action, String[] barrList, int serviceClass) {
            return Integer.valueOf(ImsUtImplBase.this.updateCallBarringForServiceClass(cbType, action, barrList, serviceClass));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCallBarringForServiceClass(final int cbType, final int action, final String[] barrList, final int serviceClass) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda21
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCallBarringForServiceClass$18;
                    lambda$updateCallBarringForServiceClass$18 = ImsUtImplBase.BinderC33061.this.lambda$updateCallBarringForServiceClass$18(cbType, action, barrList, serviceClass);
                    return lambda$updateCallBarringForServiceClass$18;
                }
            }, "updateCallBarringForServiceClass")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$updateCallBarringWithPassword$19(int cbType, int action, String[] barrList, int serviceClass, String password) {
            return Integer.valueOf(ImsUtImplBase.this.updateCallBarringWithPassword(cbType, action, barrList, serviceClass, password));
        }

        @Override // com.android.ims.internal.IImsUt
        public int updateCallBarringWithPassword(final int cbType, final int action, final String[] barrList, final int serviceClass, final String password) throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$updateCallBarringWithPassword$19;
                    lambda$updateCallBarringWithPassword$19 = ImsUtImplBase.BinderC33061.this.lambda$updateCallBarringWithPassword$19(cbType, action, barrList, serviceClass, password);
                    return lambda$updateCallBarringWithPassword$19;
                }
            }, "updateCallBarringWithPassword")).intValue();
        }

        private void executeMethodAsync(final Runnable r, String errorLogName) throws RemoteException {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, ImsUtImplBase.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(ImsUtImplBase.TAG, "ImsUtImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
                throw new RemoteException(e.getMessage());
            }
        }

        private <T> T executeMethodAsyncForResult(final Supplier<T> r, String errorLogName) throws RemoteException {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.stub.ImsUtImplBase$1$$ExternalSyntheticLambda16
                @Override // java.util.function.Supplier
                public final Object get() {
                    Object runWithCleanCallingIdentity;
                    runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                    return runWithCleanCallingIdentity;
                }
            }, ImsUtImplBase.this.mExecutor);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.m104w(ImsUtImplBase.TAG, "ImsUtImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
                throw new RemoteException(e.getMessage());
            }
        }
    }

    public void close() {
    }

    public int queryCallBarring(int cbType) {
        return -1;
    }

    public int queryCallBarringForServiceClass(int cbType, int serviceClass) {
        return -1;
    }

    public int queryCallForward(int condition, String number) {
        return -1;
    }

    public int queryCallWaiting() {
        return -1;
    }

    public int queryCLIR() {
        return queryClir();
    }

    public int queryCLIP() {
        return queryClip();
    }

    public int queryCOLR() {
        return queryColr();
    }

    public int queryCOLP() {
        return queryColp();
    }

    public int queryClir() {
        return -1;
    }

    public int queryClip() {
        return -1;
    }

    public int queryColr() {
        return -1;
    }

    public int queryColp() {
        return -1;
    }

    public int transact(Bundle ssInfo) {
        return -1;
    }

    public int updateCallBarring(int cbType, int action, String[] barrList) {
        return -1;
    }

    public int updateCallBarringForServiceClass(int cbType, int action, String[] barrList, int serviceClass) {
        return -1;
    }

    public int updateCallBarringWithPassword(int cbType, int action, String[] barrList, int serviceClass, String password) {
        return -1;
    }

    public int updateCallForward(int action, int condition, String number, int serviceClass, int timeSeconds) {
        return 0;
    }

    public int updateCallWaiting(boolean enable, int serviceClass) {
        return -1;
    }

    public int updateCLIR(int clirMode) {
        return updateClir(clirMode);
    }

    public int updateCLIP(boolean enable) {
        return updateClip(enable);
    }

    public int updateCOLR(int presentation) {
        return updateColr(presentation);
    }

    public int updateCOLP(boolean enable) {
        return updateColp(enable);
    }

    public int updateClir(int clirMode) {
        return -1;
    }

    public int updateClip(boolean enable) {
        return -1;
    }

    public int updateColr(int presentation) {
        return -1;
    }

    public int updateColp(boolean enable) {
        return -1;
    }

    public void setListener(ImsUtListener listener) {
    }

    public IImsUt getInterface() {
        return this.mServiceImpl;
    }

    public final void setDefaultExecutor(Executor executor) {
        this.mExecutor = executor;
    }
}
