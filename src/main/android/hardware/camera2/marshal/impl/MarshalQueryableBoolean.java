package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableBoolean implements MarshalQueryable<Boolean> {

    /* loaded from: classes.dex */
    private class MarshalerBoolean extends Marshaler<Boolean> {
        protected MarshalerBoolean(TypeReference<Boolean> typeReference, int nativeType) {
            super(MarshalQueryableBoolean.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(Boolean value, ByteBuffer buffer) {
            buffer.put(value.booleanValue() ? (byte) 1 : (byte) 0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public Boolean unmarshal(ByteBuffer buffer) {
            return Boolean.valueOf(buffer.get() != 0);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 1;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<Boolean> createMarshaler(TypeReference<Boolean> managedType, int nativeType) {
        return new MarshalerBoolean(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<Boolean> managedType, int nativeType) {
        return (Boolean.class.equals(managedType.getType()) || Boolean.TYPE.equals(managedType.getType())) && nativeType == 0;
    }
}
