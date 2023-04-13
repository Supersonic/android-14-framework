package com.android.server.texttospeech;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.ITextToSpeechService;
import android.speech.tts.ITextToSpeechSession;
import android.speech.tts.ITextToSpeechSessionCallback;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.ServiceConnector;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.texttospeech.TextToSpeechManagerPerUserService;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes2.dex */
public final class TextToSpeechManagerPerUserService extends AbstractPerUserSystemService<TextToSpeechManagerPerUserService, TextToSpeechManagerService> {
    public static final String TAG = "TextToSpeechManagerPerUserService";

    /* loaded from: classes2.dex */
    public interface ThrowingRunnable {
        void runOrThrow() throws RemoteException;
    }

    public TextToSpeechManagerPerUserService(TextToSpeechManagerService textToSpeechManagerService, Object obj, int i) {
        super(textToSpeechManagerService, obj, i);
    }

    public void createSessionLocked(String str, ITextToSpeechSessionCallback iTextToSpeechSessionCallback) {
        TextToSpeechSessionConnection.start(getContext(), this.mUserId, str, iTextToSpeechSessionCallback);
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            return AppGlobals.getPackageManager().getServiceInfo(componentName, 128L, this.mUserId);
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    /* loaded from: classes2.dex */
    public static class TextToSpeechSessionConnection extends ServiceConnector.Impl<ITextToSpeechService> {
        private ITextToSpeechSessionCallback mCallback;
        private final String mEngine;
        private final IBinder.DeathRecipient mUnbindOnDeathHandler;

        public long getAutoDisconnectTimeoutMs() {
            return 0L;
        }

        public static void start(Context context, int i, String str, ITextToSpeechSessionCallback iTextToSpeechSessionCallback) {
            new TextToSpeechSessionConnection(context, i, str, iTextToSpeechSessionCallback).start();
        }

        public TextToSpeechSessionConnection(Context context, int i, String str, ITextToSpeechSessionCallback iTextToSpeechSessionCallback) {
            super(context, new Intent("android.intent.action.TTS_SERVICE").setPackage(str), 1, i, new Function() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ITextToSpeechService.Stub.asInterface((IBinder) obj);
                }
            });
            this.mEngine = str;
            this.mCallback = iTextToSpeechSessionCallback;
            this.mUnbindOnDeathHandler = new IBinder.DeathRecipient() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda1
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    TextToSpeechManagerPerUserService.TextToSpeechSessionConnection.this.lambda$new$0();
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            unbindEngine("client process death is reported");
        }

        public final void start() {
            String str = TextToSpeechManagerPerUserService.TAG;
            Slog.d(str, "Trying to start connection to TTS engine: " + this.mEngine);
            connect().thenAccept(new Consumer() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TextToSpeechManagerPerUserService.TextToSpeechSessionConnection.this.lambda$start$2((ITextToSpeechService) obj);
                }
            }).exceptionally(new Function() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Void lambda$start$4;
                    lambda$start$4 = TextToSpeechManagerPerUserService.TextToSpeechSessionConnection.this.lambda$start$4((Throwable) obj);
                    return lambda$start$4;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$start$2(ITextToSpeechService iTextToSpeechService) {
            if (iTextToSpeechService != null) {
                String str = TextToSpeechManagerPerUserService.TAG;
                Slog.d(str, "Connected successfully to TTS engine: " + this.mEngine);
                try {
                    this.mCallback.onConnected(new ITextToSpeechSession.Stub() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService.TextToSpeechSessionConnection.1
                        public void disconnect() {
                            TextToSpeechSessionConnection.this.unbindEngine("client disconnection request");
                        }
                    }, iTextToSpeechService.asBinder());
                    this.mCallback.asBinder().linkToDeath(this.mUnbindOnDeathHandler, 0);
                    return;
                } catch (RemoteException e) {
                    Slog.w(TextToSpeechManagerPerUserService.TAG, "Error notifying the client on connection", e);
                    unbindEngine("failed communicating with the client - process is dead");
                    return;
                }
            }
            Slog.w(TextToSpeechManagerPerUserService.TAG, "Failed to obtain TTS engine binder");
            TextToSpeechManagerPerUserService.runSessionCallbackMethod(new ThrowingRunnable() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda5
                @Override // com.android.server.texttospeech.TextToSpeechManagerPerUserService.ThrowingRunnable
                public final void runOrThrow() {
                    TextToSpeechManagerPerUserService.TextToSpeechSessionConnection.this.lambda$start$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$start$1() throws RemoteException {
            this.mCallback.onError("Failed creating TTS session");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Void lambda$start$4(final Throwable th) {
            Slog.w(TextToSpeechManagerPerUserService.TAG, "TTS engine binding error", th);
            TextToSpeechManagerPerUserService.runSessionCallbackMethod(new ThrowingRunnable() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda6
                @Override // com.android.server.texttospeech.TextToSpeechManagerPerUserService.ThrowingRunnable
                public final void runOrThrow() {
                    TextToSpeechManagerPerUserService.TextToSpeechSessionConnection.this.lambda$start$3(th);
                }
            });
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$start$3(Throwable th) throws RemoteException {
            ITextToSpeechSessionCallback iTextToSpeechSessionCallback = this.mCallback;
            iTextToSpeechSessionCallback.onError("Failed creating TTS session: " + th.getCause());
        }

        public void onServiceConnectionStatusChanged(ITextToSpeechService iTextToSpeechService, boolean z) {
            if (z) {
                return;
            }
            Slog.w(TextToSpeechManagerPerUserService.TAG, "Disconnected from TTS engine");
            final ITextToSpeechSessionCallback iTextToSpeechSessionCallback = this.mCallback;
            Objects.requireNonNull(iTextToSpeechSessionCallback);
            TextToSpeechManagerPerUserService.runSessionCallbackMethod(new ThrowingRunnable() { // from class: com.android.server.texttospeech.TextToSpeechManagerPerUserService$TextToSpeechSessionConnection$$ExternalSyntheticLambda2
                @Override // com.android.server.texttospeech.TextToSpeechManagerPerUserService.ThrowingRunnable
                public final void runOrThrow() {
                    iTextToSpeechSessionCallback.onDisconnected();
                }
            });
            try {
                this.mCallback.asBinder().unlinkToDeath(this.mUnbindOnDeathHandler, 0);
            } catch (NoSuchElementException unused) {
                Slog.d(TextToSpeechManagerPerUserService.TAG, "The death recipient was not linked.");
            }
            this.mCallback = null;
        }

        public final void unbindEngine(String str) {
            String str2 = TextToSpeechManagerPerUserService.TAG;
            Slog.d(str2, "Unbinding TTS engine: " + this.mEngine + ". Reason: " + str);
            unbind();
        }
    }

    public static void runSessionCallbackMethod(ThrowingRunnable throwingRunnable) {
        try {
            throwingRunnable.runOrThrow();
        } catch (RemoteException e) {
            String str = TAG;
            Slog.i(str, "Failed running callback method: " + e);
        }
    }
}
