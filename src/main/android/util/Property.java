package android.util;
/* loaded from: classes3.dex */
public abstract class Property<T, V> {
    private final String mName;
    private final Class<V> mType;

    public abstract V get(T t);

    /* renamed from: of */
    public static <T, V> Property<T, V> m99of(Class<T> hostType, Class<V> valueType, String name) {
        return new ReflectiveProperty(hostType, valueType, name);
    }

    public Property(Class<V> type, String name) {
        this.mName = name;
        this.mType = type;
    }

    public boolean isReadOnly() {
        return false;
    }

    public void set(T object, V value) {
        throw new UnsupportedOperationException("Property " + getName() + " is read-only");
    }

    public String getName() {
        return this.mName;
    }

    public Class<V> getType() {
        return this.mType;
    }
}
