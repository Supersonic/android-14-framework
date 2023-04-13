package android.media.p007tv;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.SectionResponse */
/* loaded from: classes2.dex */
public final class SectionResponse extends BroadcastInfoResponse implements Parcelable {
    public static final Parcelable.Creator<SectionResponse> CREATOR = new Parcelable.Creator<SectionResponse>() { // from class: android.media.tv.SectionResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SectionResponse createFromParcel(Parcel source) {
            source.readInt();
            return SectionResponse.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SectionResponse[] newArray(int size) {
            return new SectionResponse[size];
        }
    };
    private static final int RESPONSE_TYPE = 3;
    private final Bundle mSessionData;
    private final int mSessionId;
    private final int mVersion;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SectionResponse createFromParcelBody(Parcel in) {
        return new SectionResponse(in);
    }

    public SectionResponse(int requestId, int sequence, int responseResult, int sessionId, int version, Bundle sessionData) {
        super(3, requestId, sequence, responseResult);
        this.mSessionId = sessionId;
        this.mVersion = version;
        this.mSessionData = sessionData;
    }

    SectionResponse(Parcel source) {
        super(3, source);
        this.mSessionId = source.readInt();
        this.mVersion = source.readInt();
        this.mSessionData = source.readBundle();
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public Bundle getSessionData() {
        return this.mSessionData;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mSessionId);
        dest.writeInt(this.mVersion);
        dest.writeBundle(this.mSessionData);
    }
}
