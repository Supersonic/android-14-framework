package android.telecom;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CallAttributes implements Parcelable {
    public static final int AUDIO_CALL = 1;
    public static final String CALL_CAPABILITIES_KEY = "TelecomCapabilities";
    public static final Parcelable.Creator<CallAttributes> CREATOR = new Parcelable.Creator<CallAttributes>() { // from class: android.telecom.CallAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallAttributes createFromParcel(Parcel source) {
            return new CallAttributes((PhoneAccountHandle) source.readParcelable(getClass().getClassLoader(), PhoneAccountHandle.class), source.readCharSequence(), (Uri) source.readParcelable(getClass().getClassLoader(), Uri.class), source.readInt(), source.readInt(), source.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallAttributes[] newArray(int size) {
            return new CallAttributes[size];
        }
    };
    public static final int DIRECTION_INCOMING = 1;
    public static final int DIRECTION_OUTGOING = 2;
    public static final int SUPPORTS_SET_INACTIVE = 2;
    public static final int SUPPORTS_STREAM = 4;
    public static final int SUPPORTS_TRANSFER = 8;
    public static final int VIDEO_CALL = 2;
    private final Uri mAddress;
    private final int mCallCapabilities;
    private final int mCallType;
    private final int mDirection;
    private final CharSequence mDisplayName;
    private final PhoneAccountHandle mPhoneAccountHandle;

    /* loaded from: classes3.dex */
    public @interface CallCapability {
    }

    /* loaded from: classes3.dex */
    public @interface CallType {
    }

    /* loaded from: classes3.dex */
    public @interface Direction {
    }

    private CallAttributes(PhoneAccountHandle phoneAccountHandle, CharSequence displayName, Uri address, int direction, int callType, int callCapabilities) {
        this.mPhoneAccountHandle = phoneAccountHandle;
        this.mDisplayName = displayName;
        this.mAddress = address;
        this.mDirection = direction;
        this.mCallType = callType;
        this.mCallCapabilities = callCapabilities;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final Uri mAddress;
        private final int mDirection;
        private final CharSequence mDisplayName;
        private final PhoneAccountHandle mPhoneAccountHandle;
        private int mCallType = 1;
        private int mCallCapabilities = 2;

        public Builder(PhoneAccountHandle phoneAccountHandle, int callDirection, CharSequence displayName, Uri address) {
            if (!isInRange(1, 2, callDirection)) {
                throw new IllegalArgumentException(TextUtils.formatSimple("CallDirection=[%d] is invalid. CallDirections value should be within [%d, %d]", Integer.valueOf(callDirection), 1, 2));
            }
            Objects.requireNonNull(phoneAccountHandle);
            Objects.requireNonNull(displayName);
            Objects.requireNonNull(address);
            this.mPhoneAccountHandle = phoneAccountHandle;
            this.mDirection = callDirection;
            this.mDisplayName = displayName;
            this.mAddress = address;
        }

        public Builder setCallType(int callType) {
            if (!isInRange(1, 2, callType)) {
                throw new IllegalArgumentException(TextUtils.formatSimple("CallType=[%d] is invalid. CallTypes value should be within [%d, %d]", Integer.valueOf(callType), 1, 2));
            }
            this.mCallType = callType;
            return this;
        }

        public Builder setCallCapabilities(int callCapabilities) {
            this.mCallCapabilities = callCapabilities;
            return this;
        }

        public CallAttributes build() {
            return new CallAttributes(this.mPhoneAccountHandle, this.mDisplayName, this.mAddress, this.mDirection, this.mCallType, this.mCallCapabilities);
        }

        private boolean isInRange(int floor, int ceiling, int value) {
            return value >= floor && value <= ceiling;
        }
    }

    public PhoneAccountHandle getPhoneAccountHandle() {
        return this.mPhoneAccountHandle;
    }

    public CharSequence getDisplayName() {
        return this.mDisplayName;
    }

    public Uri getAddress() {
        return this.mAddress;
    }

    public int getDirection() {
        return this.mDirection;
    }

    public int getCallType() {
        return this.mCallType;
    }

    public int getCallCapabilities() {
        return this.mCallCapabilities;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mPhoneAccountHandle, flags);
        dest.writeCharSequence(this.mDisplayName);
        dest.writeParcelable(this.mAddress, flags);
        dest.writeInt(this.mDirection);
        dest.writeInt(this.mCallType);
        dest.writeInt(this.mCallCapabilities);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ CallAttributes: [phoneAccountHandle: ").append(this.mPhoneAccountHandle).append("], [contactName: ").append(Log.pii(this.mDisplayName)).append("], [address=").append(Log.pii(this.mAddress)).append("], [direction=").append(this.mDirection).append("], [callType=").append(this.mCallType).append("], [mCallCapabilities=").append(this.mCallCapabilities).append("]  }");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        CallAttributes that = (CallAttributes) obj;
        return this.mDirection == that.mDirection && this.mCallType == that.mCallType && this.mCallCapabilities == that.mCallCapabilities && Objects.equals(this.mPhoneAccountHandle, that.mPhoneAccountHandle) && Objects.equals(this.mAddress, that.mAddress) && Objects.equals(this.mDisplayName, that.mDisplayName);
    }

    public int hashCode() {
        return Objects.hash(this.mPhoneAccountHandle, this.mAddress, this.mDisplayName, Integer.valueOf(this.mDirection), Integer.valueOf(this.mCallType), Integer.valueOf(this.mCallCapabilities));
    }
}
