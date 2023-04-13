package android.hardware;

import android.p008os.MemoryFile;
import dalvik.system.CloseGuard;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.channels.Channel;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public final class SensorDirectChannel implements Channel {
    public static final int RATE_FAST = 2;
    public static final int RATE_NORMAL = 1;
    public static final int RATE_STOP = 0;
    public static final int RATE_VERY_FAST = 3;
    public static final int TYPE_HARDWARE_BUFFER = 2;
    public static final int TYPE_MEMORY_FILE = 1;
    private final CloseGuard mCloseGuard;
    private final AtomicBoolean mClosed = new AtomicBoolean();
    private final SensorManager mManager;
    private final int mNativeHandle;
    private final long mSize;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MemoryType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RateLevel {
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return !this.mClosed.get();
    }

    @Deprecated
    public boolean isValid() {
        return isOpen();
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.mClosed.compareAndSet(false, true)) {
            this.mCloseGuard.close();
            this.mManager.destroyDirectChannel(this);
        }
    }

    public int configure(Sensor sensor, int rateLevel) {
        return this.mManager.configureDirectChannelImpl(this, sensor, rateLevel);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SensorDirectChannel(SensorManager manager, int id, int type, long size) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mManager = manager;
        this.mNativeHandle = id;
        this.mType = type;
        this.mSize = size;
        closeGuard.open("SensorDirectChannel");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNativeHandle() {
        return this.mNativeHandle;
    }

    static long[] encodeData(MemoryFile ashmem) {
        int fd;
        try {
            fd = ashmem.getFileDescriptor().getInt$();
        } catch (IOException e) {
            fd = -1;
        }
        return new long[]{1, 0, fd};
    }

    protected void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }
}
