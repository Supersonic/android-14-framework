package android.telecom;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.ParcelUuid;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes3.dex */
public final class CallEndpoint implements Parcelable {
    public static final Parcelable.Creator<CallEndpoint> CREATOR = new Parcelable.Creator<CallEndpoint>() { // from class: android.telecom.CallEndpoint.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallEndpoint createFromParcel(Parcel source) {
            CharSequence name = source.readCharSequence();
            int type = source.readInt();
            ParcelUuid id = ParcelUuid.CREATOR.createFromParcel(source);
            return new CallEndpoint(name, type, id);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallEndpoint[] newArray(int size) {
            return new CallEndpoint[size];
        }
    };
    public static final int ENDPOINT_OPERATION_FAILED = 1;
    public static final int ENDPOINT_OPERATION_SUCCESS = 0;
    public static final int TYPE_BLUETOOTH = 2;
    public static final int TYPE_EARPIECE = 1;
    public static final int TYPE_SPEAKER = 4;
    public static final int TYPE_STREAMING = 5;
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_WIRED_HEADSET = 3;
    private final ParcelUuid mIdentifier;
    private final CharSequence mName;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EndpointType {
    }

    public CallEndpoint(CharSequence name, int type, ParcelUuid id) {
        this.mName = name;
        this.mType = type;
        this.mIdentifier = id;
    }

    public CallEndpoint(CharSequence name, int type) {
        this(name, type, new ParcelUuid(UUID.randomUUID()));
    }

    public CallEndpoint(CallEndpoint endpoint) {
        this.mName = endpoint.getEndpointName();
        this.mType = endpoint.getEndpointType();
        this.mIdentifier = endpoint.getIdentifier();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CallEndpoint)) {
            return false;
        }
        CallEndpoint endpoint = (CallEndpoint) obj;
        return getEndpointName().toString().contentEquals(endpoint.getEndpointName()) && getEndpointType() == endpoint.getEndpointType() && getIdentifier().equals(endpoint.getIdentifier());
    }

    public int hashCode() {
        return Objects.hash(this.mName, Integer.valueOf(this.mType), this.mIdentifier);
    }

    public String toString() {
        return TextUtils.formatSimple("[CallEndpoint Name: %s, Type: %s, Identifier: %s]", this.mName.toString(), endpointTypeToString(this.mType), this.mIdentifier.toString());
    }

    public CharSequence getEndpointName() {
        return this.mName;
    }

    public int getEndpointType() {
        return this.mType;
    }

    public ParcelUuid getIdentifier() {
        return this.mIdentifier;
    }

    public static String endpointTypeToString(int endpointType) {
        switch (endpointType) {
            case 1:
                return "EARPIECE";
            case 2:
                return "BLUETOOTH";
            case 3:
                return "WIRED_HEADSET";
            case 4:
                return "SPEAKER";
            case 5:
                return "EXTERNAL";
            default:
                return "UNKNOWN (" + endpointType + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeCharSequence(this.mName);
        destination.writeInt(this.mType);
        this.mIdentifier.writeToParcel(destination, flags);
    }
}
