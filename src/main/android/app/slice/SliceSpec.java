package android.app.slice;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class SliceSpec implements Parcelable {
    public static final Parcelable.Creator<SliceSpec> CREATOR = new Parcelable.Creator<SliceSpec>() { // from class: android.app.slice.SliceSpec.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SliceSpec createFromParcel(Parcel source) {
            return new SliceSpec(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SliceSpec[] newArray(int size) {
            return new SliceSpec[size];
        }
    };
    private final int mRevision;
    private final String mType;

    public SliceSpec(String type, int revision) {
        this.mType = type;
        this.mRevision = revision;
    }

    public SliceSpec(Parcel source) {
        this.mType = source.readString();
        this.mRevision = source.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mType);
        dest.writeInt(this.mRevision);
    }

    public String getType() {
        return this.mType;
    }

    public int getRevision() {
        return this.mRevision;
    }

    public boolean canRender(SliceSpec candidate) {
        return this.mType.equals(candidate.mType) && this.mRevision >= candidate.mRevision;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SliceSpec) {
            SliceSpec other = (SliceSpec) obj;
            return this.mType.equals(other.mType) && this.mRevision == other.mRevision;
        }
        return false;
    }

    public String toString() {
        return String.format("SliceSpec{%s,%d}", this.mType, Integer.valueOf(this.mRevision));
    }
}
