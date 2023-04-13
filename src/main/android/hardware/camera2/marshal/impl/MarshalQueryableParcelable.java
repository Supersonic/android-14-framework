package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableParcelable<T extends Parcelable> implements MarshalQueryable<T> {
    private static final boolean DEBUG = false;
    private static final String FIELD_CREATOR = "CREATOR";
    private static final String TAG = "MarshalParcelable";

    /* loaded from: classes.dex */
    private class MarshalerParcelable extends Marshaler<T> {
        private final Class<T> mClass;
        private final Parcelable.Creator<T> mCreator;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.hardware.camera2.marshal.Marshaler
        public /* bridge */ /* synthetic */ int calculateMarshalSize(Object obj) {
            return calculateMarshalSize((MarshalerParcelable) ((Parcelable) obj));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.hardware.camera2.marshal.Marshaler
        public /* bridge */ /* synthetic */ void marshal(Object obj, ByteBuffer byteBuffer) {
            marshal((MarshalerParcelable) ((Parcelable) obj), byteBuffer);
        }

        protected MarshalerParcelable(TypeReference<T> typeReference, int nativeType) {
            super(MarshalQueryableParcelable.this, typeReference, nativeType);
            Class cls = (Class<? super T>) typeReference.getRawType();
            this.mClass = cls;
            try {
                Field creatorField = cls.getDeclaredField(MarshalQueryableParcelable.FIELD_CREATOR);
                try {
                    this.mCreator = (Parcelable.Creator) creatorField.get(null);
                } catch (IllegalAccessException e) {
                    throw new AssertionError(e);
                } catch (IllegalArgumentException e2) {
                    throw new AssertionError(e2);
                }
            } catch (NoSuchFieldException e3) {
                throw new AssertionError(e3);
            }
        }

        public void marshal(T value, ByteBuffer buffer) {
            Parcel parcel = Parcel.obtain();
            try {
                value.writeToParcel(parcel, 0);
                if (parcel.hasFileDescriptors()) {
                    throw new UnsupportedOperationException("Parcelable " + value + " must not have file descriptors");
                }
                byte[] parcelContents = parcel.marshall();
                parcel.recycle();
                if (parcelContents.length == 0) {
                    throw new AssertionError("No data marshaled for " + value);
                }
                buffer.put(parcelContents);
            } catch (Throwable th) {
                parcel.recycle();
                throw th;
            }
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public T unmarshal(ByteBuffer buffer) {
            buffer.mark();
            Parcel parcel = Parcel.obtain();
            try {
                int maxLength = buffer.remaining();
                byte[] remaining = new byte[maxLength];
                buffer.get(remaining);
                parcel.unmarshall(remaining, 0, maxLength);
                parcel.setDataPosition(0);
                T value = this.mCreator.createFromParcel(parcel);
                int actualLength = parcel.dataPosition();
                if (actualLength == 0) {
                    throw new AssertionError("No data marshaled for " + value);
                }
                buffer.reset();
                buffer.position(buffer.position() + actualLength);
                return this.mClass.cast(value);
            } finally {
                parcel.recycle();
            }
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return NATIVE_SIZE_DYNAMIC;
        }

        public int calculateMarshalSize(T value) {
            Parcel parcel = Parcel.obtain();
            try {
                value.writeToParcel(parcel, 0);
                int length = parcel.marshall().length;
                return length;
            } finally {
                parcel.recycle();
            }
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<T> createMarshaler(TypeReference<T> managedType, int nativeType) {
        return new MarshalerParcelable(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<T> managedType, int nativeType) {
        return Parcelable.class.isAssignableFrom(managedType.getRawType());
    }
}
