package com.android.server.backup.transport;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.EventLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.backup.IBackupTransport;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/* loaded from: classes.dex */
public class TransportConnection {
    @VisibleForTesting
    static final String TAG = "TransportConnection";
    public final Intent mBindIntent;
    public final CloseGuard mCloseGuard;
    public final ServiceConnection mConnection;
    public final Context mContext;
    public final String mCreatorLogString;
    public final String mIdentifier;
    public final Handler mListenerHandler;
    @GuardedBy({"mStateLock"})
    public final Map<TransportConnectionListener, String> mListeners;
    @GuardedBy({"mLogBufferLock"})
    public final List<String> mLogBuffer;
    public final Object mLogBufferLock;
    public final String mPrefixForLog;
    @GuardedBy({"mStateLock"})
    public int mState;
    public final Object mStateLock;
    @GuardedBy({"mStateLock"})
    public volatile BackupTransportClient mTransport;
    public final ComponentName mTransportComponent;
    public final TransportStats mTransportStats;
    public final int mUserId;

    public final int transitionThroughState(int i, int i2, int i3) {
        if (i >= i3 || i3 > i2) {
            return (i < i3 || i3 <= i2) ? 0 : -1;
        }
        return 1;
    }

    public TransportConnection(int i, Context context, TransportStats transportStats, Intent intent, ComponentName componentName, String str, String str2) {
        this(i, context, transportStats, intent, componentName, str, str2, new Handler(Looper.getMainLooper()));
    }

    @VisibleForTesting
    public TransportConnection(int i, Context context, TransportStats transportStats, Intent intent, ComponentName componentName, String str, String str2, Handler handler) {
        this.mStateLock = new Object();
        this.mLogBufferLock = new Object();
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mLogBuffer = new LinkedList();
        this.mListeners = new ArrayMap();
        this.mState = 1;
        this.mUserId = i;
        this.mContext = context;
        this.mTransportStats = transportStats;
        this.mTransportComponent = componentName;
        this.mBindIntent = intent;
        this.mIdentifier = str;
        this.mCreatorLogString = str2;
        this.mListenerHandler = handler;
        this.mConnection = new TransportConnectionMonitor(context, this);
        String replaceFirst = componentName.getShortClassName().replaceFirst(".*\\.", "");
        this.mPrefixForLog = replaceFirst + "#" + str + XmlUtils.STRING_ARRAY_SEPARATOR;
        closeGuard.open("markAsDisposed");
    }

    public ComponentName getTransportComponent() {
        return this.mTransportComponent;
    }

    public void connectAsync(TransportConnectionListener transportConnectionListener, String str) {
        synchronized (this.mStateLock) {
            checkStateIntegrityLocked();
            int i = this.mState;
            if (i == 0) {
                log(5, str, "Async connect: UNUSABLE client");
                notifyListener(transportConnectionListener, null, str);
            } else if (i != 1) {
                if (i == 2) {
                    log(3, str, "Async connect: already connecting, adding listener");
                    this.mListeners.put(transportConnectionListener, str);
                } else if (i == 3) {
                    log(3, str, "Async connect: reusing transport");
                    notifyListener(transportConnectionListener, this.mTransport, str);
                }
            } else if (this.mContext.bindServiceAsUser(this.mBindIntent, this.mConnection, 1, UserHandle.of(this.mUserId))) {
                log(3, str, "Async connect: service bound, connecting");
                setStateLocked(2, null);
                this.mListeners.put(transportConnectionListener, str);
            } else {
                log(6, "Async connect: bindService returned false");
                this.mContext.unbindService(this.mConnection);
                notifyListener(transportConnectionListener, null, str);
            }
        }
    }

    public void unbind(String str) {
        synchronized (this.mStateLock) {
            checkStateIntegrityLocked();
            log(3, str, "Unbind requested (was " + stateToString(this.mState) + ")");
            int i = this.mState;
            if (i == 2) {
                setStateLocked(1, null);
                this.mContext.unbindService(this.mConnection);
                notifyListenersAndClearLocked(null);
            } else if (i == 3) {
                setStateLocked(1, null);
                this.mContext.unbindService(this.mConnection);
            }
        }
    }

    public void markAsDisposed() {
        synchronized (this.mStateLock) {
            Preconditions.checkState(this.mState < 2, "Can't mark as disposed if still bound");
            this.mCloseGuard.close();
        }
    }

