package android.hardware;

import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.time.Duration;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public final class SyncFence implements AutoCloseable, Parcelable {
    public static final long SIGNAL_TIME_INVALID = -1;
    public static final long SIGNAL_TIME_PENDING = Long.MAX_VALUE;
    private final Runnable mCloser;
    private long mNativePtr;
    private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createNonmalloced(SyncFence.class.getClassLoader(), nGetDestructor(), 4);
    public static final Parcelable.Creator<SyncFence> CREATOR = new Parcelable.Creator<SyncFence>() { // from class: android.hardware.SyncFence.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SyncFence createFromParcel(Parcel in) {
            return new SyncFence(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SyncFence[] newArray(int size) {
            return new SyncFence[size];
        }
    };

    private static native long nCreate(int i);

    private static native long nGetDestructor();

    private static native int nGetFd(long j);

    private static native long nGetSignalTime(long j);

    private static native boolean nIsValid(long j);

    private static native boolean nWait(long j, long j2);

    private SyncFence(int fileDescriptor) {
        long nCreate = nCreate(fileDescriptor);
        this.mNativePtr = nCreate;
        this.mCloser = sRegistry.registerNativeAllocation(this, nCreate);
    }

    private SyncFence(Parcel parcel) {
        boolean valid = parcel.readBoolean();
        FileDescriptor fileDescriptor = valid ? parcel.readRawFileDescriptor() : null;
        if (fileDescriptor != null) {
            long nCreate = nCreate(fileDescriptor.getInt$());
            this.mNativePtr = nCreate;
            this.mCloser = sRegistry.registerNativeAllocation(this, nCreate);
            return;
        }
        this.mCloser = new Runnable() { // from class: android.hardware.SyncFence$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SyncFence.lambda$new$0();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$new$0() {
    }

    public SyncFence(long nativeFencePtr) {
        this.mNativePtr = nativeFencePtr;
        if (nativeFencePtr != 0) {
            this.mCloser = sRegistry.registerNativeAllocation(this, nativeFencePtr);
        } else {
            this.mCloser = new Runnable() { // from class: android.hardware.SyncFence$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SyncFence.lambda$new$1();
                }
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$new$1() {
    }

    private SyncFence() {
        this.mCloser = new Runnable() { // from class: android.hardware.SyncFence$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SyncFence.lambda$new$2();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$new$2() {
    }

    public static SyncFence createEmpty() {
        return new SyncFence();
    }

    public static SyncFence create(ParcelFileDescriptor wrapped) {
        return new SyncFence(wrapped.detachFd());
    }

    public static SyncFence adopt(int fileDescriptor) {
        return new SyncFence(fileDescriptor);
    }

    public ParcelFileDescriptor getFdDup() throws IOException {
        ParcelFileDescriptor fromFd;
        synchronized (this.mCloser) {
            long j = this.mNativePtr;
            int fd = j != 0 ? nGetFd(j) : -1;
            if (fd == -1) {
                throw new IllegalStateException("Cannot dup the FD of an invalid SyncFence");
            }
            fromFd = ParcelFileDescriptor.fromFd(fd);
        }
        return fromFd;
    }

    public boolean isValid() {
        boolean z;
        synchronized (this.mCloser) {
            long j = this.mNativePtr;
            z = j != 0 && nIsValid(j);
        }
        return z;
    }

    public boolean await(Duration timeout) {
        long timeoutNanos;
        if (timeout.isNegative()) {
            timeoutNanos = -1;
        } else {
            timeoutNanos = timeout.toNanos();
        }
        return await(timeoutNanos);
    }

    public boolean awaitForever() {
        return await(-1L);
    }

    private boolean await(long timeoutNanos) {
        boolean z;
        synchronized (this.mCloser) {
            long j = this.mNativePtr;
            z = j != 0 && nWait(j, timeoutNanos);
        }
        return z;
    }

    public long getSignalTime() {
        long nGetSignalTime;
        synchronized (this.mCloser) {
            long j = this.mNativePtr;
            nGetSignalTime = j != 0 ? nGetSignalTime(j) : -1L;
        }
        return nGetSignalTime;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this.mCloser) {
            if (this.mNativePtr == 0) {
                return;
            }
            this.mNativePtr = 0L;
            this.mCloser.run();
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 1;
    }

    public Object getLock() {
        return this.mCloser;
    }

    public long getNativeFence() {
        return this.mNativePtr;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        synchronized (this.mCloser) {
            long j = this.mNativePtr;
            int fd = j != 0 ? nGetFd(j) : -1;
            if (fd == -1) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                FileDescriptor temp = new FileDescriptor();
                temp.setInt$(fd);
                out.writeFileDescriptor(temp);
            }
        }
    }
}
