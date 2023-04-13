package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableBlackLevelPattern implements MarshalQueryable<BlackLevelPattern> {
    private static final int SIZE = 16;

    /* loaded from: classes.dex */
    private class MarshalerBlackLevelPattern extends Marshaler<BlackLevelPattern> {
        protected MarshalerBlackLevelPattern(TypeReference<BlackLevelPattern> typeReference, int nativeType) {
            super(MarshalQueryableBlackLevelPattern.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(BlackLevelPattern value, ByteBuffer buffer) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    buffer.putInt(value.getOffsetForIndex(j, i));
                }
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public BlackLevelPattern unmarshal(ByteBuffer buffer) {
            int[] channelOffsets = new int[4];
            for (int i = 0; i < 4; i++) {
                channelOffsets[i] = buffer.getInt();
            }
            return new BlackLevelPattern(channelOffsets);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 16;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<BlackLevelPattern> createMarshaler(TypeReference<BlackLevelPattern> managedType, int nativeType) {
        return new MarshalerBlackLevelPattern(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<BlackLevelPattern> managedType, int nativeType) {
        return nativeType == 1 && BlackLevelPattern.class.equals(managedType.getType());
    }
}
