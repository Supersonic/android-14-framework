package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.SectionRequest */
/* loaded from: classes2.dex */
public final class SectionRequest extends BroadcastInfoRequest implements Parcelable {
    public static final Parcelable.Creator<SectionRequest> CREATOR = new Parcelable.Creator<SectionRequest>() { // from class: android.media.tv.SectionRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SectionRequest createFromParcel(Parcel source) {
            source.readInt();
            return SectionRequest.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SectionRequest[] newArray(int size) {
            return new SectionRequest[size];
        }
    };
    private static final int REQUEST_TYPE = 3;
    private final int mTableId;
    private final int mTsPid;
    private final int mVersion;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SectionRequest createFromParcelBody(Parcel in) {
        return new SectionRequest(in);
    }

    public SectionRequest(int requestId, int option, int tsPid, int tableId, int version) {
        super(3, requestId, option);
        this.mTsPid = tsPid;
        this.mTableId = tableId;
        this.mVersion = version;
    }

    SectionRequest(Parcel source) {
        super(3, source);
        this.mTsPid = source.readInt();
        this.mTableId = source.readInt();
        this.mVersion = source.readInt();
    }

    public int getTsPid() {
        return this.mTsPid;
    }

    public int getTableId() {
        return this.mTableId;
    }

    public int getVersion() {
        return this.mVersion;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mTsPid);
        dest.writeInt(this.mTableId);
        dest.writeInt(this.mVersion);
    }
}
