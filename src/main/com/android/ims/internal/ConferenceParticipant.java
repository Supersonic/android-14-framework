package com.android.ims.internal;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.telecom.Connection;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class ConferenceParticipant implements Parcelable {
    private static final String ANONYMOUS_INVALID_HOST = "anonymous.invalid";
    public static final Parcelable.Creator<ConferenceParticipant> CREATOR = new Parcelable.Creator<ConferenceParticipant>() { // from class: com.android.ims.internal.ConferenceParticipant.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConferenceParticipant createFromParcel(Parcel source) {
            ClassLoader classLoader = ConferenceParticipant.class.getClassLoader();
            Uri handle = (Uri) source.readParcelable(classLoader);
            String displayName = source.readString();
            Uri endpoint = (Uri) source.readParcelable(classLoader);
            int state = source.readInt();
            long connectTime = source.readLong();
            long elapsedRealTime = source.readLong();
            int callDirection = source.readInt();
            ConferenceParticipant participant = new ConferenceParticipant(handle, displayName, endpoint, state, callDirection);
            participant.setConnectTime(connectTime);
            participant.setConnectElapsedTime(elapsedRealTime);
            return participant;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ConferenceParticipant[] newArray(int size) {
            return new ConferenceParticipant[size];
        }
    };
    private static final String TAG = "ConferenceParticipant";
    private int mCallDirection;
    private long mConnectElapsedTime;
    private long mConnectTime;
    private final String mDisplayName;
    private final Uri mEndpoint;
    private final Uri mHandle;
    private final int mState;

    public ConferenceParticipant(Uri handle, String displayName, Uri endpoint, int state, int callDirection) {
        this.mHandle = handle;
        this.mDisplayName = displayName;
        this.mEndpoint = endpoint;
        this.mState = state;
        this.mCallDirection = callDirection;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getParticipantPresentation() {
        Uri address = getHandle();
        if (address == null) {
            return 2;
        }
        String number = address.getSchemeSpecificPart();
        if (TextUtils.isEmpty(number)) {
            return 2;
        }
        String[] hostParts = number.split("[;]");
        String addressPart = hostParts[0];
        String[] numberParts = addressPart.split("[@]");
        if (numberParts.length != 2) {
            return 1;
        }
        String hostName = numberParts[1];
        return hostName.equals(ANONYMOUS_INVALID_HOST) ? 2 : 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mHandle, 0);
        dest.writeString(this.mDisplayName);
        dest.writeParcelable(this.mEndpoint, 0);
        dest.writeInt(this.mState);
        dest.writeLong(this.mConnectTime);
        dest.writeLong(this.mConnectElapsedTime);
        dest.writeInt(this.mCallDirection);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ConferenceParticipant Handle: ");
        sb.append(Rlog.pii(TAG, this.mHandle));
        sb.append(" DisplayName: ");
        sb.append(Rlog.pii(TAG, this.mDisplayName));
        sb.append(" Endpoint: ");
        sb.append(Rlog.pii(TAG, this.mEndpoint));
        sb.append(" State: ");
        sb.append(Connection.stateToString(this.mState));
        sb.append(" ConnectTime: ");
        sb.append(getConnectTime());
        sb.append(" ConnectElapsedTime: ");
        sb.append(getConnectElapsedTime());
        sb.append(" Direction: ");
        sb.append(getCallDirection() == 0 ? "Incoming" : "Outgoing");
        sb.append("]");
        return sb.toString();
    }

    public Uri getHandle() {
        return this.mHandle;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public Uri getEndpoint() {
        return this.mEndpoint;
    }

    public int getState() {
        return this.mState;
    }

    public long getConnectTime() {
        return this.mConnectTime;
    }

    public void setConnectTime(long connectTime) {
        this.mConnectTime = connectTime;
    }

    public long getConnectElapsedTime() {
        return this.mConnectElapsedTime;
    }

    public void setConnectElapsedTime(long connectElapsedTime) {
        this.mConnectElapsedTime = connectElapsedTime;
    }

    public int getCallDirection() {
        return this.mCallDirection;
    }

    public void setCallDirection(int callDirection) {
        this.mCallDirection = callDirection;
    }

    public static Uri getParticipantAddress(Uri address, String countryIso) {
        if (address == null) {
            return address;
        }
        String number = address.getSchemeSpecificPart();
        if (TextUtils.isEmpty(number)) {
            return address;
        }
        String[] numberParts = number.split("[@;:]");
        if (numberParts.length == 0) {
            return address;
        }
        String number2 = numberParts[0];
        String formattedNumber = null;
        if (!TextUtils.isEmpty(countryIso)) {
            formattedNumber = PhoneNumberUtils.formatNumberToE164(number2, countryIso);
        }
        return Uri.fromParts("tel", formattedNumber != null ? formattedNumber : number2, null);
    }
}