    public BackupTransportClient connect(String str) {
        Preconditions.checkState(!Looper.getMainLooper().isCurrentThread(), "Can't call connect() on main thread");
        BackupTransportClient backupTransportClient = this.mTransport;
        if (backupTransportClient != null) {
            log(3, str, "Sync connect: reusing transport");
            return backupTransportClient;
        }
        synchronized (this.mStateLock) {
            if (this.mState == 0) {
                log(5, str, "Sync connect: UNUSABLE client");
                return null;
            }
            final CompletableFuture completableFuture = new CompletableFuture();
            TransportConnectionListener transportConnectionListener = new TransportConnectionListener() { // from class: com.android.server.backup.transport.TransportConnection$$ExternalSyntheticLambda1
                @Override // com.android.server.backup.transport.TransportConnectionListener
                public final void onTransportConnectionResult(BackupTransportClient backupTransportClient2, TransportConnection transportConnection) {
                    completableFuture.complete(backupTransportClient2);
                }
            };
            long elapsedRealtime = SystemClock.elapsedRealtime();
            log(3, str, "Sync connect: calling async");
            connectAsync(transportConnectionListener, str);
            try {
                BackupTransportClient backupTransportClient2 = (BackupTransportClient) completableFuture.get();
                long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
                this.mTransportStats.registerConnectionTime(this.mTransportComponent, elapsedRealtime2);
                log(3, str, String.format(Locale.US, "Connect took %d ms", Long.valueOf(elapsedRealtime2)));
                return backupTransportClient2;
            } catch (InterruptedException | ExecutionException e) {
                String simpleName = e.getClass().getSimpleName();
                log(6, str, simpleName + " while waiting for transport: " + e.getMessage());
                return null;
            }
        }
    }

    public BackupTransportClient connectOrThrow(String str) throws TransportNotAvailableException {
        BackupTransportClient connect = connect(str);
        if (connect != null) {
            return connect;
        }
        log(6, str, "Transport connection failed");
        throw new TransportNotAvailableException();
    }

    public BackupTransportClient getConnectedTransport(String str) throws TransportNotAvailableException {
        BackupTransportClient backupTransportClient = this.mTransport;
        if (backupTransportClient != null) {
            return backupTransportClient;
        }
        log(6, str, "Transport not connected");
        throw new TransportNotAvailableException();
    }

    public String toString() {
        return "TransportClient{" + this.mTransportComponent.flattenToShortString() + "#" + this.mIdentifier + "}";
    }

    public void finalize() throws Throwable {
        synchronized (this.mStateLock) {
            this.mCloseGuard.warnIfOpen();
            if (this.mState >= 2) {
                log(6, "TransportClient.finalize()", "Dangling TransportClient created in [" + this.mCreatorLogString + "] being GC'ed. Left bound, unbinding...");
                try {
                    unbind("TransportClient.finalize()");
                } catch (IllegalStateException unused) {
                }
            }
        }
    }

    public final void onServiceConnected(IBinder iBinder) {
        BackupTransportClient backupTransportClient = new BackupTransportClient(IBackupTransport.Stub.asInterface(iBinder));
        synchronized (this.mStateLock) {
            checkStateIntegrityLocked();
            if (this.mState != 0) {
                log(3, "Transport connected");
                setStateLocked(3, backupTransportClient);
                notifyListenersAndClearLocked(backupTransportClient);
            }
        }
    }

    public final void onServiceDisconnected() {
        synchronized (this.mStateLock) {
            log(6, "Service disconnected: client UNUSABLE");
            if (this.mTransport != null) {
                this.mTransport.onBecomingUnusable();
            }
            setStateLocked(0, null);
            try {
                this.mContext.unbindService(this.mConnection);
            } catch (IllegalArgumentException e) {
                log(5, "Exception trying to unbind onServiceDisconnected(): " + e.getMessage());
            }
        }
    }

    public final void onBindingDied() {
        synchronized (this.mStateLock) {
            checkStateIntegrityLocked();
            log(6, "Binding died: client UNUSABLE");
            if (this.mTransport != null) {
                this.mTransport.onBecomingUnusable();
            }
            int i = this.mState;
            if (i == 1) {
                log(6, "Unexpected state transition IDLE => UNUSABLE");
                setStateLocked(0, null);
            } else if (i == 2) {
                setStateLocked(0, null);
                this.mContext.unbindService(this.mConnection);
                notifyListenersAndClearLocked(null);
            } else if (i == 3) {
                setStateLocked(0, null);
                this.mContext.unbindService(this.mConnection);
            }
        }
    }

