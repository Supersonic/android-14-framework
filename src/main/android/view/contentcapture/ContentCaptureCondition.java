package android.view.contentcapture;

import android.content.LocusId;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.DebugUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class ContentCaptureCondition implements Parcelable {
    public static final Parcelable.Creator<ContentCaptureCondition> CREATOR = new Parcelable.Creator<ContentCaptureCondition>() { // from class: android.view.contentcapture.ContentCaptureCondition.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentCaptureCondition createFromParcel(Parcel parcel) {
            return new ContentCaptureCondition((LocusId) parcel.readParcelable(null, LocusId.class), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentCaptureCondition[] newArray(int size) {
            return new ContentCaptureCondition[size];
        }
    };
    public static final int FLAG_IS_REGEX = 2;
    private final int mFlags;
    private final LocusId mLocusId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    @interface Flags {
    }

    public ContentCaptureCondition(LocusId locusId, int flags) {
        this.mLocusId = (LocusId) Objects.requireNonNull(locusId);
        this.mFlags = flags;
    }

    public LocusId getLocusId() {
        return this.mLocusId;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int hashCode() {
        int result = (1 * 31) + this.mFlags;
        int result2 = result * 31;
        LocusId locusId = this.mLocusId;
        return result2 + (locusId == null ? 0 : locusId.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ContentCaptureCondition other = (ContentCaptureCondition) obj;
        if (this.mFlags != other.mFlags) {
            return false;
        }
        LocusId locusId = this.mLocusId;
        if (locusId == null) {
            if (other.mLocusId != null) {
                return false;
            }
        } else if (!locusId.equals(other.mLocusId)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder string = new StringBuilder(this.mLocusId.toString());
        if (this.mFlags != 0) {
            string.append(" (").append(DebugUtils.flagsToString(ContentCaptureCondition.class, "FLAG_", this.mFlags)).append(')');
        }
        return string.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mLocusId, flags);
        parcel.writeInt(this.mFlags);
    }
}
