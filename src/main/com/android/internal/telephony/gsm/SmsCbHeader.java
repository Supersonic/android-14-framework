package com.android.internal.telephony.gsm;

import android.app.backup.FullBackup;
import android.telephony.SmsCbCmasInfo;
import android.telephony.SmsCbEtwsInfo;
import com.android.internal.midi.MidiConstants;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes3.dex */
public class SmsCbHeader {
    public static final int FORMAT_ETWS_PRIMARY = 3;
    public static final int FORMAT_GSM = 1;
    public static final int FORMAT_UMTS = 2;
    private static final String[] LANGUAGE_CODES_GROUP_0 = {Locale.GERMAN.getLanguage(), Locale.ENGLISH.getLanguage(), Locale.ITALIAN.getLanguage(), Locale.FRENCH.getLanguage(), new Locale("es").getLanguage(), new Locale("nl").getLanguage(), new Locale("sv").getLanguage(), new Locale("da").getLanguage(), new Locale("pt").getLanguage(), new Locale("fi").getLanguage(), new Locale(FullBackup.NO_BACKUP_TREE_TOKEN).getLanguage(), new Locale("el").getLanguage(), new Locale("tr").getLanguage(), new Locale("hu").getLanguage(), new Locale("pl").getLanguage(), null};
    private static final String[] LANGUAGE_CODES_GROUP_2 = {new Locale("cs").getLanguage(), new Locale("he").getLanguage(), new Locale("ar").getLanguage(), new Locale("ru").getLanguage(), new Locale("is").getLanguage(), null, null, null, null, null, null, null, null, null, null, null};
    private static final int MESSAGE_TYPE_CBS_MESSAGE = 1;
    public static final int PDU_HEADER_LENGTH = 6;
    private static final int PDU_LENGTH_ETWS = 56;
    private static final int PDU_LENGTH_GSM = 88;
    private final SmsCbCmasInfo mCmasInfo;
    private final int mDataCodingScheme;
    private DataCodingScheme mDataCodingSchemeStructedData;
    private final SmsCbEtwsInfo mEtwsInfo;
    private final int mFormat;
    private final int mGeographicalScope;
    private final int mMessageIdentifier;
    private final int mNrOfPages;
    private final int mPageIndex;
    private final int mSerialNumber;

    public SmsCbHeader(byte[] pdu) throws IllegalArgumentException {
        byte[] warningSecurityInfo;
        if (pdu == null || pdu.length < 6) {
            throw new IllegalArgumentException("Illegal PDU");
        }
        if (pdu.length <= 88) {
            this.mGeographicalScope = (pdu[0] & MidiConstants.STATUS_PROGRAM_CHANGE) >>> 6;
            this.mSerialNumber = ((pdu[0] & 255) << 8) | (pdu[1] & 255);
            this.mMessageIdentifier = ((pdu[2] & 255) << 8) | (pdu[3] & 255);
            if (isEtwsMessage() && pdu.length <= 56) {
                this.mFormat = 3;
                this.mDataCodingScheme = -1;
                this.mPageIndex = -1;
                this.mNrOfPages = -1;
                boolean emergencyUserAlert = (pdu[4] & 1) != 0;
                boolean activatePopup = (pdu[5] & 128) != 0;
                int warningType = (pdu[4] & MidiConstants.STATUS_ACTIVE_SENSING) >>> 1;
                if (pdu.length > 6) {
                    warningSecurityInfo = Arrays.copyOfRange(pdu, 6, pdu.length);
                } else {
                    warningSecurityInfo = null;
                }
                this.mEtwsInfo = new SmsCbEtwsInfo(warningType, emergencyUserAlert, activatePopup, true, warningSecurityInfo);
                this.mCmasInfo = null;
                return;
            }
            this.mFormat = 1;
            this.mDataCodingScheme = pdu[4] & 255;
            int pageIndex = (pdu[5] & 240) >>> 4;
            int nrOfPages = pdu[5] & MidiConstants.STATUS_CHANNEL_MASK;
            if (pageIndex == 0 || nrOfPages == 0 || pageIndex > nrOfPages) {
                pageIndex = 1;
                nrOfPages = 1;
            }
            this.mPageIndex = pageIndex;
            this.mNrOfPages = nrOfPages;
        } else {
            this.mFormat = 2;
            int messageType = pdu[0];
            if (messageType == 1) {
                this.mMessageIdentifier = ((pdu[1] & 255) << 8) | (pdu[2] & 255);
                this.mGeographicalScope = (pdu[3] & MidiConstants.STATUS_PROGRAM_CHANGE) >>> 6;
                this.mSerialNumber = ((pdu[3] & 255) << 8) | (pdu[4] & 255);
                this.mDataCodingScheme = pdu[5] & 255;
                this.mPageIndex = 1;
                this.mNrOfPages = 1;
            } else {
                throw new IllegalArgumentException("Unsupported message type " + messageType);
            }
        }
        int messageType2 = this.mDataCodingScheme;
        if (messageType2 != -1) {
            this.mDataCodingSchemeStructedData = new DataCodingScheme(messageType2);
        }
        if (isEtwsMessage()) {
            boolean emergencyUserAlert2 = isEtwsEmergencyUserAlert();
            boolean activatePopup2 = isEtwsPopupAlert();
            int warningType2 = getEtwsWarningType();
            this.mEtwsInfo = new SmsCbEtwsInfo(warningType2, emergencyUserAlert2, activatePopup2, false, null);
            this.mCmasInfo = null;
        } else if (isCmasMessage()) {
            int messageClass = getCmasMessageClass();
            int severity = getCmasSeverity();
            int urgency = getCmasUrgency();
            int certainty = getCmasCertainty();
            this.mEtwsInfo = null;
            this.mCmasInfo = new SmsCbCmasInfo(messageClass, -1, -1, severity, urgency, certainty);
        } else {
            this.mEtwsInfo = null;
            this.mCmasInfo = null;
        }
    }

