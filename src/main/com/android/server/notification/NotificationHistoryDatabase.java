package com.android.server.notification;

import android.app.NotificationHistory;
import android.os.Handler;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.notification.NotificationHistoryFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class NotificationHistoryDatabase {
    public static final boolean DEBUG = NotificationManagerService.DBG;
    public final Handler mFileWriteHandler;
    public final File mHistoryDir;
    public final File mVersionFile;
    public final Object mLock = new Object();
    public int mCurrentVersion = 1;
    @VisibleForTesting
    final List<AtomicFile> mHistoryFiles = new ArrayList();
    @VisibleForTesting
    NotificationHistory mBuffer = new NotificationHistory();
    public final WriteBufferRunnable mWriteBufferRunnable = new WriteBufferRunnable();

    public NotificationHistoryDatabase(Handler handler, File file) {
        this.mFileWriteHandler = handler;
        this.mVersionFile = new File(file, "version");
        this.mHistoryDir = new File(file, "history");
    }

    public void init() {
        synchronized (this.mLock) {
            try {
                if (!this.mHistoryDir.exists() && !this.mHistoryDir.mkdir()) {
                    throw new IllegalStateException("could not create history directory");
                }
                this.mVersionFile.createNewFile();
            } catch (Exception e) {
                Slog.e("NotiHistoryDatabase", "could not create needed files", e);
            }
            checkVersionAndBuildLocked();
            indexFilesLocked();
            prune();
        }
    }

    public final void indexFilesLocked() {
        this.mHistoryFiles.clear();
        File[] listFiles = this.mHistoryDir.listFiles();
        if (listFiles == null) {
            return;
        }
        Arrays.sort(listFiles, new Comparator() { // from class: com.android.server.notification.NotificationHistoryDatabase$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$indexFilesLocked$0;
                lambda$indexFilesLocked$0 = NotificationHistoryDatabase.lambda$indexFilesLocked$0((File) obj, (File) obj2);
                return lambda$indexFilesLocked$0;
            }
        });
        for (File file : listFiles) {
            this.mHistoryFiles.add(new AtomicFile(file));
        }
    }

    public static /* synthetic */ int lambda$indexFilesLocked$0(File file, File file2) {
        return Long.compare(safeParseLong(file2.getName()), safeParseLong(file.getName()));
    }

    public final void checkVersionAndBuildLocked() {
        int i;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.mVersionFile));
            i = Integer.parseInt(bufferedReader.readLine());
            bufferedReader.close();
        } catch (IOException | NumberFormatException unused) {
            i = 0;
        }
        if (i == this.mCurrentVersion || !this.mVersionFile.exists()) {
            return;
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.mVersionFile));
            bufferedWriter.write(Integer.toString(this.mCurrentVersion));
            bufferedWriter.write("\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            Slog.e("NotiHistoryDatabase", "Failed to write new version");
            throw new RuntimeException(e);
        }
    }

    public void forceWriteToDisk() {
        this.mFileWriteHandler.post(this.mWriteBufferRunnable);
    }

    public void onPackageRemoved(String str) {
        this.mFileWriteHandler.post(new RemovePackageRunnable(str));
    }

    public void deleteNotificationHistoryItem(String str, long j) {
        this.mFileWriteHandler.post(new RemoveNotificationRunnable(str, j));
    }

    public void deleteConversations(String str, Set<String> set) {
        this.mFileWriteHandler.post(new RemoveConversationRunnable(str, set));
    }

    public void deleteNotificationChannel(String str, String str2) {
        this.mFileWriteHandler.post(new RemoveChannelRunnable(str, str2));
    }

    public void addNotification(NotificationHistory.HistoricalNotification historicalNotification) {
        synchronized (this.mLock) {
            this.mBuffer.addNewNotificationToWrite(historicalNotification);
            if (this.mBuffer.getHistoryCount() == 1) {
                this.mFileWriteHandler.postDelayed(this.mWriteBufferRunnable, 1200000L);
            }
        }
    }

    public NotificationHistory readNotificationHistory() {
        NotificationHistory notificationHistory;
        synchronized (this.mLock) {
            notificationHistory = new NotificationHistory();
            notificationHistory.addNotificationsToWrite(this.mBuffer);
            for (AtomicFile atomicFile : this.mHistoryFiles) {
                try {
                    readLocked(atomicFile, notificationHistory, new NotificationHistoryFilter.Builder().build());
                } catch (Exception e) {
                    Slog.e("NotiHistoryDatabase", "error reading " + atomicFile.getBaseFile().getAbsolutePath(), e);
                }
            }
        }
        return notificationHistory;
    }

    public void disableHistory() {
        synchronized (this.mLock) {
            for (AtomicFile atomicFile : this.mHistoryFiles) {
                atomicFile.delete();
            }
            this.mHistoryDir.delete();
            this.mHistoryFiles.clear();
        }
    }

    public void prune() {
        prune(1, System.currentTimeMillis());
    }

    public void prune(int i, long j) {
        synchronized (this.mLock) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTimeInMillis(j);
            gregorianCalendar.add(5, i * (-1));
            for (int size = this.mHistoryFiles.size() - 1; size >= 0; size--) {
                AtomicFile atomicFile = this.mHistoryFiles.get(size);
                long safeParseLong = safeParseLong(atomicFile.getBaseFile().getName());
                if (DEBUG) {
                    Slog.d("NotiHistoryDatabase", "File " + atomicFile.getBaseFile().getName() + " created on " + safeParseLong);
                }
                if (safeParseLong <= gregorianCalendar.getTimeInMillis()) {
                    deleteFile(atomicFile);
                }
            }
        }
    }

    public void removeFilePathFromHistory(String str) {
        if (str == null) {
            return;
        }
        Iterator<AtomicFile> it = this.mHistoryFiles.iterator();
        while (it.hasNext()) {
            AtomicFile next = it.next();
            if (next != null && str.equals(next.getBaseFile().getAbsolutePath())) {
                it.remove();
                return;
            }
        }
    }

    public final void deleteFile(AtomicFile atomicFile) {
        if (DEBUG) {
            Slog.d("NotiHistoryDatabase", "Removed " + atomicFile.getBaseFile().getName());
        }
        atomicFile.delete();
        removeFilePathFromHistory(atomicFile.getBaseFile().getAbsolutePath());
    }

    public final void writeLocked(AtomicFile atomicFile, NotificationHistory notificationHistory) throws IOException {
        FileOutputStream startWrite = atomicFile.startWrite();
        try {
            NotificationHistoryProtoHelper.write(startWrite, notificationHistory, this.mCurrentVersion);
            atomicFile.finishWrite(startWrite);
            atomicFile.failWrite(null);
        } catch (Throwable th) {
            atomicFile.failWrite(startWrite);
            throw th;
        }
    }

    public static void readLocked(AtomicFile atomicFile, NotificationHistory notificationHistory, NotificationHistoryFilter notificationHistoryFilter) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            try {
                fileInputStream = atomicFile.openRead();
                NotificationHistoryProtoHelper.read(fileInputStream, notificationHistory, notificationHistoryFilter);
            } catch (FileNotFoundException e) {
                Slog.e("NotiHistoryDatabase", "Cannot open " + atomicFile.getBaseFile().getAbsolutePath(), e);
                throw e;
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    public static long safeParseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException unused) {
            return -1L;
        }
    }

    /* loaded from: classes2.dex */
    public final class WriteBufferRunnable implements Runnable {
        public WriteBufferRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            run(new AtomicFile(new File(NotificationHistoryDatabase.this.mHistoryDir, String.valueOf(System.currentTimeMillis()))));
        }

        public void run(AtomicFile atomicFile) {
            synchronized (NotificationHistoryDatabase.this.mLock) {
                if (NotificationHistoryDatabase.DEBUG) {
                    Slog.d("NotiHistoryDatabase", "WriteBufferRunnable " + atomicFile.getBaseFile().getAbsolutePath());
                }
                try {
                    NotificationHistoryDatabase notificationHistoryDatabase = NotificationHistoryDatabase.this;
                    notificationHistoryDatabase.writeLocked(atomicFile, notificationHistoryDatabase.mBuffer);
                    NotificationHistoryDatabase.this.mHistoryFiles.add(0, atomicFile);
                    NotificationHistoryDatabase.this.mBuffer = new NotificationHistory();
                } catch (IOException e) {
                    Slog.e("NotiHistoryDatabase", "Failed to write buffer to disk. not flushing buffer", e);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class RemovePackageRunnable implements Runnable {
        public String mPkg;

        public RemovePackageRunnable(String str) {
            this.mPkg = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (NotificationHistoryDatabase.DEBUG) {
                Slog.d("NotiHistoryDatabase", "RemovePackageRunnable " + this.mPkg);
            }
            synchronized (NotificationHistoryDatabase.this.mLock) {
                NotificationHistoryDatabase.this.mBuffer.removeNotificationsFromWrite(this.mPkg);
                for (AtomicFile atomicFile : NotificationHistoryDatabase.this.mHistoryFiles) {
                    try {
                        NotificationHistory notificationHistory = new NotificationHistory();
                        NotificationHistoryDatabase.readLocked(atomicFile, notificationHistory, new NotificationHistoryFilter.Builder().build());
                        notificationHistory.removeNotificationsFromWrite(this.mPkg);
                        NotificationHistoryDatabase.this.writeLocked(atomicFile, notificationHistory);
                    } catch (Exception e) {
                        Slog.e("NotiHistoryDatabase", "Cannot clean up file on pkg removal " + atomicFile.getBaseFile().getAbsolutePath(), e);
                    }
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class RemoveNotificationRunnable implements Runnable {
        public NotificationHistory mNotificationHistory;
        public String mPkg;
        public long mPostedTime;

        public RemoveNotificationRunnable(String str, long j) {
            this.mPkg = str;
            this.mPostedTime = j;
        }

        @VisibleForTesting
        public void setNotificationHistory(NotificationHistory notificationHistory) {
            this.mNotificationHistory = notificationHistory;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (NotificationHistoryDatabase.DEBUG) {
                Slog.d("NotiHistoryDatabase", "RemoveNotificationRunnable");
            }
            synchronized (NotificationHistoryDatabase.this.mLock) {
                NotificationHistoryDatabase.this.mBuffer.removeNotificationFromWrite(this.mPkg, this.mPostedTime);
                for (AtomicFile atomicFile : NotificationHistoryDatabase.this.mHistoryFiles) {
                    try {
                        NotificationHistory notificationHistory = this.mNotificationHistory;
                        if (notificationHistory == null) {
                            notificationHistory = new NotificationHistory();
                        }
                        NotificationHistoryDatabase.readLocked(atomicFile, notificationHistory, new NotificationHistoryFilter.Builder().build());
                        if (notificationHistory.removeNotificationFromWrite(this.mPkg, this.mPostedTime)) {
                            NotificationHistoryDatabase.this.writeLocked(atomicFile, notificationHistory);
                        }
                    } catch (Exception e) {
                        Slog.e("NotiHistoryDatabase", "Cannot clean up file on notification removal " + atomicFile.getBaseFile().getName(), e);
                    }
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class RemoveConversationRunnable implements Runnable {
        public Set<String> mConversationIds;
        public NotificationHistory mNotificationHistory;
        public String mPkg;

        public RemoveConversationRunnable(String str, Set<String> set) {
            this.mPkg = str;
            this.mConversationIds = set;
        }

        @VisibleForTesting
        public void setNotificationHistory(NotificationHistory notificationHistory) {
            this.mNotificationHistory = notificationHistory;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (NotificationHistoryDatabase.DEBUG) {
                Slog.d("NotiHistoryDatabase", "RemoveConversationRunnable " + this.mPkg + " " + this.mConversationIds);
            }
            synchronized (NotificationHistoryDatabase.this.mLock) {
                NotificationHistoryDatabase.this.mBuffer.removeConversationsFromWrite(this.mPkg, this.mConversationIds);
                for (AtomicFile atomicFile : NotificationHistoryDatabase.this.mHistoryFiles) {
                    try {
                        NotificationHistory notificationHistory = this.mNotificationHistory;
                        if (notificationHistory == null) {
                            notificationHistory = new NotificationHistory();
                        }
                        NotificationHistoryDatabase.readLocked(atomicFile, notificationHistory, new NotificationHistoryFilter.Builder().build());
                        if (notificationHistory.removeConversationsFromWrite(this.mPkg, this.mConversationIds)) {
                            NotificationHistoryDatabase.this.writeLocked(atomicFile, notificationHistory);
                        }
                    } catch (Exception e) {
                        Slog.e("NotiHistoryDatabase", "Cannot clean up file on conversation removal " + atomicFile.getBaseFile().getName(), e);
                    }
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class RemoveChannelRunnable implements Runnable {
        public String mChannelId;
        public NotificationHistory mNotificationHistory;
        public String mPkg;

        public RemoveChannelRunnable(String str, String str2) {
            this.mPkg = str;
            this.mChannelId = str2;
        }

        @VisibleForTesting
        public void setNotificationHistory(NotificationHistory notificationHistory) {
            this.mNotificationHistory = notificationHistory;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (NotificationHistoryDatabase.DEBUG) {
                Slog.d("NotiHistoryDatabase", "RemoveChannelRunnable");
            }
            synchronized (NotificationHistoryDatabase.this.mLock) {
                NotificationHistoryDatabase.this.mBuffer.removeChannelFromWrite(this.mPkg, this.mChannelId);
                for (AtomicFile atomicFile : NotificationHistoryDatabase.this.mHistoryFiles) {
                    try {
                        NotificationHistory notificationHistory = this.mNotificationHistory;
                        if (notificationHistory == null) {
                            notificationHistory = new NotificationHistory();
                        }
                        NotificationHistoryDatabase.readLocked(atomicFile, notificationHistory, new NotificationHistoryFilter.Builder().build());
                        if (notificationHistory.removeChannelFromWrite(this.mPkg, this.mChannelId)) {
                            NotificationHistoryDatabase.this.writeLocked(atomicFile, notificationHistory);
                        }
                    } catch (Exception e) {
                        Slog.e("NotiHistoryDatabase", "Cannot clean up file on channel removal " + atomicFile.getBaseFile().getName(), e);
                    }
                }
            }
        }
    }
}
