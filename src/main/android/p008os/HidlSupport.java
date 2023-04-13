package android.p008os;

import android.annotation.SystemApi;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
@SystemApi
/* renamed from: android.os.HidlSupport */
/* loaded from: classes3.dex */
public class HidlSupport {
    @SystemApi
    public static native int getPidIfSharable();

    @SystemApi
    public static boolean deepEquals(Object lft, Object rgt) {
        Class<?> lftClazz;
        Class<?> rgtClazz;
        if (lft == rgt) {
            return true;
        }
        if (lft == null || rgt == null || (lftClazz = lft.getClass()) != (rgtClazz = rgt.getClass())) {
            return false;
        }
        if (lftClazz.isArray()) {
            Class<?> lftElementType = lftClazz.getComponentType();
            if (lftElementType != rgtClazz.getComponentType()) {
                return false;
            }
            if (lftElementType.isPrimitive()) {
                return Objects.deepEquals(lft, rgt);
            }
            final Object[] lftArray = (Object[]) lft;
            final Object[] rgtArray = (Object[]) rgt;
            if (lftArray.length == rgtArray.length && IntStream.range(0, lftArray.length).allMatch(new IntPredicate() { // from class: android.os.HidlSupport$$ExternalSyntheticLambda2
                @Override // java.util.function.IntPredicate
                public final boolean test(int i) {
                    boolean deepEquals;
                    deepEquals = HidlSupport.deepEquals(lftArray[i], rgtArray[i]);
                    return deepEquals;
                }
            })) {
                return true;
            }
            return false;
        } else if (lft instanceof List) {
            List<Object> lftList = (List) lft;
            List<Object> rgtList = (List) rgt;
            if (lftList.size() != rgtList.size()) {
                return false;
            }
            final Iterator<Object> lftIter = lftList.iterator();
            return rgtList.stream().allMatch(new Predicate() { // from class: android.os.HidlSupport$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean deepEquals;
                    deepEquals = HidlSupport.deepEquals(lftIter.next(), obj);
                    return deepEquals;
                }
            });
        } else {
            throwErrorIfUnsupportedType(lft);
            return lft.equals(rgt);
        }
    }

    /* renamed from: android.os.HidlSupport$Mutable */
    /* loaded from: classes3.dex */
    public static final class Mutable<E> {
        public E value;

        public Mutable() {
            this.value = null;
        }

        public Mutable(E value) {
            this.value = value;
        }
    }

    @SystemApi
    public static int deepHashCode(Object o) {
        if (o == null) {
            return 0;
        }
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            Class<?> elementType = clazz.getComponentType();
            if (elementType.isPrimitive()) {
                return primitiveArrayHashCode(o);
            }
            return Arrays.hashCode(Arrays.stream((Object[]) o).mapToInt(new ToIntFunction() { // from class: android.os.HidlSupport$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int deepHashCode;
                    deepHashCode = HidlSupport.deepHashCode(obj);
                    return deepHashCode;
                }
            }).toArray());
        } else if (o instanceof List) {
            return Arrays.hashCode(((List) o).stream().mapToInt(new ToIntFunction() { // from class: android.os.HidlSupport$$ExternalSyntheticLambda1
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int deepHashCode;
                    deepHashCode = HidlSupport.deepHashCode(obj);
                    return deepHashCode;
                }
            }).toArray());
        } else {
            throwErrorIfUnsupportedType(o);
            return o.hashCode();
        }
    }

    private static void throwErrorIfUnsupportedType(Object o) {
        if ((o instanceof Collection) && !(o instanceof List)) {
            throw new UnsupportedOperationException("Cannot check equality on collections other than lists: " + o.getClass().getName());
        }
        if (o instanceof Map) {
            throw new UnsupportedOperationException("Cannot check equality on maps");
        }
    }

    private static int primitiveArrayHashCode(Object o) {
        Class<?> elementType = o.getClass().getComponentType();
        if (elementType == Boolean.TYPE) {
            return Arrays.hashCode((boolean[]) o);
        }
        if (elementType == Byte.TYPE) {
            return Arrays.hashCode((byte[]) o);
        }
        if (elementType == Character.TYPE) {
            return Arrays.hashCode((char[]) o);
        }
        if (elementType == Double.TYPE) {
            return Arrays.hashCode((double[]) o);
        }
        if (elementType == Float.TYPE) {
            return Arrays.hashCode((float[]) o);
        }
        if (elementType == Integer.TYPE) {
            return Arrays.hashCode((int[]) o);
        }
        if (elementType == Long.TYPE) {
            return Arrays.hashCode((long[]) o);
        }
        if (elementType == Short.TYPE) {
            return Arrays.hashCode((short[]) o);
        }
        throw new UnsupportedOperationException();
    }

    @SystemApi
    public static boolean interfacesEqual(IHwInterface lft, Object rgt) {
        if (lft == rgt) {
            return true;
        }
        if (lft == null || rgt == null || !(rgt instanceof IHwInterface)) {
            return false;
        }
        return Objects.equals(lft.asBinder(), ((IHwInterface) rgt).asBinder());
    }
}
