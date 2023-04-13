package android.app;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class StartLockscreenValidationRequest implements Parcelable {
    public static final Parcelable.Creator<StartLockscreenValidationRequest> CREATOR = new Parcelable.Creator<StartLockscreenValidationRequest>() { // from class: android.app.StartLockscreenValidationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartLockscreenValidationRequest createFromParcel(Parcel source) {
            return new StartLockscreenValidationRequest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartLockscreenValidationRequest[] newArray(int size) {
            return new StartLockscreenValidationRequest[size];
        }
    };
    private int mLockscreenUiType;
    private int mRemainingAttempts;
    private byte[] mSourcePublicKey;

    /* loaded from: classes.dex */
    public static final class Builder {
        private StartLockscreenValidationRequest mInstance = new StartLockscreenValidationRequest();

        public Builder setLockscreenUiType(int lockscreenUiType) {
            this.mInstance.mLockscreenUiType = lockscreenUiType;
            return this;
        }

        public Builder setSourcePublicKey(byte[] publicKey) {
            this.mInstance.mSourcePublicKey = publicKey;
            return this;
        }

        public Builder setRemainingAttempts(int remainingAttempts) {
            this.mInstance.mRemainingAttempts = remainingAttempts;
            return this;
        }

        public StartLockscreenValidationRequest build() {
            Objects.requireNonNull(this.mInstance.mSourcePublicKey);
            return this.mInstance;
        }
    }

    public int getLockscreenUiType() {
        return this.mLockscreenUiType;
    }

    public byte[] getSourcePublicKey() {
        return this.mSourcePublicKey;
    }

    public int getRemainingAttempts() {
        return this.mRemainingAttempts;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mLockscreenUiType);
        out.writeByteArray(this.mSourcePublicKey);
        out.writeInt(this.mRemainingAttempts);
    }

    private StartLockscreenValidationRequest() {
    }

    private StartLockscreenValidationRequest(Parcel in) {
        this.mLockscreenUiType = in.readInt();
        this.mSourcePublicKey = in.createByteArray();
        this.mRemainingAttempts = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
