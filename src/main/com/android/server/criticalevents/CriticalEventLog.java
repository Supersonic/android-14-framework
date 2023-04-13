package com.android.server.criticalevents;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Slog;
import com.android.framework.protobuf.nano.MessageNanoPrinter;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.RingBuffer;
import com.android.server.criticalevents.nano.CriticalEventLogProto;
import com.android.server.criticalevents.nano.CriticalEventLogStorageProto;
import com.android.server.criticalevents.nano.CriticalEventProto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
/* loaded from: classes.dex */
public class CriticalEventLog {
    @VisibleForTesting
    static final String FILENAME = "critical_event_log.pb";
    public static final String TAG = "CriticalEventLog";
    public static CriticalEventLog sInstance;
    public final ThreadSafeRingBuffer<CriticalEventProto> mEvents;
    public final Handler mHandler;
    public long mLastSaveAttemptMs;
    public final boolean mLoadAndSaveImmediately;
    public final File mLogFile;
    public final long mMinTimeBetweenSavesMs;
    public final Runnable mSaveRunnable;
    public final int mWindowMs;

    /* loaded from: classes.dex */
    public interface ILogLoader {
        void load(File file, ThreadSafeRingBuffer<CriticalEventProto> threadSafeRingBuffer);
    }

    @VisibleForTesting
    public CriticalEventLog(String str, int i, int i2, long j, boolean z, final ILogLoader iLogLoader) {
        this.mLastSaveAttemptMs = 0L;
        this.mSaveRunnable = new Runnable() { // from class: com.android.server.criticalevents.CriticalEventLog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CriticalEventLog.this.saveLogToFileNow();
            }
        };
        this.mLogFile = Paths.get(str, FILENAME).toFile();
        this.mWindowMs = i2;
        this.mMinTimeBetweenSavesMs = j;
        this.mLoadAndSaveImmediately = z;
        this.mEvents = new ThreadSafeRingBuffer<>(CriticalEventProto.class, i);
        HandlerThread handlerThread = new HandlerThread("CriticalEventLogIO");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        this.mHandler = handler;
        Runnable runnable = new Runnable() { // from class: com.android.server.criticalevents.CriticalEventLog$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CriticalEventLog.this.lambda$new$0(iLogLoader);
            }
        };
        if (!z) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ILogLoader iLogLoader) {
        iLogLoader.load(this.mLogFile, this.mEvents);
    }

    public CriticalEventLog() {
        this("/data/misc/critical-events", 20, (int) Duration.ofMinutes(5L).toMillis(), Duration.ofSeconds(2L).toMillis(), false, new LogLoader());
    }

    public static CriticalEventLog getInstance() {
        if (sInstance == null) {
            sInstance = new CriticalEventLog();
        }
        return sInstance;
    }

    public static void init() {
        getInstance();
    }

    @VisibleForTesting
    public long getWallTimeMillis() {
        return System.currentTimeMillis();
    }

    public void logWatchdog(String str, UUID uuid) {
        CriticalEventProto.Watchdog watchdog = new CriticalEventProto.Watchdog();
        watchdog.subject = str;
        watchdog.uuid = uuid.toString();
        CriticalEventProto criticalEventProto = new CriticalEventProto();
        criticalEventProto.setWatchdog(watchdog);
        log(criticalEventProto);
    }

    public void logHalfWatchdog(String str) {
        CriticalEventProto.HalfWatchdog halfWatchdog = new CriticalEventProto.HalfWatchdog();
        halfWatchdog.subject = str;
        CriticalEventProto criticalEventProto = new CriticalEventProto();
        criticalEventProto.setHalfWatchdog(halfWatchdog);
        log(criticalEventProto);
    }

    public void logAnr(String str, int i, String str2, int i2, int i3) {
        CriticalEventProto.AppNotResponding appNotResponding = new CriticalEventProto.AppNotResponding();
        appNotResponding.subject = str;
        appNotResponding.processClass = i;
        appNotResponding.process = str2;
        appNotResponding.uid = i2;
        appNotResponding.pid = i3;
        CriticalEventProto criticalEventProto = new CriticalEventProto();
        criticalEventProto.setAnr(appNotResponding);
        log(criticalEventProto);
    }

    public void logJavaCrash(String str, int i, String str2, int i2, int i3) {
        CriticalEventProto.JavaCrash javaCrash = new CriticalEventProto.JavaCrash();
        javaCrash.exceptionClass = str;
        javaCrash.processClass = i;
        javaCrash.process = str2;
        javaCrash.uid = i2;
        javaCrash.pid = i3;
        CriticalEventProto criticalEventProto = new CriticalEventProto();
        criticalEventProto.setJavaCrash(javaCrash);
        log(criticalEventProto);
    }

    public void logNativeCrash(int i, String str, int i2, int i3) {
        CriticalEventProto.NativeCrash nativeCrash = new CriticalEventProto.NativeCrash();
        nativeCrash.processClass = i;
        nativeCrash.process = str;
        nativeCrash.uid = i2;
        nativeCrash.pid = i3;
        CriticalEventProto criticalEventProto = new CriticalEventProto();
        criticalEventProto.setNativeCrash(nativeCrash);
        log(criticalEventProto);
    }

    public final void log(CriticalEventProto criticalEventProto) {
        criticalEventProto.timestampMs = getWallTimeMillis();
        appendAndSave(criticalEventProto);
    }

    @VisibleForTesting
    public void appendAndSave(CriticalEventProto criticalEventProto) {
        this.mEvents.append(criticalEventProto);
        saveLogToFile();
    }

    public String logLinesForSystemServerTraceFile() {
        return logLinesForTraceFile(3, "AID_SYSTEM", 1000);
    }

    public String logLinesForTraceFile(int i, String str, int i2) {
        CriticalEventLogProto outputLogProto = getOutputLogProto(i, str, i2);
        return "--- CriticalEventLog ---\n" + MessageNanoPrinter.print(outputLogProto) + '\n';
    }

    @VisibleForTesting
    public CriticalEventLogProto getOutputLogProto(int i, String str, int i2) {
        CriticalEventLogProto criticalEventLogProto = new CriticalEventLogProto();
        criticalEventLogProto.timestampMs = getWallTimeMillis();
        criticalEventLogProto.windowMs = this.mWindowMs;
        criticalEventLogProto.capacity = this.mEvents.capacity();
        CriticalEventProto[] recentEventsWithMinTimestamp = recentEventsWithMinTimestamp(criticalEventLogProto.timestampMs - this.mWindowMs);
        LogSanitizer logSanitizer = new LogSanitizer(i, str, i2);
        for (int i3 = 0; i3 < recentEventsWithMinTimestamp.length; i3++) {
            recentEventsWithMinTimestamp[i3] = logSanitizer.process(recentEventsWithMinTimestamp[i3]);
        }
        criticalEventLogProto.events = recentEventsWithMinTimestamp;
        return criticalEventLogProto;
    }

    public final CriticalEventProto[] recentEventsWithMinTimestamp(long j) {
        CriticalEventProto[] array = this.mEvents.toArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i].timestampMs >= j) {
                return (CriticalEventProto[]) Arrays.copyOfRange(array, i, array.length);
            }
        }
        return new CriticalEventProto[0];
    }

    public final void saveLogToFile() {
        if (this.mLoadAndSaveImmediately) {
            saveLogToFileNow();
        } else if (this.mHandler.hasCallbacks(this.mSaveRunnable) || this.mHandler.postDelayed(this.mSaveRunnable, saveDelayMs())) {
        } else {
            Slog.w(TAG, "Error scheduling save");
        }
    }

    @VisibleForTesting
    public long saveDelayMs() {
        return Math.max(0L, (this.mLastSaveAttemptMs + this.mMinTimeBetweenSavesMs) - getWallTimeMillis());
    }

    @VisibleForTesting
    public void saveLogToFileNow() {
        this.mLastSaveAttemptMs = getWallTimeMillis();
        File parentFile = this.mLogFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdir()) {
            String str = TAG;
            Slog.e(str, "Error creating log directory: " + parentFile.getPath());
            return;
        }
        if (!this.mLogFile.exists()) {
            try {
                this.mLogFile.createNewFile();
            } catch (IOException e) {
                Slog.e(TAG, "Error creating log file", e);
                return;
            }
        }
        CriticalEventLogStorageProto criticalEventLogStorageProto = new CriticalEventLogStorageProto();
        criticalEventLogStorageProto.events = this.mEvents.toArray();
        byte[] byteArray = CriticalEventLogStorageProto.toByteArray(criticalEventLogStorageProto);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.mLogFile, false);
            fileOutputStream.write(byteArray);
            fileOutputStream.close();
        } catch (IOException e2) {
            Slog.e(TAG, "Error saving log to disk.", e2);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ThreadSafeRingBuffer<T> {
        public final RingBuffer<T> mBuffer;
        public final int mCapacity;

        public ThreadSafeRingBuffer(Class<T> cls, int i) {
            this.mCapacity = i;
            this.mBuffer = new RingBuffer<>(cls, i);
        }

        public synchronized void append(T t) {
            this.mBuffer.append(t);
        }

        public synchronized T[] toArray() {
            return (T[]) this.mBuffer.toArray();
        }

        public int capacity() {
            return this.mCapacity;
        }
    }

    /* loaded from: classes.dex */
    public static class LogLoader implements ILogLoader {
        @Override // com.android.server.criticalevents.CriticalEventLog.ILogLoader
        public void load(File file, ThreadSafeRingBuffer<CriticalEventProto> threadSafeRingBuffer) {
            for (CriticalEventProto criticalEventProto : loadLogFromFile(file).events) {
                threadSafeRingBuffer.append(criticalEventProto);
            }
        }

        public static CriticalEventLogStorageProto loadLogFromFile(File file) {
            if (!file.exists()) {
                Slog.i(CriticalEventLog.TAG, "No log found, returning empty log proto.");
                return new CriticalEventLogStorageProto();
            }
            try {
                return CriticalEventLogStorageProto.parseFrom(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                Slog.e(CriticalEventLog.TAG, "Error reading log from disk.", e);
                return new CriticalEventLogStorageProto();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class LogSanitizer {
        public int mTraceProcessClassEnum;
        public String mTraceProcessName;
        public int mTraceUid;

        public LogSanitizer(int i, String str, int i2) {
            this.mTraceProcessClassEnum = i;
            this.mTraceProcessName = str;
            this.mTraceUid = i2;
        }

        public CriticalEventProto process(CriticalEventProto criticalEventProto) {
            if (criticalEventProto.hasAnr()) {
                CriticalEventProto.AppNotResponding anr = criticalEventProto.getAnr();
                if (shouldSanitize(anr.processClass, anr.process, anr.uid)) {
                    return sanitizeAnr(criticalEventProto);
                }
            } else if (criticalEventProto.hasJavaCrash()) {
                CriticalEventProto.JavaCrash javaCrash = criticalEventProto.getJavaCrash();
                if (shouldSanitize(javaCrash.processClass, javaCrash.process, javaCrash.uid)) {
                    return sanitizeJavaCrash(criticalEventProto);
                }
            } else if (criticalEventProto.hasNativeCrash()) {
                CriticalEventProto.NativeCrash nativeCrash = criticalEventProto.getNativeCrash();
                if (shouldSanitize(nativeCrash.processClass, nativeCrash.process, nativeCrash.uid)) {
                    return sanitizeNativeCrash(criticalEventProto);
                }
            }
            return criticalEventProto;
        }

        public final boolean shouldSanitize(int i, String str, int i2) {
            return i == 1 && this.mTraceProcessClassEnum == 1 && !(str != null && str.equals(this.mTraceProcessName) && this.mTraceUid == i2);
        }

        public static CriticalEventProto sanitizeAnr(CriticalEventProto criticalEventProto) {
            CriticalEventProto.AppNotResponding appNotResponding = new CriticalEventProto.AppNotResponding();
            appNotResponding.processClass = criticalEventProto.getAnr().processClass;
            appNotResponding.uid = criticalEventProto.getAnr().uid;
            appNotResponding.pid = criticalEventProto.getAnr().pid;
            CriticalEventProto sanitizeCriticalEventProto = sanitizeCriticalEventProto(criticalEventProto);
            sanitizeCriticalEventProto.setAnr(appNotResponding);
            return sanitizeCriticalEventProto;
        }

        public static CriticalEventProto sanitizeJavaCrash(CriticalEventProto criticalEventProto) {
            CriticalEventProto.JavaCrash javaCrash = new CriticalEventProto.JavaCrash();
            javaCrash.processClass = criticalEventProto.getJavaCrash().processClass;
            javaCrash.uid = criticalEventProto.getJavaCrash().uid;
            javaCrash.pid = criticalEventProto.getJavaCrash().pid;
            CriticalEventProto sanitizeCriticalEventProto = sanitizeCriticalEventProto(criticalEventProto);
            sanitizeCriticalEventProto.setJavaCrash(javaCrash);
            return sanitizeCriticalEventProto;
        }

        public static CriticalEventProto sanitizeNativeCrash(CriticalEventProto criticalEventProto) {
            CriticalEventProto.NativeCrash nativeCrash = new CriticalEventProto.NativeCrash();
            nativeCrash.processClass = criticalEventProto.getNativeCrash().processClass;
            nativeCrash.uid = criticalEventProto.getNativeCrash().uid;
            nativeCrash.pid = criticalEventProto.getNativeCrash().pid;
            CriticalEventProto sanitizeCriticalEventProto = sanitizeCriticalEventProto(criticalEventProto);
            sanitizeCriticalEventProto.setNativeCrash(nativeCrash);
            return sanitizeCriticalEventProto;
        }

        public static CriticalEventProto sanitizeCriticalEventProto(CriticalEventProto criticalEventProto) {
            CriticalEventProto criticalEventProto2 = new CriticalEventProto();
            criticalEventProto2.timestampMs = criticalEventProto.timestampMs;
            return criticalEventProto2;
        }
    }
}
