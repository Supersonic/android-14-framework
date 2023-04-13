package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalHelpers;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Rational;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public final class MarshalQueryablePrimitive<T> implements MarshalQueryable<T> {

    /* loaded from: classes.dex */
    private class MarshalerPrimitive extends Marshaler<T> {
        private final Class<T> mClass;

        protected MarshalerPrimitive(TypeReference<T> typeReference, int nativeType) {
            super(MarshalQueryablePrimitive.this, typeReference, nativeType);
            this.mClass = MarshalHelpers.wrapClassIfPrimitive(typeReference.getRawType());
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public T unmarshal(ByteBuffer buffer) {
            return this.mClass.cast(unmarshalObject(buffer));
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int calculateMarshalSize(T value) {
            return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(T value, ByteBuffer buffer) {
            if (value instanceof Integer) {
                MarshalHelpers.checkNativeTypeEquals(1, this.mNativeType);
                int val = ((Integer) value).intValue();
                marshalPrimitive(val, buffer);
            } else if (value instanceof Float) {
                MarshalHelpers.checkNativeTypeEquals(2, this.mNativeType);
                float val2 = ((Float) value).floatValue();
                marshalPrimitive(val2, buffer);
            } else if (value instanceof Long) {
                MarshalHelpers.checkNativeTypeEquals(3, this.mNativeType);
                long val3 = ((Long) value).longValue();
                marshalPrimitive(val3, buffer);
            } else if (value instanceof Rational) {
                MarshalHelpers.checkNativeTypeEquals(5, this.mNativeType);
                marshalPrimitive((Rational) value, buffer);
            } else if (value instanceof Double) {
                MarshalHelpers.checkNativeTypeEquals(4, this.mNativeType);
                double val4 = ((Double) value).doubleValue();
                marshalPrimitive(val4, buffer);
            } else if (value instanceof Byte) {
                MarshalHelpers.checkNativeTypeEquals(0, this.mNativeType);
                byte val5 = ((Byte) value).byteValue();
                marshalPrimitive(val5, buffer);
            } else {
                throw new UnsupportedOperationException("Can't marshal managed type " + this.mTypeReference);
            }
        }

        private void marshalPrimitive(int value, ByteBuffer buffer) {
            buffer.putInt(value);
        }

        private void marshalPrimitive(float value, ByteBuffer buffer) {
            buffer.putFloat(value);
        }

        private void marshalPrimitive(double value, ByteBuffer buffer) {
            buffer.putDouble(value);
        }

        private void marshalPrimitive(long value, ByteBuffer buffer) {
            buffer.putLong(value);
        }

        private void marshalPrimitive(Rational value, ByteBuffer buffer) {
            buffer.putInt(value.getNumerator());
            buffer.putInt(value.getDenominator());
        }

        private void marshalPrimitive(byte value, ByteBuffer buffer) {
            buffer.put(value);
        }

        private Object unmarshalObject(ByteBuffer buffer) {
            switch (this.mNativeType) {
                case 0:
                    return Byte.valueOf(buffer.get());
                case 1:
                    return Integer.valueOf(buffer.getInt());
                case 2:
                    return Float.valueOf(buffer.getFloat());
                case 3:
                    return Long.valueOf(buffer.getLong());
                case 4:
                    return Double.valueOf(buffer.getDouble());
                case 5:
                    int numerator = buffer.getInt();
                    int denominator = buffer.getInt();
                    return new Rational(numerator, denominator);
                default:
                    throw new UnsupportedOperationException("Can't unmarshal native type " + this.mNativeType);
            }
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<T> createMarshaler(TypeReference<T> managedType, int nativeType) {
        return new MarshalerPrimitive(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<T> managedType, int nativeType) {
        if (managedType.getType() instanceof Class) {
            Class<?> klass = (Class) managedType.getType();
            return (klass == Byte.TYPE || klass == Byte.class) ? nativeType == 0 : (klass == Integer.TYPE || klass == Integer.class) ? nativeType == 1 : (klass == Float.TYPE || klass == Float.class) ? nativeType == 2 : (klass == Long.TYPE || klass == Long.class) ? nativeType == 3 : (klass == Double.TYPE || klass == Double.class) ? nativeType == 4 : klass == Rational.class && nativeType == 5;
        }
        return false;
    }
}
