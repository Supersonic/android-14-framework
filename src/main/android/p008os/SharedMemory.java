package android.p008os;

import android.p008os.Parcelable;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import dalvik.system.VMRuntime;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DirectByteBuffer;
import java.nio.NioUtils;
import sun.misc.Cleaner;
/* renamed from: android.os.SharedMemory */
/* loaded from: classes3.dex */
public final class SharedMemory implements Parcelable, Closeable {
    private Cleaner mCleaner;
    private final FileDescriptor mFileDescriptor;
    private final MemoryRegistration mMemoryRegistration;
    private final int mSize;
    private static final int PROT_MASK = ((OsConstants.PROT_READ | OsConstants.PROT_WRITE) | OsConstants.PROT_EXEC) | OsConstants.PROT_NONE;
    public static final Parcelable.Creator<SharedMemory> CREATOR = new Parcelable.Creator<SharedMemory>() { // from class: android.os.SharedMemory.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SharedMemory createFromParcel(Parcel source) {
            FileDescriptor descriptor = source.readRawFileDescriptor();
            return new SharedMemory(descriptor);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SharedMemory[] newArray(int size) {
            return new SharedMemory[size];
        }
    };

    private static native FileDescriptor nCreate(String str, int i) throws ErrnoException;

    private static native int nGetSize(FileDescriptor fileDescriptor);

    private static native int nSetProt(FileDescriptor fileDescriptor, int i);

    private SharedMemory(FileDescriptor fd) {
        if (fd == null) {
            throw new IllegalArgumentException("Unable to create SharedMemory from a null FileDescriptor");
        }
        if (!fd.valid()) {
            throw new IllegalArgumentException("Unable to create SharedMemory from closed FileDescriptor");
        }
        this.mFileDescriptor = fd;
        int nGetSize = nGetSize(fd);
        this.mSize = nGetSize;
        if (nGetSize <= 0) {
            throw new IllegalArgumentException("FileDescriptor is not a valid ashmem fd");
        }
        MemoryRegistration memoryRegistration = new MemoryRegistration(nGetSize);
        this.mMemoryRegistration = memoryRegistration;
        this.mCleaner = Cleaner.create(fd, new Closer(fd.getInt$(), memoryRegistration));
    }

    public static SharedMemory create(String name, int size) throws ErrnoException {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        return new SharedMemory(nCreate(name, size));
    }

    private void checkOpen() {
        if (!this.mFileDescriptor.valid()) {
            throw new IllegalStateException("SharedMemory is closed");
        }
    }

    public static SharedMemory fromFileDescriptor(ParcelFileDescriptor fd) {
        FileDescriptor f = new FileDescriptor();
        f.setInt$(fd.detachFd());
        return new SharedMemory(f);
    }

    private static void validateProt(int prot) {
        if (((~PROT_MASK) & prot) != 0) {
            throw new IllegalArgumentException("Invalid prot value");
        }
    }

    public boolean setProtect(int prot) {
        checkOpen();
        validateProt(prot);
        int errno = nSetProt(this.mFileDescriptor, prot);
        return errno == 0;
    }

    public FileDescriptor getFileDescriptor() {
        return this.mFileDescriptor;
    }

    public int getFd() {
        return this.mFileDescriptor.getInt$();
    }

    public int getSize() {
        checkOpen();
        return this.mSize;
    }

    public ByteBuffer mapReadWrite() throws ErrnoException {
        return map(OsConstants.PROT_READ | OsConstants.PROT_WRITE, 0, this.mSize);
    }

    public ByteBuffer mapReadOnly() throws ErrnoException {
        return map(OsConstants.PROT_READ, 0, this.mSize);
    }

    public ByteBuffer map(int prot, int offset, int length) throws ErrnoException {
        checkOpen();
        validateProt(prot);
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be >= 0");
        }
        if (length > 0) {
            if (offset + length > this.mSize) {
                throw new IllegalArgumentException("offset + length must not exceed getSize()");
            }
            long address = Os.mmap(0L, length, prot, OsConstants.MAP_SHARED, this.mFileDescriptor, offset);
            boolean readOnly = (prot & OsConstants.PROT_WRITE) == 0;
            Runnable unmapper = new Unmapper(address, length, this.mMemoryRegistration.acquire());
            return new DirectByteBuffer(length, address, this.mFileDescriptor, unmapper, readOnly);
        }
        throw new IllegalArgumentException("Length must be > 0");
    }

    public static void unmap(ByteBuffer buffer) {
        if (buffer instanceof DirectByteBuffer) {
            NioUtils.freeDirectBuffer(buffer);
            return;
        }
        throw new IllegalArgumentException("ByteBuffer wasn't created by #map(int, int, int); can't unmap");
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mFileDescriptor.setInt$(-1);
        Cleaner cleaner = this.mCleaner;
        if (cleaner != null) {
            cleaner.clean();
            this.mCleaner = null;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        checkOpen();
        dest.writeFileDescriptor(this.mFileDescriptor);
    }

    public ParcelFileDescriptor getFdDup() throws IOException {
        return ParcelFileDescriptor.dup(this.mFileDescriptor);
    }

    /* renamed from: android.os.SharedMemory$Closer */
    /* loaded from: classes3.dex */
    private static final class Closer implements Runnable {
        private int mFd;
        private MemoryRegistration mMemoryReference;

        private Closer(int fd, MemoryRegistration memoryReference) {
            this.mFd = fd;
            this.mMemoryReference = memoryReference;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                FileDescriptor fd = new FileDescriptor();
                fd.setInt$(this.mFd);
                Os.close(fd);
            } catch (ErrnoException e) {
            }
            this.mMemoryReference.release();
            this.mMemoryReference = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.SharedMemory$Unmapper */
    /* loaded from: classes3.dex */
    public static final class Unmapper implements Runnable {
        private long mAddress;
        private MemoryRegistration mMemoryReference;
        private int mSize;

        private Unmapper(long address, int size, MemoryRegistration memoryReference) {
            this.mAddress = address;
            this.mSize = size;
            this.mMemoryReference = memoryReference;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                Os.munmap(this.mAddress, this.mSize);
            } catch (ErrnoException e) {
            }
            this.mMemoryReference.release();
            this.mMemoryReference = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.SharedMemory$MemoryRegistration */
    /* loaded from: classes3.dex */
    public static final class MemoryRegistration {
        private int mReferenceCount;
        private int mSize;

        private MemoryRegistration(int size) {
            this.mSize = size;
            this.mReferenceCount = 1;
            VMRuntime.getRuntime().registerNativeAllocation(this.mSize);
        }

        public synchronized MemoryRegistration acquire() {
            this.mReferenceCount++;
            return this;
        }

        public synchronized void release() {
            int i = this.mReferenceCount - 1;
            this.mReferenceCount = i;
            if (i == 0) {
                VMRuntime.getRuntime().registerNativeFree(this.mSize);
            }
        }
    }
}
