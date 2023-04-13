package android.util;
/* loaded from: classes3.dex */
public final class Pools {

    /* loaded from: classes3.dex */
    public interface Pool<T> {
        T acquire();

        boolean release(T t);
    }

    private Pools() {
    }

    /* loaded from: classes3.dex */
    public static class SimplePool<T> implements Pool<T> {
        private final Object[] mPool;
        private int mPoolSize;

        public SimplePool(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            this.mPool = new Object[maxPoolSize];
        }

        @Override // android.util.Pools.Pool
        public T acquire() {
            int i = this.mPoolSize;
            if (i > 0) {
                int lastPooledIndex = i - 1;
                Object[] objArr = this.mPool;
                T instance = (T) objArr[lastPooledIndex];
                objArr[lastPooledIndex] = null;
                this.mPoolSize = i - 1;
                return instance;
            }
            return null;
        }

        @Override // android.util.Pools.Pool
        public boolean release(T instance) {
            if (isInPool(instance)) {
                throw new IllegalStateException("Already in the pool!");
            }
            int i = this.mPoolSize;
            Object[] objArr = this.mPool;
            if (i < objArr.length) {
                objArr[i] = instance;
                this.mPoolSize = i + 1;
                return true;
            }
            return false;
        }

        private boolean isInPool(T instance) {
            for (int i = 0; i < this.mPoolSize; i++) {
                if (this.mPool[i] == instance) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static class SynchronizedPool<T> extends SimplePool<T> {
        private final Object mLock;

        public SynchronizedPool(int maxPoolSize, Object lock) {
            super(maxPoolSize);
            this.mLock = lock;
        }

        public SynchronizedPool(int maxPoolSize) {
            this(maxPoolSize, new Object());
        }

        @Override // android.util.Pools.SimplePool, android.util.Pools.Pool
        public T acquire() {
            T t;
            synchronized (this.mLock) {
                t = (T) super.acquire();
            }
            return t;
        }

        @Override // android.util.Pools.SimplePool, android.util.Pools.Pool
        public boolean release(T element) {
            boolean release;
            synchronized (this.mLock) {
                release = super.release(element);
            }
            return release;
        }
    }
}
