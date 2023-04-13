package android.p008os;

import android.annotation.SystemApi;
import android.util.MutableInt;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import libcore.util.HexEncoding;
@SystemApi
/* renamed from: android.os.SystemProperties */
/* loaded from: classes3.dex */
public class SystemProperties {
    public static final int PROP_NAME_MAX = Integer.MAX_VALUE;
    public static final int PROP_VALUE_MAX = 91;
    private static final String TAG = "SystemProperties";
    private static final boolean TRACK_KEY_ACCESS = false;
    private static final ArrayList<Runnable> sChangeCallbacks = new ArrayList<>();
    private static final HashMap<String, MutableInt> sRoReads = null;

    private static native void native_add_change_callback();

    @FastNative
    private static native long native_find(String str);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native String native_get(long j);

    @FastNative
    private static native String native_get(String str, String str2);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native boolean native_get_boolean(long j, boolean z);

    @FastNative
    private static native boolean native_get_boolean(String str, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native int native_get_int(long j, int i);

    @FastNative
    private static native int native_get_int(String str, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native long native_get_long(long j, long j2);

    @FastNative
    private static native long native_get_long(String str, long j);

    private static native void native_report_sysprop_change();

    private static native void native_set(String str, String str2);

    private static void onKeyAccess(String key) {
    }

    private static String native_get(String key) {
        return native_get(key, "");
    }

    @SystemApi
    public static String get(String key) {
        return native_get(key);
    }

    @SystemApi
    public static String get(String key, String def) {
        return native_get(key, def);
    }

    @SystemApi
    public static int getInt(String key, int def) {
        return native_get_int(key, def);
    }

    @SystemApi
    public static long getLong(String key, long def) {
        return native_get_long(key, def);
    }

    @SystemApi
    public static boolean getBoolean(String key, boolean def) {
        return native_get_boolean(key, def);
    }

    public static void set(String key, String val) {
        if (val != null && !key.startsWith("ro.") && val.getBytes(StandardCharsets.UTF_8).length > 91) {
            throw new IllegalArgumentException("value of system property '" + key + "' is longer than 91 bytes: " + val);
        }
        native_set(key, val);
    }

    public static void addChangeCallback(Runnable callback) {
        ArrayList<Runnable> arrayList = sChangeCallbacks;
        synchronized (arrayList) {
            if (arrayList.size() == 0) {
                native_add_change_callback();
            }
            arrayList.add(callback);
        }
    }

    public static void removeChangeCallback(Runnable callback) {
        ArrayList<Runnable> arrayList = sChangeCallbacks;
        synchronized (arrayList) {
            if (arrayList.contains(callback)) {
                arrayList.remove(callback);
            }
        }
    }

    private static void callChangeCallbacks() {
        ArrayList<Runnable> arrayList = sChangeCallbacks;
        synchronized (arrayList) {
            if (arrayList.size() == 0) {
                return;
            }
            ArrayList<Runnable> callbacks = new ArrayList<>(arrayList);
            long token = Binder.clearCallingIdentity();
            for (int i = 0; i < callbacks.size(); i++) {
                try {
                    callbacks.get(i).run();
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }
    }

    public static void reportSyspropChanged() {
        native_report_sysprop_change();
    }

    public static String digestOf(String... keys) {
        Arrays.sort(keys);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            for (String key : keys) {
                String item = key + "=" + get(key) + "\n";
                digest.update(item.getBytes(StandardCharsets.UTF_8));
            }
            return HexEncoding.encodeToString(digest.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private SystemProperties() {
    }

    public static Handle find(String name) {
        long nativeHandle = native_find(name);
        if (nativeHandle == 0) {
            return null;
        }
        return new Handle(nativeHandle);
    }

    /* renamed from: android.os.SystemProperties$Handle */
    /* loaded from: classes3.dex */
    public static final class Handle {
        private final long mNativeHandle;

        public String get() {
            return SystemProperties.native_get(this.mNativeHandle);
        }

        public int getInt(int def) {
            return SystemProperties.native_get_int(this.mNativeHandle, def);
        }

        public long getLong(long def) {
            return SystemProperties.native_get_long(this.mNativeHandle, def);
        }

        public boolean getBoolean(boolean def) {
            return SystemProperties.native_get_boolean(this.mNativeHandle, def);
        }

        private Handle(long nativeHandle) {
            this.mNativeHandle = nativeHandle;
        }
    }
}
