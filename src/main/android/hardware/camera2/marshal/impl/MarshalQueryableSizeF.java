package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.SizeF;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableSizeF implements MarshalQueryable<SizeF> {
    private static final int SIZE = 8;

    /* loaded from: classes.dex */
    private class MarshalerSizeF extends Marshaler<SizeF> {
        protected MarshalerSizeF(TypeReference<SizeF> typeReference, int nativeType) {
            super(MarshalQueryableSizeF.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(SizeF value, ByteBuffer buffer) {
            buffer.putFloat(value.getWidth());
            buffer.putFloat(value.getHeight());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public SizeF unmarshal(ByteBuffer buffer) {
            float width = buffer.getFloat();
            float height = buffer.getFloat();
            return new SizeF(width, height);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 8;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<SizeF> createMarshaler(TypeReference<SizeF> managedType, int nativeType) {
        return new MarshalerSizeF(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<SizeF> managedType, int nativeType) {
        return nativeType == 2 && SizeF.class.equals(managedType.getType());
    }
}
