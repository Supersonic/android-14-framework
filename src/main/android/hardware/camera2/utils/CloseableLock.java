package android.hardware.camera2.utils;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Log;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/* loaded from: classes.dex */
public class CloseableLock implements AutoCloseable {
    private static final boolean VERBOSE = false;
    private final Condition mCondition;
    private final ReentrantLock mLock;
    private final ThreadLocal<Integer> mLockCount;
    private final String mName;
    private final String TAG = "CloseableLock";
    private volatile boolean mClosed = false;
    private boolean mExclusive = false;
    private int mSharedLocks = 0;

    /* loaded from: classes.dex */
    public class ScopedLock implements AutoCloseable {
        private ScopedLock() {
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            CloseableLock.this.releaseLock();
        }
    }

    public CloseableLock() {
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mLock = reentrantLock;
        this.mCondition = reentrantLock.newCondition();
        this.mLockCount = new ThreadLocal<Integer>() { // from class: android.hardware.camera2.utils.CloseableLock.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public Integer initialValue() {
                return 0;
            }
        };
        this.mName = "";
    }

    public CloseableLock(String name) {
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mLock = reentrantLock;
        this.mCondition = reentrantLock.newCondition();
        this.mLockCount = new ThreadLocal<Integer>() { // from class: android.hardware.camera2.utils.CloseableLock.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public Integer initialValue() {
                return 0;
            }
        };
        this.mName = name;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.mClosed) {
            return;
        }
        ScopedLock scoper = acquireExclusiveLock();
        if (scoper == null) {
            return;
        }
        if (this.mLockCount.get().intValue() != 1) {
            throw new IllegalStateException("Cannot close while one or more acquired locks are being held by this thread; release all other locks first");
        }
        try {
            this.mLock.lock();
            this.mClosed = true;
            this.mExclusive = false;
            this.mSharedLocks = 0;
            this.mLockCount.remove();
            this.mCondition.signalAll();
        } finally {
            this.mLock.unlock();
        }
    }

    public ScopedLock acquireLock() {
        try {
            this.mLock.lock();
            if (this.mClosed) {
                return null;
            }
            int ownedLocks = this.mLockCount.get().intValue();
            if (this.mExclusive && ownedLocks > 0) {
                throw new IllegalStateException("Cannot acquire shared lock while holding exclusive lock");
            }
            while (this.mExclusive) {
                this.mCondition.awaitUninterruptibly();
                if (this.mClosed) {
                    return null;
                }
            }
            this.mSharedLocks++;
            int ownedLocks2 = this.mLockCount.get().intValue() + 1;
            this.mLockCount.set(Integer.valueOf(ownedLocks2));
            this.mLock.unlock();
            return new ScopedLock();
        } finally {
            this.mLock.unlock();
        }
    }

    public ScopedLock acquireExclusiveLock() {
        try {
            this.mLock.lock();
            if (this.mClosed) {
                return null;
            }
            int ownedLocks = this.mLockCount.get().intValue();
            if (!this.mExclusive && ownedLocks > 0) {
                throw new IllegalStateException("Cannot acquire exclusive lock while holding shared lock");
            }
            while (ownedLocks == 0 && (this.mExclusive || this.mSharedLocks > 0)) {
                this.mCondition.awaitUninterruptibly();
                if (this.mClosed) {
                    return null;
                }
            }
            this.mExclusive = true;
            this.mLockCount.set(Integer.valueOf(this.mLockCount.get().intValue() + 1));
            this.mLock.unlock();
            return new ScopedLock();
        } finally {
            this.mLock.unlock();
        }
    }

    public void releaseLock() {
        if (this.mLockCount.get().intValue() <= 0) {
            throw new IllegalStateException("Cannot release lock that was not acquired by this thread");
        }
        try {
            this.mLock.lock();
            if (this.mClosed) {
                throw new IllegalStateException("Do not release after the lock has been closed");
            }
            if (!this.mExclusive) {
                this.mSharedLocks--;
            } else if (this.mSharedLocks != 0) {
                throw new AssertionError("Too many shared locks " + this.mSharedLocks);
            }
            int ownedLocks = this.mLockCount.get().intValue() - 1;
            this.mLockCount.set(Integer.valueOf(ownedLocks));
            if (ownedLocks == 0 && this.mExclusive) {
                this.mExclusive = false;
                this.mCondition.signalAll();
            } else if (ownedLocks == 0 && this.mSharedLocks == 0) {
                this.mCondition.signalAll();
            }
        } finally {
            this.mLock.unlock();
        }
    }

    private void log(String what) {
        Log.m106v("CloseableLock[" + this.mName + NavigationBarInflaterView.SIZE_MOD_END, what);
    }
}
