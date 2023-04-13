package android.util;
/* loaded from: classes3.dex */
public final class CloseGuard {
    private final dalvik.system.CloseGuard mImpl = dalvik.system.CloseGuard.get();

    public void open(String closeMethodName) {
        this.mImpl.open(closeMethodName);
    }

    public void close() {
        this.mImpl.close();
    }

    public void warnIfOpen() {
        this.mImpl.warnIfOpen();
    }
}
