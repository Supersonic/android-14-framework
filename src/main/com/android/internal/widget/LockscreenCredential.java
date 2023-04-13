package com.android.internal.widget;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.LockPatternView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import libcore.util.HexEncoding;
/* loaded from: classes5.dex */
public class LockscreenCredential implements Parcelable, AutoCloseable {
    public static final Parcelable.Creator<LockscreenCredential> CREATOR = new Parcelable.Creator<LockscreenCredential>() { // from class: com.android.internal.widget.LockscreenCredential.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LockscreenCredential createFromParcel(Parcel source) {
            return new LockscreenCredential(source.readInt(), source.createByteArray());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LockscreenCredential[] newArray(int size) {
            return new LockscreenCredential[size];
        }
    };
    private byte[] mCredential;
    private final int mType;

    private LockscreenCredential(int type, byte[] credential) {
        Objects.requireNonNull(credential);
        if (type == -1) {
            Preconditions.checkArgument(credential.length == 0);
        } else {
            Preconditions.checkArgument(type == 3 || type == 4 || type == 1);
            Preconditions.checkArgument(credential.length > 0);
        }
        this.mType = type;
        this.mCredential = credential;
    }

    public static LockscreenCredential createNone() {
        return new LockscreenCredential(-1, new byte[0]);
    }

    public static LockscreenCredential createPattern(List<LockPatternView.Cell> pattern) {
        return new LockscreenCredential(1, LockPatternUtils.patternToByteArray(pattern));
    }

    public static LockscreenCredential createPassword(CharSequence password) {
        return new LockscreenCredential(4, charSequenceToByteArray(password));
    }

    public static LockscreenCredential createManagedPassword(byte[] password) {
        return new LockscreenCredential(4, Arrays.copyOf(password, password.length));
    }

    public static LockscreenCredential createPin(CharSequence pin) {
        return new LockscreenCredential(3, charSequenceToByteArray(pin));
    }

    public static LockscreenCredential createPasswordOrNone(CharSequence password) {
        if (TextUtils.isEmpty(password)) {
            return createNone();
        }
        return createPassword(password);
    }

    public static LockscreenCredential createPinOrNone(CharSequence pin) {
        if (TextUtils.isEmpty(pin)) {
            return createNone();
        }
        return createPin(pin);
    }

    private void ensureNotZeroized() {
        Preconditions.checkState(this.mCredential != null, "Credential is already zeroized");
    }

    public int getType() {
        ensureNotZeroized();
        return this.mType;
    }

    public byte[] getCredential() {
        ensureNotZeroized();
        return this.mCredential;
    }

    public boolean isNone() {
        ensureNotZeroized();
        return this.mType == -1;
    }

    public boolean isPattern() {
        ensureNotZeroized();
        return this.mType == 1;
    }

    public boolean isPin() {
        ensureNotZeroized();
        return this.mType == 3;
    }

    public boolean isPassword() {
        ensureNotZeroized();
        return this.mType == 4;
    }

    public int size() {
        ensureNotZeroized();
        return this.mCredential.length;
    }

    public LockscreenCredential duplicate() {
        int i = this.mType;
        byte[] bArr = this.mCredential;
        return new LockscreenCredential(i, bArr != null ? Arrays.copyOf(bArr, bArr.length) : null);
    }

    public void zeroize() {
        byte[] bArr = this.mCredential;
        if (bArr != null) {
            Arrays.fill(bArr, (byte) 0);
            this.mCredential = null;
        }
    }

    public void checkLength() {
        if (isNone()) {
            return;
        }
        if (isPattern()) {
            if (size() < 4) {
                throw new IllegalArgumentException("pattern must not be null and at least 4 dots long.");
            }
        } else if ((isPassword() || isPin()) && size() < 4) {
            throw new IllegalArgumentException("password must not be null and at least of length 4");
        }
    }

    public boolean checkAgainstStoredType(int storedCredentialType) {
        return storedCredentialType == 2 ? getType() == 4 || getType() == 3 : getType() == storedCredentialType;
    }

    public String passwordToHistoryHash(byte[] salt, byte[] hashFactor) {
        return passwordToHistoryHash(this.mCredential, salt, hashFactor);
    }

    public static String passwordToHistoryHash(byte[] passwordToHash, byte[] salt, byte[] hashFactor) {
        if (passwordToHash == null || passwordToHash.length == 0 || hashFactor == null || salt == null) {
            return null;
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(hashFactor);
            sha256.update(passwordToHash);
            sha256.update(salt);
            return HexEncoding.encodeToString(sha256.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("Missing digest algorithm: ", e);
        }
    }

    @Deprecated
    public static String legacyPasswordToHash(byte[] password, byte[] salt) {
        if (password == null || password.length == 0 || salt == null) {
            return null;
        }
        try {
            byte[] saltedPassword = ArrayUtils.concat(password, salt);
            byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(saltedPassword);
            byte[] md5 = MessageDigest.getInstance(KeyProperties.DIGEST_MD5).digest(saltedPassword);
            Arrays.fill(saltedPassword, (byte) 0);
            return HexEncoding.encodeToString(ArrayUtils.concat(sha1, md5));
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("Missing digest algorithm: ", e);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeByteArray(this.mCredential);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        zeroize();
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mType), Integer.valueOf(Arrays.hashCode(this.mCredential)));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LockscreenCredential) {
            LockscreenCredential other = (LockscreenCredential) o;
            return this.mType == other.mType && Arrays.equals(this.mCredential, other.mCredential);
        }
        return false;
    }

    private static byte[] charSequenceToByteArray(CharSequence chars) {
        if (chars == null) {
            return new byte[0];
        }
        byte[] bytes = new byte[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            bytes[i] = (byte) chars.charAt(i);
        }
        return bytes;
    }
}
