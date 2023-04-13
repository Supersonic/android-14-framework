package android.hardware.camera2.marshal;

import android.hardware.camera2.utils.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class MarshalRegistry {
    private static final Object sMarshalLock = new Object();
    private static final List<MarshalQueryable<?>> sRegisteredMarshalQueryables = new ArrayList();
    private static final HashMap<MarshalToken<?>, Marshaler<?>> sMarshalerMap = new HashMap<>();

    public static <T> void registerMarshalQueryable(MarshalQueryable<T> queryable) {
        synchronized (sMarshalLock) {
            sRegisteredMarshalQueryables.add(queryable);
        }
    }

    public static <T> Marshaler<T> getMarshaler(TypeReference<T> typeToken, int nativeType) {
        Marshaler<T> marshaler;
        synchronized (sMarshalLock) {
            MarshalToken<?> marshalToken = new MarshalToken<>(typeToken, nativeType);
            marshaler = (Marshaler<T>) sMarshalerMap.get(marshalToken);
            if (marshaler == null) {
                List<MarshalQueryable<?>> list = sRegisteredMarshalQueryables;
                if (list.size() == 0) {
                    throw new AssertionError("No available query marshalers registered");
                }
                Iterator<MarshalQueryable<?>> it = list.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    MarshalQueryable<?> potentialMarshaler = it.next();
                    if (potentialMarshaler.isTypeMappingSupported(typeToken, nativeType)) {
                        marshaler = (Marshaler<T>) potentialMarshaler.createMarshaler(typeToken, nativeType);
                        break;
                    }
                }
                if (marshaler == null) {
                    throw new UnsupportedOperationException("Could not find marshaler that matches the requested combination of type reference " + typeToken + " and native type " + MarshalHelpers.toStringNativeType(nativeType));
                }
                sMarshalerMap.put(marshalToken, marshaler);
            }
        }
        return marshaler;
    }

    /* loaded from: classes.dex */
    private static class MarshalToken<T> {
        private final int hash;
        final int nativeType;
        final TypeReference<T> typeReference;

        public MarshalToken(TypeReference<T> typeReference, int nativeType) {
            this.typeReference = typeReference;
            this.nativeType = nativeType;
            this.hash = typeReference.hashCode() ^ nativeType;
        }

        public boolean equals(Object other) {
            if (other instanceof MarshalToken) {
                MarshalToken<?> otherToken = (MarshalToken) other;
                return this.typeReference.equals(otherToken.typeReference) && this.nativeType == otherToken.nativeType;
            }
            return false;
        }

        public int hashCode() {
            return this.hash;
        }
    }

    private MarshalRegistry() {
        throw new AssertionError();
    }
}
