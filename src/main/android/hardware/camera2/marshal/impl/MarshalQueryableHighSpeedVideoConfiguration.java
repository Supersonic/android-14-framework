package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.HighSpeedVideoConfiguration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableHighSpeedVideoConfiguration implements MarshalQueryable<HighSpeedVideoConfiguration> {
    private static final int SIZE = 20;

    /* loaded from: classes.dex */
    private class MarshalerHighSpeedVideoConfiguration extends Marshaler<HighSpeedVideoConfiguration> {
        protected MarshalerHighSpeedVideoConfiguration(TypeReference<HighSpeedVideoConfiguration> typeReference, int nativeType) {
            super(MarshalQueryableHighSpeedVideoConfiguration.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(HighSpeedVideoConfiguration value, ByteBuffer buffer) {
            buffer.putInt(value.getWidth());
            buffer.putInt(value.getHeight());
            buffer.putInt(value.getFpsMin());
            buffer.putInt(value.getFpsMax());
            buffer.putInt(value.getBatchSizeMax());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public HighSpeedVideoConfiguration unmarshal(ByteBuffer buffer) {
            int width = buffer.getInt();
            int height = buffer.getInt();
            int fpsMin = buffer.getInt();
            int fpsMax = buffer.getInt();
            int batchSizeMax = buffer.getInt();
            return new HighSpeedVideoConfiguration(width, height, fpsMin, fpsMax, batchSizeMax);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 20;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<HighSpeedVideoConfiguration> createMarshaler(TypeReference<HighSpeedVideoConfiguration> managedType, int nativeType) {
        return new MarshalerHighSpeedVideoConfiguration(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<HighSpeedVideoConfiguration> managedType, int nativeType) {
        return nativeType == 1 && managedType.getType().equals(HighSpeedVideoConfiguration.class);
    }
}