    public int getGeographicalScope() {
        return this.mGeographicalScope;
    }

    public int getSerialNumber() {
        return this.mSerialNumber;
    }

    public int getServiceCategory() {
        return this.mMessageIdentifier;
    }

    public int getDataCodingScheme() {
        return this.mDataCodingScheme;
    }

    public DataCodingScheme getDataCodingSchemeStructedData() {
        return this.mDataCodingSchemeStructedData;
    }

    public int getPageIndex() {
        return this.mPageIndex;
    }

    public int getNumberOfPages() {
        return this.mNrOfPages;
    }

    public SmsCbEtwsInfo getEtwsInfo() {
        return this.mEtwsInfo;
    }

    public SmsCbCmasInfo getCmasInfo() {
        return this.mCmasInfo;
    }

    public boolean isEmergencyMessage() {
        int i = this.mMessageIdentifier;
        return i >= 4352 && i <= 6399;
    }

    private boolean isEtwsMessage() {
        return (this.mMessageIdentifier & SmsCbConstants.MESSAGE_ID_ETWS_TYPE_MASK) == 4352;
    }

    public boolean isEtwsPrimaryNotification() {
        return this.mFormat == 3;
    }

    public boolean isUmtsFormat() {
        return this.mFormat == 2;
    }

    private boolean isCmasMessage() {
        int i = this.mMessageIdentifier;
        return i >= 4370 && i <= 4400;
    }

    private boolean isEtwsPopupAlert() {
        return (this.mSerialNumber & 4096) != 0;
    }

    private boolean isEtwsEmergencyUserAlert() {
        return (this.mSerialNumber & 8192) != 0;
    }

    private int getEtwsWarningType() {
        return this.mMessageIdentifier - 4352;
    }

    private int getCmasMessageClass() {
        switch (this.mMessageIdentifier) {
            case 4370:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL_LANGUAGE /* 4383 */:
                return 0;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED /* 4371 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY /* 4372 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED_LANGUAGE /* 4384 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY_LANGUAGE /* 4385 */:
                return 1;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED /* 4373 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY /* 4374 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED /* 4375 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY /* 4376 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED /* 4377 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY /* 4378 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED_LANGUAGE /* 4386 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY_LANGUAGE /* 4387 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED_LANGUAGE /* 4388 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY_LANGUAGE /* 4389 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED_LANGUAGE /* 4390 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY_LANGUAGE /* 4391 */:
                return 2;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY /* 4379 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY_LANGUAGE /* 4392 */:
                return 3;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST /* 4380 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST_LANGUAGE /* 4393 */:
                return 4;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE /* 4381 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE_LANGUAGE /* 4394 */:
                return 5;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE /* 4382 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE_LANGUAGE /* 4395 */:
                return 6;
            default:
                return -1;
        }
    }

