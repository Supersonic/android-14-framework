package android.view.textclassifier;

import android.content.Context;
import android.p008os.Bundle;
import android.p008os.Looper;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.service.textclassifier.ITextClassifierCallback;
import android.service.textclassifier.ITextClassifierService;
import android.service.textclassifier.TextClassifierService;
import android.view.textclassifier.ConversationActions;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationContext;
import android.view.textclassifier.TextClassifier;
import android.view.textclassifier.TextLanguage;
import android.view.textclassifier.TextLinks;
import android.view.textclassifier.TextSelection;
import com.android.internal.util.IndentingPrintWriter;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes4.dex */
public final class SystemTextClassifier implements TextClassifier {
    private static final String LOG_TAG = "androidtc";
    private TextClassificationSessionId mSessionId;
    private final TextClassificationConstants mSettings;
    private final SystemTextClassifierMetadata mSystemTcMetadata;
    private final ITextClassifierService mManagerService = ITextClassifierService.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.TEXT_CLASSIFICATION_SERVICE));
    private final TextClassifier mFallback = TextClassifier.NO_OP;

    public SystemTextClassifier(Context context, TextClassificationConstants settings, boolean useDefault) throws ServiceManager.ServiceNotFoundException {
        this.mSettings = (TextClassificationConstants) Objects.requireNonNull(settings);
        this.mSystemTcMetadata = new SystemTextClassifierMetadata((String) Objects.requireNonNull(context.getOpPackageName()), context.getUserId(), useDefault);
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextSelection suggestSelection(TextSelection.Request request) {
        Objects.requireNonNull(request);
        TextClassifier.Utils.checkMainThread();
        try {
            request.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            BlockingCallback<TextSelection> callback = new BlockingCallback<>("textselection", this.mSettings);
            this.mManagerService.onSuggestSelection(this.mSessionId, request, callback);
            TextSelection selection = callback.get();
            if (selection != null) {
                return selection;
            }
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error suggesting selection for text. Using fallback.", e);
        }
        return this.mFallback.suggestSelection(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextClassification classifyText(TextClassification.Request request) {
        Objects.requireNonNull(request);
        TextClassifier.Utils.checkMainThread();
        try {
            request.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            BlockingCallback<TextClassification> callback = new BlockingCallback<>(Context.TEXT_CLASSIFICATION_SERVICE, this.mSettings);
            this.mManagerService.onClassifyText(this.mSessionId, request, callback);
            TextClassification classification = callback.get();
            if (classification != null) {
                return classification;
            }
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error classifying text. Using fallback.", e);
        }
        return this.mFallback.classifyText(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextLinks generateLinks(TextLinks.Request request) {
        Objects.requireNonNull(request);
        TextClassifier.Utils.checkMainThread();
        if (!TextClassifier.Utils.checkTextLength(request.getText(), getMaxGenerateLinksTextLength())) {
            return this.mFallback.generateLinks(request);
        }
        if (!this.mSettings.isSmartLinkifyEnabled() && request.isLegacyFallback()) {
            return TextClassifier.Utils.generateLegacyLinks(request);
        }
        try {
            request.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            BlockingCallback<TextLinks> callback = new BlockingCallback<>("textlinks", this.mSettings);
            this.mManagerService.onGenerateLinks(this.mSessionId, request, callback);
            TextLinks links = callback.get();
            if (links != null) {
                return links;
            }
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error generating links. Using fallback.", e);
        }
        return this.mFallback.generateLinks(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public void onSelectionEvent(SelectionEvent event) {
        Objects.requireNonNull(event);
        TextClassifier.Utils.checkMainThread();
        try {
            event.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            this.mManagerService.onSelectionEvent(this.mSessionId, event);
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error reporting selection event.", e);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public void onTextClassifierEvent(TextClassifierEvent event) {
        TextClassificationContext tcContext;
        Objects.requireNonNull(event);
        TextClassifier.Utils.checkMainThread();
        try {
            if (event.getEventContext() == null) {
                tcContext = new TextClassificationContext.Builder(this.mSystemTcMetadata.getCallingPackageName(), "unknown").build();
            } else {
                tcContext = event.getEventContext();
            }
            tcContext.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            event.setEventContext(tcContext);
            this.mManagerService.onTextClassifierEvent(this.mSessionId, event);
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error reporting textclassifier event.", e);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextLanguage detectLanguage(TextLanguage.Request request) {
        Objects.requireNonNull(request);
        TextClassifier.Utils.checkMainThread();
        try {
            request.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            BlockingCallback<TextLanguage> callback = new BlockingCallback<>("textlanguage", this.mSettings);
            this.mManagerService.onDetectLanguage(this.mSessionId, request, callback);
            TextLanguage textLanguage = callback.get();
            if (textLanguage != null) {
                return textLanguage;
            }
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error detecting language.", e);
        }
        return this.mFallback.detectLanguage(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public ConversationActions suggestConversationActions(ConversationActions.Request request) {
        Objects.requireNonNull(request);
        TextClassifier.Utils.checkMainThread();
        try {
            request.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            BlockingCallback<ConversationActions> callback = new BlockingCallback<>("conversation-actions", this.mSettings);
            this.mManagerService.onSuggestConversationActions(this.mSessionId, request, callback);
            ConversationActions conversationActions = callback.get();
            if (conversationActions != null) {
                return conversationActions;
            }
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error reporting selection event.", e);
        }
        return this.mFallback.suggestConversationActions(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public int getMaxGenerateLinksTextLength() {
        return this.mSettings.getGenerateLinksMaxTextLength();
    }

    @Override // android.view.textclassifier.TextClassifier
    public void destroy() {
        try {
            TextClassificationSessionId textClassificationSessionId = this.mSessionId;
            if (textClassificationSessionId != null) {
                this.mManagerService.onDestroyTextClassificationSession(textClassificationSessionId);
            }
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error destroying classification session.", e);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public void dump(IndentingPrintWriter printWriter) {
        printWriter.println("SystemTextClassifier:");
        printWriter.increaseIndent();
        printWriter.printPair("mFallback", this.mFallback);
        printWriter.printPair("mSessionId", this.mSessionId);
        printWriter.printPair("mSystemTcMetadata", this.mSystemTcMetadata);
        printWriter.decreaseIndent();
        printWriter.println();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initializeRemoteSession(TextClassificationContext classificationContext, TextClassificationSessionId sessionId) {
        this.mSessionId = (TextClassificationSessionId) Objects.requireNonNull(sessionId);
        try {
            classificationContext.setSystemTextClassifierMetadata(this.mSystemTcMetadata);
            this.mManagerService.onCreateTextClassificationSession(classificationContext, this.mSessionId);
        } catch (RemoteException e) {
            Log.m78e("androidtc", "Error starting a new classification session.", e);
        }
    }

    /* loaded from: classes4.dex */
    private static final class BlockingCallback<T extends Parcelable> extends ITextClassifierCallback.Stub {
        private final ResponseReceiver<T> mReceiver;

        BlockingCallback(String name, TextClassificationConstants settings) {
            this.mReceiver = new ResponseReceiver<>(name, settings);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.service.textclassifier.ITextClassifierCallback
        public void onSuccess(Bundle result) {
            this.mReceiver.onSuccess(TextClassifierService.getResponse(result));
        }

        @Override // android.service.textclassifier.ITextClassifierCallback
        public void onFailure() {
            this.mReceiver.onFailure();
        }

        public T get() {
            return this.mReceiver.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class ResponseReceiver<T> {
        private final CountDownLatch mLatch;
        private final String mName;
        private T mResponse;
        private final TextClassificationConstants mSettings;

        private ResponseReceiver(String name, TextClassificationConstants settings) {
            this.mLatch = new CountDownLatch(1);
            this.mName = name;
            this.mSettings = settings;
        }

        public void onSuccess(T response) {
            this.mResponse = response;
            this.mLatch.countDown();
        }

        public void onFailure() {
            Log.m78e("androidtc", "Request failed at " + this.mName, null);
            this.mLatch.countDown();
        }

        public T get() {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                try {
                    boolean success = this.mLatch.await(this.mSettings.getSystemTextClassifierApiTimeoutInSecond(), TimeUnit.SECONDS);
                    if (!success) {
                        Log.m76w("androidtc", "Timeout in ResponseReceiver.get(): " + this.mName);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.m78e("androidtc", "Interrupted during ResponseReceiver.get(): " + this.mName, e);
                }
            }
            return this.mResponse;
        }
    }
}
