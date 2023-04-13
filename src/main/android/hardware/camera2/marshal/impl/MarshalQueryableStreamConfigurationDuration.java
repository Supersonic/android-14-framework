package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.StreamConfigurationDuration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableStreamConfigurationDuration implements MarshalQueryable<StreamConfigurationDuration> {
    private static final long MASK_UNSIGNED_INT = 4294967295L;
    private static final int SIZE = 32;

    /* loaded from: classes.dex */
    private class MarshalerStreamConfigurationDuration extends Marshaler<StreamConfigurationDuration> {
        protected MarshalerStreamConfigurationDuration(TypeReference<StreamConfigurationDuration> typeReference, int nativeType) {
            super(MarshalQueryableStreamConfigurationDuration.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(StreamConfigurationDuration value, ByteBuffer buffer) {
            buffer.putLong(value.getFormat() & 4294967295L);
            buffer.putLong(value.getWidth());
            buffer.putLong(value.getHeight());
            buffer.putLong(value.getDuration());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public StreamConfigurationDuration unmarshal(ByteBuffer buffer) {
            int format = (int) buffer.getLong();
            int width = (int) buffer.getLong();
            int height = (int) buffer.getLong();
            long durationNs = buffer.getLong();
            return new StreamConfigurationDuration(format, width, height, durationNs);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 32;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<StreamConfigurationDuration> createMarshaler(TypeReference<StreamConfigurationDuration> managedType, int nativeType) {
        return new MarshalerStreamConfigurationDuration(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<StreamConfigurationDuration> managedType, int nativeType) {
        return nativeType == 3 && StreamConfigurationDuration.class.equals(managedType.getType());
    }
}
