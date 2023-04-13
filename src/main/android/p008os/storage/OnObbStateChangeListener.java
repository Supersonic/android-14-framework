package android.p008os.storage;
/* renamed from: android.os.storage.OnObbStateChangeListener */
/* loaded from: classes3.dex */
public abstract class OnObbStateChangeListener {
    public static final int ERROR_ALREADY_MOUNTED = 24;
    public static final int ERROR_COULD_NOT_MOUNT = 21;
    public static final int ERROR_COULD_NOT_UNMOUNT = 22;
    public static final int ERROR_INTERNAL = 20;
    public static final int ERROR_NOT_MOUNTED = 23;
    public static final int ERROR_PERMISSION_DENIED = 25;
    public static final int MOUNTED = 1;
    public static final int UNMOUNTED = 2;

    public void onObbStateChange(String path, int state) {
    }
}
