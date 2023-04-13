package android.p008os;

import android.app.AppOpsManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.ExceptionUtils;
import android.util.Log;
import android.util.MathUtils;
import android.util.Pair;
import android.util.Size;
import android.util.SizeF;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import libcore.util.SneakyThrow;
/* renamed from: android.os.Parcel */
/* loaded from: classes3.dex */
public final class Parcel {
    private static final boolean DEBUG_ARRAY_MAP = false;
    private static final boolean DEBUG_RECYCLE = false;
    private static final int EX_BAD_PARCELABLE = -2;
    public static final int EX_HAS_NOTED_APPOPS_REPLY_HEADER = -127;
    private static final int EX_HAS_STRICTMODE_REPLY_HEADER = -128;
    private static final int EX_ILLEGAL_ARGUMENT = -3;
    private static final int EX_ILLEGAL_STATE = -5;
    private static final int EX_NETWORK_MAIN_THREAD = -6;
    private static final int EX_NULL_POINTER = -4;
    private static final int EX_PARCELABLE = -9;
    private static final int EX_SECURITY = -1;
    private static final int EX_SERVICE_SPECIFIC = -8;
    private static final int EX_TRANSACTION_FAILED = -129;
    private static final int EX_UNSUPPORTED_OPERATION = -7;
    public static final int FLAG_IS_REPLY_FROM_BLOCKING_ALLOWED_OBJECT = 1;
    public static final int FLAG_PROPAGATE_ALLOW_BLOCKING = 2;

    /* renamed from: OK */
    private static final int f320OK = 0;
    private static final int POOL_SIZE = 32;
    private static final String TAG = "Parcel";
    private static final int VAL_BOOLEAN = 9;
    private static final int VAL_BOOLEANARRAY = 23;
    private static final int VAL_BUNDLE = 3;
    private static final int VAL_BYTE = 20;
    private static final int VAL_BYTEARRAY = 13;
    private static final int VAL_CHAR = 29;
    private static final int VAL_CHARARRAY = 31;
    private static final int VAL_CHARSEQUENCE = 10;
    private static final int VAL_CHARSEQUENCEARRAY = 24;
    private static final int VAL_DOUBLE = 8;
    private static final int VAL_DOUBLEARRAY = 28;
    private static final int VAL_FLOAT = 7;
    private static final int VAL_FLOATARRAY = 32;
    private static final int VAL_IBINDER = 15;
    private static final int VAL_INTARRAY = 18;
    private static final int VAL_INTEGER = 1;
    private static final int VAL_LIST = 11;
    private static final int VAL_LONG = 6;
    private static final int VAL_LONGARRAY = 19;
    private static final int VAL_MAP = 2;
    private static final int VAL_NULL = -1;
    private static final int VAL_OBJECTARRAY = 17;
    private static final int VAL_PARCELABLE = 4;
    private static final int VAL_PARCELABLEARRAY = 16;
    private static final int VAL_PERSISTABLEBUNDLE = 25;
    private static final int VAL_SERIALIZABLE = 21;
    private static final int VAL_SHORT = 5;
    private static final int VAL_SHORTARRAY = 30;
    private static final int VAL_SIZE = 26;
    private static final int VAL_SIZEF = 27;
    private static final int VAL_SPARSEARRAY = 12;
    private static final int VAL_SPARSEBOOLEANARRAY = 22;
    private static final int VAL_STRING = 0;
    private static final int VAL_STRINGARRAY = 14;
    private static final int WRITE_EXCEPTION_STACK_TRACE_THRESHOLD_MS = 1000;
    private static Parcel sHolderPool;
    private static volatile long sLastWriteExceptionStackTrace;
    private static Parcel sOwnedPool;
    private static boolean sParcelExceptionStackTrace;
    private ArrayMap<Class, Object> mClassCookies;
    private int mFlags;
    private long mNativePtr;
    private long mNativeSize;
    private boolean mOwnsNativeParcelObject;
    private Parcel mPoolNext;
    private SparseArray<Parcelable> mReadSquashableParcelables;
    private RuntimeException mStack;
    private ArrayMap<Parcelable, Integer> mWrittenSquashableParcelables;
    private static final Object sPoolSync = new Object();
    private static int sOwnedPoolSize = 0;
    private static int sHolderPoolSize = 0;
    public static final Parcelable.Creator<String> STRING_CREATOR = new Parcelable.Creator<String>() { // from class: android.os.Parcel.1
        @Override // android.p008os.Parcelable.Creator
        public String createFromParcel(Parcel source) {
            return source.readString();
        }

        @Override // android.p008os.Parcelable.Creator
        public String[] newArray(int size) {
            return new String[size];
        }
    };
    private static final HashMap<ClassLoader, HashMap<String, Parcelable.Creator<?>>> mCreators = new HashMap<>();
    private static final HashMap<ClassLoader, HashMap<String, Pair<Parcelable.Creator<?>, Class<?>>>> sPairedCreators = new HashMap<>();
    private boolean mRecycled = false;
    private ReadWriteHelper mReadWriteHelper = ReadWriteHelper.DEFAULT;
    private boolean mAllowSquashing = false;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.Parcel$ParcelFlags */
    /* loaded from: classes3.dex */
    public @interface ParcelFlags {
    }

    /* renamed from: android.os.Parcel$SquashReadHelper */
    /* loaded from: classes3.dex */
    public interface SquashReadHelper<T> {
        T readRawParceled(Parcel parcel);
    }

    public static native long getGlobalAllocCount();

    public static native long getGlobalAllocSize();

    private static native void nativeAppendFrom(long j, long j2, int i, int i2);

    private static native int nativeCompareData(long j, long j2);

    private static native boolean nativeCompareDataInRange(long j, int i, long j2, int i2, int i3);

    private static native long nativeCreate();

    private static native byte[] nativeCreateByteArray(long j);

    @CriticalNative
    private static native int nativeDataAvail(long j);

    @CriticalNative
    private static native int nativeDataCapacity(long j);

    @CriticalNative
    private static native int nativeDataPosition(long j);

    @CriticalNative
    private static native int nativeDataSize(long j);

    private static native void nativeDestroy(long j);

    private static native void nativeEnforceInterface(long j, String str);

    private static native void nativeFreeBuffer(long j);

    @CriticalNative
    private static native long nativeGetOpenAshmemSize(long j);

    @CriticalNative
    private static native boolean nativeHasFileDescriptors(long j);

    private static native boolean nativeHasFileDescriptorsInRange(long j, int i, int i2);

    @CriticalNative
    private static native boolean nativeIsForRpc(long j);

    @FastNative
    private static native void nativeMarkForBinder(long j, IBinder iBinder);

    @CriticalNative
    private static native void nativeMarkSensitive(long j);

    private static native byte[] nativeMarshall(long j);

    @CriticalNative
    private static native boolean nativePushAllowFds(long j, boolean z);

    private static native byte[] nativeReadBlob(long j);

    private static native boolean nativeReadByteArray(long j, byte[] bArr, int i);

    @CriticalNative
    private static native int nativeReadCallingWorkSourceUid(long j);

    @CriticalNative
    private static native double nativeReadDouble(long j);

    @FastNative
    private static native FileDescriptor nativeReadFileDescriptor(long j);

    @CriticalNative
    private static native float nativeReadFloat(long j);

    @CriticalNative
    private static native int nativeReadInt(long j);

    @CriticalNative
    private static native long nativeReadLong(long j);

    @FastNative
    private static native String nativeReadString16(long j);

    @FastNative
    private static native String nativeReadString8(long j);

    @FastNative
    private static native IBinder nativeReadStrongBinder(long j);

    @CriticalNative
    private static native boolean nativeReplaceCallingWorkSourceUid(long j, int i);

    @CriticalNative
    private static native void nativeRestoreAllowFds(long j, boolean z);

    @FastNative
    private static native void nativeSetDataCapacity(long j, int i);

    @CriticalNative
    private static native void nativeSetDataPosition(long j, int i);

    @FastNative
    private static native void nativeSetDataSize(long j, int i);

    private static native void nativeSignalExceptionForError(int i);

    private static native void nativeUnmarshall(long j, byte[] bArr, int i, int i2);

    private static native void nativeWriteBlob(long j, byte[] bArr, int i, int i2);

    private static native void nativeWriteByteArray(long j, byte[] bArr, int i, int i2);

    @CriticalNative
    private static native int nativeWriteDouble(long j, double d);

    @FastNative
    private static native void nativeWriteFileDescriptor(long j, FileDescriptor fileDescriptor);

    @CriticalNative
    private static native int nativeWriteFloat(long j, float f);

    @CriticalNative
    private static native int nativeWriteInt(long j, int i);

    private static native void nativeWriteInterfaceToken(long j, String str);

    @CriticalNative
    private static native int nativeWriteLong(long j, long j2);

    @FastNative
    private static native void nativeWriteString16(long j, String str);

    @FastNative
    private static native void nativeWriteString8(long j, String str);

    @FastNative
    private static native void nativeWriteStrongBinder(long j, IBinder iBinder);

    /* renamed from: android.os.Parcel$ReadWriteHelper */
    /* loaded from: classes3.dex */
    public static class ReadWriteHelper {
        public static final ReadWriteHelper DEFAULT = new ReadWriteHelper();

        public void writeString8(Parcel p, String s) {
            p.writeString8NoHelper(s);
        }

        public void writeString16(Parcel p, String s) {
            p.writeString16NoHelper(s);
        }

        public String readString8(Parcel p) {
            return p.readString8NoHelper();
        }

        public String readString16(Parcel p) {
            return p.readString16NoHelper();
        }
    }

    public static Parcel obtain() {
        Parcel res = null;
        synchronized (sPoolSync) {
            Parcel parcel = sOwnedPool;
            if (parcel != null) {
                res = parcel;
                sOwnedPool = res.mPoolNext;
                res.mPoolNext = null;
                sOwnedPoolSize--;
            }
        }
        if (res == null) {
            Parcel res2 = new Parcel(0L);
            return res2;
        }
        res.mRecycled = false;
        res.mReadWriteHelper = ReadWriteHelper.DEFAULT;
        return res;
    }

    public static Parcel obtain(IBinder binder) {
        Parcel parcel = obtain();
        parcel.markForBinder(binder);
        return parcel;
    }

    public final void recycle() {
        if (this.mRecycled) {
            Log.wtf(TAG, "Recycle called on unowned Parcel. (recycle twice?) Here: " + Log.getStackTraceString(new Throwable()) + " Original recycle call (if DEBUG_RECYCLE): ", this.mStack);
            return;
        }
        this.mRecycled = true;
        this.mClassCookies = null;
        freeBuffer();
        if (this.mOwnsNativeParcelObject) {
            synchronized (sPoolSync) {
                int i = sOwnedPoolSize;
                if (i < 32) {
                    this.mPoolNext = sOwnedPool;
                    sOwnedPool = this;
                    sOwnedPoolSize = i + 1;
                }
            }
            return;
        }
        this.mNativePtr = 0L;
        synchronized (sPoolSync) {
            int i2 = sHolderPoolSize;
            if (i2 < 32) {
                this.mPoolNext = sHolderPool;
                sHolderPool = this;
                sHolderPoolSize = i2 + 1;
            }
        }
    }

