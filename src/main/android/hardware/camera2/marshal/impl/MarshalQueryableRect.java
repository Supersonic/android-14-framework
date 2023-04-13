package android.hardware.camera2.marshal.impl;

import android.graphics.Rect;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class MarshalQueryableRect implements MarshalQueryable<Rect> {
    private static final int SIZE = 16;

    /* loaded from: classes.dex */
    private class MarshalerRect extends Marshaler<Rect> {
        protected MarshalerRect(TypeReference<Rect> typeReference, int nativeType) {
            super(MarshalQueryableRect.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(Rect value, ByteBuffer buffer) {
            buffer.putInt(value.left);
            buffer.putInt(value.top);
            buffer.putInt(value.width());
            buffer.putInt(value.height());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public Rect unmarshal(ByteBuffer buffer) {
            int left = buffer.getInt();
            int top = buffer.getInt();
            int width = buffer.getInt();
            int height = buffer.getInt();
            int right = left + width;
            int bottom = top + height;
            return new Rect(left, top, right, bottom);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 16;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<Rect> createMarshaler(TypeReference<Rect> managedType, int nativeType) {
        return new MarshalerRect(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<Rect> managedType, int nativeType) {
        return nativeType == 1 && Rect.class.equals(managedType.getType());
    }
}
