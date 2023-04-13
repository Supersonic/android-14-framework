package com.android.internal.util.function.pooled;

import android.p008os.Message;
import com.android.internal.util.function.DecConsumer;
import com.android.internal.util.function.DodecConsumer;
import com.android.internal.util.function.HeptConsumer;
import com.android.internal.util.function.HexConsumer;
import com.android.internal.util.function.NonaConsumer;
import com.android.internal.util.function.OctConsumer;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuadPredicate;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.QuintPredicate;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.TriPredicate;
import com.android.internal.util.function.UndecConsumer;
import com.android.internal.util.function.pooled.PooledSupplier;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public interface PooledLambda {
    void recycle();

    PooledLambda recycleOnUse();

    /* renamed from: __ */
    static <R> ArgumentPlaceholder<R> m17__() {
        return (ArgumentPlaceholder<R>) ArgumentPlaceholder.INSTANCE;
    }

    /* renamed from: __ */
    static <R> ArgumentPlaceholder<R> m16__(Class<R> typeHint) {
        return m17__();
    }

    static <R> PooledSupplier<R> obtainSupplier(R value) {
        PooledLambdaImpl r = PooledLambdaImpl.acquireConstSupplier(3);
        r.mFunc = value;
        return r;
    }

    static PooledSupplier.OfInt obtainSupplier(int value) {
        PooledLambdaImpl r = PooledLambdaImpl.acquireConstSupplier(4);
        r.mConstValue = value;
        return r;
    }

    static PooledSupplier.OfLong obtainSupplier(long value) {
        PooledLambdaImpl r = PooledLambdaImpl.acquireConstSupplier(5);
        r.mConstValue = value;
        return r;
    }

    static PooledSupplier.OfDouble obtainSupplier(double value) {
        PooledLambdaImpl r = PooledLambdaImpl.acquireConstSupplier(6);
        r.mConstValue = Double.doubleToRawLongBits(value);
        return r;
    }

    static <A> PooledRunnable obtainRunnable(Consumer<? super A> function, A arg1) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 1, 0, 1, arg1, null, null, null, null, null, null, null, null, null, null, null);
    }

    static <A> Message obtainMessage(Consumer<? super A> function, A arg1) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 1, 0, 1, arg1, null, null, null, null, null, null, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B> PooledRunnable obtainRunnable(BiConsumer<? super A, ? super B> function, A arg1, B arg2) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 2, 0, 1, arg1, arg2, null, null, null, null, null, null, null, null, null, null);
    }

    static <A, B> PooledPredicate<A> obtainPredicate(BiPredicate<? super A, ? super B> function, ArgumentPlaceholder<A> arg1, B arg2) {
        return (PooledPredicate) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 2, 1, 2, arg1, arg2, null, null, null, null, null, null, null, null, null, null);
    }

    static <A, B, C> PooledPredicate<A> obtainPredicate(TriPredicate<? super A, ? super B, ? super C> function, ArgumentPlaceholder<A> arg1, B arg2, C arg3) {
        return (PooledPredicate) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 3, 1, 2, arg1, arg2, arg3, null, null, null, null, null, null, null, null, null);
    }

    static <A, B, C, D> PooledPredicate<A> obtainPredicate(QuadPredicate<? super A, ? super B, ? super C, ? super D> function, ArgumentPlaceholder<A> arg1, B arg2, C arg3, D arg4) {
        return (PooledPredicate) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 4, 1, 2, arg1, arg2, arg3, arg4, null, null, null, null, null, null, null, null);
    }

    static <A, B, C, D, E> PooledPredicate<A> obtainPredicate(QuintPredicate<? super A, ? super B, ? super C, ? super D, ? super E> function, ArgumentPlaceholder<A> arg1, B arg2, C arg3, D arg4, E arg5) {
        return (PooledPredicate) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 5, 1, 2, arg1, arg2, arg3, arg4, arg5, null, null, null, null, null, null, null);
    }

    static <A, B> PooledPredicate<B> obtainPredicate(BiPredicate<? super A, ? super B> function, A arg1, ArgumentPlaceholder<B> arg2) {
        return (PooledPredicate) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 2, 1, 2, arg1, arg2, null, null, null, null, null, null, null, null, null, null);
    }

    static <A, B> Message obtainMessage(BiConsumer<? super A, ? super B> function, A arg1, B arg2) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 2, 0, 1, arg1, arg2, null, null, null, null, null, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C> PooledRunnable obtainRunnable(TriConsumer<? super A, ? super B, ? super C> function, A arg1, B arg2, C arg3) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 3, 0, 1, arg1, arg2, arg3, null, null, null, null, null, null, null, null, null);
    }

    static <A, B, C> Message obtainMessage(TriConsumer<? super A, ? super B, ? super C> function, A arg1, B arg2, C arg3) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 3, 0, 1, arg1, arg2, arg3, null, null, null, null, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D> PooledRunnable obtainRunnable(QuadConsumer<? super A, ? super B, ? super C, ? super D> function, A arg1, B arg2, C arg3, D arg4) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 4, 0, 1, arg1, arg2, arg3, arg4, null, null, null, null, null, null, null, null);
    }

    static <A, B, C, D> Message obtainMessage(QuadConsumer<? super A, ? super B, ? super C, ? super D> function, A arg1, B arg2, C arg3, D arg4) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 4, 0, 1, arg1, arg2, arg3, arg4, null, null, null, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E> PooledRunnable obtainRunnable(QuintConsumer<? super A, ? super B, ? super C, ? super D, ? super E> function, A arg1, B arg2, C arg3, D arg4, E arg5) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 5, 0, 1, arg1, arg2, arg3, arg4, arg5, null, null, null, null, null, null, null);
    }

    static <A, B, C, D, E> Message obtainMessage(QuintConsumer<? super A, ? super B, ? super C, ? super D, ? super E> function, A arg1, B arg2, C arg3, D arg4, E arg5) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 5, 0, 1, arg1, arg2, arg3, arg4, arg5, null, null, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F> PooledRunnable obtainRunnable(HexConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 6, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, null, null, null, null, null, null);
    }

    static <A, B, C, D, E, F> Message obtainMessage(HexConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 6, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, null, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F, G> PooledRunnable obtainRunnable(HeptConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 7, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, null, null, null, null, null);
    }

    static <A, B, C, D, E, F, G> Message obtainMessage(HeptConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 7, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, null, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F, G, H> PooledRunnable obtainRunnable(OctConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 8, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, null, null, null, null);
    }

    static <A, B, C, D, E, F, G, H> Message obtainMessage(OctConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 8, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, null, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F, G, H, I> PooledRunnable obtainRunnable(NonaConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 9, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, null, null, null);
    }

    static <A, B, C, D, E, F, G, H, I> Message obtainMessage(NonaConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 9, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, null, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F, G, H, I, J> PooledRunnable obtainRunnable(DecConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? super J> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9, J arg10) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 10, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, null, null);
    }

    static <A, B, C, D, E, F, G, H, I, J> Message obtainMessage(DecConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? super J> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9, J arg10) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 10, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, null, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F, G, H, I, J, K> PooledRunnable obtainRunnable(UndecConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? super J, ? super K> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9, J arg10, K arg11) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 11, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, null);
    }

    static <A, B, C, D, E, F, G, H, I, J, K> Message obtainMessage(UndecConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? super J, ? super K> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9, J arg10, K arg11) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 11, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, null);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L> PooledRunnable obtainRunnable(DodecConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? super J, ? super K, ? super L> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9, J arg10, K arg11, L arg12) {
        return (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sPool, function, 12, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
    }

    static <A, B, C, D, E, F, G, H, I, J, K, L> Message obtainMessage(DodecConsumer<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? super J, ? super K, ? super L> function, A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7, H arg8, I arg9, J arg10, K arg11, L arg12) {
        Message callback;
        synchronized (Message.sPoolSync) {
            PooledRunnable callback2 = (PooledRunnable) PooledLambdaImpl.acquire(PooledLambdaImpl.sMessageCallbacksPool, function, 11, 0, 1, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
            callback = Message.obtain().setCallback(callback2.recycleOnUse());
        }
        return callback;
    }
}
