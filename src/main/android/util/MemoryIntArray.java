package android.util;

import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import libcore.io.IoUtils;
/* loaded from: classes3.dex */
public final class MemoryIntArray implements Parcelable, Closeable {
    public static final Parcelable.Creator<MemoryIntArray> CREATOR = new Parcelable.Creator<MemoryIntArray>() { // from class: android.util.MemoryIntArray.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MemoryIntArray createFromParcel(Parcel parcel) {
            try {
                return new MemoryIntArray(parcel);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error unparceling MemoryIntArray");
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MemoryIntArray[] newArray(int size) {
            return new MemoryIntArray[size];
        }
    };
    private static final int MAX_SIZE = 1024;
    private static final String TAG = "MemoryIntArray";
    private final dalvik.system.CloseGuard mCloseGuard;
    private int mFd;
    private final boolean mIsOwner;
    private final long mMemoryAddr;

    private native void nativeClose(int i, long j, boolean z);

    private native int nativeCreate(String str, int i);

    private native int nativeGet(int i, long j, int i2);

    private native long nativeOpen(int i, boolean z);

    private native void nativeSet(int i, long j, int i2, int i3);

    private native int nativeSize(int i);

    public MemoryIntArray(int size) throws IOException {
        dalvik.system.CloseGuard closeGuard = dalvik.system.CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mFd = -1;
        if (size > 1024) {
            throw new IllegalArgumentException("Max size is 1024");
        }
        this.mIsOwner = true;
        String name = UUID.randomUUID().toString();
        int nativeCreate = nativeCreate(name, size);
        this.mFd = nativeCreate;
        this.mMemoryAddr = nativeOpen(nativeCreate, true);
        closeGuard.open("MemoryIntArray.close");
    }

    private MemoryIntArray(Parcel parcel) throws IOException {
        dalvik.system.CloseGuard closeGuard = dalvik.system.CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mFd = -1;
        this.mIsOwner = false;
        ParcelFileDescriptor pfd = (ParcelFileDescriptor) parcel.readParcelable(null, ParcelFileDescriptor.class);
        if (pfd == null) {
            throw new IOException("No backing file descriptor");
        }
        int detachFd = pfd.detachFd();
        this.mFd = detachFd;
        this.mMemoryAddr = nativeOpen(detachFd, false);
        closeGuard.open("MemoryIntArray.close");
    }

    public boolean isWritable() {
        enforceNotClosed();
        return this.mIsOwner;
    }

    public int get(int index) throws IOException {
        enforceNotClosed();
        enforceValidIndex(index);
        return nativeGet(this.mFd, this.mMemoryAddr, index);
    }

    public void set(int index, int value) throws IOException {
        enforceNotClosed();
        enforceWritable();
        enforceValidIndex(index);
        nativeSet(this.mFd, this.mMemoryAddr, index, value);
    }

    public int size() throws IOException {
        enforceNotClosed();
        return nativeSize(this.mFd);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!isClosed()) {
            nativeClose(this.mFd, this.mMemoryAddr, this.mIsOwner);
            this.mFd = -1;
            this.mCloseGuard.close();
        }
    }

    public boolean isClosed() {
        return this.mFd == -1;
    }

    protected void finalize() throws Throwable {
        try {
            dalvik.system.CloseGuard closeGuard = this.mCloseGuard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            IoUtils.closeQuietly(this);
        } finally {
            super.finalize();
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        try {
            ParcelFileDescriptor pfd = ParcelFileDescriptor.fromFd(this.mFd);
            parcel.writeParcelable(pfd, flags);
            if (pfd != null) {
                pfd.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MemoryIntArray other = (MemoryIntArray) obj;
        if (this.mFd != other.mFd) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mFd;
    }

    private void enforceNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cannot interact with a closed instance");
        }
    }

    private void enforceValidIndex(int index) throws IOException {
        int size = size();
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException(index + " not between 0 and " + (size - 1));
        }
    }

    private void enforceWritable() {
        if (!isWritable()) {
            throw new UnsupportedOperationException("array is not writable");
        }
    }

    public static int getMaxSize() {
        return 1024;
    }
}
