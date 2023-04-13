package com.android.ims.internal.uce.presence;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class PresSubscriptionState implements Parcelable {
    public static final Parcelable.Creator<PresSubscriptionState> CREATOR = new Parcelable.Creator<PresSubscriptionState>() { // from class: com.android.ims.internal.uce.presence.PresSubscriptionState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresSubscriptionState createFromParcel(Parcel source) {
            return new PresSubscriptionState(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresSubscriptionState[] newArray(int size) {
            return new PresSubscriptionState[size];
        }
    };
    public static final int UCE_PRES_SUBSCRIPTION_STATE_ACTIVE = 0;
    public static final int UCE_PRES_SUBSCRIPTION_STATE_PENDING = 1;
    public static final int UCE_PRES_SUBSCRIPTION_STATE_TERMINATED = 2;
    public static final int UCE_PRES_SUBSCRIPTION_STATE_UNKNOWN = 3;
    private int mPresSubscriptionState;

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPresSubscriptionState);
    }

    private PresSubscriptionState(Parcel source) {
        this.mPresSubscriptionState = 3;
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mPresSubscriptionState = source.readInt();
    }

    public PresSubscriptionState() {
        this.mPresSubscriptionState = 3;
    }

    public int getPresSubscriptionStateValue() {
        return this.mPresSubscriptionState;
    }

    public void setPresSubscriptionState(int nPresSubscriptionState) {
        this.mPresSubscriptionState = nPresSubscriptionState;
    }
}
