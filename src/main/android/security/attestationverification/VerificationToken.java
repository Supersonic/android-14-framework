package android.security.attestationverification;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.attestationverification.AttestationVerificationManager;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Parcelling;
import java.lang.annotation.Annotation;
import java.time.Instant;
/* loaded from: classes3.dex */
public final class VerificationToken implements Parcelable {
    public static final Parcelable.Creator<VerificationToken> CREATOR;
    static Parcelling<Instant> sParcellingForVerificationTime;
    private final AttestationProfile mAttestationProfile;
    private final byte[] mHmac;
    private final int mLocalBindingType;
    private final Bundle mRequirements;
    private int mUid;
    private final int mVerificationResult;
    private final Instant mVerificationTime;

    VerificationToken(AttestationProfile attestationProfile, int localBindingType, Bundle requirements, int verificationResult, Instant verificationTime, byte[] hmac, int uid) {
        this.mAttestationProfile = attestationProfile;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) attestationProfile);
        this.mLocalBindingType = localBindingType;
        AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.LocalBindingType.class, (Annotation) null, localBindingType);
        this.mRequirements = requirements;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) requirements);
        this.mVerificationResult = verificationResult;
        AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.VerificationResult.class, (Annotation) null, verificationResult);
        this.mVerificationTime = verificationTime;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) verificationTime);
        this.mHmac = hmac;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hmac);
        this.mUid = uid;
    }

    public AttestationProfile getAttestationProfile() {
        return this.mAttestationProfile;
    }

    public int getLocalBindingType() {
        return this.mLocalBindingType;
    }

    public Bundle getRequirements() {
        return this.mRequirements;
    }

    public int getVerificationResult() {
        return this.mVerificationResult;
    }

    public Instant getVerificationTime() {
        return this.mVerificationTime;
    }

    public byte[] getHmac() {
        return this.mHmac;
    }

    public int getUid() {
        return this.mUid;
    }

    static {
        Parcelling<Instant> parcelling = Parcelling.Cache.get(Parcelling.BuiltIn.ForInstant.class);
        sParcellingForVerificationTime = parcelling;
        if (parcelling == null) {
            sParcellingForVerificationTime = Parcelling.Cache.put(new Parcelling.BuiltIn.ForInstant());
        }
        CREATOR = new Parcelable.Creator<VerificationToken>() { // from class: android.security.attestationverification.VerificationToken.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public VerificationToken[] newArray(int size) {
                return new VerificationToken[size];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public VerificationToken createFromParcel(Parcel in) {
                return new VerificationToken(in);
            }
        };
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mAttestationProfile, flags);
        dest.writeInt(this.mLocalBindingType);
        dest.writeBundle(this.mRequirements);
        dest.writeInt(this.mVerificationResult);
        sParcellingForVerificationTime.parcel(this.mVerificationTime, dest, flags);
        dest.writeByteArray(this.mHmac);
        dest.writeInt(this.mUid);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    VerificationToken(Parcel in) {
        AttestationProfile attestationProfile = (AttestationProfile) in.readTypedObject(AttestationProfile.CREATOR);
        int localBindingType = in.readInt();
        Bundle requirements = in.readBundle();
        int verificationResult = in.readInt();
        Instant verificationTime = sParcellingForVerificationTime.unparcel(in);
        byte[] hmac = in.createByteArray();
        int uid = in.readInt();
        this.mAttestationProfile = attestationProfile;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) attestationProfile);
        this.mLocalBindingType = localBindingType;
        AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.LocalBindingType.class, (Annotation) null, localBindingType);
        this.mRequirements = requirements;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) requirements);
        this.mVerificationResult = verificationResult;
        AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.VerificationResult.class, (Annotation) null, verificationResult);
        this.mVerificationTime = verificationTime;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) verificationTime);
        this.mHmac = hmac;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hmac);
        this.mUid = uid;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private AttestationProfile mAttestationProfile;
        private long mBuilderFieldsSet = 0;
        private byte[] mHmac;
        private int mLocalBindingType;
        private Bundle mRequirements;
        private int mUid;
        private int mVerificationResult;
        private Instant mVerificationTime;

        public Builder(AttestationProfile attestationProfile, int localBindingType, Bundle requirements, int verificationResult, Instant verificationTime, byte[] hmac, int uid) {
            this.mAttestationProfile = attestationProfile;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) attestationProfile);
            this.mLocalBindingType = localBindingType;
            AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.LocalBindingType.class, (Annotation) null, localBindingType);
            this.mRequirements = requirements;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) requirements);
            this.mVerificationResult = verificationResult;
            AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.VerificationResult.class, (Annotation) null, verificationResult);
            this.mVerificationTime = verificationTime;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) verificationTime);
            this.mHmac = hmac;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hmac);
            this.mUid = uid;
        }

        public Builder setAttestationProfile(AttestationProfile value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mAttestationProfile = value;
            return this;
        }

        public Builder setLocalBindingType(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mLocalBindingType = value;
            return this;
        }

        public Builder setRequirements(Bundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mRequirements = value;
            return this;
        }

        public Builder setVerificationResult(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mVerificationResult = value;
            return this;
        }

        public Builder setVerificationTime(Instant value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mVerificationTime = value;
            return this;
        }

        public Builder setHmac(byte... value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mHmac = value;
            return this;
        }

        public Builder setUid(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 64;
            this.mUid = value;
            return this;
        }

        public VerificationToken build() {
            checkNotUsed();
            this.mBuilderFieldsSet |= 128;
            VerificationToken o = new VerificationToken(this.mAttestationProfile, this.mLocalBindingType, this.mRequirements, this.mVerificationResult, this.mVerificationTime, this.mHmac, this.mUid);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 128) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
