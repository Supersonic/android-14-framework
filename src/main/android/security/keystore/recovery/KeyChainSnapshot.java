package android.security.keystore.recovery;

import android.annotation.SystemApi;
import android.p008os.BadParcelableException;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class KeyChainSnapshot implements Parcelable {
    public static final Parcelable.Creator<KeyChainSnapshot> CREATOR = new Parcelable.Creator<KeyChainSnapshot>() { // from class: android.security.keystore.recovery.KeyChainSnapshot.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyChainSnapshot createFromParcel(Parcel in) {
            return new KeyChainSnapshot(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyChainSnapshot[] newArray(int length) {
            return new KeyChainSnapshot[length];
        }
    };
    private static final long DEFAULT_COUNTER_ID = 1;
    private static final int DEFAULT_MAX_ATTEMPTS = 10;
    private RecoveryCertPath mCertPath;
    private long mCounterId;
    private byte[] mEncryptedRecoveryKeyBlob;
    private List<WrappedApplicationKey> mEntryRecoveryData;
    private List<KeyChainProtectionParams> mKeyChainProtectionParams;
    private int mMaxAttempts;
    private byte[] mServerParams;
    private int mSnapshotVersion;

    private KeyChainSnapshot() {
        this.mMaxAttempts = 10;
        this.mCounterId = 1L;
    }

    public int getSnapshotVersion() {
        return this.mSnapshotVersion;
    }

    public int getMaxAttempts() {
        return this.mMaxAttempts;
    }

    public long getCounterId() {
        return this.mCounterId;
    }

    public byte[] getServerParams() {
        return this.mServerParams;
    }

    public CertPath getTrustedHardwareCertPath() {
        try {
            return this.mCertPath.getCertPath();
        } catch (CertificateException e) {
            throw new BadParcelableException(e);
        }
    }

    public List<KeyChainProtectionParams> getKeyChainProtectionParams() {
        return this.mKeyChainProtectionParams;
    }

    public List<WrappedApplicationKey> getWrappedApplicationKeys() {
        return this.mEntryRecoveryData;
    }

    public byte[] getEncryptedRecoveryKeyBlob() {
        return this.mEncryptedRecoveryKeyBlob;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private KeyChainSnapshot mInstance = new KeyChainSnapshot();

        public Builder setSnapshotVersion(int snapshotVersion) {
            this.mInstance.mSnapshotVersion = snapshotVersion;
            return this;
        }

        public Builder setMaxAttempts(int maxAttempts) {
            this.mInstance.mMaxAttempts = maxAttempts;
            return this;
        }

        public Builder setCounterId(long counterId) {
            this.mInstance.mCounterId = counterId;
            return this;
        }

        public Builder setServerParams(byte[] serverParams) {
            this.mInstance.mServerParams = serverParams;
            return this;
        }

        public Builder setTrustedHardwareCertPath(CertPath certPath) throws CertificateException {
            this.mInstance.mCertPath = RecoveryCertPath.createRecoveryCertPath(certPath);
            return this;
        }

        public Builder setKeyChainProtectionParams(List<KeyChainProtectionParams> keyChainProtectionParams) {
            this.mInstance.mKeyChainProtectionParams = keyChainProtectionParams;
            return this;
        }

        public Builder setWrappedApplicationKeys(List<WrappedApplicationKey> entryRecoveryData) {
            this.mInstance.mEntryRecoveryData = entryRecoveryData;
            return this;
        }

        public Builder setEncryptedRecoveryKeyBlob(byte[] encryptedRecoveryKeyBlob) {
            this.mInstance.mEncryptedRecoveryKeyBlob = encryptedRecoveryKeyBlob;
            return this;
        }

        public KeyChainSnapshot build() {
            Preconditions.checkCollectionElementsNotNull(this.mInstance.mKeyChainProtectionParams, "keyChainProtectionParams");
            Preconditions.checkCollectionElementsNotNull(this.mInstance.mEntryRecoveryData, "entryRecoveryData");
            Objects.requireNonNull(this.mInstance.mEncryptedRecoveryKeyBlob);
            Objects.requireNonNull(this.mInstance.mServerParams);
            Objects.requireNonNull(this.mInstance.mCertPath);
            return this.mInstance;
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mSnapshotVersion);
        out.writeTypedList(this.mKeyChainProtectionParams);
        out.writeByteArray(this.mEncryptedRecoveryKeyBlob);
        out.writeTypedList(this.mEntryRecoveryData);
        out.writeInt(this.mMaxAttempts);
        out.writeLong(this.mCounterId);
        out.writeByteArray(this.mServerParams);
        out.writeTypedObject(this.mCertPath, 0);
    }

    protected KeyChainSnapshot(Parcel in) {
        this.mMaxAttempts = 10;
        this.mCounterId = 1L;
        this.mSnapshotVersion = in.readInt();
        this.mKeyChainProtectionParams = in.createTypedArrayList(KeyChainProtectionParams.CREATOR);
        this.mEncryptedRecoveryKeyBlob = in.createByteArray();
        this.mEntryRecoveryData = in.createTypedArrayList(WrappedApplicationKey.CREATOR);
        this.mMaxAttempts = in.readInt();
        this.mCounterId = in.readLong();
        this.mServerParams = in.createByteArray();
        this.mCertPath = (RecoveryCertPath) in.readTypedObject(RecoveryCertPath.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
