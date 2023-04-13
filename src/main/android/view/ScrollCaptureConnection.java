package android.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.p008os.CancellationSignal;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.telephony.BinderCacheManager$BinderDeathTracker$$ExternalSyntheticLambda0;
import android.util.CloseGuard;
import android.util.Log;
import android.view.IScrollCaptureConnection;
import java.lang.ref.Reference;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class ScrollCaptureConnection extends IScrollCaptureConnection.Stub implements IBinder.DeathRecipient {
    private static final String END_CAPTURE = "endCapture";
    private static final String REQUEST_IMAGE = "requestImage";
    private static final String SESSION = "Session";
    private static final String START_CAPTURE = "startCapture";
    private static final String TAG = "ScrollCaptureConnection";
    private static final String TRACE_TRACK = "Scroll Capture";
    private volatile boolean mActive;
    private CancellationSignal mCancellation;
    private volatile boolean mConnected;
    private ScrollCaptureCallback mLocal;
    private final Point mPositionInWindow;
    private IScrollCaptureCallbacks mRemote;
    private final Rect mScrollBounds;
    private ScrollCaptureSession mSession;
    private int mTraceId;
    private final Executor mUiThread;
    private final Object mLock = new Object();
    private final CloseGuard mCloseGuard = new CloseGuard();

    public ScrollCaptureConnection(Executor uiThread, ScrollCaptureTarget selectedTarget) {
        this.mUiThread = (Executor) Objects.requireNonNull(uiThread, "<uiThread> must non-null");
        Objects.requireNonNull(selectedTarget, "<selectedTarget> must non-null");
        this.mScrollBounds = (Rect) Objects.requireNonNull(Rect.copyOrNull(selectedTarget.getScrollBounds()), "target.getScrollBounds() must be non-null to construct a client");
        this.mLocal = selectedTarget.getCallback();
        this.mPositionInWindow = new Point(selectedTarget.getPositionInWindow());
    }

    @Override // android.view.IScrollCaptureConnection
    public ICancellationSignal startCapture(Surface surface, IScrollCaptureCallbacks remote) throws RemoteException {
        int identityHashCode = System.identityHashCode(surface);
        this.mTraceId = identityHashCode;
        Trace.asyncTraceForTrackBegin(2L, TRACE_TRACK, "Session", identityHashCode);
        Trace.asyncTraceForTrackBegin(2L, TRACE_TRACK, START_CAPTURE, this.mTraceId);
        this.mCloseGuard.open("ScrollCaptureConnection.close");
        if (!surface.isValid()) {
            throw new RemoteException(new IllegalArgumentException("surface must be valid"));
        }
        IScrollCaptureCallbacks iScrollCaptureCallbacks = (IScrollCaptureCallbacks) Objects.requireNonNull(remote, "<callbacks> must non-null");
        this.mRemote = iScrollCaptureCallbacks;
        iScrollCaptureCallbacks.asBinder().linkToDeath(this, 0);
        this.mConnected = true;
        ICancellationSignal cancellation = CancellationSignal.createTransport();
        this.mCancellation = CancellationSignal.fromTransport(cancellation);
        this.mSession = new ScrollCaptureSession(surface, this.mScrollBounds, this.mPositionInWindow);
        final Runnable listener = SafeCallback.create(this.mCancellation, this.mUiThread, new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ScrollCaptureConnection.this.onStartCaptureCompleted();
            }
        });
        this.mUiThread.execute(new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ScrollCaptureConnection.this.lambda$startCapture$0(listener);
            }
        });
        return cancellation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startCapture$0(Runnable listener) {
        this.mLocal.onScrollCaptureStart(this.mSession, this.mCancellation, listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartCaptureCompleted() {
        this.mActive = true;
        try {
            this.mRemote.onCaptureStarted();
        } catch (RemoteException e) {
            Log.m103w(TAG, "Shutting down due to error: ", e);
            close();
        }
        this.mCancellation = null;
        Trace.asyncTraceForTrackEnd(2L, TRACE_TRACK, this.mTraceId);
    }

    @Override // android.view.IScrollCaptureConnection
    public ICancellationSignal requestImage(final Rect requestRect) throws RemoteException {
        Trace.asyncTraceForTrackBegin(2L, TRACE_TRACK, REQUEST_IMAGE, this.mTraceId);
        checkActive();
        cancelPendingAction();
        ICancellationSignal cancellation = CancellationSignal.createTransport();
        CancellationSignal fromTransport = CancellationSignal.fromTransport(cancellation);
        this.mCancellation = fromTransport;
        final Consumer<Rect> listener = SafeCallback.create(fromTransport, this.mUiThread, new Consumer() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ScrollCaptureConnection.this.onImageRequestCompleted((Rect) obj);
            }
        });
        this.mUiThread.execute(new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ScrollCaptureConnection.this.lambda$requestImage$1(requestRect, listener);
            }
        });
        return cancellation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestImage$1(Rect requestRect, Consumer listener) {
        ScrollCaptureCallback scrollCaptureCallback = this.mLocal;
        if (scrollCaptureCallback != null) {
            scrollCaptureCallback.onScrollCaptureImageRequest(this.mSession, this.mCancellation, new Rect(requestRect), listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onImageRequestCompleted(Rect capturedArea) {
        try {
            try {
                this.mRemote.onImageRequestCompleted(0, capturedArea);
            } catch (RemoteException e) {
                Log.m103w(TAG, "Shutting down due to error: ", e);
                close();
            }
            Trace.asyncTraceForTrackEnd(2L, TRACE_TRACK, this.mTraceId);
        } finally {
            this.mCancellation = null;
        }
    }

    @Override // android.view.IScrollCaptureConnection
    public ICancellationSignal endCapture() throws RemoteException {
        Trace.asyncTraceForTrackBegin(2L, TRACE_TRACK, END_CAPTURE, this.mTraceId);
        checkActive();
        cancelPendingAction();
        ICancellationSignal cancellation = CancellationSignal.createTransport();
        CancellationSignal fromTransport = CancellationSignal.fromTransport(cancellation);
        this.mCancellation = fromTransport;
        final Runnable listener = SafeCallback.create(fromTransport, this.mUiThread, new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ScrollCaptureConnection.this.onEndCaptureCompleted();
            }
        });
        this.mUiThread.execute(new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ScrollCaptureConnection.this.lambda$endCapture$2(listener);
            }
        });
        return cancellation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$endCapture$2(Runnable listener) {
        ScrollCaptureCallback scrollCaptureCallback = this.mLocal;
        if (scrollCaptureCallback != null) {
            scrollCaptureCallback.onScrollCaptureEnd(listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEndCaptureCompleted() {
        this.mActive = false;
        try {
            try {
                IScrollCaptureCallbacks iScrollCaptureCallbacks = this.mRemote;
                if (iScrollCaptureCallbacks != null) {
                    iScrollCaptureCallbacks.onCaptureEnded();
                }
            } catch (RemoteException e) {
                Log.m103w(TAG, "Caught exception confirming capture end!", e);
            }
            Trace.asyncTraceForTrackEnd(2L, TRACE_TRACK, this.mTraceId);
            Trace.asyncTraceForTrackEnd(2L, TRACE_TRACK, this.mTraceId);
        } finally {
            this.mCancellation = null;
            close();
        }
    }

    @Override // android.p008os.IBinder.DeathRecipient
    public void binderDied() {
        Trace.instantForTrack(2L, TRACE_TRACK, "binderDied");
        Log.m110e(TAG, "Controlling process just died.");
        close();
    }

    @Override // android.view.IScrollCaptureConnection
    public synchronized void close() {
        Trace.instantForTrack(2L, TRACE_TRACK, "close");
        if (this.mActive) {
            Log.m104w(TAG, "close(): capture session still active! Ending now.");
            cancelPendingAction();
            final ScrollCaptureCallback callback = this.mLocal;
            this.mUiThread.execute(new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ScrollCaptureCallback.this.onScrollCaptureEnd(new Runnable() { // from class: android.view.ScrollCaptureConnection$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            ScrollCaptureConnection.lambda$close$3();
                        }
                    });
                }
            });
            this.mActive = false;
        }
        IScrollCaptureCallbacks iScrollCaptureCallbacks = this.mRemote;
        if (iScrollCaptureCallbacks != null) {
            iScrollCaptureCallbacks.asBinder().unlinkToDeath(this, 0);
        }
        this.mActive = false;
        this.mConnected = false;
        this.mSession = null;
        this.mRemote = null;
        this.mLocal = null;
        this.mCloseGuard.close();
        Trace.endSection();
        Reference.reachabilityFence(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$close$3() {
    }

    private void cancelPendingAction() {
        if (this.mCancellation != null) {
            Trace.instantForTrack(2L, TRACE_TRACK, "CancellationSignal.cancel");
            Log.m104w(TAG, "cancelling pending operation.");
            this.mCancellation.cancel();
            this.mCancellation = null;
        }
    }

    public boolean isConnected() {
        return this.mConnected;
    }

    public boolean isActive() {
        return this.mActive;
    }

    private void checkActive() throws RemoteException {
        synchronized (this.mLock) {
            if (!this.mActive) {
                throw new RemoteException(new IllegalStateException("Not started!"));
            }
        }
    }

    public String toString() {
        return "ScrollCaptureConnection{active=" + this.mActive + ", session=" + this.mSession + ", remote=" + this.mRemote + ", local=" + this.mLocal + "}";
    }

    protected void finalize() throws Throwable {
        try {
            this.mCloseGuard.warnIfOpen();
            close();
        } finally {
            super.finalize();
        }
    }

    /* loaded from: classes4.dex */
    private static class SafeCallback<T> {
        private final Executor mExecutor;
        private final CancellationSignal mSignal;
        private final AtomicReference<T> mValue;

        protected SafeCallback(CancellationSignal signal, Executor executor, T value) {
            this.mSignal = signal;
            this.mValue = new AtomicReference<>(value);
            this.mExecutor = executor;
        }

        protected final void maybeAccept(final Consumer<T> consumer) {
            final T value = this.mValue.getAndSet(null);
            if (!this.mSignal.isCanceled() && value != null) {
                this.mExecutor.execute(new Runnable() { // from class: android.view.ScrollCaptureConnection$SafeCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        consumer.accept(value);
                    }
                });
            }
        }

        static Runnable create(CancellationSignal signal, Executor executor, Runnable target) {
            return new RunnableCallback(signal, executor, target);
        }

        static <T> Consumer<T> create(CancellationSignal signal, Executor executor, Consumer<T> target) {
            return new ConsumerCallback(signal, executor, target);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class RunnableCallback extends SafeCallback<Runnable> implements Runnable {
        RunnableCallback(CancellationSignal signal, Executor executor, Runnable target) {
            super(signal, executor, target);
        }

        @Override // java.lang.Runnable
        public void run() {
            maybeAccept(new BinderCacheManager$BinderDeathTracker$$ExternalSyntheticLambda0());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class ConsumerCallback<T> extends SafeCallback<Consumer<T>> implements Consumer<T> {
        ConsumerCallback(CancellationSignal signal, Executor executor, Consumer<T> target) {
            super(signal, executor, target);
        }

        @Override // java.util.function.Consumer
        public void accept(final T value) {
            maybeAccept(new Consumer() { // from class: android.view.ScrollCaptureConnection$ConsumerCallback$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((Consumer) obj).accept(value);
                }
            });
        }
    }
}
