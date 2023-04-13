package com.android.internal.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.Telephony;
/* loaded from: classes3.dex */
public class OperatorInfo implements Parcelable {
    public static final Parcelable.Creator<OperatorInfo> CREATOR = new Parcelable.Creator<OperatorInfo>() { // from class: com.android.internal.telephony.OperatorInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OperatorInfo createFromParcel(Parcel in) {
            OperatorInfo opInfo = new OperatorInfo(in.readString(), in.readString(), in.readString(), (State) in.readSerializable(State.class.getClassLoader(), State.class), in.readInt());
            return opInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OperatorInfo[] newArray(int size) {
            return new OperatorInfo[size];
        }
    };
    private String mOperatorAlphaLong;
    private String mOperatorAlphaShort;
    private String mOperatorNumeric;
    private int mRan;
    private State mState;

    /* loaded from: classes3.dex */
    public enum State {
        UNKNOWN,
        AVAILABLE,
        CURRENT,
        FORBIDDEN
    }

    public String getOperatorAlphaLong() {
        return this.mOperatorAlphaLong;
    }

    public String getOperatorAlphaShort() {
        return this.mOperatorAlphaShort;
    }

    public String getOperatorNumeric() {
        return this.mOperatorNumeric;
    }

    public State getState() {
        return this.mState;
    }

    public int getRan() {
        return this.mRan;
    }

    OperatorInfo(String operatorAlphaLong, String operatorAlphaShort, String operatorNumeric, State state) {
        this.mState = State.UNKNOWN;
        this.mRan = 0;
        this.mOperatorAlphaLong = operatorAlphaLong;
        this.mOperatorAlphaShort = operatorAlphaShort;
        this.mOperatorNumeric = operatorNumeric;
        this.mState = state;
    }

    OperatorInfo(String operatorAlphaLong, String operatorAlphaShort, String operatorNumeric, State state, int ran) {
        this(operatorAlphaLong, operatorAlphaShort, operatorNumeric, state);
        this.mRan = ran;
    }

    public OperatorInfo(String operatorAlphaLong, String operatorAlphaShort, String operatorNumeric, String stateString) {
        this(operatorAlphaLong, operatorAlphaShort, operatorNumeric, rilStateToState(stateString));
    }

    public OperatorInfo(String operatorAlphaLong, String operatorAlphaShort, String operatorNumeric, int ran) {
        this(operatorAlphaLong, operatorAlphaShort, operatorNumeric);
        this.mRan = ran;
    }

    public OperatorInfo(String operatorAlphaLong, String operatorAlphaShort, String operatorNumeric) {
        this(operatorAlphaLong, operatorAlphaShort, operatorNumeric, State.UNKNOWN);
    }

    private static State rilStateToState(String s) {
        if (s.equals("unknown")) {
            return State.UNKNOWN;
        }
        if (s.equals("available")) {
            return State.AVAILABLE;
        }
        if (s.equals(Telephony.Carriers.CURRENT)) {
            return State.CURRENT;
        }
        if (s.equals("forbidden")) {
            return State.FORBIDDEN;
        }
        throw new RuntimeException("RIL impl error: Invalid network state '" + s + "'");
    }

    public String toString() {
        return "OperatorInfo " + this.mOperatorAlphaLong + "/" + this.mOperatorAlphaShort + "/" + this.mOperatorNumeric + "/" + this.mState + "/" + this.mRan;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOperatorAlphaLong);
        dest.writeString(this.mOperatorAlphaShort);
        dest.writeString(this.mOperatorNumeric);
        dest.writeSerializable(this.mState);
        dest.writeInt(this.mRan);
    }
}
