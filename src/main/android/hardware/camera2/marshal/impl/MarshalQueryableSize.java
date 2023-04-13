package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Size;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableSize implements MarshalQueryable<Size> {
    private static final int SIZE = 8;

    /* loaded from: classes.dex */
    private class MarshalerSize extends Marshaler<Size> {
        protected MarshalerSize(TypeReference<Size> typeReference, int nativeType) {
            super(MarshalQueryableSize.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(Size value, ByteBuffer buffer) {
            buffer.putInt(value.getWidth());
            buffer.putInt(value.getHeight());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public Size unmarshal(ByteBuffer buffer) {
            int width = buffer.getInt();
            int height = buffer.getInt();
            return new Size(width, height);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 8;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<Size> createMarshaler(TypeReference<Size> managedType, int nativeType) {
        return new MarshalerSize(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<Size> managedType, int nativeType) {
        return nativeType == 1 && Size.class.equals(managedType.getType());
    }
}
