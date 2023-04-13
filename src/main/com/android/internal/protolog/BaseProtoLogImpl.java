package com.android.internal.protolog;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.p008os.ShellCommand;
import android.p008os.SystemClock;
import android.text.TextUtils;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.protolog.common.IProtoLogGroup;
import com.android.internal.protolog.common.LogDataType;
import com.android.internal.util.TraceBuffer;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
/* loaded from: classes4.dex */
public class BaseProtoLogImpl {
    private static final int DEFAULT_PER_CHUNK_SIZE = 0;
    private static final long MAGIC_NUMBER_VALUE = 5138409603453637200L;
    static final String PROTOLOG_VERSION = "1.0.0";
    private static final String TAG = "ProtoLog";
    private final TraceBuffer mBuffer;
    private final File mLogFile;
    private final int mPerChunkSize;
    private boolean mProtoLogEnabled;
    private final Object mProtoLogEnabledLock;
    private boolean mProtoLogEnabledLockFree;
    protected final ProtoLogViewerConfigReader mViewerConfig;
    private final String mViewerConfigFilename;
    protected static final TreeMap<String, IProtoLogGroup> LOG_GROUPS = new TreeMap<>();
    public static Runnable sCacheUpdater = new Runnable() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda5
        @Override // java.lang.Runnable
        public final void run() {
            BaseProtoLogImpl.lambda$static$0();
        }
    };

    /* loaded from: classes4.dex */
    public enum LogLevel {
        DEBUG,
        VERBOSE,
        INFO,
        WARN,
        ERROR,
        WTF
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$static$0() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void addLogGroupEnum(IProtoLogGroup[] config) {
        for (IProtoLogGroup group : config) {
            LOG_GROUPS.put(group.name(), group);
        }
    }

    public void log(LogLevel level, IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object[] args) {
        if (group.isLogToProto()) {
            logToProto(messageHash, paramsMask, args);
        }
        if (group.isLogToLogcat()) {
            logToLogcat(group.getTag(), level, messageHash, messageString, args);
        }
    }

    private void logToLogcat(String tag, LogLevel level, int messageHash, String messageString, Object[] args) {
        String message = null;
        if (messageString == null) {
            messageString = this.mViewerConfig.getViewerString(messageHash);
        }
        if (messageString != null) {
            if (args != null) {
                try {
                    message = TextUtils.formatSimple(messageString, args);
                } catch (Exception ex) {
                    Slog.m89w(TAG, "Invalid ProtoLog format string.", ex);
                }
            } else {
                message = messageString;
            }
        }
        if (message == null) {
            StringBuilder builder = new StringBuilder("UNKNOWN MESSAGE (" + messageHash + NavigationBarInflaterView.KEY_CODE_END);
            for (Object o : args) {
                builder.append(" ").append(o);
            }
            message = builder.toString();
        }
        passToLogcat(tag, level, message);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.protolog.BaseProtoLogImpl$1 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C43561 {

        /* renamed from: $SwitchMap$com$android$internal$protolog$BaseProtoLogImpl$LogLevel */
        static final /* synthetic */ int[] f908x7973bb3e;

        static {
            int[] iArr = new int[LogLevel.values().length];
            f908x7973bb3e = iArr;
            try {
                iArr[LogLevel.DEBUG.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f908x7973bb3e[LogLevel.VERBOSE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f908x7973bb3e[LogLevel.INFO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f908x7973bb3e[LogLevel.WARN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f908x7973bb3e[LogLevel.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f908x7973bb3e[LogLevel.WTF.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public void passToLogcat(String tag, LogLevel level, String message) {
        switch (C43561.f908x7973bb3e[level.ordinal()]) {
            case 1:
                Slog.m98d(tag, message);
                return;
            case 2:
                Slog.m92v(tag, message);
                return;
            case 3:
                Slog.m94i(tag, message);
                return;
            case 4:
                Slog.m90w(tag, message);
                return;
            case 5:
                Slog.m96e(tag, message);
                return;
            case 6:
                Slog.wtf(tag, message);
                return;
            default:
                return;
        }
    }

    private void logToProto(int messageHash, int paramsMask, Object[] args) {
        Object[] objArr = args;
        if (!isProtoEnabled()) {
            return;
        }
        try {
            ProtoOutputStream os = new ProtoOutputStream(this.mPerChunkSize);
            long token = os.start(2246267895812L);
            try {
                os.write(ProtoLogMessage.MESSAGE_HASH, messageHash);
                os.write(ProtoLogMessage.ELAPSED_REALTIME_NANOS, SystemClock.elapsedRealtimeNanos());
                if (objArr != null) {
                    try {
                        ArrayList<Long> longParams = new ArrayList<>();
                        ArrayList<Double> doubleParams = new ArrayList<>();
                        ArrayList<Boolean> booleanParams = new ArrayList<>();
                        int length = objArr.length;
                        int i = 0;
                        int argIndex = 0;
                        while (i < length) {
                            Object o = objArr[i];
                            int type = LogDataType.bitmaskToLogDataType(paramsMask, argIndex);
                            switch (type) {
                                case 0:
                                    os.write(2237677961219L, o.toString());
                                    break;
                                case 1:
                                    longParams.add(Long.valueOf(((Number) o).longValue()));
                                    break;
                                case 2:
                                    doubleParams.add(Double.valueOf(((Number) o).doubleValue()));
                                    break;
                                case 3:
                                    try {
                                        booleanParams.add(Boolean.valueOf(((Boolean) o).booleanValue()));
                                        break;
                                    } catch (ClassCastException ex) {
                                        os.write(2237677961219L, "(INVALID PARAMS_MASK) " + o.toString());
                                        Slog.m95e(TAG, "Invalid ProtoLog paramsMask", ex);
                                        break;
                                    }
                            }
                            argIndex++;
                            i++;
                            objArr = args;
                        }
                        if (longParams.size() > 0) {
                            os.writePackedSInt64(ProtoLogMessage.SINT64_PARAMS, longParams.stream().mapToLong(new ToLongFunction() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda3
                                @Override // java.util.function.ToLongFunction
                                public final long applyAsLong(Object obj) {
                                    long longValue;
                                    longValue = ((Long) obj).longValue();
                                    return longValue;
                                }
                            }).toArray());
                        }
                        if (doubleParams.size() > 0) {
                            os.writePackedDouble(ProtoLogMessage.DOUBLE_PARAMS, doubleParams.stream().mapToDouble(new ToDoubleFunction() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda4
                                @Override // java.util.function.ToDoubleFunction
                                public final double applyAsDouble(Object obj) {
                                    double doubleValue;
                                    doubleValue = ((Double) obj).doubleValue();
                                    return doubleValue;
                                }
                            }).toArray());
                        }
                        if (booleanParams.size() > 0) {
                            boolean[] arr = new boolean[booleanParams.size()];
                            for (int i2 = 0; i2 < booleanParams.size(); i2++) {
                                arr[i2] = booleanParams.get(i2).booleanValue();
                            }
                            os.writePackedBool(ProtoLogMessage.BOOLEAN_PARAMS, arr);
                        }
                    } catch (Exception e) {
                        e = e;
                        Slog.m95e(TAG, "Exception while logging to proto", e);
                        return;
                    }
                }
                os.end(token);
                this.mBuffer.add(os);
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    public BaseProtoLogImpl(File file, String viewerConfigFilename, int bufferCapacity, ProtoLogViewerConfigReader viewerConfig) {
        this(file, viewerConfigFilename, bufferCapacity, viewerConfig, 0);
    }

    public BaseProtoLogImpl(File file, String viewerConfigFilename, int bufferCapacity, ProtoLogViewerConfigReader viewerConfig, int perChunkSize) {
        this.mProtoLogEnabledLock = new Object();
        this.mLogFile = file;
        this.mBuffer = new TraceBuffer(bufferCapacity);
        this.mViewerConfigFilename = viewerConfigFilename;
        this.mViewerConfig = viewerConfig;
        this.mPerChunkSize = perChunkSize;
    }

    public void startProtoLog(PrintWriter pw) {
        if (isProtoEnabled()) {
            return;
        }
        synchronized (this.mProtoLogEnabledLock) {
            logAndPrintln(pw, "Start logging to " + this.mLogFile + MediaMetrics.SEPARATOR);
            this.mBuffer.resetBuffer();
            this.mProtoLogEnabled = true;
            this.mProtoLogEnabledLockFree = true;
        }
        sCacheUpdater.run();
    }

    public void stopProtoLog(PrintWriter pw, boolean writeToFile) {
        if (!isProtoEnabled()) {
            return;
        }
        synchronized (this.mProtoLogEnabledLock) {
            logAndPrintln(pw, "Stop logging to " + this.mLogFile + ". Waiting for log to flush.");
            this.mProtoLogEnabledLockFree = false;
            this.mProtoLogEnabled = false;
            if (writeToFile) {
                writeProtoLogToFileLocked();
                logAndPrintln(pw, "Log written to " + this.mLogFile + MediaMetrics.SEPARATOR);
                this.mBuffer.resetBuffer();
            }
            if (this.mProtoLogEnabled) {
                logAndPrintln(pw, "ERROR: logging was re-enabled while waiting for flush.");
                throw new IllegalStateException("logging enabled while waiting for flush.");
            }
        }
        sCacheUpdater.run();
    }

    public boolean isProtoEnabled() {
        return this.mProtoLogEnabledLockFree;
    }

    protected int setLogging(boolean setTextLogging, boolean value, PrintWriter pw, String... groups) {
        for (String group : groups) {
            IProtoLogGroup g = LOG_GROUPS.get(group);
            if (g != null) {
                if (setTextLogging) {
                    g.setLogToLogcat(value);
                } else {
                    g.setLogToProto(value);
                }
            } else {
                logAndPrintln(pw, "No IProtoLogGroup named " + group);
                return -1;
            }
        }
        sCacheUpdater.run();
        return 0;
    }

    private int unknownCommand(PrintWriter pw) {
        pw.println("Unknown command");
        pw.println("Window manager logging options:");
        pw.println("  start: Start proto logging");
        pw.println("  stop: Stop proto logging");
        pw.println("  enable [group...]: Enable proto logging for given groups");
        pw.println("  disable [group...]: Disable proto logging for given groups");
        pw.println("  enable-text [group...]: Enable logcat logging for given groups");
        pw.println("  disable-text [group...]: Disable logcat logging for given groups");
        return -1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int onShellCommand(ShellCommand shell) {
        char c;
        PrintWriter pw = shell.getOutPrintWriter();
        String cmd = shell.getNextArg();
        if (cmd == null) {
            return unknownCommand(pw);
        }
        ArrayList<String> args = new ArrayList<>();
        while (true) {
            String arg = shell.getNextArg();
            if (arg == null) {
                break;
            }
            args.add(arg);
        }
        String[] groups = (String[]) args.toArray(new String[args.size()]);
        switch (cmd.hashCode()) {
            case -1475003593:
                if (cmd.equals("enable-text")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1298848381:
                if (cmd.equals("enable")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1032071950:
                if (cmd.equals("disable-text")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -892481550:
                if (cmd.equals("status")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3540994:
                if (cmd.equals("stop")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 109757538:
                if (cmd.equals("start")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1671308008:
                if (cmd.equals("disable")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                startProtoLog(pw);
                return 0;
            case 1:
                stopProtoLog(pw, true);
                return 0;
            case 2:
                logAndPrintln(pw, getStatus());
                return 0;
            case 3:
                return setLogging(false, true, pw, groups);
            case 4:
                this.mViewerConfig.loadViewerConfig(pw, this.mViewerConfigFilename);
                return setLogging(true, true, pw, groups);
            case 5:
                return setLogging(false, false, pw, groups);
            case 6:
                return setLogging(true, false, pw, groups);
            default:
                return unknownCommand(pw);
        }
    }

    public String getStatus() {
        StringBuilder append = new StringBuilder().append("ProtoLog status: ").append(isProtoEnabled() ? "Enabled" : "Disabled").append("\nEnabled log groups: \n  Proto: ");
        TreeMap<String, IProtoLogGroup> treeMap = LOG_GROUPS;
        return append.append((String) treeMap.values().stream().filter(new Predicate() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return BaseProtoLogImpl.lambda$getStatus$3((IProtoLogGroup) obj);
            }
        }).map(new Function() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((IProtoLogGroup) obj).name();
            }
        }).collect(Collectors.joining(" "))).append("\n  Logcat: ").append((String) treeMap.values().stream().filter(new Predicate() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return BaseProtoLogImpl.lambda$getStatus$4((IProtoLogGroup) obj);
            }
        }).map(new Function() { // from class: com.android.internal.protolog.BaseProtoLogImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((IProtoLogGroup) obj).name();
            }
        }).collect(Collectors.joining(" "))).append("\nLogging definitions loaded: ").append(this.mViewerConfig.knownViewerStringsNumber()).toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getStatus$3(IProtoLogGroup it) {
        return it.isEnabled() && it.isLogToProto();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getStatus$4(IProtoLogGroup it) {
        return it.isEnabled() && it.isLogToLogcat();
    }

    private void writeProtoLogToFileLocked() {
        try {
            long offset = System.currentTimeMillis() - (SystemClock.elapsedRealtimeNanos() / 1000000);
            ProtoOutputStream proto = new ProtoOutputStream(this.mPerChunkSize);
            proto.write(1125281431553L, MAGIC_NUMBER_VALUE);
            proto.write(1138166333442L, PROTOLOG_VERSION);
            proto.write(1125281431555L, offset);
            this.mBuffer.writeTraceToFile(this.mLogFile, proto);
        } catch (IOException e) {
            Slog.m95e(TAG, "Unable to write buffer to file", e);
        }
    }

    static void logAndPrintln(PrintWriter pw, String msg) {
        Slog.m94i(TAG, msg);
        if (pw != null) {
            pw.println(msg);
            pw.flush();
        }
    }
}
