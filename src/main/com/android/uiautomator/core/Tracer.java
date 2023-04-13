package com.android.uiautomator.core;

import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class Tracer {
    private static final int CALLER_LOCATION = 6;
    private static final int METHOD_TO_TRACE_LOCATION = 5;
    private static final int MIN_STACK_TRACE_LENGTH = 7;
    private static final String UIAUTOMATOR_PACKAGE = "com.android.uiautomator.core";
    private static final String UNKNOWN_METHOD_STRING = "(unknown method)";
    private static Tracer mInstance = null;
    private File mOutputFile;
    private Mode mCurrentMode = Mode.NONE;
    private List<TracerSink> mSinks = new ArrayList();

    /* loaded from: classes.dex */
    public enum Mode {
        NONE,
        FILE,
        LOGCAT,
        ALL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface TracerSink {
        void close();

        void log(String str);
    }

    /* loaded from: classes.dex */
    private class FileSink implements TracerSink {
        private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        private PrintWriter mOut;

        public FileSink(File file) throws FileNotFoundException {
            this.mOut = new PrintWriter(file);
        }

        @Override // com.android.uiautomator.core.Tracer.TracerSink
        public void log(String message) {
            this.mOut.printf("%s %s\n", this.mDateFormat.format(new Date()), message);
        }

        @Override // com.android.uiautomator.core.Tracer.TracerSink
        public void close() {
            this.mOut.close();
        }
    }

    /* loaded from: classes.dex */
    private class LogcatSink implements TracerSink {
        private static final String LOGCAT_TAG = "UiAutomatorTrace";

        private LogcatSink() {
        }

        @Override // com.android.uiautomator.core.Tracer.TracerSink
        public void log(String message) {
            Log.i(LOGCAT_TAG, message);
        }

        @Override // com.android.uiautomator.core.Tracer.TracerSink
        public void close() {
        }
    }

    public static Tracer getInstance() {
        if (mInstance == null) {
            mInstance = new Tracer();
        }
        return mInstance;
    }

    public void setOutputMode(Mode mode) {
        closeSinks();
        this.mCurrentMode = mode;
        try {
            switch (C00061.$SwitchMap$com$android$uiautomator$core$Tracer$Mode[mode.ordinal()]) {
                case 1:
                    File file = this.mOutputFile;
                    if (file == null) {
                        throw new IllegalArgumentException("Please provide a filename before attempting write trace to a file");
                    }
                    this.mSinks.add(new FileSink(file));
                    return;
                case 2:
                    this.mSinks.add(new LogcatSink());
                    return;
                case 3:
                    this.mSinks.add(new LogcatSink());
                    File file2 = this.mOutputFile;
                    if (file2 == null) {
                        throw new IllegalArgumentException("Please provide a filename before attempting write trace to a file");
                    }
                    this.mSinks.add(new FileSink(file2));
                    return;
                default:
                    return;
            }
        } catch (FileNotFoundException e) {
            Log.w("Tracer", "Could not open log file: " + e.getMessage());
        }
    }

    /* renamed from: com.android.uiautomator.core.Tracer$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00061 {
        static final /* synthetic */ int[] $SwitchMap$com$android$uiautomator$core$Tracer$Mode;

        static {
            int[] iArr = new int[Mode.values().length];
            $SwitchMap$com$android$uiautomator$core$Tracer$Mode = iArr;
            try {
                iArr[Mode.FILE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$uiautomator$core$Tracer$Mode[Mode.LOGCAT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$uiautomator$core$Tracer$Mode[Mode.ALL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void closeSinks() {
        for (TracerSink sink : this.mSinks) {
            sink.close();
        }
        this.mSinks.clear();
    }

    public void setOutputFilename(String filename) {
        this.mOutputFile = new File(filename);
    }

    private void doTrace(Object[] arguments) {
        String caller;
        if (this.mCurrentMode == Mode.NONE || (caller = getCaller()) == null) {
            return;
        }
        log(String.format("%s (%s)", caller, join(", ", arguments)));
    }

    private void log(String message) {
        for (TracerSink sink : this.mSinks) {
            sink.log(message);
        }
    }

    public boolean isTracingEnabled() {
        return this.mCurrentMode != Mode.NONE;
    }

    public static void trace(Object... arguments) {
        getInstance().doTrace(arguments);
    }

    private static String join(String separator, Object[] strings) {
        if (strings.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(objectToString(strings[0]));
        for (int i = 1; i < strings.length; i++) {
            builder.append(separator);
            builder.append(objectToString(strings[i]));
        }
        return builder.toString();
    }

    private static String objectToString(Object obj) {
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return Arrays.deepToString((Object[]) obj);
            }
            return "[...]";
        }
        return obj.toString();
    }

    private static String getCaller() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < MIN_STACK_TRACE_LENGTH) {
            return UNKNOWN_METHOD_STRING;
        }
        StackTraceElement caller = stackTrace[METHOD_TO_TRACE_LOCATION];
        StackTraceElement previousCaller = stackTrace[CALLER_LOCATION];
        if (previousCaller.getClassName().startsWith(UIAUTOMATOR_PACKAGE)) {
            return null;
        }
        int indexOfDot = caller.getClassName().lastIndexOf(46);
        if (indexOfDot < 0) {
            indexOfDot = 0;
        }
        if (indexOfDot + 1 >= caller.getClassName().length()) {
            return UNKNOWN_METHOD_STRING;
        }
        String shortClassName = caller.getClassName().substring(indexOfDot + 1);
        return String.format("%s.%s from %s() at %s:%d", shortClassName, caller.getMethodName(), previousCaller.getMethodName(), previousCaller.getFileName(), Integer.valueOf(previousCaller.getLineNumber()));
    }
}
