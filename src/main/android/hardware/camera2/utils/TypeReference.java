package android.hardware.camera2.utils;

import com.android.internal.util.Preconditions;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
/* loaded from: classes.dex */
public abstract class TypeReference<T> {
    private final int mHash;
    private final Type mType;

    /* JADX INFO: Access modifiers changed from: protected */
    public TypeReference() {
        ParameterizedType thisType = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = thisType.getActualTypeArguments()[0];
        this.mType = type;
        if (containsTypeVariable(type)) {
            throw new IllegalArgumentException("Including a type variable in a type reference is not allowed");
        }
        this.mHash = type.hashCode();
    }

    public Type getType() {
        return this.mType;
    }

    private TypeReference(Type type) {
        this.mType = type;
        if (containsTypeVariable(type)) {
            throw new IllegalArgumentException("Including a type variable in a type reference is not allowed");
        }
        this.mHash = type.hashCode();
    }

    /* loaded from: classes.dex */
    private static class SpecializedTypeReference<T> extends TypeReference<T> {
        public SpecializedTypeReference(Class<T> klass) {
            super(klass);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SpecializedBaseTypeReference extends TypeReference {
        public SpecializedBaseTypeReference(Type type) {
            super(type);
        }
    }

    public static <T> TypeReference<T> createSpecializedTypeReference(Class<T> klass) {
        return new SpecializedTypeReference(klass);
    }

    public static TypeReference<?> createSpecializedTypeReference(Type type) {
        return new SpecializedBaseTypeReference(type);
    }

    public final Class<? super T> getRawType() {
        return (Class<? super T>) getRawType(this.mType);
    }

    private static final Class<?> getRawType(Type type) {
        if (type == null) {
            throw new NullPointerException("type must not be null");
        }
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            return getArrayClass(getRawType(((GenericArrayType) type).getGenericComponentType()));
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds());
        }
        if (type instanceof TypeVariable) {
            throw new AssertionError("Type variables are not allowed in type references");
        }
        throw new AssertionError("Unhandled branch to get raw type for type " + type);
    }

    private static final Class<?> getRawType(Type[] types) {
        if (types == null) {
            return null;
        }
        for (Type type : types) {
            Class<?> klass = getRawType(type);
            if (klass != null) {
                return klass;
            }
        }
        return null;
    }

    private static final Class<?> getArrayClass(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    public TypeReference<?> getComponentType() {
        Type componentType = getComponentType(this.mType);
        if (componentType != null) {
            return createSpecializedTypeReference(componentType);
        }
        return null;
    }

    private static Type getComponentType(Type type) {
        Preconditions.checkNotNull(type, "type must not be null");
        if (type instanceof Class) {
            return ((Class) type).getComponentType();
        }
        if (type instanceof ParameterizedType) {
            return null;
        }
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        if (type instanceof WildcardType) {
            throw new UnsupportedOperationException("TODO: support wild card components");
        }
        if (type instanceof TypeVariable) {
            throw new AssertionError("Type variables are not allowed in type references");
        }
        throw new AssertionError("Unhandled branch to get component type for type " + type);
    }

    public boolean equals(Object o) {
        return (o instanceof TypeReference) && this.mType.equals(((TypeReference) o).mType);
    }

    public int hashCode() {
        return this.mHash;
    }

    public static boolean containsTypeVariable(Type type) {
        Type[] actualTypeArguments;
        if (type == null) {
            return false;
        }
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof Class) {
            Class<?> klass = (Class) type;
            if (klass.getTypeParameters().length != 0) {
                return true;
            }
            return containsTypeVariable(klass.getDeclaringClass());
        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            for (Type arg : p.getActualTypeArguments()) {
                if (containsTypeVariable(arg)) {
                    return true;
                }
            }
            return false;
        } else if (!(type instanceof WildcardType)) {
            return false;
        } else {
            WildcardType wild = (WildcardType) type;
            return containsTypeVariable(wild.getLowerBounds()) || containsTypeVariable(wild.getUpperBounds());
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TypeReference<");
        toString(getType(), builder);
        builder.append(">");
        return builder.toString();
    }

    private static void toString(Type type, StringBuilder out) {
        if (type == null) {
            return;
        }
        if (type instanceof TypeVariable) {
            out.append(((TypeVariable) type).getName());
        } else if (type instanceof Class) {
            Class<?> klass = (Class) type;
            out.append(klass.getName());
            toString(klass.getTypeParameters(), out);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            out.append(((Class) p.getRawType()).getName());
            toString(p.getActualTypeArguments(), out);
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType) type;
            toString(gat.getGenericComponentType(), out);
            out.append("[]");
        } else {
            out.append(type.toString());
        }
    }

    private static void toString(Type[] types, StringBuilder out) {
        if (types == null || types.length == 0) {
            return;
        }
        out.append("<");
        for (int i = 0; i < types.length; i++) {
            toString(types[i], out);
            if (i != types.length - 1) {
                out.append(", ");
            }
        }
        out.append(">");
    }

    private static boolean containsTypeVariable(Type[] typeArray) {
        if (typeArray == null) {
            return false;
        }
        for (Type type : typeArray) {
            if (containsTypeVariable(type)) {
                return true;
            }
        }
        return false;
    }
}
