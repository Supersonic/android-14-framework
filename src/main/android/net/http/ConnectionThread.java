package android.net.http;

import android.content.Context;
import android.net.http.RequestQueue;
import android.os.Process;
import android.os.SystemClock;
import org.apache.http.HttpHost;
/* loaded from: classes.dex */
class ConnectionThread extends Thread {
    static final int WAIT_TICK = 1000;
    static final int WAIT_TIMEOUT = 5000;
    Connection mConnection;
    private RequestQueue.ConnectionManager mConnectionManager;
    private Context mContext;
    long mCurrentThreadTime;
    private int mId;
    private RequestFeeder mRequestFeeder;
    private volatile boolean mRunning = true;
    long mTotalThreadTime;
    private boolean mWaiting;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionThread(Context context, int id, RequestQueue.ConnectionManager connectionManager, RequestFeeder requestFeeder) {
        this.mContext = context;
        setName(HttpHost.DEFAULT_SCHEME_NAME + id);
        this.mId = id;
        this.mConnectionManager = connectionManager;
        this.mRequestFeeder = requestFeeder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestStop() {
        synchronized (this.mRequestFeeder) {
            this.mRunning = false;
            this.mRequestFeeder.notify();
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Process.setThreadPriority(1);
        this.mCurrentThreadTime = 0L;
        this.mTotalThreadTime = 0L;
        while (this.mRunning) {
            if (this.mCurrentThreadTime == -1) {
                this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
            }
            Request request = this.mRequestFeeder.getRequest();
            if (request == null) {
                synchronized (this.mRequestFeeder) {
                    this.mWaiting = true;
                    try {
                        this.mRequestFeeder.wait();
                    } catch (InterruptedException e) {
                    }
                    this.mWaiting = false;
                    if (this.mCurrentThreadTime != 0) {
                        this.mCurrentThreadTime = SystemClock.currentThreadTimeMillis();
                    }
                }
            } else {
                Connection connection = this.mConnectionManager.getConnection(this.mContext, request.mHost);
                this.mConnection = connection;
                connection.processRequests(request);
                if (this.mConnection.getCanPersist()) {
                    if (!this.mConnectionManager.recycleConnection(this.mConnection)) {
                        this.mConnection.closeConnection();
                    }
                } else {
                    this.mConnection.closeConnection();
                }
                this.mConnection = null;
                if (this.mCurrentThreadTime > 0) {
                    long start = this.mCurrentThreadTime;
                    long currentThreadTimeMillis = SystemClock.currentThreadTimeMillis();
                    this.mCurrentThreadTime = currentThreadTimeMillis;
                    this.mTotalThreadTime += currentThreadTimeMillis - start;
                }
            }
        }
    }

    @Override // java.lang.Thread
    public synchronized String toString() {
        String con;
        String active;
        Connection connection = this.mConnection;
        con = connection == null ? "" : connection.toString();
        active = this.mWaiting ? "w" : "a";
        return "cid " + this.mId + " " + active + " " + con;
    }
}
