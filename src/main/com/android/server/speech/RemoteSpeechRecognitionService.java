package com.android.server.speech;

import android.content.AttributionSource;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.IModelDownloadListener;
import android.speech.IRecognitionListener;
import android.speech.IRecognitionService;
import android.speech.IRecognitionSupportCallback;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.ServiceConnector;
import com.android.server.speech.RemoteSpeechRecognitionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class RemoteSpeechRecognitionService extends ServiceConnector.Impl<IRecognitionService> {
    public static final String TAG = RemoteSpeechRecognitionService.class.getSimpleName();
    private final int mCallingUid;
    @GuardedBy({"mLock"})
    private final List<Pair<IBinder, IRecognitionListener>> mClientListeners;
    @GuardedBy({"mLock"})
    private final Map<IBinder, ClientState> mClients;
    private final ComponentName mComponentName;
    private boolean mConnected;
    private final Object mLock;

    public long getAutoDisconnectTimeoutMs() {
        return 0L;
    }

    public RemoteSpeechRecognitionService(Context context, ComponentName componentName, int i, int i2) {
        super(context, new Intent("android.speech.RecognitionService").setComponent(componentName), 67112961, i, new Function() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return IRecognitionService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mLock = new Object();
        this.mConnected = false;
        this.mClients = new HashMap();
        this.mClientListeners = new ArrayList();
        this.mCallingUid = i2;
        this.mComponentName = componentName;
    }

    public ComponentName getServiceComponentName() {
        return this.mComponentName;
    }

    public void startListening(final Intent intent, final IRecognitionListener iRecognitionListener, final AttributionSource attributionSource) {
        if (iRecognitionListener == null) {
            Slog.w(TAG, "#startListening called with no preceding #setListening - ignoring.");
        } else if (!this.mConnected) {
            tryRespondWithError(iRecognitionListener, 11);
        } else {
            synchronized (this.mLock) {
                final ClientState clientState = this.mClients.get(iRecognitionListener.asBinder());
                if (clientState == null) {
                    if (this.mClients.size() >= 100) {
                        tryRespondWithError(iRecognitionListener, 8);
                        Log.i(TAG, "#startListening received when the recognizer's capacity is full - ignoring this call.");
                        return;
                    }
                    clientState = new ClientState();
                    clientState.mDelegatingListener = new DelegatingListener(iRecognitionListener, new Runnable() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            RemoteSpeechRecognitionService.this.lambda$startListening$0(clientState);
                        }
                    }, new Runnable() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            RemoteSpeechRecognitionService.this.lambda$startListening$1(iRecognitionListener);
                        }
                    });
                    this.mClients.put(iRecognitionListener.asBinder(), clientState);
                } else if (clientState.mRecordingInProgress) {
                    Slog.i(TAG, "#startListening called while listening is in progress for this caller.");
                    tryRespondWithError(iRecognitionListener, 5);
                    return;
                } else {
                    clientState.mRecordingInProgress = true;
                }
                final DelegatingListener delegatingListener = clientState.mDelegatingListener;
                run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda5
                    public final void runNoResult(Object obj) {
                        ((IRecognitionService) obj).startListening(intent, delegatingListener, attributionSource);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startListening$0(ClientState clientState) {
        synchronized (this.mLock) {
            clientState.mRecordingInProgress = false;
        }
    }

    public void stopListening(IRecognitionListener iRecognitionListener) {
        if (!this.mConnected) {
            tryRespondWithError(iRecognitionListener, 11);
            return;
        }
        synchronized (this.mLock) {
            ClientState clientState = this.mClients.get(iRecognitionListener.asBinder());
            if (clientState == null) {
                Slog.w(TAG, "#stopListening called with no preceding #startListening - ignoring.");
                tryRespondWithError(iRecognitionListener, 5);
            } else if (!clientState.mRecordingInProgress) {
                tryRespondWithError(iRecognitionListener, 5);
                Slog.i(TAG, "#stopListening called while listening isn't in progress - ignoring.");
            } else {
                clientState.mRecordingInProgress = false;
                final DelegatingListener delegatingListener = clientState.mDelegatingListener;
                run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda1
                    public final void runNoResult(Object obj) {
                        ((IRecognitionService) obj).stopListening(RemoteSpeechRecognitionService.DelegatingListener.this);
                    }
                });
            }
        }
    }

    public void cancel(IRecognitionListener iRecognitionListener, final boolean z) {
        if (!this.mConnected) {
            tryRespondWithError(iRecognitionListener, 11);
        }
        synchronized (this.mLock) {
            ClientState clientState = this.mClients.get(iRecognitionListener.asBinder());
            if (clientState == null) {
                return;
            }
            clientState.mRecordingInProgress = false;
            final DelegatingListener delegatingListener = clientState.mDelegatingListener;
            run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda8
                public final void runNoResult(Object obj) {
                    ((IRecognitionService) obj).cancel(delegatingListener, z);
                }
            });
            if (z) {
                lambda$startListening$1(iRecognitionListener);
                if (this.mClients.isEmpty()) {
                    run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda9
                        public final void runNoResult(Object obj) {
                            RemoteSpeechRecognitionService.this.lambda$cancel$5((IRecognitionService) obj);
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancel$5(IRecognitionService iRecognitionService) throws Exception {
        unbind();
    }

    public void checkRecognitionSupport(final Intent intent, final AttributionSource attributionSource, final IRecognitionSupportCallback iRecognitionSupportCallback) {
        if (!this.mConnected) {
            try {
                iRecognitionSupportCallback.onError(11);
                return;
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to report the connection broke to the caller.", e);
                e.printStackTrace();
                return;
            }
        }
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda7
            public final void runNoResult(Object obj) {
                ((IRecognitionService) obj).checkRecognitionSupport(intent, attributionSource, iRecognitionSupportCallback);
            }
        });
    }

    public void triggerModelDownload(final Intent intent, final AttributionSource attributionSource) {
        if (!this.mConnected) {
            Slog.e(TAG, "#downloadModel failed due to connection.");
        } else {
            run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda2
                public final void runNoResult(Object obj) {
                    ((IRecognitionService) obj).triggerModelDownload(intent, attributionSource);
                }
            });
        }
    }

    public void setModelDownloadListener(final Intent intent, final AttributionSource attributionSource, final IModelDownloadListener iModelDownloadListener) {
        if (!this.mConnected) {
            try {
                iModelDownloadListener.onError(11);
                return;
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to report the connection broke to the caller.", e);
                e.printStackTrace();
                return;
            }
        }
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                ((IRecognitionService) obj).setModelDownloadListener(intent, attributionSource, iModelDownloadListener);
            }
        });
    }

    public void clearModelDownloadListener(final Intent intent, final AttributionSource attributionSource) {
        if (this.mConnected) {
            run(new ServiceConnector.VoidJob() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda6
                public final void runNoResult(Object obj) {
                    ((IRecognitionService) obj).clearModelDownloadListener(intent, attributionSource);
                }
            });
        }
    }

    public void shutdown(IBinder iBinder) {
        synchronized (this.mLock) {
            for (Pair<IBinder, IRecognitionListener> pair : this.mClientListeners) {
                if (pair.first == iBinder) {
                    cancel((IRecognitionListener) pair.second, true);
                }
            }
        }
    }

    public void onServiceConnectionStatusChanged(IRecognitionService iRecognitionService, boolean z) {
        this.mConnected = z;
        synchronized (this.mLock) {
            if (!z) {
                if (this.mClients.isEmpty()) {
                    Slog.i(TAG, "Connection to speech recognition service lost, but no #startListening has been invoked yet.");
                    return;
                }
                for (ClientState clientState : this.mClients.values()) {
                    tryRespondWithError(clientState.mDelegatingListener.mRemoteListener, 11);
                    lambda$startListening$1(clientState.mDelegatingListener.mRemoteListener);
                }
            }
        }
    }

    /* renamed from: removeClient */
    public final void lambda$startListening$1(final IRecognitionListener iRecognitionListener) {
        synchronized (this.mLock) {
            ClientState remove = this.mClients.remove(iRecognitionListener.asBinder());
            if (remove != null) {
                remove.reset();
            }
            this.mClientListeners.removeIf(new Predicate() { // from class: com.android.server.speech.RemoteSpeechRecognitionService$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$removeClient$10;
                    lambda$removeClient$10 = RemoteSpeechRecognitionService.lambda$removeClient$10(iRecognitionListener, (Pair) obj);
                    return lambda$removeClient$10;
                }
            });
        }
    }

    public static /* synthetic */ boolean lambda$removeClient$10(IRecognitionListener iRecognitionListener, Pair pair) {
        return pair.second == iRecognitionListener;
    }

    public static void tryRespondWithError(IRecognitionListener iRecognitionListener, int i) {
        if (iRecognitionListener != null) {
            try {
                iRecognitionListener.onError(i);
            } catch (RemoteException e) {
                Slog.w(TAG, TextUtils.formatSimple("Failed to respond with an error %d to the client", new Object[]{Integer.valueOf(i)}), e);
            }
        }
    }

    public boolean hasActiveSessions() {
        boolean z;
        synchronized (this.mLock) {
            z = !this.mClients.isEmpty();
        }
        return z;
    }

    public void associateClientWithActiveListener(IBinder iBinder, IRecognitionListener iRecognitionListener) {
        synchronized (this.mLock) {
            if (this.mClients.containsKey(iRecognitionListener.asBinder())) {
                this.mClientListeners.add(new Pair<>(iBinder, iRecognitionListener));
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class DelegatingListener extends IRecognitionListener.Stub {
        public final Runnable mOnSessionFailure;
        public final Runnable mOnSessionSuccess;
        public final IRecognitionListener mRemoteListener;

        public DelegatingListener(IRecognitionListener iRecognitionListener, Runnable runnable, Runnable runnable2) {
            this.mRemoteListener = iRecognitionListener;
            this.mOnSessionSuccess = runnable;
            this.mOnSessionFailure = runnable2;
        }

        public void onReadyForSpeech(Bundle bundle) throws RemoteException {
            this.mRemoteListener.onReadyForSpeech(bundle);
        }

        public void onBeginningOfSpeech() throws RemoteException {
            this.mRemoteListener.onBeginningOfSpeech();
        }

        public void onRmsChanged(float f) throws RemoteException {
            this.mRemoteListener.onRmsChanged(f);
        }

        public void onBufferReceived(byte[] bArr) throws RemoteException {
            this.mRemoteListener.onBufferReceived(bArr);
        }

        public void onEndOfSpeech() throws RemoteException {
            this.mRemoteListener.onEndOfSpeech();
        }

        public void onError(int i) throws RemoteException {
            this.mOnSessionFailure.run();
            this.mRemoteListener.onError(i);
        }

        public void onResults(Bundle bundle) throws RemoteException {
            this.mOnSessionSuccess.run();
            this.mRemoteListener.onResults(bundle);
        }

        public void onPartialResults(Bundle bundle) throws RemoteException {
            this.mRemoteListener.onPartialResults(bundle);
        }

        public void onSegmentResults(Bundle bundle) throws RemoteException {
            this.mRemoteListener.onSegmentResults(bundle);
        }

        public void onEndOfSegmentedSession() throws RemoteException {
            this.mOnSessionSuccess.run();
            this.mRemoteListener.onEndOfSegmentedSession();
        }

        public void onLanguageDetection(Bundle bundle) throws RemoteException {
            this.mRemoteListener.onLanguageDetection(bundle);
        }

        public void onEvent(int i, Bundle bundle) throws RemoteException {
            this.mRemoteListener.onEvent(i, bundle);
        }
    }

    /* loaded from: classes2.dex */
    public static class ClientState {
        public DelegatingListener mDelegatingListener;
        public boolean mRecordingInProgress;

        public ClientState(DelegatingListener delegatingListener, boolean z) {
            this.mDelegatingListener = delegatingListener;
            this.mRecordingInProgress = z;
        }

        public ClientState() {
            this(null, true);
        }

        public void reset() {
            this.mDelegatingListener = null;
            this.mRecordingInProgress = false;
        }
    }
}
