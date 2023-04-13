package android.p008os;

import android.p008os.Parcelable;
import java.util.Objects;
/* renamed from: android.os.TimestampedValue */
/* loaded from: classes3.dex */
public final class TimestampedValue<T> implements Parcelable {
    public static final Parcelable.Creator<TimestampedValue<?>> CREATOR = new Parcelable.ClassLoaderCreator<TimestampedValue<?>>() { // from class: android.os.TimestampedValue.1
        @Override // android.p008os.Parcelable.Creator
        public TimestampedValue<?> createFromParcel(Parcel source) {
            return createFromParcel(source, (ClassLoader) null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.ClassLoaderCreator
        public TimestampedValue<?> createFromParcel(Parcel source, ClassLoader classLoader) {
            long referenceTimeMillis = source.readLong();
            Object value = source.readValue(classLoader);
            return new TimestampedValue<>(referenceTimeMillis, value);
        }

        @Override // android.p008os.Parcelable.Creator
        public TimestampedValue[] newArray(int size) {
            return new TimestampedValue[size];
        }
    };
    private final long mReferenceTimeMillis;
    private final T mValue;

    public TimestampedValue(long referenceTimeMillis, T value) {
        this.mReferenceTimeMillis = referenceTimeMillis;
        this.mValue = value;
    }

    public long getReferenceTimeMillis() {
        return this.mReferenceTimeMillis;
    }

    public T getValue() {
        return this.mValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimestampedValue<?> that = (TimestampedValue) o;
        if (this.mReferenceTimeMillis == that.mReferenceTimeMillis && Objects.equals(this.mValue, that.mValue)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mReferenceTimeMillis), this.mValue);
    }

    public String toString() {
        return "TimestampedValue{mReferenceTimeMillis=" + this.mReferenceTimeMillis + ", mValue=" + this.mValue + '}';
    }

    public static long referenceTimeDifference(TimestampedValue<?> one, TimestampedValue<?> two) {
        return ((TimestampedValue) one).mReferenceTimeMillis - ((TimestampedValue) two).mReferenceTimeMillis;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mReferenceTimeMillis);
        dest.writeValue(this.mValue);
    }
}