    private int getCmasSeverity() {
        switch (this.mMessageIdentifier) {
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED /* 4371 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY /* 4372 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED /* 4373 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY /* 4374 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED_LANGUAGE /* 4384 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY_LANGUAGE /* 4385 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED_LANGUAGE /* 4386 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY_LANGUAGE /* 4387 */:
                return 0;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED /* 4375 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY /* 4376 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED /* 4377 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY /* 4378 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED_LANGUAGE /* 4388 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY_LANGUAGE /* 4389 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED_LANGUAGE /* 4390 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY_LANGUAGE /* 4391 */:
                return 1;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY /* 4379 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST /* 4380 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE /* 4381 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE /* 4382 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL_LANGUAGE /* 4383 */:
            default:
                return -1;
        }
    }

    private int getCmasUrgency() {
        switch (this.mMessageIdentifier) {
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED /* 4371 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY /* 4372 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED /* 4375 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY /* 4376 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED_LANGUAGE /* 4384 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY_LANGUAGE /* 4385 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED_LANGUAGE /* 4388 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY_LANGUAGE /* 4389 */:
                return 0;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED /* 4373 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY /* 4374 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED /* 4377 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY /* 4378 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED_LANGUAGE /* 4386 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY_LANGUAGE /* 4387 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED_LANGUAGE /* 4390 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY_LANGUAGE /* 4391 */:
                return 1;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY /* 4379 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST /* 4380 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE /* 4381 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE /* 4382 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL_LANGUAGE /* 4383 */:
            default:
                return -1;
        }
    }

    private int getCmasCertainty() {
        switch (this.mMessageIdentifier) {
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED /* 4371 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED /* 4373 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED /* 4375 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED /* 4377 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED_LANGUAGE /* 4384 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED_LANGUAGE /* 4386 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED_LANGUAGE /* 4388 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED_LANGUAGE /* 4390 */:
                return 0;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY /* 4372 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY /* 4374 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY /* 4376 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY /* 4378 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY_LANGUAGE /* 4385 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY_LANGUAGE /* 4387 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY_LANGUAGE /* 4389 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY_LANGUAGE /* 4391 */:
                return 1;
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY /* 4379 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST /* 4380 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE /* 4381 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE /* 4382 */:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL_LANGUAGE /* 4383 */:
            default:
                return -1;
        }
    }

    public String toString() {
        return "SmsCbHeader{GS=" + this.mGeographicalScope + ", serialNumber=0x" + Integer.toHexString(this.mSerialNumber) + ", messageIdentifier=0x" + Integer.toHexString(this.mMessageIdentifier) + ", format=" + this.mFormat + ", DCS=0x" + Integer.toHexString(this.mDataCodingScheme) + ", page " + this.mPageIndex + " of " + this.mNrOfPages + '}';
    }

    /* loaded from: classes3.dex */
    public static final class DataCodingScheme {
        public final int encoding;
        public final boolean hasLanguageIndicator;
        public final String language;

        public DataCodingScheme(int dataCodingScheme) {
            int encoding;
            String language = null;
            boolean hasLanguageIndicator = false;
            switch ((dataCodingScheme & 240) >> 4) {
                case 0:
                    encoding = 1;
                    language = SmsCbHeader.LANGUAGE_CODES_GROUP_0[dataCodingScheme & 15];
                    break;
                case 1:
                    hasLanguageIndicator = true;
                    if ((dataCodingScheme & 15) == 1) {
                        encoding = 3;
                        break;
                    } else {
                        encoding = 1;
                        break;
                    }
                case 2:
                    encoding = 1;
                    language = SmsCbHeader.LANGUAGE_CODES_GROUP_2[dataCodingScheme & 15];
                    break;
                case 3:
                    encoding = 1;
                    break;
                case 4:
                case 5:
                    switch ((dataCodingScheme & 12) >> 2) {
                        case 1:
                            encoding = 2;
                            break;
                        case 2:
                            encoding = 3;
                            break;
                        default:
                            encoding = 1;
                            break;
                    }
                case 6:
                case 7:
                case 9:
                case 14:
                    throw new IllegalArgumentException("Unsupported GSM dataCodingScheme " + dataCodingScheme);
                case 8:
                case 10:
                case 11:
                case 12:
                case 13:
                default:
                    encoding = 1;
                    break;
                case 15:
                    if (((dataCodingScheme & 4) >> 2) == 1) {
                        encoding = 2;
                        break;
                    } else {
                        encoding = 1;
                        break;
                    }
            }
            this.encoding = encoding;
            this.language = language;
            this.hasLanguageIndicator = hasLanguageIndicator;
        }
    }
}
