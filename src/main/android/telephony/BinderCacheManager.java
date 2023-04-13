package android.telephony;

import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.RemoteException;
import android.telephony.BinderCacheManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
/* loaded from: classes3.dex */
public class BinderCacheManager<T extends IInterface> {
    private final BinderInterfaceFactory<T> mBinderInterfaceFactory;
    private final AtomicReference<BinderCacheManager<T>.BinderDeathTracker> mCachedConnection = new AtomicReference<>();

    /* loaded from: classes3.dex */
    public interface BinderInterfaceFactory<T> {
        T create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class BinderDeathTracker implements IBinder.DeathRecipient {
        private final T mConnection;
        private final HashMap<Object, Runnable> mListeners = new HashMap<>();

        BinderDeathTracker(T connection) {
            this.mConnection = connection;
            try {
                connection.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        public boolean addListener(Object key, Runnable r) {
            synchronized (this.mListeners) {
                if (isAlive()) {
                    this.mListeners.put(key, r);
                    return true;
                }
                return false;
            }
        }

        public void removeListener(Object runnableKey) {
            synchronized (this.mListeners) {
                this.mListeners.remove(runnableKey);
            }
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            ArrayList<Runnable> listeners;
            synchronized (this.mListeners) {
                listeners = new ArrayList<>(this.mListeners.values());
                this.mListeners.clear();
                try {
                    this.mConnection.asBinder().unlinkToDeath(this, 0);
                } catch (NoSuchElementException e) {
                }
            }
            listeners.forEach(new BinderCacheManager$BinderDeathTracker$$ExternalSyntheticLambda0());
        }

        public T getConnection() {
            return this.mConnection;
        }

        public boolean isAlive() {
            return this.mConnection.asBinder().isBinderAlive();
        }
    }

    public BinderCacheManager(BinderInterfaceFactory<T> factory) {
        this.mBinderInterfaceFactory = factory;
    }

    public T listenOnBinder(Object runnableKey, Runnable deadRunnable) {
        BinderCacheManager<T>.BinderDeathTracker tracker;
        if (runnableKey == null || deadRunnable == null || (tracker = getTracker()) == null) {
            return null;
        }
        boolean addSucceeded = tracker.addListener(runnableKey, deadRunnable);
        if (addSucceeded) {
            return (T) tracker.getConnection();
        }
        return null;
    }

    public T getBinder() {
        BinderCacheManager<T>.BinderDeathTracker tracker = getTracker();
        if (tracker != null) {
            return (T) tracker.getConnection();
        }
        return null;
    }

    public T removeRunnable(Object runnableKey) {
        BinderCacheManager<T>.BinderDeathTracker tracker;
        if (runnableKey == null || (tracker = getTracker()) == null) {
            return null;
        }
        tracker.removeListener(runnableKey);
        return (T) tracker.getConnection();
    }

    private BinderCacheManager<T>.BinderDeathTracker getTracker() {
        return this.mCachedConnection.updateAndGet(new UnaryOperator() { // from class: android.telephony.BinderCacheManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                BinderCacheManager.BinderDeathTracker lambda$getTracker$0;
                lambda$getTracker$0 = BinderCacheManager.this.lambda$getTracker$0((BinderCacheManager.BinderDeathTracker) obj);
                return lambda$getTracker$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ BinderDeathTracker lambda$getTracker$0(BinderDeathTracker oldVal) {
        BinderDeathTracker binderDeathTracker = oldVal;
        if (binderDeathTracker == null || !binderDeathTracker.isAlive()) {
            T binder = this.mBinderInterfaceFactory.create();
            binderDeathTracker = binder != null ? new BinderDeathTracker(binder) : null;
        }
        if (binderDeathTracker == null || !binderDeathTracker.isAlive()) {
            return null;
        }
        return binderDeathTracker;
    }
}
