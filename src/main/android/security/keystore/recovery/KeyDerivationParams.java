package android.security.keystore.recovery;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class KeyDerivationParams implements Parcelable {
    public static final int ALGORITHM_SCRYPT = 2;
    public static final int ALGORITHM_SHA256 = 1;
    public static final Parcelable.Creator<KeyDerivationParams> CREATOR = new Parcelable.Creator<KeyDerivationParams>() { // from class: android.security.keystore.recovery.KeyDerivationParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyDerivationParams createFromParcel(Parcel in) {
            return new KeyDerivationParams(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public KeyDerivationParams[] newArray(int length) {
            return new KeyDerivationParams[length];
        }
    };
    private final int mAlgorithm;
    private final int mMemoryDifficulty;
    private final byte[] mSalt;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface KeyDerivationAlgorithm {
    }

    public static KeyDerivationParams createSha256Params(byte[] salt) {
        return new KeyDerivationParams(1, salt);
    }

    public static KeyDerivationParams createScryptParams(byte[] salt, int memoryDifficulty) {
        return new KeyDerivationParams(2, salt, memoryDifficulty);
    }

    private KeyDerivationParams(int algorithm, byte[] salt) {
        this(algorithm, salt, -1);
    }

    private KeyDerivationParams(int algorithm, byte[] salt, int memoryDifficulty) {
        this.mAlgorithm = algorithm;
        this.mSalt = (byte[]) Objects.requireNonNull(salt);
        this.mMemoryDifficulty = memoryDifficulty;
    }

    public int getAlgorithm() {
        return this.mAlgorithm;
    }

    public byte[] getSalt() {
        return this.mSalt;
    }

    public int getMemoryDifficulty() {
        return this.mMemoryDifficulty;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mAlgorithm);
        out.writeByteArray(this.mSalt);
        out.writeInt(this.mMemoryDifficulty);
    }

    protected KeyDerivationParams(Parcel in) {
        this.mAlgorithm = in.readInt();
        this.mSalt = in.createByteArray();
        this.mMemoryDifficulty = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
