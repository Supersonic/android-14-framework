package android.animation;
/* loaded from: classes.dex */
public abstract class BidirectionalTypeConverter<T, V> extends TypeConverter<T, V> {
    private BidirectionalTypeConverter mInvertedConverter;

    public abstract T convertBack(V v);

    public BidirectionalTypeConverter(Class<T> fromClass, Class<V> toClass) {
        super(fromClass, toClass);
    }

    public BidirectionalTypeConverter<V, T> invert() {
        if (this.mInvertedConverter == null) {
            this.mInvertedConverter = new InvertedConverter(this);
        }
        return this.mInvertedConverter;
    }

    /* loaded from: classes.dex */
    private static class InvertedConverter<From, To> extends BidirectionalTypeConverter<From, To> {
        private BidirectionalTypeConverter<To, From> mConverter;

        public InvertedConverter(BidirectionalTypeConverter<To, From> converter) {
            super(converter.getTargetType(), converter.getSourceType());
            this.mConverter = converter;
        }

        @Override // android.animation.BidirectionalTypeConverter
        public From convertBack(To value) {
            return this.mConverter.convert(value);
        }

        @Override // android.animation.TypeConverter
        public To convert(From value) {
            return this.mConverter.convertBack(value);
        }
    }
}
