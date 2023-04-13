package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.PesRequest */
/* loaded from: classes2.dex */
public final class PesRequest extends BroadcastInfoRequest implements Parcelable {
    public static final Parcelable.Creator<PesRequest> CREATOR = new Parcelable.Creator<PesRequest>() { // from class: android.media.tv.PesRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PesRequest createFromParcel(Parcel source) {
            source.readInt();
            return PesRequest.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PesRequest[] newArray(int size) {
            return new PesRequest[size];
        }
    };
    private static final int REQUEST_TYPE = 4;
    private final int mStreamId;
    private final int mTsPid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PesRequest createFromParcelBody(Parcel in) {
        return new PesRequest(in);
    }

    public PesRequest(int requestId, int option, int tsPid, int streamId) {
        super(4, requestId, option);
        this.mTsPid = tsPid;
        this.mStreamId = streamId;
    }

    PesRequest(Parcel source) {
        super(4, source);
        this.mTsPid = source.readInt();
        this.mStreamId = source.readInt();
    }

    public int getTsPid() {
        return this.mTsPid;
    }

    public int getStreamId() {
        return this.mStreamId;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoRequest, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mTsPid);
        dest.writeInt(this.mStreamId);
    }
}
