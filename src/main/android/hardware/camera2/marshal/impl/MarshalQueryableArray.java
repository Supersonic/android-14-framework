package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalHelpers;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.MarshalRegistry;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Log;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class MarshalQueryableArray<T> implements MarshalQueryable<T> {
    private static final boolean DEBUG = false;
    private static final String TAG = MarshalQueryableArray.class.getSimpleName();

    /* loaded from: classes.dex */
    private interface PrimitiveArrayFiller {
        void fillArray(Object obj, int i, ByteBuffer byteBuffer);

        static PrimitiveArrayFiller getPrimitiveArrayFiller(Class<?> componentType) {
            if (componentType == Integer.TYPE) {
                return new PrimitiveArrayFiller() { // from class: android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller.1
                    @Override // android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller
                    public void fillArray(Object arr, int size, ByteBuffer buffer) {
                        IntBuffer ib = buffer.asIntBuffer().get((int[]) int[].class.cast(arr), 0, size);
                        buffer.position(buffer.position() + (ib.position() * 4));
                    }
                };
            }
            if (componentType == Float.TYPE) {
                return new PrimitiveArrayFiller() { // from class: android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller.2
                    @Override // android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller
                    public void fillArray(Object arr, int size, ByteBuffer buffer) {
                        FloatBuffer fb = buffer.asFloatBuffer().get((float[]) float[].class.cast(arr), 0, size);
                        buffer.position(buffer.position() + (fb.position() * 4));
                    }
                };
            }
            if (componentType == Long.TYPE) {
                return new PrimitiveArrayFiller() { // from class: android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller.3
                    @Override // android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller
                    public void fillArray(Object arr, int size, ByteBuffer buffer) {
                        LongBuffer lb = buffer.asLongBuffer().get((long[]) long[].class.cast(arr), 0, size);
                        buffer.position(buffer.position() + (lb.position() * 8));
                    }
                };
            }
            if (componentType == Double.TYPE) {
                return new PrimitiveArrayFiller() { // from class: android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller.4
                    @Override // android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller
                    public void fillArray(Object arr, int size, ByteBuffer buffer) {
                        DoubleBuffer db = buffer.asDoubleBuffer().get((double[]) double[].class.cast(arr), 0, size);
                        buffer.position(buffer.position() + (db.position() * 8));
                    }
                };
            }
            if (componentType == Byte.TYPE) {
                return new PrimitiveArrayFiller() { // from class: android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller.5
                    @Override // android.hardware.camera2.marshal.impl.MarshalQueryableArray.PrimitiveArrayFiller
                    public void fillArray(Object arr, int size, ByteBuffer buffer) {
                        buffer.get((byte[]) byte[].class.cast(arr), 0, size);
                    }
                };
            }
            throw new UnsupportedOperationException("PrimitiveArrayFiller of type " + componentType.getName() + " not supported");
        }
    }

    /* loaded from: classes.dex */
    private class MarshalerArray extends Marshaler<T> {
        private final Class<T> mClass;
        private final Class<?> mComponentClass;
        private final Marshaler<?> mComponentMarshaler;

        protected MarshalerArray(TypeReference<T> typeReference, int nativeType) {
            super(MarshalQueryableArray.this, typeReference, nativeType);
            this.mClass = (Class<? super T>) typeReference.getRawType();
            TypeReference<?> componentToken = typeReference.getComponentType();
            this.mComponentMarshaler = MarshalRegistry.getMarshaler(componentToken, this.mNativeType);
            this.mComponentClass = componentToken.getRawType();
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(T value, ByteBuffer buffer) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                marshalArrayElement(this.mComponentMarshaler, buffer, value, i);
            }
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public T unmarshal(ByteBuffer buffer) {
            Object array;
            int elementSize = this.mComponentMarshaler.getNativeSize();
            if (elementSize != Marshaler.NATIVE_SIZE_DYNAMIC) {
                int remaining = buffer.remaining();
                int arraySize = remaining / elementSize;
                if (remaining % elementSize != 0) {
                    throw new UnsupportedOperationException("Arrays for " + this.mTypeReference + " must be packed tighly into a multiple of " + elementSize + "; but there are " + (remaining % elementSize) + " left over bytes");
                }
                array = Array.newInstance(this.mComponentClass, arraySize);
                if (MarshalHelpers.isUnwrappedPrimitiveClass(this.mComponentClass) && this.mComponentClass == MarshalHelpers.getPrimitiveTypeClass(this.mNativeType)) {
                    PrimitiveArrayFiller.getPrimitiveArrayFiller(this.mComponentClass).fillArray(array, arraySize, buffer);
                } else {
                    for (int i = 0; i < arraySize; i++) {
                        Object elem = this.mComponentMarshaler.unmarshal(buffer);
                        Array.set(array, i, elem);
                    }
                }
            } else {
                ArrayList<Object> arrayList = new ArrayList<>();
                while (buffer.hasRemaining()) {
                    Object elem2 = this.mComponentMarshaler.unmarshal(buffer);
                    arrayList.add(elem2);
                }
                array = copyListToArray(arrayList, Array.newInstance(this.mComponentClass, arrayList.size()));
            }
            if (buffer.remaining() != 0) {
                Log.m110e(MarshalQueryableArray.TAG, "Trailing bytes (" + buffer.remaining() + ") left over after unpacking " + this.mClass);
            }
            return this.mClass.cast(array);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return NATIVE_SIZE_DYNAMIC;
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int calculateMarshalSize(T value) {
            int elementSize = this.mComponentMarshaler.getNativeSize();
            int arrayLength = Array.getLength(value);
            if (elementSize != Marshaler.NATIVE_SIZE_DYNAMIC) {
                return elementSize * arrayLength;
            }
            int size = 0;
            for (int i = 0; i < arrayLength; i++) {
                size += calculateElementMarshalSize(this.mComponentMarshaler, value, i);
            }
            return size;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private <TElem> void marshalArrayElement(Marshaler<TElem> marshaler, ByteBuffer buffer, Object array, int index) {
            marshaler.marshal(Array.get(array, index), buffer);
        }

        private Object copyListToArray(ArrayList<?> arrayList, Object arrayDest) {
            return arrayList.toArray((Object[]) arrayDest);
        }

        /* JADX WARN: Multi-variable type inference failed */
        private <TElem> int calculateElementMarshalSize(Marshaler<TElem> marshaler, Object array, int index) {
            Object elem = Array.get(array, index);
            return marshaler.calculateMarshalSize(elem);
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<T> createMarshaler(TypeReference<T> managedType, int nativeType) {
        return new MarshalerArray(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<T> managedType, int nativeType) {
        return managedType.getRawType().isArray();
    }
}
