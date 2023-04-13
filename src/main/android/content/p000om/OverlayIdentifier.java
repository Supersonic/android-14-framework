package android.content.p000om;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* renamed from: android.content.om.OverlayIdentifier */
/* loaded from: classes.dex */
public final class OverlayIdentifier implements Parcelable {
    public static final Parcelable.Creator<OverlayIdentifier> CREATOR = new Parcelable.Creator<OverlayIdentifier>() { // from class: android.content.om.OverlayIdentifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OverlayIdentifier[] newArray(int size) {
            return new OverlayIdentifier[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OverlayIdentifier createFromParcel(Parcel in) {
            return new OverlayIdentifier(in);
        }
    };
    private final String mOverlayName;
    private final String mPackageName;

    public OverlayIdentifier(String packageName, String overlayName) {
        this.mPackageName = packageName;
        this.mOverlayName = overlayName;
    }

    public OverlayIdentifier(String packageName) {
        this.mPackageName = packageName;
        this.mOverlayName = null;
    }

    public String toString() {
        return this.mOverlayName == null ? this.mPackageName : this.mPackageName + ":" + this.mOverlayName;
    }

    public static OverlayIdentifier fromString(String text) {
        String[] parts = text.split(":", 2);
        if (parts.length == 2) {
            return new OverlayIdentifier(parts[0], parts[1]);
        }
        return new OverlayIdentifier(parts[0]);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getOverlayName() {
        return this.mOverlayName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OverlayIdentifier that = (OverlayIdentifier) o;
        if (Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(this.mOverlayName, that.mOverlayName)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mPackageName);
        return (_hash * 31) + Objects.hashCode(this.mOverlayName);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mPackageName != null ? (byte) (0 | 1) : (byte) 0;
        if (this.mOverlayName != null) {
            flg = (byte) (flg | 2);
        }
        dest.writeByte(flg);
        String str = this.mPackageName;
        if (str != null) {
            dest.writeString(str);
        }
        String str2 = this.mOverlayName;
        if (str2 != null) {
            dest.writeString(str2);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    OverlayIdentifier(Parcel in) {
        byte flg = in.readByte();
        String packageName = (flg & 1) == 0 ? null : in.readString();
        String overlayName = (flg & 2) != 0 ? in.readString() : null;
        this.mPackageName = packageName;
        this.mOverlayName = overlayName;
    }

    @Deprecated
    private void __metadata() {
    }
}
