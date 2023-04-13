package android.telecom;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class PhoneAccountSuggestion implements Parcelable {
    public static final Parcelable.Creator<PhoneAccountSuggestion> CREATOR = new Parcelable.Creator<PhoneAccountSuggestion>() { // from class: android.telecom.PhoneAccountSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhoneAccountSuggestion createFromParcel(Parcel in) {
            return new PhoneAccountSuggestion(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhoneAccountSuggestion[] newArray(int size) {
            return new PhoneAccountSuggestion[size];
        }
    };
    public static final int REASON_FREQUENT = 2;
    public static final int REASON_INTRA_CARRIER = 1;
    public static final int REASON_NONE = 0;
    public static final int REASON_OTHER = 4;
    public static final int REASON_USER_SET = 3;
    private PhoneAccountHandle mHandle;
    private int mReason;
    private boolean mShouldAutoSelect;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SuggestionReason {
    }

    public PhoneAccountSuggestion(PhoneAccountHandle handle, int reason, boolean shouldAutoSelect) {
        this.mHandle = handle;
        this.mReason = reason;
        this.mShouldAutoSelect = shouldAutoSelect;
    }

    private PhoneAccountSuggestion(Parcel in) {
        this.mHandle = (PhoneAccountHandle) in.readParcelable(PhoneAccountHandle.class.getClassLoader(), PhoneAccountHandle.class);
        this.mReason = in.readInt();
        this.mShouldAutoSelect = in.readByte() != 0;
    }

    public PhoneAccountHandle getPhoneAccountHandle() {
        return this.mHandle;
    }

    public int getReason() {
        return this.mReason;
    }

    public boolean shouldAutoSelect() {
        return this.mShouldAutoSelect;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mHandle, flags);
        dest.writeInt(this.mReason);
        dest.writeByte(this.mShouldAutoSelect ? (byte) 1 : (byte) 0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneAccountSuggestion that = (PhoneAccountSuggestion) o;
        if (this.mReason == that.mReason && this.mShouldAutoSelect == that.mShouldAutoSelect && Objects.equals(this.mHandle, that.mHandle)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mHandle, Integer.valueOf(this.mReason), Boolean.valueOf(this.mShouldAutoSelect));
    }
}
