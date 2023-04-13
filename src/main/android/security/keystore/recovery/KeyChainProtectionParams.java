package android.security.keystore.recovery;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class KeyChainProtectionParams implements Parcelable {
    public static final Parcelable.Creator<KeyChainProtectionParams> CREATOR = new Parcelable.Creator<KeyChainProtectionParams>() { // from class: android.security.keystore.recovery.KeyChainProtectionParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyChainProtectionParams createFromParcel(Parcel in) {
            return new KeyChainProtectionParams(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyChainProtectionParams[] newArray(int length) {
            return new KeyChainProtectionParams[length];
        }
    };
    public static final int TYPE_LOCKSCREEN = 100;
    public static final int UI_FORMAT_PASSWORD = 2;
    public static final int UI_FORMAT_PATTERN = 3;
    public static final int UI_FORMAT_PIN = 1;
    private KeyDerivationParams mKeyDerivationParams;
    private Integer mLockScreenUiFormat;
    private byte[] mSecret;
    private Integer mUserSecretType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LockScreenUiFormat {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface UserSecretType {
    }

    private KeyChainProtectionParams() {
    }

    public int getUserSecretType() {
        return this.mUserSecretType.intValue();
    }

    public int getLockScreenUiFormat() {
        return this.mLockScreenUiFormat.intValue();
    }

    public KeyDerivationParams getKeyDerivationParams() {
        return this.mKeyDerivationParams;
    }

    public byte[] getSecret() {
        return this.mSecret;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private KeyChainProtectionParams mInstance = new KeyChainProtectionParams();

        public Builder setUserSecretType(int userSecretType) {
            this.mInstance.mUserSecretType = Integer.valueOf(userSecretType);
            return this;
        }

        public Builder setLockScreenUiFormat(int lockScreenUiFormat) {
            this.mInstance.mLockScreenUiFormat = Integer.valueOf(lockScreenUiFormat);
            return this;
        }

        public Builder setKeyDerivationParams(KeyDerivationParams keyDerivationParams) {
            this.mInstance.mKeyDerivationParams = keyDerivationParams;
            return this;
        }

        public Builder setSecret(byte[] secret) {
            this.mInstance.mSecret = secret;
            return this;
        }

        public KeyChainProtectionParams build() {
            if (this.mInstance.mUserSecretType == null) {
                this.mInstance.mUserSecretType = 100;
            }
            Objects.requireNonNull(this.mInstance.mLockScreenUiFormat);
            Objects.requireNonNull(this.mInstance.mKeyDerivationParams);
            if (this.mInstance.mSecret == null) {
                this.mInstance.mSecret = new byte[0];
            }
            return this.mInstance;
        }
    }

    public void clearSecret() {
        Arrays.fill(this.mSecret, (byte) 0);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mUserSecretType.intValue());
        out.writeInt(this.mLockScreenUiFormat.intValue());
        out.writeTypedObject(this.mKeyDerivationParams, flags);
        out.writeByteArray(this.mSecret);
    }

    protected KeyChainProtectionParams(Parcel in) {
        this.mUserSecretType = Integer.valueOf(in.readInt());
        this.mLockScreenUiFormat = Integer.valueOf(in.readInt());
        this.mKeyDerivationParams = (KeyDerivationParams) in.readTypedObject(KeyDerivationParams.CREATOR);
        this.mSecret = in.createByteArray();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
