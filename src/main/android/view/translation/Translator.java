package android.view.translation;

import android.content.Context;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.RemoteException;
import android.service.translation.ITranslationCallback;
import android.util.Log;
import android.view.translation.ITranslationDirectManager;
import android.view.translation.Translator;
import com.android.internal.p028os.IResultReceiver;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class Translator {
    public static final String EXTRA_SERVICE_BINDER = "binder";
    public static final String EXTRA_SESSION_ID = "sessionId";
    private static final String TAG = "Translator";
    private final Context mContext;
    private boolean mDestroyed;
    private ITranslationDirectManager mDirectServiceBinder;
    private final Handler mHandler;
    private int mId;
    private final Object mLock;
    private final TranslationManager mManager;
    private final ServiceBinderReceiver mServiceBinderReceiver;
    private ITranslationManager mSystemServerBinder;
    private final TranslationContext mTranslationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ServiceBinderReceiver extends IResultReceiver.Stub {
        private Consumer<Translator> mCallback;
        private final CountDownLatch mLatch = new CountDownLatch(1);
        private int mSessionId;
        private final Translator mTranslator;

        ServiceBinderReceiver(Translator translator, Consumer<Translator> callback) {
            this.mTranslator = translator;
            this.mCallback = callback;
        }

        ServiceBinderReceiver(Translator translator) {
            this.mTranslator = translator;
        }

        int getSessionStateResult() throws TimeoutException {
            try {
                if (!this.mLatch.await(60000L, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Session not created in 60000ms");
                }
                return this.mSessionId;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new TimeoutException("Session not created because interrupted");
            }
        }

        @Override // com.android.internal.p028os.IResultReceiver
        public void send(int resultCode, Bundle resultData) {
            IBinder binder;
            if (resultCode == 2) {
                this.mLatch.countDown();
                Consumer<Translator> consumer = this.mCallback;
                if (consumer != null) {
                    consumer.accept(null);
                    return;
                }
                return;
            }
            if (resultData != null) {
                this.mSessionId = resultData.getInt("sessionId");
                binder = resultData.getBinder("binder");
                if (binder == null) {
                    Log.wtf(Translator.TAG, "No binder extra result");
                    return;
                }
            } else {
                binder = null;
            }
            this.mTranslator.setServiceBinder(binder);
            this.mLatch.countDown();
            Consumer<Translator> consumer2 = this.mCallback;
            if (consumer2 != null) {
                consumer2.accept(this.mTranslator);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public static final class TimeoutException extends Exception {
            private TimeoutException(String msg) {
                super(msg);
            }
        }
    }

    public Translator(Context context, TranslationContext translationContext, int sessionId, TranslationManager translationManager, Handler handler, ITranslationManager systemServerBinder, Consumer<Translator> callback) {
        this.mLock = new Object();
        this.mContext = context;
        this.mTranslationContext = translationContext;
        this.mId = sessionId;
        this.mManager = translationManager;
        this.mHandler = handler;
        this.mSystemServerBinder = systemServerBinder;
        ServiceBinderReceiver serviceBinderReceiver = new ServiceBinderReceiver(this, callback);
        this.mServiceBinderReceiver = serviceBinderReceiver;
        try {
            this.mSystemServerBinder.onSessionCreated(translationContext, this.mId, serviceBinderReceiver, context.getUserId());
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling startSession(): " + e);
        }
    }

    public Translator(Context context, TranslationContext translationContext, int sessionId, TranslationManager translationManager, Handler handler, ITranslationManager systemServerBinder) {
        this.mLock = new Object();
        this.mContext = context;
        this.mTranslationContext = translationContext;
        this.mId = sessionId;
        this.mManager = translationManager;
        this.mHandler = handler;
        this.mSystemServerBinder = systemServerBinder;
        this.mServiceBinderReceiver = new ServiceBinderReceiver(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        try {
            this.mSystemServerBinder.onSessionCreated(this.mTranslationContext, this.mId, this.mServiceBinderReceiver, this.mContext.getUserId());
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling startSession(): " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSessionCreated() throws ServiceBinderReceiver.TimeoutException {
        int receivedId = this.mServiceBinderReceiver.getSessionStateResult();
        return receivedId > 0;
    }

    private int getNextRequestId() {
        return this.mManager.getAvailableRequestId().getAndIncrement();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setServiceBinder(IBinder binder) {
        synchronized (this.mLock) {
            if (this.mDirectServiceBinder != null) {
                return;
            }
            if (binder != null) {
                this.mDirectServiceBinder = ITranslationDirectManager.Stub.asInterface(binder);
            }
        }
    }

    public TranslationContext getTranslationContext() {
        return this.mTranslationContext;
    }

    public int getTranslatorId() {
        return this.mId;
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("translationContext: ");
        pw.println(this.mTranslationContext);
    }

    @Deprecated
    public void translate(TranslationRequest request, Executor executor, Consumer<TranslationResponse> callback) {
        Objects.requireNonNull(request, "Translation request cannot be null");
        Objects.requireNonNull(executor, "Executor cannot be null");
        Objects.requireNonNull(callback, "Callback cannot be null");
        if (isDestroyed()) {
            throw new IllegalStateException("This translator has been destroyed");
        }
        ITranslationCallback responseCallback = new TranslationResponseCallbackImpl(callback, executor);
        try {
            this.mDirectServiceBinder.onTranslationRequest(request, this.mId, CancellationSignal.createTransport(), responseCallback);
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling requestTranslate(): " + e);
        }
    }

    public void translate(TranslationRequest request, CancellationSignal cancellationSignal, Executor executor, Consumer<TranslationResponse> callback) {
        Objects.requireNonNull(request, "Translation request cannot be null");
        Objects.requireNonNull(executor, "Executor cannot be null");
        Objects.requireNonNull(callback, "Callback cannot be null");
        if (isDestroyed()) {
            throw new IllegalStateException("This translator has been destroyed");
        }
        ICancellationSignal transport = null;
        if (cancellationSignal != null) {
            transport = CancellationSignal.createTransport();
            cancellationSignal.setRemote(transport);
        }
        ITranslationCallback responseCallback = new TranslationResponseCallbackImpl(callback, executor);
        try {
            this.mDirectServiceBinder.onTranslationRequest(request, this.mId, transport, responseCallback);
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling requestTranslate(): " + e);
        }
    }

    public void destroy() {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                return;
            }
            this.mDestroyed = true;
            try {
                this.mDirectServiceBinder.onFinishTranslationSession(this.mId);
            } catch (RemoteException e) {
                Log.m104w(TAG, "RemoteException calling onSessionFinished");
            }
            this.mDirectServiceBinder = null;
            this.mManager.removeTranslator(this.mId);
        }
    }

    public boolean isDestroyed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDestroyed;
        }
        return z;
    }

    public void requestUiTranslate(TranslationRequest request, Executor executor, Consumer<TranslationResponse> callback) {
        if (this.mDirectServiceBinder == null) {
            Log.wtf(TAG, "Translator created without proper initialization.");
            return;
        }
        ITranslationCallback translationCallback = new TranslationResponseCallbackImpl(callback, executor);
        try {
            this.mDirectServiceBinder.onTranslationRequest(request, this.mId, CancellationSignal.createTransport(), translationCallback);
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling flushRequest");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TranslationResponseCallbackImpl extends ITranslationCallback.Stub {
        private final Consumer<TranslationResponse> mCallback;
        private final Executor mExecutor;

        TranslationResponseCallbackImpl(Consumer<TranslationResponse> callback, Executor executor) {
            this.mCallback = callback;
            this.mExecutor = executor;
        }

        @Override // android.service.translation.ITranslationCallback
        public void onTranslationResponse(final TranslationResponse response) throws RemoteException {
            if (UiTranslationController.DEBUG) {
                Log.m108i(Translator.TAG, "onTranslationResponse called.");
            }
            Runnable runnable = new Runnable() { // from class: android.view.translation.Translator$TranslationResponseCallbackImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Translator.TranslationResponseCallbackImpl.this.lambda$onTranslationResponse$0(response);
                }
            };
            long token = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(runnable);
            } finally {
                restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTranslationResponse$0(TranslationResponse response) {
            this.mCallback.accept(response);
        }
    }
}
