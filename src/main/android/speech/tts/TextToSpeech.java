package android.speech.tts;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.audio.Enums;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.speech.tts.ITextToSpeechCallback;
import android.speech.tts.ITextToSpeechManager;
import android.speech.tts.ITextToSpeechService;
import android.speech.tts.ITextToSpeechSessionCallback;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.content.NativeLibraryHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class TextToSpeech {
    public static final String ACTION_TTS_QUEUE_PROCESSING_COMPLETED = "android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED";
    private static final boolean DEBUG = false;
    public static final int ERROR = -1;
    public static final int ERROR_INVALID_REQUEST = -8;
    public static final int ERROR_NETWORK = -6;
    public static final int ERROR_NETWORK_TIMEOUT = -7;
    public static final int ERROR_NOT_INSTALLED_YET = -9;
    public static final int ERROR_OUTPUT = -5;
    public static final int ERROR_SERVICE = -4;
    public static final int ERROR_SYNTHESIS = -3;
    public static final int LANG_AVAILABLE = 0;
    public static final int LANG_COUNTRY_AVAILABLE = 1;
    public static final int LANG_COUNTRY_VAR_AVAILABLE = 2;
    public static final int LANG_MISSING_DATA = -1;
    public static final int LANG_NOT_SUPPORTED = -2;
    public static final int QUEUE_ADD = 1;
    static final int QUEUE_DESTROY = 2;
    public static final int QUEUE_FLUSH = 0;
    public static final int STOPPED = -2;
    public static final int SUCCESS = 0;
    private static final String TAG = "TextToSpeech";
    private Connection mConnectingServiceConnection;
    private final Context mContext;
    private volatile String mCurrentEngine;
    private final Map<String, Uri> mEarcons;
    private final TtsEngines mEnginesHelper;
    private final Executor mInitExecutor;
    private OnInitListener mInitListener;
    private final boolean mIsSystem;
    private final Bundle mParams;
    private String mRequestedEngine;
    private Connection mServiceConnection;
    private final Object mStartLock;
    private final boolean mUseFallback;
    private volatile UtteranceProgressListener mUtteranceProgressListener;
    private final Map<CharSequence, Uri> mUtterances;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public interface Action<R> {
        R run(ITextToSpeechService iTextToSpeechService) throws RemoteException;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Error {
    }

    /* loaded from: classes3.dex */
    public interface OnInitListener {
        void onInit(int i);
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnUtteranceCompletedListener {
        void onUtteranceCompleted(String str);
    }

    /* loaded from: classes3.dex */
    public class Engine {
        public static final String ACTION_CHECK_TTS_DATA = "android.speech.tts.engine.CHECK_TTS_DATA";
        public static final String ACTION_GET_SAMPLE_TEXT = "android.speech.tts.engine.GET_SAMPLE_TEXT";
        public static final String ACTION_INSTALL_TTS_DATA = "android.speech.tts.engine.INSTALL_TTS_DATA";
        public static final String ACTION_TTS_DATA_INSTALLED = "android.speech.tts.engine.TTS_DATA_INSTALLED";
        @Deprecated
        public static final int CHECK_VOICE_DATA_BAD_DATA = -1;
        public static final int CHECK_VOICE_DATA_FAIL = 0;
        @Deprecated
        public static final int CHECK_VOICE_DATA_MISSING_DATA = -2;
        @Deprecated
        public static final int CHECK_VOICE_DATA_MISSING_VOLUME = -3;
        public static final int CHECK_VOICE_DATA_PASS = 1;
        @Deprecated
        public static final String DEFAULT_ENGINE = "com.svox.pico";
        public static final float DEFAULT_PAN = 0.0f;
        public static final int DEFAULT_PITCH = 100;
        public static final int DEFAULT_RATE = 100;
        public static final int DEFAULT_STREAM = 3;
        public static final float DEFAULT_VOLUME = 1.0f;
        public static final String EXTRA_AVAILABLE_VOICES = "availableVoices";
        @Deprecated
        public static final String EXTRA_CHECK_VOICE_DATA_FOR = "checkVoiceDataFor";
        public static final String EXTRA_SAMPLE_TEXT = "sampleText";
        @Deprecated
        public static final String EXTRA_TTS_DATA_INSTALLED = "dataInstalled";
        public static final String EXTRA_UNAVAILABLE_VOICES = "unavailableVoices";
        @Deprecated
        public static final String EXTRA_VOICE_DATA_FILES = "dataFiles";
        @Deprecated
        public static final String EXTRA_VOICE_DATA_FILES_INFO = "dataFilesInfo";
        @Deprecated
        public static final String EXTRA_VOICE_DATA_ROOT_DIRECTORY = "dataRoot";
        public static final String INTENT_ACTION_TTS_SERVICE = "android.intent.action.TTS_SERVICE";
        @Deprecated
        public static final String KEY_FEATURE_EMBEDDED_SYNTHESIS = "embeddedTts";
        public static final String KEY_FEATURE_NETWORK_RETRIES_COUNT = "networkRetriesCount";
        @Deprecated
        public static final String KEY_FEATURE_NETWORK_SYNTHESIS = "networkTts";
        public static final String KEY_FEATURE_NETWORK_TIMEOUT_MS = "networkTimeoutMs";
        public static final String KEY_FEATURE_NOT_INSTALLED = "notInstalled";
        public static final String KEY_PARAM_AUDIO_ATTRIBUTES = "audioAttributes";
        public static final String KEY_PARAM_COUNTRY = "country";
        public static final String KEY_PARAM_ENGINE = "engine";
        public static final String KEY_PARAM_LANGUAGE = "language";
        public static final String KEY_PARAM_PAN = "pan";
        public static final String KEY_PARAM_PITCH = "pitch";
        public static final String KEY_PARAM_RATE = "rate";
        public static final String KEY_PARAM_SESSION_ID = "sessionId";
        public static final String KEY_PARAM_STREAM = "streamType";
        public static final String KEY_PARAM_UTTERANCE_ID = "utteranceId";
        public static final String KEY_PARAM_VARIANT = "variant";
        public static final String KEY_PARAM_VOICE_NAME = "voiceName";
        public static final String KEY_PARAM_VOLUME = "volume";
        public static final String SERVICE_META_DATA = "android.speech.tts";
        public static final int USE_DEFAULTS = 0;

        public Engine() {
        }
    }

    public TextToSpeech(Context context, OnInitListener listener) {
        this(context, listener, null);
    }

    public TextToSpeech(Context context, OnInitListener listener, String engine) {
        this(context, listener, engine, null, true);
    }

    public TextToSpeech(Context context, OnInitListener listener, String engine, String packageName, boolean useFallback) {
        this(context, null, listener, engine, packageName, useFallback, true);
    }

    private TextToSpeech(Context context, Executor initExecutor, OnInitListener initListener, String engine, String packageName, boolean useFallback, boolean isSystem) {
        this.mStartLock = new Object();
        this.mParams = new Bundle();
        this.mCurrentEngine = null;
        this.mContext = context;
        this.mInitExecutor = initExecutor;
        this.mInitListener = initListener;
        this.mRequestedEngine = engine;
        this.mUseFallback = useFallback;
        this.mEarcons = new HashMap();
        this.mUtterances = new HashMap();
        this.mUtteranceProgressListener = null;
        this.mEnginesHelper = new TtsEngines(context);
        this.mIsSystem = isSystem;
        initTts();
    }

    private <R> R runActionNoReconnect(Action<R> action, R errorResult, String method, boolean onlyEstablishedConnection) {
        return (R) runAction(action, errorResult, method, false, onlyEstablishedConnection);
    }

    private <R> R runAction(Action<R> action, R errorResult, String method) {
        return (R) runAction(action, errorResult, method, true, true);
    }

    private <R> R runAction(Action<R> action, R errorResult, String method, boolean reconnect, boolean onlyEstablishedConnection) {
        synchronized (this.mStartLock) {
            Connection connection = this.mServiceConnection;
            if (connection == null) {
                Log.m104w(TAG, method + " failed: not bound to TTS engine");
                return errorResult;
            }
            return (R) connection.runAction(action, errorResult, method, reconnect, onlyEstablishedConnection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int initTts() {
        String str = this.mRequestedEngine;
        if (str != null) {
            if (this.mEnginesHelper.isEngineInstalled(str)) {
                if (connectToEngine(this.mRequestedEngine)) {
                    this.mCurrentEngine = this.mRequestedEngine;
                    return 0;
                } else if (!this.mUseFallback) {
                    this.mCurrentEngine = null;
                    dispatchOnInit(-1);
                    return -1;
                }
            } else if (!this.mUseFallback) {
                Log.m108i(TAG, "Requested engine not installed: " + this.mRequestedEngine);
                this.mCurrentEngine = null;
                dispatchOnInit(-1);
                return -1;
            }
        }
        String defaultEngine = getDefaultEngine();
        if (defaultEngine != null && !defaultEngine.equals(this.mRequestedEngine) && connectToEngine(defaultEngine)) {
            this.mCurrentEngine = defaultEngine;
            return 0;
        }
        String highestRanked = this.mEnginesHelper.getHighestRankedEngineName();
        if (highestRanked != null && !highestRanked.equals(this.mRequestedEngine) && !highestRanked.equals(defaultEngine) && connectToEngine(highestRanked)) {
            this.mCurrentEngine = highestRanked;
            return 0;
        }
        this.mCurrentEngine = null;
        dispatchOnInit(-1);
        return -1;
    }

    private boolean connectToEngine(String engine) {
        Connection connection;
        if (this.mIsSystem) {
            connection = new SystemConnection();
        } else {
            connection = new DirectConnection();
        }
        boolean bound = connection.connect(engine);
        if (!bound) {
            Log.m110e(TAG, "Failed to bind to " + engine);
            return false;
        }
        Log.m108i(TAG, "Sucessfully bound to " + engine);
        this.mConnectingServiceConnection = connection;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnInit(final int result) {
        Runnable onInitCommand = new Runnable() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TextToSpeech.this.lambda$dispatchOnInit$0(result);
            }
        };
        Executor executor = this.mInitExecutor;
        if (executor != null) {
            executor.execute(onInitCommand);
        } else {
            onInitCommand.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchOnInit$0(int result) {
        synchronized (this.mStartLock) {
            OnInitListener onInitListener = this.mInitListener;
            if (onInitListener != null) {
                onInitListener.onInit(result);
                this.mInitListener = null;
            }
        }
    }

    private IBinder getCallerIdentity() {
        return this.mServiceConnection.getCallerIdentity();
    }

    public void shutdown() {
        synchronized (this.mStartLock) {
            Connection connection = this.mConnectingServiceConnection;
            if (connection != null) {
                connection.disconnect();
                this.mConnectingServiceConnection = null;
                return;
            }
            runActionNoReconnect(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda15
                @Override // android.speech.tts.TextToSpeech.Action
                public final Object run(ITextToSpeechService iTextToSpeechService) {
                    Object lambda$shutdown$1;
                    lambda$shutdown$1 = TextToSpeech.this.lambda$shutdown$1(iTextToSpeechService);
                    return lambda$shutdown$1;
                }
            }, null, "shutdown", false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$shutdown$1(ITextToSpeechService service) throws RemoteException {
        service.setCallback(getCallerIdentity(), null);
        service.stop(getCallerIdentity());
        this.mServiceConnection.disconnect();
        this.mServiceConnection = null;
        this.mCurrentEngine = null;
        return null;
    }

    public int addSpeech(String text, String packagename, int resourceId) {
        return addSpeech(text, makeResourceUri(packagename, resourceId));
    }

    public int addSpeech(CharSequence text, String packagename, int resourceId) {
        return addSpeech(text, makeResourceUri(packagename, resourceId));
    }

    public int addSpeech(String text, String filename) {
        return addSpeech(text, Uri.parse(filename));
    }

    public int addSpeech(CharSequence text, File file) {
        return addSpeech(text, Uri.fromFile(file));
    }

    public int addSpeech(CharSequence text, Uri uri) {
        synchronized (this.mStartLock) {
            this.mUtterances.put(text, uri);
        }
        return 0;
    }

    public int addEarcon(String earcon, String packagename, int resourceId) {
        return addEarcon(earcon, makeResourceUri(packagename, resourceId));
    }

    @Deprecated
    public int addEarcon(String earcon, String filename) {
        return addEarcon(earcon, Uri.parse(filename));
    }

    public int addEarcon(String earcon, File file) {
        return addEarcon(earcon, Uri.fromFile(file));
    }

    public int addEarcon(String earcon, Uri uri) {
        synchronized (this.mStartLock) {
            this.mEarcons.put(earcon, uri);
        }
        return 0;
    }

    private Uri makeResourceUri(String packageName, int resourceId) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).encodedAuthority(packageName).appendEncodedPath(String.valueOf(resourceId)).build();
    }

    public int speak(final CharSequence text, final int queueMode, final Bundle params, final String utteranceId) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda16
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$speak$2;
                lambda$speak$2 = TextToSpeech.this.lambda$speak$2(text, queueMode, params, utteranceId, iTextToSpeechService);
                return lambda$speak$2;
            }
        }, -1, "speak")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$speak$2(CharSequence text, int queueMode, Bundle params, String utteranceId, ITextToSpeechService service) throws RemoteException {
        Uri utteranceUri = this.mUtterances.get(text);
        if (utteranceUri != null) {
            return Integer.valueOf(service.playAudio(getCallerIdentity(), utteranceUri, queueMode, getParams(params), utteranceId));
        }
        return Integer.valueOf(service.speak(getCallerIdentity(), text, queueMode, getParams(params), utteranceId));
    }

    @Deprecated
    public int speak(String text, int queueMode, HashMap<String, String> params) {
        return speak(text, queueMode, convertParamsHashMaptoBundle(params), params == null ? null : params.get(Engine.KEY_PARAM_UTTERANCE_ID));
    }

    public int playEarcon(final String earcon, final int queueMode, final Bundle params, final String utteranceId) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda3
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$playEarcon$3;
                lambda$playEarcon$3 = TextToSpeech.this.lambda$playEarcon$3(earcon, queueMode, params, utteranceId, iTextToSpeechService);
                return lambda$playEarcon$3;
            }
        }, -1, "playEarcon")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$playEarcon$3(String earcon, int queueMode, Bundle params, String utteranceId, ITextToSpeechService service) throws RemoteException {
        Uri earconUri = this.mEarcons.get(earcon);
        if (earconUri == null) {
            return -1;
        }
        return Integer.valueOf(service.playAudio(getCallerIdentity(), earconUri, queueMode, getParams(params), utteranceId));
    }

    @Deprecated
    public int playEarcon(String earcon, int queueMode, HashMap<String, String> params) {
        return playEarcon(earcon, queueMode, convertParamsHashMaptoBundle(params), params == null ? null : params.get(Engine.KEY_PARAM_UTTERANCE_ID));
    }

    public int playSilentUtterance(final long durationInMs, final int queueMode, final String utteranceId) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda9
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$playSilentUtterance$4;
                lambda$playSilentUtterance$4 = TextToSpeech.this.lambda$playSilentUtterance$4(durationInMs, queueMode, utteranceId, iTextToSpeechService);
                return lambda$playSilentUtterance$4;
            }
        }, -1, "playSilentUtterance")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$playSilentUtterance$4(long durationInMs, int queueMode, String utteranceId, ITextToSpeechService service) throws RemoteException {
        return Integer.valueOf(service.playSilence(getCallerIdentity(), durationInMs, queueMode, utteranceId));
    }

    @Deprecated
    public int playSilence(long durationInMs, int queueMode, HashMap<String, String> params) {
        return playSilentUtterance(durationInMs, queueMode, params == null ? null : params.get(Engine.KEY_PARAM_UTTERANCE_ID));
    }

    @Deprecated
    public Set<String> getFeatures(final Locale locale) {
        return (Set) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda11
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                return TextToSpeech.lambda$getFeatures$5(locale, iTextToSpeechService);
            }
        }, null, "getFeatures");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Set lambda$getFeatures$5(Locale locale, ITextToSpeechService service) throws RemoteException {
        try {
            String[] features = service.getFeaturesForLanguage(locale.getISO3Language(), locale.getISO3Country(), locale.getVariant());
            if (features == null) {
                return null;
            }
            Set<String> featureSet = new HashSet<>();
            Collections.addAll(featureSet, features);
            return featureSet;
        } catch (MissingResourceException e) {
            Log.m103w(TAG, "Couldn't retrieve 3 letter ISO 639-2/T language and/or ISO 3166 country code for locale: " + locale, e);
            return null;
        }
    }

    public boolean isSpeaking() {
        return ((Boolean) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda17
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Boolean valueOf;
                valueOf = Boolean.valueOf(iTextToSpeechService.isSpeaking());
                return valueOf;
            }
        }, false, "isSpeaking")).booleanValue();
    }

    public int stop() {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda4
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$stop$7;
                lambda$stop$7 = TextToSpeech.this.lambda$stop$7(iTextToSpeechService);
                return lambda$stop$7;
            }
        }, -1, "stop")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$stop$7(ITextToSpeechService service) throws RemoteException {
        return Integer.valueOf(service.stop(getCallerIdentity()));
    }

    public int setSpeechRate(float speechRate) {
        int intRate;
        if (speechRate > 0.0f && (intRate = (int) (100.0f * speechRate)) > 0) {
            synchronized (this.mStartLock) {
                this.mParams.putInt(Engine.KEY_PARAM_RATE, intRate);
            }
            return 0;
        }
        return -1;
    }

    public int setPitch(float pitch) {
        int intPitch;
        if (pitch > 0.0f && (intPitch = (int) (100.0f * pitch)) > 0) {
            synchronized (this.mStartLock) {
                this.mParams.putInt(Engine.KEY_PARAM_PITCH, intPitch);
            }
            return 0;
        }
        return -1;
    }

    public int setAudioAttributes(AudioAttributes audioAttributes) {
        if (audioAttributes != null) {
            synchronized (this.mStartLock) {
                this.mParams.putParcelable(Engine.KEY_PARAM_AUDIO_ATTRIBUTES, audioAttributes);
            }
            return 0;
        }
        return -1;
    }

    public String getCurrentEngine() {
        return this.mCurrentEngine;
    }

    @Deprecated
    public Locale getDefaultLanguage() {
        return (Locale) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda6
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                return iTextToSpeechService.getClientDefaultLanguage();
            }
        }, null, "getDefaultLanguage");
    }

    public int setLanguage(final Locale loc) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda7
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$setLanguage$9;
                lambda$setLanguage$9 = TextToSpeech.this.lambda$setLanguage$9(loc, iTextToSpeechService);
                return lambda$setLanguage$9;
            }
        }, -2, "setLanguage")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$setLanguage$9(Locale loc, ITextToSpeechService service) throws RemoteException {
        if (loc == null) {
            return -2;
        }
        try {
            String language = loc.getISO3Language();
            try {
                String country = loc.getISO3Country();
                String variant = loc.getVariant();
                int result = service.isLanguageAvailable(language, country, variant);
                if (result >= 0) {
                    String voiceName = service.getDefaultVoiceNameFor(language, country, variant);
                    if (TextUtils.isEmpty(voiceName)) {
                        Log.m104w(TAG, "Couldn't find the default voice for " + language + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + country + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + variant);
                        return -2;
                    } else if (service.loadVoice(getCallerIdentity(), voiceName) == -1) {
                        Log.m104w(TAG, "The service claimed " + language + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + country + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + variant + " was available with voice name " + voiceName + " but loadVoice returned ERROR");
                        return -2;
                    } else {
                        Voice voice = getVoice(service, voiceName);
                        if (voice == null) {
                            Log.m104w(TAG, "getDefaultVoiceNameFor returned " + voiceName + " for locale " + language + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + country + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + variant + " but getVoice returns null");
                            return -2;
                        }
                        String voiceLanguage = "";
                        try {
                            voiceLanguage = voice.getLocale().getISO3Language();
                        } catch (MissingResourceException e) {
                            Log.m103w(TAG, "Couldn't retrieve ISO 639-2/T language code for locale: " + voice.getLocale(), e);
                        }
                        String voiceCountry = "";
                        try {
                            voiceCountry = voice.getLocale().getISO3Country();
                        } catch (MissingResourceException e2) {
                            Log.m103w(TAG, "Couldn't retrieve ISO 3166 country code for locale: " + voice.getLocale(), e2);
                        }
                        this.mParams.putString(Engine.KEY_PARAM_VOICE_NAME, voiceName);
                        this.mParams.putString("language", voiceLanguage);
                        this.mParams.putString(Engine.KEY_PARAM_COUNTRY, voiceCountry);
                        this.mParams.putString(Engine.KEY_PARAM_VARIANT, voice.getLocale().getVariant());
                    }
                }
                return Integer.valueOf(result);
            } catch (MissingResourceException e3) {
                Log.m103w(TAG, "Couldn't retrieve ISO 3166 country code for locale: " + loc, e3);
                return -2;
            }
        } catch (MissingResourceException e4) {
            Log.m103w(TAG, "Couldn't retrieve ISO 639-2/T language code for locale: " + loc, e4);
            return -2;
        }
    }

    @Deprecated
    public Locale getLanguage() {
        return (Locale) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda0
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Locale lambda$getLanguage$10;
                lambda$getLanguage$10 = TextToSpeech.this.lambda$getLanguage$10(iTextToSpeechService);
                return lambda$getLanguage$10;
            }
        }, null, "getLanguage");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Locale lambda$getLanguage$10(ITextToSpeechService service) throws RemoteException {
        String lang = this.mParams.getString("language", "");
        String country = this.mParams.getString(Engine.KEY_PARAM_COUNTRY, "");
        String variant = this.mParams.getString(Engine.KEY_PARAM_VARIANT, "");
        return new Locale(lang, country, variant);
    }

    public Set<Locale> getAvailableLanguages() {
        return (Set) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda13
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                return TextToSpeech.lambda$getAvailableLanguages$11(iTextToSpeechService);
            }
        }, null, "getAvailableLanguages");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ HashSet lambda$getAvailableLanguages$11(ITextToSpeechService service) throws RemoteException {
        List<Voice> voices = service.getVoices();
        if (voices == null) {
            return new HashSet();
        }
        HashSet<Locale> locales = new HashSet<>();
        for (Voice voice : voices) {
            locales.add(voice.getLocale());
        }
        return locales;
    }

    public Set<Voice> getVoices() {
        return (Set) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda12
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                return iTextToSpeechService.getVoices();
            }
        }, null, "getVoices");
    }

    public int setVoice(final Voice voice) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda8
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$setVoice$13;
                lambda$setVoice$13 = TextToSpeech.this.lambda$setVoice$13(voice, iTextToSpeechService);
                return lambda$setVoice$13;
            }
        }, -2, "setVoice")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$setVoice$13(Voice voice, ITextToSpeechService service) throws RemoteException {
        int result = service.loadVoice(getCallerIdentity(), voice.getName());
        if (result == 0) {
            this.mParams.putString(Engine.KEY_PARAM_VOICE_NAME, voice.getName());
            String language = "";
            try {
                language = voice.getLocale().getISO3Language();
            } catch (MissingResourceException e) {
                Log.m103w(TAG, "Couldn't retrieve ISO 639-2/T language code for locale: " + voice.getLocale(), e);
            }
            String country = "";
            try {
                country = voice.getLocale().getISO3Country();
            } catch (MissingResourceException e2) {
                Log.m103w(TAG, "Couldn't retrieve ISO 3166 country code for locale: " + voice.getLocale(), e2);
            }
            this.mParams.putString("language", language);
            this.mParams.putString(Engine.KEY_PARAM_COUNTRY, country);
            this.mParams.putString(Engine.KEY_PARAM_VARIANT, voice.getLocale().getVariant());
        }
        return Integer.valueOf(result);
    }

    public Voice getVoice() {
        return (Voice) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda10
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Voice lambda$getVoice$14;
                lambda$getVoice$14 = TextToSpeech.this.lambda$getVoice$14(iTextToSpeechService);
                return lambda$getVoice$14;
            }
        }, null, "getVoice");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Voice lambda$getVoice$14(ITextToSpeechService service) throws RemoteException {
        String voiceName = this.mParams.getString(Engine.KEY_PARAM_VOICE_NAME, "");
        if (TextUtils.isEmpty(voiceName)) {
            return null;
        }
        return getVoice(service, voiceName);
    }

    private Voice getVoice(ITextToSpeechService service, String voiceName) throws RemoteException {
        List<Voice> voices = service.getVoices();
        if (voices == null) {
            Log.m104w(TAG, "getVoices returned null");
            return null;
        }
        for (Voice voice : voices) {
            if (voice.getName().equals(voiceName)) {
                return voice;
            }
        }
        Log.m104w(TAG, "Could not find voice " + voiceName + " in voice list");
        return null;
    }

    public Voice getDefaultVoice() {
        return (Voice) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda14
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                return TextToSpeech.lambda$getDefaultVoice$15(iTextToSpeechService);
            }
        }, null, "getDefaultVoice");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Voice lambda$getDefaultVoice$15(ITextToSpeechService service) throws RemoteException {
        List<Voice> voices;
        String[] defaultLanguage = service.getClientDefaultLanguage();
        if (defaultLanguage == null || defaultLanguage.length == 0) {
            Log.m110e(TAG, "service.getClientDefaultLanguage() returned empty array");
            return null;
        }
        String language = defaultLanguage[0];
        String country = defaultLanguage.length > 1 ? defaultLanguage[1] : "";
        String variant = defaultLanguage.length > 2 ? defaultLanguage[2] : "";
        int result = service.isLanguageAvailable(language, country, variant);
        if (result < 0) {
            return null;
        }
        String voiceName = service.getDefaultVoiceNameFor(language, country, variant);
        if (TextUtils.isEmpty(voiceName) || (voices = service.getVoices()) == null) {
            return null;
        }
        for (Voice voice : voices) {
            if (voice.getName().equals(voiceName)) {
                return voice;
            }
        }
        return null;
    }

    public int isLanguageAvailable(final Locale loc) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda2
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                return TextToSpeech.lambda$isLanguageAvailable$16(loc, iTextToSpeechService);
            }
        }, -2, "isLanguageAvailable")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Integer lambda$isLanguageAvailable$16(Locale loc, ITextToSpeechService service) throws RemoteException {
        try {
            String language = loc.getISO3Language();
            try {
                String country = loc.getISO3Country();
                return Integer.valueOf(service.isLanguageAvailable(language, country, loc.getVariant()));
            } catch (MissingResourceException e) {
                Log.m103w(TAG, "Couldn't retrieve ISO 3166 country code for locale: " + loc, e);
                return -2;
            }
        } catch (MissingResourceException e2) {
            Log.m103w(TAG, "Couldn't retrieve ISO 639-2/T language code for locale: " + loc, e2);
            return -2;
        }
    }

    public int synthesizeToFile(final CharSequence text, final Bundle params, final ParcelFileDescriptor fileDescriptor, final String utteranceId) {
        return ((Integer) runAction(new Action() { // from class: android.speech.tts.TextToSpeech$$ExternalSyntheticLambda5
            @Override // android.speech.tts.TextToSpeech.Action
            public final Object run(ITextToSpeechService iTextToSpeechService) {
                Integer lambda$synthesizeToFile$17;
                lambda$synthesizeToFile$17 = TextToSpeech.this.lambda$synthesizeToFile$17(text, fileDescriptor, params, utteranceId, iTextToSpeechService);
                return lambda$synthesizeToFile$17;
            }
        }, -1, "synthesizeToFile")).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$synthesizeToFile$17(CharSequence text, ParcelFileDescriptor fileDescriptor, Bundle params, String utteranceId, ITextToSpeechService service) throws RemoteException {
        return Integer.valueOf(service.synthesizeToFileDescriptor(getCallerIdentity(), text, fileDescriptor, getParams(params), utteranceId));
    }

    public int synthesizeToFile(CharSequence text, Bundle params, File file, String utteranceId) {
        if (file.exists() && !file.canWrite()) {
            Log.m110e(TAG, "Can't write to " + file);
            return -1;
        }
        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, Enums.AUDIO_FORMAT_MPEGH);
            int returnValue = synthesizeToFile(text, params, fileDescriptor, utteranceId);
            fileDescriptor.close();
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
            return returnValue;
        } catch (FileNotFoundException e) {
            Log.m109e(TAG, "Opening file " + file + " failed", e);
            return -1;
        } catch (IOException e2) {
            Log.m109e(TAG, "Closing file " + file + " failed", e2);
            return -1;
        }
    }

    @Deprecated
    public int synthesizeToFile(String text, HashMap<String, String> params, String filename) {
        return synthesizeToFile(text, convertParamsHashMaptoBundle(params), new File(filename), params.get(Engine.KEY_PARAM_UTTERANCE_ID));
    }

    private Bundle convertParamsHashMaptoBundle(HashMap<String, String> params) {
        if (params != null && !params.isEmpty()) {
            Bundle bundle = new Bundle();
            copyIntParam(bundle, params, Engine.KEY_PARAM_STREAM);
            copyIntParam(bundle, params, "sessionId");
            copyStringParam(bundle, params, Engine.KEY_PARAM_UTTERANCE_ID);
            copyFloatParam(bundle, params, Engine.KEY_PARAM_VOLUME);
            copyFloatParam(bundle, params, Engine.KEY_PARAM_PAN);
            copyStringParam(bundle, params, Engine.KEY_FEATURE_NETWORK_SYNTHESIS);
            copyStringParam(bundle, params, Engine.KEY_FEATURE_EMBEDDED_SYNTHESIS);
            copyIntParam(bundle, params, Engine.KEY_FEATURE_NETWORK_TIMEOUT_MS);
            copyIntParam(bundle, params, Engine.KEY_FEATURE_NETWORK_RETRIES_COUNT);
            if (!TextUtils.isEmpty(this.mCurrentEngine)) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    if (key != null && key.startsWith(this.mCurrentEngine)) {
                        bundle.putString(key, entry.getValue());
                    }
                }
            }
            return bundle;
        }
        return null;
    }

    private Bundle getParams(Bundle params) {
        if (params != null && !params.isEmpty()) {
            Bundle bundle = new Bundle(this.mParams);
            bundle.putAll(params);
            verifyIntegerBundleParam(bundle, Engine.KEY_PARAM_STREAM);
            verifyIntegerBundleParam(bundle, "sessionId");
            verifyStringBundleParam(bundle, Engine.KEY_PARAM_UTTERANCE_ID);
            verifyFloatBundleParam(bundle, Engine.KEY_PARAM_VOLUME);
            verifyFloatBundleParam(bundle, Engine.KEY_PARAM_PAN);
            verifyBooleanBundleParam(bundle, Engine.KEY_FEATURE_NETWORK_SYNTHESIS);
            verifyBooleanBundleParam(bundle, Engine.KEY_FEATURE_EMBEDDED_SYNTHESIS);
            verifyIntegerBundleParam(bundle, Engine.KEY_FEATURE_NETWORK_TIMEOUT_MS);
            verifyIntegerBundleParam(bundle, Engine.KEY_FEATURE_NETWORK_RETRIES_COUNT);
            return bundle;
        }
        return this.mParams;
    }

    private static boolean verifyIntegerBundleParam(Bundle bundle, String key) {
        if (bundle.containsKey(key) && !(bundle.get(key) instanceof Integer) && !(bundle.get(key) instanceof Long)) {
            bundle.remove(key);
            Log.m104w(TAG, "Synthesis request paramter " + key + " containst value  with invalid type. Should be an Integer or a Long");
            return false;
        }
        return true;
    }

    private static boolean verifyStringBundleParam(Bundle bundle, String key) {
        if (bundle.containsKey(key) && !(bundle.get(key) instanceof String)) {
            bundle.remove(key);
            Log.m104w(TAG, "Synthesis request paramter " + key + " containst value  with invalid type. Should be a String");
            return false;
        }
        return true;
    }

    private static boolean verifyBooleanBundleParam(Bundle bundle, String key) {
        if (bundle.containsKey(key) && !(bundle.get(key) instanceof Boolean) && !(bundle.get(key) instanceof String)) {
            bundle.remove(key);
            Log.m104w(TAG, "Synthesis request paramter " + key + " containst value  with invalid type. Should be a Boolean or String");
            return false;
        }
        return true;
    }

    private static boolean verifyFloatBundleParam(Bundle bundle, String key) {
        if (bundle.containsKey(key) && !(bundle.get(key) instanceof Float) && !(bundle.get(key) instanceof Double)) {
            bundle.remove(key);
            Log.m104w(TAG, "Synthesis request paramter " + key + " containst value  with invalid type. Should be a Float or a Double");
            return false;
        }
        return true;
    }

    private void copyStringParam(Bundle bundle, HashMap<String, String> params, String key) {
        String value = params.get(key);
        if (value != null) {
            bundle.putString(key, value);
        }
    }

    private void copyIntParam(Bundle bundle, HashMap<String, String> params, String key) {
        String valueString = params.get(key);
        if (!TextUtils.isEmpty(valueString)) {
            try {
                int value = Integer.parseInt(valueString);
                bundle.putInt(key, value);
            } catch (NumberFormatException e) {
            }
        }
    }

    private void copyFloatParam(Bundle bundle, HashMap<String, String> params, String key) {
        String valueString = params.get(key);
        if (!TextUtils.isEmpty(valueString)) {
            try {
                float value = Float.parseFloat(valueString);
                bundle.putFloat(key, value);
            } catch (NumberFormatException e) {
            }
        }
    }

    @Deprecated
    public int setOnUtteranceCompletedListener(OnUtteranceCompletedListener listener) {
        this.mUtteranceProgressListener = UtteranceProgressListener.from(listener);
        return 0;
    }

    public int setOnUtteranceProgressListener(UtteranceProgressListener listener) {
        this.mUtteranceProgressListener = listener;
        return 0;
    }

    @Deprecated
    public int setEngineByPackageName(String enginePackageName) {
        this.mRequestedEngine = enginePackageName;
        return initTts();
    }

    public String getDefaultEngine() {
        return this.mEnginesHelper.getDefaultEngine();
    }

    @Deprecated
    public boolean areDefaultsEnforced() {
        return false;
    }

    public List<EngineInfo> getEngines() {
        return this.mEnginesHelper.getEngines();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public abstract class Connection implements ServiceConnection {
        private final ITextToSpeechCallback.Stub mCallback;
        private boolean mEstablished;
        private SetupConnectionAsyncTask mOnSetupConnectionAsyncTask;
        private ITextToSpeechService mService;

        abstract boolean connect(String str);

        abstract void disconnect();

        private Connection() {
            this.mCallback = new ITextToSpeechCallback.Stub() { // from class: android.speech.tts.TextToSpeech.Connection.1
                @Override // android.speech.tts.ITextToSpeechCallback
                public void onStop(String utteranceId, boolean isStarted) throws RemoteException {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onStop(utteranceId, isStarted);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onSuccess(String utteranceId) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onDone(utteranceId);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onError(String utteranceId, int errorCode) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onError(utteranceId, errorCode);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onStart(String utteranceId) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onStart(utteranceId);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onBeginSynthesis(String utteranceId, int sampleRateInHz, int audioFormat, int channelCount) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onBeginSynthesis(utteranceId, sampleRateInHz, audioFormat, channelCount);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onAudioAvailable(String utteranceId, byte[] audio) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onAudioAvailable(utteranceId, audio);
                    }
                }

                @Override // android.speech.tts.ITextToSpeechCallback
                public void onRangeStart(String utteranceId, int start, int end, int frame) {
                    UtteranceProgressListener listener = TextToSpeech.this.mUtteranceProgressListener;
                    if (listener != null) {
                        listener.onRangeStart(utteranceId, start, end, frame);
                    }
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class SetupConnectionAsyncTask extends AsyncTask<Void, Void, Integer> {
            private SetupConnectionAsyncTask() {
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public Integer doInBackground(Void... params) {
                synchronized (TextToSpeech.this.mStartLock) {
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        Connection.this.mService.setCallback(Connection.this.getCallerIdentity(), Connection.this.mCallback);
                        if (TextToSpeech.this.mParams.getString("language") == null) {
                            String[] defaultLanguage = Connection.this.mService.getClientDefaultLanguage();
                            TextToSpeech.this.mParams.putString("language", defaultLanguage[0]);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_COUNTRY, defaultLanguage[1]);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_VARIANT, defaultLanguage[2]);
                            String defaultVoiceName = Connection.this.mService.getDefaultVoiceNameFor(defaultLanguage[0], defaultLanguage[1], defaultLanguage[2]);
                            TextToSpeech.this.mParams.putString(Engine.KEY_PARAM_VOICE_NAME, defaultVoiceName);
                        }
                        Log.m108i(TextToSpeech.TAG, "Setting up the connection to TTS engine...");
                        return 0;
                    } catch (RemoteException e) {
                        Log.m110e(TextToSpeech.TAG, "Error connecting to service, setCallback() failed");
                        return -1;
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public void onPostExecute(Integer result) {
                synchronized (TextToSpeech.this.mStartLock) {
                    if (Connection.this.mOnSetupConnectionAsyncTask == this) {
                        Connection.this.mOnSetupConnectionAsyncTask = null;
                    }
                    Connection.this.mEstablished = true;
                    TextToSpeech.this.dispatchOnInit(result.intValue());
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            synchronized (TextToSpeech.this.mStartLock) {
                TextToSpeech.this.mConnectingServiceConnection = null;
                Log.m108i(TextToSpeech.TAG, "Connected to TTS engine");
                SetupConnectionAsyncTask setupConnectionAsyncTask = this.mOnSetupConnectionAsyncTask;
                if (setupConnectionAsyncTask != null) {
                    setupConnectionAsyncTask.cancel(false);
                }
                this.mService = ITextToSpeechService.Stub.asInterface(service);
                TextToSpeech.this.mServiceConnection = this;
                this.mEstablished = false;
                SetupConnectionAsyncTask setupConnectionAsyncTask2 = new SetupConnectionAsyncTask();
                this.mOnSetupConnectionAsyncTask = setupConnectionAsyncTask2;
                setupConnectionAsyncTask2.execute(new Void[0]);
            }
        }

        public IBinder getCallerIdentity() {
            return this.mCallback;
        }

        protected boolean clearServiceConnection() {
            boolean result;
            synchronized (TextToSpeech.this.mStartLock) {
                result = false;
                SetupConnectionAsyncTask setupConnectionAsyncTask = this.mOnSetupConnectionAsyncTask;
                if (setupConnectionAsyncTask != null) {
                    result = setupConnectionAsyncTask.cancel(false);
                    this.mOnSetupConnectionAsyncTask = null;
                }
                this.mService = null;
                if (TextToSpeech.this.mServiceConnection == this) {
                    TextToSpeech.this.mServiceConnection = null;
                }
            }
            return result;
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.m108i(TextToSpeech.TAG, "Disconnected from TTS engine");
            if (clearServiceConnection()) {
                TextToSpeech.this.dispatchOnInit(-1);
            }
        }

        public boolean isEstablished() {
            return this.mService != null && this.mEstablished;
        }

        public <R> R runAction(Action<R> action, R errorResult, String method, boolean reconnect, boolean onlyEstablishedConnection) {
            synchronized (TextToSpeech.this.mStartLock) {
                try {
                    try {
                        if (this.mService == null) {
                            Log.m104w(TextToSpeech.TAG, method + " failed: not connected to TTS engine");
                            return errorResult;
                        } else if (onlyEstablishedConnection && !isEstablished()) {
                            Log.m104w(TextToSpeech.TAG, method + " failed: TTS engine connection not fully set up");
                            return errorResult;
                        } else {
                            return action.run(this.mService);
                        }
                    } catch (RemoteException ex) {
                        Log.m109e(TextToSpeech.TAG, method + " failed", ex);
                        if (reconnect) {
                            disconnect();
                            TextToSpeech.this.initTts();
                        }
                        return errorResult;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class DirectConnection extends Connection {
        private DirectConnection() {
            super();
        }

        @Override // android.speech.tts.TextToSpeech.Connection
        boolean connect(String engine) {
            Intent intent = new Intent(Engine.INTENT_ACTION_TTS_SERVICE);
            intent.setPackage(engine);
            return TextToSpeech.this.mContext.bindService(intent, this, 1);
        }

        @Override // android.speech.tts.TextToSpeech.Connection
        void disconnect() {
            TextToSpeech.this.mContext.unbindService(this);
            clearServiceConnection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SystemConnection extends Connection {
        private volatile ITextToSpeechSession mSession;

        private SystemConnection() {
            super();
        }

        @Override // android.speech.tts.TextToSpeech.Connection
        boolean connect(String engine) {
            IBinder binder = ServiceManager.getService(Context.TEXT_TO_SPEECH_MANAGER_SERVICE);
            ITextToSpeechManager manager = ITextToSpeechManager.Stub.asInterface(binder);
            if (manager == null) {
                Log.m110e(TextToSpeech.TAG, "System service is not available!");
                return false;
            }
            try {
                manager.createSession(engine, new ITextToSpeechSessionCallback.Stub() { // from class: android.speech.tts.TextToSpeech.SystemConnection.1
                    @Override // android.speech.tts.ITextToSpeechSessionCallback
                    public void onConnected(ITextToSpeechSession session, IBinder serviceBinder) {
                        SystemConnection.this.mSession = session;
                        SystemConnection.this.onServiceConnected(null, serviceBinder);
                    }

                    @Override // android.speech.tts.ITextToSpeechSessionCallback
                    public void onDisconnected() {
                        SystemConnection.this.onServiceDisconnected(null);
                        SystemConnection.this.mSession = null;
                    }

                    @Override // android.speech.tts.ITextToSpeechSessionCallback
                    public void onError(String errorInfo) {
                        Log.m104w(TextToSpeech.TAG, "System TTS connection error: " + errorInfo);
                        TextToSpeech.this.dispatchOnInit(-1);
                    }
                });
                return true;
            } catch (RemoteException ex) {
                Log.m109e(TextToSpeech.TAG, "Error communicating with the System Server: ", ex);
                throw ex.rethrowFromSystemServer();
            }
        }

        @Override // android.speech.tts.TextToSpeech.Connection
        void disconnect() {
            ITextToSpeechSession session = this.mSession;
            if (session != null) {
                try {
                    session.disconnect();
                } catch (RemoteException ex) {
                    Log.m103w(TextToSpeech.TAG, "Error disconnecting session", ex);
                }
                clearServiceConnection();
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class EngineInfo {
        public int icon;
        public String label;
        public String name;
        public int priority;
        public boolean system;

        public String toString() {
            return "EngineInfo{name=" + this.name + "}";
        }
    }

    public static int getMaxSpeechInputLength() {
        return 4000;
    }
}
