package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.CommandResponse */
/* loaded from: classes2.dex */
public final class CommandResponse extends BroadcastInfoResponse implements Parcelable {
    public static final Parcelable.Creator<CommandResponse> CREATOR = new Parcelable.Creator<CommandResponse>() { // from class: android.media.tv.CommandResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CommandResponse createFromParcel(Parcel source) {
            source.readInt();
            return CommandResponse.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CommandResponse[] newArray(int size) {
            return new CommandResponse[size];
        }
    };
    private static final int RESPONSE_TYPE = 7;
    public static final String RESPONSE_TYPE_JSON = "json";
    public static final String RESPONSE_TYPE_XML = "xml";
    private final String mResponse;
    private final String mResponseType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CommandResponse createFromParcelBody(Parcel in) {
        return new CommandResponse(in);
    }

    public CommandResponse(int requestId, int sequence, int responseResult, String response, String responseType) {
        super(7, requestId, sequence, responseResult);
        this.mResponse = response;
        this.mResponseType = responseType;
    }

    CommandResponse(Parcel source) {
        super(7, source);
        this.mResponse = source.readString();
        this.mResponseType = source.readString();
    }

    public String getResponse() {
        return this.mResponse;
    }

    public String getResponseType() {
        return this.mResponseType;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mResponse);
        dest.writeString(this.mResponseType);
    }
}
