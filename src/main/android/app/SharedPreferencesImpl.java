package android.app;

import android.app.SharedPreferencesImpl;
import android.compat.Compatibility;
import android.content.SharedPreferences;
import android.p008os.FileUtils;
import android.p008os.Looper;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStat;
import android.system.StructTimespec;
import android.util.Log;
import com.android.internal.util.ExponentiallyBucketedHistogram;
import com.android.internal.util.XmlUtils;
import dalvik.system.BlockGuard;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SharedPreferencesImpl implements SharedPreferences {
    private static final long CALLBACK_ON_CLEAR_CHANGE = 119147584;
    private static final Object CONTENT = new Object();
    private static final boolean DEBUG = false;
    private static final long MAX_FSYNC_DURATION_MILLIS = 256;
    private static final String TAG = "SharedPreferencesImpl";
    private final File mBackupFile;
    private long mCurrentMemoryStateGeneration;
    private long mDiskStateGeneration;
    private final File mFile;
    private boolean mLoaded;
    private final int mMode;
    private long mStatSize;
    private StructTimespec mStatTimestamp;
    private final Object mLock = new Object();
    private final Object mWritingToDiskLock = new Object();
    private int mDiskWritesInFlight = 0;
    private final WeakHashMap<SharedPreferences.OnSharedPreferenceChangeListener, Object> mListeners = new WeakHashMap<>();
    private final ExponentiallyBucketedHistogram mSyncTimes = new ExponentiallyBucketedHistogram(16);
    private int mNumSync = 0;
    private Map<String, Object> mMap = null;
    private Throwable mThrowable = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SharedPreferencesImpl(File file, int mode) {
        this.mLoaded = false;
        this.mFile = file;
        this.mBackupFile = makeBackupFile(file);
        this.mMode = mode;
        this.mLoaded = false;
        startLoadFromDisk();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.app.SharedPreferencesImpl$1] */
    private void startLoadFromDisk() {
        synchronized (this.mLock) {
            this.mLoaded = false;
        }
        new Thread("SharedPreferencesImpl-load") { // from class: android.app.SharedPreferencesImpl.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                SharedPreferencesImpl.this.loadFromDisk();
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadFromDisk() {
        StructStat stat;
        Map<String, Object> map;
        synchronized (this.mLock) {
            if (this.mLoaded) {
                return;
            }
            if (this.mBackupFile.exists()) {
                this.mFile.delete();
                this.mBackupFile.renameTo(this.mFile);
            }
            if (this.mFile.exists() && !this.mFile.canRead()) {
                Log.m104w(TAG, "Attempt to read preferences file " + this.mFile + " without permission");
            }
            Map<String, Object> map2 = null;
            StructStat stat2 = null;
            try {
                stat2 = Os.stat(this.mFile.getPath());
                if (this.mFile.canRead()) {
                    BufferedInputStream str = null;
                    try {
                        str = new BufferedInputStream(new FileInputStream(this.mFile), 16384);
                        map2 = XmlUtils.readMapXml(str);
                    } catch (Exception e) {
                        Log.m103w(TAG, "Cannot read " + this.mFile.getAbsolutePath(), e);
                    }
                    IoUtils.closeQuietly(str);
                }
            } catch (ErrnoException e2) {
            } catch (Throwable th) {
                thrown = th;
                stat = stat2;
                map = map2;
            }
            thrown = null;
            stat = stat2;
            map = map2;
            synchronized (this.mLock) {
                this.mLoaded = true;
                this.mThrowable = thrown;
                if (thrown == null) {
                    if (map != null) {
                        this.mMap = map;
                        this.mStatTimestamp = stat.st_mtim;
                        this.mStatSize = stat.st_size;
                    } else {
                        this.mMap = new HashMap();
                    }
                }
                this.mLock.notifyAll();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static File makeBackupFile(File prefsFile) {
        return new File(prefsFile.getPath() + ".bak");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startReloadIfChangedUnexpectedly() {
        synchronized (this.mLock) {
            if (hasFileChangedUnexpectedly()) {
                startLoadFromDisk();
            }
        }
    }

    private boolean hasFileChangedUnexpectedly() {
        synchronized (this.mLock) {
            boolean z = false;
            if (this.mDiskWritesInFlight > 0) {
                return false;
            }
            try {
                BlockGuard.getThreadPolicy().onReadFromDisk();
                StructStat stat = Os.stat(this.mFile.getPath());
                synchronized (this.mLock) {
                    if (!stat.st_mtim.equals(this.mStatTimestamp) || this.mStatSize != stat.st_size) {
                        z = true;
                    }
                }
                return z;
            } catch (ErrnoException e) {
                return true;
            }
        }
    }

    @Override // android.content.SharedPreferences
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (this.mLock) {
            this.mListeners.put(listener, CONTENT);
        }
    }

    @Override // android.content.SharedPreferences
    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (this.mLock) {
            this.mListeners.remove(listener);
        }
    }

    private void awaitLoadedLocked() {
        if (!this.mLoaded) {
            BlockGuard.getThreadPolicy().onReadFromDisk();
        }
        while (!this.mLoaded) {
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
            }
        }
        if (this.mThrowable != null) {
            throw new IllegalStateException(this.mThrowable);
        }
    }

    @Override // android.content.SharedPreferences
    public Map<String, ?> getAll() {
        HashMap hashMap;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            hashMap = new HashMap(this.mMap);
        }
        return hashMap;
    }

    @Override // android.content.SharedPreferences
    public String getString(String key, String defValue) {
        String str;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            String v = (String) this.mMap.get(key);
            str = v != null ? v : defValue;
        }
        return str;
    }

    @Override // android.content.SharedPreferences
    public Set<String> getStringSet(String key, Set<String> defValues) {
        Set<String> set;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            Set<String> v = (Set) this.mMap.get(key);
            set = v != null ? v : defValues;
        }
        return set;
    }

    @Override // android.content.SharedPreferences
    public int getInt(String key, int defValue) {
        int intValue;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            Integer v = (Integer) this.mMap.get(key);
            intValue = v != null ? v.intValue() : defValue;
        }
        return intValue;
    }

    @Override // android.content.SharedPreferences
    public long getLong(String key, long defValue) {
        long longValue;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            Long v = (Long) this.mMap.get(key);
            longValue = v != null ? v.longValue() : defValue;
        }
        return longValue;
    }

    @Override // android.content.SharedPreferences
    public float getFloat(String key, float defValue) {
        float floatValue;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            Float v = (Float) this.mMap.get(key);
            floatValue = v != null ? v.floatValue() : defValue;
        }
        return floatValue;
    }

    @Override // android.content.SharedPreferences
    public boolean getBoolean(String key, boolean defValue) {
        boolean booleanValue;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            Boolean v = (Boolean) this.mMap.get(key);
            booleanValue = v != null ? v.booleanValue() : defValue;
        }
        return booleanValue;
    }

    @Override // android.content.SharedPreferences
    public boolean contains(String key) {
        boolean containsKey;
        synchronized (this.mLock) {
            awaitLoadedLocked();
            containsKey = this.mMap.containsKey(key);
        }
        return containsKey;
    }

    @Override // android.content.SharedPreferences
    public SharedPreferences.Editor edit() {
        synchronized (this.mLock) {
            awaitLoadedLocked();
        }
        return new EditorImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MemoryCommitResult {
        final boolean keysCleared;
        final List<String> keysModified;
        final Set<SharedPreferences.OnSharedPreferenceChangeListener> listeners;
        final Map<String, Object> mapToWriteToDisk;
        final long memoryStateGeneration;
        boolean wasWritten;
        volatile boolean writeToDiskResult;
        final CountDownLatch writtenToDiskLatch;

        private MemoryCommitResult(long memoryStateGeneration, boolean keysCleared, List<String> keysModified, Set<SharedPreferences.OnSharedPreferenceChangeListener> listeners, Map<String, Object> mapToWriteToDisk) {
            this.writtenToDiskLatch = new CountDownLatch(1);
            this.writeToDiskResult = false;
            this.wasWritten = false;
            this.memoryStateGeneration = memoryStateGeneration;
            this.keysCleared = keysCleared;
            this.keysModified = keysModified;
            this.listeners = listeners;
            this.mapToWriteToDisk = mapToWriteToDisk;
        }

        void setDiskWriteResult(boolean wasWritten, boolean result) {
            this.wasWritten = wasWritten;
            this.writeToDiskResult = result;
            this.writtenToDiskLatch.countDown();
        }
    }

    /* loaded from: classes.dex */
    public final class EditorImpl implements SharedPreferences.Editor {
        private final Object mEditorLock = new Object();
        private final Map<String, Object> mModified = new HashMap();
        private boolean mClear = false;

        public EditorImpl() {
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putString(String key, String value) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, value);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, values == null ? null : new HashSet(values));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putInt(String key, int value) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, Integer.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putLong(String key, long value) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, Long.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putFloat(String key, float value) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, Float.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, Boolean.valueOf(value));
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor remove(String key) {
            synchronized (this.mEditorLock) {
                this.mModified.put(key, this);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor clear() {
            synchronized (this.mEditorLock) {
                this.mClear = true;
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public void apply() {
            final long startTime = System.currentTimeMillis();
            final MemoryCommitResult mcr = commitToMemory();
            final Runnable awaitCommit = new Runnable() { // from class: android.app.SharedPreferencesImpl.EditorImpl.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        mcr.writtenToDiskLatch.await();
                    } catch (InterruptedException e) {
                    }
                }
            };
            QueuedWork.addFinisher(awaitCommit);
            Runnable postWriteRunnable = new Runnable() { // from class: android.app.SharedPreferencesImpl.EditorImpl.2
                @Override // java.lang.Runnable
                public void run() {
                    awaitCommit.run();
                    QueuedWork.removeFinisher(awaitCommit);
                }
            };
            SharedPreferencesImpl.this.enqueueDiskWrite(mcr, postWriteRunnable);
            lambda$notifyListeners$0(mcr);
        }

        /* JADX WARN: Removed duplicated region for block: B:44:0x00b9 A[Catch: all -> 0x00e5, TryCatch #1 {, blocks: (B:4:0x000a, B:6:0x0012, B:7:0x0022, B:13:0x0045, B:14:0x005b, B:15:0x005d, B:51:0x00d8, B:17:0x005f, B:19:0x0063, B:21:0x0069, B:22:0x006d, B:23:0x0070, B:24:0x007a, B:26:0x0080, B:30:0x0095, B:32:0x009b, B:34:0x00a1, B:37:0x00a8, B:44:0x00b9, B:38:0x00ac, B:41:0x00b3, B:46:0x00bd, B:48:0x00c4, B:49:0x00d0, B:50:0x00d7), top: B:60:0x000a }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private MemoryCommitResult commitToMemory() {
            Map<String, Object> mapToWriteToDisk;
            long memoryStateGeneration;
            Object existingValue;
            boolean keysCleared = false;
            List<String> keysModified = null;
            Set<SharedPreferences.OnSharedPreferenceChangeListener> listeners = null;
            synchronized (SharedPreferencesImpl.this.mLock) {
                if (SharedPreferencesImpl.this.mDiskWritesInFlight > 0) {
                    SharedPreferencesImpl.this.mMap = new HashMap(SharedPreferencesImpl.this.mMap);
                }
                mapToWriteToDisk = SharedPreferencesImpl.this.mMap;
                boolean z = true;
                SharedPreferencesImpl.this.mDiskWritesInFlight++;
                if (SharedPreferencesImpl.this.mListeners.size() <= 0) {
                    z = false;
                }
                boolean hasListeners = z;
                if (hasListeners) {
                    keysModified = new ArrayList<>();
                    listeners = new HashSet<>(SharedPreferencesImpl.this.mListeners.keySet());
                }
                synchronized (this.mEditorLock) {
                    boolean changesMade = false;
                    if (this.mClear) {
                        if (!mapToWriteToDisk.isEmpty()) {
                            changesMade = true;
                            mapToWriteToDisk.clear();
                        }
                        keysCleared = true;
                        this.mClear = false;
                    }
                    for (Map.Entry<String, Object> e : this.mModified.entrySet()) {
                        String k = e.getKey();
                        Object v = e.getValue();
                        if (v != this && v != null) {
                            if (!mapToWriteToDisk.containsKey(k) || (existingValue = mapToWriteToDisk.get(k)) == null || !existingValue.equals(v)) {
                                mapToWriteToDisk.put(k, v);
                                changesMade = true;
                                if (hasListeners) {
                                    keysModified.add(k);
                                }
                            }
                        }
                        if (mapToWriteToDisk.containsKey(k)) {
                            mapToWriteToDisk.remove(k);
                            changesMade = true;
                            if (hasListeners) {
                            }
                        }
                    }
                    this.mModified.clear();
                    if (changesMade) {
                        SharedPreferencesImpl.this.mCurrentMemoryStateGeneration++;
                    }
                    memoryStateGeneration = SharedPreferencesImpl.this.mCurrentMemoryStateGeneration;
                }
            }
            return new MemoryCommitResult(memoryStateGeneration, keysCleared, keysModified, listeners, mapToWriteToDisk);
        }

        @Override // android.content.SharedPreferences.Editor
        public boolean commit() {
            MemoryCommitResult mcr = commitToMemory();
            SharedPreferencesImpl.this.enqueueDiskWrite(mcr, null);
            try {
                mcr.writtenToDiskLatch.await();
                lambda$notifyListeners$0(mcr);
                return mcr.writeToDiskResult;
            } catch (InterruptedException e) {
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: notifyListeners */
        public void lambda$notifyListeners$0(final MemoryCommitResult mcr) {
            if (mcr.listeners != null) {
                if (mcr.keysModified == null && !mcr.keysCleared) {
                    return;
                }
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    if (mcr.keysCleared && Compatibility.isChangeEnabled((long) SharedPreferencesImpl.CALLBACK_ON_CLEAR_CHANGE)) {
                        for (SharedPreferences.OnSharedPreferenceChangeListener listener : mcr.listeners) {
                            if (listener != null) {
                                listener.onSharedPreferenceChanged(SharedPreferencesImpl.this, null);
                            }
                        }
                    }
                    for (int i = mcr.keysModified.size() - 1; i >= 0; i--) {
                        String key = mcr.keysModified.get(i);
                        for (SharedPreferences.OnSharedPreferenceChangeListener listener2 : mcr.listeners) {
                            if (listener2 != null) {
                                listener2.onSharedPreferenceChanged(SharedPreferencesImpl.this, key);
                            }
                        }
                    }
                    return;
                }
                ActivityThread.sMainThreadHandler.post(new Runnable() { // from class: android.app.SharedPreferencesImpl$EditorImpl$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SharedPreferencesImpl.EditorImpl.this.lambda$notifyListeners$0(mcr);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enqueueDiskWrite(final MemoryCommitResult mcr, final Runnable postWriteRunnable) {
        boolean wasEmpty;
        final boolean isFromSyncCommit = postWriteRunnable == null;
        Runnable writeToDiskRunnable = new Runnable() { // from class: android.app.SharedPreferencesImpl.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (SharedPreferencesImpl.this.mWritingToDiskLock) {
                    SharedPreferencesImpl.this.writeToFile(mcr, isFromSyncCommit);
                }
                synchronized (SharedPreferencesImpl.this.mLock) {
                    SharedPreferencesImpl sharedPreferencesImpl = SharedPreferencesImpl.this;
                    sharedPreferencesImpl.mDiskWritesInFlight--;
                }
                Runnable runnable = postWriteRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        if (isFromSyncCommit) {
            synchronized (this.mLock) {
                wasEmpty = this.mDiskWritesInFlight == 1;
            }
            if (wasEmpty) {
                writeToDiskRunnable.run();
                return;
            }
        }
        QueuedWork.queue(writeToDiskRunnable, isFromSyncCommit ? false : true);
    }

    private static FileOutputStream createFileOutputStream(File file) {
        try {
            FileOutputStream str = new FileOutputStream(file);
            return str;
        } catch (FileNotFoundException e) {
            File parent = file.getParentFile();
            if (!parent.mkdir()) {
                Log.m110e(TAG, "Couldn't create directory for SharedPreferences file " + file);
                return null;
            }
            FileUtils.setPermissions(parent.getPath(), 505, -1, -1);
            try {
                FileOutputStream str2 = new FileOutputStream(file);
                return str2;
            } catch (FileNotFoundException e2) {
                Log.m109e(TAG, "Couldn't create SharedPreferences file " + file, e2);
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeToFile(MemoryCommitResult mcr, boolean isFromSyncCommit) {
        boolean fileExists = this.mFile.exists();
        if (fileExists) {
            boolean needsWrite = false;
            long j = this.mDiskStateGeneration;
            long existsTime = mcr.memoryStateGeneration;
            if (j < existsTime) {
                if (isFromSyncCommit) {
                    needsWrite = true;
                } else {
                    synchronized (this.mLock) {
                        try {
                            try {
                                if (this.mCurrentMemoryStateGeneration == mcr.memoryStateGeneration) {
                                    needsWrite = true;
                                }
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            throw th;
                        }
                    }
                }
            }
            if (!needsWrite) {
                mcr.setDiskWriteResult(false, true);
                return;
            }
            boolean backupFileExists = this.mBackupFile.exists();
            if (!backupFileExists) {
                if (!this.mFile.renameTo(this.mBackupFile)) {
                    Log.m110e(TAG, "Couldn't rename file " + this.mFile + " to backup file " + this.mBackupFile);
                    mcr.setDiskWriteResult(false, false);
                    return;
                }
            } else {
                this.mFile.delete();
            }
        }
        try {
            FileOutputStream str = createFileOutputStream(this.mFile);
            if (str == null) {
                mcr.setDiskWriteResult(false, false);
                return;
            }
            XmlUtils.writeMapXml(mcr.mapToWriteToDisk, str);
            long writeTime = System.currentTimeMillis();
            FileUtils.sync(str);
            long fsyncTime = System.currentTimeMillis();
            str.close();
            ContextImpl.setFilePermissionsFromMode(this.mFile.getPath(), this.mMode, 0);
            try {
                StructStat stat = Os.stat(this.mFile.getPath());
                synchronized (this.mLock) {
                    this.mStatTimestamp = stat.st_mtim;
                    this.mStatSize = stat.st_size;
                }
            } catch (ErrnoException e) {
            }
            this.mBackupFile.delete();
            this.mDiskStateGeneration = mcr.memoryStateGeneration;
            mcr.setDiskWriteResult(true, true);
            long fsyncDuration = fsyncTime - writeTime;
            this.mSyncTimes.add((int) fsyncDuration);
            int i = this.mNumSync + 1;
            this.mNumSync = i;
            if (i % 1024 == 0 || fsyncDuration > 256) {
                this.mSyncTimes.log(TAG, "Time required to fsync " + this.mFile + ": ");
            }
        } catch (IOException e2) {
            Log.m103w(TAG, "writeToFile: Got exception:", e2);
            if (this.mFile.exists() && !this.mFile.delete()) {
                Log.m110e(TAG, "Couldn't clean up partially-written file " + this.mFile);
            }
            mcr.setDiskWriteResult(false, false);
        } catch (XmlPullParserException e3) {
            Log.m103w(TAG, "writeToFile: Got exception:", e3);
            if (this.mFile.exists()) {
                Log.m110e(TAG, "Couldn't clean up partially-written file " + this.mFile);
            }
            mcr.setDiskWriteResult(false, false);
        }
    }
}
