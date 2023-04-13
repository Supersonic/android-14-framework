package com.android.server.utils;

import android.os.Build;
import android.util.Log;
import java.lang.reflect.Field;
/* loaded from: classes2.dex */
public interface Watchable {
    void dispatchChange(Watchable watchable);

    boolean isRegisteredObserver(Watcher watcher);

    void registerObserver(Watcher watcher);

    void unregisterObserver(Watcher watcher);

    static void verifyWatchedAttributes(Object obj, Watcher watcher, boolean z) {
        Field[] declaredFields;
        if (Build.IS_ENG || Build.IS_USERDEBUG) {
            for (Field field : obj.getClass().getDeclaredFields()) {
                Watched watched = (Watched) field.getAnnotation(Watched.class);
                if (watched != null) {
                    String str = obj.getClass().getName() + "." + field.getName();
                    try {
                        field.setAccessible(true);
                        Object obj2 = field.get(obj);
                        if (obj2 instanceof Watchable) {
                            Watchable watchable = (Watchable) obj2;
                            if (watchable != null && !watchable.isRegisteredObserver(watcher)) {
                                handleVerifyError("Watchable " + str + " missing an observer", z);
                            }
                        } else if (!watched.manual()) {
                            handleVerifyError("@Watched annotated field " + str + " is not a watchable type and is not flagged for manual watching.", z);
                        }
                    } catch (IllegalAccessException unused) {
                        handleVerifyError("Watchable " + str + " not visible", z);
                    }
                }
            }
        }
    }

    static void handleVerifyError(String str, boolean z) {
        if (z) {
            Log.e("Watchable", str);
            return;
        }
        throw new RuntimeException(str);
    }

    static void verifyWatchedAttributes(Object obj, Watcher watcher) {
        verifyWatchedAttributes(obj, watcher, false);
    }
}
