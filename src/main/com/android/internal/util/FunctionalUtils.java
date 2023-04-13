package com.android.internal.util;

import android.p008os.RemoteException;
import android.util.ExceptionUtils;
import com.android.internal.util.FunctionalUtils;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public class FunctionalUtils {

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingChecked2Consumer<Input, ExceptionOne extends Exception, ExceptionTwo extends Exception> {
        void accept(Input input) throws Exception, Exception;
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingCheckedConsumer<Input, ExceptionType extends Exception> {
        void accept(Input input) throws Exception;
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingCheckedFunction<Input, Output, ExceptionType extends Exception> {
        Output apply(Input input) throws Exception;
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingCheckedSupplier<Output, ExceptionType extends Exception> {
        Output get() throws Exception;
    }

    private FunctionalUtils() {
    }

    public static <T> Consumer<T> uncheckExceptions(ThrowingConsumer<T> action) {
        return action;
    }

    public static <I, O> Function<I, O> uncheckExceptions(ThrowingFunction<I, O> action) {
        return action;
    }

    public static Runnable uncheckExceptions(ThrowingRunnable action) {
        return action;
    }

    public static <A, B> BiConsumer<A, B> uncheckExceptions(ThrowingBiConsumer<A, B> action) {
        return action;
    }

    public static <T> Supplier<T> uncheckExceptions(ThrowingSupplier<T> action) {
        return action;
    }

    public static <T> Consumer<T> ignoreRemoteException(RemoteExceptionIgnoringConsumer<T> action) {
        return action;
    }

    public static Runnable handleExceptions(final ThrowingRunnable r, final Consumer<Throwable> handler) {
        return new Runnable() { // from class: com.android.internal.util.FunctionalUtils$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FunctionalUtils.lambda$handleExceptions$0(FunctionalUtils.ThrowingRunnable.this, handler);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$handleExceptions$0(ThrowingRunnable r, Consumer handler) {
        try {
            r.run();
        } catch (Throwable th) {
            handler.accept(th);
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingRunnable extends Runnable {
        void runOrThrow() throws Exception;

        @Override // java.lang.Runnable
        default void run() {
            try {
                runOrThrow();
            } catch (Exception ex) {
                throw ExceptionUtils.propagate(ex);
            }
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingSupplier<T> extends Supplier<T> {
        T getOrThrow() throws Exception;

        @Override // java.util.function.Supplier
        default T get() {
            try {
                return getOrThrow();
            } catch (Exception ex) {
                throw ExceptionUtils.propagate(ex);
            }
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingConsumer<T> extends Consumer<T> {
        void acceptOrThrow(T t) throws Exception;

        @Override // java.util.function.Consumer
        default void accept(T t) {
            try {
                acceptOrThrow(t);
            } catch (Exception ex) {
                throw ExceptionUtils.propagate(ex);
            }
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface RemoteExceptionIgnoringConsumer<T> extends Consumer<T> {
        void acceptOrThrow(T t) throws RemoteException;

        @Override // java.util.function.Consumer
        default void accept(T t) {
            try {
                acceptOrThrow(t);
            } catch (RemoteException e) {
            }
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingFunction<T, R> extends Function<T, R> {
        R applyOrThrow(T t) throws Exception;

        @Override // java.util.function.Function
        default R apply(T t) {
            try {
                return applyOrThrow(t);
            } catch (Exception ex) {
                throw ExceptionUtils.propagate(ex);
            }
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingBiFunction<T, U, R> extends BiFunction<T, U, R> {
        R applyOrThrow(T t, U u) throws Exception;

        @Override // java.util.function.BiFunction
        default R apply(T t, U u) {
            try {
                return applyOrThrow(t, u);
            } catch (Exception ex) {
                throw ExceptionUtils.propagate(ex);
            }
        }
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface ThrowingBiConsumer<A, B> extends BiConsumer<A, B> {
        void acceptOrThrow(A a, B b) throws Exception;

        @Override // java.util.function.BiConsumer
        default void accept(A a, B b) {
            try {
                acceptOrThrow(a, b);
            } catch (Exception ex) {
                throw ExceptionUtils.propagate(ex);
            }
        }
    }

    public static String getLambdaName(Object function) {
        int firstDollarIdx;
        String fullFunction = function.toString();
        int endPkgIdx = fullFunction.indexOf("-$$");
        if (endPkgIdx == -1 || (firstDollarIdx = fullFunction.indexOf(36, endPkgIdx + 3)) == -1) {
            return fullFunction;
        }
        int endClassIdx = fullFunction.indexOf(36, firstDollarIdx + 1);
        return endClassIdx == -1 ? fullFunction.substring(0, endPkgIdx - 1) + "$Lambda" : fullFunction.substring(0, endPkgIdx) + fullFunction.substring(firstDollarIdx + 1, endClassIdx) + "$Lambda";
    }
}
