package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class AdnRecord implements Parcelable {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static final Parcelable.Creator<AdnRecord> CREATOR = new Parcelable.Creator<AdnRecord>() { // from class: com.android.internal.telephony.uicc.AdnRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdnRecord createFromParcel(Parcel parcel) {
            return new AdnRecord(parcel.readInt(), parcel.readInt(), parcel.readString(), parcel.readString(), parcel.createStringArray(), parcel.createStringArray());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdnRecord[] newArray(int i) {
            return new AdnRecord[i];
        }
    };
    String[] mAdditionalNumbers;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mAlphaTag;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    int mEfid;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String[] mEmails;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    int mExtRecord;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    String mNumber;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    int mRecordNumber;

    public static int getMaxPhoneNumberDigits() {
        return 20;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static int getMaxAlphaTagBytes(int i) {
        return Math.max(0, i - 14);
    }

    public static byte[] encodeAlphaTag(String str) {
        return TextUtils.isEmpty(str) ? new byte[0] : IccUtils.stringToAdnStringField(str);
    }

    public static String decodeAlphaTag(byte[] bArr, int i, int i2) {
        return IccUtils.adnStringFieldToString(bArr, i, i2);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecord(byte[] bArr) {
        this(0, 0, bArr);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecord(int i, int i2, byte[] bArr) {
        this.mAlphaTag = null;
        this.mNumber = null;
        this.mAdditionalNumbers = null;
        this.mExtRecord = 255;
        this.mEfid = i;
        this.mRecordNumber = i2;
        parseRecord(bArr);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecord(String str, String str2) {
        this(0, 0, str, str2);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecord(String str, String str2, String[] strArr) {
        this(0, 0, str, str2, strArr);
    }

    public AdnRecord(String str, String str2, String[] strArr, String[] strArr2) {
        this(0, 0, str, str2, strArr, strArr2);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecord(int i, int i2, String str, String str2, String[] strArr) {
        this.mExtRecord = 255;
        this.mEfid = i;
        this.mRecordNumber = i2;
        this.mAlphaTag = str;
        this.mNumber = str2;
        this.mEmails = strArr;
        this.mAdditionalNumbers = null;
    }

    public AdnRecord(int i, int i2, String str, String str2, String[] strArr, String[] strArr2) {
        this.mExtRecord = 255;
        this.mEfid = i;
        this.mRecordNumber = i2;
        this.mAlphaTag = str;
        this.mNumber = str2;
        this.mEmails = strArr;
        this.mAdditionalNumbers = strArr2;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public AdnRecord(int i, int i2, String str, String str2) {
        this.mExtRecord = 255;
        this.mEfid = i;
        this.mRecordNumber = i2;
        this.mAlphaTag = str;
        this.mNumber = str2;
        this.mEmails = null;
        this.mAdditionalNumbers = null;
    }

    public String getAlphaTag() {
        return this.mAlphaTag;
    }

    public int getEfid() {
        return this.mEfid;
    }

    public int getRecId() {
        return this.mRecordNumber;
    }

    public void setRecId(int i) {
        this.mRecordNumber = i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getNumber() {
        return this.mNumber;
    }

    public void setNumber(String str) {
        this.mNumber = str;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String[] getEmails() {
        return this.mEmails;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setEmails(String[] strArr) {
        this.mEmails = strArr;
    }

    public String[] getAdditionalNumbers() {
        return this.mAdditionalNumbers;
    }

    public void setAdditionalNumbers(String[] strArr) {
        this.mAdditionalNumbers = strArr;
    }

    public String toString() {
        return "ADN Record '" + this.mAlphaTag + "' '" + Rlog.pii("AdnRecord", this.mNumber) + " " + Rlog.pii("AdnRecord", this.mEmails) + " " + Rlog.pii("AdnRecord", this.mAdditionalNumbers) + "'";
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isEmpty() {
        return TextUtils.isEmpty(this.mAlphaTag) && TextUtils.isEmpty(this.mNumber) && this.mEmails == null && this.mAdditionalNumbers == null;
    }

    public boolean hasExtendedRecord() {
        int i = this.mExtRecord;
        return (i == 0 || i == 255) ? false : true;
    }

    private static boolean stringCompareNullEqualsEmpty(String str, String str2) {
        if (str == str2) {
            return true;
        }
        if (str == null) {
            str = PhoneConfigurationManager.SSSS;
        }
        if (str2 == null) {
            str2 = PhoneConfigurationManager.SSSS;
        }
        return str.equals(str2);
    }

    private static boolean arrayCompareNullEqualsEmpty(String[] strArr, String[] strArr2) {
        if (strArr == strArr2) {
            return true;
        }
        List asList = Arrays.asList((String[]) ArrayUtils.emptyIfNull(strArr, String.class));
        List asList2 = Arrays.asList((String[]) ArrayUtils.emptyIfNull(strArr2, String.class));
        if (asList.size() != asList2.size()) {
            return false;
        }
        return asList.containsAll(asList2);
    }

    public boolean isEqual(AdnRecord adnRecord) {
        return stringCompareNullEqualsEmpty(this.mAlphaTag, adnRecord.mAlphaTag) && stringCompareNullEqualsEmpty(this.mNumber, adnRecord.mNumber) && arrayCompareNullEqualsEmpty(this.mEmails, adnRecord.mEmails) && arrayCompareNullEqualsEmpty(this.mAdditionalNumbers, adnRecord.mAdditionalNumbers);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mEfid);
        parcel.writeInt(this.mRecordNumber);
        parcel.writeString(this.mAlphaTag);
        parcel.writeString(this.mNumber);
        parcel.writeStringArray(this.mEmails);
        parcel.writeStringArray(this.mAdditionalNumbers);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public byte[] buildAdnString(int i) {
        int i2 = i - 14;
        byte[] bArr = new byte[i];
        for (int i3 = 0; i3 < i; i3++) {
            bArr[i3] = -1;
        }
        if (TextUtils.isEmpty(this.mNumber) && TextUtils.isEmpty(this.mAlphaTag)) {
            Rlog.w("AdnRecord", "[buildAdnString] Empty dialing number");
            return bArr;
        }
        String str = this.mNumber;
        if (str != null && str.length() > 20) {
            Rlog.w("AdnRecord", "[buildAdnString] Max length of dialing number is 20");
            return null;
        }
        byte[] encodeAlphaTag = encodeAlphaTag(this.mAlphaTag);
        if (encodeAlphaTag.length > i2) {
            Rlog.w("AdnRecord", "[buildAdnString] Max length of tag is " + i2);
            return null;
        }
        if (!TextUtils.isEmpty(this.mNumber)) {
            byte[] numberToCalledPartyBCD = PhoneNumberUtils.numberToCalledPartyBCD(this.mNumber, 1);
            System.arraycopy(numberToCalledPartyBCD, 0, bArr, i2 + 1, numberToCalledPartyBCD.length);
            bArr[i2 + 0] = (byte) numberToCalledPartyBCD.length;
        }
        bArr[i2 + 12] = -1;
        bArr[i2 + 13] = -1;
        if (encodeAlphaTag.length > 0) {
            System.arraycopy(encodeAlphaTag, 0, bArr, 0, encodeAlphaTag.length);
        }
        return bArr;
    }

    public void appendExtRecord(byte[] bArr) {
        try {
            if (bArr.length == 13 && (bArr[0] & 3) == 2 && (bArr[1] & 255) <= 10) {
                this.mNumber += PhoneNumberUtils.calledPartyBCDFragmentToString(bArr, 2, bArr[1] & 255, 1);
            }
        } catch (RuntimeException e) {
            Rlog.w("AdnRecord", "Error parsing AdnRecord ext record", e);
        }
    }

    private void parseRecord(byte[] bArr) {
        try {
            this.mAlphaTag = decodeAlphaTag(bArr, 0, bArr.length - 14);
            int length = bArr.length - 14;
            int i = bArr[length] & 255;
            if (i > 11) {
                this.mNumber = PhoneConfigurationManager.SSSS;
                return;
            }
            this.mNumber = PhoneNumberUtils.calledPartyBCDToString(bArr, length + 1, i, 1);
            this.mExtRecord = bArr[bArr.length - 1] & 255;
            this.mEmails = null;
            this.mAdditionalNumbers = null;
        } catch (RuntimeException e) {
            Rlog.w("AdnRecord", "Error parsing AdnRecord", e);
            this.mNumber = PhoneConfigurationManager.SSSS;
            this.mAlphaTag = PhoneConfigurationManager.SSSS;
            this.mEmails = null;
            this.mAdditionalNumbers = null;
        }
    }
}
