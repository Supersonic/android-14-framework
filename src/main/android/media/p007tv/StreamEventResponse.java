package android.media.p007tv;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.media.tv.StreamEventResponse */
/* loaded from: classes2.dex */
public final class StreamEventResponse extends BroadcastInfoResponse implements Parcelable {
    public static final Parcelable.Creator<StreamEventResponse> CREATOR = new Parcelable.Creator<StreamEventResponse>() { // from class: android.media.tv.StreamEventResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StreamEventResponse createFromParcel(Parcel source) {
            source.readInt();
            return StreamEventResponse.createFromParcelBody(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StreamEventResponse[] newArray(int size) {
            return new StreamEventResponse[size];
        }
    };
    private static final int RESPONSE_TYPE = 5;
    private final byte[] mData;
    private final int mEventId;
    private final long mNptMillis;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StreamEventResponse createFromParcelBody(Parcel in) {
        return new StreamEventResponse(in);
    }

    public StreamEventResponse(int requestId, int sequence, int responseResult, int eventId, long nptMillis, byte[] data) {
        super(5, requestId, sequence, responseResult);
        this.mEventId = eventId;
        this.mNptMillis = nptMillis;
        this.mData = data;
    }

    private StreamEventResponse(Parcel source) {
        super(5, source);
        this.mEventId = source.readInt();
        this.mNptMillis = source.readLong();
        int dataLength = source.readInt();
        if (dataLength > 0) {
            byte[] bArr = new byte[dataLength];
            this.mData = bArr;
            source.readByteArray(bArr);
            return;
        }
        this.mData = null;
    }

    public int getEventId() {
        return this.mEventId;
    }

    public long getNptMillis() {
        return this.mNptMillis;
    }

    public byte[] getData() {
        return this.mData;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.media.p007tv.BroadcastInfoResponse, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mEventId);
        dest.writeLong(this.mNptMillis);
        byte[] bArr = this.mData;
        if (bArr != null && bArr.length > 0) {
            dest.writeInt(bArr.length);
            dest.writeByteArray(this.mData);
            return;
        }
        dest.writeInt(0);
    }
}
