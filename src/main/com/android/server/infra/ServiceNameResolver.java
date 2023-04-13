package com.android.server.infra;

import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public interface ServiceNameResolver {

    /* loaded from: classes.dex */
    public interface NameResolverListener {
        void onNameResolved(int i, String str, boolean z);
    }

    void dumpShort(PrintWriter printWriter);

    void dumpShort(PrintWriter printWriter, int i);

    String getDefaultServiceName(int i);

    default boolean isConfiguredInMultipleMode() {
        return false;
    }

    default boolean isTemporary(int i) {
        return false;
    }

    default void setOnTemporaryServiceNameChangedCallback(NameResolverListener nameResolverListener) {
    }

    default void setServiceNameList(List<String> list, int i) {
    }

    default String[] getDefaultServiceNameList(int i) {
        if (isConfiguredInMultipleMode()) {
            throw new UnsupportedOperationException("getting default service list not supported");
        }
        return new String[]{getDefaultServiceName(i)};
    }

    default String getServiceName(int i) {
        return getDefaultServiceName(i);
    }

    default String[] getServiceNameList(int i) {
        return getDefaultServiceNameList(i);
    }

    default void setTemporaryService(int i, String str, int i2) {
        throw new UnsupportedOperationException("temporary user not supported");
    }

    default void setTemporaryServices(int i, String[] strArr, int i2) {
        throw new UnsupportedOperationException("temporary user not supported");
    }

    default void resetTemporaryService(int i) {
        throw new UnsupportedOperationException("temporary user not supported");
    }

    default boolean setDefaultServiceEnabled(int i, boolean z) {
        throw new UnsupportedOperationException("changing default service not supported");
    }

    default boolean isDefaultServiceEnabled(int i) {
        throw new UnsupportedOperationException("checking default service not supported");
    }
}
