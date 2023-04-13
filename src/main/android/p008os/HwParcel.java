package android.p008os;

import android.annotation.SystemApi;
import dalvik.annotation.optimization.FastNative;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import libcore.util.NativeAllocationRegistry;
@SystemApi
/* renamed from: android.os.HwParcel */
/* loaded from: classes3.dex */
public class HwParcel {
    public static final int STATUS_SUCCESS = 0;
    private static final String TAG = "HwParcel";
    private static final NativeAllocationRegistry sNativeRegistry;
    private long mNativeContext;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.HwParcel$Status */
    /* loaded from: classes3.dex */
    public @interface Status {
    }

    private static final native long native_init();

    @FastNative
    private final native void native_setup(boolean z);

    @FastNative
    private final native boolean[] readBoolVectorAsArray();

    @FastNative
    private final native double[] readDoubleVectorAsArray();

    @FastNative
    private final native float[] readFloatVectorAsArray();

    @FastNative
    private final native short[] readInt16VectorAsArray();

    @FastNative
    private final native int[] readInt32VectorAsArray();

    @FastNative
    private final native long[] readInt64VectorAsArray();

    @FastNative
    private final native byte[] readInt8VectorAsArray();

    @FastNative
    private final native NativeHandle[] readNativeHandleAsArray();

    @FastNative
    private final native String[] readStringVectorAsArray();

    @FastNative
    private final native void writeBoolVector(boolean[] zArr);

    @FastNative
    private final native void writeDoubleVector(double[] dArr);

    @FastNative
    private final native void writeFloatVector(float[] fArr);

    @FastNative
    private final native void writeInt16Vector(short[] sArr);

    @FastNative
    private final native void writeInt32Vector(int[] iArr);

    @FastNative
    private final native void writeInt64Vector(long[] jArr);

    @FastNative
    private final native void writeInt8Vector(byte[] bArr);

    @FastNative
    private final native void writeNativeHandleVector(NativeHandle[] nativeHandleArr);

    @FastNative
    private final native void writeStringVector(String[] strArr);

    public final native void enforceInterface(String str);

    @FastNative
    public final native boolean readBool();

    @FastNative
    public final native HwBlob readBuffer(long j);

    @FastNative
    public final native double readDouble();

    @FastNative
    public final native HwBlob readEmbeddedBuffer(long j, long j2, long j3, boolean z);

    @FastNative
    public final native HidlMemory readEmbeddedHidlMemory(long j, long j2, long j3);

    @FastNative
    public final native NativeHandle readEmbeddedNativeHandle(long j, long j2);

    @FastNative
    public final native float readFloat();

    @FastNative
    public final native HidlMemory readHidlMemory();

    @FastNative
    public final native short readInt16();

    @FastNative
    public final native int readInt32();

    @FastNative
    public final native long readInt64();

    @FastNative
    public final native byte readInt8();

    @FastNative
    public final native NativeHandle readNativeHandle();

    @FastNative
    public final native String readString();

    @FastNative
    public final native IHwBinder readStrongBinder();

    @FastNative
    public final native void release();

    @FastNative
    public final native void releaseTemporaryStorage();

    public final native void send();

    @FastNative
    public final native void verifySuccess();

    @FastNative
    public final native void writeBool(boolean z);

    @FastNative
    public final native void writeBuffer(HwBlob hwBlob);

    @FastNative
    public final native void writeDouble(double d);

    @FastNative
    public final native void writeFloat(float f);

    @FastNative
    public final native void writeHidlMemory(HidlMemory hidlMemory);

    @FastNative
    public final native void writeInt16(short s);

    @FastNative
    public final native void writeInt32(int i);

    @FastNative
    public final native void writeInt64(long j);

    @FastNative
    public final native void writeInt8(byte b);

    @FastNative
    public final native void writeInterfaceToken(String str);

    @FastNative
    public final native void writeNativeHandle(NativeHandle nativeHandle);

    @FastNative
    public final native void writeStatus(int i);

    @FastNative
    public final native void writeString(String str);

    @FastNative
    public final native void writeStrongBinder(IHwBinder iHwBinder);

    private HwParcel(boolean allocate) {
        native_setup(allocate);
        sNativeRegistry.registerNativeAllocation(this, this.mNativeContext);
    }

    public HwParcel() {
        native_setup(true);
        sNativeRegistry.registerNativeAllocation(this, this.mNativeContext);
    }

    public final void writeBoolVector(ArrayList<Boolean> val) {
        int n = val.size();
        boolean[] array = new boolean[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).booleanValue();
        }
        writeBoolVector(array);
    }

    public final void writeInt8Vector(ArrayList<Byte> val) {
        int n = val.size();
        byte[] array = new byte[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).byteValue();
        }
        writeInt8Vector(array);
    }

    public final void writeInt16Vector(ArrayList<Short> val) {
        int n = val.size();
        short[] array = new short[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).shortValue();
        }
        writeInt16Vector(array);
    }

    public final void writeInt32Vector(ArrayList<Integer> val) {
        int n = val.size();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).intValue();
        }
        writeInt32Vector(array);
    }

    public final void writeInt64Vector(ArrayList<Long> val) {
        int n = val.size();
        long[] array = new long[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).longValue();
        }
        writeInt64Vector(array);
    }

    public final void writeFloatVector(ArrayList<Float> val) {
        int n = val.size();
        float[] array = new float[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).floatValue();
        }
        writeFloatVector(array);
    }

    public final void writeDoubleVector(ArrayList<Double> val) {
        int n = val.size();
        double[] array = new double[n];
        for (int i = 0; i < n; i++) {
            array[i] = val.get(i).doubleValue();
        }
        writeDoubleVector(array);
    }

    public final void writeStringVector(ArrayList<String> val) {
        writeStringVector((String[]) val.toArray(new String[val.size()]));
    }

    public final void writeNativeHandleVector(ArrayList<NativeHandle> val) {
        writeNativeHandleVector((NativeHandle[]) val.toArray(new NativeHandle[val.size()]));
    }

    public final ArrayList<Boolean> readBoolVector() {
        Boolean[] array = HwBlob.wrapArray(readBoolVectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<Byte> readInt8Vector() {
        Byte[] array = HwBlob.wrapArray(readInt8VectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<Short> readInt16Vector() {
        Short[] array = HwBlob.wrapArray(readInt16VectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<Integer> readInt32Vector() {
        Integer[] array = HwBlob.wrapArray(readInt32VectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<Long> readInt64Vector() {
        Long[] array = HwBlob.wrapArray(readInt64VectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<Float> readFloatVector() {
        Float[] array = HwBlob.wrapArray(readFloatVectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<Double> readDoubleVector() {
        Double[] array = HwBlob.wrapArray(readDoubleVectorAsArray());
        return new ArrayList<>(Arrays.asList(array));
    }

    public final ArrayList<String> readStringVector() {
        return new ArrayList<>(Arrays.asList(readStringVectorAsArray()));
    }

    public final ArrayList<NativeHandle> readNativeHandleVector() {
        return new ArrayList<>(Arrays.asList(readNativeHandleAsArray()));
    }

    static {
        long freeFunction = native_init();
        sNativeRegistry = new NativeAllocationRegistry(HwParcel.class.getClassLoader(), freeFunction, 128L);
    }
}
