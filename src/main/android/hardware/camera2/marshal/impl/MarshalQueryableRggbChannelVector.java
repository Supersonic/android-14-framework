package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.RggbChannelVector;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableRggbChannelVector implements MarshalQueryable<RggbChannelVector> {
    private static final int SIZE = 16;

    /* loaded from: classes.dex */
    private class MarshalerRggbChannelVector extends Marshaler<RggbChannelVector> {
        protected MarshalerRggbChannelVector(TypeReference<RggbChannelVector> typeReference, int nativeType) {
            super(MarshalQueryableRggbChannelVector.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(RggbChannelVector value, ByteBuffer buffer) {
            for (int i = 0; i < 4; i++) {
                buffer.putFloat(value.getComponent(i));
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public RggbChannelVector unmarshal(ByteBuffer buffer) {
            float red = buffer.getFloat();
            float gEven = buffer.getFloat();
            float gOdd = buffer.getFloat();
            float blue = buffer.getFloat();
            return new RggbChannelVector(red, gEven, gOdd, blue);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 16;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<RggbChannelVector> createMarshaler(TypeReference<RggbChannelVector> managedType, int nativeType) {
        return new MarshalerRggbChannelVector(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<RggbChannelVector> managedType, int nativeType) {
        return nativeType == 2 && RggbChannelVector.class.equals(managedType.getType());
    }
}
