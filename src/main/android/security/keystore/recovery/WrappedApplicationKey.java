package android.security.keystore.recovery;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class WrappedApplicationKey implements Parcelable {
    public static final Parcelable.Creator<WrappedApplicationKey> CREATOR = new Parcelable.Creator<WrappedApplicationKey>() { // from class: android.security.keystore.recovery.WrappedApplicationKey.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WrappedApplicationKey createFromParcel(Parcel in) {
            return new WrappedApplicationKey(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WrappedApplicationKey[] newArray(int length) {
            return new WrappedApplicationKey[length];
        }
    };
    private String mAlias;
    private byte[] mEncryptedKeyMaterial;
    private byte[] mMetadata;

    /* loaded from: classes3.dex */
    public static class Builder {
        private WrappedApplicationKey mInstance = new WrappedApplicationKey();

        public Builder setAlias(String alias) {
            this.mInstance.mAlias = alias;
            return this;
        }

        public Builder setEncryptedKeyMaterial(byte[] encryptedKeyMaterial) {
            this.mInstance.mEncryptedKeyMaterial = encryptedKeyMaterial;
            return this;
        }

        public Builder setMetadata(byte[] metadata) {
            this.mInstance.mMetadata = metadata;
            return this;
        }

        public WrappedApplicationKey build() {
            Objects.requireNonNull(this.mInstance.mAlias);
            Objects.requireNonNull(this.mInstance.mEncryptedKeyMaterial);
            return this.mInstance;
        }
    }

    private WrappedApplicationKey() {
    }

    @Deprecated
    public WrappedApplicationKey(String alias, byte[] encryptedKeyMaterial) {
        this.mAlias = (String) Objects.requireNonNull(alias);
        this.mEncryptedKeyMaterial = (byte[]) Objects.requireNonNull(encryptedKeyMaterial);
    }

    public String getAlias() {
        return this.mAlias;
    }

    public byte[] getEncryptedKeyMaterial() {
        return this.mEncryptedKeyMaterial;
    }

    public byte[] getMetadata() {
        return this.mMetadata;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mAlias);
        out.writeByteArray(this.mEncryptedKeyMaterial);
        out.writeByteArray(this.mMetadata);
    }

    protected WrappedApplicationKey(Parcel in) {
        this.mAlias = in.readString();
        this.mEncryptedKeyMaterial = in.createByteArray();
        this.mMetadata = in.createByteArray();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
