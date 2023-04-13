package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.StreamConfiguration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableStreamConfiguration implements MarshalQueryable<StreamConfiguration> {
    private static final int SIZE = 16;

    /* loaded from: classes.dex */
    private class MarshalerStreamConfiguration extends Marshaler<StreamConfiguration> {
        protected MarshalerStreamConfiguration(TypeReference<StreamConfiguration> typeReference, int nativeType) {
            super(MarshalQueryableStreamConfiguration.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(StreamConfiguration value, ByteBuffer buffer) {
            buffer.putInt(value.getFormat());
            buffer.putInt(value.getWidth());
            buffer.putInt(value.getHeight());
            buffer.putInt(value.isInput() ? 1 : 0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public StreamConfiguration unmarshal(ByteBuffer buffer) {
            int format = buffer.getInt();
            int width = buffer.getInt();
            int height = buffer.getInt();
            boolean input = buffer.getInt() != 0;
            return new StreamConfiguration(format, width, height, input);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 16;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<StreamConfiguration> createMarshaler(TypeReference<StreamConfiguration> managedType, int nativeType) {
        return new MarshalerStreamConfiguration(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<StreamConfiguration> managedType, int nativeType) {
        return nativeType == 1 && managedType.getType().equals(StreamConfiguration.class);
    }
}
