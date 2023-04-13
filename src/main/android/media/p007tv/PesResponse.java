package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.PesResponse */
/* loaded from: classes2.dex */
public final class PesResponse extends BroadcastInfoResponse implements Parcelable {
    public static final Parcelable.Creator<PesResponse> CREATOR = new Parcelable.Creator<PesResponse>() { // from class: android.media.tv.PesResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PesResponse createFromParcel(Parcel source) {
            source.readInt();
            return PesResponse.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PesResponse[] newArray(int size) {
            return new PesResponse[size];
        }
    };
    private static final int RESPONSE_TYPE = 4;
    private final String mSharedFilterToken;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PesResponse createFromParcelBody(Parcel in) {
        return new PesResponse(in);
    }

    public PesResponse(int requestId, int sequence, int responseResult, String sharedFilterToken) {
        super(4, requestId, sequence, responseResult);
        this.mSharedFilterToken = sharedFilterToken;
    }

    PesResponse(Parcel source) {
        super(4, source);
        this.mSharedFilterToken = source.readString();
    }

    public String getSharedFilterToken() {
        return this.mSharedFilterToken;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mSharedFilterToken);
    }
}
