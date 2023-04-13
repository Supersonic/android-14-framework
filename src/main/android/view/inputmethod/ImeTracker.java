package android.view.inputmethod;

import android.app.ActivityThread;
import android.content.Context;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemProperties;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.inputmethod.ImeTracker;
import com.android.internal.inputmethod.InputMethodDebug;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.util.LatencyTracker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes4.dex */
public interface ImeTracker {
    public static final int ORIGIN_CLIENT_HIDE_SOFT_INPUT = 2;
    public static final int ORIGIN_CLIENT_SHOW_SOFT_INPUT = 1;
    public static final int ORIGIN_SERVER_HIDE_INPUT = 4;
    public static final int ORIGIN_SERVER_START_INPUT = 3;
    public static final int PHASE_CLIENT_ANIMATION_CANCEL = 40;
    public static final int PHASE_CLIENT_ANIMATION_FINISHED_HIDE = 42;
    public static final int PHASE_CLIENT_ANIMATION_FINISHED_SHOW = 41;
    public static final int PHASE_CLIENT_ANIMATION_RUNNING = 39;
    public static final int PHASE_CLIENT_APPLY_ANIMATION = 32;
    public static final int PHASE_CLIENT_COLLECT_SOURCE_CONTROLS = 35;
    public static final int PHASE_CLIENT_CONTROL_ANIMATION = 33;
    public static final int PHASE_CLIENT_DISABLED_USER_ANIMATION = 34;
    public static final int PHASE_CLIENT_HANDLE_HIDE_INSETS = 31;
    public static final int PHASE_CLIENT_HANDLE_SHOW_INSETS = 30;
    public static final int PHASE_CLIENT_HIDE_INSETS = 29;
    public static final int PHASE_CLIENT_INSETS_CONSUMER_NOTIFY_HIDDEN = 38;
    public static final int PHASE_CLIENT_INSETS_CONSUMER_REQUEST_SHOW = 36;
    public static final int PHASE_CLIENT_REQUEST_IME_SHOW = 37;
    public static final int PHASE_CLIENT_SHOW_INSETS = 28;
    public static final int PHASE_CLIENT_VIEW_SERVED = 1;
    public static final int PHASE_IME_APPLY_VISIBILITY_INSETS_CONSUMER = 16;
    public static final int PHASE_IME_HIDE_SOFT_INPUT = 14;
    public static final int PHASE_IME_ON_SHOW_SOFT_INPUT_TRUE = 15;
    public static final int PHASE_IME_SHOW_SOFT_INPUT = 13;
    public static final int PHASE_IME_WRAPPER = 11;
    public static final int PHASE_IME_WRAPPER_DISPATCH = 12;
    public static final int PHASE_NOT_SET = 0;
    public static final int PHASE_SERVER_ACCESSIBILITY = 4;
    public static final int PHASE_SERVER_APPLY_IME_VISIBILITY = 17;
    public static final int PHASE_SERVER_CLIENT_FOCUSED = 3;
    public static final int PHASE_SERVER_CLIENT_KNOWN = 2;
    public static final int PHASE_SERVER_HAS_IME = 9;
    public static final int PHASE_SERVER_HIDE_IMPLICIT = 6;
    public static final int PHASE_SERVER_HIDE_NOT_ALWAYS = 7;
    public static final int PHASE_SERVER_SHOULD_HIDE = 10;
    public static final int PHASE_SERVER_SYSTEM_READY = 5;
    public static final int PHASE_SERVER_WAIT_IME = 8;
    public static final int PHASE_WM_ANIMATION_CREATE = 26;
    public static final int PHASE_WM_ANIMATION_RUNNING = 27;
    public static final int PHASE_WM_HAS_IME_INSETS_CONTROL_TARGET = 20;
    public static final int PHASE_WM_REMOTE_INSETS_CONTROLLER = 25;
    public static final int PHASE_WM_REMOTE_INSETS_CONTROL_TARGET_HIDE_INSETS = 24;
    public static final int PHASE_WM_REMOTE_INSETS_CONTROL_TARGET_SHOW_INSETS = 23;
    public static final int PHASE_WM_SHOW_IME_READY = 19;
    public static final int PHASE_WM_SHOW_IME_RUNNER = 18;
    public static final int PHASE_WM_WINDOW_INSETS_CONTROL_TARGET_HIDE_INSETS = 22;
    public static final int PHASE_WM_WINDOW_INSETS_CONTROL_TARGET_SHOW_INSETS = 21;
    public static final int STATUS_CANCEL = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_RUN = 1;
    public static final int STATUS_SUCCESS = 4;
    public static final int STATUS_TIMEOUT = 5;
    public static final String TAG = "ImeTracker";
    public static final String TOKEN_NONE = "TOKEN_NONE";
    public static final int TYPE_HIDE = 2;
    public static final int TYPE_SHOW = 1;
    public static final boolean DEBUG_IME_VISIBILITY = SystemProperties.getBoolean("persist.debug.imf_event", false);
    public static final ImeTracker LOGGER = new inputmethodImeTrackerC36371();
    public static final ImeJankTracker JANK_TRACKER = new ImeJankTracker();
    public static final ImeLatencyTracker LATENCY_TRACKER = new ImeLatencyTracker();

