package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.RecommendedStreamConfiguration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableRecommendedStreamConfiguration implements MarshalQueryable<RecommendedStreamConfiguration> {
    private static final int SIZE = 20;

    /* loaded from: classes.dex */
    private class MarshalerRecommendedStreamConfiguration extends Marshaler<RecommendedStreamConfiguration> {
        protected MarshalerRecommendedStreamConfiguration(TypeReference<RecommendedStreamConfiguration> typeReference, int nativeType) {
            super(MarshalQueryableRecommendedStreamConfiguration.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(RecommendedStreamConfiguration value, ByteBuffer buffer) {
            buffer.putInt(value.getWidth());
            buffer.putInt(value.getHeight());
            buffer.putInt(value.getFormat());
            buffer.putInt(value.isInput() ? 1 : 0);
            buffer.putInt(value.getUsecaseBitmap());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public RecommendedStreamConfiguration unmarshal(ByteBuffer buffer) {
            int width = buffer.getInt();
            int height = buffer.getInt();
            int format = buffer.getInt();
            boolean input = buffer.getInt() != 0;
            int usecaseBitmap = buffer.getInt();
            return new RecommendedStreamConfiguration(format, width, height, input, usecaseBitmap);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 20;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<RecommendedStreamConfiguration> createMarshaler(TypeReference<RecommendedStreamConfiguration> managedType, int nativeType) {
        return new MarshalerRecommendedStreamConfiguration(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<RecommendedStreamConfiguration> managedType, int nativeType) {
        return nativeType == 1 && managedType.getType().equals(RecommendedStreamConfiguration.class);
    }
}
