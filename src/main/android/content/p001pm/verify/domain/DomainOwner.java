package android.content.p001pm.verify.domain;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
@SystemApi
/* renamed from: android.content.pm.verify.domain.DomainOwner */
/* loaded from: classes.dex */
public final class DomainOwner implements Parcelable {
    public static final Parcelable.Creator<DomainOwner> CREATOR = new Parcelable.Creator<DomainOwner>() { // from class: android.content.pm.verify.domain.DomainOwner.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DomainOwner[] newArray(int size) {
            return new DomainOwner[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DomainOwner createFromParcel(Parcel in) {
            return new DomainOwner(in);
        }
    };
    private final boolean mOverrideable;
    private final String mPackageName;

    public DomainOwner(String packageName, boolean overrideable) {
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mOverrideable = overrideable;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean isOverrideable() {
        return this.mOverrideable;
    }

    public String toString() {
        return "DomainOwner { packageName = " + this.mPackageName + ", overrideable = " + this.mOverrideable + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainOwner that = (DomainOwner) o;
        if (Objects.equals(this.mPackageName, that.mPackageName) && this.mOverrideable == that.mOverrideable) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mPackageName);
        return (_hash * 31) + Boolean.hashCode(this.mOverrideable);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mOverrideable ? (byte) (0 | 2) : (byte) 0;
        dest.writeByte(flg);
        dest.writeString(this.mPackageName);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    DomainOwner(Parcel in) {
        byte flg = in.readByte();
        boolean overrideable = (flg & 2) != 0;
        String packageName = in.readString();
        this.mPackageName = packageName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        this.mOverrideable = overrideable;
    }

    @Deprecated
    private void __metadata() {
    }
}
