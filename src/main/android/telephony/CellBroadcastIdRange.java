package android.telephony;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class CellBroadcastIdRange implements Parcelable {
    public static final Parcelable.Creator<CellBroadcastIdRange> CREATOR = new Parcelable.Creator<CellBroadcastIdRange>() { // from class: android.telephony.CellBroadcastIdRange.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellBroadcastIdRange createFromParcel(Parcel in) {
            int startId = in.readInt();
            int endId = in.readInt();
            int type = in.readInt();
            boolean isEnabled = in.readBoolean();
            return new CellBroadcastIdRange(startId, endId, type, isEnabled);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellBroadcastIdRange[] newArray(int size) {
            return new CellBroadcastIdRange[size];
        }
    };
    private int mEndId;
    private boolean mIsEnabled;
    private int mStartId;
    private int mType;

    public CellBroadcastIdRange(int startId, int endId, int type, boolean isEnabled) throws IllegalArgumentException {
        if (startId < 0 || endId < 0 || startId > 65535 || endId > 65535) {
            throw new IllegalArgumentException("invalid id");
        }
        if (endId < startId) {
            throw new IllegalArgumentException("endId must be greater than or equal to startId");
        }
        this.mStartId = startId;
        this.mEndId = endId;
        this.mType = type;
        this.mIsEnabled = isEnabled;
    }

    public int getStartId() {
        return this.mStartId;
    }

    public int getEndId() {
        return this.mEndId;
    }

    public int getType() {
        return this.mType;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mStartId);
        out.writeInt(this.mEndId);
        out.writeInt(this.mType);
        out.writeBoolean(this.mIsEnabled);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mStartId), Integer.valueOf(this.mEndId), Integer.valueOf(this.mType), Boolean.valueOf(this.mIsEnabled));
    }

    public boolean equals(Object obj) {
        if (obj instanceof CellBroadcastIdRange) {
            CellBroadcastIdRange other = (CellBroadcastIdRange) obj;
            return this.mStartId == other.mStartId && this.mEndId == other.mEndId && this.mType == other.mType && this.mIsEnabled == other.mIsEnabled;
        }
        return false;
    }

    public String toString() {
        return "CellBroadcastIdRange[" + this.mStartId + ", " + this.mEndId + ", " + this.mType + ", " + this.mIsEnabled + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
