package android.window;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public interface OnBackInvokedDispatcher {
    public static final boolean DEBUG = false;
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_OVERLAY = 1000000;
    public static final int PRIORITY_SYSTEM = -1;
    public static final String TAG = "OnBackInvokedDispatcher";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Priority {
    }

    void registerOnBackInvokedCallback(int i, OnBackInvokedCallback onBackInvokedCallback);

    void unregisterOnBackInvokedCallback(OnBackInvokedCallback onBackInvokedCallback);

    default void registerSystemOnBackInvokedCallback(OnBackInvokedCallback callback) {
    }

    default void setImeOnBackInvokedDispatcher(ImeOnBackInvokedDispatcher imeDispatcher) {
    }
}
