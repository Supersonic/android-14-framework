package com.android.server.location.listeners;

import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.listeners.ListenerExecutor;
import com.android.internal.util.Preconditions;
import com.android.server.location.listeners.ListenerRegistration;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public abstract class ListenerMultiplexer<TKey, TListener, TRegistration extends ListenerRegistration<TListener>, TMergedRegistration> {
    @GuardedBy({"mMultiplexerLock"})
    public TMergedRegistration mMerged;
    public final Object mMultiplexerLock = new Object();
    @GuardedBy({"mMultiplexerLock"})
    public final ArrayMap<TKey, TRegistration> mRegistrations = new ArrayMap<>();
    public final ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer mUpdateServiceBuffer = new UpdateServiceBuffer();
    public final ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard mReentrancyGuard = new ReentrancyGuard();
    @GuardedBy({"mMultiplexerLock"})
    public int mActiveRegistrationsCount = 0;
    @GuardedBy({"mMultiplexerLock"})
    public boolean mServiceRegistered = false;

    @GuardedBy({"mMultiplexerLock"})
    public abstract boolean isActive(TRegistration tregistration);

    @GuardedBy({"mMultiplexerLock"})
    public abstract TMergedRegistration mergeRegistrations(Collection<TRegistration> collection);

    @GuardedBy({"mMultiplexerLock"})
    public void onActive() {
    }

    @GuardedBy({"mMultiplexerLock"})
    public void onInactive() {
    }

    @GuardedBy({"mMultiplexerLock"})
    public void onRegister() {
    }

    @GuardedBy({"mMultiplexerLock"})
    public void onRegistrationAdded(TKey tkey, TRegistration tregistration) {
    }

    @GuardedBy({"mMultiplexerLock"})
    public void onRegistrationRemoved(TKey tkey, TRegistration tregistration) {
    }

    @GuardedBy({"mMultiplexerLock"})
    public void onUnregister() {
    }

    @GuardedBy({"mMultiplexerLock"})
    public abstract boolean registerWithService(TMergedRegistration tmergedregistration, Collection<TRegistration> collection);

    @GuardedBy({"mMultiplexerLock"})
    public abstract void unregisterWithService();

    @GuardedBy({"mMultiplexerLock"})
    public boolean reregisterWithService(TMergedRegistration tmergedregistration, TMergedRegistration tmergedregistration2, Collection<TRegistration> collection) {
        return registerWithService(tmergedregistration2, collection);
    }

    @GuardedBy({"mMultiplexerLock"})
    public void onRegistrationReplaced(TKey tkey, TRegistration tregistration, TKey tkey2, TRegistration tregistration2) {
        onRegistrationRemoved(tkey, tregistration);
        onRegistrationAdded(tkey2, tregistration2);
    }

    public final void putRegistration(TKey tkey, TRegistration tregistration) {
        replaceRegistration(tkey, tkey, tregistration);
    }

    public final void replaceRegistration(TKey tkey, TKey tkey2, TRegistration tregistration) {
        TRegistration tregistration2;
        Objects.requireNonNull(tkey);
        Objects.requireNonNull(tkey2);
        Objects.requireNonNull(tregistration);
        synchronized (this.mMultiplexerLock) {
            boolean z = true;
            Preconditions.checkState(!this.mReentrancyGuard.isReentrant());
            if (tkey != tkey2 && this.mRegistrations.containsKey(tkey2)) {
                z = false;
            }
            Preconditions.checkArgument(z);
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire = this.mUpdateServiceBuffer.acquire();
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire2 = this.mReentrancyGuard.acquire();
            try {
                boolean isEmpty = this.mRegistrations.isEmpty();
                int indexOfKey = this.mRegistrations.indexOfKey(tkey);
                if (indexOfKey >= 0) {
                    tregistration2 = this.mRegistrations.valueAt(indexOfKey);
                    unregister(tregistration2);
                    tregistration2.onUnregister();
                    if (tkey != tkey2) {
                        this.mRegistrations.removeAt(indexOfKey);
                    }
                } else {
                    tregistration2 = null;
                }
                if (tkey == tkey2 && indexOfKey >= 0) {
                    this.mRegistrations.setValueAt(indexOfKey, tregistration);
                } else {
                    this.mRegistrations.put(tkey2, tregistration);
                }
                if (isEmpty) {
                    onRegister();
                }
                tregistration.onRegister(tkey2);
                if (tregistration2 == null) {
                    onRegistrationAdded(tkey2, tregistration);
                } else {
                    onRegistrationReplaced(tkey, tregistration2, tkey2, tregistration);
                }
                onRegistrationActiveChanged(tregistration);
                if (acquire2 != null) {
                    acquire2.close();
                }
                if (acquire != null) {
                    acquire.close();
                }
            } catch (Throwable th) {
                if (acquire2 != null) {
                    try {
                        acquire2.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    public final void removeRegistrationIf(Predicate<TKey> predicate) {
        synchronized (this.mMultiplexerLock) {
            Preconditions.checkState(!this.mReentrancyGuard.isReentrant());
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire = this.mUpdateServiceBuffer.acquire();
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire2 = this.mReentrancyGuard.acquire();
            try {
                int size = this.mRegistrations.size();
                for (int i = 0; i < size; i++) {
                    TKey keyAt = this.mRegistrations.keyAt(i);
                    if (predicate.test(keyAt)) {
                        removeRegistration(keyAt, this.mRegistrations.valueAt(i));
                    }
                }
                if (acquire2 != null) {
                    acquire2.close();
                }
                if (acquire != null) {
                    acquire.close();
                }
            } catch (Throwable th) {
                if (acquire2 != null) {
                    try {
                        acquire2.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    public final void removeRegistration(TKey tkey) {
        synchronized (this.mMultiplexerLock) {
            Preconditions.checkState(!this.mReentrancyGuard.isReentrant());
            int indexOfKey = this.mRegistrations.indexOfKey(tkey);
            if (indexOfKey < 0) {
                return;
            }
            removeRegistration(indexOfKey);
        }
    }

    public final void removeRegistration(TKey tkey, ListenerRegistration<?> listenerRegistration) {
        synchronized (this.mMultiplexerLock) {
            int indexOfKey = this.mRegistrations.indexOfKey(tkey);
            if (indexOfKey < 0) {
                return;
            }
            TRegistration valueAt = this.mRegistrations.valueAt(indexOfKey);
            if (valueAt != listenerRegistration) {
                return;
            }
            if (this.mReentrancyGuard.isReentrant()) {
                unregister(valueAt);
                this.mReentrancyGuard.markForRemoval(tkey, valueAt);
            } else {
                removeRegistration(indexOfKey);
            }
        }
    }

    @GuardedBy({"mMultiplexerLock"})
    public final void removeRegistration(int i) {
        TKey keyAt = this.mRegistrations.keyAt(i);
        TRegistration valueAt = this.mRegistrations.valueAt(i);
        ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire = this.mUpdateServiceBuffer.acquire();
        try {
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire2 = this.mReentrancyGuard.acquire();
            unregister(valueAt);
            onRegistrationRemoved(keyAt, valueAt);
            valueAt.onUnregister();
            this.mRegistrations.removeAt(i);
            if (this.mRegistrations.isEmpty()) {
                onUnregister();
            }
            if (acquire2 != null) {
                acquire2.close();
            }
            if (acquire != null) {
                acquire.close();
            }
        } catch (Throwable th) {
            if (acquire != null) {
                try {
                    acquire.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void updateService() {
        synchronized (this.mMultiplexerLock) {
            if (this.mUpdateServiceBuffer.isBuffered()) {
                this.mUpdateServiceBuffer.markUpdateServiceRequired();
                return;
            }
            int size = this.mRegistrations.size();
            ArrayList arrayList = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                TRegistration valueAt = this.mRegistrations.valueAt(i);
                if (valueAt.isActive()) {
                    arrayList.add(valueAt);
                }
            }
            if (arrayList.isEmpty()) {
                if (this.mServiceRegistered) {
                    this.mMerged = null;
                    this.mServiceRegistered = false;
                    unregisterWithService();
                }
            } else {
                TMergedRegistration mergeRegistrations = mergeRegistrations(arrayList);
                if (this.mServiceRegistered) {
                    if (!Objects.equals(mergeRegistrations, this.mMerged)) {
                        boolean reregisterWithService = reregisterWithService(this.mMerged, mergeRegistrations, arrayList);
                        this.mServiceRegistered = reregisterWithService;
                        this.mMerged = reregisterWithService ? mergeRegistrations : null;
                    }
                } else {
                    boolean registerWithService = registerWithService(mergeRegistrations, arrayList);
                    this.mServiceRegistered = registerWithService;
                    this.mMerged = registerWithService ? mergeRegistrations : null;
                }
            }
        }
    }

    public final void resetService() {
        synchronized (this.mMultiplexerLock) {
            if (this.mServiceRegistered) {
                this.mMerged = null;
                this.mServiceRegistered = false;
                unregisterWithService();
                updateService();
            }
        }
    }

    public final boolean findRegistration(Predicate<TRegistration> predicate) {
        synchronized (this.mMultiplexerLock) {
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire = this.mReentrancyGuard.acquire();
            int size = this.mRegistrations.size();
            for (int i = 0; i < size; i++) {
                if (predicate.test(this.mRegistrations.valueAt(i))) {
                    if (acquire != null) {
                        acquire.close();
                    }
                    return true;
                }
            }
            if (acquire != null) {
                acquire.close();
            }
            return false;
        }
    }

    public final void updateRegistrations(Predicate<TRegistration> predicate) {
        synchronized (this.mMultiplexerLock) {
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire = this.mUpdateServiceBuffer.acquire();
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire2 = this.mReentrancyGuard.acquire();
            try {
                int size = this.mRegistrations.size();
                for (int i = 0; i < size; i++) {
                    TRegistration valueAt = this.mRegistrations.valueAt(i);
                    if (predicate.test(valueAt)) {
                        onRegistrationActiveChanged(valueAt);
                    }
                }
                if (acquire2 != null) {
                    acquire2.close();
                }
                if (acquire != null) {
                    acquire.close();
                }
            } catch (Throwable th) {
                if (acquire2 != null) {
                    try {
                        acquire2.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    public final boolean updateRegistration(Object obj, Predicate<TRegistration> predicate) {
        synchronized (this.mMultiplexerLock) {
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire = this.mUpdateServiceBuffer.acquire();
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire2 = this.mReentrancyGuard.acquire();
            try {
                int indexOfKey = this.mRegistrations.indexOfKey(obj);
                if (indexOfKey < 0) {
                    if (acquire2 != null) {
                        acquire2.close();
                    }
                    if (acquire != null) {
                        acquire.close();
                    }
                    return false;
                }
                TRegistration valueAt = this.mRegistrations.valueAt(indexOfKey);
                if (predicate.test(valueAt)) {
                    onRegistrationActiveChanged(valueAt);
                }
                if (acquire2 != null) {
                    acquire2.close();
                }
                if (acquire != null) {
                    acquire.close();
                }
                return true;
            } catch (Throwable th) {
                if (acquire2 != null) {
                    try {
                        acquire2.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    @GuardedBy({"mMultiplexerLock"})
    public final void onRegistrationActiveChanged(TRegistration tregistration) {
        boolean z = tregistration.isRegistered() && isActive(tregistration);
        if (tregistration.setActive(z)) {
            if (z) {
                int i = this.mActiveRegistrationsCount + 1;
                this.mActiveRegistrationsCount = i;
                if (i == 1) {
                    onActive();
                }
                tregistration.onActive();
            } else {
                tregistration.onInactive();
                int i2 = this.mActiveRegistrationsCount - 1;
                this.mActiveRegistrationsCount = i2;
                if (i2 == 0) {
                    onInactive();
                }
            }
            updateService();
        }
    }

    public final void deliverToListeners(Function<TRegistration, ListenerExecutor.ListenerOperation<TListener>> function) {
        ListenerExecutor.ListenerOperation<TListener> apply;
        synchronized (this.mMultiplexerLock) {
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire = this.mReentrancyGuard.acquire();
            int size = this.mRegistrations.size();
            for (int i = 0; i < size; i++) {
                TRegistration valueAt = this.mRegistrations.valueAt(i);
                if (valueAt.isActive() && (apply = function.apply(valueAt)) != null) {
                    valueAt.executeOperation(apply);
                }
            }
            if (acquire != null) {
                acquire.close();
            }
        }
    }

    public final void deliverToListeners(ListenerExecutor.ListenerOperation<TListener> listenerOperation) {
        synchronized (this.mMultiplexerLock) {
            ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire = this.mReentrancyGuard.acquire();
            int size = this.mRegistrations.size();
            for (int i = 0; i < size; i++) {
                TRegistration valueAt = this.mRegistrations.valueAt(i);
                if (valueAt.isActive()) {
                    valueAt.executeOperation(listenerOperation);
                }
            }
            if (acquire != null) {
                acquire.close();
            }
        }
    }

    @GuardedBy({"mMultiplexerLock"})
    public final void unregister(TRegistration tregistration) {
        tregistration.unregisterInternal();
        onRegistrationActiveChanged(tregistration);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mMultiplexerLock) {
            printWriter.print("service: ");
            printWriter.print(getServiceState());
            printWriter.println();
            if (!this.mRegistrations.isEmpty()) {
                printWriter.println("listeners:");
                int size = this.mRegistrations.size();
                for (int i = 0; i < size; i++) {
                    TRegistration valueAt = this.mRegistrations.valueAt(i);
                    printWriter.print("  ");
                    printWriter.print(valueAt);
                    if (!valueAt.isActive()) {
                        printWriter.println(" (inactive)");
                    } else {
                        printWriter.println();
                    }
                }
            }
        }
    }

    @GuardedBy({"mMultiplexerLock"})
    public String getServiceState() {
        if (this.mServiceRegistered) {
            TMergedRegistration tmergedregistration = this.mMerged;
            return tmergedregistration != null ? tmergedregistration.toString() : "registered";
        }
        return "unregistered";
    }

    /* loaded from: classes.dex */
    public final class ReentrancyGuard implements AutoCloseable {
        @GuardedBy({"mMultiplexerLock"})
        public int mGuardCount = 0;
        @GuardedBy({"mMultiplexerLock"})
        public ArraySet<Map.Entry<TKey, ListenerRegistration<?>>> mScheduledRemovals = null;

        public ReentrancyGuard() {
        }

        public boolean isReentrant() {
            boolean z;
            synchronized (ListenerMultiplexer.this.mMultiplexerLock) {
                z = this.mGuardCount != 0;
            }
            return z;
        }

        public void markForRemoval(TKey tkey, ListenerRegistration<?> listenerRegistration) {
            synchronized (ListenerMultiplexer.this.mMultiplexerLock) {
                Preconditions.checkState(isReentrant());
                if (this.mScheduledRemovals == null) {
                    this.mScheduledRemovals = new ArraySet<>(ListenerMultiplexer.this.mRegistrations.size());
                }
                this.mScheduledRemovals.add(new AbstractMap.SimpleImmutableEntry(tkey, listenerRegistration));
            }
        }

        public ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.ReentrancyGuard acquire() {
            synchronized (ListenerMultiplexer.this.mMultiplexerLock) {
                this.mGuardCount++;
            }
            return this;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            synchronized (ListenerMultiplexer.this.mMultiplexerLock) {
                Preconditions.checkState(this.mGuardCount > 0);
                int i = this.mGuardCount - 1;
                this.mGuardCount = i;
                ArraySet<Map.Entry<TKey, ListenerRegistration<?>>> arraySet = null;
                if (i == 0) {
                    ArraySet<Map.Entry<TKey, ListenerRegistration<?>>> arraySet2 = this.mScheduledRemovals;
                    this.mScheduledRemovals = null;
                    arraySet = arraySet2;
                }
                if (arraySet == null) {
                    return;
                }
                ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire = ListenerMultiplexer.this.mUpdateServiceBuffer.acquire();
                int size = arraySet.size();
                for (int i2 = 0; i2 < size; i2++) {
                    Map.Entry<TKey, ListenerRegistration<?>> valueAt = arraySet.valueAt(i2);
                    ListenerMultiplexer.this.removeRegistration(valueAt.getKey(), valueAt.getValue());
                }
                if (acquire != null) {
                    acquire.close();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class UpdateServiceBuffer implements AutoCloseable {
        @GuardedBy({"this"})
        public int mBufferCount = 0;
        @GuardedBy({"this"})
        public boolean mUpdateServiceRequired = false;

        public UpdateServiceBuffer() {
        }

        public synchronized boolean isBuffered() {
            return this.mBufferCount != 0;
        }

        public synchronized void markUpdateServiceRequired() {
            Preconditions.checkState(isBuffered());
            this.mUpdateServiceRequired = true;
        }

        public synchronized ListenerMultiplexer<TKey, TListener, TRegistration, TMergedRegistration>.UpdateServiceBuffer acquire() {
            this.mBufferCount++;
            return this;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            boolean z;
            synchronized (this) {
                z = false;
                Preconditions.checkState(this.mBufferCount > 0);
                int i = this.mBufferCount - 1;
                this.mBufferCount = i;
                if (i == 0) {
                    boolean z2 = this.mUpdateServiceRequired;
                    this.mUpdateServiceRequired = false;
                    z = z2;
                }
            }
            if (z) {
                ListenerMultiplexer.this.updateService();
            }
        }
    }
}
