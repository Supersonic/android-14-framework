package com.android.internal.p028os;

import android.p008os.Binder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
/* renamed from: com.android.internal.os.BinderTransactionNameResolver */
/* loaded from: classes4.dex */
public class BinderTransactionNameResolver {
    private static final Method NO_GET_DEFAULT_TRANSACTION_NAME_METHOD;
    private final HashMap<Class<? extends Binder>, Method> mGetDefaultTransactionNameMethods = new HashMap<>();

    public static String noDefaultTransactionName(int transactionCode) {
        return String.valueOf(transactionCode);
    }

    static {
        try {
            NO_GET_DEFAULT_TRANSACTION_NAME_METHOD = BinderTransactionNameResolver.class.getMethod("noDefaultTransactionName", Integer.TYPE);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMethodName(Class<? extends Binder> binderClass, int transactionCode) {
        Method method = this.mGetDefaultTransactionNameMethods.get(binderClass);
        if (method == null) {
            try {
                method = binderClass.getMethod("getDefaultTransactionName", Integer.TYPE);
            } catch (NoSuchMethodException e) {
                method = NO_GET_DEFAULT_TRANSACTION_NAME_METHOD;
            }
            if (method.getReturnType() != String.class || !Modifier.isStatic(method.getModifiers())) {
                method = NO_GET_DEFAULT_TRANSACTION_NAME_METHOD;
            }
            this.mGetDefaultTransactionNameMethods.put(binderClass, method);
        }
        try {
            return (String) method.invoke(null, Integer.valueOf(transactionCode));
        } catch (IllegalAccessException | InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }
}
