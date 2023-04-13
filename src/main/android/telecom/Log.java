package android.telecom;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.p008os.Build;
import android.telecom.Logging.EventManager;
import android.telecom.Logging.Session;
import android.telecom.Logging.SessionManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.util.IndentingPrintWriter;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public class Log {
    public static boolean DEBUG = false;
    public static boolean ERROR = false;
    private static final int EVENTS_TO_CACHE = 10;
    private static final int EVENTS_TO_CACHE_DEBUG = 20;
    private static final long EXTENDED_LOGGING_DURATION_MILLIS = 1800000;
    private static final boolean FORCE_LOGGING = false;
    public static boolean INFO;
    private static final int NUM_DIALABLE_DIGITS_TO_LOG;
    public static String TAG;
    private static final boolean USER_BUILD;
    public static boolean VERBOSE;
    public static boolean WARN;
    private static EventManager sEventManager;
    private static boolean sIsUnitTestingEnabled;
    private static boolean sIsUserExtendedLoggingEnabled;
    private static SessionManager sSessionManager;
    private static final Object sSingletonSync;
    private static long sUserExtendedLoggingStopTime;

    static {
        NUM_DIALABLE_DIGITS_TO_LOG = Build.IS_USER ? 0 : 2;
        TAG = "TelecomFramework";
        DEBUG = isLoggable(3);
        INFO = isLoggable(4);
        VERBOSE = isLoggable(2);
        WARN = isLoggable(5);
        ERROR = isLoggable(6);
        USER_BUILD = Build.IS_USER;
        sSingletonSync = new Object();
        sIsUserExtendedLoggingEnabled = false;
        sIsUnitTestingEnabled = false;
        sUserExtendedLoggingStopTime = 0L;
    }

    private Log() {
    }

    /* renamed from: d */
    public static void m138d(String prefix, String format, Object... args) {
        if (sIsUserExtendedLoggingEnabled) {
            maybeDisableLogging();
            Slog.m94i(TAG, buildMessage(prefix, format, args));
        } else if (DEBUG) {
            Slog.m98d(TAG, buildMessage(prefix, format, args));
        }
    }

    /* renamed from: d */
    public static void m139d(Object objectPrefix, String format, Object... args) {
        if (sIsUserExtendedLoggingEnabled) {
            maybeDisableLogging();
            Slog.m94i(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        } else if (DEBUG) {
            Slog.m98d(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* renamed from: i */
    public static void m134i(String prefix, String format, Object... args) {
        if (INFO) {
            Slog.m94i(TAG, buildMessage(prefix, format, args));
        }
    }

    /* renamed from: i */
    public static void m135i(Object objectPrefix, String format, Object... args) {
        if (INFO) {
            Slog.m94i(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* renamed from: v */
    public static void m132v(String prefix, String format, Object... args) {
        if (sIsUserExtendedLoggingEnabled) {
            maybeDisableLogging();
            Slog.m94i(TAG, buildMessage(prefix, format, args));
        } else if (VERBOSE) {
            Slog.m92v(TAG, buildMessage(prefix, format, args));
        }
    }

    /* renamed from: v */
    public static void m133v(Object objectPrefix, String format, Object... args) {
        if (sIsUserExtendedLoggingEnabled) {
            maybeDisableLogging();
            Slog.m94i(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        } else if (VERBOSE) {
            Slog.m92v(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* renamed from: w */
    public static void m130w(String prefix, String format, Object... args) {
        if (WARN) {
            Slog.m90w(TAG, buildMessage(prefix, format, args));
        }
    }

    /* renamed from: w */
    public static void m131w(Object objectPrefix, String format, Object... args) {
        if (WARN) {
            Slog.m90w(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    /* renamed from: e */
    public static void m136e(String prefix, Throwable tr, String format, Object... args) {
        if (ERROR) {
            Slog.m95e(TAG, buildMessage(prefix, format, args), tr);
        }
    }

    /* renamed from: e */
    public static void m137e(Object objectPrefix, Throwable tr, String format, Object... args) {
        if (ERROR) {
            Slog.m95e(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args), tr);
        }
    }

    public static void wtf(String prefix, Throwable tr, String format, Object... args) {
        Slog.wtf(TAG, buildMessage(prefix, format, args), tr);
    }

    public static void wtf(Object objectPrefix, Throwable tr, String format, Object... args) {
        Slog.wtf(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args), tr);
    }

    public static void wtf(String prefix, String format, Object... args) {
        String msg = buildMessage(prefix, format, args);
        Slog.wtf(TAG, msg, new IllegalStateException(msg));
    }

    public static void wtf(Object objectPrefix, String format, Object... args) {
        String msg = buildMessage(getPrefixFromObject(objectPrefix), format, args);
        Slog.wtf(TAG, msg, new IllegalStateException(msg));
    }

    public static void setSessionContext(Context context) {
        getSessionManager().setContext(context);
    }

    public static void startSession(String shortMethodName) {
        getSessionManager().startSession(shortMethodName, null);
    }

    public static void startSession(Session.Info info, String shortMethodName) {
        getSessionManager().startSession(info, shortMethodName, null);
    }

    public static void startSession(String shortMethodName, String callerIdentification) {
        getSessionManager().startSession(shortMethodName, callerIdentification);
    }

    public static void startSession(Session.Info info, String shortMethodName, String callerIdentification) {
        getSessionManager().startSession(info, shortMethodName, callerIdentification);
    }

    public static Session createSubsession() {
        return getSessionManager().createSubsession();
    }

    public static Session.Info getExternalSession() {
        return getSessionManager().getExternalSession();
    }

    public static Session.Info getExternalSession(String ownerInfo) {
        return getSessionManager().getExternalSession(ownerInfo);
    }

    public static void cancelSubsession(Session subsession) {
        getSessionManager().cancelSubsession(subsession);
    }

    public static void continueSession(Session subsession, String shortMethodName) {
        getSessionManager().continueSession(subsession, shortMethodName);
    }

    public static void endSession() {
        getSessionManager().endSession();
    }

    public static void registerSessionListener(SessionManager.ISessionListener l) {
        getSessionManager().registerSessionListener(l);
    }

    public static String getSessionId() {
        synchronized (sSingletonSync) {
            if (sSessionManager != null) {
                return getSessionManager().getSessionId();
            }
            return "";
        }
    }

    public static void addEvent(EventManager.Loggable recordEntry, String event) {
        getEventManager().event(recordEntry, event, null);
    }

    public static void addEvent(EventManager.Loggable recordEntry, String event, Object data) {
        getEventManager().event(recordEntry, event, data);
    }

    public static void addEvent(EventManager.Loggable recordEntry, String event, String format, Object... args) {
        getEventManager().event(recordEntry, event, format, args);
    }

    public static void registerEventListener(EventManager.EventListener e) {
        getEventManager().registerEventListener(e);
    }

    public static void addRequestResponsePair(EventManager.TimedEventPair p) {
        getEventManager().addRequestResponsePair(p);
    }

    public static void dumpEvents(IndentingPrintWriter pw) {
        synchronized (sSingletonSync) {
            if (sEventManager != null) {
                getEventManager().dumpEvents(pw);
            } else {
                pw.println("No Historical Events Logged.");
            }
        }
    }

    public static void dumpEventsTimeline(IndentingPrintWriter pw) {
        synchronized (sSingletonSync) {
            if (sEventManager != null) {
                getEventManager().dumpEventsTimeline(pw);
            } else {
                pw.println("No Historical Events Logged.");
            }
        }
    }

    public static void setIsExtendedLoggingEnabled(boolean isExtendedLoggingEnabled) {
        if (sIsUserExtendedLoggingEnabled == isExtendedLoggingEnabled) {
            return;
        }
        EventManager eventManager = sEventManager;
        if (eventManager != null) {
            eventManager.changeEventCacheSize(isExtendedLoggingEnabled ? 20 : 10);
        }
        sIsUserExtendedLoggingEnabled = isExtendedLoggingEnabled;
        if (isExtendedLoggingEnabled) {
            sUserExtendedLoggingStopTime = System.currentTimeMillis() + 1800000;
        } else {
            sUserExtendedLoggingStopTime = 0L;
        }
    }

    public static void setUnitTestingEnabled(boolean isEnabled) {
        sIsUnitTestingEnabled = isEnabled;
    }

    public static boolean isUnitTestingEnabled() {
        return sIsUnitTestingEnabled;
    }

    private static EventManager getEventManager() {
        if (sEventManager == null) {
            synchronized (sSingletonSync) {
                if (sEventManager == null) {
                    EventManager eventManager = new EventManager(new SessionManager.ISessionIdQueryHandler() { // from class: android.telecom.Log$$ExternalSyntheticLambda0
                        @Override // android.telecom.Logging.SessionManager.ISessionIdQueryHandler
                        public final String getSessionId() {
                            return Log.getSessionId();
                        }
                    });
                    sEventManager = eventManager;
                    return eventManager;
                }
            }
        }
        return sEventManager;
    }

    public static SessionManager getSessionManager() {
        if (sSessionManager == null) {
            synchronized (sSingletonSync) {
                if (sSessionManager == null) {
                    SessionManager sessionManager = new SessionManager();
                    sSessionManager = sessionManager;
                    return sessionManager;
                }
            }
        }
        return sSessionManager;
    }

    public static void setTag(String tag) {
        TAG = tag;
        DEBUG = isLoggable(3);
        INFO = isLoggable(4);
        VERBOSE = isLoggable(2);
        WARN = isLoggable(5);
        ERROR = isLoggable(6);
    }

    private static void maybeDisableLogging() {
        if (sIsUserExtendedLoggingEnabled && sUserExtendedLoggingStopTime < System.currentTimeMillis()) {
            sUserExtendedLoggingStopTime = 0L;
            sIsUserExtendedLoggingEnabled = false;
        }
    }

    public static boolean isLoggable(int level) {
        return android.util.Log.isLoggable(TAG, level);
    }

    public static String piiHandle(Object pii) {
        if (pii == null || VERBOSE) {
            return String.valueOf(pii);
        }
        StringBuilder sb = new StringBuilder();
        if (pii instanceof Uri) {
            Uri uri = (Uri) pii;
            String scheme = uri.getScheme();
            if (!TextUtils.isEmpty(scheme)) {
                sb.append(scheme).append(":");
            }
            String textToObfuscate = uri.getSchemeSpecificPart();
            if (PhoneAccount.SCHEME_TEL.equals(scheme)) {
                obfuscatePhoneNumber(sb, textToObfuscate);
            } else if ("sip".equals(scheme)) {
                for (int i = 0; i < textToObfuscate.length(); i++) {
                    char c = textToObfuscate.charAt(i);
                    if (c != '@' && c != '.') {
                        c = '*';
                    }
                    sb.append(c);
                }
            } else {
                sb.append(pii(pii));
            }
        } else if (pii instanceof String) {
            String number = (String) pii;
            obfuscatePhoneNumber(sb, number);
        }
        return sb.toString();
    }

    private static void obfuscatePhoneNumber(StringBuilder sb, String phoneNumber) {
        int numDigitsToObfuscate = getDialableCount(phoneNumber) - NUM_DIALABLE_DIGITS_TO_LOG;
        for (int i = 0; i < phoneNumber.length(); i++) {
            char c = phoneNumber.charAt(i);
            boolean isDialable = PhoneNumberUtils.isDialable(c);
            if (isDialable) {
                numDigitsToObfuscate--;
            }
            sb.append((!isDialable || numDigitsToObfuscate < 0) ? Character.valueOf(c) : "*");
        }
    }

    private static int getDialableCount(String toCount) {
        char[] charArray;
        int numDialable = 0;
        for (char c : toCount.toCharArray()) {
            if (PhoneNumberUtils.isDialable(c)) {
                numDialable++;
            }
        }
        return numDialable;
    }

    public static String pii(Object pii) {
        if (pii == null || VERBOSE) {
            return String.valueOf(pii);
        }
        return "***";
    }

    private static String getPrefixFromObject(Object obj) {
        return obj == null ? "<null>" : obj.getClass().getSimpleName();
    }

    private static String buildMessage(String prefix, String format, Object... args) {
        String msg;
        String format2;
        String sessionName = getSessionId();
        String sessionPostfix = TextUtils.isEmpty(sessionName) ? "" : ": " + sessionName;
        if (args != null) {
            try {
            } catch (IllegalFormatException ife) {
                m136e(TAG, (Throwable) ife, "Log: IllegalFormatException: formatString='%s' numArgs=%d", format, Integer.valueOf(args.length));
                msg = format + " (An error occurred while formatting the message.)";
            }
            if (args.length != 0) {
                format2 = String.format(Locale.US, format, args);
                msg = format2;
                return String.format(Locale.US, "%s: %s%s", prefix, msg, sessionPostfix);
            }
        }
        format2 = format;
        msg = format2;
        return String.format(Locale.US, "%s: %s%s", prefix, msg, sessionPostfix);
    }

    public static String getPackageAbbreviation(ComponentName componentName) {
        if (componentName == null) {
            return "";
        }
        return getPackageAbbreviation(componentName.getPackageName());
    }

    public static String getPackageAbbreviation(String packageName) {
        if (packageName == null) {
            return "";
        }
        return (String) Arrays.stream(packageName.split("\\.")).map(new Function() { // from class: android.telecom.Log$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Log.lambda$getPackageAbbreviation$0((String) obj);
            }
        }).collect(Collectors.joining(""));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String lambda$getPackageAbbreviation$0(String s) {
        return s.length() == 0 ? "" : s.substring(0, 1);
    }
}
