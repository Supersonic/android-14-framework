package android.content;

import android.accounts.Account;
import android.content.ISyncAdapter;
import android.p008os.Binder;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public abstract class AbstractThreadedSyncAdapter {
    private static final boolean ENABLE_LOG;
    @Deprecated
    public static final int LOG_SYNC_DETAILS = 2743;
    private static final String TAG = "SyncAdapter";
    private boolean mAllowParallelSyncs;
    private final boolean mAutoInitialize;
    private final Context mContext;
    private final ISyncAdapterImpl mISyncAdapterImpl;
    private final AtomicInteger mNumSyncStarts;
    private final Object mSyncThreadLock;
    private final HashMap<Account, SyncThread> mSyncThreads;

    public abstract void onPerformSync(Account account, Bundle bundle, String str, ContentProviderClient contentProviderClient, SyncResult syncResult);

    static {
        ENABLE_LOG = Build.IS_DEBUGGABLE && Log.isLoggable(TAG, 3);
    }

    public AbstractThreadedSyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
    }

    public AbstractThreadedSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        this.mSyncThreads = new HashMap<>();
        this.mSyncThreadLock = new Object();
        this.mContext = context;
        this.mISyncAdapterImpl = new ISyncAdapterImpl();
        this.mNumSyncStarts = new AtomicInteger(0);
        this.mAutoInitialize = autoInitialize;
        this.mAllowParallelSyncs = allowParallelSyncs;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Account toSyncKey(Account account) {
        if (this.mAllowParallelSyncs) {
            return account;
        }
        return null;
    }

    /* loaded from: classes.dex */
    private class ISyncAdapterImpl extends ISyncAdapter.Stub {
        private ISyncAdapterImpl() {
        }

        private boolean isCallerSystem() {
            long callingUid = Binder.getCallingUid();
            if (callingUid != 1000) {
                EventLog.writeEvent(1397638484, "203229608", -1, "");
                return false;
            }
            return true;
        }

        @Override // android.content.ISyncAdapter
        public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback cb) {
            if (!isCallerSystem()) {
                return;
            }
            Handler.getMain().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.content.AbstractThreadedSyncAdapter$ISyncAdapterImpl$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((AbstractThreadedSyncAdapter) obj).handleOnUnsyncableAccount((ISyncAdapterUnsyncableAccountCallback) obj2);
                }
            }, AbstractThreadedSyncAdapter.this, cb));
        }

        /* JADX WARN: Removed duplicated region for block: B:63:0x012e  */
        @Override // android.content.ISyncAdapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void startSync(ISyncContext syncContext, String authority, Account account, Bundle extras) {
            boolean alreadyInProgress;
            if (isCallerSystem()) {
                if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                    if (extras != null) {
                        extras.size();
                    }
                    Log.m112d(AbstractThreadedSyncAdapter.TAG, "startSync() start " + authority + " " + account + " " + extras);
                }
                try {
                    try {
                    } catch (Throwable th) {
                        th = th;
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.m112d(AbstractThreadedSyncAdapter.TAG, "startSync() finishing");
                        }
                        throw th;
                    }
                } catch (Error | RuntimeException e) {
                    th = e;
                } catch (Throwable th2) {
                    th = th2;
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                    }
                    throw th;
                }
                try {
                    SyncContext syncContextClient = new SyncContext(syncContext);
                    Account threadsKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
                    synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                        if (AbstractThreadedSyncAdapter.this.mSyncThreads.containsKey(threadsKey)) {
                            boolean alreadyInProgress2 = AbstractThreadedSyncAdapter.ENABLE_LOG;
                            if (alreadyInProgress2) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "  alreadyInProgress");
                            }
                            alreadyInProgress = true;
                        } else if (AbstractThreadedSyncAdapter.this.mAutoInitialize && extras != null && extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)) {
                            if (ContentResolver.getIsSyncable(account, authority) < 0) {
                                ContentResolver.setIsSyncable(account, authority, 1);
                            }
                            syncContextClient.onFinished(new SyncResult());
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "startSync() finishing");
                                return;
                            }
                            return;
                        } else {
                            SyncThread syncThread = new SyncThread("SyncAdapterThread-" + AbstractThreadedSyncAdapter.this.mNumSyncStarts.incrementAndGet(), syncContextClient, authority, account, extras);
                            AbstractThreadedSyncAdapter.this.mSyncThreads.put(threadsKey, syncThread);
                            syncThread.start();
                            alreadyInProgress = false;
                        }
                        if (alreadyInProgress) {
                            syncContextClient.onFinished(SyncResult.ALREADY_IN_PROGRESS);
                        }
                        boolean alreadyInProgress3 = AbstractThreadedSyncAdapter.ENABLE_LOG;
                        if (alreadyInProgress3) {
                            Log.m112d(AbstractThreadedSyncAdapter.TAG, "startSync() finishing");
                        }
                    }
                } catch (Error | RuntimeException e2) {
                    th = e2;
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.m111d(AbstractThreadedSyncAdapter.TAG, "startSync() caught exception", th);
                    }
                    throw th;
                }
            }
        }

        @Override // android.content.ISyncAdapter
        public void cancelSync(ISyncContext syncContext) {
            if (!isCallerSystem()) {
                return;
            }
            SyncThread info = null;
            try {
                try {
                    synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                        Iterator it = AbstractThreadedSyncAdapter.this.mSyncThreads.values().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            SyncThread current = (SyncThread) it.next();
                            if (current.mSyncContext.getSyncContextBinder() == syncContext.asBinder()) {
                                info = current;
                                break;
                            }
                        }
                    }
                    if (info != null) {
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.m112d(AbstractThreadedSyncAdapter.TAG, "cancelSync() " + info.mAuthority + " " + info.mAccount);
                        }
                        if (AbstractThreadedSyncAdapter.this.mAllowParallelSyncs) {
                            AbstractThreadedSyncAdapter.this.onSyncCanceled(info);
                        } else {
                            AbstractThreadedSyncAdapter.this.onSyncCanceled();
                        }
                    } else if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.m104w(AbstractThreadedSyncAdapter.TAG, "cancelSync() unknown context");
                    }
                } catch (Error | RuntimeException th) {
                    if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                        Log.m111d(AbstractThreadedSyncAdapter.TAG, "cancelSync() caught exception", th);
                    }
                    throw th;
                }
            } finally {
                if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                    Log.m112d(AbstractThreadedSyncAdapter.TAG, "cancelSync() finishing");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SyncThread extends Thread {
        private final Account mAccount;
        private final String mAuthority;
        private final Bundle mExtras;
        private final SyncContext mSyncContext;
        private final Account mThreadsKey;

        private SyncThread(String name, SyncContext syncContext, String authority, Account account, Bundle extras) {
            super(name);
            this.mSyncContext = syncContext;
            this.mAuthority = authority;
            this.mAccount = account;
            this.mExtras = extras;
            this.mThreadsKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Process.setThreadPriority(10);
            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                Log.m112d(AbstractThreadedSyncAdapter.TAG, "Thread started");
            }
            Trace.traceBegin(128L, this.mAuthority);
            SyncResult syncResult = new SyncResult();
            ContentProviderClient provider = null;
            try {
                try {
                    try {
                        if (isCanceled()) {
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "Already canceled");
                            }
                            Trace.traceEnd(128L);
                            if (0 != 0) {
                                provider.release();
                            }
                            if (!isCanceled()) {
                                this.mSyncContext.onFinished(syncResult);
                            }
                            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                                AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                            }
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                                return;
                            }
                            return;
                        }
                        if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                            Log.m112d(AbstractThreadedSyncAdapter.TAG, "Calling onPerformSync...");
                        }
                        ContentProviderClient provider2 = AbstractThreadedSyncAdapter.this.mContext.getContentResolver().acquireContentProviderClient(this.mAuthority);
                        try {
                            if (provider2 != null) {
                                AbstractThreadedSyncAdapter.this.onPerformSync(this.mAccount, this.mExtras, this.mAuthority, provider2, syncResult);
                            } else {
                                syncResult.databaseError = true;
                            }
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "onPerformSync done");
                            }
                            Trace.traceEnd(128L);
                            if (provider2 != null) {
                                provider2.release();
                            }
                            if (!isCanceled()) {
                                this.mSyncContext.onFinished(syncResult);
                            }
                            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                                AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                            }
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                            }
                        } catch (Error | RuntimeException e) {
                            th = e;
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m111d(AbstractThreadedSyncAdapter.TAG, "caught exception", th);
                            }
                            throw th;
                        } catch (SecurityException e2) {
                            e = e2;
                            provider = provider2;
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m111d(AbstractThreadedSyncAdapter.TAG, "SecurityException", e);
                            }
                            AbstractThreadedSyncAdapter.this.onSecurityException(this.mAccount, this.mExtras, this.mAuthority, syncResult);
                            syncResult.databaseError = true;
                            Trace.traceEnd(128L);
                            if (provider != null) {
                                provider.release();
                            }
                            if (!isCanceled()) {
                                this.mSyncContext.onFinished(syncResult);
                            }
                            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                                AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                            }
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                            }
                        } catch (Throwable th) {
                            th = th;
                            provider = provider2;
                            Trace.traceEnd(128L);
                            if (provider != null) {
                                provider.release();
                            }
                            if (!isCanceled()) {
                                this.mSyncContext.onFinished(syncResult);
                            }
                            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                                AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                            }
                            if (AbstractThreadedSyncAdapter.ENABLE_LOG) {
                                Log.m112d(AbstractThreadedSyncAdapter.TAG, "Thread finished");
                            }
                            throw th;
                        }
                    } catch (Error | RuntimeException e3) {
                        th = e3;
                    }
                } catch (SecurityException e4) {
                    e = e4;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }

        private boolean isCanceled() {
            return Thread.currentThread().isInterrupted();
        }
    }

    public final IBinder getSyncAdapterBinder() {
        return this.mISyncAdapterImpl.asBinder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback cb) {
        boolean doSync;
        try {
            doSync = onUnsyncableAccount();
        } catch (RuntimeException e) {
            Log.m109e(TAG, "Exception while calling onUnsyncableAccount, assuming 'true'", e);
            doSync = true;
        }
        try {
            cb.onUnsyncableAccountDone(doSync);
        } catch (RemoteException e2) {
            Log.m109e(TAG, "Could not report result of onUnsyncableAccount", e2);
        }
    }

    public boolean onUnsyncableAccount() {
        return true;
    }

    public void onSecurityException(Account account, Bundle extras, String authority, SyncResult syncResult) {
    }

    public void onSyncCanceled() {
        SyncThread syncThread;
        synchronized (this.mSyncThreadLock) {
            syncThread = this.mSyncThreads.get(null);
        }
        if (syncThread != null) {
            syncThread.interrupt();
        }
    }

    public void onSyncCanceled(Thread thread) {
        thread.interrupt();
    }
}
