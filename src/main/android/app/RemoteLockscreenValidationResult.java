package android.app;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public final class RemoteLockscreenValidationResult implements Parcelable {
    public static final Parcelable.Creator<RemoteLockscreenValidationResult> CREATOR = new Parcelable.Creator<RemoteLockscreenValidationResult>() { // from class: android.app.RemoteLockscreenValidationResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteLockscreenValidationResult createFromParcel(Parcel source) {
            return new RemoteLockscreenValidationResult(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteLockscreenValidationResult[] newArray(int size) {
            return new RemoteLockscreenValidationResult[size];
        }
    };
    public static final int RESULT_GUESS_INVALID = 2;
    public static final int RESULT_GUESS_VALID = 1;
    public static final int RESULT_LOCKOUT = 3;
    public static final int RESULT_NO_REMAINING_ATTEMPTS = 4;
    private int mResultCode;
    private long mTimeoutMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface ResultCode {
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private RemoteLockscreenValidationResult mInstance = new RemoteLockscreenValidationResult();

        public Builder setResultCode(int resultCode) {
            this.mInstance.mResultCode = resultCode;
            return this;
        }

        public Builder setTimeoutMillis(long timeoutMillis) {
            this.mInstance.mTimeoutMillis = timeoutMillis;
            return this;
        }

        public RemoteLockscreenValidationResult build() {
            if (this.mInstance.mResultCode == 0) {
                throw new IllegalStateException("Result code must be set");
            }
            return this.mInstance;
        }
    }

    public int getResultCode() {
        return this.mResultCode;
    }

    public long getTimeoutMillis() {
        return this.mTimeoutMillis;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mResultCode);
        out.writeLong(this.mTimeoutMillis);
    }

    private RemoteLockscreenValidationResult() {
    }

    private RemoteLockscreenValidationResult(Parcel in) {
        this.mResultCode = in.readInt();
        this.mTimeoutMillis = in.readLong();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