    /* loaded from: classes4.dex */
    public interface InputMethodJankContext {
        Context getDisplayContext();

        String getHostPackageName();

        SurfaceControl getTargetSurfaceControl();
    }

    /* loaded from: classes4.dex */
    public interface InputMethodLatencyContext {
        Context getAppContext();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Origin {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Phase {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Status {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Type {
    }

    void onCancelled(Token token, int i);

    void onFailed(Token token, int i);

    void onHidden(Token token);

    void onProgress(Token token, int i);

    Token onRequestHide(String str, int i, int i2, int i3);

    Token onRequestShow(String str, int i, int i2, int i3);

    void onShown(Token token);

    void onTodo(Token token, int i);

    static ImeTracker forLogging() {
        return LOGGER;
    }

    static ImeJankTracker forJank() {
        return JANK_TRACKER;
    }

    static ImeLatencyTracker forLatency() {
        return LATENCY_TRACKER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.view.inputmethod.ImeTracker$1  reason: invalid class name */
    /* loaded from: classes4.dex */
    public class inputmethodImeTrackerC36371 implements ImeTracker {
        private boolean mLogProgress = SystemProperties.getBoolean("persist.debug.imetracker", false);

        inputmethodImeTrackerC36371() {
            SystemProperties.addChangeCallback(new Runnable() { // from class: android.view.inputmethod.ImeTracker$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImeTracker.inputmethodImeTrackerC36371.this.lambda$new$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            this.mLogProgress = SystemProperties.getBoolean("persist.debug.imetracker", false);
        }

        @Override // android.view.inputmethod.ImeTracker
        public Token onRequestShow(String component, int uid, int origin, int reason) {
            String tag = getTag(component);
            Token token = IInputMethodManagerGlobalInvoker.onRequestShow(tag, uid, origin, reason);
            Log.m108i(ImeTracker.TAG, token.mTag + ": onRequestShow at " + Debug.originToString(origin) + " reason " + InputMethodDebug.softInputDisplayReasonToString(reason));
            return token;
        }

        @Override // android.view.inputmethod.ImeTracker
        public Token onRequestHide(String component, int uid, int origin, int reason) {
            String tag = getTag(component);
            Token token = IInputMethodManagerGlobalInvoker.onRequestHide(tag, uid, origin, reason);
            Log.m108i(ImeTracker.TAG, token.mTag + ": onRequestHide at " + Debug.originToString(origin) + " reason " + InputMethodDebug.softInputDisplayReasonToString(reason));
            return token;
        }

        @Override // android.view.inputmethod.ImeTracker
        public void onProgress(Token token, int phase) {
            if (token == null) {
                return;
            }
            IInputMethodManagerGlobalInvoker.onProgress(token.mBinder, phase);
            if (this.mLogProgress) {
                Log.m108i(ImeTracker.TAG, token.mTag + ": onProgress at " + Debug.phaseToString(phase));
            }
        }

        @Override // android.view.inputmethod.ImeTracker
        public void onFailed(Token token, int phase) {
            if (token == null) {
                return;
            }
            IInputMethodManagerGlobalInvoker.onFailed(token, phase);
            Log.m108i(ImeTracker.TAG, token.mTag + ": onFailed at " + Debug.phaseToString(phase));
        }

        @Override // android.view.inputmethod.ImeTracker
        public void onTodo(Token token, int phase) {
            if (token == null) {
                return;
            }
            Log.m108i(ImeTracker.TAG, token.mTag + ": onTodo at " + Debug.phaseToString(phase));
        }

        @Override // android.view.inputmethod.ImeTracker
        public void onCancelled(Token token, int phase) {
            if (token == null) {
                return;
            }
            IInputMethodManagerGlobalInvoker.onCancelled(token, phase);
            Log.m108i(ImeTracker.TAG, token.mTag + ": onCancelled at " + Debug.phaseToString(phase));
        }

        @Override // android.view.inputmethod.ImeTracker
        public void onShown(Token token) {
            if (token == null) {
                return;
            }
            IInputMethodManagerGlobalInvoker.onShown(token);
            Log.m108i(ImeTracker.TAG, token.mTag + ": onShown");
        }

        @Override // android.view.inputmethod.ImeTracker
        public void onHidden(Token token) {
            if (token == null) {
                return;
            }
            IInputMethodManagerGlobalInvoker.onHidden(token);
            Log.m108i(ImeTracker.TAG, token.mTag + ": onHidden");
        }

        private String getTag(String component) {
            if (component == null) {
                component = ActivityThread.currentProcessName();
            }
            return component + ":" + Integer.toHexString(ThreadLocalRandom.current().nextInt());
        }
    }

    /* loaded from: classes4.dex */
    public static final class Token implements Parcelable {
        public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() { // from class: android.view.inputmethod.ImeTracker.Token.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Token createFromParcel(Parcel in) {
                return new Token(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Token[] newArray(int size) {
                return new Token[size];
            }
        };
        private final IBinder mBinder;
        private final String mTag;

        public Token(IBinder binder, String tag) {
            this.mBinder = binder;
            this.mTag = tag;
        }

        private Token(Parcel in) {
            this.mBinder = in.readStrongBinder();
            this.mTag = in.readString8();
        }

        public IBinder getBinder() {
            return this.mBinder;
        }

        public String getTag() {
            return this.mTag;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStrongBinder(this.mBinder);
            dest.writeString8(this.mTag);
        }
    }

    /* loaded from: classes4.dex */
    public static final class Debug {
        private static final Map<Integer, String> sTypes = getFieldMapping(ImeTracker.class, "TYPE_");
        private static final Map<Integer, String> sStatus = getFieldMapping(ImeTracker.class, "STATUS_");
        private static final Map<Integer, String> sOrigins = getFieldMapping(ImeTracker.class, "ORIGIN_");
        private static final Map<Integer, String> sPhases = getFieldMapping(ImeTracker.class, "PHASE_");

        public static String typeToString(int type) {
            return sTypes.getOrDefault(Integer.valueOf(type), "TYPE_" + type);
        }

        public static String statusToString(int status) {
            return sStatus.getOrDefault(Integer.valueOf(status), "STATUS_" + status);
        }

        public static String originToString(int origin) {
            return sOrigins.getOrDefault(Integer.valueOf(origin), "ORIGIN_" + origin);
        }

        public static String phaseToString(int phase) {
            return sPhases.getOrDefault(Integer.valueOf(phase), "PHASE_" + phase);
        }

        private static Map<Integer, String> getFieldMapping(Class<?> cls, final String fieldPrefix) {
            return (Map) Arrays.stream(cls.getDeclaredFields()).filter(new Predicate() { // from class: android.view.inputmethod.ImeTracker$Debug$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean startsWith;
                    startsWith = ((Field) obj).getName().startsWith(fieldPrefix);
                    return startsWith;
                }
            }).collect(Collectors.toMap(new Function() { // from class: android.view.inputmethod.ImeTracker$Debug$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    int fieldValue;
                    fieldValue = ImeTracker.Debug.getFieldValue((Field) obj);
                    return Integer.valueOf(fieldValue);
                }
            }, new Function() { // from class: android.view.inputmethod.ImeTracker$Debug$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((Field) obj).getName();
                }
            }));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static int getFieldValue(Field field) {
            try {
                return field.getInt(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class ImeJankTracker {
        private ImeJankTracker() {
        }

        public void onRequestAnimation(InputMethodJankContext jankContext, int animType, boolean useSeparatedThread) {
            if (jankContext.getDisplayContext() == null || jankContext.getTargetSurfaceControl() == null) {
                return;
            }
            InteractionJankMonitor.Configuration.Builder builder = InteractionJankMonitor.Configuration.Builder.withSurface(69, jankContext.getDisplayContext(), jankContext.getTargetSurfaceControl()).setTag(String.format(Locale.US, "%d@%d@%s", Integer.valueOf(animType), Integer.valueOf(!useSeparatedThread ? 1 : 0), jankContext.getHostPackageName()));
            InteractionJankMonitor.getInstance().begin(builder);
        }

        public void onCancelAnimation() {
            InteractionJankMonitor.getInstance().cancel(69);
        }

        public void onFinishAnimation() {
            InteractionJankMonitor.getInstance().end(69);
        }
    }

    /* loaded from: classes4.dex */
    public static final class ImeLatencyTracker {
        private ImeLatencyTracker() {
        }

        private boolean shouldMonitorLatency(int reason) {
            return reason == 1 || reason == 4 || reason == 26 || reason == 28 || reason == 3 || reason == 5;
        }

        public void onRequestShow(Token token, int origin, int reason, InputMethodLatencyContext latencyContext) {
            if (shouldMonitorLatency(reason)) {
                LatencyTracker.getInstance(latencyContext.getAppContext()).onActionStart(20, InputMethodDebug.softInputDisplayReasonToString(reason));
            }
        }

        public void onRequestHide(Token token, int origin, int reason, InputMethodLatencyContext latencyContext) {
            if (shouldMonitorLatency(reason)) {
                LatencyTracker.getInstance(latencyContext.getAppContext()).onActionStart(21, InputMethodDebug.softInputDisplayReasonToString(reason));
            }
        }

        public void onShowFailed(Token token, int phase, InputMethodLatencyContext latencyContext) {
            onShowCancelled(token, phase, latencyContext);
        }

        public void onHideFailed(Token token, int phase, InputMethodLatencyContext latencyContext) {
            onHideCancelled(token, phase, latencyContext);
        }

        public void onShowCancelled(Token token, int phase, InputMethodLatencyContext latencyContext) {
            LatencyTracker.getInstance(latencyContext.getAppContext()).lambda$onActionStart$1(20);
        }

        public void onHideCancelled(Token token, int phase, InputMethodLatencyContext latencyContext) {
            LatencyTracker.getInstance(latencyContext.getAppContext()).lambda$onActionStart$1(21);
        }

        public void onShown(Token token, InputMethodLatencyContext latencyContext) {
            LatencyTracker.getInstance(latencyContext.getAppContext()).onActionEnd(20);
        }

        public void onHidden(Token token, InputMethodLatencyContext latencyContext) {
            LatencyTracker.getInstance(latencyContext.getAppContext()).onActionEnd(21);
        }
    }
}
