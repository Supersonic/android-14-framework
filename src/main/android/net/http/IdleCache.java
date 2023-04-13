package android.net.http;

import android.os.Process;
import android.os.SystemClock;
import org.apache.http.HttpHost;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class IdleCache {
    private static final int CHECK_INTERVAL = 2000;
    private static final int EMPTY_CHECK_MAX = 5;
    private static final int IDLE_CACHE_MAX = 8;
    private static final int TIMEOUT = 6000;
    private Entry[] mEntries = new Entry[IDLE_CACHE_MAX];
    private int mCount = 0;
    private IdleReaper mThread = null;
    private int mCached = 0;
    private int mReused = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Entry {
        Connection mConnection;
        HttpHost mHost;
        long mTimeout;

        Entry() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IdleCache() {
        for (int i = 0; i < IDLE_CACHE_MAX; i++) {
            this.mEntries[i] = new Entry();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x0017, code lost:
        r5.mHost = r9;
        r5.mConnection = r10;
        r5.mTimeout = 6000 + r3;
        r8.mCount++;
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0029, code lost:
        if (r8.mThread != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x002b, code lost:
        r2 = new android.net.http.IdleCache.IdleReaper(r8, null);
        r8.mThread = r2;
        r2.start();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized boolean cacheConnection(HttpHost host, Connection connection) {
        boolean ret;
        ret = false;
        if (this.mCount < IDLE_CACHE_MAX) {
            long time = SystemClock.uptimeMillis();
            int i = 0;
            while (true) {
                if (i >= IDLE_CACHE_MAX) {
                    break;
                }
                Entry entry = this.mEntries[i];
                if (entry.mHost == null) {
                    break;
                }
                i++;
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0019, code lost:
        r0 = r2.mConnection;
        r2.mHost = null;
        r2.mConnection = null;
        r5.mCount--;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized Connection getConnection(HttpHost host) {
        Connection ret;
        ret = null;
        if (this.mCount > 0) {
            int i = 0;
            while (true) {
                if (i < IDLE_CACHE_MAX) {
                    Entry entry = this.mEntries[i];
                    HttpHost eHost = entry.mHost;
                    if (eHost != null && eHost.equals(host)) {
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void clear() {
        for (int i = 0; this.mCount > 0 && i < IDLE_CACHE_MAX; i++) {
            Entry entry = this.mEntries[i];
            if (entry.mHost != null) {
                entry.mHost = null;
                entry.mConnection.closeConnection();
                entry.mConnection = null;
                this.mCount--;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void clearIdle() {
        if (this.mCount > 0) {
            long time = SystemClock.uptimeMillis();
            for (int i = 0; i < IDLE_CACHE_MAX; i++) {
                Entry entry = this.mEntries[i];
                if (entry.mHost != null && time > entry.mTimeout) {
                    entry.mHost = null;
                    entry.mConnection.closeConnection();
                    entry.mConnection = null;
                    this.mCount--;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class IdleReaper extends Thread {
        private IdleReaper() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int check = 0;
            setName("IdleReaper");
            Process.setThreadPriority(10);
            synchronized (IdleCache.this) {
                while (check < 5) {
                    try {
                        IdleCache.this.wait(2000L);
                    } catch (InterruptedException e) {
                    }
                    if (IdleCache.this.mCount == 0) {
                        check++;
                    } else {
                        check = 0;
                        IdleCache.this.clearIdle();
                    }
                }
                IdleCache.this.mThread = null;
            }
        }
    }
}
