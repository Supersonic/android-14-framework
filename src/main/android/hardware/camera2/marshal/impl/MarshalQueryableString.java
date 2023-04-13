package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
/* loaded from: classes.dex */
public class MarshalQueryableString implements MarshalQueryable<String> {
    private static final boolean DEBUG = false;
    private static final byte NUL = 0;
    private static final String TAG = MarshalQueryableString.class.getSimpleName();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PreloadHolder {
        public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

        private PreloadHolder() {
        }
    }

    /* loaded from: classes.dex */
    private class MarshalerString extends Marshaler<String> {
        protected MarshalerString(TypeReference<String> typeReference, int nativeType) {
            super(MarshalQueryableString.this, typeReference, nativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(String value, ByteBuffer buffer) {
            byte[] arr = value.getBytes(PreloadHolder.UTF8_CHARSET);
            buffer.put(arr);
            buffer.put((byte) 0);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int calculateMarshalSize(String value) {
            byte[] arr = value.getBytes(PreloadHolder.UTF8_CHARSET);
            return arr.length + 1;
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public String unmarshal(ByteBuffer buffer) {
            buffer.mark();
            boolean foundNull = false;
            int stringLength = 0;
            while (true) {
                if (!buffer.hasRemaining()) {
                    break;
                } else if (buffer.get() == 0) {
                    foundNull = true;
                    break;
                } else {
                    stringLength++;
                }
            }
            if (!foundNull) {
                throw new UnsupportedOperationException("Strings must be null-terminated");
            }
            buffer.reset();
            byte[] strBytes = new byte[stringLength + 1];
            buffer.get(strBytes, 0, stringLength + 1);
            return new String(strBytes, 0, stringLength, PreloadHolder.UTF8_CHARSET);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return NATIVE_SIZE_DYNAMIC;
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<String> createMarshaler(TypeReference<String> managedType, int nativeType) {
        return new MarshalerString(managedType, nativeType);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<String> managedType, int nativeType) {
        return nativeType == 0 && String.class.equals(managedType.getType());
    }
}
