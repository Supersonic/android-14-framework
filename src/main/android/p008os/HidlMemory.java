package android.p008os;

import android.annotation.SystemApi;
import java.io.Closeable;
import java.io.IOException;
@SystemApi
/* renamed from: android.os.HidlMemory */
/* loaded from: classes3.dex */
public class HidlMemory implements Closeable {
    private NativeHandle mHandle;
    private final String mName;
    private long mNativeContext;
    private final long mSize;

    private native void nativeFinalize();

    public HidlMemory(String name, long size, NativeHandle handle) {
        this.mName = name;
        this.mSize = size;
        this.mHandle = handle;
    }

    public HidlMemory dup() throws IOException {
        String str = this.mName;
        long j = this.mSize;
        NativeHandle nativeHandle = this.mHandle;
        return new HidlMemory(str, j, nativeHandle != null ? nativeHandle.dup() : null);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        NativeHandle nativeHandle = this.mHandle;
        if (nativeHandle != null) {
            nativeHandle.close();
            this.mHandle = null;
        }
    }

    public NativeHandle releaseHandle() {
        NativeHandle handle = this.mHandle;
        this.mHandle = null;
        return handle;
    }

    public String getName() {
        return this.mName;
    }

    public long getSize() {
        return this.mSize;
    }

    public NativeHandle getHandle() {
        return this.mHandle;
    }

    protected void finalize() {
        try {
            try {
                close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            nativeFinalize();
        }
    }
}
