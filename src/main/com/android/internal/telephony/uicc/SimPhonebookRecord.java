package com.android.internal.telephony.uicc;

import android.hardware.radio.V1_6.PhonebookRecordInfo;
import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class SimPhonebookRecord {
    private String[] mAdditionalNumbers;
    private String mAlphaTag;
    private String[] mEmails;
    private String mNumber;
    private int mRecordId;

    private String convertNullToEmptyString(String str) {
        return str != null ? str : PhoneConfigurationManager.SSSS;
    }

    public SimPhonebookRecord(int i, String str, String str2, String[] strArr, String[] strArr2) {
        this.mRecordId = i;
        this.mAlphaTag = str;
        this.mNumber = convertRecordFormatToNumber(str2);
        this.mEmails = strArr;
        if (strArr2 != null) {
            this.mAdditionalNumbers = new String[strArr2.length];
            for (int i2 = 0; i2 < strArr2.length; i2++) {
                this.mAdditionalNumbers[i2] = convertRecordFormatToNumber(strArr2[i2]);
            }
        }
    }

    public SimPhonebookRecord(PhonebookRecordInfo phonebookRecordInfo) {
        this.mRecordId = 0;
        if (phonebookRecordInfo != null) {
            this.mRecordId = phonebookRecordInfo.recordId;
            this.mAlphaTag = phonebookRecordInfo.name;
            this.mNumber = phonebookRecordInfo.number;
            ArrayList arrayList = phonebookRecordInfo.emails;
            this.mEmails = arrayList == null ? null : (String[]) arrayList.toArray(new String[arrayList.size()]);
            ArrayList arrayList2 = phonebookRecordInfo.additionalNumbers;
            this.mAdditionalNumbers = arrayList2 != null ? (String[]) arrayList2.toArray(new String[arrayList2.size()]) : null;
        }
    }

    public SimPhonebookRecord() {
        this.mRecordId = 0;
    }

    public PhonebookRecordInfo toPhonebookRecordInfo() {
        PhonebookRecordInfo phonebookRecordInfo = new PhonebookRecordInfo();
        phonebookRecordInfo.recordId = this.mRecordId;
        phonebookRecordInfo.name = convertNullToEmptyString(this.mAlphaTag);
        phonebookRecordInfo.number = convertNullToEmptyString(convertNumberToRecordFormat(this.mNumber));
        if (this.mEmails != null) {
            phonebookRecordInfo.emails = new ArrayList(Arrays.asList(this.mEmails));
        }
        String[] strArr = this.mAdditionalNumbers;
        if (strArr != null) {
            for (String str : strArr) {
                phonebookRecordInfo.additionalNumbers.add(convertNumberToRecordFormat(str));
            }
        }
        return phonebookRecordInfo;
    }

    public android.hardware.radio.sim.PhonebookRecordInfo toPhonebookRecordInfoAidl() {
        android.hardware.radio.sim.PhonebookRecordInfo phonebookRecordInfo = new android.hardware.radio.sim.PhonebookRecordInfo();
        phonebookRecordInfo.recordId = this.mRecordId;
        phonebookRecordInfo.name = convertNullToEmptyString(this.mAlphaTag);
        phonebookRecordInfo.number = convertNullToEmptyString(convertNumberToRecordFormat(this.mNumber));
        String[] strArr = this.mEmails;
        if (strArr != null) {
            phonebookRecordInfo.emails = strArr;
        } else {
            phonebookRecordInfo.emails = new String[0];
        }
        String[] strArr2 = this.mAdditionalNumbers;
        if (strArr2 != null) {
            int length = strArr2.length;
            String[] strArr3 = new String[length];
            for (int i = 0; i < length; i++) {
                strArr3[i] = convertNumberToRecordFormat(this.mAdditionalNumbers[i]);
            }
            phonebookRecordInfo.additionalNumbers = strArr3;
        } else {
            phonebookRecordInfo.additionalNumbers = new String[0];
        }
        return phonebookRecordInfo;
    }

    public int getRecordId() {
        return this.mRecordId;
    }

    public String getAlphaTag() {
        return this.mAlphaTag;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public String[] getEmails() {
        return this.mEmails;
    }

    public String[] getAdditionalNumbers() {
        return this.mAdditionalNumbers;
    }

    private String convertRecordFormatToNumber(String str) {
        if (str == null) {
            return null;
        }
        return str.replace('e', ';').replace('T', ',').replace('?', 'N');
    }

    private String convertNumberToRecordFormat(String str) {
        if (str == null) {
            return null;
        }
        return str.replace(';', 'e').replace(',', 'T').replace('N', '?');
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.mAlphaTag) && TextUtils.isEmpty(this.mNumber) && ArrayUtils.isEmpty(this.mEmails) && ArrayUtils.isEmpty(this.mAdditionalNumbers);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimPhoneBookRecord{");
        sb.append("ID =");
        sb.append(this.mRecordId);
        sb.append(", name = ");
        String str = this.mAlphaTag;
        if (str == null) {
            str = "null";
        }
        sb.append(str);
        sb.append(", number = ");
        String str2 = this.mNumber;
        sb.append(str2 != null ? str2 : "null");
        sb.append(", email count = ");
        String[] strArr = this.mEmails;
        sb.append(strArr == null ? 0 : strArr.length);
        sb.append(", email = ");
        sb.append(Arrays.toString(this.mEmails));
        sb.append(", ad number count = ");
        String[] strArr2 = this.mAdditionalNumbers;
        sb.append(strArr2 != null ? strArr2.length : 0);
        sb.append(", ad number = ");
        sb.append(Arrays.toString(this.mAdditionalNumbers));
        sb.append("}");
        return sb.toString();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private String[] mAdditionalNumbers;
        private String[] mEmails;
        private int mRecordId = 0;
        private String mAlphaTag = null;
        private String mNumber = null;

        public SimPhonebookRecord build() {
            SimPhonebookRecord simPhonebookRecord = new SimPhonebookRecord();
            simPhonebookRecord.mAlphaTag = this.mAlphaTag;
            simPhonebookRecord.mRecordId = this.mRecordId;
            simPhonebookRecord.mNumber = this.mNumber;
            String[] strArr = this.mEmails;
            if (strArr != null) {
                simPhonebookRecord.mEmails = strArr;
            }
            String[] strArr2 = this.mAdditionalNumbers;
            if (strArr2 != null) {
                simPhonebookRecord.mAdditionalNumbers = strArr2;
            }
            return simPhonebookRecord;
        }

        public Builder setRecordId(int i) {
            this.mRecordId = i;
            return this;
        }

        public Builder setAlphaTag(String str) {
            this.mAlphaTag = str;
            return this;
        }

        public Builder setNumber(String str) {
            this.mNumber = str;
            return this;
        }

        public Builder setEmails(String[] strArr) {
            this.mEmails = strArr;
            return this;
        }

        public Builder setAdditionalNumbers(String[] strArr) {
            this.mAdditionalNumbers = strArr;
            return this;
        }
    }
}
