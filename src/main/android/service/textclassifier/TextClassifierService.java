package android.service.textclassifier;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.service.textclassifier.ITextClassifierService;
import android.service.textclassifier.TextClassifierService;
import android.text.TextUtils;
import android.util.Slog;
import android.view.textclassifier.ConversationActions;
import android.view.textclassifier.SelectionEvent;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationContext;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassificationSessionId;
import android.view.textclassifier.TextClassifier;
import android.view.textclassifier.TextClassifierEvent;
import android.view.textclassifier.TextLanguage;
import android.view.textclassifier.TextLinks;
import android.view.textclassifier.TextSelection;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@SystemApi
/* loaded from: classes3.dex */
public abstract class TextClassifierService extends Service {
    public static final int CONNECTED = 0;
    public static final int DISCONNECTED = 1;
    private static final String KEY_RESULT = "key_result";
    private static final String LOG_TAG = "TextClassifierService";
    public static final String SERVICE_INTERFACE = "android.service.textclassifier.TextClassifierService";
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper(), null, true);
    private final ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    private final ITextClassifierService.Stub mBinder = new BinderC26501();

    /* loaded from: classes3.dex */
    public interface Callback<T> {
        void onFailure(CharSequence charSequence);

        void onSuccess(T t);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ConnectionState {
    }

    public abstract void onClassifyText(TextClassificationSessionId textClassificationSessionId, TextClassification.Request request, CancellationSignal cancellationSignal, Callback<TextClassification> callback);

    public abstract void onGenerateLinks(TextClassificationSessionId textClassificationSessionId, TextLinks.Request request, CancellationSignal cancellationSignal, Callback<TextLinks> callback);

    public abstract void onSuggestSelection(TextClassificationSessionId textClassificationSessionId, TextSelection.Request request, CancellationSignal cancellationSignal, Callback<TextSelection> callback);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.service.textclassifier.TextClassifierService$1 */
    /* loaded from: classes3.dex */
    public class BinderC26501 extends ITextClassifierService.Stub {
        private final CancellationSignal mCancellationSignal = new CancellationSignal();

        BinderC26501() {
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onSuggestSelection(final TextClassificationSessionId sessionId, final TextSelection.Request request, final ITextClassifierCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onSuggestSelection$0(sessionId, request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSuggestSelection$0(TextClassificationSessionId sessionId, TextSelection.Request request, ITextClassifierCallback callback) {
            TextClassifierService.this.onSuggestSelection(sessionId, request, this.mCancellationSignal, new ProxyCallback(callback));
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onClassifyText(final TextClassificationSessionId sessionId, final TextClassification.Request request, final ITextClassifierCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onClassifyText$1(sessionId, request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClassifyText$1(TextClassificationSessionId sessionId, TextClassification.Request request, ITextClassifierCallback callback) {
            TextClassifierService.this.onClassifyText(sessionId, request, this.mCancellationSignal, new ProxyCallback(callback));
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onGenerateLinks(final TextClassificationSessionId sessionId, final TextLinks.Request request, final ITextClassifierCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onGenerateLinks$2(sessionId, request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onGenerateLinks$2(TextClassificationSessionId sessionId, TextLinks.Request request, ITextClassifierCallback callback) {
            TextClassifierService.this.onGenerateLinks(sessionId, request, this.mCancellationSignal, new ProxyCallback(callback));
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onSelectionEvent(final TextClassificationSessionId sessionId, final SelectionEvent event) {
            Objects.requireNonNull(event);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onSelectionEvent$3(sessionId, event);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSelectionEvent$3(TextClassificationSessionId sessionId, SelectionEvent event) {
            TextClassifierService.this.onSelectionEvent(sessionId, event);
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onTextClassifierEvent(final TextClassificationSessionId sessionId, final TextClassifierEvent event) {
            Objects.requireNonNull(event);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onTextClassifierEvent$4(sessionId, event);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTextClassifierEvent$4(TextClassificationSessionId sessionId, TextClassifierEvent event) {
            TextClassifierService.this.onTextClassifierEvent(sessionId, event);
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onDetectLanguage(final TextClassificationSessionId sessionId, final TextLanguage.Request request, final ITextClassifierCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onDetectLanguage$5(sessionId, request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetectLanguage$5(TextClassificationSessionId sessionId, TextLanguage.Request request, ITextClassifierCallback callback) {
            TextClassifierService.this.onDetectLanguage(sessionId, request, this.mCancellationSignal, new ProxyCallback(callback));
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onSuggestConversationActions(final TextClassificationSessionId sessionId, final ConversationActions.Request request, final ITextClassifierCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onSuggestConversationActions$6(sessionId, request, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSuggestConversationActions$6(TextClassificationSessionId sessionId, ConversationActions.Request request, ITextClassifierCallback callback) {
            TextClassifierService.this.onSuggestConversationActions(sessionId, request, this.mCancellationSignal, new ProxyCallback(callback));
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onCreateTextClassificationSession(final TextClassificationContext context, final TextClassificationSessionId sessionId) {
            Objects.requireNonNull(context);
            Objects.requireNonNull(sessionId);
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onCreateTextClassificationSession$7(context, sessionId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateTextClassificationSession$7(TextClassificationContext context, TextClassificationSessionId sessionId) {
            TextClassifierService.this.onCreateTextClassificationSession(context, sessionId);
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onDestroyTextClassificationSession(final TextClassificationSessionId sessionId) {
            TextClassifierService.this.mMainThreadHandler.post(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    TextClassifierService.BinderC26501.this.lambda$onDestroyTextClassificationSession$8(sessionId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDestroyTextClassificationSession$8(TextClassificationSessionId sessionId) {
            TextClassifierService.this.onDestroyTextClassificationSession(sessionId);
        }

        @Override // android.service.textclassifier.ITextClassifierService
        public void onConnectedStateChanged(int connected) {
            Runnable runnable;
            Handler handler = TextClassifierService.this.mMainThreadHandler;
            if (connected == 0) {
                final TextClassifierService textClassifierService = TextClassifierService.this;
                runnable = new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        TextClassifierService.this.onConnected();
                    }
                };
            } else {
                final TextClassifierService textClassifierService2 = TextClassifierService.this;
                runnable = new Runnable() { // from class: android.service.textclassifier.TextClassifierService$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        TextClassifierService.this.onDisconnected();
                    }
                };
            }
            handler.post(runnable);
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        onDisconnected();
        return super.onUnbind(intent);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public void onDetectLanguage(TextClassificationSessionId sessionId, final TextLanguage.Request request, CancellationSignal cancellationSignal, final Callback<TextLanguage> callback) {
        this.mSingleThreadExecutor.submit(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TextClassifierService.this.lambda$onDetectLanguage$0(callback, request);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDetectLanguage$0(Callback callback, TextLanguage.Request request) {
        callback.onSuccess(getLocalTextClassifier().detectLanguage(request));
    }

    public void onSuggestConversationActions(TextClassificationSessionId sessionId, final ConversationActions.Request request, CancellationSignal cancellationSignal, final Callback<ConversationActions> callback) {
        this.mSingleThreadExecutor.submit(new Runnable() { // from class: android.service.textclassifier.TextClassifierService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TextClassifierService.this.lambda$onSuggestConversationActions$1(callback, request);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSuggestConversationActions$1(Callback callback, ConversationActions.Request request) {
        callback.onSuccess(getLocalTextClassifier().suggestConversationActions(request));
    }

    @Deprecated
    public void onSelectionEvent(TextClassificationSessionId sessionId, SelectionEvent event) {
    }

    public void onTextClassifierEvent(TextClassificationSessionId sessionId, TextClassifierEvent event) {
    }

    public void onCreateTextClassificationSession(TextClassificationContext context, TextClassificationSessionId sessionId) {
    }

    public void onDestroyTextClassificationSession(TextClassificationSessionId sessionId) {
    }

    @Deprecated
    public final TextClassifier getLocalTextClassifier() {
        return TextClassifier.NO_OP;
    }

    public static TextClassifier getDefaultTextClassifierImplementation(Context context) {
        String defaultTextClassifierPackageName = context.getPackageManager().getDefaultTextClassifierPackageName();
        if (TextUtils.isEmpty(defaultTextClassifierPackageName)) {
            return TextClassifier.NO_OP;
        }
        if (defaultTextClassifierPackageName.equals(context.getPackageName())) {
            throw new RuntimeException("The default text classifier itself should not call thegetDefaultTextClassifierImplementation() method.");
        }
        TextClassificationManager tcm = (TextClassificationManager) context.getSystemService(TextClassificationManager.class);
        return tcm.getTextClassifier(2);
    }

    public static <T extends Parcelable> T getResponse(Bundle bundle) {
        return (T) bundle.getParcelable(KEY_RESULT);
    }

    public static <T extends Parcelable> void putResponse(Bundle bundle, T response) {
        bundle.putParcelable(KEY_RESULT, response);
    }

    public static ComponentName getServiceComponentName(Context context, String packageName, int resolveFlags) {
        Intent intent = new Intent(SERVICE_INTERFACE).setPackage(packageName);
        ResolveInfo ri = context.getPackageManager().resolveService(intent, resolveFlags);
        if (ri == null || ri.serviceInfo == null) {
            Slog.m90w(LOG_TAG, String.format("Package or service not found in package %s for user %d", packageName, Integer.valueOf(context.getUserId())));
            return null;
        }
        ServiceInfo si = ri.serviceInfo;
        String permission = si.permission;
        if (Manifest.C0000permission.BIND_TEXTCLASSIFIER_SERVICE.equals(permission)) {
            return si.getComponentName();
        }
        Slog.m90w(LOG_TAG, String.format("Service %s should require %s permission. Found %s permission", si.getComponentName(), Manifest.C0000permission.BIND_TEXTCLASSIFIER_SERVICE, si.permission));
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class ProxyCallback<T extends Parcelable> implements Callback<T> {
        private ITextClassifierCallback mTextClassifierCallback;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.service.textclassifier.TextClassifierService.Callback
        public /* bridge */ /* synthetic */ void onSuccess(Object obj) {
            onSuccess((ProxyCallback<T>) ((Parcelable) obj));
        }

        private ProxyCallback(ITextClassifierCallback textClassifierCallback) {
            this.mTextClassifierCallback = (ITextClassifierCallback) Objects.requireNonNull(textClassifierCallback);
        }

        public void onSuccess(T result) {
            try {
                Bundle bundle = new Bundle(1);
                bundle.putParcelable(TextClassifierService.KEY_RESULT, result);
                this.mTextClassifierCallback.onSuccess(bundle);
            } catch (RemoteException e) {
                Slog.m98d(TextClassifierService.LOG_TAG, "Error calling callback");
            }
        }

        @Override // android.service.textclassifier.TextClassifierService.Callback
        public void onFailure(CharSequence error) {
            try {
                Slog.m90w(TextClassifierService.LOG_TAG, "Request fail: " + ((Object) error));
                this.mTextClassifierCallback.onFailure();
            } catch (RemoteException e) {
                Slog.m98d(TextClassifierService.LOG_TAG, "Error calling callback");
            }
        }
    }
}
