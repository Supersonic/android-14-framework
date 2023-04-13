package com.android.server.location.contexthub;

import android.hardware.location.IContextHubTransactionCallback;
import android.hardware.location.NanoAppBinary;
import android.hardware.location.NanoAppState;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class ContextHubTransactionManager {
    public final ContextHubClientManager mClientManager;
    public final IContextHubWrapper mContextHubProxy;
    public final NanoAppStateManager mNanoAppStateManager;
    public final ArrayDeque<ContextHubServiceTransaction> mTransactionQueue = new ArrayDeque<>();
    public final AtomicInteger mNextAvailableId = new AtomicInteger();
    public final ScheduledThreadPoolExecutor mTimeoutExecutor = new ScheduledThreadPoolExecutor(1);
    public ScheduledFuture<?> mTimeoutFuture = null;
    public final ConcurrentLinkedEvictingDeque<TransactionRecord> mTransactionRecordDeque = new ConcurrentLinkedEvictingDeque<>(20);

    public final int toStatsTransactionResult(int i) {
        if (i != 0) {
            switch (i) {
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
                case 5:
                    return 5;
                case 6:
                    return 6;
                case 7:
                    return 7;
                case 8:
                    return 8;
                default:
                    return 1;
            }
        }
        return 0;
    }

    /* loaded from: classes.dex */
    public class TransactionRecord {
        public final long mTimestamp = System.currentTimeMillis();
        public final String mTransaction;

        public TransactionRecord(String str) {
            this.mTransaction = str;
        }

        public String toString() {
            return ContextHubServiceUtil.formatDateFromTimestamp(this.mTimestamp) + " " + this.mTransaction;
        }
    }

    public ContextHubTransactionManager(IContextHubWrapper iContextHubWrapper, ContextHubClientManager contextHubClientManager, NanoAppStateManager nanoAppStateManager) {
        this.mContextHubProxy = iContextHubWrapper;
        this.mClientManager = contextHubClientManager;
        this.mNanoAppStateManager = nanoAppStateManager;
    }

    public ContextHubServiceTransaction createLoadTransaction(final int i, final NanoAppBinary nanoAppBinary, final IContextHubTransactionCallback iContextHubTransactionCallback, String str) {
        return new ContextHubServiceTransaction(this.mNextAvailableId.getAndIncrement(), 0, nanoAppBinary.getNanoAppId(), str) { // from class: com.android.server.location.contexthub.ContextHubTransactionManager.1
            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public int onTransact() {
                try {
                    return ContextHubTransactionManager.this.mContextHubProxy.loadNanoapp(i, nanoAppBinary, getTransactionId());
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while trying to load nanoapp with ID 0x" + Long.toHexString(nanoAppBinary.getNanoAppId()), e);
                    return 1;
                }
            }

            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public void onTransactionComplete(int i2) {
                ContextHubStatsLog.write(401, nanoAppBinary.getNanoAppId(), nanoAppBinary.getNanoAppVersion(), 1, ContextHubTransactionManager.this.toStatsTransactionResult(i2));
                ContextHubEventLogger.getInstance().logNanoappLoad(i, nanoAppBinary.getNanoAppId(), nanoAppBinary.getNanoAppVersion(), nanoAppBinary.getBinary().length, i2 == 0);
                if (i2 == 0) {
                    ContextHubTransactionManager.this.mNanoAppStateManager.addNanoAppInstance(i, nanoAppBinary.getNanoAppId(), nanoAppBinary.getNanoAppVersion());
                }
                try {
                    iContextHubTransactionCallback.onTransactionComplete(i2);
                    if (i2 == 0) {
                        ContextHubTransactionManager.this.mClientManager.onNanoAppLoaded(i, nanoAppBinary.getNanoAppId());
                    }
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while calling client onTransactionComplete", e);
                }
            }
        };
    }

    public ContextHubServiceTransaction createUnloadTransaction(final int i, final long j, final IContextHubTransactionCallback iContextHubTransactionCallback, String str) {
        return new ContextHubServiceTransaction(this.mNextAvailableId.getAndIncrement(), 1, j, str) { // from class: com.android.server.location.contexthub.ContextHubTransactionManager.2
            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public int onTransact() {
                try {
                    return ContextHubTransactionManager.this.mContextHubProxy.unloadNanoapp(i, j, getTransactionId());
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while trying to unload nanoapp with ID 0x" + Long.toHexString(j), e);
                    return 1;
                }
            }

            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public void onTransactionComplete(int i2) {
                ContextHubStatsLog.write(401, j, 0, 2, ContextHubTransactionManager.this.toStatsTransactionResult(i2));
                ContextHubEventLogger.getInstance().logNanoappUnload(i, j, i2 == 0);
                if (i2 == 0) {
                    ContextHubTransactionManager.this.mNanoAppStateManager.removeNanoAppInstance(i, j);
                }
                try {
                    iContextHubTransactionCallback.onTransactionComplete(i2);
                    if (i2 == 0) {
                        ContextHubTransactionManager.this.mClientManager.onNanoAppUnloaded(i, j);
                    }
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while calling client onTransactionComplete", e);
                }
            }
        };
    }

    public ContextHubServiceTransaction createEnableTransaction(final int i, final long j, final IContextHubTransactionCallback iContextHubTransactionCallback, String str) {
        return new ContextHubServiceTransaction(this.mNextAvailableId.getAndIncrement(), 2, str) { // from class: com.android.server.location.contexthub.ContextHubTransactionManager.3
            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public int onTransact() {
                try {
                    return ContextHubTransactionManager.this.mContextHubProxy.enableNanoapp(i, j, getTransactionId());
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while trying to enable nanoapp with ID 0x" + Long.toHexString(j), e);
                    return 1;
                }
            }

            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public void onTransactionComplete(int i2) {
                try {
                    iContextHubTransactionCallback.onTransactionComplete(i2);
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while calling client onTransactionComplete", e);
                }
            }
        };
    }

    public ContextHubServiceTransaction createDisableTransaction(final int i, final long j, final IContextHubTransactionCallback iContextHubTransactionCallback, String str) {
        return new ContextHubServiceTransaction(this.mNextAvailableId.getAndIncrement(), 3, str) { // from class: com.android.server.location.contexthub.ContextHubTransactionManager.4
            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public int onTransact() {
                try {
                    return ContextHubTransactionManager.this.mContextHubProxy.disableNanoapp(i, j, getTransactionId());
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while trying to disable nanoapp with ID 0x" + Long.toHexString(j), e);
                    return 1;
                }
            }

            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public void onTransactionComplete(int i2) {
                try {
                    iContextHubTransactionCallback.onTransactionComplete(i2);
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while calling client onTransactionComplete", e);
                }
            }
        };
    }

    public ContextHubServiceTransaction createQueryTransaction(final int i, final IContextHubTransactionCallback iContextHubTransactionCallback, String str) {
        return new ContextHubServiceTransaction(this.mNextAvailableId.getAndIncrement(), 4, str) { // from class: com.android.server.location.contexthub.ContextHubTransactionManager.5
            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public int onTransact() {
                try {
                    return ContextHubTransactionManager.this.mContextHubProxy.queryNanoapps(i);
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while trying to query for nanoapps", e);
                    return 1;
                }
            }

            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public void onTransactionComplete(int i2) {
                onQueryResponse(i2, Collections.emptyList());
            }

            @Override // com.android.server.location.contexthub.ContextHubServiceTransaction
            public void onQueryResponse(int i2, List<NanoAppState> list) {
                try {
                    iContextHubTransactionCallback.onQueryResponse(i2, list);
                } catch (RemoteException e) {
                    Log.e("ContextHubTransactionManager", "RemoteException while calling client onQueryComplete", e);
                }
            }
        };
    }

    public synchronized void addTransaction(ContextHubServiceTransaction contextHubServiceTransaction) throws IllegalStateException {
        if (this.mTransactionQueue.size() == 10000) {
            throw new IllegalStateException("Transaction queue is full (capacity = 10000)");
        }
        this.mTransactionQueue.add(contextHubServiceTransaction);
        this.mTransactionRecordDeque.add(new TransactionRecord(contextHubServiceTransaction.toString()));
        if (this.mTransactionQueue.size() == 1) {
            startNextTransaction();
        }
    }

    public synchronized void onTransactionResponse(int i, boolean z) {
        ContextHubServiceTransaction peek = this.mTransactionQueue.peek();
        if (peek == null) {
            Log.w("ContextHubTransactionManager", "Received unexpected transaction response (no transaction pending)");
        } else if (peek.getTransactionId() != i) {
            Log.w("ContextHubTransactionManager", "Received unexpected transaction response (expected ID = " + peek.getTransactionId() + ", received ID = " + i + ")");
        } else {
            peek.onTransactionComplete(z ? 0 : 5);
            removeTransactionAndStartNext();
        }
    }

    public synchronized void onQueryResponse(List<NanoAppState> list) {
        ContextHubServiceTransaction peek = this.mTransactionQueue.peek();
        if (peek == null) {
            Log.w("ContextHubTransactionManager", "Received unexpected query response (no transaction pending)");
        } else if (peek.getTransactionType() != 4) {
            Log.w("ContextHubTransactionManager", "Received unexpected query response (expected " + peek + ")");
        } else {
            peek.onQueryResponse(0, list);
            removeTransactionAndStartNext();
        }
    }

    public synchronized void onHubReset() {
        if (this.mTransactionQueue.peek() == null) {
            return;
        }
        removeTransactionAndStartNext();
    }

    public final void removeTransactionAndStartNext() {
        this.mTimeoutFuture.cancel(false);
        this.mTransactionQueue.remove().setComplete();
        if (this.mTransactionQueue.isEmpty()) {
            return;
        }
        startNextTransaction();
    }

    public final void startNextTransaction() {
        int i = 1;
        while (i != 0 && !this.mTransactionQueue.isEmpty()) {
            final ContextHubServiceTransaction peek = this.mTransactionQueue.peek();
            int onTransact = peek.onTransact();
            if (onTransact == 0) {
                Runnable runnable = new Runnable() { // from class: com.android.server.location.contexthub.ContextHubTransactionManager$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContextHubTransactionManager.this.lambda$startNextTransaction$0(peek);
                    }
                };
                TimeUnit timeUnit = TimeUnit.SECONDS;
                try {
                    this.mTimeoutFuture = this.mTimeoutExecutor.schedule(runnable, peek.getTimeout(timeUnit), timeUnit);
                } catch (Exception e) {
                    Log.e("ContextHubTransactionManager", "Error when schedule a timer", e);
                }
            } else {
                peek.onTransactionComplete(ContextHubServiceUtil.toTransactionResult(onTransact));
                this.mTransactionQueue.remove();
            }
            i = onTransact;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startNextTransaction$0(ContextHubServiceTransaction contextHubServiceTransaction) {
        synchronized (this) {
            if (!contextHubServiceTransaction.isComplete()) {
                Log.d("ContextHubTransactionManager", contextHubServiceTransaction + " timed out");
                contextHubServiceTransaction.onTransactionComplete(6);
                removeTransactionAndStartNext();
            }
        }
    }

    public String toString() {
        int i;
        ContextHubServiceTransaction[] contextHubServiceTransactionArr;
        StringBuilder sb = new StringBuilder(100);
        synchronized (this) {
            contextHubServiceTransactionArr = (ContextHubServiceTransaction[]) this.mTransactionQueue.toArray(new ContextHubServiceTransaction[0]);
        }
        for (i = 0; i < contextHubServiceTransactionArr.length; i++) {
            sb.append(i + ": " + contextHubServiceTransactionArr[i] + "\n");
        }
        sb.append("Transaction History:\n");
        Iterator<TransactionRecord> descendingIterator = this.mTransactionRecordDeque.descendingIterator();
        while (descendingIterator.hasNext()) {
            sb.append(descendingIterator.next() + "\n");
        }
        return sb.toString();
    }
}
