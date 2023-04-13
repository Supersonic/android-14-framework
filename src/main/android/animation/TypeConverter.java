package android.animation;
/* loaded from: classes.dex */
public abstract class TypeConverter<T, V> {
    private Class<T> mFromClass;
    private Class<V> mToClass;

    public abstract V convert(T t);

    public TypeConverter(Class<T> fromClass, Class<V> toClass) {
        this.mFromClass = fromClass;
        this.mToClass = toClass;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<V> getTargetType() {
        return this.mToClass;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<T> getSourceType() {
        return this.mFromClass;
    }
}
