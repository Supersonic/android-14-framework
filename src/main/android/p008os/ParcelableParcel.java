package android.p008os;

import android.p008os.Parcelable;
import android.util.MathUtils;
/* renamed from: android.os.ParcelableParcel */
/* loaded from: classes3.dex */
public class ParcelableParcel implements Parcelable {
    public static final Parcelable.ClassLoaderCreator<ParcelableParcel> CREATOR = new Parcelable.ClassLoaderCreator<ParcelableParcel>() { // from class: android.os.ParcelableParcel.1
        @Override // android.p008os.Parcelable.Creator
        public ParcelableParcel createFromParcel(Parcel in) {
            return new ParcelableParcel(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.ClassLoaderCreator
        public ParcelableParcel createFromParcel(Parcel in, ClassLoader loader) {
            return new ParcelableParcel(in, loader);
        }

        @Override // android.p008os.Parcelable.Creator
        public ParcelableParcel[] newArray(int size) {
            return new ParcelableParcel[size];
        }
    };
    final ClassLoader mClassLoader;
    final Parcel mParcel;

    public ParcelableParcel(ClassLoader loader) {
        this.mParcel = Parcel.obtain();
        this.mClassLoader = loader;
    }

    public ParcelableParcel(Parcel src, ClassLoader loader) {
        Parcel obtain = Parcel.obtain();
        this.mParcel = obtain;
        this.mClassLoader = loader;
        int size = src.readInt();
        if (size < 0) {
            throw new IllegalArgumentException("Negative size read from parcel");
        }
        int pos = src.dataPosition();
        src.setDataPosition(MathUtils.addOrThrow(pos, size));
        obtain.appendFrom(src, pos, size);
    }

    public Parcel getParcel() {
        this.mParcel.setDataPosition(0);
        return this.mParcel;
    }

    public ClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mParcel.dataSize());
        Parcel parcel = this.mParcel;
        dest.appendFrom(parcel, 0, parcel.dataSize());
    }
}
