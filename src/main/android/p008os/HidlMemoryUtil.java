package android.p008os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.nio.ByteBuffer;
import java.nio.DirectByteBuffer;
import java.util.ArrayList;
import java.util.List;
/* renamed from: android.os.HidlMemoryUtil */
/* loaded from: classes3.dex */
public final class HidlMemoryUtil {
    private static final String TAG = "HidlMemoryUtil";

    private HidlMemoryUtil() {
    }

    public static HidlMemory byteArrayToHidlMemory(byte[] input) {
        return byteArrayToHidlMemory(input, null);
    }

    public static HidlMemory byteArrayToHidlMemory(byte[] input, String name) {
        Preconditions.checkNotNull(input);
        if (input.length == 0) {
            return new HidlMemory("ashmem", 0L, null);
        }
        try {
            SharedMemory shmem = SharedMemory.create(name != null ? name : "", input.length);
            ByteBuffer buffer = shmem.mapReadWrite();
            buffer.put(input);
            SharedMemory.unmap(buffer);
            HidlMemory sharedMemoryToHidlMemory = sharedMemoryToHidlMemory(shmem);
            if (shmem != null) {
                shmem.close();
            }
            return sharedMemoryToHidlMemory;
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
    }

    public static HidlMemory byteListToHidlMemory(List<Byte> input) {
        return byteListToHidlMemory(input, null);
    }

    public static HidlMemory byteListToHidlMemory(List<Byte> input, String name) {
        Preconditions.checkNotNull(input);
        if (input.isEmpty()) {
            return new HidlMemory("ashmem", 0L, null);
        }
        try {
            SharedMemory shmem = SharedMemory.create(name != null ? name : "", input.size());
            ByteBuffer buffer = shmem.mapReadWrite();
            for (Byte b : input) {
                buffer.put(b.byteValue());
            }
            SharedMemory.unmap(buffer);
            HidlMemory sharedMemoryToHidlMemory = sharedMemoryToHidlMemory(shmem);
            if (shmem != null) {
                shmem.close();
            }
            return sharedMemoryToHidlMemory;
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hidlMemoryToByteArray(HidlMemory mem) {
        Preconditions.checkNotNull(mem);
        Preconditions.checkArgumentInRange(mem.getSize(), 0L, 2147483647L, "Memory size");
        Preconditions.checkArgument(mem.getSize() == 0 || mem.getName().equals("ashmem"), "Unsupported memory type: %s", mem.getName());
        if (mem.getSize() == 0) {
            return new byte[0];
        }
        ByteBuffer buffer = getBuffer(mem);
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    public static ArrayList<Byte> hidlMemoryToByteList(HidlMemory mem) {
        Preconditions.checkNotNull(mem);
        Preconditions.checkArgumentInRange(mem.getSize(), 0L, 2147483647L, "Memory size");
        Preconditions.checkArgument(mem.getSize() == 0 || mem.getName().equals("ashmem"), "Unsupported memory type: %s", mem.getName());
        if (mem.getSize() == 0) {
            return new ArrayList<>();
        }
        ByteBuffer buffer = getBuffer(mem);
        ArrayList<Byte> result = new ArrayList<>(buffer.remaining());
        while (buffer.hasRemaining()) {
            result.add(Byte.valueOf(buffer.get()));
        }
        return result;
    }

    public static HidlMemory sharedMemoryToHidlMemory(SharedMemory shmem) {
        if (shmem == null) {
            return new HidlMemory("ashmem", 0L, null);
        }
        return fileDescriptorToHidlMemory(shmem.getFileDescriptor(), shmem.getSize());
    }

    public static HidlMemory fileDescriptorToHidlMemory(FileDescriptor fd, int size) {
        Preconditions.checkArgument(fd != null || size == 0);
        if (fd == null) {
            return new HidlMemory("ashmem", 0L, null);
        }
        try {
            NativeHandle handle = new NativeHandle(Os.dup(fd), true);
            return new HidlMemory("ashmem", size, handle);
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
    }

    private static ByteBuffer getBuffer(HidlMemory mem) {
        try {
            final int size = (int) mem.getSize();
            if (size == 0) {
                return ByteBuffer.wrap(new byte[0]);
            }
            NativeHandle handle = mem.getHandle();
            final long address = Os.mmap(0L, size, OsConstants.PROT_READ, OsConstants.MAP_SHARED, handle.getFileDescriptor(), 0L);
            return new DirectByteBuffer(size, address, handle.getFileDescriptor(), new Runnable() { // from class: android.os.HidlMemoryUtil$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HidlMemoryUtil.lambda$getBuffer$0(address, size);
                }
            }, true);
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getBuffer$0(long address, int size) {
        try {
            Os.munmap(address, size);
        } catch (ErrnoException e) {
            Log.wtf(TAG, e);
        }
    }
}
