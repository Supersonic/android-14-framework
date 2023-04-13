package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.PackageUtils;
import android.util.Slog;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import libcore.util.HexEncoding;
/* renamed from: android.content.pm.SigningDetails */
/* loaded from: classes.dex */
public final class SigningDetails implements Parcelable {
    private static final int PAST_CERT_EXISTS = 0;
    private static final String TAG = "SigningDetails";
    private final Signature[] mPastSigningCertificates;
    private final ArraySet<PublicKey> mPublicKeys;
    private final int mSignatureSchemeVersion;
    private final Signature[] mSignatures;
    public static final SigningDetails UNKNOWN = new SigningDetails(null, 0, null, null);
    public static final Parcelable.Creator<SigningDetails> CREATOR = new Parcelable.Creator<SigningDetails>() { // from class: android.content.pm.SigningDetails.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SigningDetails createFromParcel(Parcel source) {
            if (source.readBoolean()) {
                return SigningDetails.UNKNOWN;
            }
            return new SigningDetails(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SigningDetails[] newArray(int size) {
            return new SigningDetails[size];
        }
    };

    /* renamed from: android.content.pm.SigningDetails$CapabilityMergeRule */
    /* loaded from: classes.dex */
    public @interface CapabilityMergeRule {
        public static final int MERGE_OTHER_CAPABILITY = 1;
        public static final int MERGE_RESTRICTED_CAPABILITY = 2;
        public static final int MERGE_SELF_CAPABILITY = 0;
    }

    /* renamed from: android.content.pm.SigningDetails$CertCapabilities */
    /* loaded from: classes.dex */
    public @interface CertCapabilities {
        public static final int AUTH = 16;
        public static final int INSTALLED_DATA = 1;
        public static final int PERMISSION = 4;
        public static final int ROLLBACK = 8;
        public static final int SHARED_USER_ID = 2;
    }

    /* renamed from: android.content.pm.SigningDetails$SignatureSchemeVersion */
    /* loaded from: classes.dex */
    public @interface SignatureSchemeVersion {
        public static final int JAR = 1;
        public static final int SIGNING_BLOCK_V2 = 2;
        public static final int SIGNING_BLOCK_V3 = 3;
        public static final int SIGNING_BLOCK_V4 = 4;
        public static final int UNKNOWN = 0;
    }

    public SigningDetails(Signature[] signatures, int signatureSchemeVersion, ArraySet<PublicKey> keys, Signature[] pastSigningCertificates) {
        this.mSignatures = signatures;
        this.mSignatureSchemeVersion = signatureSchemeVersion;
        this.mPublicKeys = keys;
        this.mPastSigningCertificates = pastSigningCertificates;
    }

    public SigningDetails(Signature[] signatures, int signatureSchemeVersion, Signature[] pastSigningCertificates) throws CertificateException {
        this(signatures, signatureSchemeVersion, toSigningKeys(signatures), pastSigningCertificates);
    }

    public SigningDetails(Signature[] signatures, int signatureSchemeVersion) throws CertificateException {
        this(signatures, signatureSchemeVersion, null);
    }

    public SigningDetails(SigningDetails orig) {
        if (orig != null) {
            Signature[] signatureArr = orig.mSignatures;
            if (signatureArr != null) {
                this.mSignatures = (Signature[]) signatureArr.clone();
            } else {
                this.mSignatures = null;
            }
            this.mSignatureSchemeVersion = orig.mSignatureSchemeVersion;
            this.mPublicKeys = new ArraySet<>(orig.mPublicKeys);
            Signature[] signatureArr2 = orig.mPastSigningCertificates;
            if (signatureArr2 != null) {
                this.mPastSigningCertificates = (Signature[]) signatureArr2.clone();
                return;
            } else {
                this.mPastSigningCertificates = null;
                return;
            }
        }
        this.mSignatures = null;
        this.mSignatureSchemeVersion = 0;
        this.mPublicKeys = null;
        this.mPastSigningCertificates = null;
    }

    public SigningDetails mergeLineageWith(SigningDetails otherSigningDetails) {
        return mergeLineageWith(otherSigningDetails, 1);
    }

    public SigningDetails mergeLineageWith(SigningDetails otherSigningDetails, int mergeRule) {
        if (!hasPastSigningCertificates()) {
            return (otherSigningDetails.hasPastSigningCertificates() && otherSigningDetails.hasAncestorOrSelf(this)) ? otherSigningDetails : this;
        } else if (!otherSigningDetails.hasPastSigningCertificates()) {
            return this;
        } else {
            SigningDetails descendantSigningDetails = getDescendantOrSelf(otherSigningDetails);
            if (descendantSigningDetails == null) {
                return this;
            }
            if (descendantSigningDetails == this) {
                return mergeLineageWithAncestorOrSelf(otherSigningDetails, mergeRule);
            }
            switch (mergeRule) {
                case 0:
                    return otherSigningDetails.mergeLineageWithAncestorOrSelf(this, 1);
                case 1:
                    return otherSigningDetails.mergeLineageWithAncestorOrSelf(this, 0);
                case 2:
                    return otherSigningDetails.mergeLineageWithAncestorOrSelf(this, mergeRule);
                default:
                    return this;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0091, code lost:
        if (r7 < 0) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0093, code lost:
        return r10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private SigningDetails mergeLineageWithAncestorOrSelf(SigningDetails otherSigningDetails, int mergeRule) {
        int index;
        int otherIndex;
        int index2 = this.mPastSigningCertificates.length - 1;
        int otherIndex2 = otherSigningDetails.mPastSigningCertificates.length - 1;
        if (index2 < 0 || otherIndex2 < 0) {
            return this;
        }
        List<Signature> mergedSignatures = new ArrayList<>();
        boolean capabilitiesModified = false;
        while (index2 >= 0 && !this.mPastSigningCertificates[index2].equals(otherSigningDetails.mPastSigningCertificates[otherIndex2])) {
            mergedSignatures.add(new Signature(this.mPastSigningCertificates[index2]));
            index2--;
        }
        if (index2 < 0) {
            return this;
        }
        while (true) {
            index = index2 - 1;
            Signature signature = this.mPastSigningCertificates[index2];
            otherIndex = otherIndex2 - 1;
            Signature ancestorSignature = otherSigningDetails.mPastSigningCertificates[otherIndex2];
            Signature mergedSignature = new Signature(signature);
            if (signature.getFlags() != ancestorSignature.getFlags()) {
                capabilitiesModified = true;
                switch (mergeRule) {
                    case 0:
                        mergedSignature.setFlags(signature.getFlags());
                        break;
                    case 1:
                        mergedSignature.setFlags(ancestorSignature.getFlags());
                        break;
                    case 2:
                        mergedSignature.setFlags(signature.getFlags() & ancestorSignature.getFlags());
                        break;
                }
            }
            mergedSignatures.add(mergedSignature);
            if (index >= 0 && otherIndex >= 0 && this.mPastSigningCertificates[index].equals(otherSigningDetails.mPastSigningCertificates[otherIndex])) {
                index2 = index;
                otherIndex2 = otherIndex;
            }
        }
        while (otherIndex >= 0) {
            mergedSignatures.add(new Signature(otherSigningDetails.mPastSigningCertificates[otherIndex]));
            otherIndex--;
        }
        while (index >= 0) {
            mergedSignatures.add(new Signature(this.mPastSigningCertificates[index]));
            index--;
        }
        if (mergedSignatures.size() == this.mPastSigningCertificates.length && !capabilitiesModified) {
            return this;
        }
        Collections.reverse(mergedSignatures);
        try {
            return new SigningDetails(new Signature[]{new Signature(this.mSignatures[0])}, this.mSignatureSchemeVersion, (Signature[]) mergedSignatures.toArray(new Signature[0]));
        } catch (CertificateException e) {
            Slog.m95e(TAG, "Caught an exception creating the merged lineage: ", e);
            return this;
        }
    }

    public boolean hasCommonAncestor(SigningDetails otherSigningDetails) {
        if (!hasPastSigningCertificates()) {
            return otherSigningDetails.hasAncestorOrSelf(this);
        }
        if (otherSigningDetails.hasPastSigningCertificates()) {
            return getDescendantOrSelf(otherSigningDetails) != null;
        }
        return hasAncestorOrSelf(otherSigningDetails);
    }

    public boolean hasAncestorOrSelfWithDigest(Set<String> certDigests) {
        if (this == UNKNOWN || certDigests == null || certDigests.size() == 0) {
            return false;
        }
        Signature[] signatureArr = this.mSignatures;
        if (signatureArr.length > 1) {
            int size = certDigests.size();
            Signature[] signatureArr2 = this.mSignatures;
            if (size < signatureArr2.length) {
                return false;
            }
            for (Signature signature : signatureArr2) {
                String signatureDigest = PackageUtils.computeSha256Digest(signature.toByteArray());
                if (!certDigests.contains(signatureDigest)) {
                    return false;
                }
            }
            return true;
        }
        String signatureDigest2 = PackageUtils.computeSha256Digest(signatureArr[0].toByteArray());
        if (certDigests.contains(signatureDigest2)) {
            return true;
        }
        if (hasPastSigningCertificates()) {
            int i = 0;
            while (true) {
                Signature[] signatureArr3 = this.mPastSigningCertificates;
                if (i >= signatureArr3.length - 1) {
                    break;
                }
                String signatureDigest3 = PackageUtils.computeSha256Digest(signatureArr3[i].toByteArray());
                if (certDigests.contains(signatureDigest3)) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    private SigningDetails getDescendantOrSelf(SigningDetails otherSigningDetails) {
        SigningDetails descendantSigningDetails;
        SigningDetails ancestorSigningDetails;
        if (hasAncestorOrSelf(otherSigningDetails)) {
            descendantSigningDetails = this;
            ancestorSigningDetails = otherSigningDetails;
        } else if (!otherSigningDetails.hasAncestor(this)) {
            return null;
        } else {
            descendantSigningDetails = otherSigningDetails;
            ancestorSigningDetails = this;
        }
        int descendantIndex = descendantSigningDetails.mPastSigningCertificates.length - 1;
        int ancestorIndex = ancestorSigningDetails.mPastSigningCertificates.length - 1;
        while (descendantIndex >= 0 && !descendantSigningDetails.mPastSigningCertificates[descendantIndex].equals(ancestorSigningDetails.mPastSigningCertificates[ancestorIndex])) {
            descendantIndex--;
        }
        if (descendantIndex < 0) {
            return null;
        }
        do {
            descendantIndex--;
            ancestorIndex--;
            if (descendantIndex < 0 || ancestorIndex < 0) {
                break;
            }
        } while (descendantSigningDetails.mPastSigningCertificates[descendantIndex].equals(ancestorSigningDetails.mPastSigningCertificates[ancestorIndex]));
        if (descendantIndex < 0 || ancestorIndex < 0) {
            return descendantSigningDetails;
        }
        return null;
    }

    public boolean hasSignatures() {
        Signature[] signatureArr = this.mSignatures;
        return signatureArr != null && signatureArr.length > 0;
    }

    public boolean hasPastSigningCertificates() {
        Signature[] signatureArr = this.mPastSigningCertificates;
        return signatureArr != null && signatureArr.length > 0;
    }

    public boolean hasAncestorOrSelf(SigningDetails oldDetails) {
        SigningDetails signingDetails = UNKNOWN;
        if (this == signingDetails || oldDetails == signingDetails) {
            return false;
        }
        Signature[] signatureArr = oldDetails.mSignatures;
        if (signatureArr.length > 1) {
            return signaturesMatchExactly(oldDetails);
        }
        return hasCertificate(signatureArr[0]);
    }

    public boolean hasAncestor(SigningDetails oldDetails) {
        SigningDetails signingDetails = UNKNOWN;
        if (this != signingDetails && oldDetails != signingDetails && hasPastSigningCertificates() && oldDetails.mSignatures.length == 1) {
            int i = 0;
            while (true) {
                Signature[] signatureArr = this.mPastSigningCertificates;
                if (i >= signatureArr.length - 1) {
                    break;
                } else if (signatureArr[i].equals(oldDetails.mSignatures[0])) {
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    public boolean hasCommonSignerWithCapability(SigningDetails otherDetails, int flags) {
        SigningDetails signingDetails = UNKNOWN;
        if (this == signingDetails || otherDetails == signingDetails) {
            return false;
        }
        if (this.mSignatures.length > 1 || otherDetails.mSignatures.length > 1) {
            return signaturesMatchExactly(otherDetails);
        }
        Set<Signature> otherSignatures = new ArraySet<>();
        if (otherDetails.hasPastSigningCertificates()) {
            otherSignatures.addAll(Arrays.asList(otherDetails.mPastSigningCertificates));
        } else {
            otherSignatures.addAll(Arrays.asList(otherDetails.mSignatures));
        }
        if (otherSignatures.contains(this.mSignatures[0])) {
            return true;
        }
        if (hasPastSigningCertificates()) {
            int i = 0;
            while (true) {
                Signature[] signatureArr = this.mPastSigningCertificates;
                if (i >= signatureArr.length - 1) {
                    break;
                } else if (otherSignatures.contains(signatureArr[i]) && (this.mPastSigningCertificates[i].getFlags() & flags) == flags) {
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    public boolean checkCapability(SigningDetails oldDetails, int flags) {
        SigningDetails signingDetails = UNKNOWN;
        if (this == signingDetails || oldDetails == signingDetails) {
            return false;
        }
        Signature[] signatureArr = oldDetails.mSignatures;
        if (signatureArr.length > 1) {
            return signaturesMatchExactly(oldDetails);
        }
        return hasCertificate(signatureArr[0], flags);
    }

    public boolean checkCapabilityRecover(SigningDetails oldDetails, int flags) throws CertificateException {
        SigningDetails signingDetails = UNKNOWN;
        if (oldDetails == signingDetails || this == signingDetails) {
            return false;
        }
        if (hasPastSigningCertificates() && oldDetails.mSignatures.length == 1) {
            int i = 0;
            while (true) {
                Signature[] signatureArr = this.mPastSigningCertificates;
                if (i >= signatureArr.length) {
                    return false;
                }
                if (Signature.areEffectiveMatch(oldDetails.mSignatures[0], signatureArr[i]) && this.mPastSigningCertificates[i].getFlags() == flags) {
                    return true;
                }
                i++;
            }
        } else {
            return Signature.areEffectiveMatch(oldDetails.mSignatures, this.mSignatures);
        }
    }

    public boolean hasCertificate(Signature signature) {
        return hasCertificateInternal(signature, 0);
    }

    public boolean hasCertificate(Signature signature, int flags) {
        return hasCertificateInternal(signature, flags);
    }

    public boolean hasCertificate(byte[] certificate) {
        Signature signature = new Signature(certificate);
        return hasCertificate(signature);
    }

    private boolean hasCertificateInternal(Signature signature, int flags) {
        int i;
        if (this == UNKNOWN) {
            return false;
        }
        if (hasPastSigningCertificates()) {
            while (true) {
                Signature[] signatureArr = this.mPastSigningCertificates;
                if (i >= signatureArr.length - 1) {
                    break;
                }
                i = (signatureArr[i].equals(signature) && (flags == 0 || (this.mPastSigningCertificates[i].getFlags() & flags) == flags)) ? 0 : i + 1;
            }
            return true;
        }
        Signature[] signatureArr2 = this.mSignatures;
        return signatureArr2.length == 1 && signatureArr2[0].equals(signature);
    }

    public boolean checkCapability(String sha256String, int flags) {
        if (this == UNKNOWN || TextUtils.isEmpty(sha256String)) {
            return false;
        }
        byte[] sha256Bytes = HexEncoding.decode(sha256String, false);
        if (hasSha256Certificate(sha256Bytes, flags)) {
            return true;
        }
        String[] mSignaturesSha256Digests = PackageUtils.computeSignaturesSha256Digests(this.mSignatures);
        String mSignaturesSha256Digest = PackageUtils.computeSignaturesSha256Digest(mSignaturesSha256Digests);
        return mSignaturesSha256Digest.equals(sha256String);
    }

    public boolean hasSha256Certificate(byte[] sha256Certificate) {
        return hasSha256CertificateInternal(sha256Certificate, 0);
    }

    public boolean hasSha256Certificate(byte[] sha256Certificate, int flags) {
        return hasSha256CertificateInternal(sha256Certificate, flags);
    }

    private boolean hasSha256CertificateInternal(byte[] sha256Certificate, int flags) {
        int i;
        if (this == UNKNOWN) {
            return false;
        }
        if (hasPastSigningCertificates()) {
            while (true) {
                Signature[] signatureArr = this.mPastSigningCertificates;
                if (i >= signatureArr.length - 1) {
                    break;
                }
                byte[] digest = PackageUtils.computeSha256DigestBytes(signatureArr[i].toByteArray());
                i = (Arrays.equals(sha256Certificate, digest) && (flags == 0 || (this.mPastSigningCertificates[i].getFlags() & flags) == flags)) ? 0 : i + 1;
            }
            return true;
        }
        Signature[] signatureArr2 = this.mSignatures;
        if (signatureArr2.length == 1) {
            byte[] digest2 = PackageUtils.computeSha256DigestBytes(signatureArr2[0].toByteArray());
            return Arrays.equals(sha256Certificate, digest2);
        }
        return false;
    }

    public boolean signaturesMatchExactly(SigningDetails other) {
        return Signature.areExactMatch(this.mSignatures, other.mSignatures);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        boolean isUnknown = UNKNOWN == this;
        dest.writeBoolean(isUnknown);
        if (isUnknown) {
            return;
        }
        dest.writeTypedArray(this.mSignatures, flags);
        dest.writeInt(this.mSignatureSchemeVersion);
        dest.writeArraySet(this.mPublicKeys);
        dest.writeTypedArray(this.mPastSigningCertificates, flags);
    }

    protected SigningDetails(Parcel in) {
        ClassLoader boot = Object.class.getClassLoader();
        this.mSignatures = (Signature[]) in.createTypedArray(Signature.CREATOR);
        this.mSignatureSchemeVersion = in.readInt();
        this.mPublicKeys = in.readArraySet(boot);
        this.mPastSigningCertificates = (Signature[]) in.createTypedArray(Signature.CREATOR);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SigningDetails)) {
            return false;
        }
        SigningDetails that = (SigningDetails) o;
        if (this.mSignatureSchemeVersion != that.mSignatureSchemeVersion || !Signature.areExactMatch(this.mSignatures, that.mSignatures)) {
            return false;
        }
        ArraySet<PublicKey> arraySet = this.mPublicKeys;
        if (arraySet != null) {
            if (!arraySet.equals(that.mPublicKeys)) {
                return false;
            }
        } else if (that.mPublicKeys != null) {
            return false;
        }
        if (!Arrays.equals(this.mPastSigningCertificates, that.mPastSigningCertificates)) {
            return false;
        }
        int i = 0;
        while (true) {
            Signature[] signatureArr = this.mPastSigningCertificates;
            if (i >= signatureArr.length) {
                return true;
            }
            if (signatureArr[i].getFlags() != that.mPastSigningCertificates[i].getFlags()) {
                return false;
            }
            i++;
        }
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.mSignatures);
        int result2 = ((result * 31) + this.mSignatureSchemeVersion) * 31;
        ArraySet<PublicKey> arraySet = this.mPublicKeys;
        return ((result2 + (arraySet != null ? arraySet.hashCode() : 0)) * 31) + Arrays.hashCode(this.mPastSigningCertificates);
    }

    /* renamed from: android.content.pm.SigningDetails$Builder */
    /* loaded from: classes.dex */
    public static class Builder {
        private Signature[] mPastSigningCertificates;
        private int mSignatureSchemeVersion = 0;
        private Signature[] mSignatures;

        public Builder setSignatures(Signature[] signatures) {
            this.mSignatures = signatures;
            return this;
        }

        public Builder setSignatureSchemeVersion(int signatureSchemeVersion) {
            this.mSignatureSchemeVersion = signatureSchemeVersion;
            return this;
        }

        public Builder setPastSigningCertificates(Signature[] pastSigningCertificates) {
            this.mPastSigningCertificates = pastSigningCertificates;
            return this;
        }

        private void checkInvariants() {
            if (this.mSignatures == null) {
                throw new IllegalStateException("SigningDetails requires the current signing certificates.");
            }
        }

        public SigningDetails build() throws CertificateException {
            checkInvariants();
            return new SigningDetails(this.mSignatures, this.mSignatureSchemeVersion, this.mPastSigningCertificates);
        }
    }

    public static ArraySet<PublicKey> toSigningKeys(Signature[] signatures) throws CertificateException {
        ArraySet<PublicKey> keys = new ArraySet<>(signatures.length);
        for (Signature signature : signatures) {
            keys.add(signature.getPublicKey());
        }
        return keys;
    }

    public Signature[] getSignatures() {
        return this.mSignatures;
    }

    public int getSignatureSchemeVersion() {
        return this.mSignatureSchemeVersion;
    }

    public ArraySet<PublicKey> getPublicKeys() {
        return this.mPublicKeys;
    }

    public Signature[] getPastSigningCertificates() {
        return this.mPastSigningCertificates;
    }

    @Deprecated
    private void __metadata() {
    }
}
