package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraOfflineSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.impl.CallbackProxies;
import android.hardware.camera2.impl.CameraCaptureSessionImpl;
import android.hardware.camera2.impl.CameraDeviceImpl;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.utils.TaskDrainer;
import android.hardware.camera2.utils.TaskSingleDrainer;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.SystemClock;
import android.util.Log;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class CameraCaptureSessionImpl extends CameraCaptureSession implements CameraCaptureSessionCore {
    private static final boolean DEBUG = false;
    private static final String TAG = "CameraCaptureSession";
    private final TaskSingleDrainer mAbortDrainer;
    private volatile boolean mAborting;
    private boolean mClosed;
    private final boolean mConfigureSuccess;
    private final Executor mDeviceExecutor;
    private final CameraDeviceImpl mDeviceImpl;
    private final int mId;
    private final String mIdString;
    private final TaskSingleDrainer mIdleDrainer;
    private final Surface mInput;
    private final TaskDrainer<Integer> mSequenceDrainer;
    private boolean mSkipUnconfigure = false;
    private final CameraCaptureSession.StateCallback mStateCallback;
    private final Executor mStateExecutor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CameraCaptureSessionImpl(int id, Surface input, CameraCaptureSession.StateCallback callback, Executor stateExecutor, CameraDeviceImpl deviceImpl, Executor deviceStateExecutor, boolean configureSuccess) {
        this.mClosed = false;
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        this.mId = id;
        String format = String.format("Session %d: ", Integer.valueOf(id));
        this.mIdString = format;
        this.mInput = input;
        Executor executor = (Executor) Preconditions.checkNotNull(stateExecutor, "stateExecutor must not be null");
        this.mStateExecutor = executor;
        CameraCaptureSession.StateCallback createUserStateCallbackProxy = createUserStateCallbackProxy(executor, callback);
        this.mStateCallback = createUserStateCallbackProxy;
        Executor executor2 = (Executor) Preconditions.checkNotNull(deviceStateExecutor, "deviceStateExecutor must not be null");
        this.mDeviceExecutor = executor2;
        this.mDeviceImpl = (CameraDeviceImpl) Preconditions.checkNotNull(deviceImpl, "deviceImpl must not be null");
        this.mSequenceDrainer = new TaskDrainer<>(executor2, new SequenceDrainListener(), "seq");
        this.mIdleDrainer = new TaskSingleDrainer(executor2, new IdleDrainListener(), "idle");
        this.mAbortDrainer = new TaskSingleDrainer(executor2, new AbortDrainListener(), "abort");
        if (configureSuccess) {
            createUserStateCallbackProxy.onConfigured(this);
            this.mConfigureSuccess = true;
            return;
        }
        createUserStateCallbackProxy.onConfigureFailed(this);
        this.mClosed = true;
        Log.m110e(TAG, format + "Failed to create capture session; configuration failed");
        this.mConfigureSuccess = false;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraDevice getDevice() {
        return this.mDeviceImpl;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(Surface surface) throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            this.mDeviceImpl.prepare(surface);
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void prepare(int maxCount, Surface surface) throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            this.mDeviceImpl.prepare(maxCount, surface);
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void tearDown(Surface surface) throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            this.mDeviceImpl.tearDown(surface);
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void finalizeOutputConfigurations(List<OutputConfiguration> outputConfigs) throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            this.mDeviceImpl.finalizeOutputConfigs(outputConfigs);
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int capture(CaptureRequest request, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        int addPendingSequence;
        checkCaptureRequest(request);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.capture(request, createCaptureCallbackProxy(CameraDeviceImpl.checkHandler(handler, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureSingleRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        int addPendingSequence;
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkCaptureRequest(request);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.capture(request, createCaptureCallbackProxyWithExecutor(CameraDeviceImpl.checkExecutor(executor, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    private void checkCaptureRequest(CaptureRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.isReprocess() && !isReprocessable()) {
            throw new IllegalArgumentException("this capture session cannot handle reprocess requests");
        }
        if (request.isReprocess() && request.getReprocessableSessionId() != this.mId) {
            throw new IllegalArgumentException("capture request was created for another session");
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurst(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        int addPendingSequence;
        checkCaptureRequests(requests);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.captureBurst(requests, createCaptureCallbackProxy(CameraDeviceImpl.checkHandler(handler, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int captureBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        int addPendingSequence;
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkCaptureRequests(requests);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.captureBurst(requests, createCaptureCallbackProxyWithExecutor(CameraDeviceImpl.checkExecutor(executor, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    private void checkCaptureRequests(List<CaptureRequest> requests) {
        if (requests == null) {
            throw new IllegalArgumentException("Requests must not be null");
        }
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Requests must have at least one element");
        }
        for (CaptureRequest request : requests) {
            if (request.isReprocess()) {
                if (!isReprocessable()) {
                    throw new IllegalArgumentException("This capture session cannot handle reprocess requests");
                }
                if (request.getReprocessableSessionId() != this.mId) {
                    throw new IllegalArgumentException("Capture request was created for another session");
                }
            }
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingRequest(CaptureRequest request, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        int addPendingSequence;
        checkRepeatingRequest(request);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingRequest(request, createCaptureCallbackProxy(CameraDeviceImpl.checkHandler(handler, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setSingleRepeatingRequest(CaptureRequest request, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        int addPendingSequence;
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkRepeatingRequest(request);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingRequest(request, createCaptureCallbackProxyWithExecutor(CameraDeviceImpl.checkExecutor(executor, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    private void checkRepeatingRequest(CaptureRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.isReprocess()) {
            throw new IllegalArgumentException("repeating reprocess requests are not supported");
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurst(List<CaptureRequest> requests, CameraCaptureSession.CaptureCallback callback, Handler handler) throws CameraAccessException {
        int addPendingSequence;
        checkRepeatingRequests(requests);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingBurst(requests, createCaptureCallbackProxy(CameraDeviceImpl.checkHandler(handler, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public int setRepeatingBurstRequests(List<CaptureRequest> requests, Executor executor, CameraCaptureSession.CaptureCallback callback) throws CameraAccessException {
        int addPendingSequence;
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkRepeatingRequests(requests);
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            addPendingSequence = addPendingSequence(this.mDeviceImpl.setRepeatingBurst(requests, createCaptureCallbackProxyWithExecutor(CameraDeviceImpl.checkExecutor(executor, callback), callback), this.mDeviceExecutor));
        }
        return addPendingSequence;
    }

    private void checkRepeatingRequests(List<CaptureRequest> requests) {
        if (requests == null) {
            throw new IllegalArgumentException("requests must not be null");
        }
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("requests must have at least one element");
        }
        for (CaptureRequest r : requests) {
            if (r.isReprocess()) {
                throw new IllegalArgumentException("repeating reprocess burst requests are not supported");
            }
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void stopRepeating() throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            this.mDeviceImpl.stopRepeating();
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void abortCaptures() throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            if (this.mAborting) {
                Log.m104w(TAG, this.mIdString + "abortCaptures - Session is already aborting; doing nothing");
                return;
            }
            this.mAborting = true;
            this.mAbortDrainer.taskStarted();
            this.mDeviceImpl.flush();
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public void updateOutputConfiguration(OutputConfiguration config) throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
            this.mDeviceImpl.updateOutputConfiguration(config);
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public CameraOfflineSession switchToOffline(Collection<Surface> offlineOutputs, Executor executor, CameraOfflineSession.CameraOfflineSessionCallback listener) throws CameraAccessException {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
        }
        return this.mDeviceImpl.switchToOffline(offlineOutputs, executor, listener);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean supportsOfflineProcessing(Surface surface) {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            checkNotClosed();
        }
        return this.mDeviceImpl.supportsOfflineProcessing(surface);
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public boolean isReprocessable() {
        return this.mInput != null;
    }

    @Override // android.hardware.camera2.CameraCaptureSession
    public Surface getInputSurface() {
        return this.mInput;
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public void replaceSessionClose() {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            this.mSkipUnconfigure = true;
            close();
        }
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public void closeWithoutDraining() {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            if (this.mClosed) {
                return;
            }
            this.mClosed = true;
            this.mStateCallback.onClosed(this);
            Surface surface = this.mInput;
            if (surface != null) {
                surface.release();
            }
        }
    }

    @Override // android.hardware.camera2.CameraCaptureSession, java.lang.AutoCloseable
    public void close() {
        synchronized (this.mDeviceImpl.mInterfaceLock) {
            if (this.mClosed) {
                return;
            }
            this.mClosed = true;
            try {
                try {
                    this.mDeviceImpl.stopRepeating();
                } catch (CameraAccessException e) {
                    Log.m109e(TAG, this.mIdString + "Exception while stopping repeating: ", e);
                }
                this.mSequenceDrainer.beginDrain();
                Surface surface = this.mInput;
                if (surface != null) {
                    surface.release();
                }
            } catch (IllegalStateException e2) {
                this.mStateCallback.onClosed(this);
            }
        }
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public boolean isAborting() {
        return this.mAborting;
    }

    private CameraCaptureSession.StateCallback createUserStateCallbackProxy(Executor executor, CameraCaptureSession.StateCallback callback) {
        return new CallbackProxies.SessionStateCallbackProxy(executor, callback);
    }

    private CaptureCallback createCaptureCallbackProxy(Handler handler, CameraCaptureSession.CaptureCallback callback) {
        Executor executor = callback != null ? CameraDeviceImpl.checkAndWrapHandler(handler) : null;
        return createCaptureCallbackProxyWithExecutor(executor, callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.hardware.camera2.impl.CameraCaptureSessionImpl$1 */
    /* loaded from: classes.dex */
    public class C09341 extends CaptureCallback {
        final /* synthetic */ CameraCaptureSession.CaptureCallback val$callback;
        final /* synthetic */ Executor val$executor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C09341(Executor executor, CameraCaptureSession.CaptureCallback callback, CameraCaptureSession.CaptureCallback captureCallback, Executor executor2) {
            super(executor, callback);
            this.val$callback = captureCallback;
            this.val$executor = executor2;
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureStarted(CameraDevice camera, final CaptureRequest request, final long timestamp, final long frameNumber) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureStarted$0(captureCallback, request, timestamp, frameNumber);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureStarted$0(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, long timestamp, long frameNumber) {
            callback.onCaptureStarted(CameraCaptureSessionImpl.this, request, timestamp, frameNumber);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onReadoutStarted(CameraDevice camera, final CaptureRequest request, final long timestamp, final long frameNumber) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onReadoutStarted$1(captureCallback, request, timestamp, frameNumber);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReadoutStarted$1(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, long timestamp, long frameNumber) {
            callback.onReadoutStarted(CameraCaptureSessionImpl.this, request, timestamp, frameNumber);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCapturePartial(CameraDevice camera, final CaptureRequest request, final CaptureResult result) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCapturePartial$2(captureCallback, request, result);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapturePartial$2(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, CaptureResult result) {
            callback.onCapturePartial(CameraCaptureSessionImpl.this, request, result);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureProgressed(CameraDevice camera, final CaptureRequest request, final CaptureResult partialResult) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureProgressed$3(captureCallback, request, partialResult);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureProgressed$3(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, CaptureResult partialResult) {
            callback.onCaptureProgressed(CameraCaptureSessionImpl.this, request, partialResult);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureCompleted(CameraDevice camera, final CaptureRequest request, final TotalCaptureResult result) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureCompleted$4(captureCallback, request, result);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureCompleted$4(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, TotalCaptureResult result) {
            callback.onCaptureCompleted(CameraCaptureSessionImpl.this, request, result);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureFailed(CameraDevice camera, final CaptureRequest request, final CaptureFailure failure) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureFailed$5(captureCallback, request, failure);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureFailed$5(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, CaptureFailure failure) {
            callback.onCaptureFailed(CameraCaptureSessionImpl.this, request, failure);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureSequenceCompleted(CameraDevice camera, final int sequenceId, final long frameNumber) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureSequenceCompleted$6(captureCallback, sequenceId, frameNumber);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            CameraCaptureSessionImpl.this.finishPendingSequence(sequenceId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureSequenceCompleted$6(CameraCaptureSession.CaptureCallback callback, int sequenceId, long frameNumber) {
            callback.onCaptureSequenceCompleted(CameraCaptureSessionImpl.this, sequenceId, frameNumber);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureSequenceAborted(CameraDevice camera, final int sequenceId) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureSequenceAborted$7(captureCallback, sequenceId);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            CameraCaptureSessionImpl.this.finishPendingSequence(sequenceId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureSequenceAborted$7(CameraCaptureSession.CaptureCallback callback, int sequenceId) {
            callback.onCaptureSequenceAborted(CameraCaptureSessionImpl.this, sequenceId);
        }

        @Override // android.hardware.camera2.impl.CaptureCallback
        public void onCaptureBufferLost(CameraDevice camera, final CaptureRequest request, final Surface target, final long frameNumber) {
            if (this.val$callback != null && this.val$executor != null) {
                long ident = Binder.clearCallingIdentity();
                try {
                    Executor executor = this.val$executor;
                    final CameraCaptureSession.CaptureCallback captureCallback = this.val$callback;
                    executor.execute(new Runnable() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl$1$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            CameraCaptureSessionImpl.C09341.this.lambda$onCaptureBufferLost$8(captureCallback, request, target, frameNumber);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCaptureBufferLost$8(CameraCaptureSession.CaptureCallback callback, CaptureRequest request, Surface target, long frameNumber) {
            callback.onCaptureBufferLost(CameraCaptureSessionImpl.this, request, target, frameNumber);
        }
    }

    private CaptureCallback createCaptureCallbackProxyWithExecutor(Executor executor, CameraCaptureSession.CaptureCallback callback) {
        return new C09341(executor, callback, callback, executor);
    }

    @Override // android.hardware.camera2.impl.CameraCaptureSessionCore
    public CameraDeviceImpl.StateCallbackKK getDeviceStateCallback() {
        final Object interfaceLock = this.mDeviceImpl.mInterfaceLock;
        return new CameraDeviceImpl.StateCallbackKK() { // from class: android.hardware.camera2.impl.CameraCaptureSessionImpl.2
            private boolean mBusy = false;
            private boolean mActive = false;

            @Override // android.hardware.camera2.CameraDevice.StateCallback
            public void onOpened(CameraDevice camera) {
                throw new AssertionError("Camera must already be open before creating a session");
            }

            @Override // android.hardware.camera2.CameraDevice.StateCallback
            public void onDisconnected(CameraDevice camera) {
                CameraCaptureSessionImpl.this.close();
            }

            @Override // android.hardware.camera2.CameraDevice.StateCallback
            public void onError(CameraDevice camera, int error) {
                Log.wtf(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "Got device error " + error);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onActive(CameraDevice camera) {
                CameraCaptureSessionImpl.this.mIdleDrainer.taskStarted();
                this.mActive = true;
                CameraCaptureSessionImpl.this.mStateCallback.onActive(this);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onIdle(CameraDevice camera) {
                boolean isAborting;
                synchronized (interfaceLock) {
                    isAborting = CameraCaptureSessionImpl.this.mAborting;
                }
                if (this.mBusy && isAborting) {
                    CameraCaptureSessionImpl.this.mAbortDrainer.taskFinished();
                    synchronized (interfaceLock) {
                        CameraCaptureSessionImpl.this.mAborting = false;
                    }
                }
                if (this.mActive) {
                    CameraCaptureSessionImpl.this.mIdleDrainer.taskFinished();
                }
                this.mBusy = false;
                this.mActive = false;
                CameraCaptureSessionImpl.this.mStateCallback.onReady(this);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onBusy(CameraDevice camera) {
                this.mBusy = true;
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onUnconfigured(CameraDevice camera) {
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onRequestQueueEmpty() {
                CameraCaptureSessionImpl.this.mStateCallback.onCaptureQueueEmpty(this);
            }

            @Override // android.hardware.camera2.impl.CameraDeviceImpl.StateCallbackKK
            public void onSurfacePrepared(Surface surface) {
                CameraCaptureSessionImpl.this.mStateCallback.onSurfacePrepared(this, surface);
            }
        };
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void checkNotClosed() {
        if (this.mClosed) {
            throw new IllegalStateException("Session has been closed; further changes are illegal.");
        }
    }

    private int addPendingSequence(int sequenceId) {
        this.mSequenceDrainer.taskStarted(Integer.valueOf(sequenceId));
        return sequenceId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishPendingSequence(int sequenceId) {
        try {
            this.mSequenceDrainer.taskFinished(Integer.valueOf(sequenceId));
        } catch (IllegalStateException e) {
            Log.m104w(TAG, e.getMessage());
        }
    }

    /* loaded from: classes.dex */
    private class SequenceDrainListener implements TaskDrainer.DrainListener {
        private SequenceDrainListener() {
        }

        @Override // android.hardware.camera2.utils.TaskDrainer.DrainListener
        public void onDrained() {
            CameraCaptureSessionImpl.this.mStateCallback.onClosed(CameraCaptureSessionImpl.this);
            if (CameraCaptureSessionImpl.this.mSkipUnconfigure) {
                return;
            }
            CameraCaptureSessionImpl.this.mAbortDrainer.beginDrain();
        }
    }

    /* loaded from: classes.dex */
    private class AbortDrainListener implements TaskDrainer.DrainListener {
        private AbortDrainListener() {
        }

        @Override // android.hardware.camera2.utils.TaskDrainer.DrainListener
        public void onDrained() {
            synchronized (CameraCaptureSessionImpl.this.mDeviceImpl.mInterfaceLock) {
                if (CameraCaptureSessionImpl.this.mSkipUnconfigure) {
                    return;
                }
                CameraCaptureSessionImpl.this.mIdleDrainer.beginDrain();
            }
        }
    }

    /* loaded from: classes.dex */
    private class IdleDrainListener implements TaskDrainer.DrainListener {
        private IdleDrainListener() {
        }

        @Override // android.hardware.camera2.utils.TaskDrainer.DrainListener
        public void onDrained() {
            synchronized (CameraCaptureSessionImpl.this.mDeviceImpl.mInterfaceLock) {
                if (CameraCaptureSessionImpl.this.mSkipUnconfigure) {
                    return;
                }
                try {
                    CameraCaptureSessionImpl.this.mDeviceImpl.configureStreamsChecked(null, null, 0, null, SystemClock.uptimeMillis());
                } catch (CameraAccessException e) {
                    Log.m109e(CameraCaptureSessionImpl.TAG, CameraCaptureSessionImpl.this.mIdString + "Exception while unconfiguring outputs: ", e);
                } catch (IllegalStateException e2) {
                }
            }
        }
    }
}
