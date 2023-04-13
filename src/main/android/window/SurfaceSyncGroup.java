package android.window;

import android.p008os.Binder;
import android.p008os.BinderProxy;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.AttachedSurfaceControl;
import android.view.InsetsController$$ExternalSyntheticLambda8;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceView;
import android.view.WindowManagerGlobal;
import android.window.ISurfaceSyncGroup;
import android.window.ISurfaceSyncGroupCompletedListener;
import android.window.ITransactionReadyCallback;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public final class SurfaceSyncGroup {
    private static final boolean DEBUG = false;
    private static final int MAX_COUNT = 100;
    private static final String TAG = "SurfaceSyncGroup";
    private static final AtomicInteger sCounter = new AtomicInteger(0);
    private static Supplier<SurfaceControl.Transaction> sTransactionFactory = new InsetsController$$ExternalSyntheticLambda8();
    private Runnable mAddedToSyncListener;
    private boolean mFinished;
    private boolean mHasWMSync;
    public final ISurfaceSyncGroup mISurfaceSyncGroup;
    private final Object mLock;
    private final String mName;
    private ISurfaceSyncGroup mParentSyncGroup;
    private final ArraySet<ITransactionReadyCallback> mPendingSyncs;
    private ISurfaceSyncGroupCompletedListener mSurfaceSyncGroupCompletedListener;
    private final ArraySet<Pair<Executor, Runnable>> mSyncCompleteCallbacks;
    private boolean mSyncReady;
    private final Binder mToken;
    private final SurfaceControl.Transaction mTransaction;
    private Consumer<SurfaceControl.Transaction> mTransactionReadyConsumer;

    /* loaded from: classes4.dex */
    public interface SurfaceViewFrameCallback {
        void onFrameStarted();
    }

    private static boolean isLocalBinder(IBinder binder) {
        return !(binder instanceof BinderProxy);
    }

    private static SurfaceSyncGroup getSurfaceSyncGroup(ISurfaceSyncGroup iSurfaceSyncGroup) {
        if (iSurfaceSyncGroup instanceof ISurfaceSyncGroupImpl) {
            return ((ISurfaceSyncGroupImpl) iSurfaceSyncGroup).getSurfaceSyncGroup();
        }
        return null;
    }

    public static void setTransactionFactory(Supplier<SurfaceControl.Transaction> transactionFactory) {
        sTransactionFactory = transactionFactory;
    }

    public SurfaceSyncGroup(String name) {
        this(name, new Consumer() { // from class: android.window.SurfaceSyncGroup$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SurfaceSyncGroup.lambda$new$0((SurfaceControl.Transaction) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$new$0(SurfaceControl.Transaction transaction) {
        if (transaction != null) {
            transaction.apply();
        }
    }

    public SurfaceSyncGroup(String name, final Consumer<SurfaceControl.Transaction> transactionReadyConsumer) {
        this.mLock = new Object();
        this.mPendingSyncs = new ArraySet<>();
        this.mTransaction = sTransactionFactory.get();
        this.mSyncCompleteCallbacks = new ArraySet<>();
        this.mISurfaceSyncGroup = new ISurfaceSyncGroupImpl();
        this.mToken = new Binder();
        AtomicInteger atomicInteger = sCounter;
        if (atomicInteger.get() >= 100) {
            atomicInteger.set(0);
        }
        String str = name + "#" + atomicInteger.getAndIncrement();
        this.mName = str;
        this.mTransactionReadyConsumer = new Consumer() { // from class: android.window.SurfaceSyncGroup$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SurfaceSyncGroup.this.lambda$new$1(transactionReadyConsumer, (SurfaceControl.Transaction) obj);
            }
        };
        if (Trace.isTagEnabled(8L)) {
            Trace.instant(8L, "new SurfaceSyncGroup " + str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Consumer transactionReadyConsumer, SurfaceControl.Transaction transaction) {
        if (Trace.isTagEnabled(8L)) {
            Trace.instant(8L, "Final TransactionCallback with " + transaction + " for " + this.mName);
        }
        transactionReadyConsumer.accept(transaction);
        synchronized (this.mLock) {
            if (this.mSurfaceSyncGroupCompletedListener == null) {
                invokeSyncCompleteCallbacks();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeSyncCompleteCallbacks() {
        this.mSyncCompleteCallbacks.forEach(new Consumer() { // from class: android.window.SurfaceSyncGroup$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Executor) r1.first).execute((Runnable) ((Pair) obj).second);
            }
        });
    }

    public void addSyncCompleteCallback(Executor executor, Runnable runnable) {
        synchronized (this.mLock) {
            this.mSyncCompleteCallbacks.add(new Pair<>(executor, runnable));
        }
    }

    public void markSyncReady() {
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "markSyncReady " + this.mName);
        }
        synchronized (this.mLock) {
            if (this.mHasWMSync) {
                try {
                    WindowManagerGlobal.getWindowManagerService().markSurfaceSyncGroupReady(this.mToken);
                } catch (RemoteException e) {
                }
            }
            this.mSyncReady = true;
            checkIfSyncIsComplete();
        }
        if (Trace.isTagEnabled(8L)) {
            Trace.traceEnd(8L);
        }
    }

    public boolean add(final SurfaceView surfaceView, Consumer<SurfaceViewFrameCallback> frameCallbackConsumer) {
        final SurfaceSyncGroup surfaceSyncGroup = new SurfaceSyncGroup(surfaceView.getName());
        if (add(surfaceSyncGroup.mISurfaceSyncGroup, false, null)) {
            frameCallbackConsumer.accept(new SurfaceViewFrameCallback() { // from class: android.window.SurfaceSyncGroup$$ExternalSyntheticLambda0
                @Override // android.window.SurfaceSyncGroup.SurfaceViewFrameCallback
                public final void onFrameStarted() {
                    SurfaceView.this.syncNextFrame(new Consumer() { // from class: android.window.SurfaceSyncGroup$$ExternalSyntheticLambda4
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            SurfaceSyncGroup.lambda$add$3(SurfaceSyncGroup.this, (SurfaceControl.Transaction) obj);
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$add$3(SurfaceSyncGroup surfaceSyncGroup, SurfaceControl.Transaction transaction) {
        surfaceSyncGroup.addTransaction(transaction);
        surfaceSyncGroup.markSyncReady();
    }

    public boolean add(AttachedSurfaceControl attachedSurfaceControl, Runnable runnable) {
        SurfaceSyncGroup surfaceSyncGroup;
        if (attachedSurfaceControl == null || (surfaceSyncGroup = attachedSurfaceControl.getOrCreateSurfaceSyncGroup()) == null) {
            return false;
        }
        return add(surfaceSyncGroup, runnable);
    }

    public boolean add(SurfaceControlViewHost.SurfacePackage surfacePackage, Runnable runnable) {
        try {
            ISurfaceSyncGroup surfaceSyncGroup = surfacePackage.getRemoteInterface().getSurfaceSyncGroup();
            if (surfaceSyncGroup == null) {
                Log.m110e(TAG, "Failed to add SurfaceControlViewHost to SurfaceSyncGroup. SCVH returned null SurfaceSyncGroup");
                return false;
            }
            return add(surfaceSyncGroup, false, runnable);
        } catch (RemoteException e) {
            Log.m110e(TAG, "Failed to add SurfaceControlViewHost to SurfaceSyncGroup");
            return false;
        }
    }

    public boolean add(SurfaceSyncGroup surfaceSyncGroup, Runnable runnable) {
        return add(surfaceSyncGroup.mISurfaceSyncGroup, false, runnable);
    }

    public boolean add(ISurfaceSyncGroup surfaceSyncGroup, boolean parentSyncGroupMerge, Runnable runnable) {
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "addToSync token=" + this.mToken.hashCode() + " parent=" + this.mName);
        }
        synchronized (this.mLock) {
            if (this.mSyncReady) {
                Log.m104w(TAG, "Trying to add to sync when already marked as ready " + this.mName);
                if (Trace.isTagEnabled(8L)) {
                    Trace.traceEnd(8L);
                }
                return false;
            }
            if (runnable != null) {
                runnable.run();
            }
            if (isLocalBinder(surfaceSyncGroup.asBinder())) {
                boolean didAddLocalSync = addLocalSync(surfaceSyncGroup, parentSyncGroupMerge);
                if (Trace.isTagEnabled(8L)) {
                    Trace.traceEnd(8L);
                }
                return didAddLocalSync;
            }
            synchronized (this.mLock) {
                if (!this.mHasWMSync) {
                    ISurfaceSyncGroupCompletedListener.Stub stub = new ISurfaceSyncGroupCompletedListener.Stub() { // from class: android.window.SurfaceSyncGroup.1
                        @Override // android.window.ISurfaceSyncGroupCompletedListener
                        public void onSurfaceSyncGroupComplete() {
                            synchronized (SurfaceSyncGroup.this.mLock) {
                                SurfaceSyncGroup.this.invokeSyncCompleteCallbacks();
                            }
                        }
                    };
                    this.mSurfaceSyncGroupCompletedListener = stub;
                    if (!addSyncToWm(this.mToken, false, stub)) {
                        this.mSurfaceSyncGroupCompletedListener = null;
                        if (Trace.isTagEnabled(8L)) {
                            Trace.traceEnd(8L);
                        }
                        return false;
                    }
                    this.mHasWMSync = true;
                }
                try {
                    surfaceSyncGroup.onAddedToSyncGroup(this.mToken, parentSyncGroupMerge);
                    if (Trace.isTagEnabled(8L)) {
                        Trace.traceEnd(8L);
                    }
                    return true;
                } catch (RemoteException e) {
                    if (Trace.isTagEnabled(8L)) {
                        Trace.traceEnd(8L);
                    }
                    return false;
                }
            }
        }
    }

    public void addTransaction(SurfaceControl.Transaction transaction) {
        synchronized (this.mLock) {
            this.mTransaction.merge(transaction);
        }
    }

    public void setAddedToSyncListener(Runnable addedToSyncListener) {
        synchronized (this.mLock) {
            this.mAddedToSyncListener = addedToSyncListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean addSyncToWm(IBinder token, boolean parentSyncGroupMerge, ISurfaceSyncGroupCompletedListener surfaceSyncGroupCompletedListener) {
        try {
            if (Trace.isTagEnabled(8L)) {
                Trace.traceBegin(8L, "addSyncToWm=" + token.hashCode() + " group=" + this.mName);
            }
            AddToSurfaceSyncGroupResult addToSyncGroupResult = new AddToSurfaceSyncGroupResult();
            if (!WindowManagerGlobal.getWindowManagerService().addToSurfaceSyncGroup(token, parentSyncGroupMerge, surfaceSyncGroupCompletedListener, addToSyncGroupResult)) {
                if (Trace.isTagEnabled(8L)) {
                    Trace.traceEnd(8L);
                }
                return false;
            }
            setTransactionCallbackFromParent(addToSyncGroupResult.mParentSyncGroup, addToSyncGroupResult.mTransactionReadyCallback);
            if (Trace.isTagEnabled(8L)) {
                Trace.traceEnd(8L);
                return true;
            }
            return true;
        } catch (RemoteException e) {
            if (Trace.isTagEnabled(8L)) {
                Trace.traceEnd(8L);
            }
            return false;
        }
    }

    private boolean addLocalSync(ISurfaceSyncGroup childSyncToken, boolean parentSyncGroupMerge) {
        SurfaceSyncGroup childSurfaceSyncGroup = getSurfaceSyncGroup(childSyncToken);
        if (childSurfaceSyncGroup == null) {
            Log.m110e(TAG, "Trying to add a local sync that's either not valid or not from the local process=" + childSyncToken);
            return false;
        }
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "addLocalSync=" + childSurfaceSyncGroup.mName + " parent=" + this.mName);
        }
        ITransactionReadyCallback callback = createTransactionReadyCallback(parentSyncGroupMerge);
        if (callback == null) {
            return false;
        }
        childSurfaceSyncGroup.setTransactionCallbackFromParent(this.mISurfaceSyncGroup, callback);
        if (Trace.isTagEnabled(8L)) {
            Trace.traceEnd(8L);
            return true;
        }
        return true;
    }

    private void setTransactionCallbackFromParent(ISurfaceSyncGroup parentSyncGroup, final ITransactionReadyCallback transactionReadyCallback) {
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "setTransactionCallbackFromParent " + this.mName + " callback=" + transactionReadyCallback.hashCode());
        }
        boolean finished = false;
        Runnable addedToSyncListener = null;
        synchronized (this.mLock) {
            if (this.mFinished) {
                finished = true;
            } else {
                ISurfaceSyncGroup iSurfaceSyncGroup = this.mParentSyncGroup;
                if (iSurfaceSyncGroup != null && iSurfaceSyncGroup != parentSyncGroup) {
                    try {
                        parentSyncGroup.addToSync(iSurfaceSyncGroup, true);
                    } catch (RemoteException e) {
                    }
                }
                final Consumer<SurfaceControl.Transaction> lastCallback = this.mTransactionReadyConsumer;
                this.mParentSyncGroup = parentSyncGroup;
                this.mTransactionReadyConsumer = new Consumer() { // from class: android.window.SurfaceSyncGroup$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SurfaceSyncGroup.this.lambda$setTransactionCallbackFromParent$5(transactionReadyCallback, lastCallback, (SurfaceControl.Transaction) obj);
                    }
                };
                addedToSyncListener = this.mAddedToSyncListener;
            }
        }
        if (finished) {
            try {
                transactionReadyCallback.onTransactionReady(null);
            } catch (RemoteException e2) {
            }
        } else if (addedToSyncListener != null) {
            addedToSyncListener.run();
        }
        if (Trace.isTagEnabled(8L)) {
            Trace.traceEnd(8L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setTransactionCallbackFromParent$5(ITransactionReadyCallback transactionReadyCallback, Consumer lastCallback, SurfaceControl.Transaction transaction) {
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "transactionReadyCallback " + this.mName + " callback=" + transactionReadyCallback.hashCode());
        }
        lastCallback.accept(null);
        try {
            transactionReadyCallback.onTransactionReady(transaction);
        } catch (RemoteException e) {
            transaction.apply();
        }
        if (Trace.isTagEnabled(8L)) {
            Trace.traceEnd(8L);
        }
    }

    public String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfSyncIsComplete() {
        if (this.mFinished) {
            this.mTransaction.apply();
            return;
        }
        if (Trace.isTagEnabled(8L)) {
            Trace.instant(8L, "checkIfSyncIsComplete " + this.mName + " mSyncReady=" + this.mSyncReady + " mPendingSyncs=" + this.mPendingSyncs.size());
        }
        if (!this.mSyncReady || !this.mPendingSyncs.isEmpty()) {
            return;
        }
        this.mTransactionReadyConsumer.accept(this.mTransaction);
        this.mFinished = true;
    }

    public ITransactionReadyCallback createTransactionReadyCallback(final boolean parentSyncGroupMerge) {
        ITransactionReadyCallback transactionReadyCallback = new ITransactionReadyCallback.Stub() { // from class: android.window.SurfaceSyncGroup.2
            @Override // android.window.ITransactionReadyCallback
            public void onTransactionReady(SurfaceControl.Transaction t) {
                synchronized (SurfaceSyncGroup.this.mLock) {
                    if (t != null) {
                        if (parentSyncGroupMerge) {
                            t.merge(SurfaceSyncGroup.this.mTransaction);
                        }
                        SurfaceSyncGroup.this.mTransaction.merge(t);
                    }
                    SurfaceSyncGroup.this.mPendingSyncs.remove(this);
                    if (Trace.isTagEnabled(8L)) {
                        Trace.instant(8L, "onTransactionReady group=" + SurfaceSyncGroup.this.mName + " callback=" + hashCode());
                    }
                    SurfaceSyncGroup.this.checkIfSyncIsComplete();
                }
            }
        };
        synchronized (this.mLock) {
            if (this.mSyncReady) {
                Log.m110e(TAG, "Sync " + this.mName + " was already marked as ready. No more SurfaceSyncGroups can be added.");
                return null;
            }
            this.mPendingSyncs.add(transactionReadyCallback);
            if (Trace.isTagEnabled(8L)) {
                Trace.instant(8L, "createTransactionReadyCallback " + this.mName + " mPendingSyncs=" + this.mPendingSyncs.size() + " transactionReady=" + transactionReadyCallback.hashCode());
            }
            return transactionReadyCallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ISurfaceSyncGroupImpl extends ISurfaceSyncGroup.Stub {
        private ISurfaceSyncGroupImpl() {
        }

        @Override // android.window.ISurfaceSyncGroup
        public boolean onAddedToSyncGroup(IBinder parentSyncGroupToken, boolean parentSyncGroupMerge) {
            if (Trace.isTagEnabled(8L)) {
                Trace.traceBegin(8L, "onAddedToSyncGroup token=" + parentSyncGroupToken.hashCode() + " child=" + SurfaceSyncGroup.this.mName);
            }
            boolean didAdd = SurfaceSyncGroup.this.addSyncToWm(parentSyncGroupToken, parentSyncGroupMerge, null);
            if (Trace.isTagEnabled(8L)) {
                Trace.traceEnd(8L);
            }
            return didAdd;
        }

        @Override // android.window.ISurfaceSyncGroup
        public boolean addToSync(ISurfaceSyncGroup surfaceSyncGroup, boolean parentSyncGroupMerge) {
            return SurfaceSyncGroup.this.add(surfaceSyncGroup, parentSyncGroupMerge, null);
        }

        SurfaceSyncGroup getSurfaceSyncGroup() {
            return SurfaceSyncGroup.this;
        }
    }
}