    public void setReadWriteHelper(ReadWriteHelper helper) {
        this.mReadWriteHelper = helper != null ? helper : ReadWriteHelper.DEFAULT;
    }

    public boolean hasReadWriteHelper() {
        ReadWriteHelper readWriteHelper = this.mReadWriteHelper;
        return (readWriteHelper == null || readWriteHelper == ReadWriteHelper.DEFAULT) ? false : true;
    }

    public final void markSensitive() {
        nativeMarkSensitive(this.mNativePtr);
    }

    private void markForBinder(IBinder binder) {
        nativeMarkForBinder(this.mNativePtr, binder);
    }

    public final boolean isForRpc() {
        return nativeIsForRpc(this.mNativePtr);
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    public void addFlags(int flags) {
        this.mFlags |= flags;
    }

    private boolean hasFlags(int flags) {
        return (this.mFlags & flags) == flags;
    }

    public void setPropagateAllowBlocking() {
        addFlags(2);
    }

    public int dataSize() {
        return nativeDataSize(this.mNativePtr);
    }

    public final int dataAvail() {
        return nativeDataAvail(this.mNativePtr);
    }

    public final int dataPosition() {
        return nativeDataPosition(this.mNativePtr);
    }

    public final int dataCapacity() {
        return nativeDataCapacity(this.mNativePtr);
    }

    public final void setDataSize(int size) {
        nativeSetDataSize(this.mNativePtr, size);
    }

    public final void setDataPosition(int pos) {
        nativeSetDataPosition(this.mNativePtr, pos);
    }

    public final void setDataCapacity(int size) {
        nativeSetDataCapacity(this.mNativePtr, size);
    }

    public final boolean pushAllowFds(boolean allowFds) {
        return nativePushAllowFds(this.mNativePtr, allowFds);
    }

    public final void restoreAllowFds(boolean lastValue) {
        nativeRestoreAllowFds(this.mNativePtr, lastValue);
    }

    public final byte[] marshall() {
        return nativeMarshall(this.mNativePtr);
    }

    public final void unmarshall(byte[] data, int offset, int length) {
        nativeUnmarshall(this.mNativePtr, data, offset, length);
    }

    public final void appendFrom(Parcel parcel, int offset, int length) {
        nativeAppendFrom(this.mNativePtr, parcel.mNativePtr, offset, length);
    }

    public int compareData(Parcel other) {
        return nativeCompareData(this.mNativePtr, other.mNativePtr);
    }

    public static boolean compareData(Parcel a, int offsetA, Parcel b, int offsetB, int length) {
        return nativeCompareDataInRange(a.mNativePtr, offsetA, b.mNativePtr, offsetB, length);
    }

    public final void setClassCookie(Class clz, Object cookie) {
        if (this.mClassCookies == null) {
            this.mClassCookies = new ArrayMap<>();
        }
        this.mClassCookies.put(clz, cookie);
    }

    public final Object getClassCookie(Class clz) {
        ArrayMap<Class, Object> arrayMap = this.mClassCookies;
        if (arrayMap != null) {
            return arrayMap.get(clz);
        }
        return null;
    }

    public final void adoptClassCookies(Parcel from) {
        this.mClassCookies = from.mClassCookies;
    }

    public Map<Class, Object> copyClassCookies() {
        return new ArrayMap(this.mClassCookies);
    }

    public void putClassCookies(Map<Class, Object> cookies) {
        if (cookies == null) {
            return;
        }
        if (this.mClassCookies == null) {
            this.mClassCookies = new ArrayMap<>();
        }
        this.mClassCookies.putAll(cookies);
    }

    public boolean hasFileDescriptors() {
        return nativeHasFileDescriptors(this.mNativePtr);
    }

    public boolean hasFileDescriptors(int offset, int length) {
        return nativeHasFileDescriptorsInRange(this.mNativePtr, offset, length);
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x0070  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean hasFileDescriptors(Object value) {
        if (value instanceof Parcel) {
            Parcel parcel = (Parcel) value;
            return parcel.hasFileDescriptors();
        } else if (value instanceof LazyValue) {
            LazyValue lazy = (LazyValue) value;
            return lazy.hasFileDescriptors();
        } else if (value instanceof Parcelable) {
            Parcelable parcelable = (Parcelable) value;
            return (parcelable.describeContents() & 1) != 0;
        } else if (value instanceof ArrayMap) {
            ArrayMap<?, ?> map = (ArrayMap) value;
            int n = map.size();
            for (int i = 0; i < n; i++) {
                if (hasFileDescriptors(map.keyAt(i)) || hasFileDescriptors(map.valueAt(i))) {
                    return true;
                }
            }
            return false;
        } else if (value instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map) value).entrySet()) {
                if (hasFileDescriptors(entry.getKey()) || hasFileDescriptors(entry.getValue())) {
                    return true;
                }
                while (r2.hasNext()) {
                }
            }
            return false;
        } else if (value instanceof List) {
            List<?> list = (List) value;
            int n2 = list.size();
            for (int i2 = 0; i2 < n2; i2++) {
                if (hasFileDescriptors(list.get(i2))) {
                    return true;
                }
            }
            return false;
        } else if (value instanceof SparseArray) {
            SparseArray<?> array = (SparseArray) value;
            int n3 = array.size();
            for (int i3 = 0; i3 < n3; i3++) {
                if (hasFileDescriptors(array.valueAt(i3))) {
                    return true;
                }
            }
            return false;
        } else if (value instanceof Object[]) {
            for (Object obj : (Object[]) value) {
                if (hasFileDescriptors(obj)) {
                    return true;
                }
            }
            return false;
        } else {
            getValueType(value);
            return false;
        }
    }

    public final void writeInterfaceToken(String interfaceName) {
        nativeWriteInterfaceToken(this.mNativePtr, interfaceName);
    }

    public final void enforceInterface(String interfaceName) {
        nativeEnforceInterface(this.mNativePtr, interfaceName);
    }

    public void enforceNoDataAvail() {
        int n = dataAvail();
        if (n > 0) {
            throw new BadParcelableException("Parcel data not fully consumed, unread size: " + n);
        }
    }

    public boolean replaceCallingWorkSourceUid(int workSourceUid) {
        return nativeReplaceCallingWorkSourceUid(this.mNativePtr, workSourceUid);
    }

    public int readCallingWorkSourceUid() {
        return nativeReadCallingWorkSourceUid(this.mNativePtr);
    }

    public final void writeByteArray(byte[] b) {
        writeByteArray(b, 0, b != null ? b.length : 0);
    }

    public final void writeByteArray(byte[] b, int offset, int len) {
        if (b == null) {
            writeInt(-1);
            return;
        }
        ArrayUtils.throwsIfOutOfBounds(b.length, offset, len);
        nativeWriteByteArray(this.mNativePtr, b, offset, len);
    }

    public final void writeBlob(byte[] b) {
        writeBlob(b, 0, b != null ? b.length : 0);
    }

    public final void writeBlob(byte[] b, int offset, int len) {
        if (b == null) {
            writeInt(-1);
            return;
        }
        ArrayUtils.throwsIfOutOfBounds(b.length, offset, len);
        nativeWriteBlob(this.mNativePtr, b, offset, len);
    }

    public final void writeInt(int val) {
        int err = nativeWriteInt(this.mNativePtr, val);
        if (err != 0) {
            nativeSignalExceptionForError(err);
        }
    }

    public final void writeLong(long val) {
        int err = nativeWriteLong(this.mNativePtr, val);
        if (err != 0) {
            nativeSignalExceptionForError(err);
        }
    }

    public final void writeFloat(float val) {
        int err = nativeWriteFloat(this.mNativePtr, val);
        if (err != 0) {
            nativeSignalExceptionForError(err);
        }
    }

    public final void writeDouble(double val) {
        int err = nativeWriteDouble(this.mNativePtr, val);
        if (err != 0) {
            nativeSignalExceptionForError(err);
        }
    }

    public final void writeString(String val) {
        writeString16(val);
    }

    public final void writeString8(String val) {
        this.mReadWriteHelper.writeString8(this, val);
    }

    public final void writeString16(String val) {
        this.mReadWriteHelper.writeString16(this, val);
    }

    public void writeStringNoHelper(String val) {
        writeString16NoHelper(val);
    }

    public void writeString8NoHelper(String val) {
        nativeWriteString8(this.mNativePtr, val);
    }

    public void writeString16NoHelper(String val) {
        nativeWriteString16(this.mNativePtr, val);
    }

    public final void writeBoolean(boolean val) {
        writeInt(val ? 1 : 0);
    }

    public final void writeCharSequence(CharSequence val) {
        TextUtils.writeToParcel(val, this, 0);
    }

    public final void writeStrongBinder(IBinder val) {
        nativeWriteStrongBinder(this.mNativePtr, val);
    }

    public final void writeStrongInterface(IInterface val) {
        writeStrongBinder(val == null ? null : val.asBinder());
    }

    public final void writeFileDescriptor(FileDescriptor val) {
        nativeWriteFileDescriptor(this.mNativePtr, val);
    }

    public final void writeRawFileDescriptor(FileDescriptor val) {
        nativeWriteFileDescriptor(this.mNativePtr, val);
    }

    public final void writeRawFileDescriptorArray(FileDescriptor[] value) {
        if (value != null) {
            int N = value.length;
            writeInt(N);
            for (FileDescriptor fileDescriptor : value) {
                writeRawFileDescriptor(fileDescriptor);
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeByte(byte val) {
        writeInt(val);
    }

    public final void writeMap(Map val) {
        writeMapInternal(val);
    }

    void writeMapInternal(Map<String, Object> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        Set<Map.Entry<String, Object>> entries = val.entrySet();
        int size = entries.size();
        writeInt(size);
        for (Map.Entry<String, Object> e : entries) {
            writeValue(e.getKey());
            writeValue(e.getValue());
            size--;
        }
        if (size != 0) {
            throw new BadParcelableException("Map size does not match number of entries!");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeArrayMapInternal(ArrayMap<String, Object> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeString(val.keyAt(i));
            writeValue(val.valueAt(i));
        }
    }

    public void writeArrayMap(ArrayMap<String, Object> val) {
        writeArrayMapInternal(val);
    }

    public <T extends Parcelable> void writeTypedArrayMap(ArrayMap<String, T> val, int parcelableFlags) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int count = val.size();
        writeInt(count);
        for (int i = 0; i < count; i++) {
            writeString(val.keyAt(i));
            writeTypedObject(val.valueAt(i), parcelableFlags);
        }
    }

    public void writeArraySet(ArraySet<? extends Object> val) {
        int size = val != null ? val.size() : -1;
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeValue(val.valueAt(i));
        }
    }

    public final void writeBundle(Bundle val) {
        if (val == null) {
            writeInt(-1);
        } else {
            val.writeToParcel(this, 0);
        }
    }

    public final void writePersistableBundle(PersistableBundle val) {
        if (val == null) {
            writeInt(-1);
        } else {
            val.writeToParcel(this, 0);
        }
    }

    public final void writeSize(Size val) {
        writeInt(val.getWidth());
        writeInt(val.getHeight());
    }

    public final void writeSizeF(SizeF val) {
        writeFloat(val.getWidth());
        writeFloat(val.getHeight());
    }

    public final void writeList(List val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeValue(val.get(i));
        }
    }

    public final void writeArray(Object[] val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.length;
        writeInt(N);
        for (Object obj : val) {
            writeValue(obj);
        }
    }

    public final <T> void writeSparseArray(SparseArray<T> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeInt(val.keyAt(i));
            writeValue(val.valueAt(i));
        }
    }

    public final void writeSparseBooleanArray(SparseBooleanArray val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeInt(val.keyAt(i));
            writeByte(val.valueAt(i) ? (byte) 1 : (byte) 0);
        }
    }

    public final void writeSparseIntArray(SparseIntArray val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeInt(val.keyAt(i));
            writeInt(val.valueAt(i));
        }
    }

    public final void writeBooleanArray(boolean[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (boolean z : val) {
                writeInt(z ? 1 : 0);
            }
            return;
        }
        writeInt(-1);
    }

    public final boolean[] createBooleanArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            boolean[] val = new boolean[N];
            for (int i = 0; i < N; i++) {
                val[i] = readInt() != 0;
            }
            return val;
        }
        return null;
    }

    public final void readBooleanArray(boolean[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readInt() != 0;
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public void writeShortArray(short[] val) {
        if (val != null) {
            int n = val.length;
            writeInt(n);
            for (short s : val) {
                writeInt(s);
            }
            return;
        }
        writeInt(-1);
    }

    public short[] createShortArray() {
        int n = readInt();
        if (n >= 0 && n <= (dataAvail() >> 2)) {
            short[] val = new short[n];
            for (int i = 0; i < n; i++) {
                val[i] = (short) readInt();
            }
            return val;
        }
        return null;
    }

    public void readShortArray(short[] val) {
        int n = readInt();
        if (n == val.length) {
            for (int i = 0; i < n; i++) {
                val[i] = (short) readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeCharArray(char[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (char c : val) {
                writeInt(c);
            }
            return;
        }
        writeInt(-1);
    }

    public final char[] createCharArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            char[] val = new char[N];
            for (int i = 0; i < N; i++) {
                val[i] = (char) readInt();
            }
            return val;
        }
        return null;
    }

    public final void readCharArray(char[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = (char) readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeIntArray(int[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i : val) {
                writeInt(i);
            }
            return;
        }
        writeInt(-1);
    }

    public final int[] createIntArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            int[] val = new int[N];
            for (int i = 0; i < N; i++) {
                val[i] = readInt();
            }
            return val;
        }
        return null;
    }

    public final void readIntArray(int[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeLongArray(long[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (long j : val) {
                writeLong(j);
            }
            return;
        }
        writeInt(-1);
    }

    public final long[] createLongArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 3)) {
            long[] val = new long[N];
            for (int i = 0; i < N; i++) {
                val[i] = readLong();
            }
            return val;
        }
        return null;
    }

    public final void readLongArray(long[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readLong();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeFloatArray(float[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (float f : val) {
                writeFloat(f);
            }
            return;
        }
        writeInt(-1);
    }

    public final float[] createFloatArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            float[] val = new float[N];
            for (int i = 0; i < N; i++) {
                val[i] = readFloat();
            }
            return val;
        }
        return null;
    }

    public final void readFloatArray(float[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readFloat();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeDoubleArray(double[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (double d : val) {
                writeDouble(d);
            }
            return;
        }
        writeInt(-1);
    }

    public final double[] createDoubleArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 3)) {
            double[] val = new double[N];
            for (int i = 0; i < N; i++) {
                val[i] = readDouble();
            }
            return val;
        }
        return null;
    }

    public final void readDoubleArray(double[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readDouble();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeStringArray(String[] val) {
        writeString16Array(val);
    }

    public final String[] createStringArray() {
        return createString16Array();
    }

    public final void readStringArray(String[] val) {
        readString16Array(val);
    }

    public final void writeString8Array(String[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (String str : val) {
                writeString8(str);
            }
            return;
        }
        writeInt(-1);
    }

    public final String[] createString8Array() {
        int N = readInt();
        if (N >= 0) {
            String[] val = new String[N];
            for (int i = 0; i < N; i++) {
                val[i] = readString8();
            }
            return val;
        }
        return null;
    }

    public final void readString8Array(String[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readString8();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeString16Array(String[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (String str : val) {
                writeString16(str);
            }
            return;
        }
        writeInt(-1);
    }

    public final String[] createString16Array() {
        int N = readInt();
        if (N >= 0) {
            String[] val = new String[N];
            for (int i = 0; i < N; i++) {
                val[i] = readString16();
            }
            return val;
        }
        return null;
    }

    public final void readString16Array(String[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readString16();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeBinderArray(IBinder[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (IBinder iBinder : val) {
                writeStrongBinder(iBinder);
            }
            return;
        }
        writeInt(-1);
    }

    public final <T extends IInterface> void writeInterfaceArray(T[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (T t : val) {
                writeStrongInterface(t);
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeCharSequenceArray(CharSequence[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (CharSequence charSequence : val) {
                writeCharSequence(charSequence);
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeCharSequenceList(ArrayList<CharSequence> val) {
        if (val != null) {
            int N = val.size();
            writeInt(N);
            for (int i = 0; i < N; i++) {
                writeCharSequence(val.get(i));
            }
            return;
        }
        writeInt(-1);
    }

    public final IBinder[] createBinderArray() {
        int N = readInt();
        if (N >= 0) {
            IBinder[] val = new IBinder[N];
            for (int i = 0; i < N; i++) {
                val[i] = readStrongBinder();
            }
            return val;
        }
        return null;
    }

    public final void readBinderArray(IBinder[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readStrongBinder();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final <T extends IInterface> T[] createInterfaceArray(IntFunction<T[]> newArray, Function<IBinder, T> asInterface) {
        int N = readInt();
        if (N >= 0) {
            T[] val = newArray.apply(N);
            for (int i = 0; i < N; i++) {
                val[i] = asInterface.apply(readStrongBinder());
            }
            return val;
        }
        return null;
    }

    public final <T extends IInterface> void readInterfaceArray(T[] val, Function<IBinder, T> asInterface) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = asInterface.apply(readStrongBinder());
            }
            return;
        }
        throw new BadParcelableException("bad array lengths");
    }

    public final <T extends Parcelable> void writeTypedList(List<T> val) {
        writeTypedList(val, 0);
    }

    public final <T extends Parcelable> void writeTypedSparseArray(SparseArray<T> val, int parcelableFlags) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int count = val.size();
        writeInt(count);
        for (int i = 0; i < count; i++) {
            writeInt(val.keyAt(i));
            writeTypedObject(val.valueAt(i), parcelableFlags);
        }
    }

    public <T extends Parcelable> void writeTypedList(List<T> val, int parcelableFlags) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeTypedObject(val.get(i), parcelableFlags);
        }
    }

    public final void writeStringList(List<String> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeString(val.get(i));
        }
    }

    public final void writeBinderList(List<IBinder> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeStrongBinder(val.get(i));
        }
    }

    public final <T extends IInterface> void writeInterfaceList(List<T> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeStrongInterface(val.get(i));
        }
    }

    public final <T extends Parcelable> void writeParcelableList(List<T> val, int flags) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeParcelable(val.get(i), flags);
        }
    }

    public final <T extends Parcelable> void writeTypedArray(T[] val, int parcelableFlags) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (T t : val) {
                writeTypedObject(t, parcelableFlags);
            }
            return;
        }
        writeInt(-1);
    }

    public final <T extends Parcelable> void writeTypedObject(T val, int parcelableFlags) {
        if (val != null) {
            writeInt(1);
            val.writeToParcel(this, parcelableFlags);
            return;
        }
        writeInt(0);
    }

    public <T> void writeFixedArray(T val, int parcelableFlags, int... dimensions) {
        if (val == null) {
            writeInt(-1);
        } else {
            writeFixedArrayInternal(val, parcelableFlags, 0, dimensions);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> void writeFixedArrayInternal(T val, int parcelableFlags, int index, int[] dimensions) {
        if (index >= dimensions.length) {
            throw new BadParcelableException("Array has more dimensions than expected: " + dimensions.length);
        }
        int length = dimensions[index];
        if (val == null) {
            throw new BadParcelableException("Non-null array shouldn't have a null array.");
        }
        if (!val.getClass().isArray()) {
            throw new BadParcelableException("Not an array: " + val);
        }
        if (Array.getLength(val) != length) {
            throw new BadParcelableException("bad length: expected " + length + ", but got " + Array.getLength(val));
        }
        Class<?> componentType = val.getClass().getComponentType();
        if (!componentType.isArray() && index + 1 != dimensions.length) {
            throw new BadParcelableException("Array has fewer dimensions than expected: " + dimensions.length);
        }
        if (componentType == Boolean.TYPE) {
            writeBooleanArray((boolean[]) val);
        } else if (componentType == Byte.TYPE) {
            writeByteArray((byte[]) val);
        } else if (componentType == Character.TYPE) {
            writeCharArray((char[]) val);
        } else if (componentType == Integer.TYPE) {
            writeIntArray((int[]) val);
        } else if (componentType == Long.TYPE) {
            writeLongArray((long[]) val);
        } else if (componentType == Float.TYPE) {
            writeFloatArray((float[]) val);
        } else if (componentType == Double.TYPE) {
            writeDoubleArray((double[]) val);
        } else if (componentType == IBinder.class) {
            writeBinderArray((IBinder[]) val);
        } else if (IInterface.class.isAssignableFrom(componentType)) {
            writeInterfaceArray((IInterface[]) val);
        } else if (Parcelable.class.isAssignableFrom(componentType)) {
            writeTypedArray((Parcelable[]) val, parcelableFlags);
        } else if (componentType.isArray()) {
            writeInt(length);
            for (int i = 0; i < length; i++) {
                writeFixedArrayInternal(Array.get(val, i), parcelableFlags, index + 1, dimensions);
            }
        } else {
            throw new BadParcelableException("unknown type for fixed-size array: " + componentType);
        }
    }

    public final void writeValue(Object v) {
        if (v instanceof LazyValue) {
            LazyValue value = (LazyValue) v;
            value.writeToParcel(this);
            return;
        }
        int type = getValueType(v);
        writeInt(type);
        if (isLengthPrefixed(type)) {
            int length = dataPosition();
            writeInt(-1);
            int start = dataPosition();
            writeValue(type, v);
            int end = dataPosition();
            setDataPosition(length);
            writeInt(end - start);
            setDataPosition(end);
            return;
        }
        writeValue(type, v);
    }

    public static int getValueType(Object v) {
        if (v == null) {
            return -1;
        }
        if (v instanceof String) {
            return 0;
        }
        if (v instanceof Integer) {
            return 1;
        }
        if (v instanceof Map) {
            return 2;
        }
        if (v instanceof Bundle) {
            return 3;
        }
        if (v instanceof PersistableBundle) {
            return 25;
        }
        if (v instanceof SizeF) {
            return 27;
        }
        if (v instanceof Parcelable) {
            return 4;
        }
        if (v instanceof Short) {
            return 5;
        }
        if (v instanceof Long) {
            return 6;
        }
        if (v instanceof Float) {
            return 7;
        }
        if (v instanceof Double) {
            return 8;
        }
        if (v instanceof Boolean) {
            return 9;
        }
        if (v instanceof CharSequence) {
            return 10;
        }
        if (v instanceof List) {
            return 11;
        }
        if (v instanceof SparseArray) {
            return 12;
        }
        if (v instanceof boolean[]) {
            return 23;
        }
        if (v instanceof byte[]) {
            return 13;
        }
        if (v instanceof String[]) {
            return 14;
        }
        if (v instanceof CharSequence[]) {
            return 24;
        }
        if (v instanceof IBinder) {
            return 15;
        }
        if (v instanceof Parcelable[]) {
            return 16;
        }
        if (v instanceof int[]) {
            return 18;
        }
        if (v instanceof long[]) {
            return 19;
        }
        if (v instanceof Byte) {
            return 20;
        }
        if (v instanceof Size) {
            return 26;
        }
        if (v instanceof double[]) {
            return 28;
        }
        if (v instanceof Character) {
            return 29;
        }
        if (v instanceof short[]) {
            return 30;
        }
        if (v instanceof char[]) {
            return 31;
        }
        if (v instanceof float[]) {
            return 32;
        }
        Class<?> clazz = v.getClass();
        if (clazz.isArray() && clazz.getComponentType() == Object.class) {
            return 17;
        }
        if (v instanceof Serializable) {
            return 21;
        }
        throw new IllegalArgumentException("Parcel: unknown type for value " + v);
    }

    public void writeValue(int type, Object v) {
        switch (type) {
            case -1:
                return;
            case 0:
                writeString((String) v);
                return;
            case 1:
                writeInt(((Integer) v).intValue());
                return;
            case 2:
                writeMap((Map) v);
                return;
            case 3:
                writeBundle((Bundle) v);
                return;
            case 4:
                writeParcelable((Parcelable) v, 0);
                return;
            case 5:
                writeInt(((Short) v).intValue());
                return;
            case 6:
                writeLong(((Long) v).longValue());
                return;
            case 7:
                writeFloat(((Float) v).floatValue());
                return;
            case 8:
                writeDouble(((Double) v).doubleValue());
                return;
            case 9:
                writeInt(((Boolean) v).booleanValue() ? 1 : 0);
                return;
            case 10:
                writeCharSequence((CharSequence) v);
                return;
            case 11:
                writeList((List) v);
                return;
            case 12:
                writeSparseArray((SparseArray) v);
                return;
            case 13:
                writeByteArray((byte[]) v);
                return;
            case 14:
                writeStringArray((String[]) v);
                return;
            case 15:
                writeStrongBinder((IBinder) v);
                return;
            case 16:
                writeParcelableArray((Parcelable[]) v, 0);
                return;
            case 17:
                writeArray((Object[]) v);
                return;
            case 18:
                writeIntArray((int[]) v);
                return;
            case 19:
                writeLongArray((long[]) v);
                return;
            case 20:
                writeInt(((Byte) v).byteValue());
                return;
            case 21:
                writeSerializable((Serializable) v);
                return;
            case 22:
            default:
                throw new RuntimeException("Parcel: unable to marshal value " + v);
            case 23:
                writeBooleanArray((boolean[]) v);
                return;
            case 24:
                writeCharSequenceArray((CharSequence[]) v);
                return;
            case 25:
                writePersistableBundle((PersistableBundle) v);
                return;
            case 26:
                writeSize((Size) v);
                return;
            case 27:
                writeSizeF((SizeF) v);
                return;
            case 28:
                writeDoubleArray((double[]) v);
                return;
            case 29:
                writeInt(((Character) v).charValue());
                return;
            case 30:
                writeShortArray((short[]) v);
                return;
            case 31:
                writeCharArray((char[]) v);
                return;
            case 32:
                writeFloatArray((float[]) v);
                return;
        }
    }

    public final void writeParcelable(Parcelable p, int parcelableFlags) {
        if (p == null) {
            writeString(null);
            return;
        }
        writeParcelableCreator(p);
        p.writeToParcel(this, parcelableFlags);
    }

    public final void writeParcelableCreator(Parcelable p) {
        String name = p.getClass().getName();
        writeString(name);
    }

    private void ensureWrittenSquashableParcelables() {
        if (this.mWrittenSquashableParcelables != null) {
            return;
        }
        this.mWrittenSquashableParcelables = new ArrayMap<>();
    }

    public boolean allowSquashing() {
        boolean previous = this.mAllowSquashing;
        this.mAllowSquashing = true;
        return previous;
    }

    public void restoreAllowSquashing(boolean previous) {
        this.mAllowSquashing = previous;
        if (!previous) {
            this.mWrittenSquashableParcelables = null;
        }
    }

    private void resetSqaushingState() {
        if (this.mAllowSquashing) {
            Slog.wtf(TAG, "allowSquashing wasn't restored.");
        }
        this.mWrittenSquashableParcelables = null;
        this.mReadSquashableParcelables = null;
        this.mAllowSquashing = false;
    }

    private void ensureReadSquashableParcelables() {
        if (this.mReadSquashableParcelables != null) {
            return;
        }
        this.mReadSquashableParcelables = new SparseArray<>();
    }

    public boolean maybeWriteSquashed(Parcelable p) {
        if (!this.mAllowSquashing) {
            writeInt(0);
            return false;
        }
        ensureWrittenSquashableParcelables();
        Integer firstPos = this.mWrittenSquashableParcelables.get(p);
        if (firstPos != null) {
            int pos = dataPosition();
            writeInt((pos - firstPos.intValue()) + 4);
            return true;
        }
        writeInt(0);
        int pos2 = dataPosition();
        this.mWrittenSquashableParcelables.put(p, Integer.valueOf(pos2));
        return false;
    }

    public <T extends Parcelable> T readSquashed(SquashReadHelper<T> reader) {
        int offset = readInt();
        int pos = dataPosition();
        if (offset == 0) {
            T p = reader.readRawParceled(this);
            ensureReadSquashableParcelables();
            this.mReadSquashableParcelables.put(pos, p);
            return p;
        }
        int firstAbsolutePos = pos - offset;
        T t = (T) this.mReadSquashableParcelables.get(firstAbsolutePos);
        if (t == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.mReadSquashableParcelables.size(); i++) {
                sb.append(this.mReadSquashableParcelables.keyAt(i)).append(' ');
            }
            Slog.wtfStack(TAG, "Map doesn't contain offset " + firstAbsolutePos + " : contains=" + sb.toString());
        }
        return t;
    }

    public final void writeSerializable(Serializable s) {
        if (s == null) {
            writeString(null);
            return;
        }
        String name = s.getClass().getName();
        writeString(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.close();
            writeByteArray(baos.toByteArray());
        } catch (IOException ioe) {
            throw new BadParcelableException("Parcelable encountered IOException writing serializable object (name = " + name + NavigationBarInflaterView.KEY_CODE_END, ioe);
        }
    }

    public static void setStackTraceParceling(boolean enabled) {
        sParcelExceptionStackTrace = enabled;
    }

    public final void writeException(Exception e) {
        AppOpsManager.prefixParcelWithAppOpsIfNeeded(this);
        int code = getExceptionCode(e);
        writeInt(code);
        StrictMode.clearGatheredViolations();
        if (code == 0) {
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new RuntimeException(e);
        }
        writeString(e.getMessage());
        long timeNow = sParcelExceptionStackTrace ? SystemClock.elapsedRealtime() : 0L;
        if (sParcelExceptionStackTrace && timeNow - sLastWriteExceptionStackTrace > 1000) {
            sLastWriteExceptionStackTrace = timeNow;
            writeStackTrace(e);
        } else {
            writeInt(0);
        }
        switch (code) {
            case -9:
                int sizePosition = dataPosition();
                writeInt(0);
                writeParcelable((Parcelable) e, 1);
                int payloadPosition = dataPosition();
                setDataPosition(sizePosition);
                writeInt(payloadPosition - sizePosition);
                setDataPosition(payloadPosition);
                return;
            case -8:
                writeInt(((ServiceSpecificException) e).errorCode);
                return;
            default:
                return;
        }
    }

    public static int getExceptionCode(Throwable e) {
        if ((e instanceof Parcelable) && e.getClass().getClassLoader() == Parcelable.class.getClassLoader()) {
            return -9;
        }
        if (e instanceof SecurityException) {
            return -1;
        }
        if (e instanceof BadParcelableException) {
            return -2;
        }
        if (e instanceof IllegalArgumentException) {
            return -3;
        }
        if (e instanceof NullPointerException) {
            return -4;
        }
        if (e instanceof IllegalStateException) {
            return -5;
        }
        if (e instanceof NetworkOnMainThreadException) {
            return -6;
        }
        if (e instanceof UnsupportedOperationException) {
            return -7;
        }
        if (!(e instanceof ServiceSpecificException)) {
            return 0;
        }
        return -8;
    }

    public void writeStackTrace(Throwable e) {
        int sizePosition = dataPosition();
        writeInt(0);
        StackTraceElement[] stackTrace = e.getStackTrace();
        int truncatedSize = Math.min(stackTrace.length, 5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < truncatedSize; i++) {
            sb.append("\tat ").append(stackTrace[i]).append('\n');
        }
        writeString(sb.toString());
        int payloadPosition = dataPosition();
        setDataPosition(sizePosition);
        writeInt(payloadPosition - sizePosition);
        setDataPosition(payloadPosition);
    }

    public final void writeNoException() {
        AppOpsManager.prefixParcelWithAppOpsIfNeeded(this);
        if (StrictMode.hasGatheredViolations()) {
            writeInt(-128);
            int sizePosition = dataPosition();
            writeInt(0);
            StrictMode.writeGatheredViolationsToParcel(this);
            int payloadPosition = dataPosition();
            setDataPosition(sizePosition);
            writeInt(payloadPosition - sizePosition);
            setDataPosition(payloadPosition);
            return;
        }
        writeInt(0);
    }

    public final void readException() {
        int code = readExceptionCode();
        if (code != 0) {
            String msg = readString();
            readException(code, msg);
        }
    }

    public final int readExceptionCode() {
        int code = readInt();
        if (code == -127) {
            AppOpsManager.readAndLogNotedAppops(this);
            code = readInt();
        }
        if (code == -128) {
            int headerSize = readInt();
            if (headerSize == 0) {
                Log.m110e(TAG, "Unexpected zero-sized Parcel reply header.");
                return 0;
            }
            StrictMode.readAndHandleBinderCallViolations(this);
            return 0;
        }
        return code;
    }

    public final void readException(int code, String msg) {
        String remoteStackTrace = null;
        int remoteStackPayloadSize = readInt();
        if (remoteStackPayloadSize > 0) {
            remoteStackTrace = readString();
        }
        Exception e = createException(code, msg);
        if (remoteStackTrace != null) {
            RemoteException cause = new RemoteException("Remote stack trace:\n" + remoteStackTrace, null, false, false);
            ExceptionUtils.appendCause(e, cause);
        }
        SneakyThrow.sneakyThrow(e);
    }

    private Exception createException(int code, String msg) {
        Exception exception = createExceptionOrNull(code, msg);
        if (exception != null) {
            return exception;
        }
        return new RuntimeException("Unknown exception code: " + code + " msg " + msg);
    }

    public Exception createExceptionOrNull(int code, String msg) {
        switch (code) {
            case -9:
                if (readInt() > 0) {
                    return (Exception) readParcelable(Parcelable.class.getClassLoader(), Exception.class);
                }
                return new RuntimeException(msg + " [missing Parcelable]");
            case -8:
                return new ServiceSpecificException(readInt(), msg);
            case -7:
                return new UnsupportedOperationException(msg);
            case -6:
                return new NetworkOnMainThreadException();
            case -5:
                return new IllegalStateException(msg);
            case -4:
                return new NullPointerException(msg);
            case -3:
                return new IllegalArgumentException(msg);
            case -2:
                return new BadParcelableException(msg);
            case -1:
                return new SecurityException(msg);
            default:
                return null;
        }
    }

    public final int readInt() {
        return nativeReadInt(this.mNativePtr);
    }

    public final long readLong() {
        return nativeReadLong(this.mNativePtr);
    }

    public final float readFloat() {
        return nativeReadFloat(this.mNativePtr);
    }

    public final double readDouble() {
        return nativeReadDouble(this.mNativePtr);
    }

    public final String readString() {
        return readString16();
    }

    public final String readString8() {
        return this.mReadWriteHelper.readString8(this);
    }

    public final String readString16() {
        return this.mReadWriteHelper.readString16(this);
    }

    public String readStringNoHelper() {
        return readString16NoHelper();
    }

    public String readString8NoHelper() {
        return nativeReadString8(this.mNativePtr);
    }

    public String readString16NoHelper() {
        return nativeReadString16(this.mNativePtr);
    }

    public final boolean readBoolean() {
        return readInt() != 0;
    }

    public final CharSequence readCharSequence() {
        return TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this);
    }

    public final IBinder readStrongBinder() {
        IBinder result = nativeReadStrongBinder(this.mNativePtr);
        if (result != null && hasFlags(3)) {
            Binder.allowBlocking(result);
        }
        return result;
    }

    public final ParcelFileDescriptor readFileDescriptor() {
        FileDescriptor fd = nativeReadFileDescriptor(this.mNativePtr);
        if (fd != null) {
            return new ParcelFileDescriptor(fd);
        }
        return null;
    }

    public final FileDescriptor readRawFileDescriptor() {
        return nativeReadFileDescriptor(this.mNativePtr);
    }

    public final FileDescriptor[] createRawFileDescriptorArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        FileDescriptor[] f = new FileDescriptor[N];
        for (int i = 0; i < N; i++) {
            f[i] = readRawFileDescriptor();
        }
        return f;
    }

    public final void readRawFileDescriptorArray(FileDescriptor[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readRawFileDescriptor();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final byte readByte() {
        return (byte) (readInt() & 255);
    }

    @Deprecated
    public final void readMap(Map outVal, ClassLoader loader) {
        readMapInternal(outVal, loader, null, null);
    }

    public <K, V> void readMap(Map<? super K, ? super V> outVal, ClassLoader loader, Class<K> clazzKey, Class<V> clazzValue) {
        Objects.requireNonNull(clazzKey);
        Objects.requireNonNull(clazzValue);
        readMapInternal(outVal, loader, clazzKey, clazzValue);
    }

    @Deprecated
    public final void readList(List outVal, ClassLoader loader) {
        int N = readInt();
        readListInternal(outVal, N, loader, null);
    }

    public <T> void readList(List<? super T> outVal, ClassLoader loader, Class<T> clazz) {
        Objects.requireNonNull(clazz);
        int n = readInt();
        readListInternal(outVal, n, loader, clazz);
    }

    @Deprecated
    public HashMap readHashMap(ClassLoader loader) {
        return readHashMapInternal(loader, null, null);
    }

    public <K, V> HashMap<K, V> readHashMap(ClassLoader loader, Class<? extends K> clazzKey, Class<? extends V> clazzValue) {
        Objects.requireNonNull(clazzKey);
        Objects.requireNonNull(clazzValue);
        return readHashMapInternal(loader, clazzKey, clazzValue);
    }

    public final Bundle readBundle() {
        return readBundle(null);
    }

    public final Bundle readBundle(ClassLoader loader) {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        Bundle bundle = new Bundle(this, length);
        if (loader != null) {
            bundle.setClassLoader(loader);
        }
        return bundle;
    }

    public final PersistableBundle readPersistableBundle() {
        return readPersistableBundle(null);
    }

    public final PersistableBundle readPersistableBundle(ClassLoader loader) {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        PersistableBundle bundle = new PersistableBundle(this, length);
        if (loader != null) {
            bundle.setClassLoader(loader);
        }
        return bundle;
    }

    public final Size readSize() {
        int width = readInt();
        int height = readInt();
        return new Size(width, height);
    }

    public final SizeF readSizeF() {
        float width = readFloat();
        float height = readFloat();
        return new SizeF(width, height);
    }

    public final byte[] createByteArray() {
        return nativeCreateByteArray(this.mNativePtr);
    }

    public final void readByteArray(byte[] val) {
        boolean valid = nativeReadByteArray(this.mNativePtr, val, val != null ? val.length : 0);
        if (!valid) {
            throw new RuntimeException("bad array lengths");
        }
    }

    public final byte[] readBlob() {
        return nativeReadBlob(this.mNativePtr);
    }

    public final String[] readStringArray() {
        return createString16Array();
    }

    public final CharSequence[] readCharSequenceArray() {
        CharSequence[] array = null;
        int length = readInt();
        if (length >= 0) {
            array = new CharSequence[length];
            for (int i = 0; i < length; i++) {
                array[i] = readCharSequence();
            }
        }
        return array;
    }

    public final ArrayList<CharSequence> readCharSequenceList() {
        ArrayList<CharSequence> array = null;
        int length = readInt();
        if (length >= 0) {
            array = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                array.add(readCharSequence());
            }
        }
        return array;
    }

    @Deprecated
    public ArrayList readArrayList(ClassLoader loader) {
        return readArrayListInternal(loader, null);
    }

    public <T> ArrayList<T> readArrayList(ClassLoader loader, Class<? extends T> clazz) {
        Objects.requireNonNull(clazz);
        return readArrayListInternal(loader, clazz);
    }

    @Deprecated
    public Object[] readArray(ClassLoader loader) {
        return readArrayInternal(loader, null);
    }

    public <T> T[] readArray(ClassLoader loader, Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return (T[]) readArrayInternal(loader, clazz);
    }

    @Deprecated
    public <T> SparseArray<T> readSparseArray(ClassLoader loader) {
        return readSparseArrayInternal(loader, null);
    }

    public <T> SparseArray<T> readSparseArray(ClassLoader loader, Class<? extends T> clazz) {
        Objects.requireNonNull(clazz);
        return readSparseArrayInternal(loader, clazz);
    }

    public final SparseBooleanArray readSparseBooleanArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        SparseBooleanArray sa = new SparseBooleanArray(N);
        readSparseBooleanArrayInternal(sa, N);
        return sa;
    }

    public final SparseIntArray readSparseIntArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        SparseIntArray sa = new SparseIntArray(N);
        readSparseIntArrayInternal(sa, N);
        return sa;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <T> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> c) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<T> l = (ArrayList<T>) new ArrayList(N);
        while (N > 0) {
            l.add(readTypedObject(c));
            N--;
        }
        return l;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <T> void readTypedList(List<T> list, Parcelable.Creator<T> c) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            list.set(i, readTypedObject(c));
            i++;
        }
        while (i < N) {
            list.add(readTypedObject(c));
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <T extends Parcelable> SparseArray<T> createTypedSparseArray(Parcelable.Creator<T> creator) {
        int count = readInt();
        if (count < 0) {
            return null;
        }
        SparseArray<T> array = (SparseArray<T>) new SparseArray(count);
        for (int i = 0; i < count; i++) {
            int index = readInt();
            array.append(index, (Parcelable) readTypedObject(creator));
        }
        return array;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final <T extends Parcelable> ArrayMap<String, T> createTypedArrayMap(Parcelable.Creator<T> creator) {
        int count = readInt();
        if (count < 0) {
            return null;
        }
        ArrayMap<String, T> map = (ArrayMap<String, T>) new ArrayMap(count);
        for (int i = 0; i < count; i++) {
            String key = readString();
            map.append(key, (Parcelable) readTypedObject(creator));
        }
        return map;
    }

    public final ArrayList<String> createStringArrayList() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<String> l = new ArrayList<>(N);
        while (N > 0) {
            l.add(readString());
            N--;
        }
        return l;
    }

    public final ArrayList<IBinder> createBinderArrayList() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<IBinder> l = new ArrayList<>(N);
        while (N > 0) {
            l.add(readStrongBinder());
            N--;
        }
        return l;
    }

    public final <T extends IInterface> ArrayList<T> createInterfaceArrayList(Function<IBinder, T> asInterface) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<T> l = new ArrayList<>(N);
        while (N > 0) {
            l.add(asInterface.apply(readStrongBinder()));
            N--;
        }
        return l;
    }

    public final void readStringList(List<String> list) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            list.set(i, readString());
            i++;
        }
        while (i < N) {
            list.add(readString());
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    public final void readBinderList(List<IBinder> list) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            list.set(i, readStrongBinder());
            i++;
        }
        while (i < N) {
            list.add(readStrongBinder());
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    public final <T extends IInterface> void readInterfaceList(List<T> list, Function<IBinder, T> asInterface) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            list.set(i, asInterface.apply(readStrongBinder()));
            i++;
        }
        while (i < N) {
            list.add(asInterface.apply(readStrongBinder()));
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    @Deprecated
    public final <T extends Parcelable> List<T> readParcelableList(List<T> list, ClassLoader cl) {
        return readParcelableListInternal(list, cl, null);
    }

    public <T> List<T> readParcelableList(List<T> list, ClassLoader cl, Class<? extends T> clazz) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(clazz);
        return readParcelableListInternal(list, cl, clazz);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> List<T> readParcelableListInternal(List<T> list, ClassLoader cl, Class<? extends T> clazz) {
        int n = readInt();
        if (n == -1) {
            list.clear();
            return list;
        }
        int m = list.size();
        int i = 0;
        while (i < m && i < n) {
            list.set(i, readParcelableInternal(cl, clazz));
            i++;
        }
        while (i < n) {
            list.add(readParcelableInternal(cl, clazz));
            i++;
        }
        while (i < m) {
            list.remove(n);
            i++;
        }
        return list;
    }

    public final <T> T[] createTypedArray(Parcelable.Creator<T> c) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        T[] l = c.newArray(N);
        for (int i = 0; i < N; i++) {
            l[i] = readTypedObject(c);
        }
        return l;
    }

    public final <T> void readTypedArray(T[] val, Parcelable.Creator<T> c) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readTypedObject(c);
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    @Deprecated
    public final <T> T[] readTypedArray(Parcelable.Creator<T> c) {
        return (T[]) createTypedArray(c);
    }

    public final <T> T readTypedObject(Parcelable.Creator<T> c) {
        if (readInt() != 0) {
            return c.createFromParcel(this);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> void readFixedArray(T val) {
        Class<?> componentType = val.getClass().getComponentType();
        if (componentType == Boolean.TYPE) {
            readBooleanArray((boolean[]) val);
        } else if (componentType == Byte.TYPE) {
            readByteArray((byte[]) val);
        } else if (componentType == Character.TYPE) {
            readCharArray((char[]) val);
        } else if (componentType == Integer.TYPE) {
            readIntArray((int[]) val);
        } else if (componentType == Long.TYPE) {
            readLongArray((long[]) val);
        } else if (componentType == Float.TYPE) {
            readFloatArray((float[]) val);
        } else if (componentType == Double.TYPE) {
            readDoubleArray((double[]) val);
        } else if (componentType == IBinder.class) {
            readBinderArray((IBinder[]) val);
        } else if (componentType.isArray()) {
            int length = readInt();
            if (length != Array.getLength(val)) {
                throw new BadParcelableException("Bad length: expected " + Array.getLength(val) + ", but got " + length);
            }
            for (int i = 0; i < length; i++) {
                readFixedArray(Array.get(val, i));
            }
        } else {
            throw new BadParcelableException("Unknown type for fixed-size array: " + componentType);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T, S extends IInterface> void readFixedArray(T val, Function<IBinder, S> asInterface) {
        Class<?> componentType = val.getClass().getComponentType();
        if (IInterface.class.isAssignableFrom(componentType)) {
            readInterfaceArray((IInterface[]) val, asInterface);
        } else if (componentType.isArray()) {
            int length = readInt();
            if (length != Array.getLength(val)) {
                throw new BadParcelableException("Bad length: expected " + Array.getLength(val) + ", but got " + length);
            }
            for (int i = 0; i < length; i++) {
                readFixedArray((Parcel) Array.get(val, i), (Function) asInterface);
            }
        } else {
            throw new BadParcelableException("Unknown type for fixed-size array: " + componentType);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T, S extends Parcelable> void readFixedArray(T val, Parcelable.Creator<S> c) {
        Class<?> componentType = val.getClass().getComponentType();
        if (Parcelable.class.isAssignableFrom(componentType)) {
            readTypedArray((Parcelable[]) val, c);
        } else if (componentType.isArray()) {
            int length = readInt();
            if (length != Array.getLength(val)) {
                throw new BadParcelableException("Bad length: expected " + Array.getLength(val) + ", but got " + length);
            }
            for (int i = 0; i < length; i++) {
                readFixedArray((Parcel) Array.get(val, i), (Parcelable.Creator) c);
            }
        } else {
            throw new BadParcelableException("Unknown type for fixed-size array: " + componentType);
        }
    }

    private void ensureClassHasExpectedDimensions(Class<?> cls, int numDimension) {
        if (numDimension <= 0) {
            throw new BadParcelableException("Fixed-size array should have dimensions.");
        }
        for (int i = 0; i < numDimension; i++) {
            if (!cls.isArray()) {
                throw new BadParcelableException("Array has fewer dimensions than expected: " + numDimension);
            }
            cls = cls.getComponentType();
        }
        if (cls.isArray()) {
            throw new BadParcelableException("Array has more dimensions than expected: " + numDimension);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T createFixedArray(Class<T> cls, int... dimensions) {
        T val;
        ensureClassHasExpectedDimensions(cls, dimensions.length);
        Class<?> componentType = cls.getComponentType();
        if (componentType == Boolean.TYPE) {
            val = (T) createBooleanArray();
        } else if (componentType == Byte.TYPE) {
            val = (T) createByteArray();
        } else if (componentType == Character.TYPE) {
            val = (T) createCharArray();
        } else if (componentType == Integer.TYPE) {
            val = (T) createIntArray();
        } else if (componentType == Long.TYPE) {
            val = (T) createLongArray();
        } else if (componentType == Float.TYPE) {
            val = (T) createFloatArray();
        } else if (componentType == Double.TYPE) {
            val = (T) createDoubleArray();
        } else if (componentType == IBinder.class) {
            val = (T) createBinderArray();
        } else if (componentType.isArray()) {
            int length = readInt();
            if (length < 0) {
                return null;
            }
            if (length != dimensions[0]) {
                throw new BadParcelableException("Bad length: expected " + dimensions[0] + ", but got " + length);
            }
            Class<?> innermost = componentType.getComponentType();
            while (innermost.isArray()) {
                innermost = innermost.getComponentType();
            }
            T val2 = (T) Array.newInstance(innermost, dimensions);
            for (int i = 0; i < length; i++) {
                readFixedArray(Array.get(val2, i));
            }
            return val2;
        } else {
            throw new BadParcelableException("Unknown type for fixed-size array: " + componentType);
        }
        if (val == null || Array.getLength(val) == dimensions[0]) {
            return val;
        }
        throw new BadParcelableException("Bad length: expected " + dimensions[0] + ", but got " + Array.getLength(val));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T, S extends IInterface> T createFixedArray(Class<T> cls, Function<IBinder, S> asInterface, int... dimensions) {
        ensureClassHasExpectedDimensions(cls, dimensions.length);
        final Class<?> componentType = cls.getComponentType();
        if (IInterface.class.isAssignableFrom(componentType)) {
            T val = (T) createInterfaceArray(new IntFunction() { // from class: android.os.Parcel$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return Parcel.lambda$createFixedArray$0(componentType, i);
                }
            }, asInterface);
            if (val != null && Array.getLength(val) != dimensions[0]) {
                throw new BadParcelableException("Bad length: expected " + dimensions[0] + ", but got " + Array.getLength(val));
            }
            return val;
        } else if (componentType.isArray()) {
            int length = readInt();
            if (length < 0) {
                return null;
            }
            if (length != dimensions[0]) {
                throw new BadParcelableException("Bad length: expected " + dimensions[0] + ", but got " + length);
            }
            Class<?> innermost = componentType.getComponentType();
            while (innermost.isArray()) {
                innermost = innermost.getComponentType();
            }
            T val2 = (T) Array.newInstance(innermost, dimensions);
            for (int i = 0; i < length; i++) {
                readFixedArray((Parcel) Array.get(val2, i), (Function) asInterface);
            }
            return val2;
        } else {
            throw new BadParcelableException("Unknown type for fixed-size array: " + componentType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ IInterface[] lambda$createFixedArray$0(Class componentType, int n) {
        return (IInterface[]) Array.newInstance(componentType, n);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T, S extends Parcelable> T createFixedArray(Class<T> cls, Parcelable.Creator<S> c, int... dimensions) {
        ensureClassHasExpectedDimensions(cls, dimensions.length);
        Class<?> componentType = cls.getComponentType();
        if (Parcelable.class.isAssignableFrom(componentType)) {
            T val = (T) createTypedArray(c);
            if (val != null && Array.getLength(val) != dimensions[0]) {
                throw new BadParcelableException("Bad length: expected " + dimensions[0] + ", but got " + Array.getLength(val));
            }
            return val;
        } else if (componentType.isArray()) {
            int length = readInt();
            if (length < 0) {
                return null;
            }
            if (length != dimensions[0]) {
                throw new BadParcelableException("Bad length: expected " + dimensions[0] + ", but got " + length);
            }
            Class<?> innermost = componentType.getComponentType();
            while (innermost.isArray()) {
                innermost = innermost.getComponentType();
            }
            T val2 = (T) Array.newInstance(innermost, dimensions);
            for (int i = 0; i < length; i++) {
                readFixedArray((Parcel) Array.get(val2, i), (Parcelable.Creator) c);
            }
            return val2;
        } else {
            throw new BadParcelableException("Unknown type for fixed-size array: " + componentType);
        }
    }

    public final <T extends Parcelable> void writeParcelableArray(T[] value, int parcelableFlags) {
        if (value != null) {
            int N = value.length;
            writeInt(N);
            for (T t : value) {
                writeParcelable(t, parcelableFlags);
            }
            return;
        }
        writeInt(-1);
    }

    public final Object readValue(ClassLoader loader) {
        return readValue(loader, (Class<Object>) null, new Class[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> T readValue(ClassLoader loader, Class<T> clazz, Class<?>... itemTypes) {
        int type = readInt();
        if (isLengthPrefixed(type)) {
            int length = readInt();
            int start = dataPosition();
            T object = (T) readValue(type, loader, clazz, itemTypes);
            int actual = dataPosition() - start;
            if (actual != length) {
                Slog.wtfStack(TAG, "Unparcelling of " + object + " of type " + valueTypeToString(type) + "  consumed " + actual + " bytes, but " + length + " expected.");
                return object;
            }
            return object;
        }
        return (T) readValue(type, loader, clazz, itemTypes);
    }

    public Object readLazyValue(ClassLoader loader) {
        int start = dataPosition();
        int type = readInt();
        if (isLengthPrefixed(type)) {
            int objectLength = readInt();
            if (objectLength < 0) {
                return null;
            }
            int end = MathUtils.addOrThrow(dataPosition(), objectLength);
            int valueLength = end - start;
            setDataPosition(end);
            return new LazyValue(this, start, valueLength, type, loader);
        }
        return readValue(type, loader, (Class<Object>) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.Parcel$LazyValue */
    /* loaded from: classes3.dex */
    public static final class LazyValue implements BiFunction<Class<?>, Class<?>[], Object> {
        private final int mLength;
        private final ClassLoader mLoader;
        private Object mObject;
        private final int mPosition;
        private volatile Parcel mSource;
        private final int mType;

        LazyValue(Parcel source, int position, int length, int type, ClassLoader loader) {
            this.mSource = (Parcel) Objects.requireNonNull(source);
            this.mPosition = position;
            this.mLength = length;
            this.mType = type;
            this.mLoader = loader;
        }

        @Override // java.util.function.BiFunction
        public Object apply(Class<?> clazz, Class<?>[] itemTypes) {
            Parcel source = this.mSource;
            if (source != null) {
                synchronized (source) {
                    if (this.mSource != null) {
                        int restore = source.dataPosition();
                        source.setDataPosition(this.mPosition);
                        this.mObject = source.readValue(this.mLoader, clazz, itemTypes);
                        source.setDataPosition(restore);
                        this.mSource = null;
                    }
                }
            }
            return this.mObject;
        }

        public void writeToParcel(Parcel out) {
            Parcel source = this.mSource;
            if (source != null) {
                out.appendFrom(source, this.mPosition, this.mLength);
            } else {
                out.writeValue(this.mObject);
            }
        }

        public boolean hasFileDescriptors() {
            Parcel source = this.mSource;
            if (source != null) {
                return source.hasFileDescriptors(this.mPosition, this.mLength);
            }
            return Parcel.hasFileDescriptors(this.mObject);
        }

        public String toString() {
            if (this.mSource != null) {
                return "Supplier{" + Parcel.valueTypeToString(this.mType) + "@" + this.mPosition + "+" + this.mLength + '}';
            }
            return "Supplier{" + this.mObject + "}";
        }

        public boolean equals(Object other) {
            int i;
            if (this == other) {
                return true;
            }
            if (other instanceof LazyValue) {
                LazyValue value = (LazyValue) other;
                Parcel source = this.mSource;
                Parcel otherSource = value.mSource;
                if ((source == null) != (otherSource == null)) {
                    return false;
                }
                if (source == null) {
                    return Objects.equals(this.mObject, value.mObject);
                }
                if (Objects.equals(this.mLoader, value.mLoader) && this.mType == value.mType && (i = this.mLength) == value.mLength) {
                    return Parcel.compareData(source, this.mPosition, otherSource, value.mPosition, i);
                }
                return false;
            }
            return false;
        }

        public int hashCode() {
            Object[] objArr = new Object[5];
            objArr[0] = Boolean.valueOf(this.mSource == null);
            objArr[1] = this.mObject;
            objArr[2] = this.mLoader;
            objArr[3] = Integer.valueOf(this.mType);
            objArr[4] = Integer.valueOf(this.mLength);
            return Objects.hash(objArr);
        }
    }

    private <T> T readValue(int type, ClassLoader loader, Class<T> clazz) {
        return (T) readValue(type, loader, clazz, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.Object, android.os.Parcel] */
    private <T> T readValue(int type, ClassLoader loader, Class<T> clazz, Class<?>... itemTypes) {
        Object object;
        switch (type) {
            case -1:
                Object object2 = null;
                object = (T) object2;
                break;
            case 0:
                Object object3 = readString();
                object = (T) object3;
                break;
            case 1:
                object = (T) Integer.valueOf(readInt());
                break;
            case 2:
                checkTypeToUnparcel(clazz, HashMap.class);
                Class<?> keyType = (Class) ArrayUtils.getOrNull(itemTypes, 0);
                Class<?> valueType = (Class) ArrayUtils.getOrNull(itemTypes, 1);
                Preconditions.checkArgument((keyType == null) == (valueType == null));
                object = (T) readHashMapInternal(loader, keyType, valueType);
                break;
            case 3:
                Object object4 = readBundle(loader);
                object = (T) object4;
                break;
            case 4:
                Object object5 = readParcelableInternal(loader, clazz);
                object = (T) object5;
                break;
            case 5:
                object = (T) Short.valueOf((short) readInt());
                break;
            case 6:
                object = (T) Long.valueOf(readLong());
                break;
            case 7:
                object = (T) Float.valueOf(readFloat());
                break;
            case 8:
                object = (T) Double.valueOf(readDouble());
                break;
            case 9:
                object = (T) Boolean.valueOf(readInt() == 1);
                break;
            case 10:
                object = (T) readCharSequence();
                break;
            case 11:
                checkTypeToUnparcel(clazz, ArrayList.class);
                Class<?> itemType = (Class) ArrayUtils.getOrNull(itemTypes, 0);
                Object object6 = readArrayListInternal(loader, itemType);
                object = (T) object6;
                break;
            case 12:
                checkTypeToUnparcel(clazz, SparseArray.class);
                Class<?> itemType2 = (Class) ArrayUtils.getOrNull(itemTypes, 0);
                Object object7 = readSparseArrayInternal(loader, itemType2);
                object = (T) object7;
                break;
            case 13:
                Object object8 = createByteArray();
                object = (T) object8;
                break;
            case 14:
                Object object9 = readStringArray();
                object = (T) object9;
                break;
            case 15:
                object = (T) readStrongBinder();
                break;
            case 16:
                Class<Parcelable> cls = (Class) ArrayUtils.getOrNull(itemTypes, 0);
                checkArrayTypeToUnparcel(clazz, cls != null ? cls : Parcelable.class);
                Object object10 = readParcelableArrayInternal(loader, cls);
                object = (T) object10;
                break;
            case 17:
                Object object11 = ArrayUtils.getOrNull(itemTypes, 0);
                Class<?> itemType3 = (Class) object11;
                checkArrayTypeToUnparcel(clazz, itemType3 != null ? itemType3 : Object.class);
                Object object12 = readArrayInternal(loader, itemType3);
                object = (T) object12;
                break;
            case 18:
                Object object13 = createIntArray();
                object = (T) object13;
                break;
            case 19:
                Object object14 = createLongArray();
                object = (T) object14;
                break;
            case 20:
                object = (T) Byte.valueOf(readByte());
                break;
            case 21:
                Object object15 = readSerializableInternal(loader, clazz);
                object = (T) object15;
                break;
            case 22:
                Object object16 = readSparseBooleanArray();
                object = (T) object16;
                break;
            case 23:
                Object object17 = createBooleanArray();
                object = (T) object17;
                break;
            case 24:
                Object object18 = readCharSequenceArray();
                object = (T) object18;
                break;
            case 25:
                Object object19 = readPersistableBundle(loader);
                object = (T) object19;
                break;
            case 26:
                Object object20 = readSize();
                object = (T) object20;
                break;
            case 27:
                Object object21 = readSizeF();
                object = (T) object21;
                break;
            case 28:
                Object object22 = createDoubleArray();
                object = (T) object22;
                break;
            case 29:
                object = (T) Character.valueOf((char) readInt());
                break;
            case 30:
                Object object23 = createShortArray();
                object = (T) object23;
                break;
            case 31:
                Object object24 = createCharArray();
                object = (T) object24;
                break;
            case 32:
                object = (T) createFloatArray();
                break;
            default:
                int off = dataPosition() - 4;
                throw new BadParcelableException("Parcel " + ((Object) this) + ": Unmarshalling unknown type code " + type + " at offset " + off);
        }
        if (object == null || clazz == null || clazz.isInstance(object)) {
            return (T) object;
        }
        throw new BadTypeParcelableException("Unparcelled object " + object + " is not an instance of required class " + clazz.getName() + " provided in the parameter");
    }

    private boolean isLengthPrefixed(int type) {
        switch (type) {
            case 2:
            case 4:
            case 11:
            case 12:
            case 16:
            case 17:
            case 21:
                return true;
            default:
                return false;
        }
    }

    private void checkArrayTypeToUnparcel(Class<?> requiredArrayType, Class<?> componentTypeToUnparcel) {
        if (requiredArrayType != null) {
            Class<?> requiredComponentType = requiredArrayType.getComponentType();
            if (requiredComponentType == null) {
                throw new BadTypeParcelableException("About to unparcel an array but type " + requiredArrayType.getCanonicalName() + " required by caller is not an array.");
            }
            checkTypeToUnparcel(requiredComponentType, componentTypeToUnparcel);
        }
    }

    private void checkTypeToUnparcel(Class<?> requiredType, Class<?> typeToUnparcel) {
        if (requiredType != null && !requiredType.isAssignableFrom(typeToUnparcel)) {
            throw new BadTypeParcelableException("About to unparcel a " + typeToUnparcel.getCanonicalName() + ", which is not a subtype of type " + requiredType.getCanonicalName() + " required by caller.");
        }
    }

    @Deprecated
    public final <T extends Parcelable> T readParcelable(ClassLoader loader) {
        return (T) readParcelableInternal(loader, null);
    }

    public <T> T readParcelable(ClassLoader loader, Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return (T) readParcelableInternal(loader, clazz);
    }

    private <T> T readParcelableInternal(ClassLoader loader, Class<T> clazz) {
        Parcelable.Creator<?> creator = readParcelableCreatorInternal(loader, clazz);
        if (creator == null) {
            return null;
        }
        if (creator instanceof Parcelable.ClassLoaderCreator) {
            Parcelable.ClassLoaderCreator<?> classLoaderCreator = (Parcelable.ClassLoaderCreator) creator;
            return (T) classLoaderCreator.createFromParcel(this, loader);
        }
        return creator.createFromParcel(this);
    }

    public final <T extends Parcelable> T readCreator(Parcelable.Creator<?> creator, ClassLoader loader) {
        if (creator instanceof Parcelable.ClassLoaderCreator) {
            Parcelable.ClassLoaderCreator<?> classLoaderCreator = (Parcelable.ClassLoaderCreator) creator;
            return (T) classLoaderCreator.createFromParcel(this, loader);
        }
        return (T) creator.createFromParcel(this);
    }

    @Deprecated
    public final Parcelable.Creator<?> readParcelableCreator(ClassLoader loader) {
        return readParcelableCreatorInternal(loader, null);
    }

    public <T> Parcelable.Creator<T> readParcelableCreator(ClassLoader loader, Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return readParcelableCreatorInternal(loader, clazz);
    }

    private <T> Parcelable.Creator<T> readParcelableCreatorInternal(ClassLoader loader, Class<T> clazz) {
        Pair<Parcelable.Creator<?>, Class<?>> creatorAndParcelableClass;
        ClassLoader parcelableClassLoader;
        String name = readString();
        if (name == null) {
            return null;
        }
        HashMap<ClassLoader, HashMap<String, Pair<Parcelable.Creator<?>, Class<?>>>> hashMap = sPairedCreators;
        synchronized (hashMap) {
            HashMap<String, Pair<Parcelable.Creator<?>, Class<?>>> map = hashMap.get(loader);
            if (map == null) {
                hashMap.put(loader, new HashMap<>());
                mCreators.put(loader, new HashMap<>());
                creatorAndParcelableClass = null;
            } else {
                Pair<Parcelable.Creator<?>, Class<?>> creatorAndParcelableClass2 = map.get(name);
                creatorAndParcelableClass = creatorAndParcelableClass2;
            }
        }
        if (creatorAndParcelableClass != null) {
            Parcelable.Creator<T> creator = (Parcelable.Creator) creatorAndParcelableClass.first;
            Class<?> parcelableClass = creatorAndParcelableClass.second;
            if (clazz != null && !clazz.isAssignableFrom(parcelableClass)) {
                throw new BadTypeParcelableException("Parcelable creator " + name + " is not a subclass of required class " + clazz.getName() + " provided in the parameter");
            }
            return creator;
        }
        if (loader != null) {
            parcelableClassLoader = loader;
        } else {
            try {
                parcelableClassLoader = getClass().getClassLoader();
            } catch (ClassNotFoundException e) {
                Log.m109e(TAG, "Class not found when unmarshalling: " + name, e);
                throw new BadParcelableException("ClassNotFoundException when unmarshalling: " + name, e);
            } catch (IllegalAccessException e2) {
                Log.m109e(TAG, "Illegal access when unmarshalling: " + name, e2);
                throw new BadParcelableException("IllegalAccessException when unmarshalling: " + name, e2);
            } catch (NoSuchFieldException e3) {
                throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called CREATOR on class " + name, e3);
            }
        }
        Class<?> parcelableClass2 = Class.forName(name, false, parcelableClassLoader);
        if (!Parcelable.class.isAssignableFrom(parcelableClass2)) {
            throw new BadParcelableException("Parcelable protocol requires subclassing from Parcelable on class " + name);
        }
        if (clazz != null && !clazz.isAssignableFrom(parcelableClass2)) {
            throw new BadTypeParcelableException("Parcelable creator " + name + " is not a subclass of required class " + clazz.getName() + " provided in the parameter");
        }
        Field f = parcelableClass2.getField("CREATOR");
        if ((f.getModifiers() & 8) == 0) {
            throw new BadParcelableException("Parcelable protocol requires the CREATOR object to be static on class " + name);
        }
        Class<?> creatorType = f.getType();
        if (!Parcelable.Creator.class.isAssignableFrom(creatorType)) {
            throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called CREATOR on class " + name);
        }
        Parcelable.Creator<T> creator2 = (Parcelable.Creator) f.get(null);
        if (creator2 == null) {
            throw new BadParcelableException("Parcelable protocol requires a non-null Parcelable.Creator object called CREATOR on class " + name);
        }
        synchronized (hashMap) {
            hashMap.get(loader).put(name, Pair.create(creator2, parcelableClass2));
            mCreators.get(loader).put(name, creator2);
        }
        return creator2;
    }

    @Deprecated
    public Parcelable[] readParcelableArray(ClassLoader loader) {
        return (Parcelable[]) readParcelableArrayInternal(loader, null);
    }

    public <T> T[] readParcelableArray(ClassLoader loader, Class<T> clazz) {
        return (T[]) readParcelableArrayInternal(loader, (Class) Objects.requireNonNull(clazz));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> T[] readParcelableArrayInternal(ClassLoader loader, Class<T> clazz) {
        int n = readInt();
        if (n < 0) {
            return null;
        }
        T[] p = (T[]) ((Object[]) (clazz == null ? new Parcelable[n] : Array.newInstance((Class<?>) clazz, n)));
        for (int i = 0; i < n; i++) {
            p[i] = readParcelableInternal(loader, clazz);
        }
        return p;
    }

    @Deprecated
    public Serializable readSerializable() {
        return (Serializable) readSerializableInternal(null, null);
    }

    public <T> T readSerializable(ClassLoader loader, Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return (T) readSerializableInternal(loader == null ? getClass().getClassLoader() : loader, clazz);
    }

    private <T> T readSerializableInternal(final ClassLoader loader, Class<T> clazz) {
        String name = readString();
        if (name == null) {
            return null;
        }
        if (clazz != null && loader != null) {
            try {
                Class<?> cl = Class.forName(name, false, loader);
                if (!clazz.isAssignableFrom(cl)) {
                    throw new BadTypeParcelableException("Serializable object " + cl.getName() + " is not a subclass of required class " + clazz.getName() + " provided in the parameter");
                }
            } catch (IOException ioe) {
                throw new BadParcelableException("Parcelable encountered IOException reading a Serializable object (name = " + name + NavigationBarInflaterView.KEY_CODE_END, ioe);
            } catch (ClassNotFoundException cnfe) {
                throw new BadParcelableException("Parcelable encountered ClassNotFoundException reading a Serializable object (name = " + name + NavigationBarInflaterView.KEY_CODE_END, cnfe);
            }
        }
        byte[] serializedData = createByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        ObjectInputStream ois = new ObjectInputStream(bais) { // from class: android.os.Parcel.2
            @Override // java.io.ObjectInputStream
            protected Class<?> resolveClass(ObjectStreamClass osClass) throws IOException, ClassNotFoundException {
                if (loader != null) {
                    Class<?> c = Class.forName(osClass.getName(), false, loader);
                    return (Class) Objects.requireNonNull(c);
                }
                Class<?> c2 = super.resolveClass(osClass);
                return c2;
            }
        };
        T object = (T) ois.readObject();
        if (clazz != null && loader == null && !clazz.isAssignableFrom(object.getClass())) {
            throw new BadTypeParcelableException("Serializable object " + object.getClass().getName() + " is not a subclass of required class " + clazz.getName() + " provided in the parameter");
        }
        return object;
    }

    protected static final Parcel obtain(int obj) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final Parcel obtain(long obj) {
        Parcel res = null;
        synchronized (sPoolSync) {
            Parcel parcel = sHolderPool;
            if (parcel != null) {
                res = parcel;
                sHolderPool = res.mPoolNext;
                res.mPoolNext = null;
                sHolderPoolSize--;
            }
        }
        if (res == null) {
            Parcel res2 = new Parcel(obj);
            return res2;
        }
        res.mRecycled = false;
        res.init(obj);
        return res;
    }

    private Parcel(long nativePtr) {
        init(nativePtr);
    }

    private void init(long nativePtr) {
        if (nativePtr != 0) {
            this.mNativePtr = nativePtr;
            this.mOwnsNativeParcelObject = false;
            return;
        }
        this.mNativePtr = nativeCreate();
        this.mOwnsNativeParcelObject = true;
    }

    private void freeBuffer() {
        this.mFlags = 0;
        resetSqaushingState();
        if (this.mOwnsNativeParcelObject) {
            nativeFreeBuffer(this.mNativePtr);
        }
        this.mReadWriteHelper = ReadWriteHelper.DEFAULT;
    }

    private void destroy() {
        resetSqaushingState();
        long j = this.mNativePtr;
        if (j != 0) {
            if (this.mOwnsNativeParcelObject) {
                nativeDestroy(j);
            }
            this.mNativePtr = 0L;
        }
    }

    protected void finalize() throws Throwable {
        destroy();
    }

    void readMapInternal(Map outVal, int n, ClassLoader loader) {
        readMapInternal(outVal, n, loader, null, null);
    }

    private <K, V> HashMap<K, V> readHashMapInternal(ClassLoader loader, Class<? extends K> clazzKey, Class<? extends V> clazzValue) {
        int n = readInt();
        if (n < 0) {
            return null;
        }
        HashMap<K, V> map = new HashMap<>(n);
        readMapInternal(map, n, loader, clazzKey, clazzValue);
        return map;
    }

    private <K, V> void readMapInternal(Map<? super K, ? super V> outVal, ClassLoader loader, Class<K> clazzKey, Class<V> clazzValue) {
        int n = readInt();
        readMapInternal(outVal, n, loader, clazzKey, clazzValue);
    }

    private <K, V> void readMapInternal(Map<? super K, ? super V> outVal, int n, ClassLoader loader, Class<K> clazzKey, Class<V> clazzValue) {
        while (n > 0) {
            outVal.put((Object) readValue(loader, clazzKey, new Class[0]), (Object) readValue(loader, clazzValue, new Class[0]));
            n--;
        }
    }

    private void readArrayMapInternal(ArrayMap<? super String, Object> outVal, int size, ClassLoader loader) {
        readArrayMap(outVal, size, true, false, loader);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int readArrayMap(ArrayMap<? super String, Object> map, int size, boolean sorted, boolean lazy, ClassLoader loader) {
        int lazyValues = 0;
        while (size > 0) {
            String key = readString();
            Object value = lazy ? readLazyValue(loader) : readValue(loader);
            if (value instanceof LazyValue) {
                lazyValues++;
            }
            if (sorted) {
                map.append(key, value);
            } else {
                map.put(key, value);
            }
            size--;
        }
        if (sorted) {
            map.validate();
        }
        return lazyValues;
    }

    public void readArrayMap(ArrayMap<? super String, Object> outVal, ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return;
        }
        readArrayMapInternal(outVal, N, loader);
    }

    public ArraySet<? extends Object> readArraySet(ClassLoader loader) {
        int size = readInt();
        if (size < 0) {
            return null;
        }
        ArraySet<Object> result = new ArraySet<>(size);
        for (int i = 0; i < size; i++) {
            Object value = readValue(loader);
            result.append(value);
        }
        return result;
    }

    private void readListInternal(List outVal, int n, ClassLoader loader) {
        readListInternal(outVal, n, loader, null);
    }

    private <T> void readListInternal(List<? super T> outVal, int n, ClassLoader loader, Class<T> clazz) {
        while (n > 0) {
            outVal.add((Object) readValue(loader, clazz, new Class[0]));
            n--;
        }
    }

    private <T> ArrayList<T> readArrayListInternal(ClassLoader loader, Class<? extends T> clazz) {
        int n = readInt();
        if (n < 0) {
            return null;
        }
        ArrayList<T> l = new ArrayList<>(n);
        readListInternal(l, n, loader, clazz);
        return l;
    }

    private void readArrayInternal(Object[] outVal, int N, ClassLoader loader) {
        for (int i = 0; i < N; i++) {
            Object value = readValue(loader, (Class<Object>) null, new Class[0]);
            outVal[i] = value;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> T[] readArrayInternal(ClassLoader loader, Class<T> clazz) {
        int n = readInt();
        if (n < 0) {
            return null;
        }
        T[] outVal = (T[]) ((Object[]) (clazz == null ? new Object[n] : Array.newInstance((Class<?>) clazz, n)));
        for (int i = 0; i < n; i++) {
            outVal[i] = readValue(loader, clazz, new Class[0]);
        }
        return outVal;
    }

    private void readSparseArrayInternal(SparseArray outVal, int N, ClassLoader loader) {
        while (N > 0) {
            int key = readInt();
            Object value = readValue(loader);
            outVal.append(key, value);
            N--;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> SparseArray<T> readSparseArrayInternal(ClassLoader loader, Class<? extends T> clazz) {
        int n = readInt();
        if (n < 0) {
            return null;
        }
        SparseArray<T> outVal = (SparseArray<T>) new SparseArray(n);
        while (n > 0) {
            int key = readInt();
            outVal.append(key, readValue(loader, clazz, new Class[0]));
            n--;
        }
        return outVal;
    }

    private void readSparseBooleanArrayInternal(SparseBooleanArray outVal, int N) {
        while (N > 0) {
            int key = readInt();
            boolean z = true;
            if (readByte() != 1) {
                z = false;
            }
            boolean value = z;
            outVal.append(key, value);
            N--;
        }
    }

    private void readSparseIntArrayInternal(SparseIntArray outVal, int N) {
        while (N > 0) {
            int key = readInt();
            int value = readInt();
            outVal.append(key, value);
            N--;
        }
    }

    public long getOpenAshmemSize() {
        return nativeGetOpenAshmemSize(this.mNativePtr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String valueTypeToString(int type) {
        switch (type) {
            case -1:
                return "VAL_NULL";
            case 0:
            case 22:
            default:
                return "UNKNOWN(" + type + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "VAL_INTEGER";
            case 2:
                return "VAL_MAP";
            case 3:
                return "VAL_BUNDLE";
            case 4:
                return "VAL_PARCELABLE";
            case 5:
                return "VAL_SHORT";
            case 6:
                return "VAL_LONG";
            case 7:
                return "VAL_FLOAT";
            case 8:
                return "VAL_DOUBLE";
            case 9:
                return "VAL_BOOLEAN";
            case 10:
                return "VAL_CHARSEQUENCE";
            case 11:
                return "VAL_LIST";
            case 12:
                return "VAL_SPARSEARRAY";
            case 13:
                return "VAL_BYTEARRAY";
            case 14:
                return "VAL_STRINGARRAY";
            case 15:
                return "VAL_IBINDER";
            case 16:
                return "VAL_PARCELABLEARRAY";
            case 17:
                return "VAL_OBJECTARRAY";
            case 18:
                return "VAL_INTARRAY";
            case 19:
                return "VAL_LONGARRAY";
            case 20:
                return "VAL_BYTE";
            case 21:
                return "VAL_SERIALIZABLE";
            case 23:
                return "VAL_BOOLEANARRAY";
            case 24:
                return "VAL_CHARSEQUENCEARRAY";
            case 25:
                return "VAL_PERSISTABLEBUNDLE";
            case 26:
                return "VAL_SIZE";
            case 27:
                return "VAL_SIZEF";
            case 28:
                return "VAL_DOUBLEARRAY";
            case 29:
                return "VAL_CHAR";
            case 30:
                return "VAL_SHORTARRAY";
            case 31:
                return "VAL_CHARARRAY";
            case 32:
                return "VAL_FLOATARRAY";
        }
    }
}
