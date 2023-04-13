package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class PinResult implements Parcelable {
    public static final int PIN_RESULT_TYPE_ABORTED = 3;
    public static final int PIN_RESULT_TYPE_FAILURE = 2;
    public static final int PIN_RESULT_TYPE_INCORRECT = 1;
    public static final int PIN_RESULT_TYPE_SUCCESS = 0;
    private final int mAttemptsRemaining;
    private final int mResult;
    private static final PinResult sFailedResult = new PinResult(2, -1);
    public static final Parcelable.Creator<PinResult> CREATOR = new Parcelable.Creator<PinResult>() { // from class: android.telephony.PinResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PinResult createFromParcel(Parcel in) {
            return new PinResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PinResult[] newArray(int size) {
            return new PinResult[size];
        }
    };

    /* loaded from: classes3.dex */
    public @interface PinResultType {
    }

    public int getResult() {
        return this.mResult;
    }

    public int getAttemptsRemaining() {
        return this.mAttemptsRemaining;
    }

    public static PinResult getDefaultFailedResult() {
        return sFailedResult;
    }

    public PinResult(int result, int attemptsRemaining) {
        this.mResult = result;
        this.mAttemptsRemaining = attemptsRemaining;
    }

    private PinResult(Parcel in) {
        this.mResult = in.readInt();
        this.mAttemptsRemaining = in.readInt();
    }

    public String toString() {
        return "result: " + getResult() + ", attempts remaining: " + getAttemptsRemaining();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mResult);
        out.writeInt(this.mAttemptsRemaining);
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mAttemptsRemaining), Integer.valueOf(this.mResult));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PinResult other = (PinResult) obj;
        if (this.mResult == other.mResult && this.mAttemptsRemaining == other.mAttemptsRemaining) {
            return true;
        }
        return false;
    }
}
