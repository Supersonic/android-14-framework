package android.telephony;

import android.annotation.SystemApi;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.Signature;
import android.content.p001pm.SigningInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.telephony.uicc.IccUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class UiccAccessRule implements Parcelable {
    public static final Parcelable.Creator<UiccAccessRule> CREATOR = new Parcelable.Creator<UiccAccessRule>() { // from class: android.telephony.UiccAccessRule.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccAccessRule createFromParcel(Parcel in) {
            return new UiccAccessRule(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccAccessRule[] newArray(int size) {
            return new UiccAccessRule[size];
        }
    };
    private static final String DELIMITER_CERTIFICATE_HASH_PACKAGE_NAMES = ":";
    private static final String DELIMITER_INDIVIDUAL_PACKAGE_NAMES = ",";
    private static final int ENCODING_VERSION = 1;
    private static final String TAG = "UiccAccessRule";
    private final long mAccessType;
    private final byte[] mCertificateHash;
    private final String mPackageName;

    public static byte[] encodeRules(UiccAccessRule[] accessRules) {
        if (accessRules == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(baos);
            output.writeInt(1);
            output.writeInt(accessRules.length);
            for (UiccAccessRule accessRule : accessRules) {
                output.writeInt(accessRule.mCertificateHash.length);
                output.write(accessRule.mCertificateHash);
                if (accessRule.mPackageName != null) {
                    output.writeBoolean(true);
                    output.writeUTF(accessRule.mPackageName);
                } else {
                    output.writeBoolean(false);
                }
                output.writeLong(accessRule.mAccessType);
            }
            output.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayOutputStream should never lead to an IOException", e);
        }
    }

    public static UiccAccessRule[] decodeRulesFromCarrierConfig(String[] certs) {
        if (certs == null) {
            return null;
        }
        List<UiccAccessRule> carrierConfigAccessRulesArray = new ArrayList<>();
        for (String cert : certs) {
            String[] splitStr = cert.split(":");
            byte[] certificateHash = IccUtils.hexStringToBytes(splitStr[0]);
            if (splitStr.length == 1) {
                carrierConfigAccessRulesArray.add(new UiccAccessRule(certificateHash, null, 0L));
            } else {
                String[] packageNames = splitStr[1].split(",");
                for (String packageName : packageNames) {
                    carrierConfigAccessRulesArray.add(new UiccAccessRule(certificateHash, packageName, 0L));
                }
            }
        }
        return (UiccAccessRule[]) carrierConfigAccessRulesArray.toArray(new UiccAccessRule[carrierConfigAccessRulesArray.size()]);
    }

    public static UiccAccessRule[] decodeRules(byte[] encodedRules) {
        if (encodedRules == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(encodedRules);
        try {
            DataInputStream input = new DataInputStream(bais);
            input.readInt();
            int count = input.readInt();
            UiccAccessRule[] accessRules = new UiccAccessRule[count];
            for (int i = 0; i < count; i++) {
                int certificateHashLength = input.readInt();
                byte[] certificateHash = new byte[certificateHashLength];
                input.readFully(certificateHash);
                String packageName = input.readBoolean() ? input.readUTF() : null;
                long accessType = input.readLong();
                accessRules[i] = new UiccAccessRule(certificateHash, packageName, accessType);
            }
            input.close();
            input.close();
            return accessRules;
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayInputStream should never lead to an IOException", e);
        }
    }

    public UiccAccessRule(byte[] certificateHash, String packageName, long accessType) {
        this.mCertificateHash = certificateHash;
        this.mPackageName = packageName;
        this.mAccessType = accessType;
    }

    UiccAccessRule(Parcel in) {
        this.mCertificateHash = in.createByteArray();
        this.mPackageName = in.readString();
        this.mAccessType = in.readLong();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.mCertificateHash);
        dest.writeString(this.mPackageName);
        dest.writeLong(this.mAccessType);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getCertificateHexString() {
        return IccUtils.bytesToHexString(this.mCertificateHash);
    }

    public int getCarrierPrivilegeStatus(PackageInfo packageInfo) {
        List<Signature> signatures = getSignatures(packageInfo);
        if (signatures.isEmpty()) {
            throw new IllegalArgumentException("Must use GET_SIGNING_CERTIFICATES when looking up package info");
        }
        for (Signature sig : signatures) {
            int accessStatus = getCarrierPrivilegeStatus(sig, packageInfo.packageName);
            if (accessStatus != 0) {
                return accessStatus;
            }
        }
        return 0;
    }

    public int getCarrierPrivilegeStatus(Signature signature, String packageName) {
        byte[] certHash256 = getCertHash(signature, "SHA-256");
        if (matches(certHash256, packageName)) {
            return 1;
        }
        if (this.mCertificateHash.length == 20) {
            byte[] certHash = getCertHash(signature, "SHA-1");
            return matches(certHash, packageName) ? 1 : 0;
        }
        return 0;
    }

    public boolean matches(String certHash, String packageName) {
        return matches(IccUtils.hexStringToBytes(certHash), packageName);
    }

    private boolean matches(byte[] certHash, String packageName) {
        return certHash != null && Arrays.equals(this.mCertificateHash, certHash) && (TextUtils.isEmpty(this.mPackageName) || this.mPackageName.equals(packageName));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UiccAccessRule that = (UiccAccessRule) obj;
        if (Arrays.equals(this.mCertificateHash, that.mCertificateHash) && Objects.equals(this.mPackageName, that.mPackageName) && this.mAccessType == that.mAccessType) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (1 * 31) + Arrays.hashCode(this.mCertificateHash);
        return (((result * 31) + Objects.hashCode(this.mPackageName)) * 31) + Objects.hashCode(Long.valueOf(this.mAccessType));
    }

    public String toString() {
        return "cert: " + IccUtils.bytesToHexString(this.mCertificateHash) + " pkg: " + this.mPackageName + " access: " + this.mAccessType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static List<Signature> getSignatures(PackageInfo packageInfo) {
        Signature[] signatures = packageInfo.signatures;
        SigningInfo signingInfo = packageInfo.signingInfo;
        if (signingInfo != null) {
            signatures = signingInfo.getSigningCertificateHistory();
            if (signingInfo.hasMultipleSigners()) {
                signatures = signingInfo.getApkContentsSigners();
            }
        }
        return signatures == null ? Collections.EMPTY_LIST : Arrays.asList(signatures);
    }

    public static byte[] getCertHash(Signature signature, String algo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            return md.digest(signature.toByteArray());
        } catch (NoSuchAlgorithmException ex) {
            com.android.telephony.Rlog.m8e(TAG, "NoSuchAlgorithmException: " + ex);
            return null;
        }
    }
}
