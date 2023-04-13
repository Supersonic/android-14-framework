package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class SipDialogState implements Parcelable {
    public static final Parcelable.Creator<SipDialogState> CREATOR = new Parcelable.Creator<SipDialogState>() { // from class: android.telephony.ims.SipDialogState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDialogState createFromParcel(Parcel in) {
            return new SipDialogState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDialogState[] newArray(int size) {
            return new SipDialogState[size];
        }
    };
    public static final int STATE_CLOSED = 2;
    public static final int STATE_CONFIRMED = 1;
    public static final int STATE_EARLY = 0;
    private final int mState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SipDialogStateCode {
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mState;

        public Builder(int state) {
            this.mState = 0;
            this.mState = state;
        }

        public SipDialogState build() {
            return new SipDialogState(this);
        }
    }

    private SipDialogState(Builder builder) {
        this.mState = builder.mState;
    }

    private SipDialogState(Parcel in) {
        this.mState = in.readInt();
    }

    public int getState() {
        return this.mState;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mState);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SipDialogState sipDialog = (SipDialogState) o;
        if (this.mState == sipDialog.mState) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mState));
    }
}
