package com.android.internal.inputmethod;

import android.util.Log;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes4.dex */
public final class CompletableFutureUtil {
    private CompletableFutureUtil() {
    }

    private static <T> T getValueOrRethrowErrorInternal(CompletableFuture<T> future) {
        boolean interrupted = false;
        while (true) {
            try {
                try {
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                } catch (ExecutionException e2) {
                    Throwable cause = e2.getCause();
                    throw new RuntimeException(cause.getMessage(), cause.getCause());
                }
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return future.get();
    }

    private static <T> T getValueOrNullInternal(CompletableFuture<T> future, String tag, String methodName, long timeoutMillis, CancellationGroup cancellationGroup) {
        T t;
        boolean needsToUnregister = cancellationGroup != null && cancellationGroup.tryRegisterFutureOrCancelImmediately(future);
        boolean interrupted = false;
        while (true) {
            try {
                try {
                    try {
                        try {
                            t = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
                            break;
                        } catch (CompletionException e) {
                            if (e.getCause() instanceof CancellationException) {
                                logCancellationInternal(tag, methodName);
                                if (needsToUnregister) {
                                    cancellationGroup.unregisterFuture(future);
                                }
                                if (interrupted) {
                                    Thread.currentThread().interrupt();
                                }
                                return null;
                            }
                            logErrorInternal(tag, methodName, e.getMessage());
                            if (needsToUnregister) {
                                cancellationGroup.unregisterFuture(future);
                            }
                            if (interrupted) {
                                Thread.currentThread().interrupt();
                            }
                            return null;
                        }
                    } catch (CancellationException e2) {
                        logCancellationInternal(tag, methodName);
                        if (needsToUnregister) {
                            cancellationGroup.unregisterFuture(future);
                        }
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                        return null;
                    } catch (TimeoutException e3) {
                        logTimeoutInternal(tag, methodName, timeoutMillis);
                        if (needsToUnregister) {
                            cancellationGroup.unregisterFuture(future);
                        }
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                        return null;
                    }
                } catch (InterruptedException e4) {
                    interrupted = true;
                } catch (Throwable e5) {
                    logErrorInternal(tag, methodName, e5.getMessage());
                    if (needsToUnregister) {
                        cancellationGroup.unregisterFuture(future);
                    }
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                }
            } catch (Throwable th) {
                if (needsToUnregister) {
                    cancellationGroup.unregisterFuture(future);
                }
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                throw th;
            }
        }
        if (needsToUnregister) {
            cancellationGroup.unregisterFuture(future);
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return t;
    }

    private static void logTimeoutInternal(String tag, String methodName, long timeout) {
        if (tag == null || methodName == null) {
            return;
        }
        Log.m104w(tag, methodName + " didn't respond in " + timeout + " msec.");
    }

    private static void logErrorInternal(String tag, String methodName, String errorString) {
        if (tag == null || methodName == null) {
            return;
        }
        Log.m104w(tag, methodName + " was failed with an exception=" + errorString);
    }

    private static void logCancellationInternal(String tag, String methodName) {
        if (tag == null || methodName == null) {
            return;
        }
        Log.m104w(tag, methodName + " was cancelled.");
    }

    public static <T> T getResult(CompletableFuture<T> future) {
        return (T) getValueOrRethrowErrorInternal(future);
    }

    public static boolean getBooleanResult(CompletableFuture<Boolean> future) {
        return ((Boolean) getValueOrRethrowErrorInternal(future)).booleanValue();
    }

    public static int getIntegerResult(CompletableFuture<Integer> future) {
        return ((Integer) getValueOrRethrowErrorInternal(future)).intValue();
    }

    public static boolean getResultOrFalse(CompletableFuture<Boolean> future, String tag, String methodName, CancellationGroup cancellationGroup, long timeoutMillis) {
        Boolean obj = (Boolean) getValueOrNullInternal(future, tag, methodName, timeoutMillis, cancellationGroup);
        if (obj != null) {
            return obj.booleanValue();
        }
        return false;
    }

    public static int getResultOrZero(CompletableFuture<Integer> future, String tag, String methodName, CancellationGroup cancellationGroup, long timeoutMillis) {
        Integer obj = (Integer) getValueOrNullInternal(future, tag, methodName, timeoutMillis, cancellationGroup);
        if (obj != null) {
            return obj.intValue();
        }
        return 0;
    }

    public static <T> T getResultOrNull(CompletableFuture<T> future, String tag, String methodName, CancellationGroup cancellationGroup, long timeoutMillis) {
        return (T) getValueOrNullInternal(future, tag, methodName, timeoutMillis, cancellationGroup);
    }
}
