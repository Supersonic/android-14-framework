package com.android.net.module.util;

import android.p008os.Binder;
/* loaded from: classes5.dex */
public class BinderUtils {

    @FunctionalInterface
    /* loaded from: classes5.dex */
    public interface ThrowingRunnable<T extends Exception> {
        void run() throws Exception;
    }

    @FunctionalInterface
    /* loaded from: classes5.dex */
    public interface ThrowingSupplier<T, E extends Exception> {
        T get() throws Exception;
    }

    public static final <T extends Exception> void withCleanCallingIdentity(ThrowingRunnable<T> action) throws Exception {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            action.run();
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    public static final <T, E extends Exception> T withCleanCallingIdentity(ThrowingSupplier<T, E> action) throws Exception {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            return action.get();
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }
}
