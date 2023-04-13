package android.service.gatekeeper;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class GateKeeperResponse implements Parcelable {
    public static final int RESPONSE_ERROR = -1;
    public static final int RESPONSE_OK = 0;
    public static final int RESPONSE_RETRY = 1;
    private byte[] mPayload;
    private final int mResponseCode;
    private boolean mShouldReEnroll;
    private int mTimeout;
    public static final GateKeeperResponse ERROR = createGenericResponse(-1);
    public static final Parcelable.Creator<GateKeeperResponse> CREATOR = new Parcelable.Creator<GateKeeperResponse>() { // from class: android.service.gatekeeper.GateKeeperResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GateKeeperResponse createFromParcel(Parcel source) {
            int responseCode = source.readInt();
            if (responseCode == 1) {
                GateKeeperResponse response = GateKeeperResponse.createRetryResponse(source.readInt());
                return response;
            } else if (responseCode == 0) {
                boolean shouldReEnroll = source.readInt() == 1;
                byte[] payload = null;
                int size = source.readInt();
                if (size > 0) {
                    payload = new byte[size];
                    source.readByteArray(payload);
                }
                GateKeeperResponse response2 = GateKeeperResponse.createOkResponse(payload, shouldReEnroll);
                return response2;
            } else {
                GateKeeperResponse response3 = GateKeeperResponse.createGenericResponse(responseCode);
                return response3;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GateKeeperResponse[] newArray(int size) {
            return new GateKeeperResponse[size];
        }
    };

    private GateKeeperResponse(int responseCode) {
        this.mResponseCode = responseCode;
    }

    public static GateKeeperResponse createGenericResponse(int responseCode) {
        return new GateKeeperResponse(responseCode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static GateKeeperResponse createRetryResponse(int timeout) {
        GateKeeperResponse response = new GateKeeperResponse(1);
        response.mTimeout = timeout;
        return response;
    }

    public static GateKeeperResponse createOkResponse(byte[] payload, boolean shouldReEnroll) {
        GateKeeperResponse response = new GateKeeperResponse(0);
        response.mPayload = payload;
        response.mShouldReEnroll = shouldReEnroll;
        return response;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResponseCode);
        int i = this.mResponseCode;
        if (i == 1) {
            dest.writeInt(this.mTimeout);
        } else if (i == 0) {
            dest.writeInt(this.mShouldReEnroll ? 1 : 0);
            byte[] bArr = this.mPayload;
            if (bArr != null && bArr.length > 0) {
                dest.writeInt(bArr.length);
                dest.writeByteArray(this.mPayload);
                return;
            }
            dest.writeInt(0);
        }
    }

    public byte[] getPayload() {
        return this.mPayload;
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    public boolean getShouldReEnroll() {
        return this.mShouldReEnroll;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }
}
