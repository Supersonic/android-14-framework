package com.android.internal.widget;

import android.util.Log;
import android.util.Pools;
import android.view.View;
/* loaded from: classes5.dex */
public class MessagingPool<T extends View> implements Pools.Pool<T> {
    private static final boolean ENABLED = false;
    private static final String TAG = "MessagingPool";
    private Pools.SynchronizedPool<T> mCurrentPool;
    private final int mMaxPoolSize;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.util.Pools.Pool
    public /* bridge */ /* synthetic */ boolean release(Object obj) {
        return release((MessagingPool<T>) ((View) obj));
    }

    public MessagingPool(int maxPoolSize) {
        this.mMaxPoolSize = maxPoolSize;
    }

    @Override // android.util.Pools.Pool
    public T acquire() {
        return null;
    }

    public boolean release(T instance) {
        if (instance.getParent() != null) {
            Log.wtf(TAG, "releasing " + instance + " with parent " + instance.getParent());
            return false;
        }
        return false;
    }

    public void clear() {
    }
}
