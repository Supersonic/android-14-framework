package android.view.textservice;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesSessionListener;
import dalvik.system.CloseGuard;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Executor;
/* loaded from: classes4.dex */
public class SpellCheckerSession {
    private static final boolean DBG = false;
    private static final int MSG_ON_GET_SUGGESTION_MULTIPLE = 1;
    private static final int MSG_ON_GET_SUGGESTION_MULTIPLE_FOR_SENTENCE = 2;
    public static final String SERVICE_META_DATA = "android.view.textservice.scs";
    private static final String TAG = SpellCheckerSession.class.getSimpleName();
    private final Executor mExecutor;
    private final CloseGuard mGuard;
    private final InternalListener mInternalListener;
    private final SpellCheckerInfo mSpellCheckerInfo;
    private final SpellCheckerSessionListener mSpellCheckerSessionListener;
    private final SpellCheckerSessionListenerImpl mSpellCheckerSessionListenerImpl;
    private final TextServicesManager mTextServicesManager;

    /* loaded from: classes4.dex */
    public interface SpellCheckerSessionListener {
        void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr);

        void onGetSuggestions(SuggestionsInfo[] suggestionsInfoArr);
    }

    public SpellCheckerSession(SpellCheckerInfo info, TextServicesManager tsm, SpellCheckerSessionListener listener, Executor executor) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mGuard = closeGuard;
        if (info == null || listener == null || tsm == null) {
            throw new NullPointerException();
        }
        this.mSpellCheckerInfo = info;
        SpellCheckerSessionListenerImpl spellCheckerSessionListenerImpl = new SpellCheckerSessionListenerImpl(this);
        this.mSpellCheckerSessionListenerImpl = spellCheckerSessionListenerImpl;
        this.mInternalListener = new InternalListener(spellCheckerSessionListenerImpl);
        this.mTextServicesManager = tsm;
        this.mSpellCheckerSessionListener = listener;
        this.mExecutor = executor;
        closeGuard.open("finishSession");
    }

    public boolean isSessionDisconnected() {
        return this.mSpellCheckerSessionListenerImpl.isDisconnected();
    }

    public SpellCheckerInfo getSpellChecker() {
        return this.mSpellCheckerInfo;
    }

    public void cancel() {
        this.mSpellCheckerSessionListenerImpl.cancel();
    }

    public void close() {
        this.mGuard.close();
        this.mSpellCheckerSessionListenerImpl.close();
        this.mTextServicesManager.finishSpellCheckerService(this.mSpellCheckerSessionListenerImpl);
    }

    public void getSentenceSuggestions(TextInfo[] textInfos, int suggestionsLimit) {
        InputMethodManager imm = this.mTextServicesManager.getInputMethodManager();
        if (imm != null && imm.isInputMethodSuppressingSpellChecker()) {
            handleOnGetSentenceSuggestionsMultiple(new SentenceSuggestionsInfo[0]);
        } else {
            this.mSpellCheckerSessionListenerImpl.getSentenceSuggestionsMultiple(textInfos, suggestionsLimit);
        }
    }

    @Deprecated
    public void getSuggestions(TextInfo textInfo, int suggestionsLimit) {
        getSuggestions(new TextInfo[]{textInfo}, suggestionsLimit, false);
    }

    @Deprecated
    public void getSuggestions(TextInfo[] textInfos, int suggestionsLimit, boolean sequentialWords) {
        InputMethodManager imm = this.mTextServicesManager.getInputMethodManager();
        if (imm != null && imm.isInputMethodSuppressingSpellChecker()) {
            handleOnGetSuggestionsMultiple(new SuggestionsInfo[0]);
        } else {
            this.mSpellCheckerSessionListenerImpl.getSuggestionsMultiple(textInfos, suggestionsLimit, sequentialWords);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleOnGetSuggestionsMultiple$0(SuggestionsInfo[] suggestionsInfos) {
        this.mSpellCheckerSessionListener.onGetSuggestions(suggestionsInfos);
    }

    void handleOnGetSuggestionsMultiple(final SuggestionsInfo[] suggestionsInfos) {
        this.mExecutor.execute(new Runnable() { // from class: android.view.textservice.SpellCheckerSession$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SpellCheckerSession.this.lambda$handleOnGetSuggestionsMultiple$0(suggestionsInfos);
            }
        });
    }

    void handleOnGetSentenceSuggestionsMultiple(final SentenceSuggestionsInfo[] suggestionsInfos) {
        this.mExecutor.execute(new Runnable() { // from class: android.view.textservice.SpellCheckerSession$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SpellCheckerSession.this.lambda$handleOnGetSentenceSuggestionsMultiple$1(suggestionsInfos);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleOnGetSentenceSuggestionsMultiple$1(SentenceSuggestionsInfo[] suggestionsInfos) {
        this.mSpellCheckerSessionListener.onGetSentenceSuggestions(suggestionsInfos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class SpellCheckerSessionListenerImpl extends ISpellCheckerSessionListener.Stub {
        private static final int STATE_CLOSED_AFTER_CONNECTION = 2;
        private static final int STATE_CLOSED_BEFORE_CONNECTION = 3;
        private static final int STATE_CONNECTED = 1;
        private static final int STATE_WAIT_CONNECTION = 0;
        private static final int TASK_CANCEL = 1;
        private static final int TASK_CLOSE = 3;
        private static final int TASK_GET_SUGGESTIONS_MULTIPLE = 2;
        private static final int TASK_GET_SUGGESTIONS_MULTIPLE_FOR_SENTENCE = 4;
        private Handler mAsyncHandler;
        private ISpellCheckerSession mISpellCheckerSession;
        private SpellCheckerSession mSpellCheckerSession;
        private HandlerThread mThread;
        private final Queue<SpellCheckerParams> mPendingTasks = new ArrayDeque();
        private int mState = 0;

        private static String taskToString(int task) {
            switch (task) {
                case 1:
                    return "TASK_CANCEL";
                case 2:
                    return "TASK_GET_SUGGESTIONS_MULTIPLE";
                case 3:
                    return "TASK_CLOSE";
                case 4:
                    return "TASK_GET_SUGGESTIONS_MULTIPLE_FOR_SENTENCE";
                default:
                    return "Unexpected task=" + task;
            }
        }

        private static String stateToString(int state) {
            switch (state) {
                case 0:
                    return "STATE_WAIT_CONNECTION";
                case 1:
                    return "STATE_CONNECTED";
                case 2:
                    return "STATE_CLOSED_AFTER_CONNECTION";
                case 3:
                    return "STATE_CLOSED_BEFORE_CONNECTION";
                default:
                    return "Unexpected state=" + state;
            }
        }

        SpellCheckerSessionListenerImpl(SpellCheckerSession spellCheckerSession) {
            this.mSpellCheckerSession = spellCheckerSession;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class SpellCheckerParams {
            public final boolean mSequentialWords;
            public ISpellCheckerSession mSession;
            public final int mSuggestionsLimit;
            public final TextInfo[] mTextInfos;
            public final int mWhat;

            public SpellCheckerParams(int what, TextInfo[] textInfos, int suggestionsLimit, boolean sequentialWords) {
                this.mWhat = what;
                this.mTextInfos = textInfos;
                this.mSuggestionsLimit = suggestionsLimit;
                this.mSequentialWords = sequentialWords;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processTask(ISpellCheckerSession session, SpellCheckerParams scp, boolean async) {
            if (async || this.mAsyncHandler == null) {
                switch (scp.mWhat) {
                    case 1:
                        try {
                            session.onCancel();
                            break;
                        } catch (RemoteException e) {
                            Log.m110e(SpellCheckerSession.TAG, "Failed to cancel " + e);
                            break;
                        }
                    case 2:
                        try {
                            session.onGetSuggestionsMultiple(scp.mTextInfos, scp.mSuggestionsLimit, scp.mSequentialWords);
                            break;
                        } catch (RemoteException e2) {
                            Log.m110e(SpellCheckerSession.TAG, "Failed to get suggestions " + e2);
                            break;
                        }
                    case 3:
                        try {
                            session.onClose();
                            break;
                        } catch (RemoteException e3) {
                            Log.m110e(SpellCheckerSession.TAG, "Failed to close " + e3);
                            break;
                        }
                    case 4:
                        try {
                            session.onGetSentenceSuggestionsMultiple(scp.mTextInfos, scp.mSuggestionsLimit);
                            break;
                        } catch (RemoteException e4) {
                            Log.m110e(SpellCheckerSession.TAG, "Failed to get suggestions " + e4);
                            break;
                        }
                }
            } else {
                scp.mSession = session;
                Handler handler = this.mAsyncHandler;
                handler.sendMessage(Message.obtain(handler, 1, scp));
            }
            if (scp.mWhat == 3) {
                synchronized (this) {
                    processCloseLocked();
                }
            }
        }

        private void processCloseLocked() {
            this.mISpellCheckerSession = null;
            HandlerThread handlerThread = this.mThread;
            if (handlerThread != null) {
                handlerThread.quit();
            }
            this.mSpellCheckerSession = null;
            this.mPendingTasks.clear();
            this.mThread = null;
            this.mAsyncHandler = null;
            switch (this.mState) {
                case 0:
                    this.mState = 3;
                    return;
                case 1:
                    this.mState = 2;
                    return;
                default:
                    Log.m110e(SpellCheckerSession.TAG, "processCloseLocked is called unexpectedly. mState=" + stateToString(this.mState));
                    return;
            }
        }

        public void onServiceConnected(ISpellCheckerSession session) {
            synchronized (this) {
                switch (this.mState) {
                    case 0:
                        if (session == null) {
                            Log.m110e(SpellCheckerSession.TAG, "ignoring onServiceConnected due to session=null");
                            return;
                        }
                        this.mISpellCheckerSession = session;
                        if ((session.asBinder() instanceof Binder) && this.mThread == null) {
                            HandlerThread handlerThread = new HandlerThread("SpellCheckerSession", 10);
                            this.mThread = handlerThread;
                            handlerThread.start();
                            this.mAsyncHandler = new Handler(this.mThread.getLooper()) { // from class: android.view.textservice.SpellCheckerSession.SpellCheckerSessionListenerImpl.1
                                @Override // android.p008os.Handler
                                public void handleMessage(Message msg) {
                                    SpellCheckerParams scp = (SpellCheckerParams) msg.obj;
                                    SpellCheckerSessionListenerImpl.this.processTask(scp.mSession, scp, true);
                                }
                            };
                        }
                        this.mState = 1;
                        while (!this.mPendingTasks.isEmpty()) {
                            processTask(session, this.mPendingTasks.poll(), false);
                        }
                        return;
                    case 3:
                        return;
                    default:
                        Log.m110e(SpellCheckerSession.TAG, "ignoring onServiceConnected due to unexpected mState=" + stateToString(this.mState));
                        return;
                }
            }
        }

        public void cancel() {
            processOrEnqueueTask(new SpellCheckerParams(1, null, 0, false));
        }

        public void getSuggestionsMultiple(TextInfo[] textInfos, int suggestionsLimit, boolean sequentialWords) {
            processOrEnqueueTask(new SpellCheckerParams(2, textInfos, suggestionsLimit, sequentialWords));
        }

        public void getSentenceSuggestionsMultiple(TextInfo[] textInfos, int suggestionsLimit) {
            processOrEnqueueTask(new SpellCheckerParams(4, textInfos, suggestionsLimit, false));
        }

        public void close() {
            processOrEnqueueTask(new SpellCheckerParams(3, null, 0, false));
        }

        public boolean isDisconnected() {
            boolean z;
            synchronized (this) {
                z = true;
                if (this.mState == 1) {
                    z = false;
                }
            }
            return z;
        }

        private void processOrEnqueueTask(SpellCheckerParams scp) {
            int i;
            synchronized (this) {
                if (scp.mWhat == 3 && ((i = this.mState) == 2 || i == 3)) {
                    return;
                }
                int i2 = this.mState;
                if (i2 != 0 && i2 != 1) {
                    Log.m110e(SpellCheckerSession.TAG, "ignoring processOrEnqueueTask due to unexpected mState=" + stateToString(this.mState) + " scp.mWhat=" + taskToString(scp.mWhat));
                } else if (i2 == 0) {
                    if (scp.mWhat == 3) {
                        processCloseLocked();
                        return;
                    }
                    SpellCheckerParams closeTask = null;
                    if (scp.mWhat == 1) {
                        while (!this.mPendingTasks.isEmpty()) {
                            SpellCheckerParams tmp = this.mPendingTasks.poll();
                            if (tmp.mWhat == 3) {
                                closeTask = tmp;
                            }
                        }
                    }
                    this.mPendingTasks.offer(scp);
                    if (closeTask != null) {
                        this.mPendingTasks.offer(closeTask);
                    }
                } else {
                    ISpellCheckerSession session = this.mISpellCheckerSession;
                    processTask(session, scp, false);
                }
            }
        }

        @Override // com.android.internal.textservice.ISpellCheckerSessionListener
        public void onGetSuggestions(SuggestionsInfo[] results) {
            SpellCheckerSession session = getSpellCheckerSession();
            if (session != null) {
                session.handleOnGetSuggestionsMultiple(results);
            }
        }

        @Override // com.android.internal.textservice.ISpellCheckerSessionListener
        public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
            SpellCheckerSession session = getSpellCheckerSession();
            if (session != null) {
                session.handleOnGetSentenceSuggestionsMultiple(results);
            }
        }

        private SpellCheckerSession getSpellCheckerSession() {
            SpellCheckerSession spellCheckerSession;
            synchronized (this) {
                spellCheckerSession = this.mSpellCheckerSession;
            }
            return spellCheckerSession;
        }
    }

    /* loaded from: classes4.dex */
    public static class SpellCheckerSessionParams {
        private final Bundle mExtras;
        private final Locale mLocale;
        private final boolean mShouldReferToSpellCheckerLanguageSettings;
        private final int mSupportedAttributes;

        private SpellCheckerSessionParams(Locale locale, boolean referToSpellCheckerLanguageSettings, int supportedAttributes, Bundle extras) {
            this.mLocale = locale;
            this.mShouldReferToSpellCheckerLanguageSettings = referToSpellCheckerLanguageSettings;
            this.mSupportedAttributes = supportedAttributes;
            this.mExtras = extras;
        }

        public Locale getLocale() {
            return this.mLocale;
        }

        public boolean shouldReferToSpellCheckerLanguageSettings() {
            return this.mShouldReferToSpellCheckerLanguageSettings;
        }

        public int getSupportedAttributes() {
            return this.mSupportedAttributes;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        /* loaded from: classes4.dex */
        public static final class Builder {
            private Locale mLocale;
            private boolean mShouldReferToSpellCheckerLanguageSettings = false;
            private int mSupportedAttributes = 0;
            private Bundle mExtras = Bundle.EMPTY;

            public SpellCheckerSessionParams build() {
                Locale locale = this.mLocale;
                if (locale == null && !this.mShouldReferToSpellCheckerLanguageSettings) {
                    throw new IllegalArgumentException("mLocale should not be null if  mShouldReferToSpellCheckerLanguageSettings is false.");
                }
                return new SpellCheckerSessionParams(locale, this.mShouldReferToSpellCheckerLanguageSettings, this.mSupportedAttributes, this.mExtras);
            }

            public Builder setLocale(Locale locale) {
                this.mLocale = locale;
                return this;
            }

            public Builder setShouldReferToSpellCheckerLanguageSettings(boolean shouldReferToSpellCheckerLanguageSettings) {
                this.mShouldReferToSpellCheckerLanguageSettings = shouldReferToSpellCheckerLanguageSettings;
                return this;
            }

            public Builder setSupportedAttributes(int supportedAttributes) {
                this.mSupportedAttributes = supportedAttributes;
                return this;
            }

            public Builder setExtras(Bundle extras) {
                this.mExtras = extras;
                return this;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class InternalListener extends ITextServicesSessionListener.Stub {
        private final SpellCheckerSessionListenerImpl mParentSpellCheckerSessionListenerImpl;

        public InternalListener(SpellCheckerSessionListenerImpl spellCheckerSessionListenerImpl) {
            this.mParentSpellCheckerSessionListenerImpl = spellCheckerSessionListenerImpl;
        }

        @Override // com.android.internal.textservice.ITextServicesSessionListener
        public void onServiceConnected(ISpellCheckerSession session) {
            this.mParentSpellCheckerSessionListenerImpl.onServiceConnected(session);
        }
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
                close();
            }
        } finally {
            super.finalize();
        }
    }

    public ITextServicesSessionListener getTextServicesSessionListener() {
        return this.mInternalListener;
    }

    public ISpellCheckerSessionListener getSpellCheckerSessionListener() {
        return this.mSpellCheckerSessionListenerImpl;
    }
}
