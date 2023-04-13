package com.android.server.content;

import android.accounts.Account;
import android.app.job.JobParameters;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IntPair;
import com.android.server.IoThread;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.content.SyncManager;
import com.android.server.content.SyncStorageEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class SyncLogger {
    public static SyncLogger sInstance;

    public void dumpAll(PrintWriter printWriter) {
    }

    public boolean enabled() {
        return false;
    }

    public String jobParametersToString(JobParameters jobParameters) {
        return "";
    }

    public void log(Object... objArr) {
    }

    public void purgeOldLogs() {
    }

    public static synchronized SyncLogger getInstance() {
        SyncLogger syncLogger;
        synchronized (SyncLogger.class) {
            if (sInstance == null) {
                String str = SystemProperties.get("debug.synclog");
                if ((Build.IS_DEBUGGABLE || "1".equals(str) || Log.isLoggable("SyncLogger", 2)) && !"0".equals(str)) {
                    sInstance = new RotatingFileLogger();
                } else {
                    sInstance = new SyncLogger();
                }
            }
            syncLogger = sInstance;
        }
        return syncLogger;
    }

    /* loaded from: classes.dex */
    public static class RotatingFileLogger extends SyncLogger {
        @GuardedBy({"mLock"})
        public long mCurrentLogFileDayTimestamp;
        @GuardedBy({"mLock"})
        public boolean mErrorShown;
        @GuardedBy({"mLock"})
        public Writer mLogWriter;
        public static final SimpleDateFormat sTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        public static final SimpleDateFormat sFilenameDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        public static final boolean DO_LOGCAT = Log.isLoggable("SyncLogger", 3);
        public final Object mLock = new Object();
        public final long mKeepAgeMs = TimeUnit.DAYS.toMillis(7);
        @GuardedBy({"mLock"})
        public final Date mCachedDate = new Date();
        @GuardedBy({"mLock"})
        public final StringBuilder mStringBuilder = new StringBuilder();
        public final File mLogPath = new File(Environment.getDataSystemDirectory(), "syncmanager-log");
        public final MyHandler mHandler = new MyHandler(IoThread.get().getLooper());

        @Override // com.android.server.content.SyncLogger
        public boolean enabled() {
            return true;
        }

        public final void handleException(String str, Exception exc) {
            if (this.mErrorShown) {
                return;
            }
            Slog.e("SyncLogger", str, exc);
            this.mErrorShown = true;
        }

        @Override // com.android.server.content.SyncLogger
        public void log(Object... objArr) {
            if (objArr == null) {
                return;
            }
            this.mHandler.log(System.currentTimeMillis(), objArr);
        }

        public void logInner(long j, Object[] objArr) {
            synchronized (this.mLock) {
                openLogLocked(j);
                if (this.mLogWriter == null) {
                    return;
                }
                this.mStringBuilder.setLength(0);
                this.mCachedDate.setTime(j);
                this.mStringBuilder.append(sTimestampFormat.format(this.mCachedDate));
                this.mStringBuilder.append(' ');
                this.mStringBuilder.append(Process.myTid());
                this.mStringBuilder.append(' ');
                int length = this.mStringBuilder.length();
                for (Object obj : objArr) {
                    this.mStringBuilder.append(obj);
                }
                this.mStringBuilder.append('\n');
                try {
                    this.mLogWriter.append((CharSequence) this.mStringBuilder);
                    this.mLogWriter.flush();
                    if (DO_LOGCAT) {
                        Log.d("SyncLogger", this.mStringBuilder.substring(length));
                    }
                } catch (IOException e) {
                    handleException("Failed to write log", e);
                }
            }
        }

        @GuardedBy({"mLock"})
        public final void openLogLocked(long j) {
            long j2 = j % BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
            if (this.mLogWriter == null || j2 != this.mCurrentLogFileDayTimestamp) {
                closeCurrentLogLocked();
                this.mCurrentLogFileDayTimestamp = j2;
                this.mCachedDate.setTime(j);
                File file = new File(this.mLogPath, "synclog-" + sFilenameDateFormat.format(this.mCachedDate) + ".log");
                file.getParentFile().mkdirs();
                try {
                    this.mLogWriter = new FileWriter(file, true);
                } catch (IOException e) {
                    handleException("Failed to open log file: " + file, e);
                }
            }
        }

        @GuardedBy({"mLock"})
        public final void closeCurrentLogLocked() {
            IoUtils.closeQuietly(this.mLogWriter);
            this.mLogWriter = null;
        }

        @Override // com.android.server.content.SyncLogger
        public void purgeOldLogs() {
            synchronized (this.mLock) {
                FileUtils.deleteOlderFiles(this.mLogPath, 1, this.mKeepAgeMs);
            }
        }

        @Override // com.android.server.content.SyncLogger
        public String jobParametersToString(JobParameters jobParameters) {
            return SyncJobService.jobParametersToString(jobParameters);
        }

        @Override // com.android.server.content.SyncLogger
        public void dumpAll(PrintWriter printWriter) {
            synchronized (this.mLock) {
                String[] list = this.mLogPath.list();
                if (list != null && list.length != 0) {
                    Arrays.sort(list);
                    for (String str : list) {
                        dumpFile(printWriter, new File(this.mLogPath, str));
                    }
                }
            }
        }

        public final void dumpFile(PrintWriter printWriter, File file) {
            Slog.w("SyncLogger", "Dumping " + file);
            char[] cArr = new char[32768];
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                while (true) {
                    int read = bufferedReader.read(cArr);
                    if (read < 0) {
                        bufferedReader.close();
                        return;
                    } else if (read > 0) {
                        printWriter.write(cArr, 0, read);
                    }
                }
            } catch (IOException unused) {
            }
        }

        /* loaded from: classes.dex */
        public class MyHandler extends Handler {
            public MyHandler(Looper looper) {
                super(looper);
            }

            public void log(long j, Object[] objArr) {
                obtainMessage(1, IntPair.first(j), IntPair.second(j), objArr).sendToTarget();
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                RotatingFileLogger.this.logInner(IntPair.of(message.arg1, message.arg2), (Object[]) message.obj);
            }
        }
    }

    public static String logSafe(Account account) {
        return account == null ? "[null]" : account.toSafeString();
    }

    public static String logSafe(SyncStorageEngine.EndPoint endPoint) {
        return endPoint == null ? "[null]" : endPoint.toSafeString();
    }

    public static String logSafe(SyncOperation syncOperation) {
        return syncOperation == null ? "[null]" : syncOperation.toSafeString();
    }

    public static String logSafe(SyncManager.ActiveSyncContext activeSyncContext) {
        return activeSyncContext == null ? "[null]" : activeSyncContext.toSafeString();
    }
}