    public final void notifyListener(final TransportConnectionListener transportConnectionListener, final BackupTransportClient backupTransportClient, String str) {
        String str2 = backupTransportClient != null ? "BackupTransportClient" : "null";
        log(4, "Notifying [" + str + "] transport = " + str2);
        this.mListenerHandler.post(new Runnable() { // from class: com.android.server.backup.transport.TransportConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TransportConnection.this.lambda$notifyListener$1(transportConnectionListener, backupTransportClient);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyListener$1(TransportConnectionListener transportConnectionListener, BackupTransportClient backupTransportClient) {
        transportConnectionListener.onTransportConnectionResult(backupTransportClient, this);
    }

    @GuardedBy({"mStateLock"})
    public final void notifyListenersAndClearLocked(BackupTransportClient backupTransportClient) {
        for (Map.Entry<TransportConnectionListener, String> entry : this.mListeners.entrySet()) {
            notifyListener(entry.getKey(), backupTransportClient, entry.getValue());
        }
        this.mListeners.clear();
    }

    @GuardedBy({"mStateLock"})
    public final void setStateLocked(int i, BackupTransportClient backupTransportClient) {
        log(2, "State: " + stateToString(this.mState) + " => " + stateToString(i));
        onStateTransition(this.mState, i);
        this.mState = i;
        this.mTransport = backupTransportClient;
    }

    public final void onStateTransition(int i, int i2) {
        String flattenToShortString = this.mTransportComponent.flattenToShortString();
        int transitionThroughState = transitionThroughState(i, i2, 2);
        int transitionThroughState2 = transitionThroughState(i, i2, 3);
        if (transitionThroughState != 0) {
            EventLog.writeEvent(2850, flattenToShortString, Integer.valueOf(transitionThroughState == 1 ? 1 : 0));
        }
        if (transitionThroughState2 != 0) {
            EventLog.writeEvent(2851, flattenToShortString, Integer.valueOf(transitionThroughState2 == 1 ? 1 : 0));
        }
    }

    @GuardedBy({"mStateLock"})
    public final void checkStateIntegrityLocked() {
        int i = this.mState;
        if (i == 0) {
            checkState(this.mListeners.isEmpty(), "Unexpected listeners when state = UNUSABLE");
            checkState(this.mTransport == null, "Transport expected to be null when state = UNUSABLE");
        } else if (i != 1) {
            if (i == 2) {
                checkState(this.mTransport == null, "Transport expected to be null when state = BOUND_AND_CONNECTING");
                return;
            } else if (i == 3) {
                checkState(this.mListeners.isEmpty(), "Unexpected listeners when state = CONNECTED");
                checkState(this.mTransport != null, "Transport expected to be non-null when state = CONNECTED");
                return;
            } else {
                checkState(false, "Unexpected state = " + stateToString(this.mState));
                return;
            }
        }
        checkState(this.mListeners.isEmpty(), "Unexpected listeners when state = IDLE");
        checkState(this.mTransport == null, "Transport expected to be null when state = IDLE");
    }

    public final void checkState(boolean z, String str) {
        if (z) {
            return;
        }
        log(6, str);
    }

    public final String stateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "<UNKNOWN = " + i + ">";
                    }
                    return "CONNECTED";
                }
                return "BOUND_AND_CONNECTING";
            }
            return "IDLE";
        }
        return "UNUSABLE";
    }

    public final void log(int i, String str) {
        TransportUtils.log(i, TAG, TransportUtils.formatMessage(this.mPrefixForLog, null, str));
        saveLogEntry(TransportUtils.formatMessage(null, null, str));
    }

    public final void log(int i, String str, String str2) {
        TransportUtils.log(i, TAG, TransportUtils.formatMessage(this.mPrefixForLog, str, str2));
        saveLogEntry(TransportUtils.formatMessage(null, str, str2));
    }

    public final void saveLogEntry(String str) {
        List<String> list;
        CharSequence format = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
        String str2 = ((Object) format) + " " + str;
        synchronized (this.mLogBufferLock) {
            if (this.mLogBuffer.size() == 5) {
                this.mLogBuffer.remove(list.size() - 1);
            }
            this.mLogBuffer.add(0, str2);
        }
    }

    public List<String> getLogBuffer() {
        List<String> unmodifiableList;
        synchronized (this.mLogBufferLock) {
            unmodifiableList = Collections.unmodifiableList(this.mLogBuffer);
        }
        return unmodifiableList;
    }

    /* loaded from: classes.dex */
    public static class TransportConnectionMonitor implements ServiceConnection {
        public final Context mContext;
        public final WeakReference<TransportConnection> mTransportClientRef;

        public TransportConnectionMonitor(Context context, TransportConnection transportConnection) {
            this.mContext = context;
            this.mTransportClientRef = new WeakReference<>(transportConnection);
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TransportConnection transportConnection = this.mTransportClientRef.get();
            if (transportConnection == null) {
                referenceLost("TransportConnection.onServiceConnected()");
                return;
            }
            Binder.allowBlocking(iBinder);
            transportConnection.onServiceConnected(iBinder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            TransportConnection transportConnection = this.mTransportClientRef.get();
            if (transportConnection == null) {
                referenceLost("TransportConnection.onServiceDisconnected()");
            } else {
                transportConnection.onServiceDisconnected();
            }
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            TransportConnection transportConnection = this.mTransportClientRef.get();
            if (transportConnection == null) {
                referenceLost("TransportConnection.onBindingDied()");
            } else {
                transportConnection.onBindingDied();
            }
        }

        public final void referenceLost(String str) {
            this.mContext.unbindService(this);
            TransportUtils.log(4, TransportConnection.TAG, str + " called but TransportClient reference has been GC'ed");
        }
    }
}
