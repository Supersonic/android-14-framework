package android.p008os;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.os.Parcelable */
/* loaded from: classes3.dex */
public interface Parcelable {
    public static final int CONTENTS_FILE_DESCRIPTOR = 1;
    public static final int PARCELABLE_ELIDE_DUPLICATES = 2;
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int PARCELABLE_STABILITY_LOCAL = 0;
    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public static final int PARCELABLE_STABILITY_VINTF = 1;
    public static final int PARCELABLE_WRITE_RETURN_VALUE = 1;

    /* renamed from: android.os.Parcelable$ClassLoaderCreator */
    /* loaded from: classes3.dex */
    public interface ClassLoaderCreator<T> extends Creator<T> {
        T createFromParcel(Parcel parcel, ClassLoader classLoader);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.Parcelable$ContentsFlags */
    /* loaded from: classes3.dex */
    public @interface ContentsFlags {
    }

    /* renamed from: android.os.Parcelable$Creator */
    /* loaded from: classes3.dex */
    public interface Creator<T> {
        T createFromParcel(Parcel parcel);

        T[] newArray(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.Parcelable$Stability */
    /* loaded from: classes3.dex */
    public @interface Stability {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.Parcelable$WriteFlags */
    /* loaded from: classes3.dex */
    public @interface WriteFlags {
    }

    int describeContents();

    void writeToParcel(Parcel parcel, int i);

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    default int getStability() {
        return 0;
    }
}
