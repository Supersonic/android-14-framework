package android.telecom;

import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.telecom.IVideoProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class ParcelableConference implements Parcelable {
    public static final Parcelable.Creator<ParcelableConference> CREATOR = new Parcelable.Creator<ParcelableConference>() { // from class: android.telecom.ParcelableConference.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableConference createFromParcel(Parcel source) {
            ClassLoader classLoader = ParcelableConference.class.getClassLoader();
            PhoneAccountHandle phoneAccount = (PhoneAccountHandle) source.readParcelable(classLoader, PhoneAccountHandle.class);
            int state = source.readInt();
            int capabilities = source.readInt();
            ArrayList arrayList = new ArrayList(2);
            source.readList(arrayList, classLoader, String.class);
            long connectTimeMillis = source.readLong();
            IVideoProvider videoCallProvider = IVideoProvider.Stub.asInterface(source.readStrongBinder());
            int videoState = source.readInt();
            StatusHints statusHints = (StatusHints) source.readParcelable(classLoader, StatusHints.class);
            Bundle extras = source.readBundle(classLoader);
            int properties = source.readInt();
            long connectElapsedTimeMillis = source.readLong();
            Uri address = (Uri) source.readParcelable(classLoader, Uri.class);
            int addressPresentation = source.readInt();
            String callerDisplayName = source.readString();
            int callerDisplayNamePresentation = source.readInt();
            DisconnectCause disconnectCause = (DisconnectCause) source.readParcelable(classLoader, DisconnectCause.class);
            boolean isRingbackRequested = source.readInt() == 1;
            int callDirection = source.readInt();
            return new ParcelableConference(phoneAccount, state, capabilities, properties, arrayList, videoCallProvider, videoState, connectTimeMillis, connectElapsedTimeMillis, statusHints, extras, address, addressPresentation, callerDisplayName, callerDisplayNamePresentation, disconnectCause, isRingbackRequested, callDirection);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableConference[] newArray(int size) {
            return new ParcelableConference[size];
        }
    };
    private final Uri mAddress;
    private final int mAddressPresentation;
    private final int mCallDirection;
    private final String mCallerDisplayName;
    private final int mCallerDisplayNamePresentation;
    private final long mConnectElapsedTimeMillis;
    private final long mConnectTimeMillis;
    private final int mConnectionCapabilities;
    private final List<String> mConnectionIds;
    private final int mConnectionProperties;
    private final DisconnectCause mDisconnectCause;
    private final Bundle mExtras;
    private final PhoneAccountHandle mPhoneAccount;
    private final boolean mRingbackRequested;
    private final int mState;
    private final StatusHints mStatusHints;
    private final IVideoProvider mVideoProvider;
    private final int mVideoState;

    /* loaded from: classes3.dex */
    public static final class Builder {
        private Uri mAddress;
        private String mCallerDisplayName;
        private int mConnectionCapabilities;
        private int mConnectionProperties;
        private DisconnectCause mDisconnectCause;
        private Bundle mExtras;
        private final PhoneAccountHandle mPhoneAccount;
        private boolean mRingbackRequested;
        private final int mState;
        private StatusHints mStatusHints;
        private IVideoProvider mVideoProvider;
        private List<String> mConnectionIds = Collections.emptyList();
        private long mConnectTimeMillis = 0;
        private int mVideoState = 0;
        private long mConnectElapsedTimeMillis = 0;
        private int mAddressPresentation = 3;
        private int mCallerDisplayNamePresentation = 3;
        private int mCallDirection = -1;

        public Builder(PhoneAccountHandle phoneAccount, int state) {
            this.mPhoneAccount = phoneAccount;
            this.mState = state;
        }

        public Builder setDisconnectCause(DisconnectCause cause) {
            this.mDisconnectCause = cause;
            return this;
        }

        public Builder setRingbackRequested(boolean requested) {
            this.mRingbackRequested = requested;
            return this;
        }

        public Builder setCallerDisplayName(String callerDisplayName, int callerDisplayNamePresentation) {
            this.mCallerDisplayName = callerDisplayName;
            this.mCallerDisplayNamePresentation = callerDisplayNamePresentation;
            return this;
        }

        public Builder setAddress(Uri address, int addressPresentation) {
            this.mAddress = address;
            this.mAddressPresentation = addressPresentation;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder setStatusHints(StatusHints hints) {
            this.mStatusHints = hints;
            return this;
        }

        public Builder setConnectTimeMillis(long connectTimeMillis, long connectElapsedTimeMillis) {
            this.mConnectTimeMillis = connectTimeMillis;
            this.mConnectElapsedTimeMillis = connectElapsedTimeMillis;
            return this;
        }

        public Builder setVideoAttributes(IVideoProvider provider, int videoState) {
            this.mVideoProvider = provider;
            this.mVideoState = videoState;
            return this;
        }

        public Builder setConnectionIds(List<String> connectionIds) {
            this.mConnectionIds = connectionIds;
            return this;
        }

        public Builder setConnectionProperties(int properties) {
            this.mConnectionProperties = properties;
            return this;
        }

        public Builder setConnectionCapabilities(int capabilities) {
            this.mConnectionCapabilities = capabilities;
            return this;
        }

        public Builder setCallDirection(int callDirection) {
            this.mCallDirection = callDirection;
            return this;
        }

        public ParcelableConference build() {
            return new ParcelableConference(this.mPhoneAccount, this.mState, this.mConnectionCapabilities, this.mConnectionProperties, this.mConnectionIds, this.mVideoProvider, this.mVideoState, this.mConnectTimeMillis, this.mConnectElapsedTimeMillis, this.mStatusHints, this.mExtras, this.mAddress, this.mAddressPresentation, this.mCallerDisplayName, this.mCallerDisplayNamePresentation, this.mDisconnectCause, this.mRingbackRequested, this.mCallDirection);
        }
    }

    private ParcelableConference(PhoneAccountHandle phoneAccount, int state, int connectionCapabilities, int connectionProperties, List<String> connectionIds, IVideoProvider videoProvider, int videoState, long connectTimeMillis, long connectElapsedTimeMillis, StatusHints statusHints, Bundle extras, Uri address, int addressPresentation, String callerDisplayName, int callerDisplayNamePresentation, DisconnectCause disconnectCause, boolean ringbackRequested, int callDirection) {
        this.mPhoneAccount = phoneAccount;
        this.mState = state;
        this.mConnectionCapabilities = connectionCapabilities;
        this.mConnectionProperties = connectionProperties;
        this.mConnectionIds = connectionIds;
        this.mVideoProvider = videoProvider;
        this.mVideoState = videoState;
        this.mConnectTimeMillis = connectTimeMillis;
        this.mStatusHints = statusHints;
        this.mExtras = extras;
        this.mConnectElapsedTimeMillis = connectElapsedTimeMillis;
        this.mAddress = address;
        this.mAddressPresentation = addressPresentation;
        this.mCallerDisplayName = callerDisplayName;
        this.mCallerDisplayNamePresentation = callerDisplayNamePresentation;
        this.mDisconnectCause = disconnectCause;
        this.mRingbackRequested = ringbackRequested;
        this.mCallDirection = callDirection;
    }

    public String toString() {
        return new StringBuffer().append("account: ").append(this.mPhoneAccount).append(", state: ").append(Connection.stateToString(this.mState)).append(", capabilities: ").append(Connection.capabilitiesToString(this.mConnectionCapabilities)).append(", properties: ").append(Connection.propertiesToString(this.mConnectionProperties)).append(", connectTime: ").append(this.mConnectTimeMillis).append(", children: ").append(this.mConnectionIds).append(", VideoState: ").append(this.mVideoState).append(", VideoProvider: ").append(this.mVideoProvider).append(", isRingbackRequested: ").append(this.mRingbackRequested).append(", disconnectCause: ").append(this.mDisconnectCause).append(", callDirection: ").append(this.mCallDirection).toString();
    }

    public PhoneAccountHandle getPhoneAccount() {
        return this.mPhoneAccount;
    }

    public int getState() {
        return this.mState;
    }

    public int getConnectionCapabilities() {
        return this.mConnectionCapabilities;
    }

    public int getConnectionProperties() {
        return this.mConnectionProperties;
    }

    public List<String> getConnectionIds() {
        return this.mConnectionIds;
    }

    public long getConnectTimeMillis() {
        return this.mConnectTimeMillis;
    }

    public long getConnectElapsedTimeMillis() {
        return this.mConnectElapsedTimeMillis;
    }

    public IVideoProvider getVideoProvider() {
        return this.mVideoProvider;
    }

    public int getVideoState() {
        return this.mVideoState;
    }

    public StatusHints getStatusHints() {
        return this.mStatusHints;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public Uri getHandle() {
        return this.mAddress;
    }

    public final DisconnectCause getDisconnectCause() {
        return this.mDisconnectCause;
    }

    public boolean isRingbackRequested() {
        return this.mRingbackRequested;
    }

    public int getHandlePresentation() {
        return this.mAddressPresentation;
    }

    public int getCallDirection() {
        return this.mCallDirection;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeParcelable(this.mPhoneAccount, 0);
        destination.writeInt(this.mState);
        destination.writeInt(this.mConnectionCapabilities);
        destination.writeList(this.mConnectionIds);
        destination.writeLong(this.mConnectTimeMillis);
        IVideoProvider iVideoProvider = this.mVideoProvider;
        destination.writeStrongBinder(iVideoProvider != null ? iVideoProvider.asBinder() : null);
        destination.writeInt(this.mVideoState);
        destination.writeParcelable(this.mStatusHints, 0);
        destination.writeBundle(this.mExtras);
        destination.writeInt(this.mConnectionProperties);
        destination.writeLong(this.mConnectElapsedTimeMillis);
        destination.writeParcelable(this.mAddress, 0);
        destination.writeInt(this.mAddressPresentation);
        destination.writeString(this.mCallerDisplayName);
        destination.writeInt(this.mCallerDisplayNamePresentation);
        destination.writeParcelable(this.mDisconnectCause, 0);
        destination.writeInt(this.mRingbackRequested ? 1 : 0);
        destination.writeInt(this.mCallDirection);
    }
}
